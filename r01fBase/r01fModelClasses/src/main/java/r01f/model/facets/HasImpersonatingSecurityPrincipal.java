package r01f.model.facets;

import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

public interface HasImpersonatingSecurityPrincipal
	     extends ModelObjectFacet {
	/**
	 * gets the impersonating security principal oid
	 * @return the id
	 */
	public SecurityPrincipalOID getImpersonatingSecurityPrincipalOid();
	/**
	 * Sets the security principal oid of the impersonating user (it comes from the user context)
	 * @param securityPrincipalOid the id
	 */
	public void setImpersonatingSecurityPrincipalOid(final SecurityPrincipalOID securityPrincipalOid);
}
