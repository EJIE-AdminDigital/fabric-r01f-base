package r01f.patterns;

import org.slf4j.Logger;

@FunctionalInterface
public interface ProgressSubscriber<T>
	     extends OnErrorSubscriber {
	
	public void onNext(final T result);
	
	@Override
	public default void onError(final Throwable th) {
		th.printStackTrace(System.out);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Utility to create a {@link ProgressSubscriber} from another {@link ProgressSubscriber}
	 * @param <S>
	 * @param <T>
	 * @param other
	 * @return
	 */
	public static <S extends ProgressSubscriber<T>,T> ProgressSubscriber<T> wrap(final S other) {
		return new ProgressSubscriber<T>() {
						@Override
						public void onNext(final T result) {
							other.onNext(result);
						}
						@Override
						public void onError(final Throwable th) {
							other.onError(th);
						}
			   };
	}
	public static <T> ProgressSubscriber<T> log(final Logger log) {
		return result -> log.debug("{} on-next subscriber",ProgressSubscriber.class);
	}
}
