package r01f.properties;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.internal.Env;
import r01f.oids.CommonOIDs.Environment;

/**
 * Creates a holder type for a property's values by environment
 * <pre class="brush:java">
 * 		new PropertyDefaultValueByEnv<T>()
 * 				.when(Environment.forId("dev").use(value)
 * 				.when(Environment.forId("test").use(otherValue)
 * 				.when(Environment.forId("prod").use(anotherValue)
 * 				.defaultValue(defaultValue)		// used when environment is NOT any of the above
 * </pre>
 * @param <T>
 */
public class PropertyDefaultValueByEnv<T>
  implements Serializable {

	private static final long serialVersionUID = -2961694705609027722L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Map<Environment,T> _envValues = Maps.newLinkedHashMapWithExpectedSize(4);	// des, int, test, prod

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property value for the given env
	 * @param env
	 * @return
	 */
	public T getFor(final Environment env) {
		T outVal = _envValues.get(env);
		if (outVal == null) outVal = _envValues.get(Environment.DEFAULT);	// try the default value
		return outVal;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PropertyDefaultValueByEnv<T> defaultValue(final T value) {
		return this.when(Environment.DEFAULT)
				   .use(value);
	}
	public PropertyDefaultValueByEnvBuilderValStep when(final Env env) {
		return this.when(env.getDefaultEnvironment());
	}
	public PropertyDefaultValueByEnvBuilderValStep when(final Environment env) {
		return new PropertyDefaultValueByEnvBuilderValStep(env);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PropertyDefaultValueByEnvBuilderValStep {
		private final Environment _env;

		public PropertyDefaultValueByEnv<T> use(final T value) {
			if (_env != null && value != null) PropertyDefaultValueByEnv.this._envValues.put(_env,value);
			return PropertyDefaultValueByEnv.this;
		}
	}
}
