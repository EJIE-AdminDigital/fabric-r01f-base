package r01f.aspects.interfaces.dirtytrack.collections;

import java.util.Collection;
import java.util.Map;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;


/**
 * An interface for a {@link Map} that keeps tracks of the changes (the insertions and deletions)
 * <pre>
 * BEWARE!!		This type DOES NOT implements the {@link Map} interface because it it did, it could NOT be used at the
 * 				aspect ConvertToDirtyStateTrackableAspect to add the {@link ChangesTrackableMapBase} to any type extending
 * 				Map<K,V>; the weave does NOT allow adding an interface to a type that already implements it.
 * 					Cannot declare parent ChangesTrackableMap onto type XXX since it already has java.util.Map<K,V> in its hierarchy
 * </pre> 
 * @param <K>
 * @param <V>
 */
public interface ChangesTrackableMapBase<K,V> 
	     extends DirtyStateTrackable {	
//	     		 Map<K,V> {		// DO NOT make this type implement Map interface (see note above)
/////////////////////////////////////////////////////////////////////////////////////////
//  CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the {@link CollectionChangesTracker} object that keep track of the changes in the {@link Collection}
	 * [Dont! Put generized "K"  //> causes [ERROR] ChangestTrackableMapAspect.aj:33:0::0 can't override r01f.types.dirtytrack.internal.CollectionChangesTracker r01f.aspects.interfaces.dirtytrack.collections.ChangesTrackableMapBase.getChangesTracker() with r01f.types.dirtytrack.internal.CollectionChangesTracker r01f.aspects.interfaces.dirtytrack.collections.ChangesTrackableMapBase.getChangesTracker() return types don't match[m
	 */
	@SuppressWarnings("rawtypes")
	public CollectionChangesTracker getChangesTracker();  // 
	   

}
