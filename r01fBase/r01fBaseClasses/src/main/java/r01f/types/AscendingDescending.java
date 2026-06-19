package r01f.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;

/**
 * An enum for ascending / descending values to be used when setting ordering
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public enum AscendingDescending
 implements EnumWithCode<String,AscendingDescending> {
	ASCENDING("ASC"),
	DESCENDING("DESC");
	
	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
	
	public AscendingDescending getOpposite() {
		AscendingDescending opposite = null;
		switch (this) {
			case ASCENDING:
				opposite = DESCENDING;
				break;
			case DESCENDING:
				opposite = ASCENDING;
				break;
			default:
		}
		return opposite;
	}
	public boolean isAscending() {
		return this == ASCENDING;
	}
	public boolean isDescending() {
		return this == DESCENDING;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String el) {
		return EnumWithCode.canBeFromCode(el,AscendingDescending.class);
	}
	public static AscendingDescending fromCode(final String code) {
		return EnumWithCode.fromCode(code,AscendingDescending.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + AscendingDescending.class + " element with code=" + code));
	}
}
