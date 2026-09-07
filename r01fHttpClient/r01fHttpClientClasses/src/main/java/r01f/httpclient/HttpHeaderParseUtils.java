package r01f.httpclient;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import r01f.util.types.Numbers;
import r01f.util.types.Strings;

public abstract class HttpHeaderParseUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	RETRY-AFTER HEADER PARSER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Parses the Retry-After header value and returns the delay as a {@link Duration}.
	 * @param value the value of the Retry-After header
	 * @param maxBackoff the maximum delay duration to use if the Retry-After value is too large
	 * @return
	 */
	public static Optional<Duration> parseRetryAfterHeader(final String value,
														   final Duration maxBackoff) {
	    if (Strings.isNullOrEmpty(value)) return Optional.empty();
	    String trimmed = value.trim();
	
	    Optional<Duration> delay = null;
	    
	    // CASE 1: Retry-After delta-seconds: RFC syntax is a non-negative integer number of seconds.
	    if (Numbers.isLong(trimmed)) {
	    	long seconds = Long.parseLong(trimmed);
	        if (seconds < 0) return Optional.empty();
	
	        delay = Optional.of(Duration.ofSeconds(seconds));
	    }
	    // CASE 2: Retry-After contain an HTTP-date (ZonedDateTime with RFC_1123_DATE_TIME)
	    //		   e.g. Wed, 21 Oct 2015 07:28:00 GMT
	    try {
	        ZonedDateTime retryAt = ZonedDateTime.parse(trimmed,
	                									 DateTimeFormatter.RFC_1123_DATE_TIME);
	
	        delay = Optional.of(Duration.between(Instant.now(),
	                				 			 retryAt.toInstant()));
	    } catch (DateTimeParseException ignored) {
	        return Optional.empty();
	    }
	    
	    // CHECKS
        // A date in the past means "retry immediately".
        if (delay.get().isNegative()) return Optional.of(Duration.ZERO);
        
        // Avoid Duration overflow for absurdly large values.
        Duration theMaxDuration = maxBackoff != null ? maxBackoff : Duration.ofDays(365);
        if (delay.get().getSeconds() > theMaxDuration.getSeconds()) return Optional.of(theMaxDuration);
        
        return delay;
	}
}
