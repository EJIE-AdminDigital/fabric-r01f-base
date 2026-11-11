package r01f.properties;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.oids.CommonOIDs.AppComponent;
import r01f.oids.CommonOIDs.IsAppCode;
import r01f.oids.CommonOIDs.IsAppComponent;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Manages properties for an app code<br/>
 * Provides access to each app's component's properties
 */
public interface PropertiesForApp<D,N,
								  A extends PropertiesForAppComponentAccessor<D,N>,
								  C extends PropertiesForAppComponent<? extends PropertyWrapper<N>>> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the appcode
	 */
	public IsAppCode getAppCode();
/////////////////////////////////////////////////////////////////////////////////////////
//	EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if the component properties file exists
	 * @param component
	 * @return
	 */
	public boolean existsComponentPropertiesFile(final IsAppComponent component);
	/**
	 * Checks if the component properties file exists
	 * @param componentStr
	 * @return
	 */
	public default boolean existsComponentPropertiesFile(final String componentStr) {
		return this.existsComponentPropertiesFile(AppComponent.named(componentStr));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Fluent api that provides access to properties
	 * @param component app component
	 * @return
	 */
	public A of(final IsAppComponent component);
	/**
	 * Fluent api that provides access to properties
	 * @param componentStr app component
	 * @return
	 */
	public default A of(final String componentStr) {
		return this.of(AppComponent.named(componentStr));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encapsulates the component properties
	 * @param component the component
	 * @return the {@link XMLPropertiesForAppComponent}
	 */
	public C forComponent(final IsAppComponent component);
	/**
	 * For testing purposes: the XML properties are NOT loaded
	 * <pre class='brush:java'>
	 *		String xmlPropsStr = """
	 *							 --- an xml string --
	 *							 """;
	 *		Document xmlPropsDoc = XMLUtils.parse(new ByteArrayInputStream(xmlPropsStr.getBytes()));
	 *		XMLPropertiesForAppComponent storeProps = XMLPropertiesBuilder.createForApp(appCode)
	 *																	  .notUsingCache()
	 *																	  .forComponentProperties(appComponent,
	 *																			 				  xmlPropsDoc);
	 * </pre>
	 * @param component
	 * @param xmlDoc
	 * @return
	 */
	public C forComponentProperties(final IsAppComponent component,
									final D xmlDoc);
	/**
	 * Encapsulates the component properties
	 * @param componentStr the component
	 * @return the {@link XMLPropertiesForAppComponent}
	 */
	public default C forComponent(final String componentStr) {
		return this.forComponent(AppComponent.named(componentStr));
	}
	/**
	 * For testing purposes: the XML properties are NOT loaded
	 * @param componentStr
	 * @param xmlDoc
	 * @return
	 */
	public default C forComponentProperties(final String componentStr,
											final D xmlDoc) {
		return this.forComponentProperties(AppComponent.named(componentStr),
								 		   xmlDoc);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Clears the cache
	 * @return
	 */
	public void clearCache();
	/**
	 * Clears the cache of a given component
	 * @param component
	 * @return
	 */
	public void clearCacheOf(final IsAppComponent component);
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE STATS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the cache usage info
	 * @return
	 */
	public String cacheStatsDebugInfo();
	/**
	 * Returns the cache usage info by app component
	 * @return
	 */
	public Map<IsAppComponent,PropertiesAppComponentPropertyCacheStats> getCacheStatsByAppComponent();
	/**
	 * Returns the cache usage info for a component
	 * @param appComp
	 * @return
	 */
	public String cacheStatsDebugInfoOf(final IsAppComponent appComp);
	/**
	 * Returns the cache usage info for a component as a string
	 * @param appComp
	 * @return
	 */
	public PropertiesAppComponentPropertyCacheStats getCacheStatsOf(final IsAppComponent appComp);

	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public class PropertiesAppComponentPropertyCacheStats
	  implements Debuggable {
		@Getter private final long _hitCount;		// total cache hits
		@Getter private final long _defaultCount;	// total default values
		@Getter private final long _invalidCount;	// total invalid

		@Override
		public String debugInfo() {
			return Strings.customized("hits={} defaults={} invalids={}",
									  Long.toString(_hitCount),Long.toString(_defaultCount),Long.toString(_invalidCount));
		}
	}
}