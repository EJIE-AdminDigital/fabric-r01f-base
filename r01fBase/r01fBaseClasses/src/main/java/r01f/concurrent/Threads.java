package r01f.concurrent;

import java.util.concurrent.ExecutorService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.TimeLapse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Threads {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void safeSleep(final long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException ignore) {
			log.error("Could NOT sleep thread: {}",ignore.getMessage(),ignore);
		}
	}
	public static void safeSleep(final TimeLapse lapse) {
		Threads.safeSleep(lapse.asMillis());
	}
	public static void waitExecutorServiceToShutdow(final ExecutorService threadPoolExecutor,
												    final TimeLapse checkEvery) {
		try {
			while (!threadPoolExecutor.isShutdown()) {
				log.info("... still running; wait for {}",checkEvery);
				Thread.sleep(checkEvery.asMillis());
			}
		} catch (Throwable th) {
			log.error("Could not check if the thread pool executor is shouted down: {}",th.getMessage(),th);
			th.printStackTrace(System.out);
		}
	}
}
