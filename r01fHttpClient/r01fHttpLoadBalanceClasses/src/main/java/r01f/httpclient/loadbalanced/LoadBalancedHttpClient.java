package r01f.httpclient.loadbalanced;

import java.io.IOException;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;

import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.http.loadbalance.LoadBalancedBackEndServer;
import r01f.http.loadbalance.LoadBalancedBackendServerStats;
import r01f.http.loadbalance.LoadBalancerIDs.LoadBalancedServiceID;
import r01f.http.loadbalance.LoadBalancerManager;
import r01f.http.loadbalance.exception.LoadBalancerNOServerAvailableException;
import r01f.http.loadbalance.exception.LoadBalancerRetriesExceededException;
import r01f.httpclient.loadbalanced.LoadBalancedHttpClientUtil.LoadBalancerHttpRequestExecutor;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.UrlQueryString;

/**
 * Load balancer inspired by https://github.com/Kixeye/janus
 * 
 * Load balancing is splitted into two concerns:
 * 		[1] - server selection
 * 		[2] - interaction with the selected server
 * 
 * Usage
 * <pre class='brush:java'>
 * 		// [1] - Create the load balancer
 * 		LoadBalancedServiceID serviceId = LoadBalancedServiceID.named("test");
 *		LoadBalancerManager loadBalancer = LoadBalancerManager.builder()
 *														  	  .usingNewMetricRegistry()
 *															  .withDefaultRefreshInterval()
 *															  .withRandomLoadBalancing()
 *															  .withServers(serviceId,
 *																		   Url.from("http://www.google.com"),Url.from("http://www.google.es"))
 *															  .build();
 *		LoadBalancedHttpClient loadBalancedHttpClient = new LoadBalancedHttpClient(loadBalancer,
 *																				   serviceId,
 *																				   2);
 *
 *		// [2] - Execute load balanced
 *		for (int i=0; i < 10; i++) {
 *			HttpGet httpGet = new HttpGet(Url.from(UrlPath.from("search"),UrlQueryString.fromUrlEncodedParamsString("q=bilbao"))
 *											 .asString());
 *			try {
 *				org.apache.http.HttpResponse resp = loadBalancedHttpClient.executeWithLoadBalancer(httpGet);
 *				System.out.println("=====>" + resp.getStatusLine().getStatusCode());
 *				EntityUtils.consumeQuietly(resp.getEntity());	// the response MUST be consummed in order to avoid exception [httpclient exception “org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection"]
 *			} catch (Throwable th) {
 *				th.printStackTrace();
 *				break;
 *			}
 *		}
 * </pre>
 */
@Slf4j
public class LoadBalancedHttpClient {
/////////////////////////////////////////////////////////////////////////////////////////
//	FILEDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LoadBalancerManager _loadBalancer;
	private final LoadBalancedServiceID _serviceId;
	private final int _numRetries;
	
