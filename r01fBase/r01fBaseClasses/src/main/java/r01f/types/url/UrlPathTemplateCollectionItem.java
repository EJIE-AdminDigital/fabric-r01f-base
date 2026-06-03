package r01f.types.url;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionItemBase;
import r01f.types.tag.TagList;

@MarshallType(as="urlPathTemplateCollectionItem")
@Accessors(prefix="_")
public class UrlPathTemplateCollectionItem
	 extends MetaDatedValuesCollectionItemBase<UrlPathTemplate,
	 										   UrlPathTemplateCollectionItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathTemplateCollectionItem() {
		// default no-args constructor
	}
	public UrlPathTemplateCollectionItem(final UrlPathTemplate urlPathTemplate) {
		super(urlPathTemplate);
	}
	public UrlPathTemplateCollectionItem(final UrlPathTemplate urlPathTemplate,
								 		 final CollectionItemID id) {
		super(urlPathTemplate,
			  id);
	}
	public UrlPathTemplateCollectionItem(final UrlPathTemplate urlPathTemplate,
							     		 final CollectionItemID id,final TagList<String> tags) {
		super(urlPathTemplate,
			  id,tags);
	}
	public static UrlPathTemplateCollectionItem createFor(final UrlPathTemplate urlPathTemplate) {
		return new UrlPathTemplateCollectionItem(urlPathTemplate);
	}
}
