package r01f.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.properties.PropertiesForApp.PropertiesAppComponentPropertyCacheStats;
import r01f.types.Path;
import r01f.xmlproperties.XMLPropertiesForAppComponentAccessor;

/**
 * A cache for an app-component's properties
 * This object uses a {@link XMLPropertiesForAppComponentAccessor} behind the scenes
 * that handles access to the underlying XML Document when the property is not cached
 */
@Slf4j
@Accessors(prefix="_")
public abstract class PropertiesForAppComponentCache<N,A extends PropertiesForAppComponentAccessor<?,N>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int DEF_APP_COMP_PROPS_NUM = 10;

	public static final float APP_COMP_CACHE_MAP_LOAD_FACTOR = 0.5F;
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE: relates a xpath expression with the xml's DOM node containing the prop
/////////////////////////////////////////////////////////////////////////////////////////
	protected final A _props;
	protected final Map<Path,PropertiesAppComponentPropertyCacheItem> _cache;	// xpath - value
	protected final boolean _useCache;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PropertiesForAppComponentCache(final A props,
										  // cache
										  final boolean useCache) {
		_props = props;
		_useCache = useCache;

		////////// Cache
		// A Map's loadFactor is an indication of how full the underlying table can be BEFORE it's capacity is increased
		//		when numEntries > loadFactor * actualCapacity the underlying table is rebuilt and the bucket number (capacity) is doubled
		// NOTE that:
		//		- A HashMap stores it's entries indexed by a hash of their keys in the underlying table's positions (buckets)
		//		  It's possible that at a certain position (bucket) there's MORE THAN A SINGLE entry by two means:
		//			1.- When an new entry is inserted, it has the SAME hashCode than an existent one
		//					In this case, the entry's key's equals() method is called to know if it's the SAME entry -in which case the
		//					existing entry is REPLACED by the new one-, or it's a DIFFERENT entry -in which case, the bucket will store two
		//					entries-.
		// 			2.- The table's capacity has been exceed and obviously at every table's position (bucket) more than a single entry
		//				MUST be stored; in this situation, when a new entry is inserted, it's hashCode MUST be checked against the bucket
		//				existing ones:
		//					- if new entry's key's hashCode == any bucket entry's key's hash code the equals() method must be called
		//					  and replace the entry if both key's hashcodes are equal
		//					- if new entry's key's hashCode != any bucket entry's key's hash code a new entry is added to the bucket
		// The SECOND collision type is affected by the table's capacity (bucket number) that ultimately is affected by the loadFactor
		// (a value between 0 and 1)
		//		The lower the loadFactor is, the LESS collision probability (the number of buckets in the table will sooner be doubled and
		//	 	the probability of table's space starvation is LESS probable)
		float cacheMapLoadFactor = APP_COMP_CACHE_MAP_LOAD_FACTOR;
		int cacheMapSize = DEF_APP_COMP_PROPS_NUM;

		// a new cache is created
		log.trace("Creating a {} positions cache for component {}",
				  cacheMapSize,props.getAppComponent());
		_cache = useCache ? new HashMap<>(cacheMapSize,
							   			  cacheMapLoadFactor)	// the second parameter is the load factor
																// ... the lower it's... the less collision probability
						  : null;	// no cache
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTER
/////////////////////////////////////////////////////////////////////////////////////////
	public AppComponent getAppComponent() {
		return _props.getAppComponent();
	}
	public A getPropertiesAccessor() {
		return _props;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	XML
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the xml node that contains a property
	 * BEWARE this method DOES NOT use the cache!!
	 * @param path
	 * @return
	 */
	public N node(final Path path) {
		return _props.node(path);
	}
	/**
	 * Returns the xml node list that contains a property
	 * BEWARE this method DOES NOT use the cache!!
	 * @param path
	 * @return
	 */
	public Iterator<N> nodeListIterator(final Path path) {
		return _props.nodeListIterator(path);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXISTS PROPERTY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a property exists
	 * First it checks the cache BUT if the property is NOT cached, it's retrieved
	 * from the XML
	 * @param component
	 * @param propXPath
	 * @return
	 */
	public boolean existProperty(final Path propXPath) {
		// check the cache
		PropertiesAppComponentPropertyCacheItem cacheItem = _retrieveCached(propXPath);

		if (cacheItem != null) return true;

		// if false, the property MIGHT NOT EXIST or it MIGHT NOT BE ALREADY LOADED so in order to confirm
		// it really does NOT exist, try to find the xml node
		N node = _props.node(propXPath);
		return node != null;
	}
	/**
	 * Checks if a property exists
	 * First it checks the cache BUT if the property is NOT cached, it's retrieved
	 * from the XML
	 * @param component
	 * @param propXPath
	 * @return
	 */
	public boolean isPropertyCached(final Path propXPath) {
		// check the cache
		PropertiesAppComponentPropertyCacheItem cacheItem = _retrieveCached(propXPath);

		return cacheItem != null ? true : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTY ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a property from a XML node
	 * @param xPath node xPath
	 * @param transformFuncion transforms the node to the value
	 * @param defaultValue the property default value
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPropertyFromNode(final Path xPath,
							 		 final Function<N,T> transformFuncion,
							 		 final T defaultValue) {
		PropertiesAppComponentPropertyCacheItem cacheItem = _retrieveCachedOrLoadFromNode(xPath,
									 	   												  transformFuncion);
		T outObj = (T)cacheItem.getPropValue();
		if (outObj == null && defaultValue != null) {
			// The property does NOT exist but a default value was given... store the default value
			outObj = defaultValue;
			cacheItem.setValue(defaultValue,true);
		} else if (outObj == null) {
			// the property does NOT exist and NO default value was given
		}
		return outObj;
	}
	/**
	 * Returns a property from a XML node list
	 * @param xPath node xPath
	 * @param transformFuncion transforms the node to the value
	 * @param defaultValue the property default value
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPropertyFromNodeList(final Path xPath,
							 			 final Function<Iterator<N>,T> transformFuncion,
							 			 final T defaultValue) {
		PropertiesAppComponentPropertyCacheItem cacheItem = _retrieveCachedOrLoadFromNodeList(xPath,
									 	   													  transformFuncion);
		T outObj = (T)cacheItem.getPropValue();
		if (outObj == null && defaultValue != null) {
			// The property does NOT exist but a default value was given... store the default value
			outObj = defaultValue;
			cacheItem.setValue(defaultValue,true);
		} else if (outObj == null) {
			// the property does NOT exist and NO default value was given
		}
		return outObj;
	}
	/**
	 * Returns a cached value or null if the property is NOT cached
	 * @param xPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getCachedPropertyOrNull(final Path xPath) {
		PropertiesAppComponentPropertyCacheItem cacheItem = _retrieveCached(xPath);	// Try to retrieve the cached value
		return cacheItem != null ? (T)cacheItem.getPropValue() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE: RETRIEVE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieves a cached item given it's XPath
	 * @param xPath
	 * @return
	 */
	private PropertiesAppComponentPropertyCacheItem _retrieveCached(final Path xPath) {
		PropertiesAppComponentPropertyCacheItem outCacheItem = null;
		if (_useCache) {
			outCacheItem = _cache.get(xPath);		// Try to retrieve the cached value
			if (outCacheItem != null) outCacheItem.anotherHit();
		}
		return outCacheItem;
	}
	/**
	 * Retrieves a property value given an Node's XPath
	 * @param xPath
	 * @param transformFunction
	 * @return
	 */
	private <T> PropertiesAppComponentPropertyCacheItem _retrieveCachedOrLoadFromNode(final Path xPath,
									 	  											  final Function<N,T> transformFuncion) {
		// [1] - Get the cache item
		PropertiesAppComponentPropertyCacheItem outCacheItem = _retrieveCached(xPath);

		// If transformFuncion==null, just check the cache (do NOT try to load the property value from the xml)
		if (transformFuncion == null) return outCacheItem;

		// [2] - If the cache item does NOT exist, load the value from the XML DOM
		if (outCacheItem == null) {
			// It's the first time the property is accessed so the cache is empty: go to the xml
			T obj =  null;
			N node = _props.node(xPath);
			if (node != null) {
				try {
					obj = transformFuncion.apply(node);
				} catch (Throwable th) {
					log.error(String.format("Error transforming node %s at component %s to an object using a transform function: %s",
											xPath,_props.getAppComponent(),
											th.getMessage()),
							  th);
				}
			}
			// Store the created object at the cache
			// BEWARE!! the stored property value CAN be null
			//			... this way if the property does NOT exists, the XML DOM is not traversed
			//				every time the property is requested
			if (_useCache) {
				// cache is used
				outCacheItem = _store(xPath,
							  	  	  obj,		// BEWARE!! obj can be NULL if the property DOES NOT EXISTS!!!!
							  	  	  false);	// not the default value
			} else {
				// cache is NOT used: return a new item
				outCacheItem = new PropertiesAppComponentPropertyCacheItem(1,// access count
																			  System.currentTimeMillis(),	// last access time stamp
																			  obj,
																			  false);
			}
		}
		// return
		return outCacheItem;
	}
	/**
	 * Retrieves a property value given an Node's XPath
	 * @param xPath
	 * @param transformFunction
	 * @return
	 */
	private <T> PropertiesAppComponentPropertyCacheItem _retrieveCachedOrLoadFromNodeList(final Path xPath,
									 	  												  final Function<Iterator<N>,T> transformFuncion) {
		// [1] - Get the cache item
		PropertiesAppComponentPropertyCacheItem outCacheItem = _retrieveCached(xPath);

		// If transformFuncion==null, just check the cache (do NOT try to load the property value from the xml)
		if (transformFuncion == null) return outCacheItem;

		// [2] - If the cache item does NOT exist, load the value from the XML DOM
		if (outCacheItem == null) {
			// It's the first time the property is accessed so the cache is empty: go to the xml
			T obj =  null;
			Iterator<N> nodeIt = _props.nodeListIterator(xPath);
			if (nodeIt != null) {
				try {
					obj = transformFuncion.apply(nodeIt);
				} catch (Throwable th) {
					log.error(String.format("Error transforming node-list %s at component %s to an object using a transform function: %s",
											xPath,_props.getAppComponent(),
											th.getMessage()),
							  th);
				}
			}
			// Store the created object at the cache
			// BEWARE!! the stored property value CAN be null
			//			... this way if the property does NOT exists, the XML DOM is not traversed
			//				every time the property is requested
			if (_useCache) {
				// stored the object
				outCacheItem = _store(xPath,
									  obj,		// BEWARE!! obj can be NULL if the property DOES NOT EXISTS!!!!
							  	  	  false);	// not the default value
			} else {
				// cache is NOT used: return a new item
				outCacheItem = new PropertiesAppComponentPropertyCacheItem(1,// access count
																			  System.currentTimeMillis(),	// last access time stamp
																			  obj,
																			  false);
			}
		}
		// return
		return outCacheItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE: STORE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Caches a property value
	 * @param xPath
	 * @param obj
	 * @param isDefaultVal
	 * @return the previously stored value
	 */
	private PropertiesAppComponentPropertyCacheItem _store(final Path xPath,final Object obj,
												 			  final boolean isDefaultVal) {
		PropertiesAppComponentPropertyCacheItem value = new PropertiesAppComponentPropertyCacheItem(1,							// access count
																									System.currentTimeMillis(),	// last access time stamp
																									obj,
																									isDefaultVal);
		if (_useCache) _cache.put(xPath,value);
		return value;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE VALUE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Value stored at the cache
	 */
  	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	private class PropertiesAppComponentPropertyCacheItem {
		@Getter private long _accessCount;
		@Getter private long _lastAcessTimeStamp;
		@Getter private Object _propValue;
		@Getter private boolean _defaultValue;

		public void anotherHit() {
			_accessCount++;
			_lastAcessTimeStamp = System.currentTimeMillis();
		}
		public void setValue(final Object val,final boolean defaultVal) {
			_propValue = val;
			_defaultValue = defaultVal;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Cache statistics
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the usage statistics
	 * @return
	 */
	public PropertiesAppComponentPropertyCacheStats usageStats() {
		if (!_useCache) return new PropertiesAppComponentPropertyCacheStats(0,0,0);

		AtomicLong hitCount = new AtomicLong(0);
		AtomicLong defaultValuesCount = new AtomicLong(0);
		AtomicLong invalidCount = new AtomicLong(0);
		_cache.values()
			  .stream()
			  .forEach(item -> {
				  			if (item.getPropValue() != null) {
				  				// the property has value
				  				if (item.isDefaultValue()) defaultValuesCount.addAndGet(item.getAccessCount());
				  				hitCount.addAndGet(item.getAccessCount());
				  			} else {
				  				// the property has null value
				  				invalidCount.addAndGet(item.getAccessCount());
				  			}
			  		   });
		return new PropertiesAppComponentPropertyCacheStats(hitCount.get(),
													   		defaultValuesCount.get(),
													   		invalidCount.get());

	}
}
