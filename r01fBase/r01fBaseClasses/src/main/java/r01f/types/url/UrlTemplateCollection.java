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
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of url templates
 * @See {@link UrlTemplateCollectionItem}
 *
 * <pre class='brush:java'>
 * 		UrlTemplateCollection col = UrlTemplateCollection.create();
 * 		col.addUrlTemplates(UrlTemplateCollectionItem.createFor(UrlTemplate.from("www.euskadi.eus/es/{id}"),Language.SPANISH),
 * 				   	        UrlTemplateCollectionItem.createFor(UrlTemplate.from("www.eusakdi.eus/eu/{id}"),Language.BASQUE));
 * </pre>
 */
@Slf4j
@ConvertToDirtyStateTrackable
@MarshallType(as="urlTemplateCollection")
public class UrlTemplateCollection
	 extends MetaDatedValuesCollectionBase<UrlTemplate,UrlTemplateCollectionItem,
	 							   	   	   UrlTemplateCollection> {

	private static final long serialVersionUID = 4784400261189493922L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlTemplateCollection() {
		super(UrlTemplateCollectionItem::new);
	}
	// required by the marshaller
	public UrlTemplateCollection(final int length) {
		super(UrlTemplateCollectionItem::new,
			  length);
	}
	public UrlTemplateCollection(final UrlTemplate... urlTemplates) {
		super(UrlTemplateCollectionItem::new);
		if (CollectionUtils.hasData(urlTemplates)) this.addValues(urlTemplates);
	}
	public UrlTemplateCollection(final Collection<UrlTemplate> urlTemplates) {
		super(UrlTemplateCollectionItem::new);
		if (CollectionUtils.hasData(urlTemplates)) this.addValues(urlTemplates);
	}
	public static UrlTemplateCollection create() {
		return new UrlTemplateCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlTemplateCollection addUrlTemplates(final UrlTemplate... urlTemplates) {
		return this.addValues(urlTemplates);
	}
	public UrlTemplateCollection addUrlTemplates(final Collection<UrlTemplate> urlTemplates) {
		return this.addValues(urlTemplates);
	}
	public boolean removeUrlTemplate(final UrlTemplate urlTemplate) {
		Collection<UrlTemplateCollectionItem> removedUrlTemplates = this.removeItemsWithValueMatching(theUrlTemplate -> urlTemplate.equals(theUrlTemplate));
		return CollectionUtils.hasData(removedUrlTemplates);
	}
	/**
	 * Removes all url templates with the given language and returns the replaced ones
	 * @param urlTemplates the new url templates
	 * @return the replaced url templates
	 */
	public Collection<UrlTemplate> replaceUrlTemplatesInLang(final Language lang,
										     			   final Collection<UrlTemplate> urlTemplates) {
		// remove the url templates in the given language
		Collection<UrlTemplateCollectionItem> removedItems = this.removeItemMatching(item -> item.getLanguage().is(lang));
		
		// add the new url templates
		if (CollectionUtils.hasData(urlTemplates)) {
			this.addItems(urlTemplates.stream()
							  		  .map(urlTemplate -> UrlTemplateCollectionItem.createFor(urlTemplate))
							  		  .collect(Collectors.toList()));
		}
		// return the removed url templates
		return CollectionUtils.hasData(removedItems) ? removedItems.stream()
														  		   .map(item -> item.getValue())
														  		   .collect(Collectors.toList())
										 			 : Collections.emptyList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlTemplate getUrlTemplateWithId(final CollectionItemID id,
						  				    final Language lang) {
		Collection<UrlTemplate> urlTemplates = this.getValuesOfItemsMatching(item -> item.getId() != null && item.getId().equals(id) 
 																  				  && item.getLanguage() != null && item.getLanguage().is(lang));
		return CollectionUtils.hasData(urlTemplates) ? urlTemplates.iterator().next()
											 		 : null;
	}
	public Collection<UrlTemplate> getUrlTemplatesWithId(final CollectionItemID id) {
		return this.getValueOfItemsWithId(id);
	}
	public Collection<UrlTemplate> getUrlTemplatesTaggedWith(final String tag) {
		return this.getValuesOfItemsTaggedWith(tag);
	}
	public Collection<UrlTemplate> getUrlTemplatesMatching(final Predicate<UrlTemplate> pred) {
		return this.getValuesOfItemsMatching(item ->  pred.test(item.getValue()));
	}
}