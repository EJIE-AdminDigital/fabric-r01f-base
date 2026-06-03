package r01f.model.persistence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;

@MarshallPolymorphicTypeInfo(includeTypeInfo = MarshalTypeInfoIncludeCase.ALWAYS)
public interface CRUDResult<T>
		 extends PersistenceOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link CRUDOK} instance
	 */
	public default CRUDOK<T> asCRUDOK() {
		if (this instanceof CRUDError<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + CRUDOK.class);
		return (CRUDOK<T>)this;
	}
	/**
	 * @return a {@link CRUDError} instance
	 */
	public default CRUDError<T> asCRUDError() {
		if (this instanceof CRUDOK<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + CRUDError.class);
		return (CRUDError<T>)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default T getOrNull() {
		if (this.hasFailed()
		 && this.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) return null;
		return this.getOrThrow();
	}
}
