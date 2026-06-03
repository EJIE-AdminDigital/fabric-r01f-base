package r01f.patterns;

import java.util.function.Consumer;

/**
 * When a {@link Factory} that throws a CHECKED exception has to be used in a lambda expression the code is awful since the 
 * {@link Factory}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Factory} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Factory<MyObj> factory = () -> something that throws;	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Factory<MyObj> factory = () -> {
 * 									MyObj out = null;
 * 									try {
 * 										something that throws		<-- this throws an exception
 * 									} catch (MyException th) {
 * 										throw new RuntimeException(th);
 * 									}
 * 					 			 });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingFactory} like:
 * <pre class='brush:java'>
 * 		ThrowingFactory<MyObj,MyException> factory = () -> new MyObj();
 * </pre>
 * ... or if a {@link Factory} has to be used as a param:
 * <pre class='brush:java'>
 * 		_someFunction(ThrowingFactory.unchecked(() -> new MyObj());
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingFactory<T,E extends Throwable> {
	T create() throws E;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,E extends Throwable> Factory<T> unchecked(final ThrowingFactory<T,E> f) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
	public static <T,E extends Throwable> Factory<T> exceptionIgnoredAndReturnNull(final ThrowingFactory<T,E> f) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						return null;
					}
			  };
	}
	public static <T,E extends Throwable> Factory<T> exceptionHandledAndReturnNull(final ThrowingFactory<T,E> f,final Consumer<Throwable> exConsumer) {
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
