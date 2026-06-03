package r01f.file.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.patterns.FactoryFrom;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates an {@link Iterable} of a folder contents
 * Usage: 
 * <pre class='brush:java>
 * 		public class MyFolderWalkerIterable<Path>
 * 			 extends FolderWalkerIterable<Path> {
 *			// use the r01f api
 *			private final FileStoreAPI _fsApi = new LocalFileStoreAPI();
 *			private final FileStoreFilerAPI _fsFilerApi = new LocalFileStoreFilerAPI();
 *
 *			public MyFolderWalkerIterable(final Path startingPath) {
 *				super(startingPath,
 					  fileProperties -> fileProperties.getPath());	
 *			}
 *
 *		    @Override
 *		    protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
 *		    	return fsApi.getFileProperties(folderPath);
 *		    }
 *		    @Override
 *		    protected Collection<FileProperties> listFolderContents(final Path folderPath, 
 *		    														final FileFilter filter) throws IOException {
 *		    	FileProperties[] contents = fsFilerApi.listFolderContents(folderPath,
 *		    										  					  filter,
 *	 	    										  					  false);	// not recursive
 *		    	return CollectionUtils.hasData(contents) ? Lists.newArrayList(contents)
 *		    											 : null;
 *		    }
 *		    public Collection<Path> getFiles() throws IOException {
 *		    	return Streams.stream(this)
 * 		    				  .collect(Collectors.toList());
 *		    }
 *		}
 * </pre>
 * @param <T>
 */
