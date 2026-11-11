package r01f.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.patterns.ProviderFrom;
import r01f.util.types.Strings;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CryptoKeyPairBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final int DEF_KEY_SIZE = 2048;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeyPairSerialized createRSA() {
		return CryptoKeyPairBuilder.createRSA(DEF_KEY_SIZE,
											  null);
	}
	public static CryptoKeyPairSerialized createRSA(final ProviderFrom<RSAPublicKey,byte[]> fingerPrintProvider) {
		return CryptoKeyPairBuilder.createRSA(DEF_KEY_SIZE,
											  fingerPrintProvider);
	}
	public static CryptoKeyPairSerialized createRSA(final int size) {
				return CryptoKeyPairBuilder.createRSA(DEF_KEY_SIZE);
	}
	public static CryptoKeyPairSerialized createRSA(final int size,
										  			final ProviderFrom<RSAPublicKey,byte[]> fingerPrintProvider) {
		// generate keys
		CryptoKeyPair keyPair = CryptoKeyPairBuilder.createRSAKeyPair(size,
																	  fingerPrintProvider);
		// transform
		CryptoKeyPairSerialized outKeyPair = new CryptoKeyPairSerialized(keyPair);

		return outKeyPair;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER 2
/////////////////////////////////////////////////////////////////////////////////////////
	public static CryptoKeyPair createRSAKeyPair() {
		return CryptoKeyPairBuilder.createRSAKeyPair(DEF_KEY_SIZE);
	}
	public static CryptoKeyPair createRSAKeyPair(final ProviderFrom<RSAPublicKey,byte[]> fingerPrintProvider) {
		return CryptoKeyPairBuilder.createRSAKeyPair(DEF_KEY_SIZE,
													 fingerPrintProvider);
	}
	public static CryptoKeyPair createRSAKeyPair(final int size) {
		return CryptoKeyPairBuilder.createRSAKeyPair(size,
													 null);
	}
	public static CryptoKeyPair createRSAKeyPair(final int size,
												 final ProviderFrom<RSAPublicKey,byte[]> fingerPrintProvider) {
		try {
			KeyPairGenerator keyGenerator;
			keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(size);
            // Generate public ky & private key,
			KeyPair kp = keyGenerator.genKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey)kp.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
			
			// generate fingerprint
	        // Keccak256 based hash fingerprint ( Keccak256 popular and modern hash algorithm provides no very long hash )
			byte[] publicKeyFingerPrint = fingerPrintProvider != null ? fingerPrintProvider.from(publicKey) : null;

			// return
			return new CryptoKeyPair(publicKey,privateKey,publicKeyFingerPrint);
		} catch (final NoSuchAlgorithmException nsalgEx) {
			throw new IllegalArgumentException(Strings.customized("Error creating [api key pair]: {}",
																  nsalgEx.getLocalizedMessage()),
											   nsalgEx);
		}
	}
}
