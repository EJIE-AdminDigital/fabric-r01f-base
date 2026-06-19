package r01f.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates digests
 */
@Slf4j
public class FileDigest {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String DIGEST_ALG = "SHA-256";
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static DigestInputStream decorate(final InputStream data) {
		DigestInputStream dIS = null;
		try {
			if (data != null) {
				dIS = new DigestInputStream(data,
											MessageDigest.getInstance(DIGEST_ALG));
			}
			return dIS;
		} catch (NoSuchAlgorithmException nsAlgEx) {
			log.error("Could NOT create a digest (the {} algorithm is NOT available): {}",DIGEST_ALG,nsAlgEx.getMessage(),nsAlgEx);
			nsAlgEx.printStackTrace(System.out);
		}
		return null;
	}
	public static DigestOutputStream decorate(final OutputStream data) {
		
		DigestOutputStream dOS = null;
		try {
			if (data != null) {
				dOS = new DigestOutputStream(data,
											 MessageDigest.getInstance(DIGEST_ALG));
			}
			return dOS;
		} catch (NoSuchAlgorithmException nsAlgEx) {
			log.error("Could NOT create a digest (the {} algorithm is NOT available): {}",DIGEST_ALG,nsAlgEx.getMessage(),nsAlgEx);
			nsAlgEx.printStackTrace(System.out);
		}
		return null;
	}
	public static String createHexEncodedFor(final MessageDigest digest) {
		return HexFormat.of()
						.formatHex(digest.digest());
	}
}
