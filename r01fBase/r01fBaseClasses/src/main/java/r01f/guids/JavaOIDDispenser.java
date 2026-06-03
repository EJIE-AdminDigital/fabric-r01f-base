package r01f.guids;

import java.util.Random;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JavaOIDDispenser {
	/**
	 * Generates a GUID
	 * @return 
	 */
	public static String generateGUID() {
		UUID uuid = UUID.randomUUID();	
        return uuid.toString().toUpperCase();
	}
	/**
	 * Generates a GUID
	 * @return
	 */
	public static String supplyOid() {
		return JavaOIDDispenser.generateGUID();
	}
	public static String supplyShortOid() {
		return JavaOIDDispenser.randomFragment(JavaOIDDispenser.supplyOid());	// a random fragment of a full oid
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the oid's fragment at the given position asumming that the
	 * oid has the java's UUID format: b8920e99-b116-4de6-88bc-d45ee8d0e143
	 * @param oid
	 * @param pos
	 * @return
	 */
	public static String fragmentAt(final String oid,final int pos) {
		String[] items = oid.split("-");
		if (pos < 0 || pos >= items.length) throw new IllegalArgumentException("the given pos=" + pos + " is NOT valid: it must be contained in [0.." + (items.length - 1) + "]");
		return items[pos];
	}
	public static String randomFragment(final String oid) {
		String[] items = oid.split("-");
		Random randomNum = new Random(System.currentTimeMillis());
		int pos = 0 + randomNum.nextInt(items.length-1);
		return items[pos];
	}
}