public abstract class FolderWalkerIterable<T> 
  		   implements Iterable<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The starting path
	 */
	protected final Path _startingPath;
	/**
	 * The file filter to use to filter files and directories.
	 */
	protected final FileFilter _filter;
	/**
	 * The limit on the folder depth to walk.
	 */
	protected final int _depthLimit;
	/**
	 * The item factory
	 */
	protected final FactoryFrom<FileProperties,T> _itemFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Deque<FilePropertiesAndDepth> _unconsumedFiles = new LinkedList<>();
	private final Deque<FilePropertiesAndDepth> _unconsumedFolders = new LinkedList<>();
	
	private volatile boolean _cancelled = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	protected FolderWalkerIterable(final Path startingPath,
								   final FactoryFrom<FileProperties,T> itemFactory) {
		this(startingPath,
			 null,	// no filter
			 -1,	// no depth limit
			 itemFactory);
	}
	protected FolderWalkerIterable(final Path startingPath,
								   final FileFilter filter,
								   final FactoryFrom<FileProperties,T> itemFactory) {
		this(startingPath,
			 filter,
			 -1,	// no depth limit
			 itemFactory);
	}
	protected FolderWalkerIterable(final Path startingPath,
								   final FileFilter filter,
						   		   final int depthLimit,
						   		   final FactoryFrom<FileProperties,T> itemFactory) {
		_startingPath = startingPath;
		_filter = filter;
		_depthLimit = depthLimit;
		_itemFactory = itemFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSTRACT METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a folder's properties
	 * @param folderPath
	 * @return
	 */
	protected abstract FileProperties getFolderProperties(final Path folderPath) throws IOException;
	/**
	 * Returns a folder's contents
	 * @param folderPath
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	protected abstract Collection<FileProperties> listFolderContents(final Path folderPath,
																	 final FileFilter filter) throws IOException;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Stream<T> asStream() {
		return Streams.stream(this);
	}
	@Override
	public Iterator<T> iterator() {
		if (_startingPath == null) throw new NullPointerException("Starting path is null");
		
		// [1] - Setup
		try {
			FileProperties startFolderProps = this.getFolderProperties(_startingPath);
			// start
			_handleStart(startFolderProps);
			// add the starting folder to the pending folders
			if (startFolderProps.isFolder()) {
				_unconsumedFolders.push(new FilePropertiesAndDepth(startFolderProps,0));
			} else if (startFolderProps.isFile()) {
				_unconsumedFiles.push(new FilePropertiesAndDepth(startFolderProps,0));
			}
		} catch (IOException ioEx) {
			Throwables.throwUnchecked(ioEx);
		}
		// [2] - Build the iterator
		return new Iterator<T>() {
						@Override
						public boolean hasNext() {	
							if (FolderWalkerIterable.this.isCancelled()) return false;
							
							// if no file is available, try to walk
							if (CollectionUtils.isNullOrEmpty(_unconsumedFiles)) {
								try {
									_tryWalk();
								} catch (IOException ioEx) {
									Throwables.throwUnchecked(ioEx);
								}
							}
							// maybe it's cancelled
							if (FolderWalkerIterable.this.isCancelled()) throw new IllegalStateException("The folder walking has been cancelled!");
							
							// test
							return CollectionUtils.hasData(_unconsumedFiles);
						}
						@Override
						public T next() {
							if (CollectionUtils.isNullOrEmpty(_unconsumedFiles)) throw new IllegalStateException("NO items to be consumed!");
							
							// maybe it's cancelled
							if (FolderWalkerIterable.this.isCancelled()) throw new IllegalStateException("The folder walking has been cancelled!");

							// pop next
							FilePropertiesAndDepth next = _unconsumedFiles.pop();
							_handleFile(next.getFileProperties(),next.getDepth());
							
							// return
							T outItem = _itemFactory.from(next.getFileProperties());
							return outItem;
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _tryWalk() throws IOException {
		if (this.isCancelled()) return;
		if (CollectionUtils.isNullOrEmpty(_unconsumedFolders)) return;	// nothing to do
		do {
			FilePropertiesAndDepth nextFolder = _unconsumedFolders.pop();
			
			_walk(nextFolder.getFileProperties(),nextFolder.getDepth());
		} while (CollectionUtils.isNullOrEmpty(_unconsumedFiles)
			  && CollectionUtils.hasData(_unconsumedFolders)); 
	}
	/**
	 * Main recursive method to examine the folder hierarchy.
	 * @param folderProps  the folder to examine, not null
	 * @param depth  the folder level (starting folder = 0)
	 * @param results  the collection of result objects, may be updated
	 * @throws IOException if an I/O Error occurs
	 */
	private void _walk(final FileProperties folderProps,final int depth) throws IOException {
		int childDepth = depth + 1;
		if (_depthLimit < 0 || childDepth <= _depthLimit) {
			Collection<FileProperties> folderChildren = this.listFolderContents(folderProps.getPath(),
														   	 		   	   		_filter);	// not recursive
			// give an opportunity to filter folder contents
			folderChildren = _filterFolderContents(folderProps,depth,
												   folderChildren);
			if (CollectionUtils.isNullOrEmpty(folderChildren)) {
				_handleRestricted(folderProps,childDepth);
			} else {
				for (FileProperties folderChild : folderChildren) {
					if (folderChild.isFolder()
					 && _hasToHandleFolder(folderChild,childDepth)) {
						_unconsumedFolders.push(new FilePropertiesAndDepth(folderChild,childDepth));
					} else if (folderChild.isFile()
							&& _hasToHandleFile(folderChild,childDepth)) { 
						_unconsumedFiles.push(new FilePropertiesAndDepth(folderChild,childDepth));
					}
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Overridable callback method invoked at the start of processing.
	 * @param startFolderProps  the folder to start from
	 */
	protected void _handleStart(final FileProperties startFolderProps) {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked to determine if a folder should be processed.
	 * <p>
	 * This method returns a boolean to indicate if the folder should be examined or not.
	 * If you return false, the entire folder and any subdirectories will be skipped.
	 * Note that this functionality is in addition to the filtering by file filter.
	 * <p>
	 * @param folderProps  the current folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @return true to process this folder, false to skip this folder
	 */
	@SuppressWarnings("static-method")
	protected boolean _hasToHandleFolder(final FileProperties folderProps,final int depth) {
		// do nothing - overridable by subclass
		return true;  // process folder
	}
	/**
	 * Overridable callback method invoked to determine if a file should be processed.
	 * <p>
	 * This method returns a boolean to indicate if the file should be examined or not.
	 * Note that this functionality is in addition to the filtering by file filter.
	 * <p>
	 * @param fileProps the current file being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @return true to process this file, false to skip this file
	 */
	@SuppressWarnings("static-method")
	protected boolean _hasToHandleFile(final FileProperties fileProps,final int depth) {
		// do nothing - overridable by subclass
		return true;  // process folder
	}
	/**
	 * Overridable callback method invoked with the contents of each folder.
	 * @param folderProps  the current folder being processed
	 * @param depth  the current folder level (starting folder = 0)
	 * @param files the files (possibly filtered) in the folder
	 * @return the filtered list of files
	 * @throws IOException if an I/O Error occurs
	 * @since 2.0
	 */
	@SuppressWarnings("static-method")
	protected Collection<FileProperties> _filterFolderContents(final FileProperties folderProps,final int depth,
											  		 		   final Collection<FileProperties> files) throws IOException {
		return files;	// returns the files unchanged
	}
	/**
	 * Overridable callback method invoked for each (non-folder) file.
	 * @param fileProps  the current file being processed
	 * @param depth  the current folder level (starting folder = 0)
	 */
	protected void _handleFile(final FileProperties fileProps,final int depth) {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked for each restricted folder.
	 * @param folderProps  the restricted folder
	 * @param depth  the current folder level (starting folder = 0)
	 */
	protected void _handleRestricted(final FileProperties folderProps,final int depth)  {
		// do nothing - overridable by subclass
	}
	/**
	 * Overridable callback method invoked at the end of processing.
	 */
	protected void _handleEnd() {
		// do nothing - overridable by subclass
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CANCEL
/////////////////////////////////////////////////////////////////////////////////////////
	public void cancel() {
		_cancelled = true;
	}
	public boolean isCancelled() {
		return _cancelled;
	}
	public boolean isNOTCancelled() {
		return _cancelled == false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private class FilePropertiesAndDepth {
		@Getter private final FileProperties _fileProperties;
		@Getter private final int _depth;
	}
}
