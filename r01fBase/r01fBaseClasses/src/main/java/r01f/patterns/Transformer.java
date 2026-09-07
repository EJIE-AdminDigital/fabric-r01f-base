package r01f.patterns;

@FunctionalInterface
public interface Transformer<T,R> {
	public R transform(final T source);
}
