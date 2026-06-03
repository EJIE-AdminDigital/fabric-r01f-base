package r01f.filestore.api.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import r01f.file.FileProperties;
import r01f.file.util.FolderWalker;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileFilters;
import r01f.types.Path;

/**
 * Recursive file listing under a specified directory.
 */
final class LocalFolderRecursiveListing 
	extends FolderWalker<FileProperties> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalFolderRecursiveListing(final FileFilter filter) {
		super(filter,
			  -1);		// no depth limit
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Recursively walk a directory tree and return a List of all
	 * files found, the list is sorted using File.compareTo().
	 * @param startingFolder is a valid directory, which can be read.
	 * @throws FileNotFoundException
	 */
	public Collection<FileProperties> recurseFolderContents(final File startingFolder) throws IOException {
		Stream<FileProperties> stream = this.recurseFolderContentsStream(startingFolder);
		return stream.collect(Collectors.toList());
	}
	/**
	 * Recursively walk a directory tree and return a List of all
	 * files found, the list is sorted using File.compareTo().
	 * @param startingFolder is a valid directory, which can be read.
	 * @throws FileNotFoundException
	 */
	public Stream<FileProperties> recurseFolderContentsStream(final File startingFolder) throws IOException {
		_validateDirectory(startingFolder);
		
		Stream.Builder<FileProperties> streamBuilder = Stream.builder();
		this.walk(Path.from(startingFolder),
				  streamBuilder);
		return streamBuilder.build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
		File f = new File(folderPath.asAbsoluteString());
		if (f.exists()
		 && f.isDirectory()) {
			return LocalFileProperties.from(f);
		} 
		throw new IllegalArgumentException(folderPath + " is NOT a folder!");
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final Path folderPath,
															final FileFilter filter) throws IOException {
		File f = new File(folderPath.asAbsoluteString());
		File[] files = f.listFiles(FileFilters.ioFileFilterFor(filter));
		return files != null ? Stream.of(files)
									 .map(file -> LocalFileProperties.fromOrNull(file))
									 .filter(Objects::nonNull)
									 .toList()
							 : Lists.<FileProperties>newArrayList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _handleFolderStart(final FileProperties folderProps,final int depth,
									  final Stream.Builder<FileProperties> results) throws IOException {
		if (results != null) results.add(folderProps);
	}
	@Override
	protected void _handleFile(final FileProperties fileProps,final int depth,
							   final Stream.Builder<FileProperties> results) throws IOException {
		results.add(fileProps);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 * @param aDirectory the dir.
	 * @throws IOException
	 */
	private static void _validateDirectory(final File aDirectory) throws IOException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}
}
