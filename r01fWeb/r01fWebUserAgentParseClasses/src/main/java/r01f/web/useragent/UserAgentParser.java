package r01f.web.useragent;

import java.util.HashMap;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import r01f.types.JsonString;
import r01f.util.types.StringEscapeUtils;

/**
 * User agent parsing utilities.
 *
 * Provides a thin wrapper around `nl.basjes.parse.useragent.UserAgentAnalyzer`
 * to parse the `User-Agent` header and Client Hints headers that start with
 * `sec-ch-ua*` from an incoming HTTP request or from a raw user-agent string.
 *
 * Features
 * - Parse raw user-agent strings and requests
 * - Return parsed data as a `nl.basjes.parse.useragent.UserAgent` object
 * - Produce a grouped JSON-like representation of properties suitable for embedding in JavaScript contexts
 * - Expose both flat and grouped property maps for custom usage
 *
 * Usage examples
 *
 * Manual instantiation
 * ```java
 * 		UserAgentParser parser = new UserAgentParser();
 * 		UserAgent ua = parser.parse("Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...");
 * 		JsonString json = UserAgentParser.propertiesAsJsonOf(ua);
 * 		System.out.println(json.asString());
 * ```
 * Notes
 * - The class is annotated with `@Singleton` so it can be registered as a singleton in a DI container.
 * - `userAgentRelatedHeaders(HttpServletRequest)` reads header names and  values from the request. 
 * 	  When used in servlet filters take care not to consume the request input stream in a way that downstream code expects
 *   to read it (that may throw `IllegalStateException`).
 */
