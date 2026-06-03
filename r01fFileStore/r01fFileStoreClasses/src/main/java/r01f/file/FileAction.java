package r01f.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.enums.EnumWithCode;

@Accessors(prefix = "_")
@RequiredArgsConstructor
public enum FileAction 
 implements EnumWithCode<String,FileAction>,
 			Debuggable {
	// POSIX style
	NONE("---"), 
	EXECUTE("--x"),
	WRITE("-w-"),
	WRITE_EXECUTE("-wx"),
	READ("r--"),
	READ_EXECUTE("r-x"), 
	READ_WRITE("rw-"),
	ALL("rwx");
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/** Retain reference to value array. */
	private final static FileAction[] VALS = values();

	/** Symbolic representation */
	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return true if this action implies that action.
	 * 
	 * @param that
	 */
	public boolean implies(final FileAction that) {
		if (that != null) {
			return (ordinal() & that.ordinal()) == that.ordinal();
		}
		return false;
	}
	/** AND operation. */
	public FileAction and(final FileAction that) {
		return VALS[ordinal() & that.ordinal()];
	}
	/** OR operation. */
	public FileAction or(final FileAction that) {
		return VALS[ordinal() | that.ordinal()];
	}
	/** NOT operation. */
	public FileAction not() {
		return VALS[7 - ordinal()];
	}
	public String getSymbol() {
		return _code;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the FsAction enum for String representation of permissions
	 * @param permission 3-character string representation of permission. ex: rwx
	 * @return Returns FsAction enum if the corresponding FsAction exists for
	 *         permission. Otherwise returns null
	 */
	public static FileAction getFsAction(final String permission) {
		for (FileAction fsAction : VALS) {
			if (fsAction.getSymbol().equals(permission)) {
				return fsAction;
			}
		}
		return null;
	}
	public static FileAction fromCode(final String code) {
		return EnumWithCode.fromCode(code,FileAction.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + FileAction.class + " element with code=" + code));
	}
	public static FileAction fromSymbol(final String symbol) {
		return FileAction.fromCode(symbol);
	}
	public static FileAction fromUnix(final String symbol) {
		return FileAction.fromSymbol(symbol);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return this.getSymbol();
	}
}
