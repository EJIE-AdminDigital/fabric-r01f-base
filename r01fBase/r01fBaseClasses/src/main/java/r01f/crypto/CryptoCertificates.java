package r01f.crypto;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CryptoCertificates {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Load an X.509 certificate
	 * @param filename The path of the certificate
	 * */
	public static X509Certificate loadX509Certificate(final Path keyFilePath) throws IOException,
																					 CertificateException {
		try (FileInputStream fis = new FileInputStream(keyFilePath.asAbsoluteString());
			 BufferedInputStream bis = new BufferedInputStream(fis)) {

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate)cf.generateCertificate(bis);
		}
	}
}
