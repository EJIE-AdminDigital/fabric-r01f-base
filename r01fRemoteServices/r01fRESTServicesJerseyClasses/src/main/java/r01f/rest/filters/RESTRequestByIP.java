package r01f.rest.filters;

import java.util.concurrent.atomic.AtomicInteger;

public class RESTRequestByIP {
	
	private final AtomicInteger _counter = new AtomicInteger(0);
	
	/**
	 * Timestamp of the first request in the current window.
	 * 
	 * Marked as 'volatile' to ensure visibility across threads:
	 * - This value is accessed and modified by both the main Jersey thread (per-request)
	 *   and the background cleanup thread.
	 * - Volatile ensures that all threads see the latest value from main memory,
	 *   preventing stale or inconsistent reads.
	 */
	private volatile long _firstRequestTimestamp;

    public RESTRequestByIP() {
        _firstRequestTimestamp = System.currentTimeMillis();
    }

    public int incrementAndGet() {
        return _counter.incrementAndGet();
    }

    public long getFirstRequestTimestamp() {
        return _firstRequestTimestamp;
    }

    public int getCount() {
        return _counter.get();
    }
    
    public void reset() {
    	_counter.set(1); // Start at 1 because this reset is triggered by a new request, which already counts as the first one in the new window.
    	_firstRequestTimestamp = System.currentTimeMillis();
    }
}
