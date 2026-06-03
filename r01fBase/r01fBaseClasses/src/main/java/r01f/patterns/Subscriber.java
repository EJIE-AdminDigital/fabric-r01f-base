package r01f.patterns;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;

@FunctionalInterface
public interface Subscriber<T>
	     extends OnSuccessSubscriber<T>,
				 OnErrorSubscriber {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default void onError(final Throwable th) {
		th.printStackTrace(System.out);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Utility to create a {@link Subscriber} from another {@link Subscriber}
	 * @param <S>
	 * @param <T>
	 * @param other
	 * @return
	 */
	public static <S extends Subscriber<T>,T> Subscriber<T> wrap(final S other) {
		return new Subscriber<>() {
						@Override
						public void onSuccess(final T result) {
							other.onSuccess(result);
						}
						@Override
						public void onError(final Throwable th) {
							other.onError(th);
						}
			   };
	}
	/**
	 * Utility to create a {@link Subscriber} from an {@link OnSuccessSubscriber} and
	 * an {@link OnErrorSubscriber}
	 * ... this is handy when a {@link Subscriber} is required as a method param but
	 *     one want to use lambda expressions
	 * <pre class='brush:java'>
	 * 		util.doSomething(params,
	 * 						 UISubscriber.from(result -> { ..on success.. }));
	 * </pre>
	 * @param <T>
	 * @param onSuccess
	 * @return
	 */
	public static <T> Subscriber<T> from(final OnSuccessSubscriber<T> onSuccess) {
		return Subscriber.from(onSuccess,
							   // on error
							   th -> {  /* do nothing with the error */ });
	}
	/**
	 * Utility to create a {@link Subscriber} from an {@link OnSuccessSubscriber} and
	 * an {@link OnErrorSubscriber}
	 * ... this is handy when a {@link Subscriber} is required as a method param but
	 *     one want to use lambda expressions
	 * <pre class='brush:java'>
	 * 		util.doSomething(params,
	 * 						 UISubscriber.from(result -> { ..on success.. },
	 * 										   th -> {.. on error..}));
	 * </pre>
	 * @param <T>
	 * @param onSuccess
	 * @param onError
	 * @return
	 */
	public static <T> Subscriber<T> from(final OnSuccessSubscriber<T> onSuccess,
										 final OnErrorSubscriber onError) {
		// Create a subscriber delegating to the onSuccess & onError subscribers
		return new Subscriber<>() {
						@Override
						public void onSuccess(final T result) {
							onSuccess.onSuccess(result);
						}
						@Override
						public void onError(final Throwable th) {
							onError.onError(th);
						}
			   };
	}
	public static <T> Subscriber<T> nop() {
		return Subscriber.from(result -> { /* nothing */ },	// do nothing on success
							   error -> { /* nothing */ });	// do nothing on error
	}
	public static <T> Subscriber<T> log(final Logger log) {
		return Subscriber.from(result -> {
												log.debug("{} on-success subscriber",Subscriber.class);
										   },
						       error -> {
							   				log.error("{} on-error subscriber: {}",Subscriber.class,error.getMessage(),error);
						   			    });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Instead of writing this "ugly" code:
	 * <pre class='brush:java'>
	 * 		public void presenterOperation(params,
	 * 									   UIPresenterSubscriber<ReturnVal> subscriber) {
	 * 			try {
	 * 				T result = // do something to get the result
	 * 				subscriber.onSuccess(result);
	 * 			} catch (Throwable th) {
	 * 				subscriber.onError(th);
	 * 			}
	 * 		}
	 * </pre>
	 * ... this is more sucint:
	 * <pre class='brush:java'>
	 * 		public void presenterOperation(params,
	 * 									   UIPresenterSubscriber<ReturnVal> subscriber) {
	 * 			subscriber.wrap(() -> {
	 * 								return result;	// do something to get the result
	 * 							});
	 * 		}
	 * </pre>
	 * 
	 * If the supplier code can throw an exception use:
	 * <pre class='brush:java'>
	 * 		subscriber.wrap(ThrowingSupplier.unchecked(() -> {
	 * 														// the throwing code
	 * 												   });
	 * </pre>
	 * @param supplier
	 */
	public default void wrap(final Supplier<T> supplier) {
		this.wrap(supplier,null);	// no exception consummer
	}
	public default void wrap(final Supplier<T> supplier,final Consumer<Throwable> exConsumer) {
		if (supplier == null) throw new IllegalArgumentException("supplier is mandatory!");
		try {
			T out = supplier.get();
			this.onSuccess(out);
		} catch (Throwable th) {
			if (exConsumer != null) exConsumer.accept(th);
			this.onError(th);
		}
	}
	public default <E extends Throwable> void wrapThrowing(final ThrowingSupplier<T,E> supplier) {
		this.wrapThrowing(supplier,null);	// no exception supplier
	}
	public default <E extends Throwable> void wrapThrowing(final ThrowingSupplier<T,E> supplier,final Consumer<Throwable> exConsumer) {
		if (supplier == null) throw new IllegalArgumentException("supplier is mandatory!");
		try {
			T out = supplier.get();
			this.onSuccess(out);
		} catch (Throwable th) {
			if (exConsumer != null) exConsumer.accept(th);
			this.onError(th);
		}
	}
}
