package r01f.model.catalogable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.facets.HasOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePickOID;
import r01f.model.catalogable.CatalogableOIDs.CatalogablePicksOID;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CatalogableObjectsValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//  LABEL PICKS BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends CatalogablePicksOID,T extends HasOID<? extends CatalogablePickOID>,
				   P extends CatalogableObjectPicksBase<O,T,P>> Validates<P> createCatalogableObjectPicksBaseValidator() {
		return new Validates<P>() {
						@Override
						public ObjectValidationResult<P> validate(final P pick) {
							// the catalogable object's oid MUST NOT be null
							if (pick.getCatalogableObjectOid() == null) {
								return ObjectValidationResultBuilder.on(pick)
																	.isNotValidBecause("the catalogable object oid MUST NOT be null!");
							}
							// the picks collection MUST NOT be null
							if (CollectionUtils.isNullOrEmpty(pick.getPicks())) 
								return ObjectValidationResultBuilder.on(pick)
																    .isNotValidBecause("the given picks for catalogable object with oid={} are null!",
																					   pick.getCatalogableObjectOid());
							// ok!
							return ObjectValidationResultBuilder.on(pick)
																.isValid();	
						}
			   };
	}
}
