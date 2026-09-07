package r01f.httpclient;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.TimeLapse;

@MarshallType(as="httpRequestRetryParams")
public record HttpRequestRetryParams(@MarshallField(as="maxAttempts",
									  		 		whenXml=@MarshallFieldAsXml(attr=true))
									 @Getter int maxAttempts,
									 // initial backoff (delay) for the first retry
									 @MarshallField(as="initialBackoff",
									  		 		whenXml=@MarshallFieldAsXml(attr=true))
									 @Getter TimeLapse initialBackoff,
									 
									 // maximum backoff (delay) for the last retry; the backoff is increased for each retry until it reaches this value
									 @MarshallField(as="maxBackoff",
									  		 		whenXml=@MarshallFieldAsXml(attr=true))
									 @Getter TimeLapse maxBackoff) {		
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
    public static Duration DEFAULT_INITIAL_RETRY_DELAY = Duration.ofSeconds(3);
    public static final Set<Integer> RETRYABLE_STATUS_CODES = Set.of(408, // Request Timeout
            														 425, // Too Early
            														 429, // Too Many Requests
            														 500, // Internal Server Error
            														 502, // Bad Gateway
            														 503, // Service Unavailable
            														 504);// Gateway Timeout
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    
	public HttpRequestRetryParams(final int maxAttempts,
								  final TimeLapse initialBackoff,
								  final TimeLapse maxBackoff) {
		this.maxAttempts = maxAttempts > 0 ? maxAttempts
										   : 1;	// default = 1 attempt (no retry)
		this.initialBackoff = initialBackoff != null ? initialBackoff
													 : TimeLapse.of(DEFAULT_INITIAL_RETRY_DELAY);
		this.maxBackoff = maxBackoff != null ? maxBackoff
											 : TimeLapse.of(this.initialBackoff.asDuration().multipliedBy(10));
		
		if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be >= 1");
		
		Duration initialBackoffDuration = this.initialBackoff.asDuration();
		Duration maxBackoffDuration = this.maxBackoff.asDuration();
		if (initialBackoffDuration.isNegative()) throw new IllegalArgumentException("initialBackoff must be > 0");
		if (maxBackoffDuration.isZero() || maxBackoffDuration.isNegative()) throw new IllegalArgumentException("maxBackoff must be > 0");
		if (initialBackoffDuration.compareTo(maxBackoffDuration) > 0) throw new IllegalArgumentException("initialBackoff must not exceed maxBackoff");
		if (initialBackoffDuration.compareTo(maxBackoffDuration) > 0) throw new IllegalArgumentException("initialBackoff must not exceed maxBackoff");
	}
	public HttpRequestRetryParams(final int maxAttempts,
								  final TimeLapse initialBackoff) {
		this(maxAttempts,
			 initialBackoff,
			 null);
	}
	public HttpRequestRetryParams(final int maxAttempts,
								  final Duration initialBackoff,
								  final Duration maxBackoff) {
		this(maxAttempts,
			 initialBackoff != null ? TimeLapse.of(initialBackoff) : null,
			 maxBackoff != null ? TimeLapse.of(maxBackoff) : null);
	}
	public HttpRequestRetryParams(final int maxAttempts,
								  final Duration initialBackoff) {
		this(maxAttempts,
			 initialBackoff,
			 null);
	}
	public HttpRequestRetryParams(final int maxAttempts) {
		this(maxAttempts,
			 DEFAULT_INITIAL_RETRY_DELAY);
	}
	public static HttpRequestRetryParams noRetry() {
		return new HttpRequestRetryParams(1,			// max attempts
										  (TimeLapse)null,(TimeLapse)null);	// initial / max backoff
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isRetryableStatusCode(final int statusCode) {
		return RETRYABLE_STATUS_CODES.contains(statusCode);
	}
	public static boolean isNOTRetryableStatusCode(final int statusCode) {
		return !RETRYABLE_STATUS_CODES.contains(statusCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	////////// --- UTILs
    public <T> Duration computeRetryDelay(final HttpResponse<T> response,
            							  final int attempt) {
		Optional<String> retryAfterHeaderVal = response.headers()
		            						  		   .firstValue(HttpConcurrentRetryableRequestExecutor.RETRY_AFTER);
		Optional<Duration> serverSentDelay = retryAfterHeaderVal.isPresent()
													? HttpHeaderParseUtils.parseRetryAfterHeader(retryAfterHeaderVal.get(),
		    																			 		 maxBackoff.asDuration())	// max backoff is the maximum delay we are willing to wait for a retry
		    										: Optional.empty();
		return serverSentDelay.isPresent() ? serverSentDelay.get()
										     // Missing/malformed Retry-After: fall back to exponential backoff with jitter.
        								   : this.exponentialBackoffFor(attempt);
    }
    public TimeLapse exponentialBackoffForAsTimeLapse(final int attempt) {
		return TimeLapse.of(this.exponentialBackoffFor(attempt));
	}
    public Duration exponentialBackoffFor(final int attempt) {
         // attempt=1 -> initial backoff
         // attempt=2 -> 2*initial backoff
         // attempt=3 -> 4*initial backoff
         // Saturate instead of allowing Duration arithmetic to overflow.
        long initialNanos = this.initialBackoff.asDuration().toNanos();
        long maxNanos = this.maxBackoff.asDuration().toNanos();

        long exponential;
        if (attempt >= 63
         || initialNanos > maxNanos / (1L << Math.min(attempt - 1, 62))) {
            exponential = maxNanos;
        } else {
            exponential = initialNanos * (1L << Math.min(attempt - 1, 62));
            exponential = Math.min(exponential, maxNanos);
        }
        // Full jitter: [50%, 100%] of calculated delay.
        // This prevents a fleet of clients from retrying simultaneously.
        long half = exponential / 2;
        long jitter = half == 0 ? 0
                				: ThreadLocalRandom.current().nextLong(half + 1);
        return Duration.ofNanos(half + jitter);
    }
}