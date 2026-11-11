package r01f.model;


import java.time.Instant;

import r01f.model.facets.ModelObjectFacet;
import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

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
	public default Instant getCreatedAt() {
		return this.getTrackingInfo()
				   .getCreatedAt();
	}
	public default Instant getLastUpdatedAt() {
		return this.getTrackingInfo()
				   .getLastUpdatedAt();
	}
	public default SecurityPrincipalOID getCreatorSecurityPrincipalOid() {
		return this.getTrackingInfo()
				   .getCreatorSecurityPrincipalOid();
	}
	public default SecurityPrincipalID getCreatorSecurityPrincipalId() {
		return this.getTrackingInfo()
				   .getCreatorSecurityPrincipalId();
	}
	public default SecurityPrincipalOID getLastUpdaterSecurityPrincipalOid() {
		return this.getTrackingInfo()
				   .getLastUpdaterSecurityPrincipalOid();
	}
	public default SecurityPrincipalID getLastUpdaterLoginId() {
		return this.getTrackingInfo()
				   .getLastUpdaterSecurityPrincipalId();
	}
}
