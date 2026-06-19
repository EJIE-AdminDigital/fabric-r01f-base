package r01f.patterns;

@FunctionalInterface
public interface CommandOn<T> {
	public void executeOn(final T obj);
}
