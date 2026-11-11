package r01f.model.security;

import r01f.facets.HasID;
import r01f.oids.SecurityIDs.IsSecurityPersistableObjectID;
import r01f.oids.SecurityOIDs.IsSecurityPersistableObjectOID;

/**
 * Interface for every  for P12PersistableObject with an ID and an OID
 * @param <O>
 * @param <ID>
 */
public interface IsSecurityPersistableObjectWithID<O extends IsSecurityPersistableObjectOID,
												   ID extends IsSecurityPersistableObjectID<O>>
		 extends IsSecurityPersistableObject<O>,
		 		 HasID<ID> {
	// nothing
}
