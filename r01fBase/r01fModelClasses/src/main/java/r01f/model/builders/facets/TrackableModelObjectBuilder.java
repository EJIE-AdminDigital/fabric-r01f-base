package r01f.model.builders.facets;

import java.time.Instant;

import r01f.facets.builders.FacetBuilderBase;
import r01f.model.HasTrackableFacet;
import r01f.model.ModelObjectTracking;
import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;
import r01f.patterns.FactoryFrom;


public class TrackableModelObjectBuilder<NEXT_BUILDER,
										 M extends HasTrackableFacet>
  	 extends FacetBuilderBase<NEXT_BUILDER,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilder(final FactoryFrom<M,NEXT_BUILDER> nextBuilderFactory,
									   final M hasTrackableFacet) {
		super(nextBuilderFactory,
			  hasTrackableFacet);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilderCreatedAtStep createdBy(final SecurityPrincipalOID creatorSecurityPrincipalOid,final SecurityPrincipalID creatorSecurityPrincipalId) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo()
					.setCreatorSecurityPrincipalOid(creatorSecurityPrincipalOid);
		_modelObject.getTrackingInfo()
					.setCreatorSecurityPrincipalId(creatorSecurityPrincipalId);
		return new TrackableModelObjectBuilderCreatedAtStep();
	}
	public class TrackableModelObjectBuilderCreatedAtStep {
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> at(final Instant createDate) {
			_modelObject.getTrackingInfo()
						.setCreatedAt(createDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> now() {
			_modelObject.getTrackingInfo()
						.setCreatedAt(Instant.now());
			return TrackableModelObjectBuilder.this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	public TrackableModelObjectBuilderLastUpdatedAtStep lastUpdatedBy(final SecurityPrincipalOID updaterSecurityPrincipalOid,final SecurityPrincipalID updaterSecurityPrincipalId) {
		if (_modelObject.getTrackingInfo() == null) _modelObject.setTrackingInfo(new ModelObjectTracking());	// lazy create object
		_modelObject.getTrackingInfo()
					.setModifiedBy(updaterSecurityPrincipalOid,updaterSecurityPrincipalId);
		return new TrackableModelObjectBuilderLastUpdatedAtStep();
	}
	public class TrackableModelObjectBuilderLastUpdatedAtStep {
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> at(final Instant lastUpdateDate) {
			_modelObject.getTrackingInfo()
						.setLastUpdatedAt(lastUpdateDate);
			return TrackableModelObjectBuilder.this;
		}
		public TrackableModelObjectBuilder<NEXT_BUILDER,M> now() {
			_modelObject.getTrackingInfo()
						.setLastUpdatedAt(Instant.now());
			return TrackableModelObjectBuilder.this;
		}
	}
}
