package r01f.model.builders.facets;


import java.time.Instant;

import r01f.facets.builders.FacetBuilderBase;
import r01f.model.HasTrackableFacet;
import r01f.model.ModelObjectTracking;
import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

public class TrackableBuilder<CONTAINER_TYPE,
							  SELF_TYPE extends HasTrackableFacet>
  	 extends FacetBuilderBase<CONTAINER_TYPE,SELF_TYPE> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableBuilder(final CONTAINER_TYPE parentBuilder,
							final SELF_TYPE hasTrackableFacet) {
		super(parentBuilder,
			  hasTrackableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API: TRACKING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the creator and the create date to the actual date
	 * @param creator
	 */
	public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> createdBy(final SecurityPrincipalOID creatorSecurityPrincipalOid,final SecurityPrincipalID creatorSecurityPrincipalId) {
		if (creatorSecurityPrincipalId == null) return this;
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo()
					.setCreatorSecurityPrincipalOid(creatorSecurityPrincipalOid);
		_modelObject.getTrackingInfo()
					.setCreatorSecurityPrincipalId(creatorSecurityPrincipalId);
		return this;
	}
	/**
	 * Sets the last modifier and the last update date to the actual date
	 * @param updaterSecurityPrincipalId
	 */
	public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> lastUpdatedBy(final SecurityPrincipalOID updaterSecurityPrincipalOid,final SecurityPrincipalID updaterSecurityPrincipalId) {
		if (_modelObject.getTrackingInfo() == null) {
			return this.createdBy(updaterSecurityPrincipalOid,updaterSecurityPrincipalId);
		} else if (updaterSecurityPrincipalId != null) {
			_modelObject.getTrackingInfo()
						.setModifiedBy(updaterSecurityPrincipalOid,updaterSecurityPrincipalId);
		}
		return this;
	}
    /**
     * Sets the create date
     * @param date
     * @return
     */
    public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> createdAt(final Instant date) {
    	if (date == null) return this;
    	if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo()
					.setCreatedAt(date);
		return this;
    }
    /**
     * Sets the last update date
     * @param date
     * @return
     */
    public TrackableBuilder<CONTAINER_TYPE,SELF_TYPE> lastUpdatedAt(final Instant date) {
    	if (date == null) return this;
    	if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());
		_modelObject.getTrackingInfo()
					.setLastUpdatedAt(date);
		return this;
    }
}
