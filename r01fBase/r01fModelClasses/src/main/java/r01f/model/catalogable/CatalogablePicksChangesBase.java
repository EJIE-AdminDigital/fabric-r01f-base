package r01f.model.catalogable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.Sets;

import lombok.experimental.Accessors;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionChanges;

/**
 * Utility type to compute the differences between two {@link T} set
 */
@Accessors(prefix="_")
public class CatalogablePicksChangesBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final CollectionChanges<T> _changes;
	
	private final Memoized<Set<T>> _deletedPicksSet;
	private final Memoized<Set<T>> _unchangedPicksSet;
	private final Memoized<Set<T>> _newPicksSet;
	
	public Set<T> getDeletedPicksSet() {
		return _deletedPicksSet.get();
	}
	public Set<T> getUnchangedPicksSet() {
		return _unchangedPicksSet.get();
	}
	public Set<T> getNewPicksSet() {
		return _newPicksSet.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected CatalogablePicksChangesBase(final Set<T> currentPicksSet,final Set<T> receivedPicksSet,
								   	   	  final Comparator<? super T> comparator) {
		_changes = CollectionChanges.changesBetween(currentPicksSet,receivedPicksSet)
									.using(comparator);
		_deletedPicksSet = Memoized.using(() -> _collectionToSet(_changes.getDeleted()));
		_unchangedPicksSet = Memoized.using(() -> _collectionToSet(_changes.getInBothCollections()));
		_newPicksSet = Memoized.using(() -> _collectionToSet(_changes.getNew()));
	}
	private static <T> Set<T> _collectionToSet(final Collection<T> col) {
		if (col == null) return Sets.newHashSet();
		return Sets.newHashSet(col);
	}
}
