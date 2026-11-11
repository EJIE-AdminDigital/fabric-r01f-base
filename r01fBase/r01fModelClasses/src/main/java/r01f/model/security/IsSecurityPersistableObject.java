package r01f.model.security;

import r01f.model.PersistableModelObject;
import r01f.oids.SecurityOIDs.IsSecurityPersistableObjectOID;

/**
 * Interface for every P12 Model Object
 * @param <O>
 * @param <ID>
 */
public interface IsSecurityPersistableObject<O extends IsSecurityPersistableObjectOID>
		 extends PersistableModelObject<O>,			// is persistable
				 IsSecurityObject {					// it's a security object

}
