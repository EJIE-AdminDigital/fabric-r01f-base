package r01f.crypto;

import java.io.Serializable;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.util.types.StringEncodeUtils;


@MarshallType(as="cryptoKeyPair")
@Accessors(prefix="_")
public class CryptoKeyPairSerialized
  implements Serializable {
	private static final long serialVersionUID = 3934912904861805359L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="publicKey",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final SecurityToken _publicKey;

	@MarshallField(as="privateKey",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final SecurityToken _privateKey;

	@MarshallField(as="fingerPrint",
			  	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final SecurityToken _fingerPrint; // The fingerprint (https://en.wikipedia.org/wiki/Public_key_fingerprint)
	                                                  // if provided can be util to perform some operations replacing a very long value pk.
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public CryptoKeyPairSerialized(@MarshallFrom("publicKey")   final SecurityToken publicKey,
			             		   @MarshallFrom("privateKey")  final SecurityToken privateKey,
			             		   @MarshallFrom("fingerPrint") final SecurityToken fingerPrint) {

		_publicKey =   publicKey;
		_privateKey =  privateKey;
		_fingerPrint = fingerPrint;
	}
	public CryptoKeyPairSerialized(final SecurityToken publicKey) {
		_publicKey = publicKey;
		_privateKey = null;
		_fingerPrint = null;
	}
	public CryptoKeyPairSerialized(final CryptoKeyPairSerialized other) {
		_publicKey = other.getPublicKey();
		_privateKey = other.getPrivateKey();
		_fingerPrint = other.getFingerPrint();

	}
	public CryptoKeyPairSerialized(final CryptoKeyPair keyPair) {
		this(keyPair.getPublicKey(),keyPair.getPrivateKey(),keyPair.getPublicKeyFingerPrint());
	}
	public CryptoKeyPairSerialized(final RSAPublicKey publicKey,final RSAPrivateKey privateKey,final byte[] fingerPrint) {
		// The public key,
		byte[] publicKeyBytes = publicKey.getEncoded();
		_publicKey = SecurityToken.from(StringEncodeUtils.encodeBase64AsString(publicKeyBytes));
        // The private key
		byte[] privateKeyBytes = privateKey.getEncoded();
		_privateKey = SecurityToken.from(StringEncodeUtils.encodeBase64AsString(privateKeyBytes));
        // The fingerprint
		_fingerPrint = SecurityToken.from(StringEncodeUtils.encodeHex(fingerPrint));
	}
	public static CryptoKeyPairSerialized serialize(final RSAPublicKey publicKey,final RSAPrivateKey privateKey,final byte[] fingerPrint) {
		return new CryptoKeyPairSerialized(publicKey,privateKey,fingerPrint);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	As KeySpec
/////////////////////////////////////////////////////////////////////////////////////////
	public KeySpec getPublicTokenAsKeySpec() {
		return CryptoKeys.keySpecFromSecurityToken(_publicKey,
												   CryptoKeys.PUBLIC_KEY_SPEC_FACTORY);
	}
	public KeySpec getPrivateTokenAsKeySpec() {
		return CryptoKeys.keySpecFromSecurityToken(_privateKey,
												   CryptoKeys.PRIVATE_KEY_SPEC_FACTORY);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public LoginID asLoginId() {
		if (_fingerPrint == null) {
			throw new IllegalArgumentException(" No fingerprint provided to use as LoginID");
		}
		return LoginID.forId(_fingerPrint.asString());
	}
}

