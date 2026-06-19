package r01f.patterns.result;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.SneakyThrows;

/**
 * A lightweight functional result type representing either a successful value ({@link ResultOK})
 * or an error ({@link ResultError}).
 * <p>
 * This interface is useful when you want to model recoverable outcomes without throwing exceptions
 * at every call site.
 *
 * <h2>Basic usage</h2>
 * <pre>{@code
 * 		Result<Integer> ok = Result.ok(10);
 * 		Result<Integer> err = Result.error(new IllegalArgumentException("bad input"));
 * 		int value = ok.getOrThrow();       // 10
 * }</pre>
 *
 * <pre>{@code
 *		class RemoteService {
 *			public Result<String> call() {
 *				try {
 *					return Result.ok(_makeCall());
 *				} catch (HttpException e) {
 *					return Result.error(e);
 *				}
 *			}
 *			private String _makeCall() throws HttpException {
 *				// ...
 *			}				
 *		} 
 *		class Caller {
 *			public void doWork() {
 *				var result = remoteService.call();
 *				switch (result) {
 *				case Ok(var value) -> System.out.println(value);
 *				case Err(var ex) -> {
					// do something with the error
 *				}
 *			}
 *		}
 * }</pre>
 *
 * @param <T> success value type
 */
public sealed interface Result<T>
				permits Result.ResultOK,
						Result.ResultError {
/////////////////////////////////////////////////////////////////////////////////////////
//	OK & ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Successful result variant.
	 * @param <T> success value type
	 * @param value success payload
	 */
	record ResultOK<T>(T value)
implements Result<T> {
		// nothing
	}
	/**
	 * Error result variant.
	 * @param <T> intended success type
	 * @param error error payload
	 */
	record ResultError<T>(Throwable error)
implements Result<T> { 
		public Class<? extends Throwable> errorType() {
			return error.getClass();
		}
		public String getDetailedMessage() {
			return error.getMessage();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a successful result.
	 * @param value success value
	 * @return successful result
	 * @param <T> success value type
	 */
	static <T> Result<T> ok(final T value) {
		return new ResultOK<>(value);
	}
	/**
	 * Creates an error result.
	 * @param error error value
	 * @return error result
	 * @param <T> intended success value type
	 * @param <E> error type
	 */
	static <T,E extends Throwable> Result<T> error(final E error) {
		return new ResultError<>(error);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return {@code true} when this result is successful.
	 */
	default boolean isOk() {
		return this instanceof ResultOK;
	}
	default boolean hasSucceeded() {
		return isOk();
	}
	/**
	 * @return {@code true} when this result is an error.
	 */
	default boolean isError() {
		return this instanceof ResultError;
	}
	default boolean hasFailed() {
		return isError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAST	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default ResultOK<T> asOk() {
		if (this instanceof ResultOK<T> ok) {
			return ok;
		} else {
			throw new IllegalStateException("Not an OK result");
		}
	}
	public default ResultError<T> asError() {
		if (this instanceof ResultError<T> err) {
			return err;
		} else {
			throw new IllegalStateException("Not an Error result");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the success value or throws the stored error.
	 *
	 * <pre>{@code
	 * String v = Result.ok("hello")
	 * 					.getOrThrow();
	 * }</pre>
	 *
	 * @return success value
	 * @throws Throwable when the result is an error
	 */
	@SneakyThrows
	default T getOrThrow() {
		if (this instanceof ResultOK<T> ok) {
			return ok.value();
		} else if (this instanceof ResultError<T> err) {
			throw err.error();
		} else {
			throw new IllegalStateException("Unknown state");
		}
	}
	/**
	 * Returns this result if successful; otherwise returns {@code Result.ok(other)}.
	 *
	 * <pre>{@code
	 * Result<Integer> safe = maybeErr.orElse(0);
	 * }</pre>
	 *
	 * @param other fallback value
	 * @return original success or fallback success
	 */
	default Result<T> orElse(final T other) {
		return this.isError() ? new ResultOK<>(other)
							  : this;
	}
	/**
	 * Returns this result if successful; otherwise computes a fallback value from the stored error.
	 *
	 * <pre>{@code
	 * Result<Integer> safe = maybeErr.orElseGet(ex -> -1);
	 * }</pre>
	 *
	 * @param function fallback function using the error
	 * @return original success or computed fallback success
	 */
	default Result<T> orElseGet(final Function<? super Throwable,T> function) {
		if (this instanceof ResultError<T> err) {
			return new ResultOK<>(function.apply(err.error()));
		} else {
			return this;
		}
	}
	/**
	 * Returns this result if successful; otherwise computes a fallback value lazily.
	 *
	 * <pre>{@code
	 * Result<Integer> safe = maybeErr.orElseGet(() -> 100);
	 * }</pre>
	 *
	 * @param supplier lazy fallback supplier
	 * @return original success or fallback success
	 */
	default Result<T> orElseGet(final Supplier<T> supplier) {
		if (this instanceof ResultError<T>) {
			return new ResultOK<>(supplier.get());
		} else {
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Transforms a success value while preserving errors.
	 *
	 * <pre>{@code
	 * Result<Integer> len = Result.ok("abcd").map(String::length);   // ok(4)
	 * }</pre>
	 *
	 * @param function success mapper
	 * @return mapped success or original error
	 * @param <V> mapped success type
	 */
	@SuppressWarnings("unchecked")
	default <V> Result<V> map(final Function<T,V> function) {
		if (this instanceof ResultOK<T> ok) {
			return Result.ok(function.apply(ok.value()));
		} else {
			return (Result<V>)this;
		}
	}
	/**
	 * Transforms a success value into another {@link Result}, preserving errors.
	 *
	 * <pre>{@code
	 * Result<Integer> out = Result.ok("42")
	 *                             .flatMap(str -> Result.ok(Integer.parseInt(str)));
	 * }</pre>
	 *
	 * @param function success-to-result mapper
	 * @return flattened result
	 * @param <V> mapped success type
	 */
	@SuppressWarnings("unchecked")
	default <V> Result<V> flatMap(final Function<T,Result<V>> function) {
		if (this instanceof ResultOK<T> ok) {
			return function.apply(ok.value());
		} else {
			return (Result<V>)this;
		}
	}
	/**
	 * Maps an error into a successful value, preserving successful results.
	 *
	 * <pre>{@code
	 * Result<String> recovered = Result.<String>error(new IllegalStateException("x"))
	 *                                  .mapError(Throwable::getMessage); 		// ok("x")
	 * }</pre>
	 *
	 * @param function error mapper
	 * @return recovered success or original success
	 * @param <V> mapped success type
	 */
	@SuppressWarnings("unchecked")
	default <V> Result<V> mapError(final Function<? super Throwable,V> function) {
		if (this instanceof ResultError err) {
			return Result.ok(function.apply(err.error()));
		} else {
			return (Result<V>)this;
		}
	}
}
