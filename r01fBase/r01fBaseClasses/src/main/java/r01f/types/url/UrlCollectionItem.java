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

@MarshallType(as="urlCollectionItem")
@Accessors(prefix="_")
public class UrlCollectionItem
	 extends MetaDatedValuesCollectionItemBase<Url,
	 										   UrlCollectionItem>
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
	public UrlCollectionItem() {
		// default no-args constructor
	}
	public UrlCollectionItem(final Url url) {
		super(url);
	}
	public UrlCollectionItem(final Url url,
							 final Language lang) {
		this(url);
		_language = lang;
	}
	public UrlCollectionItem(final Url url,
							 final CollectionItemID id) {
		super(url,
			  id);
	}
	public UrlCollectionItem(final Url url,
							 final CollectionItemID id,final Language lang) {
		this(url,
			 id);
		_language = lang;
	}
	public UrlCollectionItem(final Url url,
							 final CollectionItemID id,final Language lang,final TagList<String> tags) {
		super(url,
			  id,tags);
		_language = lang;
	}
	public static UrlCollectionItem createFor(final Url url) {
		return new UrlCollectionItem(url);
	}
	public static UrlCollectionItem createFor(final Url url,
											  final Language lang) {
		return new UrlCollectionItem(url,
									 CollectionItemID.forLang(lang),lang);
	}
	public static UrlCollectionItem createFor(final Url url,
											  final CollectionItemID id,final Language lang) {
		return new UrlCollectionItem(url,
									 id,lang);
	}
	public static UrlCollectionItem createFor(final Url url,
											  final CollectionItemID id,final Language lang,final TagList<String> tags) {
		return new UrlCollectionItem(url,
									 id,lang,tags);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAS URL
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Url getUrl() {
		return _value;
	}
	@Override
	public void setUrl(final Url url) {
		_value = url;
	}
}
