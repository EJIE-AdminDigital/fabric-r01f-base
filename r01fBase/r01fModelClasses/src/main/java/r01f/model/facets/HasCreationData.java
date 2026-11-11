package r01f.model.facets;


import java.time.Instant;

import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

public interface HasCreationData {
	/**
	 * Gets the SecurityPrincipalId of the creator
	 * @return
	 */
	public SecurityPrincipalID getCreatorSecurityPrincipalId();
	/**
	 * Sets the SecurityPrincipalId of the creator (it comes from the user context)
	 * @param SecurityPrincipalId
	 */
	public void setCreatorSecurityPrincipalId(final SecurityPrincipalID SecurityPrincipalId);
	/**
	 * Gets the [security principal] oid of the creator
	 * @return
	 */
	public SecurityPrincipalOID getCreatorSecurityPrincipalOid();
	/**
	 * Sets the [security principal] oid of the creator (it comes from the user context)
	 * @param securityPrincipalOid
	 */
	public void setCreatorSecurityPrincipalOid(final SecurityPrincipalOID securityPrincipalOid);
	/**
	 * @return create operation timestamp
	 */
	public Instant getCreatedAt();
	/**
	 * Sets the create operation timestamp
	 * @param newTS the new timestamp
	 */
	public void setCreatedAt(Instant newTS);
}
