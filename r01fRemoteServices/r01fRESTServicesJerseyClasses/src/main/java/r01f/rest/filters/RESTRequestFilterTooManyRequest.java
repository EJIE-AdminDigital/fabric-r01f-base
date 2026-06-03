package r01f.rest.filters;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.rest.RESTFilter;
import r01f.rest.exceptions.RESTTooManyRequestsException;


/**
 * Filter to control too many requests per IP.
 *
 * <p>This filter limits the number of requests allowed from the same IP address
 * within a certain time window to protect the service from abuse or denial-of-service attacks.</p>
 *
 * <p><b>Configuration:</b></p>
 * The filter accepts three configurable parameters, injected using Guice:
 * <ul>
 *   <li>{@code maxRequestsPerIP} – Maximum number of requests allowed per IP.</li>
 *   <li>{@code maxWaitingTimeMS} – Time window in milliseconds before the counter resets.</li>
 *   <li>{@code cleanupIntervalMS} – Interval in milliseconds to clean up stale entries.</li>
 * </ul>
 *
 * These values can be configured via the binder in {@code buildResourceConfigBootstrapData()}, for example:
 *
 * <pre>{@code
 * binder.bindConstant().annotatedWith(Names.named("maxRequestsPerIP")).to(5);
 * binder.bindConstant().annotatedWith(Names.named("maxWaitingTimeMS")).to(30000L);
 * binder.bindConstant().annotatedWith(Names.named("cleanupIntervalMS")).to(10000L);
 * }</pre>
 *
 * If no values are provided through the binder, the filter uses default values defined in its constructor.
 *
 * <p><b>Registration:</b></p>
 * This filter is not registered using {@code getFilterTypes()}.
 * Instead, it is injected and manually registered in the constructor of:
 *
 * <pre>{@code
 * @Inject
 * public Constructor(ServiceLocator serviceLocator) {
 *     super(serviceLocator);
 *     this.register(_resourceConfigBootstrapDelegate.getInjector()
 *                                                   .getInstance(RESTRequestFilterTooManyRequest.class));
 * }
 * }</pre>
 */
@Slf4j
@Provider
@Priority(2) // Just after log filter
@Accessors(prefix="_")
public class RESTRequestFilterTooManyRequest 	
  implements RESTFilter,ContainerRequestFilter {
	
    private int _maxRequestsPerIP = 100;
    private long _maxWaitingTimeMS = 60_000;
    private long _cleanupIntervalMS = 10_000;
    
    private final Map<String,RESTRequestByIP> _requestsByIP = new ConcurrentHashMap<>();

    @Context
    private HttpServletRequest _httpRequest;
    
    public RESTRequestFilterTooManyRequest() {
	    _startCleanupThread();
	}
    
  /*  @Inject(optional = true)
	public void setMaxRequestsPerIP(@Named("maxRequestsPerIP") int maxRequestsPerIP) {
		_maxRequestsPerIP = maxRequestsPerIP;
	}
	
	@Inject(optional = true)
	public void setMaxWaitingTimeMS(@Named("maxWaitingTimeMS") long maxWaitingTimeMS) {
		_maxWaitingTimeMS = maxWaitingTimeMS;
	}
	
	@Inject(optional = true)
	public void setCleanupIntervalMS(@Named("cleanupIntervalMS") long cleanupIntervalMS) {
		_cleanupIntervalMS = cleanupIntervalMS;
	}*/

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIP = _resolveClientIP();

        RESTRequestByIP window = _requestsByIP.computeIfAbsent(clientIP, ip -> new RESTRequestByIP());
        long now = System.currentTimeMillis();

        if (now - window.getFirstRequestTimestamp() > _maxWaitingTimeMS) {
            window.reset(); // New window for this clientIP
        }

        int count = window.incrementAndGet();

        if (count > _maxRequestsPerIP) {
            long remaining = _maxWaitingTimeMS - (now - window.getFirstRequestTimestamp());
            long secondsRemaining = Math.max(1, remaining / 1000);

            log.warn("[RESTRequestFilterTooManyRequest] - Too many requests from IP: {}. Must wait {} seconds", clientIP, secondsRemaining);

            throw new RESTTooManyRequestsException(
            		"Please wait " + secondsRemaining + " seconds before retrying.",
                    secondsRemaining
            );
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    private String _resolveClientIP() {
        String xff = _httpRequest.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return _httpRequest.getRemoteAddr();
    }

    private void _startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(_cleanupIntervalMS); //Time to check inactive IP`s for free
                    
                    _cleanupExpiredIPs();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "RateLimitCleanupThread"); // Thread name

        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    private void _cleanupExpiredIPs() {
	    long now = System.currentTimeMillis();
	
	    _requestsByIP.entrySet().removeIf(entry -> {
	        if (now - entry.getValue().getFirstRequestTimestamp() > _maxWaitingTimeMS) {
	            log.info("[RESTRequestFilterTooManyRequest] - 🧹 Cleaning up expired rate limit requests for IP: {}", entry.getKey());
	            return true;
	        }
	        return false;
	    });
	}
}
