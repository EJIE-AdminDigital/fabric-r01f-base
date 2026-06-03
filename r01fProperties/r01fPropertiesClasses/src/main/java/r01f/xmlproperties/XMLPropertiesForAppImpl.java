package r01f.xmlproperties;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Environment;
import r01f.patterns.FactoryFrom;
import r01f.properties.PropertiesComponentDef;
import r01f.properties.PropertiesForAppComponentPropertiesLoadedListener;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xml.XMLDocumentBuilder;
import r01f.xml.XMLUtils;
import r01f.xml.merge.XMLMerger;

/**
 * Manages properties for an app, more specifically, all the app's components
 *
 * This type SHOULD NOT be used directly BUT using {@link XMLProperties} instead
 * (see {@link XMLProperties})
 */
@Slf4j
@Accessors(prefix="_")
public final class XMLPropertiesForAppImpl
		implements XMLPropertiesForApp {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int DEF_APP_COMPS_NUM = 10;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * App code
	 */
	@Getter private final AppCode _appCode;
	/**
	 * The environment
	 */
	@Getter private final Environment _environment;
	/**
	 * Listener of component loaded events
	 */
	@Getter private final PropertiesForAppComponentPropertiesLoadedListener _componentLoadedListener;
	/**
	 * true if properties are cached
	 */
	@Getter private final boolean _useCache;
	/**
	 * AppComponent cache
	 */
	@Getter private final Map<AppComponent,XMLPropertiesForAppComponentCache> _appComponentCache;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForAppImpl(final Environment environment,
								   final AppCode appCode,
								   final PropertiesForAppComponentPropertiesLoadedListener compLoadedListener,
								   final boolean useCache) {
		_appCode = appCode;
		_environment = environment;
		_componentLoadedListener = compLoadedListener;
		_useCache = useCache;
		_appComponentCache = useCache ? new HashMap<>(DEF_APP_COMPS_NUM,
													  XMLPropertiesImpl.APP_CACHE_MAP_LOAD_FACTOR)
									  : null;	// no cache
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public XMLPropertiesForAppComponent forComponent(final AppComponent component) {
		// [1] - get the component cache
		XMLPropertiesForAppComponentCache appCompsPropsCache = _retrieveComponent(component);
		// [2] - Create a XMLPropertyWrapper factory
		FactoryFrom<Path,XMLPropertyWrapper> xmlProperyWrapperFactory = xPath -> new XMLPropertyWrapperImpl(appCompsPropsCache,
																											xPath);
		// [3] - Create the XMLProperties
		return new XMLPropertiesForAppComponent(_appCode,component,
												xmlProperyWrapperFactory);
	}
	@Override
	public XMLPropertiesForAppComponent forComponentProperties(final AppComponent component,
															   final Document xmlDoc) {
		// [1] - get the component cache
		XMLPropertiesForAppComponentCache appCompsPropsCache = _createAppComponentCache(component,
																						xmlDoc);
		// [2] - Create a XMLPropertyWrapper factory
		FactoryFrom<Path,XMLPropertyWrapper> xmlProperyWrapperFactory = xPath -> new XMLPropertyWrapperImpl(appCompsPropsCache,
																											xPath);
		// [3] - Create the XMLProperties
		return new XMLPropertiesForAppComponent(_appCode,component,
												xmlProperyWrapperFactory);
	}
	@Override
	public XMLPropertiesForAppComponentAccessor of(final AppComponent component) {
		XMLPropertiesForAppComponentCache appCompsPropsCache = _retrieveComponent(component);
		return appCompsPropsCache.getXMLDocumentAccessor();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsComponentPropertiesFile(final AppComponent component) {
		XMLPropertiesForAppComponentCache cache = _retrieveComponent(component);
		return cache != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void clearCache() {
		if (_useCache) _appComponentCache.clear();	// remove all cached components: it'll be loaded again
	}
	@Override
	public void clearCacheOf(final AppComponent component) {
		if (_useCache) _appComponentCache.remove(component);	// just removing the component cache it'll be loaded again
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a cached property or it gets it from the underlying properties XML if it was NOT loaded (execs the associated xpath expression)
	 * @param appComp
	 * @return
	 */
	private XMLPropertiesForAppComponentCache _retrieveComponent(final AppComponent appComp) {
		XMLPropertiesForAppComponentCache outComp = null;
		try {
			outComp =_useCache ? _appComponentCache.get(appComp)	// Get the component from the cache if present
							   : null;								// Cache is disabled: ALWAYS LOAD FROM FILE
			if (outComp == null) {
				// Load the component definition
				PropertiesComponentDef compDef = PropertiesComponentDefLoader.loadOrDefault(_environment,
																   							_appCode,appComp);

				// [1] -- Load the XML file
				Document xmlDoc = _loadComponentXML(compDef);

				// [2] - The XML Document accessor is used by the component properties cache
				//		 to load the underlying property from the XML Document
				outComp = _createAppComponentCache(appComp,
												   xmlDoc);
				// [99] - Cache
				if (_useCache) {
					// add to the cache
					_appComponentCache.put(appComp,outComp);

					// Tell the cache that a new properties component has been loaded
					// (at this point the cache is re-built to accommodate the new estimated property number)
					if (_componentLoadedListener != null) _componentLoadedListener.newComponentLoaded(compDef);
				}
			}
		} catch (XMLPropertiesException xmlPropsEx) {
			xmlPropsEx.printStackTrace(System.out);
		}
		return outComp;
	}
	/**
	 * Returns a cached property or it gets it from the underlying properties XML if it was NOT loaded (execs the associated xpath expression)
	 * @param appComp
	 * @return
	 */
	private XMLPropertiesForAppComponentCache _createAppComponentCache(final AppComponent appComp,
																 	   final Document xmlDoc) {
		// [a] -- Create a XML Document accessor
		XMLPropertiesForAppComponentAccessor xmlDocAccessor = new XMLPropertiesForAppComponentAccessorImpl(appComp,
																										   xmlDoc);
		// [b] - The XML Document accessor is used by the component properties cache
		//		 to load the underlying property from the XML Document
		return new XMLPropertiesForAppComponentCache(xmlDocAccessor,
													 _useCache);
	}
	/**
	 * Loads a properties XML file for appCode/component as stated at the component definition
	 * @param component
	 * @param compDef component definition
	 * @return the xml {@link Document}
	 * @throws XMLPropertiesException if the XML file cannot be loaded or it's malformed
	 */
	private Document _loadComponentXML(final PropertiesComponentDef compDef) throws XMLPropertiesException {
		log.info("Loading xml properties for {}.{} using {} loader",
				 _appCode,compDef.getName(),
				 compDef.getLoaderDef().getLoader());
		// [1] Get a resources loader
		ResourcesLoader resLoader = ResourcesLoaderBuilder.createResourcesLoaderFor(compDef.getLoaderDef());

		XMLDocumentBuilder domBuilder = new XMLDocumentBuilder(resLoader);

		// [2] Load the env-independent XML properties file
		Path defPropsFileUri = compDef.getPropertiesFileURI();
		Document defXmlDoc = null;
		try {
			log.info("\t... trying to find env-independent properties file at {}",
					 defPropsFileUri);
			defXmlDoc = domBuilder.buildXMLDOM(defPropsFileUri);
			log.info("\t... found xml properties env-independent properties file at {}",
					 defPropsFileUri);
		} catch (SAXException saxEx) {
			if (Throwables.getRootCause(saxEx) instanceof FileNotFoundException) {
				log.error("Could NOT load xml properties file at {} using {} loader",
						  defPropsFileUri,
						  compDef.getLoaderDef().getLoader());
				throw XMLPropertiesException.propertiesLoadError(_environment,_appCode,compDef);
			}
			throw XMLPropertiesException.propertiesXMLError(_environment,_appCode,compDef.getName());
		}

		// [3] Try to find an env-dependent XML properties file
		Document envXmlDoc = null;
		Path envDepPropsFileUri = Path.from(_environment)
									  .joinedWith(compDef.getPropertiesFileURI());
		if (_environment != null) {
			log.info("\t... trying to find env-dependent properties file at {}",envDepPropsFileUri);
			InputStream envDepPropsFileIS = null;
			try {
				envDepPropsFileIS = resLoader.getInputStream(envDepPropsFileUri);
			} catch (IOException ioEx) {
				log.warn("\t... NO env-dependent properties file found at {}",envDepPropsFileUri);
			}
			if (envDepPropsFileIS != null) {
				try {
					envXmlDoc = domBuilder.buildXMLDOM(envDepPropsFileUri);
					log.info("\t... found env-dependent properties file at {}",
							 envDepPropsFileUri);
				} catch (SAXException saxEx) {
					saxEx.printStackTrace();
				}
			} else {
				log.info("\t... NO env-dependent properties file found at {}",envDepPropsFileUri);
			}
		} else {
			log.warn("\t... NO env set with -DR01ENV={env}: NO env-dependent properties file is used!");
		}
		// [4] Merge all files if necessary
		Document outXml = null;
		if (envXmlDoc != null) {
			try {
				log.info("\t... merge xml properties file at {} with env-dependent at {}",
						 defPropsFileUri,envDepPropsFileUri);
				XMLMerger merger = new XMLMerger();
				merger.merge(defXmlDoc);		// recessive
				merger.merge(envXmlDoc);	// dominant

				outXml = merger.buildDocument();
				log.debug("\n XMLProperties has been merged !!! : \n {} ",
						  XMLUtils.asString(outXml));
			} catch (ParserConfigurationException cfgEx) {
				log.error("Error while merging properties xml doc at {} with the env-dependent at {}: {}",
						  defPropsFileUri,envDepPropsFileUri,
						  cfgEx.getMessage(),cfgEx);
				outXml = defXmlDoc;
			}
		} else {
			outXml = defXmlDoc;
		}
		if (log.isDebugEnabled()) log.debug("Effective xml properties at {}\n{}",
											compDef.getPropertiesFileURI(),
											XMLUtils.asString(outXml));
//		System.out.println(XMLUtils.asString(outXml));
		return outXml;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATS & DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String cacheStatsDebugInfo() {
		StringBuilder dbg = new StringBuilder();
		dbg.append("XMLProperties for app").append(_appCode).append(" chache statistics:\n");
		for (Map.Entry<AppComponent,PropertiesAppComponentPropertyCacheStats> me : this.getCacheStatsByAppComponent().entrySet()) {
			dbg.append(Strings.customized("- {}: {}\n",
										  me.getKey(),me.getValue().debugInfo()));
		}
		return dbg.toString();
	}
	@Override
	public Map<AppComponent,PropertiesAppComponentPropertyCacheStats> getCacheStatsByAppComponent() {
		if (!_useCache) return Maps.newHashMap();
		return _appComponentCache.entrySet()
					.stream()
					.map(me -> new Map.Entry<AppComponent,PropertiesAppComponentPropertyCacheStats>() {
										@Override
										public AppComponent getKey() {
											return me.getKey();
										}
										@Override
										public PropertiesAppComponentPropertyCacheStats getValue() {
											return me.getValue()
													 .usageStats();
										}
										@Override
										public PropertiesAppComponentPropertyCacheStats setValue(final PropertiesAppComponentPropertyCacheStats value) {
											throw new UnsupportedOperationException();
										}
					  			 })
					.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
	}
	@Override
	public String cacheStatsDebugInfoOf(final AppComponent appComp) {
		PropertiesAppComponentPropertyCacheStats compStats = this.getCacheStatsOf(appComp);
		return compStats != null ? compStats.debugInfo()
								 : "no properties cache info for " + appComp;
	}
	@Override
	public PropertiesAppComponentPropertyCacheStats getCacheStatsOf(final AppComponent appComp) {
		XMLPropertiesForAppComponentCache compCache = _appComponentCache.get(appComp);
		return compCache != null ? compCache.usageStats()
								 : null;
	}
}
