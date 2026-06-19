package r01f.patterns;

@FunctionalInterface
public interface OnErrorSubscriber {
	public void onError(final Throwable th);
}
