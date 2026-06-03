package r01f.patterns;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * When a {@link Funtion} that throws a CHECKED exception has to be used in a lambda expression the code is awfull since the 
 * {@link Function}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Function} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Function<T,R> fn = (t) -> something that throws;	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Function<T,R> func = (t) -> {
 * 										R out = null;
 * 										try {
 * 											something that throws		<-- this throws an exception
 * 										} catch (MyException th) {
 * 											throw new RuntimeException(th);
 * 										}
 * 					 		 });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingFunction} like:
 * <pre class='brush:java'>
 * 		ThrowingFunction<T,R,MyException> func = (t) -> something that throws;
 * </pre>
 * ... or if has to be used as a param:
 * <pre class='brush:java'>
 * 		myStream.map(ThrowingFunction.unchecked(t -> new R(t));
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingFunction<T,R,E extends Throwable> {
	R apply(T t) throws E;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,R,E extends Throwable> Function<T,R> unchecked(final ThrowingFunction<T,R,E> f) {
		return t -> {
			try {
				return f.apply(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	public static <T,R,E extends Throwable> Function<T,R> exceptionIgnoredAndReturnNull(final ThrowingFunction<T,R,E> f) {
		return t -> {
					try {
						return f.apply(t);
					} catch (Throwable e) {
						// ignore
					}
					return null;
				};
	}
	public static <T,R,E extends Throwable> Function<T,R> exceptionHandledAndReturnNull(final ThrowingFunction<T,R,E> f,final BiConsumer<T,Throwable> exConsumer) {
		return t -> {
					try {
						return f.apply(t);
					} catch (Throwable e) {
						exConsumer.accept(t,e);
					}
					return null;
				};
	}
}
