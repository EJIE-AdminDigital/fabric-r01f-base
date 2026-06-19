package r01f.types.hierarchy;

import java.util.Collection;

/**
 * Models objects that have multiple direct ancestor
 * @param <T>
 */
public interface HasMultipleParents<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the parent objects
	 * @param parent
	 */
	public void addDirectAncestors(Collection<T> parents);	
	/**
	 * Adds a direct ancestor 
	 * @param item
	 * @return the added parent object
	 */
	public T addDirectAncestor(final T item);
	/**
	 * Inserts a direct ancestor at the specified index 
	 * @param index the index where the parent will be inserted
	 * @param parent 
	 * @return the added parent
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public T insertDirectAncestorAt(final T parent,
						   			final int index);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Removes a direct ancestor
	 * @param item
	 */
	public void removeDirectAncestor(final T item);
	/**
	 * Removes a direct ancestor located at the specified index
	 * @param item
	 */
	public void removeDirectAncestorAt(final int index);
	/**
	 * Removes all direct ancestors
	 */
	public void removeAllDirectAncestors();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are child
	 */
	public boolean hasDirectAncestors();
	/**
	 * Gets the parent at the specified index.
	 * @param index the index to be retrieved
	 * @return the parent at that index
	 */
	public T getDirectAncestorAt(int index);
	/**
	 * Gets the number of direct ancestors
	 * @return parent count.
	 */
	public int getDirectAncestorCount();
	/**
	 * Gets the index of the specified parent.
	 * @param parent the parent to be found
	 * @return the parent's index, or <code>-1</code> if none is found
	 */
	public int getDirectAncestorIndex(final T child);
	/**
	 * Gets te parent objects
	 * @return the parent object
	 */
	public Collection<T> getDirectAncestors();
}
