package r01f.model.persistence;

import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

@MarshallPolymorphicTypeInfo
public interface FindOIDsResult<O extends PersistableObjectOID>
       	 extends FindResult<O> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link FindOIDsError}
	 */
	public default FindOIDsError<O> asFindOIDsError() {
		if (this instanceof FindOIDsOK<O>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindOIDsError.class);
		return (FindOIDsError<O>)this;
	}
	/**
	 * @return a {@link FindOIDsOK}
	 */
	public default FindOIDsOK<O> asFindOIDsOK() {
		if (this instanceof FindOIDsError<O>) throw new ClassCastException(this.getClass() + " is not an instance of " + FindOIDsOK.class);
		return (FindOIDsOK<O>)this;
	}
}
