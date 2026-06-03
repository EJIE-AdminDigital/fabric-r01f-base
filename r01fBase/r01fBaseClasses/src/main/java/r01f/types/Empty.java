package r01f.types;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * {@link Void} type alternative.
 *
 * {@link Void}  is problematic when as used as a type parameter because of its association with {@code null},
 * as it cannot be instantiated, and extensive null-checking is in place.
 * 
 * Instead, the {@link Empty} type can be used.
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class Empty {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Empty INSTANCE = new Empty();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * An instance of the Empty singleton.
	 * @return the Empty instance
	 */
	public static Empty getInstance() {
		return INSTANCE;
	}
}
