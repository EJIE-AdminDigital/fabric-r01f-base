package r01f.httpclient;

import java.io.Closeable;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.oids.CommonOIDs.Token;
import r01f.oids.JavaOIDDispenser;
import r01f.patterns.IsBuilderStep;
import r01f.service.ServiceHandler;
import r01f.util.types.Strings;

/**
 * Abstract base class for executing HTTP requests with production-grade reliability features:
 * 	- bounded concurrency: maximum number of HTTP operations in flight at once, with fair or unfair semaphore-based concurrency limiting.
 * 	- connect, per-attempt and overall timeouts
 * 	- automatic retries with 
 * 		- exponential backoff + jitter
 * 		- {@code Retry-After} header support
 * 		- idempotency-key injection.
 * 
 * BEWARE!!
 * This class should be a singleton and shared across the application to avoid creating multiple HttpClient instances.
 * The internal shared {@link java.net.http.HttpClient} can reuse connections so to take advantage of it this class
 * should be a Singleton (reused across the application) and not created per request.
 *
 * <h2>Usage</h2>
 * <pre class='brush:java'>
 * 		HttpRetryableRequestConcurrentExecutor httpExecutor = new HttpRetryableRequestConcurrentExecutor<>(HttpRetryableRequestConcurrentExecutor.defaultHttpClient(Duration.ofSeconds(5)),	// connect timeout
 * 																  										   100);	// max concurrent requests
 *	    // [1] - Build the request
 *   	HttpRequest httpRequest = HttpRequest.newBuilder()
 *                   						 .uri(Url.from("https://www.euskadi.eus/hasiera/").asUri())
 *                   						 .GET()
 *                   						 .build();
 *      // [2] - Execute with retry/concurrency/timeout management
 *		HttpRetryableRequestExecResult<String> html = executor.returningResultOfType(String.class)
 *															  .forRequest(httpRequest)
 *															  .withDefaultTimeouts()
 *															  .retring(2)
 *															  .withNoIdempotencyKey()	// get request are inherently idempotent, so no idempotency key is needed
 *															  .sendAndhandleResponseWith(HttpResponse.BodyHandlers.ofString());
 *	    log.info("POST {} -> HTTP {} ({} attempt(s))",
 *	    		 uri,
 *				 result.getResponse().statusCode(),result.getNumberOfAttempts());
 * </pre>
 * 
 * BEWARE!!
 * 		It's very important to handle the InterruptedException exception properly. 
 * 		When the InterruptedException is catched, the [interrupt flag] is cleared (set to FALSE)
 * 		... so if the exception is catched, the [interrupt status] MUST be restored (set to TRUE) 
 * 			by calling Thread.currentThread().interrupt() 
 * 		... if not, thread pools (like ExecutorService) may fail to shut down properly.
 * <pre class='brush:java'>
 *    try {
 *    		... execute the request ...
 *    } catch (InterruptedException interruptedEx) {
 *    		// When the Interrupted exception is cached, the [interrupt flag] is cleared (set to FALSE)
 *    		// Restore the flag to TRUE so callers/frameworks know an interrupt occurred
 *    		// (if the flag is NOT restored, thread pools -like ExecutorService- may fail to shut down properly)
 *    		Thread.currentThread()
 *    			  .interrupt();
 *    }
 * </pre>
 * 
 * <h2>Usage extending</h2>
 * Extend this class and implement the concrete sender. 
 * A minimal JSON-POST example:
 * <pre class='brush:java'>
 *	private class HttpJsonPoster
 *		  extends HttpRetryableRequestConcurrentExecutor {
 *
 *	    public HttpJsonPoster() {
 *	    	super(HttpRetryableRequestConcurrentExecutor.defaultHttpClient(Duration.ofSeconds(5)),	// connect timeout
 *				  100);	// max concurrent requests
 *	    }
 *
 *	    public HttpResponse<String> postJson(final URI uri,
 *	    									 final String json,
 *	    									 final String idempotencyKey) throws IOException,
 *	    																		InterruptedException {
 *	    	// [1] - Build the request
 *	    	HttpRequest httpRequest = HttpRequest.newBuilder()
 *	                    						 .uri(uri)
 *	                    						 .header("Content-Type","application/json")
 *	                    						 .header("Accept","application/json")
 *	                    						 .POST(HttpRequest.BodyPublishers.ofString(json))
 *	                    						 .build();
 *	    	// [2] - Execute with retry/concurrency/timeout management
 *	    	var result = this.returningResultOfType(String.class)
 *							 .forRequest(httpRequest)
 *	    		  			 .withTimeouts(new HttpRequestTimeOuts(Duration.ofSeconds(30),		// request attempt overall timeout (per-request timeout)
 *	    								  				  		   Duration.ofSeconds(60)))		// overall timeout (all attempts)
 *	    		  			 .usingRetryParams(new HttpRequestRetryParams(3,							// 3 attempts (1 original + 2 retries)
 *	    				  				     				 		      Duration.ofSeconds(3),		// initial backoff
 *	    				  				     				 		      Duration.ofSeconds(20))	// max backoff
 *						     .withIdempotencyKey(idempotencyKey)
 *                           ..sendAndhandleResponseWith((theHttpCli,theHttpReq) -> theHttpCli.send(theHttpReq,
 *		    										  						 						HttpResponse.BodyHandlers.ofString()));
 *	    	log.info("POST {} -> HTTP {} ({} attempt(s))",
 *	    			 uri,
 *					 result.getResponse().statusCode(),result.getNumberOfAttempts());
 *	    	// [3] - Return the response
 *	    	return result.getResponse();
 *	    }
 *	}
 * </pre>
 *
 * <h2>Thread safety</h2>
 * Instances are safe for concurrent use by multiple threads.  The internal
 * {@link java.net.http.HttpClient} is created fresh per attempt (via {@link #_buildHttpClient})
 * which is the safest default; subclasses may cache a shared client if desired.
 *
 * <h2>Important caveats</h2>
 * <ul>
 *   <li>Retrying non-idempotent operations (e.g. POST) without an {@code Idempotency-Key}
 *       may cause duplicate side-effects on the remote server.  The class enforces that
 *       an idempotency key is provided whenever {@code maxAttempts > 1}.</li>
 *   <li>A connection failure after the server has already received and processed a POST
 *       cannot be distinguished from a failure before delivery.  An idempotency key is the
 *       only safe guard in that case.</li>
 * </ul>
 *
 * @param <T> the type of the HTTP response body (e.g. {@code String}, {@code byte[]}, …)
 */
