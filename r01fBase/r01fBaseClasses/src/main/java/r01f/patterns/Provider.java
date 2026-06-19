package r01f.patterns;

@FunctionalInterface
public interface Provider<T> {
	public T provideValue();
}
