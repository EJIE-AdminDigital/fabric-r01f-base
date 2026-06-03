package r01f.file.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.file.util.ZipFileBuilder.ZipFilesBuilderModeStep;
import r01f.file.util.ZipFiles.InputStreamFromPathSupplier;
import r01f.file.util.ZipFiles.ThrowingInputStreamFromPathSupplier;
import r01f.file.util.ZipFiles.ZipArchiveEntrySource;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.patterns.IsBuilderStep;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates an archive (a zip file) from a [folder content report diff] that lists:
 * 	- new or updated files
 * 	- deleted files
 * 	- unthouched files
 * 
 * The created archive file contains:
 * 	- The new or updated files
 * 	- A file that lists the deleted files paths	
 * 
 * Usage:
 * <pre class='brush:java'>
 * 			// create the file apis
 * 			FileStoreAPI fileApi = new LocalFileStoreAPI();
 * 			FileStoreFilerAPI filerApi = new LocalFileStoreFilerAPI();
 * 
 * 			// create the folder contents reporter
 * 			FolderContentReporter reporter = new FolderContentReporter(filerApi);
 * 
 * 			// create the report
 * 			Set<FolderContentReportItem> report1 = reporter.reportFrom(rootPath);
 * 
 * 			// ... do some changes at [root path] (create files, update files or delete files)
 * 
 * 			// create the report again
 * 			Set<FolderContentReportItem> report2 = reporter.reportFrom(rootPath);
 * 			
 * 			// Compute the differences
 * 			FolderContentReportDiff diff = FolderContentReporter.diff(items1,items2);
 * 			
 * 			// Create a ZIP file that contains the differences
 *			FolderContentReportDiffArchiver.createArchiveIncludingUntouchedFilesFrom(diff)
 *										   .writingTo(diffArchivePath)
 *										   .trimPathsInArchiveFileWith(rootPath)
 *										   .usingDefaultSourceInputStreamSupplier()
 *										   .useSerialZipArchiver()
 *										   .start();
 *
 *			// ... to create the ZIP file using multiple Threads use:
 *			FolderContentReportDiffArchiver.createArchiveIncludingUntouchedFilesFrom(diff)
 *										   .writingTo(diffArchivePath)
 *										   .trimPathsInArchiveFileWith(rootPath)
 *										   .usingDefaultSourceInputStreamSupplier()
 *										   .useParallelZipArchiverUsingDefaultExecutorService()
 *										   .storeTempFilesAtDefaultSystemLocation()
 *										   .start();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FolderContentReportDiffArchiver {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static FolderContentReportDiffArchiverBuilderOutputStep createArchiveFrom(final FolderContentReportDiff diff) {
		return new FolderContentReportDiffArchiverBuilderOutputStep(diff,
																	false);		// do NOT include untouched files
	}
	public static FolderContentReportDiffArchiverBuilderOutputStep createArchiveIncludingUntouchedFilesFrom(final FolderContentReportDiff diff) {
		return new FolderContentReportDiffArchiverBuilderOutputStep(diff,
																	true);		// include untouched files
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderContentReportDiffArchiverBuilderOutputStep
	  		 implements IsBuilderStep {
		private final FolderContentReportDiff _diff;
		private final boolean _includeUntouchedFiles;
	
		@SuppressWarnings("resource")
		public FolderContentReportDiffArchiverBuilderPathTrimStep writingTo(final Path dstArchivePath) throws IOException {
			// destination zip file
			File zipFile = new File(dstArchivePath.asAbsoluteString());
			zipFile.delete();
			
			// return 
			return this.writingTo(new FileOutputStream(zipFile));
		}
		@SuppressWarnings("resource")
		public FolderContentReportDiffArchiverBuilderPathTrimStep writingTo(final OutputStream os) {
			ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(os);
			return this.writingTo(zipOutputStream);
		}
		public FolderContentReportDiffArchiverBuilderPathTrimStep writingTo(final ZipArchiveOutputStream dstArchiveOS) {
			return new FolderContentReportDiffArchiverBuilderPathTrimStep(_diff,_includeUntouchedFiles,
																		  dstArchiveOS);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderContentReportDiffArchiverBuilderPathTrimStep
	  		 implements IsBuilderStep {
		private final FolderContentReportDiff _diff;
		private final boolean _includeUntouchedFiles;
		private final ZipArchiveOutputStream _dstArchiveOS;
		
		public FolderContentReportDiffArchiverBuilderInputStreamSupplierStep transfrormPathsInArchiveFileUsing(final Function<Path,Path> pathTransform) {
			return new FolderContentReportDiffArchiverBuilderInputStreamSupplierStep(_diff,_includeUntouchedFiles,
																	   				 _dstArchiveOS,
																	   				 pathTransform);			
		}
		public FolderContentReportDiffArchiverBuilderInputStreamSupplierStep trimPathsInArchiveFileWith(final Path trimPath) {
			return this.transfrormPathsInArchiveFileUsing(path -> path.remainingPathFrom(trimPath));			
		}
		public FolderContentReportDiffArchiverBuilderInputStreamSupplierStep useOriginalPathsInArchiveFile() {
			return new FolderContentReportDiffArchiverBuilderInputStreamSupplierStep(_diff,_includeUntouchedFiles,
																	   				 _dstArchiveOS,
																	   				 null);	// do not trim
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderContentReportDiffArchiverBuilderInputStreamSupplierStep
	  		 implements IsBuilderStep {
		private final FolderContentReportDiff _diff;
		private final boolean _includeUntouchedFiles;
		private final ZipArchiveOutputStream _dstArchiveOS;
		private final Function<Path,Path> _pathTransform;
		
		public ZipFilesBuilderModeStep usingSourceInputStreamSupplier(final InputStreamFromPathSupplier streamFromPathSuplier) {
			// Create an stream of the files to be included in the ZIP
			// a) the deleted files list file > a stream with just an element
			Stream<ZipArchiveEntrySource> deletedFilesListStream = Stream.of(new ZipArchiveEntrySource(// the deleted files list file path
																									   Path.from(".deleted-list"),
																									   // InputStreamSupplier
																									   () -> _createDeletedFilesListFile(_diff.getDeletedPaths(),
																											   							 _pathTransform)));
			// b) new or updated
			Stream<ZipArchiveEntrySource> newOrUpdatedStream = _diff.getNewOrUpadtedPaths()
																    .stream()
																    .map(path -> new ZipArchiveEntrySource(// the path inside the zip file
																		   								   _pathTransform.apply(path),
																		   								   // InputStreamSupplier
																		   								   () -> streamFromPathSuplier.getFrom(path)));
			// c) untouched
			Stream<ZipArchiveEntrySource> untouchedStream = _includeUntouchedFiles ? _diff.getUntouchedPaths()
																						  .stream()
																					      .map(path -> new ZipArchiveEntrySource(// the path inside the zip file
																							   								     _pathTransform.apply(path),
 																							   								     // InputStreamSupplier
																							   								     () -> streamFromPathSuplier.getFrom(path)))
																				   : Stream.empty();
			// d) concat > the same as Stream.of(streams)
			//								 .flatMap(stream -> stream)
			Stream<ZipArchiveEntrySource> srcStream = com.google.common.collect.Streams.concat(deletedFilesListStream,
																							   newOrUpdatedStream,
																							   untouchedStream);
			return new ZipFilesBuilderModeStep(_dstArchiveOS,
											   srcStream);
		}
		public ZipFilesBuilderModeStep usingDefaultSourceInputStreamSupplier() {
			return this.usingSourceInputStreamSupplier(ThrowingInputStreamFromPathSupplier.unchecked(path -> new LocalFileStoreAPI().getFileInputStream(path)));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static InputStream _createDeletedFilesListFile(final Collection<Path> deletedPaths,
														   final Function<Path,Path> pathTransform) {
		// create a text file list which contains a line for every deleted files paths
		String delList = CollectionUtils.hasData(deletedPaths) 
								? deletedPaths.stream()
											  .map(path -> pathTransform != null ? pathTransform.apply(path).asRelativeString()
											  			 				    	 : path.asRelativeString())
											  .collect(Collectors.joining("\n"))
								: "";
		InputStream bis = new ByteArrayInputStream(delList.getBytes());
		return bis; 
	}
}
