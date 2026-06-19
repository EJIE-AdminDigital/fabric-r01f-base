package r01f.types.url;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Splitter;

import lombok.experimental.Accessors;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="host")
@Accessors(prefix="_")
public class Host
	 extends OIDBaseMutable<String> {

	private static final long serialVersionUID = -3712825671090881670L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Somethimes (not a canonical usage), the host contains protocol, port, urlPath, querystring or anchor
	 * ... this var stores these components
	 * BEWARE! it's NOT serialized
	 */
	@MarshallIgnoredField
	private final transient UrlComponents _urlComponents;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Host() {
		/* default no args constructor for serialization purposes */
		_urlComponents = null;
	}
	public Host(final String host) {
		this(new UrlParserNoRegExp(host)
						.getComponents());
	}
	private Host(final UrlComponents urlComponents) {
		super(urlComponents.getHost().asString());	// store the host
		_urlComponents = urlComponents;
	}
	public Host(final Host other) {
		this(other != null ? other.getId() : (String)null);
	}
	public static Host from(final String host) {
		return new Host(host);
	}
	public static Host of(final String host) {
		return new Host(host);
	}
	public static Host strict(final Host other) {
		return Host.strict(other.getId());
	}
	public static Host strict(final String host) {
		if (host.indexOf("/") > 0 || host.indexOf(":") > 0) throw new IllegalArgumentException(host + " does NOT seem to be strictly a host!!");
		Host theHost = new Host();	// beware! do not set the components
		theHost.setId(host);
		return theHost;
	}
	public static Host valueOf(final String host) {
		return new Host(host);
	}
	public static Host forId(final String host) {
		return new Host(host);
	}
	
	public static String LOCALHOST_STR = "localhost";
	public static String LOCALHOST_USING_IP_STR = "127.0.0.1";
	
	public static Host LOCALHOST = new Host(LOCALHOST_STR);
	public static Host LOCALHOST_USING_IP = new Host(LOCALHOST_USING_IP_STR);
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the host as an url
	 */
	public Url asUrl() {
		return new Url(this.getUrlComponents());
	}
	/**
	 * @return the url components
	 */
	UrlComponents getUrlComponents() {
		// if the host was constructed as strict() url components is null
		return _urlComponents != null ? _urlComponents
									  : new UrlComponents(null,Host.strict(this.getId()),0,		// protocol / host / port
											  			  null,null,null);						// path / query string / anchor
	}
	/**
	 * Returns the TLD (top level domain)
	 * @return
	 */
	public String getTLD() {
		if (!this.getId().contains(".")) return null;
		int lastDotPos = this.getId().lastIndexOf('.');
		return this.getId().substring(lastDotPos+1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Try to match domains ignoring environments
	 * @param h1
	 * @param h2
	 * @return
	 */
	public static boolean areSameHostIgnoringEnv(final Host h1,final Host h2,
												 final String... envParts) {
		if (CollectionUtils.isNullOrEmpty(envParts)) return h1.is(h2);
		
		Set<String> h1Parts = StringSplitter.using(Splitter.on("."))
											.at(h1.asString())
											.toSet();
		Set<String> h2Parts = StringSplitter.using(Splitter.on("."))
											.at(h2.asString())
											.toSet();
		Set<String> inh1notinh2 = _setsDifference(h1Parts,h2Parts);	// guava: Sets.difference(h1Parts,h2Parts);
		Set<String> inh2notinh1 = _setsDifference(h2Parts,h1Parts);
		Set<String> difs = _setsUnion(inh1notinh2,inh2notinh1);
		
		if (difs.size() == 0) return true;	// no differences
		if (difs.size() > 1) return false;	// different!
		if (Strings.isContainedWrapper(difs.stream()
										   .findFirst()
										   .orElse(""))
				   .containsAny(envParts)) return true;
		return false;
	}
	private static <T> Set<T> _setsDifference(final Set<T> s1,final Set<T> s2) {
		// equivalent to guava's Sets.difference(s1,s2);
		Set<T> s1Cloned = new HashSet<>(s1);
		s1Cloned.removeAll(s2);
		return s1Cloned;
	}
	private static <T> Set<T> _setsUnion(final Set<T> s1,final Set<T> s2) {
		// equivalent to guava's Sets.difference(s1,s2);
		Set<T> s1Cloned = new HashSet<>(s1);
		s1Cloned.addAll(s2);
		return s1Cloned;
	}
}
