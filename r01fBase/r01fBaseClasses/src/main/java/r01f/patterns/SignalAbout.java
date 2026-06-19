package r01f.patterns;

/**
 * Used to be informed about something on anything
 * @param <T>
 */
@FunctionalInterface
public interface SignalAbout<T> {
	public void signal(final T about);
}
