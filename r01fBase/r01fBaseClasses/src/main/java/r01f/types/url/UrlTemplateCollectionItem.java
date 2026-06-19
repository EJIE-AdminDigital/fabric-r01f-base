package r01f.types.url;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionItemBase;
import r01f.types.tag.TagList;

@MarshallType(as="urlTemplateCollectionItem")
@Accessors(prefix="_")
public class UrlTemplateCollectionItem
	 extends MetaDatedValuesCollectionItemBase<UrlTemplate,
	 										   UrlTemplateCollectionItem>
  implements HasLanguage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter	protected Language _language;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlTemplateCollectionItem() {
		// default no-args constructor
	}
	public UrlTemplateCollectionItem(final UrlTemplate urlTemplate) {
		super(urlTemplate);
	}
	public UrlTemplateCollectionItem(final UrlTemplate urlTemplate,
							 final Language lang) {
		this(urlTemplate);
		_language = lang;
	}
	public UrlTemplateCollectionItem(final UrlTemplate urlTemplate,
							         final CollectionItemID id) {
		super(urlTemplate,
			  id);
	}
	public UrlTemplateCollectionItem(final UrlTemplate urlTemplate,
							 final CollectionItemID id,final Language lang) {
		this(urlTemplate,
			 id);
		_language = lang;
	}
	public UrlTemplateCollectionItem(final UrlTemplate urlTemplate,
							 final CollectionItemID id,final Language lang,final TagList<String> tags) {
		super(urlTemplate,
			  id,tags);
		_language = lang;
	}
	public static UrlTemplateCollectionItem createFor(final UrlTemplate urlTemplate) {
		return new UrlTemplateCollectionItem(urlTemplate);
	}
	public static UrlTemplateCollectionItem createFor(final UrlTemplate urlTemplate,final Language lang) {
		return new UrlTemplateCollectionItem(urlTemplate,lang);
	}
	public static UrlTemplateCollectionItem createFor(final UrlTemplate urlTemplate,
											  final CollectionItemID id,final Language lang) {
		return new UrlTemplateCollectionItem(urlTemplate,
									 		 id,lang);
	}
	public static UrlTemplateCollectionItem createFor(final UrlTemplate urlTemplate,
											  final CollectionItemID id,final Language lang,final TagList<String> tags) {
		return new UrlTemplateCollectionItem(urlTemplate,
									 		 id,lang,tags);
	}
}
