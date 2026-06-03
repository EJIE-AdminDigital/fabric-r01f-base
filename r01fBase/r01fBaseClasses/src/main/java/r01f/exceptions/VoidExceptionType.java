package r01f.exceptions;

import lombok.experimental.Accessors;

/**
 * Exception type for exceptions that do not have a sub-type
 */
@Accessors(prefix="_")
     class VoidExceptionType
   extends EnrichedThrowableTypeBase {

	private static final long serialVersionUID = 8214769358887843776L;
	/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public VoidExceptionType() {
		super("VOID",
			  -1,-1,
			  ExceptionSeverity.DEFAULT);
	}
	public static VoidExceptionType create() {
		return new VoidExceptionType();
	}
}
