package r01f.model.persistence;

import r01f.model.HasModelObjectTypeInfo;
import r01f.model.PersistableModelObject;
import r01f.oids.OID;

public interface CRUDOnModelObjectResult<M extends PersistableModelObject<? extends OID>>
		 extends CRUDResult<M>,
		 		 HasModelObjectTypeInfo<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link CRUDOnModelObjectOK} instance
	 */
	public default CRUDOnModelObjectOK<M> asCRUDOnModelObjectOK() {
		if (this instanceof CRUDOnModelObjectError<M>) throw new ClassCastException(this.getClass() + " is not an instance of " + CRUDOnModelObjectOK.class);
		return (CRUDOnModelObjectOK<M>)this;
	}
	/**
	 * @return a {@link CRUDOnModelObjectError} instance
	 */
	public default CRUDOnModelObjectError<M> asCRUDOnModelObjectError() {
		if (this instanceof CRUDOnModelObjectOK<M>) throw new ClassCastException(this.getClass() + " is not an instance of " + CRUDOnModelObjectError.class);
		return (CRUDOnModelObjectError<M>)this;
	}
}
