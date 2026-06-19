package r01f.types.geo.cache;

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
 *	    public static class MyGeoCountiesCache
 *	    			extends GeoItemCache<GeoCounty> {
 *	    	
 *	    	public MyGeoCountiesCache(final NORAService nora) {
 *	    		super((idPath,lang) -> {
 *	    					GeoStateID stateId = GeoStateID.forId(idPath.getPathElementAt(0));
 *	    					GeoCountyID countyId = GeoCountyID.forId(idPath.getPathElementAt(1));
 *	    					return nora.getForCounties()
 *	    							   .getCounty(stateId,countyId);
 *	    			  });
 *	    	}
 *	    	public GeoCounty getCounty(final GeoCountyID countyId) {
 *	    		return this.get(Path.from(NORAGeoIDs.EUSKADI,countyId));
 *	    	}
 *	    }
 * </pre>
 * @param <I>
 * @param <G>
 */
@Slf4j
public class GeoItemCache<G extends GeoLocation<?>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LoadingCache<GeoCacheKey,G> _cache;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoItemCache(final GeoItemLoader<G> loader) {
		this(CacheBuilder.newBuilder()
						 .expireAfterWrite(120,TimeUnit.MINUTES),		// expires after TWO hours
			 loader);
	}
	public GeoItemCache(final GeoItemLoader<G> loader,
						final int maxSize) {
		this(CacheBuilder.newBuilder()
						 .maximumSize(maxSize),
			 loader);		
	}
	public GeoItemCache(final GeoItemLoader<G> loader,
						final int maxSize,final TimeLapse itemExpiration) {
		this(CacheBuilder.newBuilder()
						 .maximumSize(maxSize)
						 .expireAfterWrite(itemExpiration.asMillis(),TimeUnit.MILLISECONDS),
			 loader);		
	}
	private GeoItemCache(final CacheBuilder<Object,Object> cacheBuilder,
						 final GeoItemLoader<G> loader) {
		// see: https://github.com/google/guava/wiki/CachesExplained
		RemovalListener<GeoCacheKey,G> cacheRemovalListener = notif -> {
															    	log.info("[geo cache] > remove cached geo item of type {} with key={} because {}",
															    			 notif.getValue().getClass().getSimpleName(),notif.getKey(),
															    			 notif.getCause());
															  };
		_cache = cacheBuilder// .refreshAfterWrite(120,TimeUnit.MINUTES)	// do NOT refresh!		
							 .removalListener(cacheRemovalListener)
							 .build(new CacheLoader<GeoCacheKey,G>() {
											@Override
											public G load(final GeoCacheKey key) { // no checked exception
												return _loadGeoItem(key.getIdPath());
											}
											@Override
											public ListenableFuture<G> reload(final GeoCacheKey key,
																			  final G prevVal) {
												ListenableFutureTask<G> task = ListenableFutureTask.create(() -> _loadGeoItem(key.getIdPath()));		// callable
												// load in a new thread > ASYNC
												// (the data will be available next time the collection is required)
												Executors.newSingleThreadExecutor()
													 	 .execute(task);
												return task;
											}
											private G _loadGeoItem(final Path idPath) {
												return loader.load(idPath);
											}
								   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	public G get(final Path idPath) {
		return _cache.getUnchecked(new GeoCacheKey(idPath));
	}
	public void reloadAll() {
		_cache.invalidateAll();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the [geo item]
	 * @param <I>
	 * @param <G>
	 */
	@FunctionalInterface
	public interface GeoItemLoader<G extends GeoLocation<?>> {
		public G load(final Path idPath);
	}
}
