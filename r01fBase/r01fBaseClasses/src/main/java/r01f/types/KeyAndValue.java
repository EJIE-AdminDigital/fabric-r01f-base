package r01f.types;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.stream.Streams;

import r01f.objectstreamer.annotations.MarshallField;

public class KeyAndValue<K,V>
  implements Map.Entry<K,V> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="key")
	private K _key;

	@MarshallField(as="value")
	private V _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public KeyAndValue() {
		// default no-args constructor
	}
	public KeyAndValue(final K key,final V value) {
		_key = key;
		_value = value;
	}
	public static <K,V> KeyAndValue<K,V> of(final K key,final V value) {
		return new KeyAndValue<>(key,value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTER & SETTER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public K getKey() {
		return _key;
	}
	public void setKey(final K key) {
		// this method is NOT in the Map.Entry interface :-)
		_key = key;
	}
	@Override
	public V getValue() {
		return _value;
	}
	@Override
	public V setValue(final V value) {
		V prevValue = _value;
		_value = value;
		return prevValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	public static <K,V> Map<K,V> toMap(final KeyAndValue<K,V>... keyAndValues) {
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
