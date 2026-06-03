package r01f.properties;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.util.OSType;

/**
 * Creates a holder type for a property's values by environment
 * <pre class="brush:java">
 * 		new PropertyDefaultValueByOS<T>()
 * 				.when(OSType.LINUX).use(value)
 * 				.when(OSType.WIN).use(otherValue)
 * 				.defaultValue(defaultValue)		// used when os is NOT any of the above
 * </pre>
 * @param <T>
 */
public class PropertyDefaultValueByOS<T>
  implements Serializable {

	private static final long serialVersionUID = -3506658494769980549L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<OSType,T> _envValues = Maps.newLinkedHashMapWithExpectedSize(2);	// linux win

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property value for the given env
	 * @param env
	 * @return
	 */
	public T getFor(final OSType env) {
		T outVal = _envValues.get(env);
		if (outVal == null) outVal = _envValues.get(OSType.UNKNOWN);	// try the default value
		return outVal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PropertyDefaultValueByOS<T> defaultValue(final T value) {
		return this.when(OSType.UNKNOWN)
				   .use(value);
	}
	public PropertyDefaultValueByOSBuilderValStep when(final OSType env) {
		return new PropertyDefaultValueByOSBuilderValStep(env);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PropertyDefaultValueByOSBuilderValStep {
		private final OSType _os;

		public PropertyDefaultValueByOS<T> use(final T value) {
			if (_os != null && value != null) PropertyDefaultValueByOS.this._envValues.put(_os,value);
			return PropertyDefaultValueByOS.this;
		}
	}
}
