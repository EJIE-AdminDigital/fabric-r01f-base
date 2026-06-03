package r01f.patterns;

import java.util.function.Function;

/**
 * Interface for factory types
 * @param <S> the source type
 * @param <T> the type to create
 */
@FunctionalInterface
public interface FactoryFrom<S,T> {
	/**
	 * Creates an object instance using the given object
	 * @param source
	 * @return an instance of type T built from other
	 */
	public T from(final S source);
	
	/**
	 * Creates a Function from this Factory
	 * @return
	 */
	public default Function<S,T> asFunction() {
		return s -> this.from(s);
	}
	/**
	 * Creates a guava Function from this Factory
	 * @return
	 */
	public default com.google.common.base.Function<S,T> asGuavaFunction() {
		return s -> this.from(s);
	}
}
