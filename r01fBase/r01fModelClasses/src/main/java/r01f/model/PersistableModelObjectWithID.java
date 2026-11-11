package r01f.model;

import r01f.facets.HasID;
import r01f.oids.PersistableObjectID;
import r01f.oids.PersistableObjectOID;

public interface PersistableModelObjectWithID<O extends PersistableObjectOID,I extends PersistableObjectID<O>> 
   	     extends PersistableModelObject<O>,
   	     		 HasID<I>{

}
