package r01f.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;


@RequiredArgsConstructor
@Accessors(prefix="_")
public enum KeySpecAlgorithmFamily
 implements EnumWithCode<String, KeySpecAlgorithmFamily> {
	RSA ("RSA"),
	ECDSA ("EC");

	@Getter private final  String _code;
	@Getter private final  Class<String>  _codeType = String.class;
	
	@Override
	public String  toString() {
		return _code;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final KeySpecAlgorithmFamily fromString(final String code) {
		return EnumWithCode.fromCode(code,KeySpecAlgorithmFamily.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + KeySpecAlgorithmFamily.class + " element with code=" + code));
	}
	public static KeySpecAlgorithmFamily forId(final String id) {
		return  KeySpecAlgorithmFamily.fromString(id);
	}
	public static final boolean canBe(final String code) {
		return EnumWithCode.canBeFromCode(code,KeySpecAlgorithmFamily.class);
	}
}


