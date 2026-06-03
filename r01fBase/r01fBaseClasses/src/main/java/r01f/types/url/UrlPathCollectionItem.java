package r01f.types.url;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionItemBase;
import r01f.types.tag.TagList;

@MarshallType(as="urlPathCollectionItem")
@Accessors(prefix="_")
public class UrlPathCollectionItem
	 extends MetaDatedValuesCollectionItemBase<UrlPath,
	 										   UrlPathCollectionItem>
  implements HasUrlPath {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathCollectionItem() {
		// default no-args constructor
	}
	public UrlPathCollectionItem(final UrlPath urlPath) {
		super(urlPath);
	}
	public UrlPathCollectionItem(final UrlPath urlPath,
								 final CollectionItemID id) {
		super(urlPath,
			  id);
	}
	public UrlPathCollectionItem(final UrlPath urlPath,
							     final CollectionItemID id,final TagList<String> tags) {
		super(urlPath,
			  id,tags);
	}
	public static UrlPathCollectionItem createFor(final UrlPath urlPath) {
		return new UrlPathCollectionItem(urlPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public UrlPath getUrlPath() {
		return _value;
	}
	@Override
	public void setUrlPath(final UrlPath urlPath) {
		_value = urlPath;
	}
}
