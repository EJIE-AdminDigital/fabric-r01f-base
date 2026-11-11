package r01f.crypto;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;

import lombok.Getter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class CryptoKeyPair
  implements Serializable {
	private static final long serialVersionUID = 3934912904861805359L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final PublicKey _publicKey;		
	@Getter private final PrivateKey _privateKey;
	@Getter private final byte[] _publicKeyFingerPrint;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CryptoKeyPair(final PublicKey publicKey,final PrivateKey privateKey,
						 final byte[] publicKeyFingerPrint) {
		_publicKey = publicKey;
		_privateKey = privateKey;
		_publicKeyFingerPrint = publicKeyFingerPrint;
	}
	public CryptoKeyPair(final PublicKey publicKey,final PrivateKey privateKey) {
			this(publicKey,privateKey,
				 null);	// no fingerprint
	}
	public CryptoKeyPair(final CryptoKeyPairSerialized cryptoKeyPairSerialized) {
		// public key
		KeySpec publicKeySpec = cryptoKeyPairSerialized.getPublicTokenAsKeySpec();
		RSAPublicKey publicKey = CryptoKeys.rsaKeyFromKeySpec(publicKeySpec,
															  CryptoKeys.PUBLIC_KEY_FROM_SPEC_FACTORY);
		
		// private key
		KeySpec privateKeySpec = cryptoKeyPairSerialized.getPrivateTokenAsKeySpec();
		RSAPrivateKey privateKey = CryptoKeys.rsaKeyFromKeySpec(privateKeySpec,
															  	CryptoKeys.PRIVATE_KEY_FROM_SPEC_FACTORY);
		// builde
		_publicKey = publicKey;
		_privateKey = privateKey;
		_publicKeyFingerPrint = cryptoKeyPairSerialized.getFingerPrint() != null ? cryptoKeyPairSerialized.getFingerPrint().getBytes() : null;
	}
}