@Slf4j
public class HttpConcurrentRetryableRequestExecutor 
  implements ServiceHandler,
		     Closeable {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";
    static final String RETRY_AFTER = "Retry-After";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
////////// --- CONCURRENCY LIMITER
    private final Semaphore _concurrencyLimiter;
    private final boolean _useConcurrencyLimiter;
    private final int _maxConcurrentRequests;
    
////////// --- HTTP CLIENT
    // IMPORTANT: 
    // There should normally be one HttpClient shared by the application/client instance rather than one per job.
    // HttpClient maintains connection pools and can reuse connections between requests.
    private final HttpClient _sharedHttpClient;	
    
    // executor service for running requests in virtual threads
    private final ExecutorService _executorService;	// Executors.newVirtualThreadPerTaskExecutor() = one virtual thread per task
    
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////    
    public HttpConcurrentRetryableRequestExecutor(final HttpClient httpClient,
    											  final int maxConcurrentRequests) {
        this(httpClient,
        	 true,maxConcurrentRequests);// fair concurrency limiter by default
    }
    public HttpConcurrentRetryableRequestExecutor(final HttpClient httpClient,
    											  final boolean useConcurrencyLimiter,final int maxConcurrentRequests) {
    	// shared http client
    	_sharedHttpClient = httpClient;
    	
        // One virtual thread per execution invocation.
        // This is intentionally NOT a fixed-size platform-thread pool.
        _executorService = Executors.newVirtualThreadPerTaskExecutor();
    	
    	// concurrency limiter
        _useConcurrencyLimiter = useConcurrencyLimiter;
        _maxConcurrentRequests = maxConcurrentRequests;
        
        _concurrencyLimiter = new Semaphore(maxConcurrentRequests,
        									useConcurrencyLimiter);	// true if this semaphore will guarantee first-in first-out granting of permits under contention, else false
    }
    
	public static Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
	public static HttpClient defaultHttpClient() {
		return HttpConcurrentRetryableRequestExecutor.defaultHttpClient(DEFAULT_CONNECT_TIMEOUT);
	}
    public static HttpClient defaultHttpClient(final Duration connectTimeout) {
    	Duration theConnectTimeout = connectTimeout != null ? connectTimeout
													 		: DEFAULT_CONNECT_TIMEOUT;
	    if (theConnectTimeout.isZero() || theConnectTimeout.isNegative()) throw new IllegalArgumentException("connectTimeout must be > 0");
		return HttpClient.newBuilder()
						 .connectTimeout(theConnectTimeout)
						 .version(HttpClient.Version.HTTP_2)
						 .build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Service Handler & Closeable
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void close() {
    	_sharedHttpClient.close();
    	
        // ExecutorService.close() waits for submitted virtual threads to terminate.
        // 
        // In a server application, normally create one ProcessClient during application startup and close it
        // during application shutdown.
        _executorService.close();
    }
    @Override
	public void start() {
    	log.info("[Service Handler] (Starting): {} ###################################################",
    			 this.getClass().getSimpleName());
    }
    @Override
	public void stop() {
    	log.info("[Service Handler] (Stopping): {} ###################################################",
    			 this.getClass().getSimpleName());
    	this.close();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	CONCURRENCY SLOTS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Current number of available concurrency slots.
     * Useful for metrics.
     * @return
     */
    public int getAvailableConcurrencySlots() {
        return _concurrencyLimiter.availablePermits();
    }
	/**
	 * Current number of in-use concurrency slots.
	 * @return
	 */
	public int getInUseConcurrencySlots() {
	    return this.getMaxConcurrencySlots() - this.getAvailableConcurrencySlots();
	}
    /**
     * Maximum number of concurrency slots.
     * @return
     */
    public int getMaxConcurrencySlots() {
         // Semaphore does not expose the configured capacity directly.
        return _maxConcurrentRequests;
    }
    public boolean isUsingConcurrencyLimiter() {
		return _useConcurrencyLimiter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
    /**
	 * <h2>Usage</h2>
	 * <pre class='brush:java'>
	 * 		HttpRetryableRequestConcurrentExecutor httpExecutor = new HttpRetryableRequestConcurrentExecutor<>(HttpRetryableRequestConcurrentExecutor.defaultHttpClient(Duration.ofSeconds(5)),	// connect timeout
	 * 																  										   100);	// max concurrent requests
	 *	    // [1] - Build the request
	 *   	HttpRequest httpRequest = HttpRequest.newBuilder()
	 *                   						 .uri(Url.from("https://www.euskadi.eus/hasiera/").asUri())
	 *                   						 .GET()
	 *                   						 .build();
	 *      // [2] - Execute with retry/concurrency/timeout management
	 *		HttpRetryableRequestExecResult<String> html = executor.returningResultOfType(String.class)
	 *															  .forRequest(httpRequest)
	 *															  .withDefaultTimeouts()
	 *															  .retring(2)
	 *															  .withNoIdempotencyKey()	// get request are inherently idempotent, so no idempotency key is needed
	 *															  .sendAndhandleResponseWith(HttpResponse.BodyHandlers.ofString());
	 *	    log.info("POST {} -> HTTP {} ({} attempt(s))",
	 *	    		 uri,
	 *				 result.getResponse().statusCode(),result.getNumberOfAttempts());
	 * </pre>
	 * 
	 * BEWARE!!
	 * 		It's very important to handle the InterruptedException exception properly. 
	 * 		When the InterruptedException is catched, the [interrupt flag] is cleared (set to FALSE)
	 * 		... so if the exception is catched, the [interrupt status] MUST be restored (set to TRUE) 
	 * 			by calling Thread.currentThread().interrupt() 
	 * 		... if not, thread pools (like ExecutorService) may fail to shut down properly.
	 * <pre class='brush:java'>
	 *    try {
	 *    		... execute the request ...
	 *    } catch (InterruptedException interruptedEx) {
	 *    		// The interrupt flag is now FALSE because the exception was thrown.
	 *    		// Restore the flag to TRUE so callers/frameworks know an interrupt occurred
	 *    		// (if the flag is NOT restored, thread pools -like ExecutorService- may fail to shut down properly)
	 *    		Thread.currentThread()
	 *    			  .interrupt();
	 *    }
	 * </pre>
	 * 
     * @param <T>
     * @param _httpRequest
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public <T> HttpConcurrentRetryableRequestExecutorBuilderRequestStep<T> returningResultOfType(final Class<T> type) throws IOException,
    																		    					  		 	 			 InterruptedException {
    	return new HttpConcurrentRetryableRequestExecutorBuilderRequestStepImpl<>();
    }   
/////////////////////////////////////////////////////////////////////////////////////////
//	SINGLE REQUEST EXECUTION
/////////////////////////////////////////////////////////////////////////////////////////    
    public interface HttpRequestExecutor<T> {
		HttpResponse<T> execute(final HttpClient httpClient,final HttpRequest httpRequest) throws IOException, 
																								  InterruptedException;
	}
    public record HttpRetryableRequestExecResult<T>(@Getter HttpResponse<T> response,
												  	@Getter int numberOfAttempts) {
    	// no additional methods
	}
////////// --- ASYNC EXECUTION 
    /**
     * Asynchronous convenience API.
     * Useful when the caller itself should not block.
     */
    protected <T> Future<HttpRetryableRequestExecResult<T>> _asyncExecuteRequest(final HttpRequest httpRequest,
    													 			  		     final HttpRequestTimeOuts requestTimeouts,final HttpRequestRetryParams retryParams,
    													 			  		     final Token idempotencyKey,
    													 			  		     final HttpRequestExecutor<T> execute) {
    	return _executorService.submit(() -> _executeRequest(httpRequest,
    												  		 requestTimeouts,retryParams,
    												  		 idempotencyKey,
    												  		 execute));
    }
////////// --- SYNC EXECUTION
    /**
     * Executes the request with the following features:
     * 		- limited concurrency (max concurrent requests)
     * 		- connection timeout
     * 		- per-attempt request timeout
     * 		- overall operation deadline
     * 		- bounded retries
     * 		- exponential backoff + jitter
     * 		- interruption propagation
     *
     * IMPORTANT:
     * Retrying a request is safe only when the operation is idempotent, 
     * or when the server supports an idempotency mechanism as an Idempotency-Key header
     */
    protected <T> HttpRetryableRequestExecResult<T> _executeRequest(final HttpRequest httpRequest,
    													 		    final HttpRequestTimeOuts requestTimeouts,final HttpRequestRetryParams retryParams,
    													 		    final Token idempotencyKey,
    													 		    final HttpRequestExecutor<T> execute) throws IOException,
    																		    					  		     InterruptedException {
        try {
	         // Acquire one permit for the entire logical operation.
	         // This is deliberately NOT done once per retry.
	    	_concurrencyLimiter.acquire();	// acquire() is intentionally interruptible
	    									// e.g. if the application is shutting down or the request is cancelled
            return _doExecuteRequest(httpRequest,
            				  		 requestTimeouts,retryParams,
            				  		 idempotencyKey,
            				  		 execute);
        } finally {
            _concurrencyLimiter.release();
        }
    }
    protected <T> HttpRetryableRequestExecResult<T> _doExecuteRequest(final HttpRequest httpRequest,
    													 			  final HttpRequestTimeOuts requestTimeouts,final HttpRequestRetryParams retryParams,
    													 			  final Token idempotencyKey,
    													 			  final HttpRequestExecutor<T> execute) throws IOException,
    																		    					  		 	 InterruptedException {
        // timeouts & retry params
    	HttpRequestTimeOuts theRequestTimeouts = requestTimeouts != null ? requestTimeouts
											 	   						 : HttpRequestTimeOuts.defaultTimeouts();
        HttpRequestRetryParams theRetryParams = retryParams != null ? retryParams
										   							: HttpRequestRetryParams.noRetry();
    	
        // HttpClient's connect timeout is configured when the client is created. 
        // The supplied connectTimeout is therefore expected to correspond to that client.
        Instant deadlineInstant = Instant.now().plus(theRequestTimeouts.overallTimeout().asDuration());

        IOException lastIOException = null;
        for (int attempt = 1; attempt <= theRetryParams.maxAttempts(); attempt++) {
        	if (log.isDebugEnabled()) log.debug("HTTP request attempt {} of {} for {}",
					  							attempt,theRetryParams.maxAttempts(),httpRequest.uri());
        	
        	// a) Check if the overall deadline has been exceeded
	        Duration remaining = Duration.between(Instant.now(),deadlineInstant);
	        remaining = remaining.isNegative() ? Duration.ZERO : remaining;
            if (remaining.isZero() || remaining.isNegative()) throw new IOException("HTTP operation exceeded overall timeout");

            // A request timeout must never extend beyond the overall deadline.
            // (choose the minimum between the request timeout and the remaining time until the overall deadline)
            Duration thisAttemptTimeout = theRequestTimeouts.requestAttemptOverallTimeout()
            												.asDuration().compareTo(remaining) <= 0 ? theRequestTimeouts.requestAttemptOverallTimeout().asDuration()
            											 						    				: remaining;
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder(httpRequest,
            																// header filter (no filter)
            													 			(headerName,headerValue) -> true)	
            										 		    .timeout(thisAttemptTimeout);
            		
            // Retry: add ONLY the IDEMPOTENCY_KEY header when the server understands the IDEMPOTENCY semantics.
            if (theRetryParams.maxAttempts() > 1) {
            	if (idempotencyKey != null && Strings.isNullOrEmpty(idempotencyKey.asString())) throw new IllegalArgumentException("Illegal [idempotency key]!");
            	if (idempotencyKey != null) httpRequestBuilder.header(IDEMPOTENCY_KEY,idempotencyKey.asString());	
            }
            HttpRequest finalHttpRequest = httpRequestBuilder.build();
            
            // b) execute the request and handle the response
            try {
            	// use the SHARED HttpClient to reuse connections between requests
                HttpResponse<T> httpResponse = execute.execute(_sharedHttpClient,finalHttpRequest);
                
                // If the response is OK or NOT retryable or the maximum number of attempts has been reached, return the response.
                if (HttpRequestRetryParams.isNOTRetryableStatusCode(httpResponse.statusCode())
                 || attempt == theRetryParams.maxAttempts()) return new HttpRetryableRequestExecResult<>(httpResponse,
																				  				         attempt);

                // Retry the request after a delay, which is determined as follows:
                // 		- If the server sent a Retry-After header, use that value.
                //		- Otherwise, use exponential backoff with jitter: the initial backoff is exponentially increased for each attempt, 
                //														  up to the maximum backoff.
                Duration retryDelay = theRetryParams.computeRetryDelay(httpResponse,
                        						  					   attempt);	// initial/max backoff = min / max waiting time for a retry
                // sleep for the retry delay, but do not exceed the overall deadline
                _sleepWithinDeadline(retryDelay,deadlineInstant);

            } catch (HttpTimeoutException httpTimeoutEx) {
            	log.error("HTTP request attempt {} of {} for {} timed out: {}",
						  attempt,theRetryParams.maxAttempts(),httpRequest.uri(),
						  httpTimeoutEx.getMessage());
            	
                 // This includes request-level timeout conditions.
                lastIOException = httpTimeoutEx;

                if (attempt == theRetryParams.maxAttempts()) throw new IOException("HTTP request timed out after " + attempt + " attempt(s)",
                            			  						   			  	   httpTimeoutEx);
                // sleep for the retry delay, but do not exceed the overall deadline
                _sleepWithinDeadline(theRetryParams.exponentialBackoffFor(attempt),
                        			 deadlineInstant);
            } catch (IOException ioEx) {
            	log.error("HTTP request attempt {} of {} for {} failed: {} > {}",
						  attempt,theRetryParams.maxAttempts(),httpRequest.uri(),
						  ioEx.getClass().getSimpleName(),ioEx.getMessage());
            	
                 // Network failures are potentially transient.
                 // Note that a connection can fail AFTER the remote server has received a POST. 
            	 // Therefore, blindly retrying a POST can create duplicate operations.
                lastIOException = ioEx;

                if (attempt == theRetryParams.maxAttempts()) throw ioEx;
                
                // sleep for the retry delay, but do not exceed the overall deadline
                _sleepWithinDeadline(theRetryParams.exponentialBackoffFor(attempt),
                        			 deadlineInstant);
            }
        }
        throw new IOException("HTTP operation failed",
                			   lastIOException);
    }
    private static void _sleepWithinDeadline(final Duration requestedDelay,
            								 final Instant deadline) throws InterruptedException, 
    																		IOException {
    	// how many time is left until the overall deadline? 
        Duration remaining = Duration.between(Instant.now(),deadline);
        remaining = remaining.isNegative() ? Duration.ZERO : remaining;

        if (remaining.isZero() || remaining.isNegative()) throw new IOException("HTTP operation exceeded overall timeout");

        // do not sleep longer than the remaining time until the overall deadline
        // (choose the minimum between the requested delay and the remaining time)
        Duration actualDelay = requestedDelay.compareTo(remaining) <= 0 ? requestedDelay
        									 							: remaining;
        if (actualDelay.isZero() || actualDelay.isNegative()) return;

        // Thread.sleep preserves interruption semantics by throwing InterruptedException. Do not catch and ignore it.
        long millis = actualDelay.toMillis();
        int nanos = actualDelay.minusMillis(millis).getNano();

        Thread.sleep(millis,nanos);
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////    
////////// --- BUILDER INTERFACES
    public interface HttpConcurrentRetryableRequestExecutorBuilderRequestStep<T>
    		 extends IsBuilderStep {
    	public HttpConcurrentRetryableRequestExecutorBuilderTimeoutStep<T> forRequest(final HttpRequest httpRequest) throws IOException, 
																	  														InterruptedException;
    }
    public interface HttpConcurrentRetryableRequestExecutorBuilderTimeoutStep<T>
    		 extends IsBuilderStep {
    	public HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T> withTimeouts(final HttpRequestTimeOuts requestTimeouts);
    	public default HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T> withTimeouts(final Duration requestAttemptOverallTimeout,
																			  	   		      final Duration overallTimeout) {
			return this.withTimeouts(new HttpRequestTimeOuts(requestAttemptOverallTimeout,
															 overallTimeout));
		}
    	public default HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T> withDefaultTimeouts() {
    		return this.withTimeouts(HttpRequestTimeOuts.defaultTimeouts());
    	}
    }
    public interface HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T>
    		 extends IsBuilderStep {
    	public HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> usingRetryParams(final HttpRequestRetryParams retryParams);
    	
    	public default HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> usingDefaultRetryParams() {
			return this.usingRetryParams(HttpRequestRetryParams.noRetry());
		}
    	public default HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> usingRetryParams(final int maxAttempts,
																								       final Duration initialBackoff,final Duration maxBackoff) {
    		return this.usingRetryParams(new HttpRequestRetryParams(maxAttempts,
    																initialBackoff,maxBackoff));
    	}
    	public default HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> retring(final int maxAttempts) {
			return this.usingRetryParams(new HttpRequestRetryParams(maxAttempts));
		}
    }
    public interface HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T>
    		 extends IsBuilderStep {
    	public HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T> withIdempotencyKey(final Token idempotencyKey);
    	
    	public default HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T> withNoIdempotencyKey() {
    		return this.withIdempotencyKey(null);
    	}
    	public default HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T> withDefaultIdempotencyKey() {
			return this.withIdempotencyKey(Token.forId(JavaOIDDispenser.supplyOid()));
		}
    }
    public interface HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T>
    		 extends IsBuilderStep {
    	public HttpRetryableRequestExecResult<T> sendAndhandleResponseWith(final HttpRequestExecutor<T> reqExec) throws IOException, 
																												 		InterruptedException;
    	public default HttpRetryableRequestExecResult<T> sendAndhandleResponseWith(final BodyHandler<T> responseBodyHandler) throws IOException, 
    																																InterruptedException {
    		return this.sendAndhandleResponseWith((theHttpCli,theHttpReq) -> theHttpCli.send(theHttpReq,
																					 		 responseBodyHandler));
    	}
    	public Future<HttpRetryableRequestExecResult<T>> sendAsyncAndhandleResponseWith(final HttpRequestExecutor<T> reqExec) throws IOException, 
																												 			 		 InterruptedException;
    	public default Future<HttpRetryableRequestExecResult<T>> sendAsyncAndhandleResponseWith(final BodyHandler<T> responseBodyHandler) throws IOException, 
    																															 		 		 InterruptedException {
    		return this.sendAsyncAndhandleResponseWith((theHttpCli,theHttpReq) -> theHttpCli.send(theHttpReq,
    																					  		  responseBodyHandler));
    	}
    }
////////// --- BUILDER IMPL
    public class HttpConcurrentRetryableRequestExecutorBuilderRequestStepImpl<T>
      implements HttpConcurrentRetryableRequestExecutorBuilderRequestStep<T> {
		@Override
		public HttpConcurrentRetryableRequestExecutorBuilderTimeoutStep<T> forRequest(final HttpRequest httpRequest) throws IOException, 
																													  		InterruptedException {
			return new HttpConcurrentRetryableRequestExecutorBuilderTimeoutStepImpl<>(httpRequest);
		}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class HttpConcurrentRetryableRequestExecutorBuilderTimeoutStepImpl<T>
       implements HttpConcurrentRetryableRequestExecutorBuilderTimeoutStep<T> {
    	private final HttpRequest _httpRequest;
		@Override
		public HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T> withTimeouts(final HttpRequestTimeOuts requestTimeouts) {
			return new HttpConcurrentRetryableRequestExecutorBuilderRetryStepImpl<>(_httpRequest,
																					requestTimeouts);
		}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class HttpConcurrentRetryableRequestExecutorBuilderRetryStepImpl<T>
      implements HttpConcurrentRetryableRequestExecutorBuilderRetryStep<T> {
    	private final HttpRequest _httpRequest;
    	private final HttpRequestTimeOuts _requestTimeouts;
    	
		@Override
		public HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> usingRetryParams(final HttpRequestRetryParams retryParams) {
			return new HttpConcurrentRetryableRequestExecutorBuilderIdempotentStepImpl<>(_httpRequest,
																						 _requestTimeouts,retryParams);
		}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class HttpConcurrentRetryableRequestExecutorBuilderIdempotentStepImpl<T>
      implements HttpConcurrentRetryableRequestExecutorBuilderIdempotentStep<T> {
    	private final HttpRequest _httpRequest;
    	private final HttpRequestTimeOuts _requestTimeouts;
    	private final HttpRequestRetryParams _retryParams;
		@Override
		public HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T> withIdempotencyKey(final Token idempotencyKey) {
			return new HttpConcurrentRetryableRequestExecutorBuilderHandlerStepImpl<>(_httpRequest,
																					  _requestTimeouts,_retryParams,
																					  idempotencyKey);
		}
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class HttpConcurrentRetryableRequestExecutorBuilderHandlerStepImpl<T>
	  implements HttpConcurrentRetryableRequestExecutorBuilderHandlerStep<T> {
    	private final HttpRequest _httpRequest;
    	private final HttpRequestTimeOuts _requestTimeouts;
    	private final HttpRequestRetryParams _retryParams;
	    private final Token _idempotencyKey;
		@Override
		public HttpRetryableRequestExecResult<T> sendAndhandleResponseWith(final HttpRequestExecutor<T> reqExec) throws IOException, 
																										  				InterruptedException {
			return _executeRequest(_httpRequest,
								   _requestTimeouts,_retryParams,
								   _idempotencyKey,
								   reqExec);
		}
		@Override
		public Future<HttpRetryableRequestExecResult<T>> sendAsyncAndhandleResponseWith(final HttpRequestExecutor<T> reqExec) throws IOException, 
																													   		 		 InterruptedException {
			return _asyncExecuteRequest(_httpRequest,
										_requestTimeouts,_retryParams,
										_idempotencyKey,
										reqExec);
		}
    }
}
