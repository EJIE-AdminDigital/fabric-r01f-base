package r01f.model;

import r01f.facets.HasOID;
import r01f.oids.PersistableObjectOID;




/**
 * Interface for model objects that can be persisted in some kind of storage
 */
public interface PersistableModelObject<O extends PersistableObjectOID>
		 extends PersistableObject,
		 		 HasOID<O> {	// all persistable model objects MUST have an OID{
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
