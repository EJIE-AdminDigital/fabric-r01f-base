package r01f.file.util.batch;

import java.io.IOException;
import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;
import r01f.file.FileStoreExecutedActionData;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

public class FilesBatch {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FilesBatchMove _move;
	private final FilesBatchDelete _delete;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FilesBatch(final FilesBatchMove move,
					  final FilesBatchDelete delete) {
		_move = move;
		_delete = delete;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Flowable<FileStoreExecutedActionData> flowableMove(final Collection<Path> newOrUpdatedRelPaths) throws IOException {
		return CollectionUtils.hasData(newOrUpdatedRelPaths)
						? _move.flowableMove(newOrUpdatedRelPaths)
						: Flowable.empty();
	}
	public Flowable<FileStoreExecutedActionData> flowableDelete(final Collection<Path> deletedRelPaths) throws IOException {
		return CollectionUtils.hasData(deletedRelPaths)
						? _delete.flowableDelete(deletedRelPaths)
						: Flowable.empty();
	}
	public Flowable<FileStoreExecutedActionData> flowableMoveAndDelete(final Collection<Path> newOrUpdatedRelPaths,
																	   final Collection<Path> deletedRelPaths) throws IOException {
		// i) new or updated
		Flowable<FileStoreExecutedActionData> newOrUpdatedFlow = CollectionUtils.hasData(newOrUpdatedRelPaths)
																	? _move.flowableMove(newOrUpdatedRelPaths)
																	: Flowable.empty();
		// ii) deleted
		Flowable<FileStoreExecutedActionData> deletedFlow = CollectionUtils.hasData(deletedRelPaths)
																	? _delete.flowableDelete(deletedRelPaths)
																	: Flowable.empty();
		// iii) merge both
		return newOrUpdatedFlow.mergeWith(deletedFlow);		// return Flowable.mergeArray(newOrUpdatedFlow,
							  								//						     deletedFlow);
	}
}
