package r01f.patterns;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * When a {@link Predicate} that throws a CHECKED exception has to be used in a lambda expression the code is awfull since the 
 * {@link Predicate}'s apply(..) method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Predicate} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Predicate<MyObj> pred = () -> { / something that throws / };	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Predicate<MyObj> pre = () -> {
 * 									try {
 * 										// something that throws	<-- this throws an exception
 * 									} catch (MyException th) {
 * 										throw new RuntimeException(th);
 * 									}
 * 					 			 });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingPredicate} like:
 * <pre class='brush:java'>
 * 		ThrowingPredicate<MyObj,MyException> pred = to -> { / something that throws / };
 * </pre>
 * ... or if has to be used as a param:
 * <pre class='brush:java'>
 * 		_someFunction(ThrowingPredicate.unchecked(to -> { / something that throws / });
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingPredicate<T,E extends Throwable> {
	boolean apply(final T to) throws E;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,E extends Throwable> Predicate<T> unchecked(final ThrowingPredicate<T,E> s) {
		return to -> {
					try {
						return s.apply(to);
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
	public static <T,E extends Throwable> Predicate<T> exceptionIgnoredAndReturnFalse(final ThrowingPredicate<T,E> s) {
		return to -> {
					try {
						return s.apply(to);
					} catch (Throwable th) {
						// ignore
						return false;
					}
			  };
	}
	public static <T,E extends Throwable> Predicate<T> exceptionHandledAndReturnFalse(final ThrowingPredicate<T,E> s,final BiConsumer<T,Throwable> exConsumer) {
		return to -> {
					try {
						return s.apply(to);
					} catch (Throwable th) {
						exConsumer.accept(to,th);
						return false;
					}
			  };
	}
}
