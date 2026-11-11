package r01f.model;

import r01f.model.facets.HasCreationData;
import r01f.model.facets.HasImpersonatingSecurityPrincipal;
import r01f.model.facets.HasLastUpdateData;

public interface HasTrackingInfo 
		 extends HasCreationData,
		 		 HasLastUpdateData,
		 		 HasImpersonatingSecurityPrincipal {
	/**
	 * @return the {@link ModelObjectTracking} info
	 */
	public ModelObjectTracking getTrackingInfo();
	/**
	 * Sets the model object tracking info
	 * @param tracking
	 */
	public void setTrackingInfo(final ModelObjectTracking tracking);
}
