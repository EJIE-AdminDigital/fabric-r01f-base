package r01f.patterns;

import java.util.function.Function;

/**
 * Interface for factory types
 * @param <T> the type to create
 */
@FunctionalInterface
public interface Factory<T> {
	/**
	 * @return an instance of type T
	 */
	public T create();
	/**
	 * Creates an instance of type T and then applies the postCreate function to it
	 * @param postCreate
	 * @return
	 */
	public default T create(final Function<T,T> postCreate) {
		T outObj = this.create();
		return postCreate.apply(outObj);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> Factory<T> unchecked(final Factory<T> f) {
		return () -> {
					try {
						return f.create();
					} catch (Throwable th) {
						throw new RuntimeException(th);
					}
			  };
	}
}
