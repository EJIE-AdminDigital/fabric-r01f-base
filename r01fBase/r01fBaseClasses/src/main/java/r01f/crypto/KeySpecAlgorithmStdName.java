package r01f.crypto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum KeySpecAlgorithmStdName // ...Note, this mostly is used at ecdsa key generation
     implements EnumWithCode<String, KeySpecAlgorithmStdName> {
	// ECDSA
	//-------------------
	ES256 ("secp256r1"),   //ECDSA using P-256 (secp256r1) curve and SHA-256 hash algorithm
	ES512("secp521r1");   // ECDSA using P-521 curve and SHA-512 hash algorithm (optional).

	@Getter private final  String _code;
	@Getter private final  Class<String>  _codeType = String.class;

	@Override
	public String  toString() {
		return _code;
	}
}


