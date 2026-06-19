package r01f.aspects.interfaces.dirtytrack.collections;

import java.util.Collection;

import r01f.aspects.interfaces.dirtytrack.DirtyStateTrackable;
import r01f.types.dirtytrack.internal.CollectionChangesTracker;

/**
 * An interface for a {@link Collection} that keeps tracks of the changes (the insertions and deletions)
 * <pre>
 * BEWARE!!		This type DOES NOT implements the {@link Collection} interface because it it did, it could NOT be used at the
 * 				aspect ConvertToDirtyStateTrackableAspect to add the {@link ChangesTrackableCollectionBase} to any type extending
 * 				Collection<V>; the weave does NOT allow adding an interface to a type that already implements it.
 * 					Cannot declare parent ChangesTrackableCollection onto type XXX since it already has java.util.Collection<V> in its hierarchy
 * </pre> 
 * @param <K>
 * @param <V>
 */
public interface ChangesTrackableCollectionBase<V> 
         extends DirtyStateTrackable { 
//         		 Collection<V> {	// DO NOT make this type implement Collection interface (see note above)
/////////////////////////////////////////////////////////////////////////////////////////
//  CHANGES TRACKER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the {@link CollectionChangesTracker} object that keep track of the changes in the {@link Collection}
	 */
	public CollectionChangesTracker<V> getChangesTracker();
}
