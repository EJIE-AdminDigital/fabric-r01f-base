package r01f.file.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.file.util.ZipFiles.InputStreamFromPathSupplier;
import r01f.file.util.ZipFiles.ThrowingInputStreamFromPathSupplier;
import r01f.file.util.ZipFiles.ZipArchiveEntrySource;
import r01f.file.util.ZipFiles.ZipFileProcessingSubscriber;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.patterns.IsBuilderStep;
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
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ZipFileBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ "resource" })
	public static ZipFilesBuilderSrcStep createZipFileAt(final Path dstZipFilePath) throws IOException {
		// destination zip file
		File zipFile = new File(dstZipFilePath.asAbsoluteString());
		zipFile.delete();
		
		// return 
		return ZipFileBuilder.createZipFileWritingTo(new FileOutputStream(zipFile));
	}
	@SuppressWarnings({ "resource" })
	public static ZipFilesBuilderSrcStep createZipFileWritingTo(final OutputStream os) {
		ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(os);
		return ZipFileBuilder.createZipFileWritingTo(zipOutputStream);
	}
	public static ZipFilesBuilderSrcStep createZipFileWritingTo(final ZipArchiveOutputStream dstZipFilOutputStream) {
		return new ZipFilesBuilderSrcStep(dstZipFilOutputStream);
	}
////////// Builder source step ----------------------------------------------------------	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ZipFilesBuilderSrcStep 
	         implements IsBuilderStep {
		private final ZipArchiveOutputStream _dstZipFilOutputStream;
		
		public ZipFilesBuilderPathTransformStep withFiles(final Collection<Path> paths) {
			return this.withFiles(paths.stream());
		}
		public ZipFilesBuilderPathTransformStep withFiles(final Iterable<Path> paths) {
			return this.withFiles(com.google.common.collect.Streams.stream(paths));
		}
		public ZipFilesBuilderPathTransformStep withFiles(final Iterator<Path> paths) {
			return this.withFiles(com.google.common.collect.Streams.stream(paths));
		}
		public ZipFilesBuilderPathTransformStep withFiles(final Stream<Path> pathStream) {
			return new ZipFilesBuilderPathTransformStep(_dstZipFilOutputStream,
												   		pathStream);
		}
		public ZipFilesBuilderModeStep withZipFileEntries(final Stream<ZipArchiveEntrySource> entryStream) {
			return new ZipFilesBuilderModeStep(_dstZipFilOutputStream,
											   entryStream);
		}
	}
////////// Builder source path trim -----------------------------------------------------	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ZipFilesBuilderPathTransformStep 
			 implements IsBuilderStep {
		private final ZipArchiveOutputStream _dstZipFilOutputStream;
		private final Stream<Path> _srcPathsStream;
		
		public ZipFilesBuilderEntryInputStreamFactoryStep trimPathsInsideZipFile(final Path pathTrim) {
			return new ZipFilesBuilderEntryInputStreamFactoryStep(_dstZipFilOutputStream,
														   		  _srcPathsStream,
														   		  path -> path.remainingPathFrom(pathTrim));	// path transform function
		}
		public ZipFilesBuilderEntryInputStreamFactoryStep transformPathsInsideZipFile(final Function<Path,Path> pathTransform) {
			return new ZipFilesBuilderEntryInputStreamFactoryStep(_dstZipFilOutputStream,
														   		  _srcPathsStream,
														   		  pathTransform);
		}
		public ZipFilesBuilderEntryInputStreamFactoryStep useSourcePathsInsideZipFile() {
			return this.trimPathsInsideZipFile(null);
		}
	}
////////// Builder inputstream factory step ---------------------------------------------
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ZipFilesBuilderEntryInputStreamFactoryStep 
			 implements IsBuilderStep {
		private final ZipArchiveOutputStream _dstZipFilOutputStream;
		private final Stream<Path> _srcPathsStream;
		private final Function<Path,Path> _pathTransform;
		
		public ZipFilesBuilderModeStep usingSourceInputStreamSupplier(final InputStreamFromPathSupplier streamFromPathSuplier) {
			Stream<ZipArchiveEntrySource> entryStream = _srcPathsStream.map(path -> {
																				// entry path inside zip file
																				Path pathInsideZip = _pathTransform != null ? _pathTransform.apply(path)
																													   		: path;
																				// entry inputstream provider
																				InputStreamSupplier streamSupplier = () -> streamFromPathSuplier.getFrom(path);
																				
																				// return the zip archive entry data
																				return new ZipArchiveEntrySource(pathInsideZip,
																												 streamSupplier);
																			});
			return new ZipFilesBuilderModeStep(_dstZipFilOutputStream,
														  entryStream);
		}
		public ZipFilesBuilderModeStep usingDefaultSourceInputStreamSupplier() {
			return this.usingSourceInputStreamSupplier(ThrowingInputStreamFromPathSupplier.unchecked(path -> new LocalFileStoreAPI().getFileInputStream(path)));
		}
	}
