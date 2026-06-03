package r01f.file.util.batch;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileDigest;
import r01f.file.FileStoreExecutedActionData;
import r01f.file.util.Files;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;

/**
 * Moves the files from a source filesystem to another
 * destination filesystem
 */
@Slf4j
public class FilesBatchMove {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreAPI _srcFileApi;
	private final FileStoreFilerAPI _srcFilerApi;

	private final FileStoreAPI _dstFileApi;
	private final FileStoreFilerAPI _dstFilerApi;

	private final Scheduler _scheduler;

	private final FilesBatchMovePathTranslator _pathTranslator;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FilesBatchMove(final FileStoreAPI srcFileApi,final FileStoreFilerAPI srcFilerApi,
						  final FileStoreAPI dstFileApi,final FileStoreFilerAPI dstFilerApi,
						  final FilesBatchMovePathTranslator pathTranslator) {
		this(srcFileApi,srcFilerApi,
			 dstFileApi,dstFilerApi,
			 pathTranslator,
			 Schedulers.io());
	}
	public FilesBatchMove(final FileStoreAPI srcFileApi,final FileStoreFilerAPI srcFilerApi,
						  final FileStoreAPI dstFileApi,final FileStoreFilerAPI dstFilerApi,
						  final FilesBatchMovePathTranslator pathTranslator,
						  final Scheduler scheduler) {
		// scheduler
		_scheduler = scheduler;

		// source file-store api
		_srcFileApi = srcFileApi;
		_srcFilerApi = srcFilerApi;

		// dest file-store api
		_dstFileApi = dstFileApi;
		_dstFilerApi = dstFilerApi;

		// the source to destionation path translator
		_pathTranslator = pathTranslator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHECKOUT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks out the given folder or file paths
	 * returns a collection of {@link FileStoreExecutedActionData} objects that encapsulates each checked-out file checkout result
	 * ... so if a path is a folder path, a result object is returned for every file inside the folder
	 * @param relPaths  the relative paths of the resources to be checked-outs
	 * @return
	 * @throws IOException
	 */
	public Collection<FileStoreExecutedActionData> move(final Collection<Path> relPaths) throws IOException {
		Flowable<FileStoreExecutedActionData> flowable = this.flowableMove(relPaths);
		return Lists.newArrayList(flowable.blockingIterable());	// BEWARE!! the flowable iterable MUST be iterated!!
	}
	/**
	 * Checks out the given folder or file paths
	 * returns a rx {@link Flowable} of {@link FileStoreExecutedActionData} objects that encapsulates each checked-out file checkout result
	 * ... so if a path is a folder path, a result object is returned for every file inside the folder
	 * @param relPaths the relative paths of the resources to be checked-outs
	 * @return
	 * @throws IOException
	 */
	public Flowable<FileStoreExecutedActionData> flowableMove(final Collection<Path> relPaths) throws IOException {
		log.debug("Checking out {} resources",
				 relPaths.size());

		// See https://dzone.com/articles/rxjava-idiomatic-concurrency-flatmap-vs-parallel
		// Concurrency is achieved using:
		//		Flowable<Person> people = Flowable.fromIterable(col)
		//										  .flatMap(mew Function<String,Flowable<String>() {
		//															@Override
		//															public Flowable<String> apply(final String item) throws Exception {
		//																return Flowable.just(item)
		//																			   .subscribeOn(Schedulers.io()
		//																			   // ... do anything in parallel;
		//														    }
		//												   })
		//										  .subscribe();
		// ... and since RxJava 2.0.5:
		//		Flowable<Person> people = Flowable.fromIterable(col)
        //										.parallel(10)
		//										.runOn(Schedulers.io())
		//										// ... do anything in parallel
		//										// wait for all parallel task to complete
		//										.sequential();
		return Flowable.fromIterable(relPaths)
					// run in parallel
					.parallel()
					.runOn(_scheduler)

					// Filter non-existent resources
					.filter(path -> {
								Path srcPath = _pathTranslator.sourceAbsolutePathFromSourceRelativePath(path);
								boolean outExists = _srcFilerApi.existsFolder(srcPath)		// it's a folder
												 || _srcFileApi.existsFile(srcPath);		// it's a file
								if (!outExists) log.debug("\t...{} DOES NOT EXIST!!",
										 				 srcPath);
								return outExists;
							})
					// checkout
					.flatMap(path -> {
								Flowable<FileStoreExecutedActionData> outFlow = null;

								Path srcPath = _pathTranslator.sourceAbsolutePathFromSourceRelativePath(path);
								if (_srcFilerApi.existsFolder(srcPath)) {
									// it's a folder: transfer all it's files
									outFlow = _checkoutFolder(path);
								}
								else if (_srcFileApi.existsFile(srcPath)) {
									// it's a file: just transfer the file
									outFlow = _moveFile(path);
								}
								return outFlow;
							})
					.doOnError(th -> log.error("\t... error during checkout: {}",
							   				   th.getMessage(),th))
					// wait all rails to finish
					.sequential();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private Flowable<FileStoreExecutedActionData> _moveFile(final Path srcRelPath) throws IOException {
		Path srcFileAbsPath = _pathTranslator.sourceAbsolutePathFromSourceRelativePath(srcRelPath);
		Path dstFileAbsPath = _pathTranslator.destinationAbsolutePathFromSourceRelativePath(srcRelPath);

		log.debug("\t\t\t[init file thread #{}]: src={} dst={}",
				  Thread.currentThread(),
				  srcFileAbsPath,dstFileAbsPath);
		Stopwatch chrono = Stopwatch.createStarted();

		FileStoreExecutedActionData result = null;

		// do the destination file previously exists?
		boolean dstExists = _dstFileApi.existsFile(dstFileAbsPath);
		try {
			// source
			InputStream srcFileIS = Files.wrap(_srcFileApi)
									 	 .forLoading(srcFileAbsPath)
									 	 .asInputStream();
			// decorate the source file is to compute the digest (hash)
			DigestInputStream digestIS = FileDigest.decorate(srcFileIS);

			// write the destination file
			Files.wrap(_dstFileApi)
				 .forOverwriting(dstFileAbsPath)
				 .write(digestIS);
			digestIS.close();		// BEWARE!!!!

			// create the result
			if (dstExists) {
				// update
				result = FileStoreExecutedActionData.successfulUpdated(dstFileAbsPath,
																	   Instant.now(),
																	   digestIS.getMessageDigest());
			} else {
				// create
				result = FileStoreExecutedActionData.successfulCreated(dstFileAbsPath,
																	   Instant.now(),
																	   digestIS.getMessageDigest());
			}
		} catch (Throwable th) {
			if (dstExists) {
				result = FileStoreExecutedActionData.failedUpdate(dstFileAbsPath,
													   			  Instant.now(),
													   			  th);
			} else {
				result = FileStoreExecutedActionData.failedCreation(dstFileAbsPath,
																	Instant.now(),
																	th);
			}
		}
		log.debug("\t\t\t[end file (elapsed={} milis)]: src={} dst={}",
				  chrono.elapsed(TimeUnit.MILLISECONDS),
				  srcFileAbsPath,dstFileAbsPath);
		return Flowable.just(result);
	}
	private Flowable<FileStoreExecutedActionData> _checkoutFolder(final Path srcFolderRelPath) {
		Path srcFolderAbsPath = _pathTranslator.sourceAbsolutePathFromSourceRelativePath(srcFolderRelPath);
		Path dstFolderAbsPath = _pathTranslator.destinationAbsolutePathFromSourceRelativePath(srcFolderRelPath);

		log.debug("\t\t[Init folder checkout (thread #{}]: src={} dst={}",
				  Thread.currentThread(),
				  srcFolderAbsPath,dstFolderAbsPath);

		// [1] - Create a folder files flowable
		final Flowable<Path> flowable = Flowable.create(new FilesBatchMoveSourceFilesEmitterOnSubscriber(_srcFilerApi,
																							   			 srcFolderAbsPath),
										     			BackpressureStrategy.BUFFER);
		// [2] - Process
		Flowable<FileStoreExecutedActionData> outFlow = flowable.flatMap(srcFileAbsPath -> {
																				// checkout the file
																				Path fileRelPath = _pathTranslator.sourceRelativePathFromSourceAbsolutePath(srcFileAbsPath);
																				return _moveFile(fileRelPath);
																		  });
		log.debug("\t\t[End folder checkout (thread #{})]: src={} dst={}",
				 Thread.currentThread(),
				 srcFolderAbsPath,dstFolderAbsPath);
		return outFlow;

//		flowable.subscribe(new Subscriber<Path>() {
//									private boolean _allFilesOK = true;
//
//									@Override
//									public void onSubscribe(final Subscription s) {
//										log.debug("\t\t[Init folder checkout (thread #{}]: src={} dst={}",
//												  Thread.currentThread(),
//												  srcFolderPath,dstBasePath);
//										s.request(Long.MAX_VALUE);		// beware!! issue a request, otherwise NO data will be emitted
//									}
//									@Override
//									public void onNext(final Path filePath) {
//										boolean fileOK = true;
//										final Date initDate = new Date();
//
//										// compose the destination path
//										Path relFilePath = filePath.remainingPathFrom(_srcBasePath);
//										Path dstFilePath = dstBasePath.joinedWith(relFilePath);
//
//										// checkout the file
//										log.debug("\t\t\t[init file]: src={} dst={}",
//												  filePath,dstFilePath);
//										try {
//											_checkoutFile(filePath,dstFilePath);
//
//										} catch (Throwable th) {
//											th.printStackTrace();
//											fileOK = false;
//											_allFilesOK = false;
//											log.error("\t\t\tError processing file {} : {}",
//													  filePath,th.getMessage(),
//													  th);
//										}
//										final Date finishDate = new Date();
//										final long elapsedTime = finishDate.getTime() - initDate.getTime();
//										log.debug("\t\t\t[end file {} ({} milis)]: {}",
//												  fileOK ? "OK" : "NOK",elapsedTime,
//												  filePath);
//									}
//									@Override
//									public void onError(final Throwable t) {
//										log.error("Error checkingout file from {}: {}",
//												  srcFolderPath,
//												  t.getMessage(),t);
//										_allFilesOK = false;
//									}
//									@Override
//									public void onComplete() {
//										log.debug("\t\t[End folder checkout {} (thread #{})]: src={} dst={}",
//												 _allFilesOK ? "OK" : "NOK",Thread.currentThread(),
//												 srcFolderPath,dstBasePath);
//									}
//						   });
	}
}
