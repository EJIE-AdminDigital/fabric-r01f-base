package r01f.types.geo.cache;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.types.geo.GeoLocation;

/**
 * Caches GeoItems
 * Usage: 
 * <pre class='brush:java'>
 *	public static class MyGeoMunicipalitiesCollectionCache
 *				extends GeoItemCollectionCache<GeoMunicipality> {
 *
 *		public MyGeoMunicipalitiesCollectionCache(final Provider<SecurityContext> securityContextProvider,
 *										   		  final NORAService nora) {
 *			super(idPath -> {
 *						GeoStateID stateId = GeoStateID.forId(idPath.getPathElementAt(0));
 *						GeoCountyID countyId = GeoCountyID.forId(idPath.getPathElementAt(1));
 *						return nora.getForMunicipalities
 *								   .getMunicipalitiesOf(stateId,countyId);
 *				  });
 *		}
 *		public Collection<GeoMunicipality> getMuniciaplitiesOf(final GeoStateID stateId,final GeoCountyID countyId) {
 *			return this.get(Path.from(stateId,countyId));
 *		}
 *	}
 * </pre>
 * @param <I>
 * @param <G>
 */
@Slf4j
public class GeoItemCollectionCache<G extends GeoLocation<?>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LoadingCache<GeoCacheKey,Collection<G>> _cache;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoItemCollectionCache(final GeoItemCollectionLoader<G> loader) {
		this(CacheBuilder.newBuilder()
						 .expireAfterWrite(120,TimeUnit.MINUTES),		// expires after TWO hours by default
			 loader);
	}
	public GeoItemCollectionCache(final GeoItemCollectionLoader<G> loader,
								  final int maxSize) {
		this(CacheBuilder.newBuilder()
						 .maximumSize(maxSize),
			 loader);
	}
	public GeoItemCollectionCache(final GeoItemCollectionLoader<G> loader,
								  final int maxSize,final TimeLapse itemExpiration) {
		this(CacheBuilder.newBuilder()
						 .maximumSize(maxSize)
						 .expireAfterWrite(itemExpiration.asMillis(),TimeUnit.MILLISECONDS),
			 loader);
	}
	private GeoItemCollectionCache(final CacheBuilder<Object,Object> cacheBuilder,
								   final GeoItemCollectionLoader<G> loader) {
		// see: https://github.com/google/guava/wiki/CachesExplained
		RemovalListener<GeoCacheKey,Collection<G>> cacheRemovalListener = notif -> {
																	    	log.info("[geo cache] > remove cached geo item collection of={} because {}",
																	    			 notif.getKey(),
																	    			 notif.getCause());
																		  };
		_cache = cacheBuilder// .refreshAfterWrite(120,TimeUnit.MINUTES)	// do NOT refresh!		
							 .removalListener(cacheRemovalListener)
							 .build(new CacheLoader<GeoCacheKey,Collection<G>>() {
											@Override
											public Collection<G> load(final GeoCacheKey key) { // no checked exception
												return _loadGeoItemCollection(key.getIdPath());
											}
											@Override
											public ListenableFuture<Collection<G>> reload(final GeoCacheKey key,
																			  			  final Collection<G> prevVal) {
												ListenableFutureTask<Collection<G>> task = ListenableFutureTask.create(() -> _loadGeoItemCollection(key.getIdPath()));		// callable
												// load in a new thread > ASYNC
												// (the data will be available next time the collection is required)
												Executors.newSingleThreadExecutor()
													 	 .execute(task);
												return task;
											}
											private Collection<G> _loadGeoItemCollection(final Path idPath) {
												return loader.loadItemsOf(idPath);
											}
								   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<G> get(final Path idPath) {
		return _cache.getUnchecked(new GeoCacheKey(idPath));
	}
	public void reloadAll() {
		_cache.invalidateAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the [geo item] collection
	 * @param <I>
	 * @param <G>
	 */
	@FunctionalInterface
	public interface GeoItemCollectionLoader<G extends GeoLocation<?>> {
		public Collection<G> loadItemsOf(final Path idPath);
	}
}