	private final CloseableHttpClient _httpClient;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("resource")
	public LoadBalancedHttpClient(final LoadBalancerManager loadBalancer,
								  final LoadBalancedServiceID serviceId,
								  final int numRetries) {
		this(loadBalancer,
			 serviceId,
			 // create an http client with some timeouts
			 _createHttpClient(),
			 numRetries);
	}
	public LoadBalancedHttpClient(final LoadBalancerManager loadBalancer,
								  final LoadBalancedServiceID serviceId,
								  final CloseableHttpClient httpClient,
								  final int numRetries) {
		_loadBalancer = loadBalancer;
		_serviceId = serviceId;
		_httpClient = httpClient;
		_numRetries = numRetries;
	}
	private static CloseableHttpClient _createHttpClient() {
		// 1. Connection-level settings 
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
															// The time to establish the connection
															.setConnectTimeout(Timeout.ofSeconds(5))
															// after establishing the connection; maximum time of inactivity between two data packets
															.setSocketTimeout(Timeout.ofSeconds(5)) // Logic formerly in SocketTimeout
															.build();
		// 2. Request-level settings
		RequestConfig requestConfig = RequestConfig.custom()
												   // the time to wait for a connection from the connection manager/pool
												   .setConnectionRequestTimeout(Timeout.ofSeconds(10))
												   // after establishing the connection; maximum time of inactivity between two data packets
												   .setResponseTimeout(Timeout.ofSeconds(5)) // Logic formerly in SocketTimeout
												   .build();
		// 3. Glue them together using a Connection Manager
		HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
																				  .setDefaultConnectionConfig(connectionConfig)
																				  .build();
		
		CloseableHttpClient httpClient = HttpClients.custom()
													.setConnectionManager(cm)
													.setDefaultRequestConfig(requestConfig)
													.build();
		return httpClient;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static Url urlFor(final LoadBalancedBackEndServer choosenServer,
					  		 final UrlPath urlPath,final UrlQueryString urlQueryString) {
		// prefix URL with selected server
		Url newUrl = urlPath != null ? choosenServer.getUrl().joinWith(urlPath) : choosenServer.getUrl();
		newUrl = urlQueryString != null ? newUrl.joinWith(urlQueryString) : newUrl;
		return newUrl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public ClassicHttpResponse executeWithLoadBalancer(final ClassicHttpRequest req) throws LoadBalancerNOServerAvailableException,
																			  		   		LoadBalancerRetriesExceededException {
		return this.executeWithLoadBalancer(req,(HttpContext)null);	// no context
	}
	public ClassicHttpResponse executeWithLoadBalancer(final ClassicHttpRequest req,
													   final HttpContext httpContext) throws LoadBalancerNOServerAvailableException,
																			  		  		 LoadBalancerRetriesExceededException {
		// ensure that the requests does NOT contains the host part
		Url url = Url.from(req.getRequestUri());
		if (url.getHost() != null) throw new IllegalArgumentException("The [request uri] MUST NOT contain the host part in order to be load balanced!");
		
		// execute the request
		return _executeWithLoadBalancer(// context to help choosing the server
										new LoadBalancerContextForHttpRequest(req),
										// request executor
										new LoadBalancerHttpRequestExecutor() {
													@Override
													public ClassicHttpResponse execute(final LoadBalancedBackEndServer choosenServer) {
														try {
															HttpHost host = HttpHost.create(choosenServer.getUrl().getHost().asString());
															return _httpClient.executeOpen(host,req,httpContext);
														} catch (Exception ex) {
															throw Throwables.throwUnchecked(ex);
														}
													}
										 });
	}
	public <T> T executeWithLoadBalancer(final ClassicHttpRequest req,
										 final HttpClientResponseHandler<? extends T> responseHandler) throws LoadBalancerNOServerAvailableException,
																			   								  LoadBalancerRetriesExceededException,
																			   								  HttpException,
																			   								  IOException {
		// get the response
		ClassicHttpResponse httpResponse = this.executeWithLoadBalancer(req);
		// run the response handler
		return responseHandler.handleResponse(httpResponse);
	}
	public <T> T executeWithLoadBalancer(final ClassicHttpRequest req,
										 final HttpClientResponseHandler<? extends T> responseHandler,
										 final HttpContext httpContext) throws LoadBalancerNOServerAvailableException,
																			   LoadBalancerRetriesExceededException,
																			   HttpException,
																			   IOException {
		// get the response
		ClassicHttpResponse httpResponse = this.executeWithLoadBalancer(req,
																 		httpContext);
		// run the response handler
		return responseHandler.handleResponse(httpResponse);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected ClassicHttpResponse _executeWithLoadBalancer(final LoadBalancerContextForHttpRequest context,
														   final LoadBalancerHttpRequestExecutor executor) throws LoadBalancerNOServerAvailableException,
																										   		  LoadBalancerRetriesExceededException {
		ClassicHttpResponse httpResponse = null;
		
		int retries = _numRetries;
		do {
			// get a load balanced server
			LoadBalancedBackendServerStats choosenServerStats = _loadBalancer.chooseServerFor(_serviceId,
																							  context);
			if (choosenServerStats == null) throw new LoadBalancerNOServerAvailableException(_serviceId);
			
			if (log.isDebugEnabled()) log.debug("[load balance] > choosen sericeId/server={}",
					  							choosenServerStats.getKey().asString());

			// make the http request
			httpResponse = LoadBalancedHttpClientUtil.executeWithLoadBalancer(choosenServerStats,
																			  executor);
			retries = retries - 1;
		} while (httpResponse == null
			  && retries >= 0);
		if (httpResponse == null) throw new LoadBalancerRetriesExceededException(_serviceId,_numRetries);
		
		return httpResponse;
	}
}
