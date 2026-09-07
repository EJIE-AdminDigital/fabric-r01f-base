package r01f.types;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.stream.Streams;

public record KeyAndValue<K,V>(K key, V value)
   implements Map.Entry<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> KeyAndValue<K,V> of(final K key,final V value) {
		return new KeyAndValue<>(key,value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTER & SETTER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public K getKey() {
		return this.key;
	}
	@Override
	public V getValue() {
		return this.value;
	}
	@Override
	public V setValue(final V value) {
		throw new UnsupportedOperationException("KeyAndValue is immutable");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> Map<K,V> toMap(@SuppressWarnings("unchecked") final KeyAndValue<K,V>... keyAndValues) {
		return Streams.of(keyAndValues)
					  .collect(Collectors.toMap(kv -> kv.getKey(),
					  					  		kv -> kv.getValue()));
	}
	public static <K,V> Map<K,V> toMap(final Iterable<KeyAndValue<K,V>> keyAndValues) {
		return Streams.of(keyAndValues)
					  .collect(Collectors.toMap(kv -> kv.getKey(),
					  					  		kv -> kv.getValue()));
	}
}
