package r01f.types;

/**
 * Triplet of objects, can be used for returned three values in one method.
 * @param <K>
 * @param <V>
 * @param <S>
 */
public record Triplet<K,V,S>(K k,V v,S s) {
	public static <K,V,S> Triplet<K,V,S> from(final K element0,final V element1,final S element2) {
		return new Triplet<>(element0,element1,element2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public K getK() {
		return this.k();
	}
	public V getV() {
		return this.v();
	}
	public S getS() {
		return this.s();
	}
}
