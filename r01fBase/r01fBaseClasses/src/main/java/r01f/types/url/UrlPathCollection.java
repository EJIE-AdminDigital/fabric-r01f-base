package r01f.types.url;

import java.util.Collection;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionBase;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of url paths
 * @See {@link UrlPathCollectionItem}
 *
 * <pre class='brush:java'>
 * 		UrlPathCollection<UrlPathCollectionItem> col = UrlPathCollection.create();
 * 		col.addUrlPaths(UrlPathCollectionItem.createFor(UrlPath.from("/api/v1/resources"),Language.SPANISH),
 * 				        UrlPathCollectionItem.createFor(UrlPath.from("/api/v1/baliabideak"),Language.BASQUE));
 * </pre>
 */
@Slf4j
@ConvertToDirtyStateTrackable
@MarshallType(as="urlPathCollection")
public class UrlPathCollection
	 extends MetaDatedValuesCollectionBase<UrlPath,UrlPathCollectionItem,
	 							   	   	   UrlPathCollection> {

	private static final long serialVersionUID = 8633443464780279610L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathCollection() {
		super(UrlPathCollectionItem::new);
	}
	// required by the marshaller
	public UrlPathCollection(final int length) {
		super(UrlPathCollectionItem::new,
			  length);
	}
	public UrlPathCollection(final UrlPath... urlPaths) {
		super(UrlPathCollectionItem::new);
		if (CollectionUtils.hasData(urlPaths)) this.addValues(urlPaths);
	}
	public UrlPathCollection(final Collection<UrlPath> urlPaths) {
		super(UrlPathCollectionItem::new);
		if (CollectionUtils.hasData(urlPaths)) this.addValues(urlPaths);
	}
	public static UrlPathCollection create() {
		return new UrlPathCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathCollection addUrlPaths(final UrlPath... urlPaths) {
		return this.addValues(urlPaths);
	}
	public UrlPathCollection addUrlPaths(final Collection<UrlPath> urlPaths) {
		return this.addValues(urlPaths);
	}
	public boolean removeUrlPath(final UrlPath urlPath) {
		Collection<UrlPathCollectionItem> removedUrlPaths = this.removeItemsWithValueMatching(theUrlPath -> urlPath.equals(theUrlPath));
		return CollectionUtils.hasData(removedUrlPaths);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<UrlPath> getUrlPathsWithId(final CollectionItemID id) {
		return this.getValueOfItemsWithId(id);
	}
	public Collection<UrlPath> getUrlPathsTaggedWith(final String tag) {
		return this.getValuesOfItemsTaggedWith(tag);
	}
	public Collection<UrlPath> getUrlPathsMatching(final Predicate<UrlPath> pred) {
		return this.getValuesOfItemsMatching(item ->  pred.test(item.getValue()));
	}
}
