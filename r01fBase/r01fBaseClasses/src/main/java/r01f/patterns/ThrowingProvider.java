package r01f.patterns;

import java.util.function.Consumer;

/**
 * When a {@link Provider} that throws a CHECKED exception has to be used in a lambda expression the code is awfull since the 
 * {@link Provider}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Provider} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Provider<MyObj> provider = () -> something that throws;	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Provider<MyObj> provider = () -> {
 * 											MyObj out = null;
 * 											try {
 * 												something that throws		<-- this throws an exception
 * 											} catch (MyException th) {
 * 												throw new RuntimeException(th);
 * 											}
 * 					 			   });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingProvider} like:
 * <pre class='brush:java'>
 * 		ThrowingProvider<MyObj,MyException> provider = () -> new MyObj();
 * </pre>
 * ... or if has to be used as a param:
 * <pre class='brush:java'>
 * 		_someFunction(ThrowingProvider.unchecked(() -> new MyObj());
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingProvider<T,E extends Throwable> {
	T create() throws E;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,E extends Throwable> Provider<T> unchecked(final ThrowingProvider<T,E> f) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
	public static <T,E extends Throwable> Provider<T> exceptionIgnoredAndReturnNull(final ThrowingProvider<T,E> f) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						// ignore
						return null;
					}
			  };
	}
	public static <T,E extends Throwable> Provider<T> exceptionHandledAndReturnNull(final ThrowingProvider<T,E> f,final Consumer<Throwable> exConsumer) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						exConsumer.accept(th);
						return null;
					}
			  };
	}
}
