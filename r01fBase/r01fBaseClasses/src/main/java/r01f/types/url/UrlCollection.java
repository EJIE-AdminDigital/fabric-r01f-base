package r01f.types.url;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.collections.CollectionItemID;
import r01f.types.collections.MetaDatedValuesCollectionBase;
import r01f.types.url.web.WebLinkCollection;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of urls
 * @See {@link WebLinkCollection} and {@link UrlCollectionItem}
 *
 * If using {@link UrlCollectionItem}
 * <pre class='brush:java'>
 * 		UrlCollection<UrlCollectionItem> col = UrlCollection.create();
 * 		col.addUrls(UrlCollectionItem.createFor(Url.from("www.euskadi.eus/es",Language.SPANISH),
 * 				    UrlCollectionItem.createFor(Url.from("www.eusakdi.eus/eu",Language.BASQUE));
 * </pre>
 */
@Slf4j
@ConvertToDirtyStateTrackable
@MarshallType(as="urlCollection")
public class UrlCollection
	 extends MetaDatedValuesCollectionBase<Url,UrlCollectionItem,
	 							   	   	   UrlCollection> {

	private static final long serialVersionUID = 8522332353669168499L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollection() {
		super(UrlCollectionItem::new);
	}
	// required by the marshaller
	public UrlCollection(final int length) {
		super(UrlCollectionItem::new,
			  length);
	}
	public UrlCollection(final Url... urls) {
		super(UrlCollectionItem::new);
		if (CollectionUtils.hasData(urls)) this.addValues(urls);
	}
	public UrlCollection(final Collection<Url> urls) {
		super(UrlCollectionItem::new);
		if (CollectionUtils.hasData(urls)) this.addValues(urls);
	}
	public static UrlCollection create() {
		return new UrlCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollection addUrls(final Url... urls) {
		return this.addValues(urls);
	}
	public UrlCollection addUrls(final Collection<Url> urls) {
		return this.addValues(urls);
	}
	public boolean removeUrl(final Url url) {
		Collection<UrlCollectionItem> removedUrls = this.removeItemsWithValueMatching(theUrl -> url.equals(theUrl));
		return CollectionUtils.hasData(removedUrls);
	}
	/**
	 * Removes all urls with the given language and returns the replaced urls
	 * @param urls the new urls
	 * @return the replaced urls
	 */
	public Collection<Url> replaceUrlsInLang(final Language lang,
										     final Collection<Url> urls) {
		// remove the urls in the given language
		Collection<UrlCollectionItem> removedItems = this.removeItemMatching(item -> item.getLanguage().is(lang));
		
		// add the new urls
		if (CollectionUtils.hasData(urls)) {
			this.addItems(urls.stream()
							  .map(url -> UrlCollectionItem.createFor(url))
							  .collect(Collectors.toList()));
		}
		// return the removed urls
		return CollectionUtils.hasData(removedItems) ? removedItems.stream()
														  		   .map(item -> item.getUrl())
														  		   .collect(Collectors.toList())
										 			 : Collections.emptyList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public Url getUrlWith(final Language lang) {
		return this.getUrlWithWith(CollectionItemID.forLang(lang),	// the id is the language
								 lang);
	}
	public Url getUrlWithWith(final CollectionItemID id,final Language lang) {
		Collection<Url> urls = this.getValuesOfItemsMatching(item -> item.getId() != null && item.getId().equals(id) 
																  && item.getLanguage() != null && item.getLanguage().is(lang));
		return CollectionUtils.hasData(urls) ? urls.iterator().next()
										   	 : null;
	}
	public Collection<Url> getUrlsWith(final CollectionItemID id) {
		return this.getValueOfItemsWithId(id);
	}
	public Collection<Url> getUrlsWith(final Language lang) {
		return this.getValuesOfItemsMatching(item -> item.getLanguage() != null && item.getLanguage().is(lang));
	}
	public Collection<Url> getUrlsTaggedWith(final String tag) {
		return this.getValuesOfItemsTaggedWith(tag);
	}
	public Collection<Url> getUrlsTaggedWith(final Language lang,
										 	 final String tag) {
		return this.getValuesOfItemsMatching(item -> item.getLanguage() != null && item.getLanguage().is(lang)
											  	  && item.getTags() != null && item.asTaggeable().isTaggedWith(tag));
	}
	public Collection<Url> getUrlsMatching(final Predicate<Url> pred) {
		return this.getValuesOfItemsMatching(item ->  pred.test(item.getValue()));
	}
}