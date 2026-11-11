package r01f.model;

import r01f.oids.OID;

/**
 * Types that contains a {@link PersistableModelObject} reference
 * @param <M>
 */
public interface HasPersistableModelObject<M extends PersistableModelObject<? extends OID>> {
	public M getModelObject();
	public void setModelObject(final M modelObj);
}
