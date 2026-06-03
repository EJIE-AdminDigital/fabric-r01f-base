package r01f.types.url.web;

import java.util.Collection;
import java.util.function.Predicate;

import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionBase;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of links
 */
@MarshallType(as="weblinks")
public class WebLinkCollection
	 extends MetaDatedValuesCollectionBase<WebLink,WebLinkCollectionItem,
	 									   WebLinkCollection> {

	private static final long serialVersionUID = -5210515759912563841L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkCollection() {
		super(WebLinkCollectionItem::new);
	}
	public WebLinkCollection(final WebLink... links) {
		super(WebLinkCollectionItem::new);
		if (CollectionUtils.hasData(links)) this.addValues(links);
	}
	public WebLinkCollection(final Collection<WebLink> links) {
		super(WebLinkCollectionItem::new);
		if (CollectionUtils.hasData(links)) this.addValues(links);
	}
	public WebLinkCollection(final WebLink other) {
		super(WebLinkCollectionItem::new);
		if (other != null) this.addValues(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLink getWebLinkWithId(final CollectionItemID id,
						  			final Language lang) {
		Collection<WebLink> urls = this.getValuesOfItemsMatching(item -> item.getId() != null && item.getId().equals(id) 
																  	  && item.getLanguage() != null && item.getLanguage().is(lang));
		return CollectionUtils.hasData(urls) ? urls.iterator().next()
										   	 : null;
	}
	public Collection<WebLink> getWebLinksWithId(final CollectionItemID id) {
		return this.getValueOfItemsWithId(id);
	}
	public Collection<WebLink> getWebLinksIn(final Language lang) {
		return this.getValuesOfItemsMatching(item -> item.getLanguage() != null && item.getLanguage().is(lang));
	}
	public Collection<WebLink> getWebLinksTaggedWith(final String tag) {
		return this.getValuesOfItemsTaggedWith(tag);
	}
	public Collection<WebLink> getWebLinksTaggedWith(final Language lang,
										 	 		 final String tag) {
		return this.getValuesOfItemsMatching(item -> item.getLanguage() != null && item.getLanguage().is(lang)
											  	  && item.getTags() != null && item.asTaggeable().isTaggedWith(tag));
	}
	public Collection<WebLink> getWebLinksMatching(final Predicate<WebLink> pred) {
		return this.getValuesOfItemsMatching(item ->  pred.test(item.getValue()));
	}
}
