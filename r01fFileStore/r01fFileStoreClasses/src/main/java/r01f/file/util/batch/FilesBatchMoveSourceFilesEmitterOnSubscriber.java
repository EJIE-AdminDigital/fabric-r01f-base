package r01f.file.util.batch;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import r01f.file.FileProperties;
import r01f.file.util.FolderWalker;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;

/**
 * Creates an {@link FlowableOnSubscribe} that that emmits a {@link Path}
 * for every file in the folder structure
 */
     class FilesBatchMoveSourceFilesEmitterOnSubscriber
   extends FolderWalker<Path> 
implements FlowableOnSubscribe<Path> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreFilerAPI _filerApi;
	private final Path _startFolderPath;
	
	private FlowableEmitter<Path> _emitter; 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FilesBatchMoveSourceFilesEmitterOnSubscriber(final FileStoreFilerAPI filerApi,
											 		    final Path path) {
		super();
		_filerApi = filerApi;
		_startFolderPath = path;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FlowableOnSubscribe
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void subscribe(final FlowableEmitter<Path> emitter) throws Exception {
		_emitter = emitter;
		try {
			this.walk(_startFolderPath,
					  // a collection that just DOES NOT STORE ANYTHING
				      // new FakeCollection<Path>()
					  new Stream.Builder<Path>() {
								@Override
								public void accept(final Path path) {
									// do nothing
								}
								@Override
								public Stream<Path> build() {
									return Stream.empty();
								}
					  });
		} catch (IOException ioEx) {
			emitter.onError(ioEx);
		}
		_emitter.onComplete();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DirectoryWalker
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    protected void _handleFolderStart(final FileProperties folderProps,final int depth,
    							      final Stream.Builder<Path> results) throws IOException {
//		_emitter.onNext(folderProps.getPath());
	}
	@Override
    protected void _handleFile(final FileProperties fileProps,final int depth,
    						   final Stream.Builder<Path> results) throws IOException {
		// emit a new file path
		_emitter.onNext(fileProps.getPath());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
		return _filerApi.getFolderProperties(folderPath);
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final Path folderPath,
															final FileFilter filter) throws IOException {
		FileProperties[] childFiles = _filerApi.listFolderContents(folderPath,
																   filter, 
																   false);		// not recursive
		return childFiles != null ? Lists.newArrayList(childFiles)
								  : Lists.newArrayList();
	}
}
