package r01f.patterns;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * When a {@link Consumer} that throws a CHECKED exception has to be used in a lambda expression the code is awfull since the 
 * {@link Consumer}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Consumer} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Consumer<T> Consumer = (t) -> something that throws	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Consumer<T> Consumer = (t) -> {
 * 										try {
 * 											something that throws		<-- this throws an exception
 * 										} catch (MyException th) {
 * 											throw new RuntimeException(th);
 * 										}
 * 					 			 });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingConsumer} like:
 * <pre class='brush:java'>
 * 		ThrowingConsumer<T,MyException> Consumer = (t) -> something that throws;
 * </pre>
 * ... or if has to be used as a param:
 * <pre class='brush:java'>
 * 		myStream.map(ThrowingConsumer.unchecked(t -> something that throws);
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingConsumer<T,E extends Throwable> {
	void accept(T t) throws E;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,E extends Throwable> Consumer<T> unchecked(final ThrowingConsumer<T,E> c) {
		return t -> {
			try {
				c.accept(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	public static <T,E extends Throwable> Consumer<T> ignored(final ThrowingConsumer<T,E> c) {
		return t -> {
			try {
				c.accept(t);
			} catch (Throwable e) {
				// ignore
			}
		};
	}
	public static <T,E extends Throwable> Consumer<T> exceptionHandled(final ThrowingConsumer<T,E> c,final BiConsumer<T,Throwable> exceptionConsummer) {
		return t -> {
			try {
				c.accept(t);
			} catch (Throwable e) {
				exceptionConsummer.accept(t,e);
			}
		};
	}
}
