package r01f.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.oids.SecurityIDs.LoginID;
import r01f.oids.SecurityIDs.Password;
import r01f.types.IsPath;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Loads a file from an URL via HTTP
 */
@Accessors(prefix="_")
public class ResourcesLoaderFromURL
	 extends ResourcesLoaderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final String PROXY_HOST_PROP = "proxyHost";
	static final String PROXY_PORT_PROP = "proxyPort";
	static final String PROXY_USER_PROP = "proxyUser";
	static final String PROXY_PASSWORD_PROP = "proxyPassword";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private String _proxyHost;
	@Getter @Setter private String _proxyPort;
	@Getter @Setter private LoginID _proxyUser;
	@Getter @Setter private Password _proxyPassword;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	ResourcesLoaderFromURL(final ResourcesLoaderDef def) {
		super(def);
		if (CollectionUtils.hasData(def.getLoaderProps())) {
			String proxyHost = def.getProperty(PROXY_HOST_PROP);
			if (!Strings.isNullOrEmpty(proxyHost)) {
				_proxyHost = proxyHost;

				String proxyPort = def.getProperty(PROXY_PORT_PROP);
				_proxyPort = !Strings.isNullOrEmpty(proxyPort) ? proxyPort : "8080";

				String proxyUser = def.getProperty(PROXY_USER_PROP);
				String proxyPwd = def.getProperty(PROXY_PASSWORD_PROP);
				_proxyUser = !Strings.isNullOrEmpty(proxyUser) ? LoginID.forId(proxyUser) : null;
				_proxyPassword = !Strings.isNullOrEmpty(proxyPwd) ? Password.forId(proxyPwd) : null;
			}
		}
	}
	@Override
	boolean _checkProperties(final Map<String,String> props) {
		boolean outOK = true;	// none of the properties are mandatory
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected InputStream _doGetInputStream(final IsPath resourceUrl,
											final boolean reload) throws IOException {
		try {
			//.. check proxy
			boolean useProxy = !Strings.isNullOrEmpty(_proxyHost);
			// ..compose request
			HttpRequest request = HttpRequest.newBuilder()
			   		  							.uri(URI.create(resourceUrl.asAbsoluteString()))
			   		  							.GET()
			   		  						 .build();
			// .. http builder
			HttpClient.Builder builder = HttpClient.newBuilder()
													.connectTimeout(Duration.ofSeconds(10));
			  // .. check proxy
			if (useProxy) {
				builder.proxy(ProxySelector.of(new InetSocketAddress(_proxyHost, Integer.parseInt(_proxyHost))));
			  	Authenticator authenticator = Authenticator.getDefault();
					if (_proxyUser != null && _proxyPassword != null) {
						authenticator = new Authenticator() {
															@Override
															protected PasswordAuthentication getPasswordAuthentication() {
															   return new PasswordAuthentication(_proxyUser.asString(), _proxyPassword.toCharArray());
															}
														};
						}
						builder.authenticator(authenticator);
					  }
			HttpResponse<InputStream> streamResponse = builder.build()
															   .send(request,
																	 HttpResponse.BodyHandlers.ofInputStream());

			// return the response
			int statusCode = streamResponse.statusCode();
			if (statusCode >= 200 && statusCode < 300) {
				return streamResponse.body();
			}
			throw new IOException("Unexpected response status: " + statusCode);

		} catch (final Throwable ioEx) {
			StringBuilder msg = new StringBuilder("Error when loading a resource from the url: " + resourceUrl);
			if (_proxyHost != null) {
				msg.append("(");
				msg.append(" proxy host:port=" + _proxyHost + ":" + _proxyPort);
				msg.append(" proxy usr/pwd=" + _proxyUser + "/" + _proxyPassword);
				msg.append(")");
			}
			throw new IOException(msg + " > " + ioEx.getMessage());
		}
	}
}
