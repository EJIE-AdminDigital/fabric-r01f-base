package r01f.httpclient;

import java.time.Duration;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.TimeLapse;

/**
 * Request timeouts for a request (per-attempt timeout and overall timeout)
 */
@MarshallType(as="httpRequestTimeouts")
public record HttpRequestTimeOuts(// @Getter TimeLapse connectTimeout,				// timeout to connect is set per-httpclient NOT per-request, so this timeout is expected to be set in the shared http client
								  // per-attempt timeout (includes connection + request processing)
								  @MarshallField(as="requestAttemptOverallTimeout",
										  		 whenXml=@MarshallFieldAsXml(attr=true))
								  @Getter TimeLapse requestAttemptOverallTimeout,	
								  
								  // overall timeout for the entire operation (includes all attempts)
								  @MarshallField(as="overallTimeout",
										  		 whenXml=@MarshallFieldAsXml(attr=true))
								  @Getter TimeLapse overallTimeout) {	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
    public static Duration DEFAULT_REQUEST_ATTEMPT_OVERALL_TIMEOUT = Duration.ofSeconds(30);
    
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    
	public HttpRequestTimeOuts(final Duration requestAttemptOverallTimeout,
							   final Duration overallTimeout) {
		this(TimeLapse.of(requestAttemptOverallTimeout),
			 TimeLapse.of(overallTimeout));
	}
	public HttpRequestTimeOuts(final TimeLapse requestAttemptOverallTimeout,
							   final TimeLapse overallTimeout) {
		this.requestAttemptOverallTimeout = requestAttemptOverallTimeout != null ? requestAttemptOverallTimeout
																				 : TimeLapse.of(DEFAULT_REQUEST_ATTEMPT_OVERALL_TIMEOUT);
		this.overallTimeout = overallTimeout != null ? overallTimeout
													 : _overallTimeoutForPerRequestTimeoutAndRetries(this.requestAttemptOverallTimeout,
							 										  								 3);	// default max attempts = 3
		
		Duration requestAttemptOverallTimeoutDuration = this.requestAttemptOverallTimeout.asDuration();
		Duration overallTimeoutDuration = this.overallTimeout.asDuration();
        if (requestAttemptOverallTimeoutDuration.isZero() || requestAttemptOverallTimeoutDuration.isNegative()) throw new IllegalArgumentException("requestAttemptOverallTimeout must be > 0");
        if (overallTimeoutDuration.isZero() || overallTimeoutDuration.isNegative()) throw new IllegalArgumentException("overallTimeout must be > 0");
	}
	public HttpRequestTimeOuts(final Duration requestAttemptOverallTimeout) {
		this(requestAttemptOverallTimeout,
			 null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEFAULTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static HttpRequestTimeOuts defaultTimeouts() {
		return new HttpRequestTimeOuts(Duration.ofSeconds(30),	// request attempt overall timeout (per-attempt timeout)
									   Duration.ofSeconds(60));	// overall timeout (all attempts)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Duration getRequestAttemptOverallTimeoutAsDuration() {
		return this.requestAttemptOverallTimeout.asDuration();
	}
	public Duration getOverallTimeoutAsDuration() {
		return this.overallTimeout.asDuration();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    private static Duration _overallTimeoutForPerRequestTimeoutAndRetries(final Duration perRequestTimeout,
												  						  final int retries) {
		Duration overallTimeout = Duration.ofSeconds(0);
		for(int i=0; i < retries; i++) {
			overallTimeout = overallTimeout.plus(perRequestTimeout)
										   .plus(Duration.ofSeconds(10));	// a margin for the retry delay (the retry delay is increased on each retry until it reaches the max retry delay)
		}
		return overallTimeout;
	}
    private static TimeLapse _overallTimeoutForPerRequestTimeoutAndRetries(final TimeLapse perRequestTimeout,
												  						   final int retries) {
    	return TimeLapse.of(_overallTimeoutForPerRequestTimeoutAndRetries(perRequestTimeout.asDuration(),
																		  retries));
    }
}