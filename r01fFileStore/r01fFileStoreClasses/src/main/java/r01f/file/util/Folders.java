package r01f.file.util;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class Folders {
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE A STREAM OF FILES CONTAINED IN A FOLDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an stream of all files under the source path
	 * <pre class='brush:java'>
	 * 		Stream<Path> pathStream = Folders.createStreamOfFilesAt(Path.of("/develop"))
	 * 										 .doNOTUserFileFilter()
	 * 										 .withoutFolderDepthLimit()
	 * 										 .using(fsApi,fsFilerApi)
	 * 										 .build();
	 * </pre>
	 * @param srcPath
	 * @return
	 */
	public static FolderWalkerBuilderFiltertStep createStreamOfFilesAt(final Path srcPath) {
		return new FolderWalkerBuilderFiltertStep(srcPath);		
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderWalkerBuilderFiltertStep {
		private final Path _srcPath;
		
		public FolderWalkerBuilderDepthLimitStep doNOTUserFileFilter() {
			return new FolderWalkerBuilderDepthLimitStep(_srcPath,
														 null);	// no filter 
		}
		public FolderWalkerBuilderDepthLimitStep filteringFilesBy(final FileFilter... fileFilters) {
			return new FolderWalkerBuilderDepthLimitStep(_srcPath,
												  		 FileFilter.combine(fileFilters));
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderWalkerBuilderDepthLimitStep {
		private final Path _srcPath;
		private final FileFilter _fileFilter;
		
		public FolderWalkerBuilderApiStep withoutFolderDepthLimit() {
			return this.withFolderDepthLimit(-1);	// no folder depth limit
		}
		public FolderWalkerBuilderApiStep withFolderDepthLimit(final int depthLimit) {
			return new FolderWalkerBuilderApiStep(_srcPath,
												  _fileFilter,
												  depthLimit);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderWalkerBuilderApiStep {
		private final Path _srcPath;
		private final FileFilter _fileFilter;
		private final int _folderDepthLim;
		
		public FolderWalkerBuilderBuildStep usingDefaultFileApi() throws IOException {
			return this.using(new LocalFileStoreAPI(),new LocalFileStoreFilerAPI());
		}
		public FolderWalkerBuilderBuildStep using(final FileStoreAPI fsApi,final FileStoreFilerAPI fsFilerApi) {
			return new FolderWalkerBuilderBuildStep(_srcPath,
													_fileFilter,
													_folderDepthLim,
													fsApi,fsFilerApi);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FolderWalkerBuilderBuildStep {
		private final Path _srcPath;
		private final FileFilter _fileFilter;
		private final int _folderDepthLimit;
		private final FileStoreAPI _fsApi;
		private final FileStoreFilerAPI _fsFilerApi;
		
		public Stream<Path> build() throws IOException {
			Stream<Path> srcPathStream = null;
			
			FileProperties srcFileProps = _fsApi.getFileProperties(_srcPath);
			if (srcFileProps == null) throw new IllegalArgumentException("File / folder at " + _srcPath + " does NOT exists!!");
			if (srcFileProps.isFolder()) {
				// create a folder walker that lists the folder contents
				srcPathStream = new FolderWalkerIterable<Path>(_srcPath,
															   _fileFilter,
															   _folderDepthLimit,
															   fileProps -> fileProps.getPath()) {
											@Override
											protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
												return _fsApi.getFileProperties(folderPath);
											}
											@Override
											protected Collection<FileProperties> listFolderContents(final Path folderPath, 
																									final FileFilter filter) throws IOException {
												FileProperties[] contents = _fsFilerApi.listFolderContents(folderPath,
																					  					   filter,
																					  					   false);	// not recursive
												return CollectionUtils.hasData(contents) ? Lists.newArrayList(contents)
																						 : null;
											}
							  		 }.asStream();
			} 
			else if (srcFileProps.isFile()) {
				srcPathStream = Stream.of(_srcPath);
			}
			return srcPathStream;
		}
	}
}
