package r01f.model.persistence;

import r01f.guids.OID;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.PersistableModelObject;

public interface FindOnModelObjectResult<M extends PersistableModelObject<? extends OID>> 
   		 extends FindResult<M>,
   		 		 HasModelObjectTypeInfo<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link FindOnModelObjectOK}
	 */
	public default FindOnModelObjectOK<M> asFindOnModelObjectOK() {
		if (this instanceof FindOnModelObjectError<M>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindOnModelObjectOK.class);
		return (FindOnModelObjectOK<M>)this;
	}
	/**
	 * @return a {@link FindOnModelObjectError}
	 */
	public default FindOnModelObjectError<M> asFindOnModelObjectError() {
		if (this instanceof FindOnModelObjectOK<M>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindOnModelObjectError.class);
		return (FindOnModelObjectError<M>)this;
	}
}
