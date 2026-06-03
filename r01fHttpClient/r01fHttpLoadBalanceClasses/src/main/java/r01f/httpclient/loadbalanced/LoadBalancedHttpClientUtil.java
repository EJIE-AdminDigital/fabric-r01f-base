package r01f.httpclient.loadbalanced;

import java.util.concurrent.TimeoutException;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.ClassicHttpResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.http.loadbalance.LoadBalancedBackEndServer;
import r01f.http.loadbalance.LoadBalancedBackendServerStats;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class LoadBalancedHttpClientUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////	
	public interface LoadBalancerHttpRequestExecutor {
		public ClassicHttpResponse execute(final LoadBalancedBackEndServer choosenServer); 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXECUTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public static ClassicHttpResponse executeWithLoadBalancer(final LoadBalancedBackendServerStats choosenServerStats,
													   		  final LoadBalancerHttpRequestExecutor executor) {
		// make the http request
		ClassicHttpResponse httpResponse = null;
		long latency = -1;
		try {
			// update the server stats
			choosenServerStats.incrementSentMessages();
			choosenServerStats.incrementOpenRequests();
			
			// make the request accounting time 
			long startTime = System.currentTimeMillis();	
			
			httpResponse = executor.execute(choosenServerStats.getServerInstance());
			
			latency = System.currentTimeMillis() - startTime;

			// error?
			if (httpResponse == null) {
				throw new TimeoutException("Timed out while waiting for a response.");
			} else if (httpResponse.getCode() >= 500) {
				throw new HttpResponseException(httpResponse.getCode(),"Unexpected response");
			}
		} catch (Exception e) {
			// unexpected exception
			log.error("[load balancer] > unexpected exception: {}, retrying another server",
					  e.getMessage(),e);
			// account errors
			choosenServerStats.incrementErrors();
		} finally {
			// update the server stats
			choosenServerStats.decrementOpenRequests();
			if (latency > 0) choosenServerStats.recordLatency(latency);
		}
		return httpResponse;	// may be null
	}
}
