package r01f.model.catalogable.geo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.model.catalogable.CatalogableObjectsValidators;
import r01f.model.catalogable.geo.GeoCatalogableOIDs.CatalogableGeoPicksOID;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class GeoCatalogableObjectsValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//  LABEL PICKS SNAPSHOT
/////////////////////////////////////////////////////////////////////////////////////////
	public static Validates<CatalogableGeoPicks> createCatalogableGeoPicksValidator() {
		return new Validates<CatalogableGeoPicks>() {
						@Override
						public ObjectValidationResult<CatalogableGeoPicks> validate(final CatalogableGeoPicks pick) {
							ObjectValidationResult<CatalogableGeoPicks> superValid = CatalogableObjectsValidators.<CatalogableGeoPicksOID,GeoPositionPick,CatalogableGeoPicks>createCatalogableObjectPicksBaseValidator()
																														 .validate(pick);
							if (superValid.isNOTValid()) return superValid;
							
							// is valid
							return ObjectValidationResultBuilder.on(pick)
																.isValid();
						}
			   };
	}
}
