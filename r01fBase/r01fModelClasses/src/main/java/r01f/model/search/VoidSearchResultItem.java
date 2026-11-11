package r01f.model.search;

import r01f.model.PersistableModelObject;
/*
 * {@link SearchResultItem} type to be used when annotating a NOT searchable model object
 * with {@link ModelObjectData}
 */
public interface VoidSearchResultItem 
		 extends SearchResultItemForModelObject<PersistableModelObject<?>> {
	/* marker interface */
}
