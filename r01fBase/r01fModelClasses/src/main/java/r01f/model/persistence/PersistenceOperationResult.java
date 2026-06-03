package r01f.model.persistence;

import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;

@MarshallPolymorphicTypeInfo
public interface PersistenceOperationResult<T>
		 extends COREServiceMethodExecResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default T getOrThrow() {
		if (this instanceof PersistenceOperationExecOK<T> ok) {
			return ok.getMethodExecResult();
		} else if (this instanceof PersistenceOperationExecError<T> err) {
			// BEWARE!! override getOrThrow method! otherwise a COREServiceExeception
			//			instance will be thrown
			throw err.getPersistenceException();	// vs throw err.getCOREServiceException() at COREServiceMethodExecResult
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUESTED OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the requested operation
	 */
	public PersistenceRequestedOperation getRequestedOperation();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link PersistenceOperationExecOK} instance
	 */
	public default PersistenceOperationExecOK<T> asPersistenceOperationOK() {
		if (this instanceof PersistenceOperationExecError<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + PersistenceOperationExecOK.class);
		return (PersistenceOperationExecOK<T>)this;
	}
	/**
	 * @return a {@link PersistenceOperationExecError} instance
	 */
	public default PersistenceOperationExecError<T> asPersistenceOperationError() {
		if (this instanceof PersistenceOperationExecOK<T>) throw new ClassCastException(this.getClass() + " is not an instance of " + PersistenceOperationExecError.class);
		return (PersistenceOperationExecError<T>)this;
	}
}
