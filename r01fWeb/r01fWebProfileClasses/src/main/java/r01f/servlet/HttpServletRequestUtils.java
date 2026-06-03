package r01f.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Various http servlet request utils
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HttpServletRequestUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the real requested host: determine the original host from which a request was made
	 * In production environments
	 *		 Browser			Web Server								   Application Server
	 *						  (Apache domain xxx.euskadi.eus)			  (Tomcat domain : mytomcatpod.domain.net)
	 *		____________		______________________________________	   ___________________________
	 *			|	req
	 *			| --------->	   |									   |
	 *			|				   |		  req						   |
	 *			|				   |	 --------------------------->	   |  --> APP (request)
	 *
	 *
	 *	By default,  in this architecture to obtain the domain of the requests at application layer :
	 *								( | request.getServerName() |)
	 *	... the domain of the application server will be returned instead of web-server's, fe mytomcatpod.domain.net
	 *	... It is particularly important to obtain the original domain of the request, for example after a authenticating flow (doing client redir)
	 *
	 *
	 *	So, how get the domain of the original request ??
	 *
	 *	Method 1 :
	 *	---------
	 *	   1. Send from the apache web layer some CUSTOM headers WITH the domain name and port:
	 *
	 *			X-R01-Forwarded-Host =  xxx.euskadi.eus
	 *			X-R01-Forwarded-Port  = 443
	 *
	 *	   2. Get these header in the application :
	 *			String theForwardedHostHeaderVal = _getHeaderVal(realHttpReq,"X-R01-Forwarded-Host");
	 *			String theForwardedPortHeaderVal = _getHeaderVal(realHttpReq,"X-R01-Forwarded-Port");
	 *
	 *	BEWARE! Don't use 'X-Forwarded-Host' type headers ( use X-R01...), because in some architectures this headers are used by middleware layers between Web Server and Aplication Layer ( f.e in OpenShift a HAProxy )(
	 *
	 *	Method 2 :
	 *	----------
	 *	Include at Tomcat server.xml the apache domain
	 *
	 *		<Connector port="8081" ...
	 *					  proxyName="xxx.euskadi.eus"
	 *					  proxyPort="443"/>
	 *
	 *	This way is application-agnostic and don't have to do anything special, 'request.getServerName()' will return "xxx.euskadi.eus"
	 *
	 *
	 *
	 * This method deals with the case that the servlet engine is proxied
	 * and req.getServerName() returns the servlet engine's host, NOT the
	 * client requested one
	 *
	 * BEWARE!!!	This method reads the request body and this can be read only once.
	 * 				If you the body is readed in a filter, the target servlet will NOT be
	 * 				able to re-read it and this will also cause IllegalStateException
	 * 				... the only solution is to use a ServletRequestWrapper
	 * 					(see http://natch3z.blogspot.com.es/2009/01/read-request-body-in-filter.html
	 * 					 and ContentCachingRequestWrapper from Spring framework)
	 * @param realHttpReq
	 * @return
	 */
	public static Host clientRequestedHost(final HttpServletRequest realHttpReq) {

		// try to get the Host request header
		//		X-R01-Forwarded-Proto: 	The original protocol (http / https) requested by the client (usually https)
		//		X-R01-Forwarded-Host: 	The original host requested by the client in the Host HTTP request header.
		//		X-R01-Forwarded-Port: 	The original host port requested by the client
		// BEWARE that these headers may contain more than a single value (comma separated)
		// (see http://httpd.apache.org/docs/2.2/mod/mod_proxy.html)

		// [1] - PROTOCOL
		String theForwardedProtocolHeaderVal = _getHeaderVal(realHttpReq,"X-R01-Forwarded-Proto");

		// [2] - HOST
		String theForwardedHostHeaderVal = _getHeaderVal(realHttpReq,"X-R01-Forwarded-Host");

		// [3] - PORT
		String theForwardedPortHeaderVal = _getHeaderVal(realHttpReq,"X-R01-Forwarded-Port");

		// [99] - Compose the returned host
		Host outHost = null;
		if (Strings.isNOTNullOrEmpty(theForwardedProtocolHeaderVal)
		 && Strings.isNOTNullOrEmpty(theForwardedHostHeaderVal)
		 && Strings.isNOTNullOrEmpty(theForwardedPortHeaderVal)) {
			log.debug("[ Request Type 1 ] // proto://host:port , values :  {} {} {} " , theForwardedProtocolHeaderVal,
																			  theForwardedHostHeaderVal,
																			  theForwardedPortHeaderVal);
			// proto://host:port
			outHost = Host.from(Strings.customized("{}://{}:{}",
												   theForwardedProtocolHeaderVal,theForwardedHostHeaderVal,theForwardedPortHeaderVal));
		} else if (Strings.isNOTNullOrEmpty(theForwardedHostHeaderVal)
				&& Strings.isNOTNullOrEmpty(theForwardedPortHeaderVal)) {
			// host:port
			log.debug("[  Request Type 2] // proto://host:port , values :  {} {}",
																		  theForwardedHostHeaderVal,
																		  theForwardedPortHeaderVal );
			outHost = Host.from(Strings.customized("{}:{}",
												   theForwardedHostHeaderVal,theForwardedPortHeaderVal));
		} else if (Strings.isNOTNullOrEmpty(theForwardedProtocolHeaderVal)
				&& Strings.isNOTNullOrEmpty(theForwardedHostHeaderVal)) {
			// proto://host
			log.debug("[  Request Type 3] // proto://host:port, values {} {}",
																	   theForwardedProtocolHeaderVal,
																	  theForwardedHostHeaderVal );
			outHost = Host.from(Strings.customized("{}://{}:{}",
												   theForwardedProtocolHeaderVal,theForwardedHostHeaderVal,realHttpReq.getRemotePort()));
		} else if (Strings.isNOTNullOrEmpty(theForwardedHostHeaderVal)) {
			// host
			log.debug("[  Request Type 4 ] // host");
			outHost = Host.from(theForwardedHostHeaderVal);
		} else {
			log.debug("[  Request Type 5] // no header received");
			// no header received (local case must use this url composition)
			outHost = Host.from(Strings.customized("{}:{}",
								realHttpReq.getServerName(),realHttpReq.getLocalPort()));
		}
		return outHost;
	}
	private static String _getHeaderVal(final HttpServletRequest realHttpReq,
										final String headerName) {
		String headerVal = realHttpReq.getHeader(headerName);
		if (Strings.isNullOrEmpty(headerVal)) headerVal = realHttpReq.getHeader(headerName.toLowerCase());	// sometimes the header is always lowercase

		// the header might contain multiple values... return just the first
		return Strings.isNOTNullOrEmpty(headerVal) ? StringSplitter.using(Splitter.on(','))
																		  		  .at(headerVal)
																		  		  .group(0)
												   : null;
	}
	/**
	 * Returns the real client ip
	 * This method deals with the case that the servlet engine is proxied and the
	 * requesting ip is the proxy one, not the client one
	 *
	 * BEWARE!!!	This method reads the request body and this can be read only once.
	 * 				If you the body is readed in a filter, the target servlet will NOT be
	 * 				able to re-read it and this will also cause IllegalStateException
	 * 				... the only solution is to use a ServletRequestWrapper
	 * 					(see http://natch3z.blogspot.com.es/2009/01/read-request-body-in-filter.html
	 * 					 and ContentCachingRequestWrapper from Spring framework)
	 * @param realHttpReq
	 * @return
	 */
	public static String requestingClientIp(final HttpServletRequest realHttpReq) {
		// X-Forwarded-For: 	The IP address of the client.
		// X-Forwarded-Host: 	The original host requested by the client in the Host HTTP request header.
		// X-Forwarded-Server: The hostname of the proxy server.
		// BEWARE that these headers may contain more than a single value (comma separated)
		String ip = realHttpReq.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = realHttpReq.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = realHttpReq.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = realHttpReq.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = realHttpReq.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = realHttpReq.getRemoteAddr();
		}
		return ip;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isInternalIP(final HttpServletRequest realHttpReq) {
		String clientIp = HttpServletRequestUtils.requestingClientIp(realHttpReq);
		return HttpServletRequestUtils.isInternalIP(clientIp);
	}
	public static boolean isInternalIP(final String ip) {
		boolean internalIP = Strings.isNOTNullOrEmpty(ip)
						  && (ip.startsWith("10.")
						   || ip.startsWith("172.16.")
						   || ip.startsWith("192.168.")
						   || ip.startsWith("169.254.")
						   || ip.equals("127.0.0.1")
						   || ip.equals("0:0:0:0:0:0:0:1")
						   || ip.equals("::1"));
		return internalIP;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// Headers
/////////////////////////////////////////////////////////////////////////////////////////
	public static Map<String,String> getHeaders(final HttpServletRequest request) {
		Map<String,String> requestHeaders = Maps.newHashMap();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			requestHeaders.put(key,value);
		}
		return requestHeaders;
	}
	public static String printHeaders(final HttpServletRequest request) {
		return Joiner.on(',').withKeyValueSeparator('=').join(HttpServletRequestUtils.getHeaders(request));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IP ADDRESS MATCHER
// 	see https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/util/matcher/IpAddressMatcher.java
//		https://seancfoley.github.io/IPAddress/#_Toc456708511
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	private final class IpAddressMatcher {

		private final int _nMaskBits;
		private final InetAddress _requiredAddress;

		/**
		 * Takes a specific IP address or a range specified using the IP/Netmask (e.g.
		 * 192.168.1.0/24 or 202.24.0.0/14).
		 *
		 * @param ipAddress the address or range of addresses from which the request must come.
		 */
		public IpAddressMatcher(final String ipAddress) {
			String theIpAddress = ipAddress;
			if (ipAddress.indexOf('/') > 0) {
				String[] addressAndMask = StringUtils.split(ipAddress,"/");
				theIpAddress = addressAndMask[0];
				_nMaskBits = Integer.parseInt(addressAndMask[1]);
			}
			else {
				_nMaskBits = -1;
			}
			_requiredAddress = _parseAddress(theIpAddress);
		}
		public boolean matches(final String address) {
			InetAddress remoteAddress = _parseAddress(address);

			if (!_requiredAddress.getClass().equals(remoteAddress.getClass())) {
				return false;
			}
			if (_nMaskBits < 0) {
				return remoteAddress.equals(_requiredAddress);
			}
			byte[] remAddr = remoteAddress.getAddress();
			byte[] reqAddr = _requiredAddress.getAddress();

			int oddBits = _nMaskBits % 8;
			int nMaskBytes = _nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
			byte[] mask = new byte[nMaskBytes];

			Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

			if (oddBits != 0) {
				int finalByte = (1 << oddBits) - 1;
				finalByte <<= 8 - oddBits;
				mask[mask.length - 1] = (byte) finalByte;
			}

			// System.out.println("Mask is " + new sun.misc.HexDumpEncoder().encode(mask));

			for (int i = 0; i < mask.length; i++) {
				if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i])) {
					return false;
				}
			}
			return true;
		}
		private InetAddress _parseAddress(final String address) {
			try {
				return InetAddress.getByName(address);
			} catch (UnknownHostException e) {
				throw new IllegalArgumentException("Failed to parse address" + address, e);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COOKIE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all cookies matching
	 * @param req
	 * @param pred
	 * @return
	 */
	public static Collection<Cookie> cookiesMatching(final HttpServletRequest req,
									 		 		 final Predicate<Cookie> pred) {
		Cookie[] cookies = req.getCookies();
		if (CollectionUtils.isNullOrEmpty(cookies)) return null;

		return Stream.of(cookies)
					 .filter(pred)
					 .toList();
	}
	/**
	 * Finds a cookie with the given name (no matter the domain)
	 * @param req
	 * @param cookieName
	 * @return
	 */
	public static Cookie cookieWithName(final HttpServletRequest req,
										final String cookieName) {
		Collection<Cookie> cookiesMatching = HttpServletRequestUtils.cookiesMatching(req,
																					 cookie -> cookie.getName().equals(cookieName));
		return CollectionUtils.hasData(cookiesMatching) ? CollectionUtils.firstOf(cookiesMatching) : null;
	}
	/**
	 * Removes a cookie
	 * BEWARE!! when deleting a cookie, you must pass the exact same path, domain and secure attributes that were used to set the cookie
	 * @param req
	 * @param res
	 * @param cookieName
	 * @param urlPath
	 * @return true if the cookie was removed
	 */
	public static boolean removeCookie(final HttpServletRequest req,final HttpServletResponse res,
									   final String cookieName,final UrlPath urlPath) {
		boolean outRemoved = false;
		Cookie cookie = HttpServletRequestUtils.cookieWithName(req,
															   cookieName);
		if (cookie != null) {
			String theUrlPath = urlPath != null ? urlPath.asAbsoluteString() : "/";
			cookie.setMaxAge(0);	// maxAge > 0 ===> the cookie will expireafter that many seconds have passed
									// maxAge < 0 ===> the cookie is not stored persistently and will be deleted when the Web browser exits.
									// maxAge = 0 ===> the cookie is deleted.
			cookie.setPath(theUrlPath);
			cookie.setValue("");
			res.addCookie(cookie);
			outRemoved = true;
		}
		return outRemoved;
	}
	/**
	 * Returns cookies debug info
	 * @param request
	 * @return
	 */
	public static CharSequence cookiesDebugInfo(final HttpServletRequest request) {
		if (CollectionUtils.isNullOrEmpty(request.getCookies())) return "NO cookie found";
		StringBuilder sb = new StringBuilder();
		for (Cookie cookie : request.getCookies()) {
			sb.append(Strings.customized("\t- Cookie name={} secure={} http-only={} domain/path={}/{} max-age={} value={}",
										 cookie.getName(),
										 cookie.getSecure(),cookie.isHttpOnly(),
										 cookie.getDomain(),cookie.getPath(),
										 cookie.getMaxAge(),
										 cookie.getValue()));
		}
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MULTIPART
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if it's a multi-part POST
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static boolean isMultiPartPOST(final HttpServletRequest req) throws ServletException,
																			   IOException {
		// Check if the request's content-type starts with "multipart/form-data"
		return req.getContentType() != null && req.getContentType().startsWith("multipart/form-data");
	}
	/**
	 * Filters multi-part post-ed parts and returns just File parts
	 * @param req
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static Collection<Part> getMultiPartPOSTFileParts(final HttpServletRequest req) throws ServletException,
																					   		  	  IOException {
		if (!HttpServletRequestUtils.isMultiPartPOST(req)) throw new IllegalArgumentException("NOT a multipart/form-data request!!");

		return req.getParts()
				  .stream()
				  .filter(part -> HttpServletRequestUtils.isMultiPartPOSTFilePart(part))
				  .collect(Collectors.toList());
	}
	public static boolean isMultiPartPOSTFilePart(final Part part) {
		return HttpServletRequestUtils.parseHttpMultiPartPOSTPartContentDispositionHeader(part.getHeader("content-disposition"))
									  .isFilePart();
	}
	public static String getFileNameFromMultiPartPOSTPart(final Part part) {
		return HttpServletRequestUtils.parseHttpMultiPartPOSTPartContentDispositionHeader(part.getHeader("content-disposition"))
									  .getFileName();
	}
	public static HttpMultipartPOSTPartContentDisposition parseHttpMultiPartPOSTPartContentDispositionHeader(final Part part) {
		return HttpMultipartPOSTPartContentDisposition.from(part.getHeader("content-disposition"));
	}
	public static HttpMultipartPOSTPartContentDisposition parseHttpMultiPartPOSTPartContentDispositionHeader(final String contentDispositionHeader) {
		return HttpMultipartPOSTPartContentDisposition.from(contentDispositionHeader);
	}
	/**
	 * Utility method to get file name from HTTP header content-disposition from [multipart] POST forms
	 * https://developer.mozilla.org/es/docs/Web/HTTP/Methods/POST
	 * Sample multipart/form-data POST
	 * <pre>
	 *		POST /test.html HTTP/1.1
	 *		Host: example.org
	 *		Content-Type: multipart/form-data;boundary="boundary"
	 *
	 *		--boundary
	 *		Content-Disposition: form-data; name="field1"
	 *
	 *		value1
	 *		--boundary
	 *		Content-Disposition: form-data; name="field2"; filename="example.txt"
	 *
	 *		file contents
	 * </pre>
	 * 
	 * An example where multiple files are uploaded:
	 * <pre>
	 *		Content-Type: multipart/form-data; boundary=---------------------------148152952621447
	 *		
	 *		-----------------------------148152952621447
	 *		Content-Disposition: form-data; name="files"; filename="fileOne.txt"
	 *		Content-Type: text/plain
	 *		
	 *		this is fileOne.txt
	 *		-----------------------------148152952621447
	 *		Content-Disposition: form-data; name="files"; filename="fileTwo.txt"
	 *		Content-Type: text/plain
	 *		
	 *		this is fileTwo.txt
	 *		-----------------------------148152952621447--
	 * </pre>
	 * The [Content-Disposition] header https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
	 * is required to provide info for each subpart
	 * 		- The first directive is ALWAYS [form-data]
	 * 		- the [name] parameter is mandatory to identify the field
	 * 		- The [filename] is used when the part is a file
	 * Examples:
	 * 		Content-Disposition: form-data; name="fieldName"
	 * 		Content-Disposition: form-data; name="fieldName"; filename="filename.jpg"
	 */
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class HttpMultipartPOSTPartContentDisposition
			 implements Serializable {

		private static final long serialVersionUID = -1227352501049383611L;

		@Getter private final String _fieldName;
		@Getter private final String _fileName;

		public static HttpMultipartPOSTPartContentDisposition from(final String contentDispositionHeader) {
			if (Strings.isNullOrEmpty(contentDispositionHeader)) throw new IllegalArgumentException("invalid content-disposition header!!");

			String theFieldName =  null;
			String theFileName = null;
			String[] tokens = contentDispositionHeader.split(";");
			for (String token : tokens) {
				if (token.trim().startsWith("name")) {
					theFieldName = token.substring(token.indexOf("=") + 2,token.length()-1);
				} else if (token.trim().startsWith("filename")) {
					theFileName = token.substring(token.indexOf("=") + 2,token.length()-1);
				}
			}
			if (Strings.isNullOrEmpty(theFieldName)) throw new IllegalArgumentException("invalid content-disposition header: no name param!!");
			return new HttpMultipartPOSTPartContentDisposition(theFieldName,theFileName);
		}
		public boolean isFilePart() {
			return Strings.isNOTNullOrEmpty(_fileName);
		}
	}
}
