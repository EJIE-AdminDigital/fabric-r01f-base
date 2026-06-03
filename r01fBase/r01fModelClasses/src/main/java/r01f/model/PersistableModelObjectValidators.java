package r01f.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class PersistableModelObjectValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <P extends PersistableModelObject<?>> Validates<P> createPersistableObjBaseValidator() {
		return new Validates<P>() {
						@Override
						public ObjectValidationResult<P> validate(final P obj) {
							// Validate the data
							if (obj.getOid() == null) {
								return ObjectValidationResultBuilder.on(obj)
																	.isNotValidBecause("The {} object MUST have an oid",
																		  			   obj.getClass().getSimpleName());
							}
							return ObjectValidationResultBuilder.on(obj)
																.isValid();
						}
			   };
	}
}
