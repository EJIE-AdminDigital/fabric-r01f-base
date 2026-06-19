package r01f.model.facets;


import java.time.Instant;

import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;

public interface HasCreationData {
	/**
	 * Gets the user code of the creator
	 * @return
	 */
	public LoginID getCreatorUserCode();
	/**
	 * Sets the user code of the creator (it comes from the user context)
	 * @param userCode
	 */
	public void setCreatorUserCode(final LoginID userCode);
	/**
	 * Gets the user oid of the creator
	 * @return
	 */
	public UserOID getCreatorUserOid();
	/**
	 * Sets the user oid of the creator (it comes from the user context)
	 * @param userOid
	 */
	public void setCreatorUserOid(final UserOID userOid);
	/**
	 * @return create operation timestamp
	 */
	public Instant getCreateTimeStamp();
	/**
	 * Sets the create operation timestamp
	 * @param newTS the new timestamp
	 */
	public void setCreateTimeStamp(Instant newTS);
}