////////// Parallel step ----------------------------------------------------------------
	@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
	public static class ZipFilesBuilderModeStep 
			 implements IsBuilderStep {
		private final ZipArchiveOutputStream _dstZipFilOutputStream;
		private final Stream<ZipArchiveEntrySource> _entryStream;
		
		public ZipFilesBuilderParallelGatherBackingStoreStep useParallelZipArchiver(final ExecutorService executorService) {
			return new ZipFilesBuilderParallelGatherBackingStoreStep(_dstZipFilOutputStream,
																	 _entryStream,
																	 executorService);
		}
		public ZipFilesBuilderParallelGatherBackingStoreStep useParallelZipArchiverUsingDefaultExecutorService() {
			return new ZipFilesBuilderParallelGatherBackingStoreStep(_dstZipFilOutputStream,
																	 _entryStream,
																	 null);
		}
		public ZipFilesBuilderSerialBuildStep useSerialZipArchiver() {
			return new ZipFilesBuilderSerialBuildStep(_dstZipFilOutputStream,
													  _entryStream);
		}
	} 
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ZipFilesBuilderParallelGatherBackingStoreStep 
			 implements IsBuilderStep {
		private final ZipArchiveOutputStream _dstZipFilOutputStream;
		private final Stream<ZipArchiveEntrySource> _entryStream;
		private final ExecutorService _executorService;
		
		public ZipFilesBuilderParallelBuildStep storeTempFilesUsing(final ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) {
			return new ZipFilesBuilderParallelBuildStep(_dstZipFilOutputStream,
											 			_entryStream,
											 			_executorService,
											 			scatterGatherBackingStoreSupplier);
		}
		public ZipFilesBuilderParallelBuildStep storeTempFilesAtDefaultSystemLocation() {
			return new ZipFilesBuilderParallelBuildStep(_dstZipFilOutputStream,
											 			_entryStream,
											 			_executorService,
											 			null);
		}
	}
////////// Builder build step -----------------------------------------------------------
	public interface ZipFilesBuilderBuildStep {
		public void start() throws IOException;
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static abstract class ZipFilesBuilderBuildStepBase
					  implements ZipFilesBuilderBuildStep,
					  			 IsBuilderStep {
		protected final ZipArchiveOutputStream _dstZipFilOutputStream;
		protected final Stream<ZipArchiveEntrySource> _entryStream;
		
		@Override
		public void start() throws IOException {
			this.start(// subscriber for zip create END event
					   dstZipFileOutputStream -> {
						   log.info("[zip file builder] > end parallel-creating zip file: close the output stream!!");
							// close the destination output stream
							try {
								dstZipFileOutputStream.close();
							} catch (IOException ioEx) {
								log.error("Error while trying to close the zip file: {}",ioEx.getMessage(),ioEx);
							}
					   });
		}
		public abstract void start(final ZipFileProcessingSubscriber subscriber) throws IOException;
	}
	public static class ZipFilesBuilderParallelBuildStep 
		 		extends ZipFilesBuilderBuildStepBase {
		private final ExecutorService _executorService;
		private final ScatterGatherBackingStoreSupplier _scatterGatherBackingStoreSupplier;
		
		private ZipFilesBuilderParallelBuildStep(final ZipArchiveOutputStream dstZipFilOutputStream,
												 final Stream<ZipArchiveEntrySource> entryStream,        
												 final ExecutorService executorService,
												 final ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier)	{
			super(dstZipFilOutputStream,
				  entryStream);
			_executorService = executorService;
			_scatterGatherBackingStoreSupplier = scatterGatherBackingStoreSupplier;
		}
		
		@Override
		public void start(final ZipFileProcessingSubscriber subscriber) throws IOException {
			ZipFiles.parallelCreateZip(_entryStream,
									   _dstZipFilOutputStream, 
									   _executorService, 
									   _scatterGatherBackingStoreSupplier, 
									   subscriber);
		}
	}
	public static class ZipFilesBuilderSerialBuildStep 
		 		extends ZipFilesBuilderBuildStepBase {
		
		private ZipFilesBuilderSerialBuildStep(final ZipArchiveOutputStream dstZipFilOutputStream,
											   final Stream<ZipArchiveEntrySource> entryStream)	{
			super(dstZipFilOutputStream,
				  entryStream);
		}
		@Override
		public void start(final ZipFileProcessingSubscriber subscriber) throws IOException {
			ZipFiles.serialCreateZip(_entryStream,
									 _dstZipFilOutputStream, 
									 subscriber);
		}
	}
}
