package r01f.crypto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum KeySpecPKCS
 implements EnumWithCode<String, KeySpecPKCS> {
	PKCS1("PKCS1"),
	PKCS8("PKCS8");

	@Getter private final  String _code;
	@Getter private final  Class<String>  _codeType = String.class;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final KeySpecPKCS fromString(final String code) {
		return EnumWithCode.fromCode(code,KeySpecPKCS.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + KeySpecPKCS.class + " element with code=" + code));
	}
}


