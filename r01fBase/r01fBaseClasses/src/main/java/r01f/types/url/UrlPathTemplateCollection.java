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
 * A collection of url path templates
 * @See {@link UrlPathTemplateCollectionItem}
 *
 * <pre class='brush:java'>
 * 		UrlPathTemplateCollection col = UrlPathTemplateCollection.create();
 * 		col.addUrlPathTemplates(UrlPathTemplateCollectionItem.createFor(UrlPathTemplate.from("/api/v1/resources"),Language.SPANISH),
 * 				                UrlPathTemplateCollectionItem.createFor(UrlPathTemplate.from("/api/v1/baliabideak"),Language.BASQUE));
 * </pre>
 */
@Slf4j
@ConvertToDirtyStateTrackable
@MarshallType(as="urlPathCollection")
public class UrlPathTemplateCollection
	 extends MetaDatedValuesCollectionBase<UrlPathTemplate,UrlPathTemplateCollectionItem,
	 							   	   	   UrlPathTemplateCollection> {

	private static final long serialVersionUID = 8633443464780279610L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathTemplateCollection() {
		super(UrlPathTemplateCollectionItem::new);
	}
	// required by the marshaller
	public UrlPathTemplateCollection(final int length) {
		super(UrlPathTemplateCollectionItem::new,
			  length);
	}
	public UrlPathTemplateCollection(final UrlPathTemplate... urlPathTemplates) {
		super(UrlPathTemplateCollectionItem::new);
		if (CollectionUtils.hasData(urlPathTemplates)) this.addValues(urlPathTemplates);
	}
	public UrlPathTemplateCollection(final Collection<UrlPathTemplate> urlPathTemplates) {
		super(UrlPathTemplateCollectionItem::new);
		if (CollectionUtils.hasData(urlPathTemplates)) this.addValues(urlPathTemplates);
	}
	public static UrlPathTemplateCollection create() {
		return new UrlPathTemplateCollection();
	}
	public static UrlPathTemplateCollection create(final UrlPathTemplate... urlPathTemplates) {
		return new UrlPathTemplateCollection(urlPathTemplates);
	}
	public static UrlPathTemplateCollection create(final Collection<UrlPathTemplate> urlPathTemplates) {
		return new UrlPathTemplateCollection(urlPathTemplates);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPathTemplateCollection addUrlPathTemplates(final UrlPathTemplate... urlPathTemplates) {
		return this.addValues(urlPathTemplates);
	}
	public UrlPathTemplateCollection addUrlPathTemplates(final Collection<UrlPathTemplate> urlPathTemplates) {
		return this.addValues(urlPathTemplates);
	}
	public boolean removeUrlPathTemplate(final UrlPathTemplate urlPathTemplate) {
		Collection<UrlPathTemplateCollectionItem> removedUrlPathTemplates = this.removeItemsWithValueMatching(theUrlPathTemplate -> urlPathTemplate.equals(theUrlPathTemplate));
		return CollectionUtils.hasData(removedUrlPathTemplates);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET Urls by tag
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<UrlPathTemplate> getUrlPathTemplatesWithId(final CollectionItemID id) {
		return this.getValueOfItemsWithId(id);
	}
	public Collection<UrlPathTemplate> getUrlPathTemplatesTaggedWith(final String tag) {
		return this.getValuesOfItemsTaggedWith(tag);
	}
	public Collection<UrlPathTemplate> getUrlPathTemplatesMatching(final Predicate<UrlPathTemplate> pred) {
		return this.getValuesOfItemsMatching(item ->  pred.test(item.getValue()));
	}
}
