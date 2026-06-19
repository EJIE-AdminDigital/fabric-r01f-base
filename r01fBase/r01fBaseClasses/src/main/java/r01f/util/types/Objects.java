package r01f.util.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;


public class Objects {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Basic equals and type checkings
	 * @param o1
	 * @param o2
	 * @return
	 */
	@SuppressWarnings("null")
	public static boolean sameObjectOrSameType(final Object o1,final Object o2) {
		if (o1 == o2) return true;
		if (o1 == null && o2 == null) return true;
		if (o1 != null && o2 == null) return false;
		if (o1 == null && o2 != null) return false;
		return o1.getClass() == o2.getClass();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NULL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if there's any not null object in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean isAnyNotNull(final Object... objects) {
		if (objects == null || objects.length == 0) throw new IllegalArgumentException();
		boolean outAnyNotNull = false;
		for (Object obj : objects) {
			if (obj != null) {
				outAnyNotNull = true;
				break;
			}
		}
		return outAnyNotNull;
	}
	/**
	 * Checks if all objects are all null in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean areAllNull(final Object... objects) {
		return !Objects.isAnyNotNull(objects);
	}
	/**
	 * Checks if all objects are all NOT null in the provided collection
	 * @param objects
	 * @return
	 */
	public static boolean areAllNOTNull(final Object... objects) {
		if (objects == null || objects.length == 0) throw new IllegalArgumentException();
		boolean areAllNotNull = true;
		for (Object obj : objects) {
			if (obj == null) {
				areAllNotNull = false;
				break;
			}
		}
		return areAllNotNull;
	}
    /**
     * Equivalent to java.util.Objects.nonNull but here for consistency with the rest of the methods in this class
     * @param obj
     * @return
     */
    public static boolean nonNull(final Object obj) {
        return obj != null;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUAL
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> boolean areEqual(final T o1,final T o2,
								   	   final BiFunction<T,T,Boolean> eqsFunc) {
		return o1 == o2 ? true
						: o1 != null && o2 != null
								? eqsFunc.apply(o1,o2)
								: o1 != null ? false
											 : o2 != null ? false
												   		  : true;	// both null
	}
	public static <T> boolean areEqual(final Collection<T> c1,final Collection<T> c2,
									   final BiFunction<T,T,Boolean> eqsFunc) {
		if (c1 == c2) return true;
		if (c1 == null && c2 == null) return true;
		if ((c1 != null && c2 == null) || (c1 == null && c2 != null)) return false;

		// the "canonical" way to compare two collections would be;
		// A) using guava: return HashMultiset.create(c1).equals(HashMultiset.create(c2));
		// B) using apache commons: CollectionUtils#isEqualCollection
		// ... but here an equals function is used
		boolean outEqs = true;
		if (c1.size() == c2.size()) {
			Map<T,Integer> mapA = _getCardinalityMap(c1,eqsFunc);
			Map<T,Integer> mapB = _getCardinalityMap(c2,eqsFunc);
			if (mapA.size() == mapA.size()) {
				Iterator<T> it = mapA.keySet().iterator();
				while (it.hasNext()) {
					T obj = it.next();
					Integer countInA = mapA.get(obj);
					Integer countInB = mapB.get(obj);
					if ((countInA != null && countInB != null && !countInA.equals(countInB))
					  ||(countInA != null && countInB == null)
					  ||(countInA == null && countInB != null)) {
						outEqs = false;
						break;
					}
				}
			} else {
				outEqs = false;
			}
		} else {
			outEqs = false;
		}
		return outEqs;
	}
	private static <T> Map<T,Integer> _getCardinalityMap(final Collection<T> col,
														 final BiFunction<T,T,Boolean> eqsFunc) {
		Map<T,Integer> count = new HashMap<>();
		for (Iterator<T> it = col.iterator(); it.hasNext();) {
			T obj = it.next();
			Map.Entry<T,Integer> existing = count.entrySet()
												 .stream()
												 .filter(me -> eqsFunc.apply(me.getKey(),obj))
												 .findFirst()
												 .orElse(null);
			if (existing == null) {
				count.put(obj,1);
			} else {
				count.put(obj,Integer.valueOf(existing.getValue().intValue() + 1));
			}
		}
		return count;
	}
	public static <K,V> boolean areEqual(final Map<K,V> m1,final Map<K,V> m2,
									     final BiFunction<K,K,Boolean> keyEqsFunc,
									     final BiFunction<V,V,Boolean> valueEqsFunc) {
		if (m1 == m2) return true;
		if (m1 == null && m2 == null) return true;
		if ((m1 != null && m2 == null) || (m1 == null && m2 != null)) return false;

		// are keys equal?
		Collection<K> keys1 = m1.keySet();
		Collection<K> keys2 = m2.keySet();
		boolean keysEqs = Objects.areEqual(keys1,keys2,
										   keyEqsFunc);
		// are values equal?
		Collection<V> values1 = m1.values();
		Collection<V> values2 = m2.values();
		boolean valuesEqs = Objects.areEqual(values1,values2,
											 valueEqsFunc);

		return keysEqs && valuesEqs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HASH
/////////////////////////////////////////////////////////////////////////////////////////
	public static int hash(final Object... objs) {
		return java.util.Objects.hash(objs);
	}
	public static int hashCode(final Object... objs) {
		return java.util.Objects.hash(objs);
	}
}
