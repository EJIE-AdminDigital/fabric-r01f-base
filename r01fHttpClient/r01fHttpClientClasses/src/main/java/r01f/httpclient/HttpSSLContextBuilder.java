package r01f.httpclient;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * Just SSL Context Builder for testing purposes ( trusting all certificates...)
 * @author PCI
 *
 */
public class HttpSSLContextBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static SSLContext buildSSlContextForUnstrustedCerts() {
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
		    sslContext.init(null, TRUST_MANAGER , new SecureRandom());
		    return sslContext;
		} catch (final Throwable e) {
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRUST MANAGER
/////////////////////////////////////////////////////////////////////////////////////////
	private static TrustManager[] TRUST_MANAGER = new TrustManager[] {
															new X509TrustManager() {
																	@Override
																	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
																		return null;
																	}
																	@Override
																	public void checkClientTrusted(final X509Certificate[] certs,
																								   final String authType) {
																		// nothing
																	}
																	@Override
																	public void checkServerTrusted(final X509Certificate[] certs,
																								   final String authType) {
																		// nothing
																	}
																}
												  };

}
