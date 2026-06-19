package r01f.crypto;

import java.security.spec.KeySpec;

/**
 * Create keys using:
 * <pre>
 * 		ApiKeyPairBuilder.createRSA()
 * </pre>
 */
public interface CryptoKeySpecFactory {

	public KeySpec from(final byte[] byteArray);

}
