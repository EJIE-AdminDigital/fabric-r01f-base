package r01f.util.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumWithCode;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum TemperatureUnit
 implements EnumWithCode<String,TemperatureUnit> {
	CELSIUS("C"),
	FARENHEIT("F");

	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static TemperatureUnit from(final String code) {
		return EnumWithCode.fromCode(code,TemperatureUnit.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + TemperatureUnit.class + " with code=" + code));
	}
	public static TemperatureUnit fromName(final String name) {
		return EnumExtended.fromName(name,TemperatureUnit.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + TemperatureUnit.class + " with name=" + name));
	}
}
