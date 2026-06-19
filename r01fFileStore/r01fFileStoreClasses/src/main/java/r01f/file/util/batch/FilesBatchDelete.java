package r01f.file.util.batch;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.Lists;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.internal.operators.flowable.FlowableError;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileStoreExecutedActionData;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;

/**
 * Deletes resources from a filesystem
 */
@Slf4j
public class FilesBatchDelete {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreAPI _fileApi;
	private final FileStoreFilerAPI _filerApi;

	private final Scheduler _scheduler;

	private final Function<Path,Path> _pathTranslator;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FilesBatchDelete(final FileStoreAPI fileApi,final FileStoreFilerAPI filerApi,
							final Function<Path,Path> pathTranslator) {
		this(fileApi,filerApi,
			 pathTranslator,
			 Schedulers.io());
	}
	public FilesBatchDelete(final FileStoreAPI fileApi,final FileStoreFilerAPI filerApi,
							final Function<Path,Path> pathTranslator,
							final Scheduler scheduler) {
		// file-store api
		_fileApi = fileApi;
		_filerApi = filerApi;

		// Thread pool
		_scheduler = scheduler;

		// the base path
		_pathTranslator = pathTranslator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Deletes the given folder or file paths
	 * returns a collection of {@link FileStoreExecutedActionData} objects that encapsulates each file deletion result
	 * ... so if a path is a folder path, a result object is returned for every file inside the folder
	 * @param srcPaths
	 * @return
	 * @throws IOException
	 */
	public Collection<FileStoreExecutedActionData> delete(final Collection<Path> srcPaths) throws IOException {
		Flowable<FileStoreExecutedActionData> flowable = this.flowableDelete(srcPaths);
		return Lists.newArrayList(flowable.blockingIterable());		// BEWARE!! consume the iterable
	}
	/**
	 * Deletes the given folder or file paths
	 * returns a rx {@link FlowableError} of {@link FileStoreExecutedActionData} objects that encapsulates each file deletion result
	 * ... so if a path is a folder path, a result object is returned for every file inside the folder
	 * @param paths
	 * @return
	 * @throws IOException
	 */
	public Flowable<FileStoreExecutedActionData> flowableDelete(final Collection<Path> paths) throws IOException {
		log.debug("Deleting {} resources", paths.size());

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
		return Flowable.fromIterable(paths)
					// run in parallel
					.parallel()
					.runOn(_scheduler)

					// Transform the given path to the path where the resource resides
					.map(path -> _pathTranslator.apply(path))
					// Filter non-existent resources
					.filter(path -> {
								boolean outExists = _filerApi.existsFolder(path)	// it's a folder
												 || _fileApi.existsFile(path);		// it's a file
								if (!outExists) log.debug("\t...{} DOES NOT EXIST!!",
										 				 path);
								return outExists;
							})
					.flatMap(dstAbsPath -> {
								Flowable<FileStoreExecutedActionData> outFlow = null;

								if (_filerApi.existsFolder(dstAbsPath)) {
									// it's a folder: delete it
									outFlow = _deleteFolder(dstAbsPath);
								}
								else if (_fileApi.existsFile(dstAbsPath)) {
									// it's a file: just deleter the file
									outFlow = _deleteFile(dstAbsPath);
								}
								return outFlow;
							})
					.doOnError(th -> log.error("\t... error during deletion: {}",
							  				   th.getMessage(),th))
					// wait all rails to finish
					.sequential();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private Flowable<FileStoreExecutedActionData> _deleteFolder(final Path folderDstAbsPath) throws IOException {
		log.info("\t...delete FOLDER at {}",
				 folderDstAbsPath);
		Flowable<FileStoreExecutedActionData> outFlow = null;

		boolean deleted = _filerApi.deleteFolder(folderDstAbsPath);
		if (deleted) {
			outFlow = Flowable.just(FileStoreExecutedActionData.successfulDeleted(folderDstAbsPath,
																				  Instant.now()));
		} else {
			outFlow = Flowable.just(FileStoreExecutedActionData.failedDeletion(folderDstAbsPath,
																			   Instant.now()));
		}
		return outFlow;
	}
	private Flowable<FileStoreExecutedActionData> _deleteFile(final Path fileDstAbsPath) throws IOException {
		log.info("\t...delete FILE at {}",
				 fileDstAbsPath);
		Flowable<FileStoreExecutedActionData> outFlow = null;

		boolean deleted = _fileApi.deleteFile(fileDstAbsPath);
		if (deleted) {
			outFlow = Flowable.just(FileStoreExecutedActionData.successfulDeleted(fileDstAbsPath,
																				  Instant.now()));
		} else {
			outFlow = Flowable.just(FileStoreExecutedActionData.failedDeletion(fileDstAbsPath,
																			   Instant.now()));
		}
		return outFlow;
	}
}
