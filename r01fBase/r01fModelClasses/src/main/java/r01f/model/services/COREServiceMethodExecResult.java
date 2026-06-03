package r01f.model.services;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.reflect.TypeToken;

import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.patterns.Subscriber;

@MarshallPolymorphicTypeInfo
public interface COREServiceMethodExecResult<T>
		 extends Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the method execution result or throw an exception if it failed
	 * @return
	 * @throws Throwable 
	 */
	public default T getOrThrow() {
		if (this instanceof COREServiceMethodExecOK<T> ok) {
			return ok.getMethodExecResult();
		} else if (this instanceof COREServiceMethodExecError<T> err) {
			throw err.getCOREServiceException();
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	public default T getOrNull() {
		if (this.hasFailed()
		 && this.asCOREServiceMethodExecError().wasBecauseAClientError()) return null;
		return this.getOrThrow();
	}
	public default T getOrDefault(final T def) {
		Optional<T> val = this.get();
		return val.orElse(def);
	}
	public default T getOrNullAndHandleException(final Consumer<Throwable> thConsummer) {
		T outT = null;
		try {
			outT = this.getOrThrow();
		} catch (Throwable th) {
			if (thConsummer != null) thConsummer.accept(th);
		}
		return outT;
	}
	public default Optional<T> get() {
		return Optional.ofNullable(this.getOrNull());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void subscribedTo(final Subscriber<T> subscriber) {
		if (this.hasSucceeded()) {
			subscriber.onSuccess(this.asCOREServiceMethodExecOK().getMethodExecResult());
		} else {
			subscriber.onError(this.asCOREServiceMethodExecError().getError());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUESTED OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the called method
	 */
	public COREServiceMethod getCalledMethod();
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR CONDITIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public default boolean hasFailed() {
		if (this instanceof COREServiceMethodExecOK<T>) {
			return false;
		} else if (this instanceof COREServiceMethodExecError<T>) {
			return true;
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	public default boolean hasSucceeded() {
		if (this instanceof COREServiceMethodExecOK<T>) {
			return true;
		} else if (this instanceof COREServiceMethodExecError<T>) {
			return false;
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the operation result message, normally if the operation has failed, it contains an error description and if the
	 * 		   operation has succeed it contains some logging info
	 */
	public String getDetailedMessage();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public default COREServiceMethodExecError<T> asCOREServiceMethodExecError() {
		if (this instanceof COREServiceMethodExecOK<T>) throw new ClassCastException(String.format("% is NOT a % instance",
																								   this.getClass(),COREServiceMethodExecError.class));
		return (COREServiceMethodExecError<T>)this;
	}
	public default COREServiceMethodExecOK<T> asCOREServiceMethodExecOK() {
		if (this instanceof COREServiceMethodExecError<T>) throw new ClassCastException(String.format("% is NOT a % instance",
																									  this.getClass(),COREServiceMethodExecOK.class));
		return (COREServiceMethodExecOK<T>)this;
	}
	@SuppressWarnings("unchecked")
	public default <R extends COREServiceMethodExecResult<T>> R as(final Class<R> type) {
		return (R)this;
	}
	@SuppressWarnings("unchecked")
	public default <R extends COREServiceMethodExecResult<T>> R as(final TypeToken<? super R> type) {
		return (R)this;
	}
}
