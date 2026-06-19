package r01f.patterns;

/**
 * When a {@link Supplier} that throws a CHECKED exception has to be used in a lambda expression the code is awfull since the 
 * {@link Supplier}'s create method has to be wrapped inside a try-catch statement
 * 
 * An usual {@link Supplier} cannot be used if a CHECKED exception is thrown
 * <pre class='brush:java'>
 * 		Supplier<MyObj> supplier = () -> something that throws;	<-- the compiler complains!!
 * </pre>
 * ... the "ugly" solution is to wrap the exception-throwing code inside a try-catch and rethrow a RuntimeException
 * <pre class='brush:java'>
 * 		Supplier<MyObj> supplier = () -> {
 * 											MyObj out = null;
 * 											try {
 * 												something that throws		<-- this throws an exception
 * 											} catch (MyException th) {
 * 												throw new RuntimeException(th);
 * 											}
 * 					 			   });
 * </pre>
 * This verbosity can be avoided with this {@link ThrowingSupplier} like:
 * <pre class='brush:java'>
 * 		ThrowingSupplier<MyObj,MyException> supplier = () -> new MyObj();
 * </pre>
 * ... or if has to be used as a param:
 * <pre class='brush:java'>
 * 		_someFunction(ThrowingSupplier.unchecked(() -> new MyObj());
 * </pre>
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowingSupplier<T,E extends Throwable> {
	T supply() throws E;
	
	default T get() throws E {
		return this.supply();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T,E extends Throwable> Supplier<T> unchecked(final ThrowingSupplier<T,E> s) {
		return () -> {
					try {
						return s.supply();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
	public static <T,E extends Throwable> java.util.function.Supplier<T> uncheckedJavaSupplier(final ThrowingSupplier<T,E> s) {
		return () -> {
					try {
						return s.supply();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
}
