package r01f.httpclient;

import java.io.Serializable;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.util.Base64;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.types.url.Host;
import r01f.util.types.Strings;

/**
 * Proxy info
 */
@NoArgsConstructor
@MarshallType(as="proxySettings")
@Accessors(prefix="_")
public class HttpClientProxySettings
  implements Debuggable,
  			 ContainsConfigData,
  			 Serializable {
	private static final long serialVersionUID = -2632461537616357832L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="host",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private  Host _proxyHost;
	@MarshallField(as="port",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private int _proxyPort;
	@MarshallField(as="user",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private LoginID _user;
	@MarshallField(as="password",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private  Password _password;
	@MarshallField(as="enabled",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private boolean _enabled;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpClientProxySettings(final Host proxyHost,final int proxyPort,
								   final LoginID userCode,final Password password,
								   final boolean enabled) {
		_proxyHost = proxyHost;
		_proxyPort = proxyPort;
		_user = userCode;
		_password = password;
		_enabled = enabled;
	}
	public HttpClientProxySettings(final Host proxyHost,final int proxyPort,
								   final LoginID userCode,final Password password) {
		this(proxyHost,proxyPort,
			 userCode,password,
			 true);
	}
	public HttpClientProxySettings(final Host proxyHost,
								   final LoginID userCode,final Password password) {
		this(proxyHost,
			 userCode,password,
			 true);
	}
	public HttpClientProxySettings(final Host proxyHost,
								   final LoginID userCode,final Password password,
								   final boolean enabled) {
		this(proxyHost.asUrl().getHost(),proxyHost.asUrl().getPort(),
			 userCode,password,
			 enabled);
	}
	public HttpClientProxySettings(final HttpClientProxySettings other,
								   final boolean enabled) {
		this(other.getProxyHost(),other.getProxyPort(),
			 other.getUser(),other.getPassword(),
			 enabled);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Proxy Authenticator
	 * [*] Warn ! :https://bugs.openjdk.org/browse/JDK-8326949
	 * @return Authenticator
	 */
	public Authenticator proxyAuthenticator() {
		if (_user == null) {
			return null;
		}
		return new Authenticator() {
									 @Override
									 protected PasswordAuthentication getPasswordAuthentication() {
										  if (getRequestorType() != RequestorType.PROXY) {
									            return null;   // We only handle proxy authentication here
									      }
										  return new PasswordAuthentication(_user.asString(),
												                            _password != null ? _password.asString().toCharArray() : null);
									  }
								   };
	}
	/**
	 * Proxy Authenticator Header
	 * This can be used as an alternative of Authenticator
	 * @return Authenticator
	 */
	public HttpHeader  proxyAuthenticatorHeader() {
		String encoded = new String(Base64.getEncoder().encode(Strings.customized("{}:{}", _user.asString(),
				                                                                           _password.asString()).getBytes()));
		HttpHeader proxyAuthenticatorHeader = new HttpHeader(HttpHeaderID.forId("Proxy-Authorization"),
				                                                                "Basic " + encoded);

		return proxyAuthenticatorHeader;
	}

	public ProxySelector  proxySelector() {
		InetSocketAddress proxyHost = new InetSocketAddress( _proxyHost.asString(),
				                                             _proxyPort);
		return ProxySelector.of(proxyHost);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		String outDbg = null;
		if (_enabled) {
			outDbg = Strings.customized("ENABLED [{}:{} {}/{}]",
										_proxyHost,_proxyPort,
										_user,_password);
		} else {
			outDbg = "DISABLED";
		}
		return outDbg;
	}
}
