package r01f.model;

import r01f.facets.HasOID;
import r01f.oids.PersistableObjectOID;

public interface SummarizedPersistableObject<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
	     extends SummarizedModelObject<M>,
	     		 HasOID<O> {
	// a marker interface
}
