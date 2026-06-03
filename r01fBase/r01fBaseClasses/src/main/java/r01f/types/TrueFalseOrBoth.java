package r01f.types;

import r01f.enums.EnumExtended;


public enum TrueFalseOrBoth 
 implements EnumExtended<TrueFalseOrBoth> {
	TRUE,
	FALSE,
	BOTH;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isTrue() {
		return this == TRUE;
	}
	public boolean isFalse() {
		return this == FALSE;
	}
	public boolean isBoth() {
		return this == BOTH;
	}
	public Boolean asBoolean() {
		return switch (this) {
			   case TRUE -> true;
			   case FALSE -> false;
			   case BOTH -> null;
			   };
	}
}
