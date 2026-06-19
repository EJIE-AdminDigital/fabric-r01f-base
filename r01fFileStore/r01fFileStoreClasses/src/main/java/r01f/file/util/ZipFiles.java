package r01f.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.file.util.ZipFileBuilder.ZipFilesBuilderSrcStep;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.io.IOStreams;
import r01f.patterns.SignalAbout;
import r01f.patterns.Subscriber;
import r01f.types.Path;

/**
 * ZIP a folder contents in parallel
 * <pre class='brush:java'>
 *		ZipFiles.createZipFileAt(Path.from("c:/develop/temp_dev/test.zip"))
 *				.withFiles(// create an stream of files contained at the given folder
 *						   Folders.createStreamOfFilesAt(Path.from("c:/develop/temp_dev/"))
 *								  .filteringFilesBy(path -> path.isNOT(dstZipFilePath))		// do NOT include the zip file being created
 *								  .withoutFolderDepthLimit()
 *								  .usingDefaultFileApi()
 *								  .build())
 *				.trimPathsInsideZipFile(Path.from("c:/develop/temp_dev/"))
 *				.usingSourceInputStreamSupplier(ThrowingInputStreamFromPathSupplier.ignored(path -> new FileInputStream(path.asAbsoluteString())))
 *				.useParallelZipArchiverUsingDefaultExecutorService()
 *				.storeTempFilesAtDefaultSystemLocation()
 *				.start();
 * </pre>
 * 
 * ZIP a folder contents in serial
 * <pre class='brush:java'>
 * 		// using the builder
 *		ZipFiles.createZipFileAt(Path.from("c:/develop/temp_dev/test-serial.zip"))
 *				.withFiles(// create an stream of files contained at the given folder
 *						   Folders.createStreamOfFilesAt(Path.from("c:/develop/temp_dev/"))
 *								  .filteringFilesBy(path -> path.isNOT(dstZipFilePath))		// do NOT include the zip file being created
 *								  .withoutFolderDepthLimit()
 *								  .usingDefaultFileApi()
 *								  .build())
 *				.trimPathsInsideZipFile(Path.from("c:/develop/temp_dev/"))
 *				.usingSourceInputStreamSupplier(ThrowingInputStreamFromPathSupplier.ignored(path -> new FileInputStream(path.asAbsoluteString())))
 *				.useSerialZipArchiver()
 *				.start();
 * 
 * 		// ... or using the local file-store
 * 		ZipFiles zips = new ZipFiles();
 * 		zips.zip(path,
 * 				 os);		// the output stream where the zip will be written
 * </pre>
 * 
 * A mix of folder and files:
 * <pre class='brush:java'>
 * 		Collection<Path> mixOfFilesAndFolders = List.of(Path.from("_file_path_"),Path.from("_folder_path_"));
 * 
 *  	Stream<Path> pathStream = mixOfFilesAndFolders.stream()
 *  												  .flatMap(path -> {
 *  																FileProperties item = _fileAPI.getProperties(path);
 *  																Stream<Path> outStream = null;
 *  																if (item.isFile()) {
 *  																	outStream = Stream.of(path);
 *  																} else {
 *						   												outStream = Folders.createStreamOfFilesAt(path)
 *								  														   .filteringFilesBy(path -> path.isNOT(dstZipFilePath))		// do NOT include the zip file being created
 *								  														   .withoutFolderDepthLimit()
 *								  														   .usingDefaultFileApi()
 *								  														   .build()
 *  																}
 *  																return outStream;
 *  														   });
 *  	ZipFiles.createZipFileAt(Path.from("c:/develop/temp_dev/test-serial.zip"))
 *				.withFiles(pathStream)
 *				.trimPathsInsideZipFile(Path.from("c:/develop/temp_dev/"))
 *				.usingSourceInputStreamSupplier(ThrowingInputStreamFromPathSupplier.ignored(path -> new FileInputStream(path.asAbsoluteString())))
 *				.useSerialZipArchiver()
 *				.start();
 * </pre>
 */
