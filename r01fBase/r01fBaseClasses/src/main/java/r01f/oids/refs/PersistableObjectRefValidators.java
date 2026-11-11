package r01f.oids.refs;

import lombok.NoArgsConstructor;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public abstract class PersistableObjectRefValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//	REFs VALIDATORS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <R extends PersistableObjectRefBase<?>> Validates<R> createObjRefValidator() {
		return new Validates<>() {
						@Override
						public ObjectValidationResult<R> validate(final R ref) {
							if (ref.getOid() == null) return ObjectValidationResultBuilder.on(ref)
																						  .isNotValidBecause("The ref oid is mandatory");
							return ObjectValidationResultBuilder.on(ref)
																.isValid();
						}
			   };
	}
	public static <R extends PersistableObjectWithIDRefBase<?,?>> Validates<R> createObjWithIdRefValidator() {
		return new Validates<>() {
						@Override
						public ObjectValidationResult<R> validate(final R ref) {
							if (ref.getOid() == null
							 || ref.getId() == null) return ObjectValidationResultBuilder.on(ref)
																						 .isNotValidBecause("The ref oid and id are mandatory");	
							return ObjectValidationResultBuilder.on(ref)
																.isValid();
						}
			   };
	}
}
