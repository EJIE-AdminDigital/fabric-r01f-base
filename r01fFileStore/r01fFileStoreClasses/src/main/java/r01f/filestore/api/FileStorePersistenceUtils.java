package r01f.filestore.api;

import java.io.IOException;

import com.google.common.collect.FluentIterable;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileProperties;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class FileStorePersistenceUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final FileStoreAPI _fileStoreAPI;
	protected final FileStoreFilerAPI _fileStoreFilerAPI;	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileStorePersistenceUtils(final FileStoreAPI fileStoreAPI,final FileStoreFilerAPI fileStoreFilerAPI) {
		_fileStoreAPI = fileStoreAPI;
		_fileStoreFilerAPI = fileStoreFilerAPI;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Lists a folder's contents
	 * @param folderFullPath
	 * @return
	 * @throws IOException
	 */
	public Path[] listFolderContents(final Path folderFullPath) throws IOException {
		return this.listFolderContents(folderFullPath,
								   	   new FileFilter() {
												@Override
												public boolean accept(final FileProperties props) {
													return props.isFile();
												}
												@Override
												public boolean accept(final Path path) {
													return path.isFilePath();
												}
								   	   });
	}
	/**
	 * Lists a folder's contents
	 * @param folderFullPath
	 * @param fileFilter
	 * @return
	 * @throws IOException
	 */
	public Path[] listFolderContents(final Path folderFullPath,
									 final FileFilter fileFilter) throws IOException {
		// [0] - Check if the folder really exists
		if (!_fileStoreFilerAPI.existsFolder(folderFullPath)) return new Path[] {};
		
		// [1] - Get the folder contents
		FileProperties[] files = _fileStoreFilerAPI.listFolderContents(folderFullPath,
																	   fileFilter,
																	   false);	// recursive
		// [99] - Return
		return CollectionUtils.hasData(files)
						? FluentIterable.from(files)
										.transform(file -> file.getPath())
										.toArray(Path.class)
						: new Path[] {};
	}
	/**
	 * Lists a folder's contents recursively
	 * @param folderFullPath
	 * @return
	 * @throws IOException
	 */
	public Path[] listFolderContentsRecursively(final Path folderFullPath) throws IOException {
		// BEWARE!! This is the exact filter that works with HDFS. 
		// Other configurations works in local fs, but not with HDFS.
		FileFilter fileFilter = new FileFilter() {
											@Override
											public boolean accept(final FileProperties props) {
												return props.isFile() || props.isFolder(); // !!!
											}
											@Override
											public boolean accept(final Path path) {
												return path.isFilePath() || path.isFolderPath(); // !!!
											}
								};
		// [0] - Check if the folder really exists
		if (!_fileStoreFilerAPI.existsFolder(folderFullPath)) return new Path[] {};
		
		// [1] - Get the folder contents
		FileProperties[] files = _fileStoreFilerAPI.listFolderContents(folderFullPath,
																	   fileFilter,
																	   true);	// recursive
		// [99] - Return
		return CollectionUtils.hasData(files)
						? FluentIterable.from(files)
										.transform(file -> file.getPath())
										.toArray(Path.class)
						: new Path[] {};
	}
	/**
	 * Ensures the existence of the given folder paths
	 * If any folder does NOT exists, it's created if [createIfNotExists] param is true
	 * @param paths
	 * @param createIfNotExists
	 * @return
	 */
	public void ensurePaths(final Path[] paths,
							final boolean createIfNotExists) throws IOException {
		if (CollectionUtils.isNullOrEmpty(paths)) return;
		for (final Path path : paths) {
			// check if the folder exists
			boolean exists = _fileStoreFilerAPI.existsFolder(path);
			log.debug("\t- folder {} does {}exists; create {}",
					  path,
					  exists ? "" : "NOT ",
					  createIfNotExists);
			if (exists || !createIfNotExists) continue;

			// create the folder
			boolean created = _fileStoreFilerAPI.createFolder(path);
			if (!created) log.warn("could NOT create folder at {}",
								   path);
		}
	}
}
