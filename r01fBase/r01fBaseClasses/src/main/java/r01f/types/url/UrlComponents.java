package r01f.types.url;

import lombok.Getter;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;

public record UrlComponents(@Getter UrlProtocol protocol,
							@Getter Host host,
							@Getter int port,
							@Getter UrlPath urlPath,
							@Getter UrlQueryString queryString,
							@Getter String urlPathFragment)
    implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlProtocol getProtocolOrDefault(final UrlProtocol def) {
		return this.protocol != null ? this.protocol : def;
	}
	public Host getHostOrDefault(final Host def) {
		return this.host != null ? this.host : def;
	}
	public int getPortOrDefault(final int def) {
		return this.port > 0 ? this.port : def;
	}
	public UrlPath getUrlPathOrDefault(final UrlPath def) {
		return this.urlPath != null ? this.urlPath : def;
	}
	public UrlQueryString getQueryStringOrDefault(final UrlQueryString def) {
		return this.queryString != null ? this.queryString : def;
	}
	public String getUrlPathFragmentOrDefault(final String def) {
		return Strings.isNOTNullOrEmpty(this.urlPathFragment) ? this.urlPathFragment : def;
	}
	public boolean hasPort() {
		return this.port > 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlComponents from(final Url url) {
		return new UrlComponents(url.getProtocol(),url.getHost(),url.getPort(),
								 url.getUrlPath(),
								 url.getQueryString(),
								 url.getUrlPathFragment());
	}
	public static UrlComponents from(final UrlPath urlPath) {
		return new UrlComponents(null,null,0,	// no protocol, host or port
								 urlPath,
								 null,		// no query string
								 null);		// no url path fragment
	}
	public static UrlComponents from(final Host host, final int port ) {
		return new UrlComponents(null,host,port,	// no protocol, host or port
								 null,
								 null,		// no query string
								 null);		// no url path fragment
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		StringBuffer sb = new StringBuffer(200);
		sb.append("-   Protocol: ").append(this.getProtocol()).append("\r\n")
		  .append("-       Site: ").append(this.getHost()).append("\r\n")
		  .append("-       Port: ").append(this.getPort()).append("\r\n")
		  .append("-       Path: ").append(this.getUrlPath()).append("\r\n")
		  .append("Query String: ").append(this.getQueryString() != null ? this.getQueryString().asStringNotEncodingParamValues() : "").append("\r\n")
		  .append("-     Anchor: ").append(this.getUrlPathFragment());
		return sb.toString();
	}
}
