package r01f.model.catalogable.geo;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.catalogable.CatalogableObjectPicksBase;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPicksOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.validation.ObjectValidationResult;


/**
 * Models the label in structure picks for a catalogable object
 */
@ConvertToDirtyStateTrackable			// changes in status are tracked
@MarshallType(as="catalogableGeoPicks")
@Accessors(prefix="_")
@NoArgsConstructor
public class CatalogableGeoPicks 
	 extends CatalogableObjectPicksBase<CatalogableGeoPicksOID,GeoPositionPick,
	 									CatalogableGeoPicks> 
  implements ContainsGeoPicksForCatalogableObject {
	
	private static final long serialVersionUID = 648049202977077833L;
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getPickMemoCode(final GeoPositionPick pick) {
		return pick.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	VALIDATE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ObjectValidationResult<CatalogableGeoPicks> validate() {
		return GeoCatalogableObjectsValidators.createCatalogableGeoPicksValidator()
											   	  .validate(this);
	}
}
