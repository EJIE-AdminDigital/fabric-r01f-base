package r01f.crypto;

import java.security.interfaces.RSAKey;
import java.security.spec.KeySpec;

import r01f.patterns.FactoryFrom;

/**
 * Create keys using:
 * <pre>
 * 		ApiKeyPairBuilder.createRSA()
 * </pre>
 */
public interface CryptoKeyFactoryFromSpec<K extends RSAKey>
		 extends FactoryFrom<KeySpec,K> {
	// just extends
}
