package r01f.httpclient;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookieRestrictionViolationException;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.BasicHeader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import r01f.util.types.collections.CollectionUtils;

/**
 * Cookie utils using apache http client
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpCookieUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Uses apache http client to parse the cookies
	 * @param uri
	 * @param cookieHeaders
	 * @return
	 */
	@SneakyThrows
	public static Collection<Cookie> parseCookies(final URI uri,
												  final List<String> cookieHeaders) {
		// [1] - In HC5, a factory to get the "RELAXED" spec (successor to Default) is used
		//		 for a standalone instance use:
	    // 				new RFC6265CookieSpecFactory()
		//						.create(null) 
	    RFC6265CookieSpecFactory factory = new RFC6265CookieSpecFactory();
	    CookieSpec cookieSpec = factory.create(null); // Default context is fine here
	
	    List<Cookie> cookies = new ArrayList<>();
	    
	    // [2] - Handle ports 
	    int port = uri.getPort();
	    if (port < 0) port = "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
	    boolean secure = "https".equalsIgnoreCase(uri.getScheme());
	
	    // [3] - CookieOrigin 
	    String path = uri.getPath() == null 
	    		   || uri.getPath().isEmpty() ? "/" 
	    				   					  : uri.getPath();
	    CookieOrigin origin = new CookieOrigin(uri.getHost(),port,path,secure);
	
	    // [4] - Parse the headers
	    for (String cookieHeader : cookieHeaders) {
	        // HC4 SM.SET_COOKIE is replaced by HttpHeaders.SET_COOKIE
	        BasicHeader header = new BasicHeader(HttpHeaders.SET_COOKIE,cookieHeader);
	        try {
	            cookies.addAll(cookieSpec.parse(header,origin));
	        } catch (CookieRestrictionViolationException e) {
	            // Handle or log cookies that violate domain/path rules if necessary
	        }
	    }
	    return cookies;
	}
	/**
	 * Finds all cookies matching
	 * @param req
	 * @param pred
	 * @return
	 */
	public static Collection<Cookie> apacheCookiesMatching(final Collection<Cookie> cookies,
									 		 		 	   final Predicate<Cookie> pred) {
		if (CollectionUtils.isNullOrEmpty(cookies)) return null;
		return cookies.stream()
					  .filter(pred)
					  .toList();
	}
	/**
	 * Finds a cookie with the given name (no matter the domain)
	 * @param req
	 * @param cookieName
	 * @return
	 */
	public static Cookie apacheCookieWithName(final Collection<Cookie> cookies,
									    	  final String cookieName) {
		Collection<Cookie> cookiesMatching = HttpCookieUtils.apacheCookiesMatching(cookies,
																				   cookie -> cookie.getName().equals(cookieName));
		return CollectionUtils.hasData(cookiesMatching) ? CollectionUtils.firstOf(cookiesMatching) : null;
	}
}
