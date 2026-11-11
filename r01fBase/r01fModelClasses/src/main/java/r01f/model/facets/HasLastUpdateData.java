package r01f.model.facets;


import java.time.Instant;

import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

public interface HasLastUpdateData {
	/**
	 * Gets the SecurityPrincipalId of the last updator
	 * @return
	 */
	public SecurityPrincipalID getLastUpdaterSecurityPrincipalId();
	/**
	 * Sets the SecurityPrincipalId of the last updater (it comes from the user context)
	 * @param SecurityPrincipalId
	 */
	public void setLastUpdaterSecurityPrincipalId(final SecurityPrincipalID SecurityPrincipalId);
	/**
	 * Gets the [security principal] oid of the last updater
	 * @return
	 */
	public SecurityPrincipalOID getLastUpdaterSecurityPrincipalOid();
	/**
	 * Sets the [security principal] oid of the last updater (it comes from the user context)
	 * @param securityPrincipalOid
	 */
	public void setLastUpdaterSecurityPrincipalOid(final SecurityPrincipalOID securityPrincipalOid);
	/**
	 * @return the last saving operation timestamp
	 */
	public Instant getLastUpdatedAt();
	/**
	 * Sets the last saving operation timestamp
	 * @param newTS the new timestamp
	 */
	public void setLastUpdatedAt(Instant newTS);
}