@Singleton
public class UserAgentParser {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private final UserAgentAnalyzer _uaAnalyzer = UserAgentAnalyzer.newBuilder()
																   .hideMatcherLoadStats()
																   .withCache(10000)
																   .build();
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public UserAgent parse(final String userAgent) {
		return _uaAnalyzer.parse(userAgent);
	}
	public UserAgent parse(final HttpServletRequest req) {
		return _uaAnalyzer.parse(UserAgentParser.userAgentRelatedHeaders(req));
	}
	/**
	 * Creates a Map with the User-Agent header and all the "sec-ch-ua*" headers (if any) from the request
	 * @param req
	 * @return
	 */
	public static Map<String,String> userAgentRelatedHeaders(final HttpServletRequest req) {
		Map<String,String> outHeaders = new HashMap<>();
		// BEWARE!!!	The request is READED so if this is used from within a servlet filter
		//				this can cause IllegalStateException when the servlet downstream tries
		//				to read again from the request
		outHeaders.put("User-Agent",req.getHeader("User-Agent"));
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(req.getHeaderNames().asIterator(),0),
							 false)	//not parallel
					 .filter(headerName -> headerName.toLowerCase().startsWith("sec-ch-ua"))
					 .forEach(headerName -> outHeaders.put(headerName,req.getHeader(headerName)));
		return outHeaders;
	}
	/**
	 * Returns a JsonString with all the properties of the UserAgent object, grouped by their prefix (ie: "agent", "device", "layout_engine", etc)
	 * ie:
	 * <pre>
	 * 		"user-agent": "the user agent string",
	 * 		"agent": {
	 * 			"class": "the agent class",
	 * 			"build": "the agent build",
	 * 			"name": "the agent name",
	 * 			...
	 * 		},
	 * 		"device": {
	 * 			"brand": "the device brand",
	 * 			"cpu": "the device cpu",
	 * 			"name": "the device name",
	 * 			...
	 * 		},
	 *		...
	 * </pre>
	 * @param request
	 * @return
	 */
	public JsonString userAgentPropertiesAsJson(final HttpServletRequest request) {
		UserAgent ua = this.parse(request);
		return UserAgentParser.propertiesAsJsonOf(ua);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a JsonString with all the properties of the UserAgent object, grouped by their prefix (ie: "agent", "device", "layout_engine", etc)
	 * ie:
	 * <pre>
	 * 		"user-agent": "the user agent string",
	 * 		"agent": {
	 * 			"class": "the agent class",
	 * 			"build": "the agent build",
	 * 			"name": "the agent name",
	 * 			...
	 * 		},
	 * 		"device": {
	 * 			"brand": "the device brand",
	 * 			"cpu": "the device cpu",
	 * 			"name": "the device name",
	 * 			...
	 * 		},
	 *		...
	 * </pre>
	 * @param userAgent
	 * @return
	 */
	public static JsonString propertiesAsJsonOf(final UserAgent userAgent) {
		Map<String,String> allProps = UserAgentParser.propertyMapOf(userAgent);
		Map<String,Map<String,String>> groupedProps = _extractPropertyGroups(allProps);
		StringBuilder outJson = new StringBuilder();
		outJson.append("{");
		
		// the "user-agent" property is not included in the grouped properties, so we add it first
		outJson.append("\"user-agent\": ")
			   .append(_enquote(userAgent.getUserAgentString()))
			   .append(",");
		// the rest of the properties are grouped by their prefix (ie: "agent", "device", "layout_engine", etc)
		groupedProps.entrySet()
					.stream()
					.forEach(groupEntry -> {
								outJson.append("\"" + groupEntry.getKey() + "\": {");
								groupEntry.getValue()
										  .entrySet()
										  .stream()
										  .forEach(propEntry -> outJson.append("\t\"" + propEntry.getKey() + "\":" + propEntry.getValue() + ","));
								if (outJson.charAt(outJson.length() - 1) == ',') outJson.deleteCharAt(outJson.length() - 1);	// remove last comma
								outJson.append("},");
					});
		if (outJson.charAt(outJson.length() - 1) == ',') outJson.deleteCharAt(outJson.length() - 1);	// remove last comma
		outJson.append("}");
		return new JsonString(outJson.toString());
	}
	/**
	 * Returns a Map<String,String> with all the properties of the UserAgent object. 
	 * The keys of the map are the property names (see UserAgent constants)
	 * ie: 
	 * 		"AGENT_CLASS", "AGENT_BUILD", "AGENT_NAME", ... 
	 * 		"DEVICE_BRAND", "DEVICE_CPU", "DEVICE_NAME", ...
	 * 		"LAYOUT_ENGINE_CLASS", "LAYOUT_ENGINE_NAME", "LAYOUT_ENGINE_VERSION",  
	 * 		etc
	 * the values of the map are the property values, properly enquoted for being used in a JavaScript context 
	 * (ie: single quoted and with special chars escaped)
	 * @param userAgent
	 * @return
	 */
	public static Map<String,String> propertyMapOf(final UserAgent userAgent) {
		return userAgent.getAvailableFieldNamesSorted()
						.stream()
						.collect(Collectors.toMap(fieldName -> fieldName,
												  fieldName -> _enquote(userAgent.getValue(fieldName))));
	}
	private static String _enquote(final String str) {
		return "'" + StringEscapeUtils.escapeEcmaScript(str) + "'";
	}
	/**
	 * Creates a Map<String,Map<String,String>> with the properties of the UserAgent object grouped by their prefix.
	 * ie:
	 * 		"agent" -> Map<String,String> with all properties whose name starts with "AGENT"
	 * 		"device" -> Map<String,String> with all properties whose name starts with "DEVICE"
	 * 		"layout_engine" -> Map<String,String> with all properties whose name starts with "LAYOUT_ENGINE"
	 * 		etc
	 * @param userAgent
	 * @return
	 */
	public static Map<String,Map<String,String>> propertyGroupsOf(final UserAgent userAgent) {
		return _extractPropertyGroups(UserAgentParser.propertyMapOf(userAgent));
	}
	private static Map<String,Map<String,String>> _extractPropertyGroups(final Map<String,String> allProps) {
		Map<String,Map<String,String>> outGroups = new HashMap<>();
		Stream.of(GROUP_PREFIXES)
			   .forEach(prefix -> outGroups.put(prefix.toLowerCase(),_propertyGroup(allProps,prefix)));
		return outGroups;
	}
	/**
	 * UserAgent properties are like:
	 * 		AGENT_CLASS,
	 * 		AGENT_BUILD,
	 * 		AGENT_NAME
	 * 		...
	 * 		DEVICE_BRAND,
	 * 		DEVICE_CPU,
	 * 		DEVICE_NAME,
	 * 		...		
	 * 		LAYOUT_ENGINE_CLASS,
	 * 		LAYOUT_ENGINE_NAME,
	 * 		LAYOUT_ENGINE_VERSION,
	 * 		...
	 * This method returns all properties of a group, ie: given a groupPrefix="AGENT" the method
	 * returns a Map<String,String> of all properties whose name starts with "AGENT"ent
	 * @param allProps
	 * @param groupPrefix
	 * @return
	 */
	private static Map<String,String> _propertyGroup(final Map<String,String> allProps,
													 final String groupPrefix) {
		return allProps.entrySet()
					   .stream()
					   .filter(entry -> entry.getKey().startsWith(groupPrefix))
					   .collect(Collectors.toMap(entry -> entry.getKey().substring(groupPrefix.length() + 1)	// +1 because of the "_" char
							   								   .toLowerCase(),
												  entry -> entry.getValue()));
	}
	private static String[] GROUP_PREFIXES = new String[] { "AGENT",
															"DEVICE",
															"LAYOUT_ENGINE",
															"FACEBOOK",
															"HACKER",
															"IE_COMPATIBILITY",
															"LAYOUT_ENGINE",
															"OPERATING_SYSTEM",
															"UACLIENT_HINT",
															"WEBVIEW_APP"};
}
