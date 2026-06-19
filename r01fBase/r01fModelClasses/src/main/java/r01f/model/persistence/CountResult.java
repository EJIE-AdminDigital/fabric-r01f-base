package r01f.model.persistence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

@MarshallPolymorphicTypeInfo
public interface CountResult<T>
		 extends PersistenceOperationResult<Long> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link CountOK} instance
	 */
	public default CountOK<T> asCountOK() {
		if (this instanceof CountError<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + CountOK.class);
		return (CountOK<T>)this;
	}
	/**
	 * @return a {@link CountError} instance
	 */
	public default CountError<T> asCountError() {
		if (this instanceof CountOK<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + CountError.class);
		return (CountError<T>)this;
	}
}
