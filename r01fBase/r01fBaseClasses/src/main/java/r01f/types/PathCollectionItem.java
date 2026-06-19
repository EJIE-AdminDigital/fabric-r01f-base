package r01f.types;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionItemBase;
import r01f.types.tag.TagList;

@MarshallType(as="pathCollectionItem")
public class PathCollectionItem 
	 extends MetaDatedValuesCollectionItemBase<Path,
	 										   PathCollectionItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollectionItem() {
		// default no-args constructor
	}
	public PathCollectionItem(final Path path) {
		super(path);
	}
	public PathCollectionItem(final Path path,
							  final CollectionItemID id) {
		super(path,id);
	}
	public PathCollectionItem(final Path path,
							  final TagList<String> tags) {
		super(path,
			  null,tags);
	}
	public PathCollectionItem(final Path path,
							  final CollectionItemID id,final TagList<String> tags) {
		super(path,
			  id,tags);
	}
}
