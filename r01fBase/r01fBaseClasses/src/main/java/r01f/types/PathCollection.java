package r01f.types;

import java.util.Collection;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.MetaDatedValuesCollectionBase;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="pathCollection")
public class PathCollection 
	 extends MetaDatedValuesCollectionBase<Path,PathCollectionItem,
	 								   PathCollection> {

	private static final long serialVersionUID = 8640918754419234685L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PathCollection() {
		super(PathCollectionItem::new);
	}
	// required by the marshaller
	public PathCollection(final int length) {
		super(PathCollectionItem::new,
			  length);
	}
	public PathCollection(final PathCollectionItem... items) {
		super(PathCollectionItem::new,
			  items);
	}
	public PathCollection(final Collection<PathCollectionItem> items) {
		super(PathCollectionItem::new,
			  items);
	}
	public static PathCollection emptyPathCollection() {
		return new PathCollection();
	}
	public static PathCollection withItems(final Collection<PathCollectionItem> items) {
		return new PathCollection(items);
	}
	public static PathCollection withPaths(final Collection<Path> paths) {
		if (CollectionUtils.isNullOrEmpty(paths)) return PathCollection.emptyPathCollection();
		return PathCollection.withItems(paths.stream()
									  		 .map(PathCollectionItem::new)
									  		 .toList());
	}
}
