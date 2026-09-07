package r01f.cache;

import java.io.Serializable;
import java.time.Instant;

import lombok.experimental.Accessors;

/**
 * A cache key used as:
 * <pre class='brush:java'>
 * 		public class MyApi {
 * 			// The cache
 * 			private final LoadingCache<Key,CacheEntry<Key,MyObj>> _structureCache;
 *		    // create the cache
 *		    // see: https://github.com/google/guava/wiki/CachesExplained
 *		    RemovalListener<Key,
 *		    				CacheEntry<Key,MyObj>> cacheRemovalListener = notif -> {
 *		    																	log.warn("remove cached entry with key={} because {}",
 *		    																			 notif.getKey(),notif.getCause());
 *		    															  };
 *		    _structureCache = CacheBuilder.newBuilder()
 *		    						.maximumSize(20)							// cache a maximum of 20 structures
 *		    						.expireAfterWrite(60,TimeUnit.MINUTES)		// for 20 minutes
 *		    						.refreshAfterWrite(2,TimeUnit.MINUTES)		// refresh after 2 minutes
 *		    						.removalListener(cacheRemovalListener)
 *		    						.build(new CacheLoader<Key,CacheEntry<Key,MyObj>>() {
 *		    										@Override
 *		    										public CacheEntry<Key,MyObj> load(final Key key) { // no checked exception
 *		    											return _load(key);
 *		    										}
 *		    										@Override
 *		    										public ListenableFuture<CacheEntry<Key,MyObj>> reload(final Key key,final CacheEntry<Key,MyObj> prevObj) {
 *		    											Instant lastUpdateDate = MyApi.this.getLastUpdateDateOf(securityContextProvider.get(),
 *		    																								    key);
 *		    											if (prevObj.needsReloadIfLastUpdatedAt(lastUpdateDate)) {
 *		    												log.warn("Reload cached entry with key={}",key);
 *		    												ListenableFutureTask<CacheEntry<Key,MyObj>> task = null;
 *		    												task = ListenableFutureTask.create(() -> _loadStructure(key));		// callable
 *		    												// force the future execution in the current thread > NOT ASYNC
 *		    												try {
 *		    													task.get();
 *		    												} catch (InterruptedException | ExecutionException ex) {
 *		    													log.error("Error while refreshing the cached entry with key={}: {}",
 *		    															  key,
 *		    															  ex.getMessage(),ex);
 *		    												}
 *		    												// the result must be ready!
 *		    												return task;
 *		    											} else {
 *		    												log.warn("cached entry with key={} does NOT needs to be reloaded",
 *		    														 key);
 *		    												return Futures.immediateFuture(prevObj);
 *		    											}
 *		    										}
 *		    										private CacheEntry<Key,MyObj> _load(final Key key) {
 *		    											log.warn("load with key={}",key);
 *		    											MyObj obj = MyApi.this.load(key);
 *		    											return new CacheEntry<>(key,structure,
 *		    																	structure.getTrackingInfo()
 *																						 .getLastUpdateDate());	// the last time the object was updated
 *		    										}
 * 		    							   });
 *
 * 		}
 * </pre>
 * @param <O>
 */
@Accessors(prefix="_")
public record CacheEntry<O,M>(O oid,
							  M object,
							  Instant cachedAt,
							  Instant lastUpdatedAt)
   implements Serializable {

	private static final long serialVersionUID = -3077860130764155886L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public CacheEntry(final O oid,final M obj,
					  final Instant lastUpdatedAt) {
		this(oid,obj,
			 Instant.now(),lastUpdatedAt);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public O getOid() {
		return this.oid;
	}
	public M getObject() {
		return this.object;
	}
	public Instant getCachedAt() {
		return this.cachedAt;
	}
	public Instant getLastUpdatedAt() {
		return this.lastUpdatedAt;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean needsReloadIfLastUpdatedAt(final Instant lastUpdatedAt) {
		if (lastUpdatedAt == null) return false;	
		return this.lastUpdatedAt != null ? lastUpdatedAt.isAfter(this.lastUpdatedAt)
									  	  : true;
	}
}
