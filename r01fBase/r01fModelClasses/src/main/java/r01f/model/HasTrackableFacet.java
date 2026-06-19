package r01f.model;


import java.time.Instant;

import r01f.model.facets.ModelObjectFacet;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;

public interface HasTrackableFacet
		 extends ModelObjectFacet {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasTrackableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTracking getTrackingInfo();

	public default void setTrackingInfo(final ModelObjectTracking trackingInfo) {
		throw new UnsupportedOperationException("override if needed!");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public default Instant getCreateDate() {
		return this.getTrackingInfo()
				   .getCreateDate();
	}
	public default Instant getLastUpdateDate() {
		return this.getTrackingInfo()
				   .getLastUpdateDate();
	}
	public default UserOID getCreatorUserOid() {
		return this.getTrackingInfo()
				   .getCreatorUserOid();
	}
	public default LoginID getCreatorUserCode() {
		return this.getTrackingInfo()
				   .getCreatorUserCode();
	}
	public default UserOID getLastUpdatorUserOid() {
		return this.getTrackingInfo()
				   .getLastUpdatorUserOid();
	}
	public default LoginID getLastUpdatorUserCode() {
		return this.getTrackingInfo()
				   .getLastUpdatorUserCode();
	}
}