@Slf4j
public class ZipFiles {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreAPI _fsApi;
	private final FileStoreFilerAPI _fsFilerApi;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR																		  
/////////////////////////////////////////////////////////////////////////////////////////
	public ZipFiles() throws IOException {
		this(new LocalFileStoreAPI(),
			 new LocalFileStoreFilerAPI());
	}
	public ZipFiles(final FileStoreAPI fsApi,final FileStoreFilerAPI fsFilerApi) throws IOException {
		_fsApi = fsApi;
		_fsFilerApi = fsFilerApi;
	}
	public static ZipFiles using(final FileStoreAPI fsApi,final FileStoreFilerAPI fsFilerApi) throws IOException {
		return new ZipFiles(fsApi,fsFilerApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public static ZipFilesBuilderSrcStep createZipFileAt(final Path dstZipFilePath) throws IOException {
		return ZipFileBuilder.createZipFileAt(dstZipFilePath);
	}
	public static ZipFilesBuilderSrcStep createZipFileWritingTo(final OutputStream os) {
		return ZipFileBuilder.createZipFileWritingTo(os);
	}
	public static ZipFilesBuilderSrcStep createZipFileWritingTo(final ZipArchiveOutputStream dstZipFilOutputStream) {
		return ZipFileBuilder.createZipFileWritingTo(dstZipFilOutputStream);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROCESS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static record ZipArchiveEntrySource(Path pathInsideZipFile,
											   InputStreamSupplier streamSupplier) {	// use ThrowingStreamSupplier 
		
		public ZipArchiveEntry toZipArchiveEntry() {
			Path filePathInsiedZip = this.pathInsideZipFile();
			ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(filePathInsiedZip.asRelativeString());
			zipArchiveEntry.setMethod(ZipEntry.DEFLATED);
			
			return zipArchiveEntry;
		}
	}
	public interface ThrowingStreamSupplier {
		InputStream get() throws IOException;
		
		public static InputStreamSupplier unchecked(final ThrowingStreamSupplier s) {
			return () -> {
						try {
							return s.get();
						} catch (Throwable th) {
							throw new RuntimeException(th);
						}
				  };
		}
	}
	public interface ZipFileProcessingSubscriber 
			 extends Subscriber<ZipArchiveOutputStream>,	// notify when zip is finished
			 		 SignalAbout<Path> {					// notify when an entry is about to be created
		@Override
		public default void signal(final Path pathInsideZip) {
			if (log.isTraceEnabled()) log.trace("...adding zip entry {}",pathInsideZip);
		}
		public default void onZipFileEntryProcessed(final Path pathInsideZip) {
			this.signal(pathInsideZip);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	private static abstract class ZipArchiveEntrySourceProcessor {
		protected final ZipFileProcessingSubscriber _subscriber;
		
		public abstract void process(final ZipArchiveEntrySource entry);
		
		public void process(final Stream<ZipArchiveEntrySource> entryStream) {
			entryStream.forEach(this::process);
		}
	}
	private static class ZipArchiveEntrySourceParallelProcessor
			     extends ZipArchiveEntrySourceProcessor {
		
		private final ParallelScatterZipCreator _scatterZipCreator;
		
		public ZipArchiveEntrySourceParallelProcessor(final ParallelScatterZipCreator scatterZipCreator,
													  final ZipFileProcessingSubscriber subscriber) {
			super(subscriber);
			_scatterZipCreator = scatterZipCreator;
		}
		
		@Override
		public void process(final ZipArchiveEntrySource entry) {
			if (log.isTraceEnabled()) log.trace("adding file {}",entry.pathInsideZipFile());
			if (_subscriber != null) _subscriber.signal(entry.pathInsideZipFile());
			
			try {
				// a) create a ZipArchiveEntry
				ZipArchiveEntry zipArchiveEntry = entry.toZipArchiveEntry();
				
				// b) create an InputStreamSupplier
				InputStreamSupplier streamSupplier = entry.streamSupplier();
				
				// c) parallelize (later the zip is created with the individual files created by every thread)
				_scatterZipCreator.addArchiveEntry(zipArchiveEntry,streamSupplier);	// this is submitted to an ExecutorService's thread to be ziped
			} catch (Throwable th) {
				log.error("error while adding entry {}: {}",
						  entry.pathInsideZipFile(),
						  th.getMessage(),th);
				if (_subscriber != null) _subscriber.onError(th);
			}
		}
	}
	private static class ZipArchiveEntrySourceSerialProcessor
			     extends ZipArchiveEntrySourceProcessor {
		
		private final ZipArchiveOutputStream _zipOS;
		
		public ZipArchiveEntrySourceSerialProcessor(final ZipArchiveOutputStream zipOS,
													final ZipFileProcessingSubscriber subscriber) {
			super(subscriber);
			_zipOS = zipOS;
		}
		
		@Override
		public void process(final ZipArchiveEntrySource entry) {
			if (log.isTraceEnabled()) log.trace("adding file {}",entry.pathInsideZipFile());
			if (_subscriber != null) _subscriber.signal(entry.pathInsideZipFile());
			
			try {
				// a) create the ZipArchiveEntry
				ZipArchiveEntry zipArchiveEntry = entry.toZipArchiveEntry();
				
				// a) get the source input stream
				InputStream srcIs = entry.streamSupplier()
										 .get();
				
				// c) add the entry to the zip file being created
				_zipOS.putArchiveEntry(zipArchiveEntry);
				
				// d) add the current file to the zip file being created
				IOStreams.copy(srcIs,
							   _zipOS,
							   false);	// do not close (a zipOS flush & close is needed)
				_zipOS.flush();
				_zipOS.closeArchiveEntry();	
				srcIs.close();
			} catch (Throwable th) {
				log.error("error while adding entry {}: {}",
						  entry.pathInsideZipFile(),
						  th.getMessage(),th);
				if (_subscriber != null) _subscriber.onError(th);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ZIP CREATION
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a zip file using multiple threads managed by an {@link ExecutorService}
	 * @param entryStream
	 * @param dstZipOS
	 * @param executorService
	 * @param scatterGatherBackingStoreSupplier where the temp files created are stored
	 * @param subscriber to be informed about the zip file creation process
	 * @throws IOException
	 */
	public static void parallelCreateZip(final Stream<ZipArchiveEntrySource> entryStream,
										 final ZipArchiveOutputStream dstZipOS,
										 final ExecutorService executorService,
										 final ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier,
										 final ZipFileProcessingSubscriber subscriber) throws IOException {
		// [0] - Setup
		// a) an ExecutorService that manages the Threads that will zip every individual entry
		ExecutorService theExecutorService = executorService != null ? executorService
																	 : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// b) the temp files backing store supplier
		ScatterGatherBackingStoreSupplier theScatterGatherBacingStoreSupplier = scatterGatherBackingStoreSupplier != null ? scatterGatherBackingStoreSupplier
																														  : null;
		// c) the parallel zip creator
		ParallelScatterZipCreator scatterZipCreator = theScatterGatherBacingStoreSupplier != null 
															? new ParallelScatterZipCreator(theExecutorService,theScatterGatherBacingStoreSupplier)
															: new ParallelScatterZipCreator(theExecutorService);	// uses a DefaultBackingStoreSupplier
		
		// [1] - Create a ZipArchiveEntry for every path and submit it to the scatterZipCretor when it's parallelized
		ZipArchiveEntrySourceProcessor entryProcessor = new ZipArchiveEntrySourceParallelProcessor(scatterZipCreator,
																								   subscriber);
		entryProcessor.process(entryStream);
		
		// [2] - Create the zip file > this waits for all threads to finish before creating the zip file
		try {	
			dstZipOS.setUseZip64(Zip64Mode.AsNeeded);
			
			scatterZipCreator.writeTo(dstZipOS);
		} catch (InterruptedException | ExecutionException ex) {
			log.error("Concurrency error while parallel-creating a zip file: {}",ex.getMessage(),ex);
			if (subscriber != null)  subscriber.onError(ex);
		} 
		// [3] - Flush & close
		IOStreams.flushQuietly(dstZipOS);
		dstZipOS.close();	// this also closes the underlying output stream
		
		// [99] - finished
		if (subscriber != null)  subscriber.onSuccess(dstZipOS);
	}
	/**
	 * Creates a zip file adding every file one after another
	 * @param entryStream
	 * @param dstZipOS
	 * @param subscriber to be informed about zip file creation processs
	 * @throws IOException
	 */
	public static void serialCreateZip(final Stream<ZipArchiveEntrySource> entryStream,
									   final ZipArchiveOutputStream dstZipOS,
									   final ZipFileProcessingSubscriber subscriber) throws IOException {
		// [1] - Create a ZipArchiveEntry for every path and add it to the zip file 
		ZipArchiveEntrySourceProcessor entryProcessor = new ZipArchiveEntrySourceSerialProcessor(dstZipOS,
																								 subscriber);
		entryProcessor.process(entryStream);
		
		// [2] - Flush & close
		IOStreams.flushQuietly(dstZipOS);
		dstZipOS.close();	// this also closes the underlying output stream
		
		// [99] - finished
		if (subscriber != null) subscriber.onSuccess(dstZipOS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO BE USED INSIDE LAMBDA EXPRESSIONS
/////////////////////////////////////////////////////////////////////////////////////////	
	@FunctionalInterface
	public interface ThrowingInputStreamSupplier {
		public InputStream get() throws IOException;
		
		public static InputStreamSupplier unchecked(final ThrowingInputStreamSupplier throwingSupplier) {
			return () -> {
						try {
							return throwingSupplier.get();
						} catch (IOException th) {
							throw new RuntimeException(th);
						}
				  };
		}
	}
	public interface InputStreamFromPathSupplier {
		InputStream getFrom(final Path path);
	}
	public interface ThrowingInputStreamFromPathSupplier {
		InputStream getFrom(final Path path) throws IOException;
		
		public static InputStreamFromPathSupplier unchecked(final ThrowingInputStreamFromPathSupplier throwingSupplier) {
			return path -> {
						try {
							return throwingSupplier.getFrom(path);
						} catch (IOException ioEx) {
							throw new RuntimeException(ioEx);
						}
				  };
		}
		public static InputStreamFromPathSupplier ignored(final ThrowingInputStreamFromPathSupplier throwingSupplier) {
			return path -> {
						try {
							return throwingSupplier.getFrom(path);
						} catch (IOException ioEx) {
							log.error("IO error while trying to get an InputStream to file at {}: {}",
									  path.asAbsoluteString(),
									  ioEx.getMessage(),ioEx);
						}
						return null;
				  };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ZIP 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final OutputStream os) throws IOException {
		this.zip(folder,
				 -1,			// depth limit = -1 (no limit)
				 null,			// no file filter
				 os,
				 folder);		// path trim	
	}
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param fileFilter
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final FileFilter fileFilter,
					final OutputStream os) throws IOException {
		this.zip(folder,
				 -1,		// depth limit = -1 (no limit)
				 fileFilter,
				 os,
				 folder);	// path trim	
	}
	/**
	 * Zips a folder contents 
	 * @param folder
	 * @param depthLimit
	 * @param fileFilter
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public void zip(final Path folder,
					final int depthLimit,
					final FileFilter fileFilter,
					final OutputStream os) throws IOException {
		this.zip(folder,
				 depthLimit,
				 fileFilter,
				 os,
				 folder);	// path trim	
	}
	public final void zip(final Path startFolderPath,final int depthLimit,
						  final FileFilter fileFilter,
						  final OutputStream os,
						  final Path pathTrim) throws IOException {
		this.zip(startFolderPath,
				 depthLimit,
				 fileFilter,
				 os,
				 path -> path.remainingPathFrom(pathTrim));	// transform function
	}
	public final void zip(final Path startFolderPath,final int depthLimit,
						  final FileFilter fileFilter,
						  final OutputStream os,
						  final Function<Path,Path> pathTransform) throws IOException {
		// Get the source files stream
		Stream<ZipArchiveEntrySource> srcStream = Folders.createStreamOfFilesAt(startFolderPath) 
														 .filteringFilesBy(fileFilter)
														 .withFolderDepthLimit(depthLimit)
														 .using(_fsApi,_fsFilerApi)
														 .build()	// this returns a Stream<Path>
														 // create a ZipArchiveEntrySource for each Path
														 .map(path -> new ZipArchiveEntrySource(pathTransform != null ? pathTransform.apply(path) : path,
																						   	    ThrowingInputStreamSupplier.unchecked(() -> _fsApi.getFileInputStream(path))));
		// zip
		try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(os)) {
			// serial zip!!!!
			ZipFiles.serialCreateZip(srcStream,
									 zipOutputStream, 
									 null);		// no subscriber
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UNZIP																		  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * unzips a file to a given path
	 * @param zIS
	 * @param dstPath
	 * @return
	 * @throws IOException
	 */
	public Collection<Path> unzip(final InputStream zIS,
					  			  final Path dstPath) throws IOException {
		// check that the destination is NOT a file
		if (_fsApi.existsFile(dstPath))	throw new IllegalArgumentException("Cannot extract ZIP to " + dstPath + " since it's an existent FILE!!");
		
		Collection<Path> outCreatedPaths = Lists.newArrayList();
		
		ZipInputStream zipIS = new ZipInputStream(zIS);
		ZipEntry zipEntry = zipIS.getNextEntry();
		while (zipEntry != null) {
			// BEWARE! a file entry can come before the directory entry where the file is located
			// i.e.:
			//   /foo/foo.txt
			//   /foo/
			if (zipEntry.isDirectory()) {
				// next entry
				zipEntry = zipIS.getNextEntry();
				continue;
			}
			
			// it's a file...
			
			// a) get the file destination path
			Path filePathInsideZip = Path.from(zipEntry.getName());
			Path fileDstPath = dstPath.joinedWith(filePathInsideZip);
			
			outCreatedPaths.add(fileDstPath);
			
			// b) Extract the file
			NonCloseableInputStreamWrapper nonCloseableZipIS = new NonCloseableInputStreamWrapper(zipIS);
			_fsApi.writeToFile(nonCloseableZipIS,
							   fileDstPath,
							   true);		// overwrite
			nonCloseableZipIS.close();		
			
			// next entry
			zipEntry = zipIS.getNextEntry();
		}
		zipIS.close();
		
		// return 
		return outCreatedPaths;
	}
}
