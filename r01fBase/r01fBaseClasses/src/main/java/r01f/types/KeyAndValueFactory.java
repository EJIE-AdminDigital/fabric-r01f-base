package r01f.types;

/**
 * Factory of a {@link KeyAndValue} type
 * @param <K> key type
 * @param <V> value type
 */
public interface KeyAndValueFactory<K,V> {
	public KeyAndValue<K,V> createFor(final K name,final V value);
}
