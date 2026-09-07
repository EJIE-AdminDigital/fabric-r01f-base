package r01f.xmlproperties;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.guids.CommonOIDs.IsAppComponent;
import r01f.properties.PropertiesForAppComponentPropertiesLoadedListener;

/**
 * see {@link XMLProperties}
 */
@Slf4j
     class XMLPropertiesImpl
implements XMLProperties {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int DEF_APP_NUM = 15;
	public static final float APP_CACHE_MAP_LOAD_FACTOR = 0.5F;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The environment (dev,prod,etc)
	 */
	private final Environment _env;
	/**
	 * true if no properties cache is used
	 */
	private final boolean _useCache;
	/**
	 * Cache of {@link XMLPropertiesForApp} for every managed app
	 * the {@link XMLPropertiesForApp} also have another cache (an instance of {@link XMLPropertiesCache})
	 */
	private final Map<IsAppCode,XMLPropertiesForApp> _appPropsCache;
	/**
	 * A listener for AppComponent properties loaded event
	 */
	private final PropertiesForAppComponentPropertiesLoadedListener _appComponentLoadedListener;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesImpl() {
		this((Environment)null,	// default environment (guessed from system env prop)
			 true,	// use cache
			 null);	// no listener
	}
	public XMLPropertiesImpl(final Environment env) {
		this(env,	// default environment (guessed from system env prop)
			 true,	// use cache
			 null);	// no listener
	}
	public XMLPropertiesImpl(final boolean useCache) {
		this((Environment)null,	// default environment (guessed from system env prop)
			 useCache,
			 null);
	}
	public XMLPropertiesImpl(final Environment env,
							 final boolean useCache) {
		this(env,	// default environment (guessed from system env prop)
			 useCache,
			 null);
	}
	public XMLPropertiesImpl(final Environment env,
							 final boolean useCache,
							 final PropertiesForAppComponentPropertiesLoadedListener appCompPropertiesLoadedListener) {
		log.debug("XMLProperties BootStraping");
		_env = env;
		_useCache = useCache;
		_appPropsCache = useCache ? new HashMap<>(DEF_APP_NUM,
												  APP_CACHE_MAP_LOAD_FACTOR)
								  : null;
		_appComponentLoadedListener = appCompPropertiesLoadedListener;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public XMLPropertiesForApp forApp(final IsAppCode appCode) {
		XMLPropertiesForApp propsForApp = _useCache ? _appPropsCache != null ? _appPropsCache.get(appCode)
																 			 : null
													: null;
		if (propsForApp == null) {
			log.warn("The properties for application {} are not present in the cache or the cache is not loaded: they must be loaded NOW",appCode);
			propsForApp = new XMLPropertiesForAppImpl(_env != null ? _env
																   : XMLPropertiesEnv.guessEnvironmentFromSystemEnvProp(),
													  appCode,
													  _appComponentLoadedListener,
													  _useCache);
			if (_useCache) _appPropsCache.put(appCode,propsForApp);
		}
		return propsForApp;
	}
	@Override
	public XMLPropertiesForAppComponent forAppComponent(final IsAppCode appCode,final IsAppComponent component) {
		XMLPropertiesForApp propsForApp = this.forApp(appCode);
		return propsForApp.forComponent(component);
	}
	@Override
	public XMLPropertiesForAppComponent forAppComponent(final String appCode,final String component) {
		return this.forAppComponent(AppCode.forId(appCode),AppComponent.forId(component));
	}
}
