package r01f.types.url.web;


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
import r01f.types.url.HasUrl;
import r01f.types.url.Url;

@MarshallType(as="webLinkCollectionItem")
@Accessors(prefix="_")
public class WebLinkCollectionItem
	 extends MetaDatedValuesCollectionItemBase<WebLink,
	 										   WebLinkCollectionItem>
  implements HasUrl,
  			 HasLanguage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter	protected Language _language;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkCollectionItem() {
		// default no-args constructor
	}
	public WebLinkCollectionItem(final WebLink webLink) {
		super(webLink);
	}
	public WebLinkCollectionItem(final WebLink webLink,
							 	 final Language lang) {
		this(webLink);
		_language = lang;
	}
	public WebLinkCollectionItem(final WebLink webLink,
							 	 final CollectionItemID id) {
		super(webLink,
			  id);
	}
	public WebLinkCollectionItem(final WebLink webLink,
							 	 final CollectionItemID id,final Language lang) {
		this(webLink,
			 id);
		_language = lang;
	}
	public WebLinkCollectionItem(final WebLink webLink,
							 	 final CollectionItemID id,final Language lang,final TagList<String> tags) {
		super(webLink,
			  id,tags);
		_language = lang;
	}
	public static WebLinkCollectionItem createFor(final WebLink webLink) {
		return new WebLinkCollectionItem(webLink);
	}
	public static WebLinkCollectionItem createFor(final WebLink webLink,
											  	  final CollectionItemID id,final Language lang) {
		return new WebLinkCollectionItem(webLink,
									 	 id,lang);
	}
	public static WebLinkCollectionItem createFor(final WebLink webLink,
											  	  final CollectionItemID id,final Language lang,final TagList<String> tags) {
		return new WebLinkCollectionItem(webLink,
									 	 id,lang,tags);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAS URL
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Url getUrl() {
		return _value.getUrl();
	}
	@Override
	public void setUrl(final Url url) {
		_value.setUrl(url);
	}
}
