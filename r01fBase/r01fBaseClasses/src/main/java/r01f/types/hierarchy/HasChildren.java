package r01f.types.hierarchy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import r01f.util.types.collections.CollectionUtils;


public interface HasChildren<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a child 
	 * @param child
	 * @return true if the child was added, false otherwise
	 */
	public boolean addChild(final T item);
	/**
	 * Inserts a child at the specified index 
	 * @param index the index where the child will be inserted
	 * @param child 
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void addChildAt(final int index,final T child);
	/**
	 * Adds a cuple of childs
	 * @param items
	 * @return true if the childs were added, false otherwise
	 */
	public boolean addChildren(final Collection<T> items);
	/**
	 * Adds a cuple of childs
	 * @param index the index where the childs will be inserted
	 * @param items
	 * @return true if the childs were added, false otherwise
	 */
	public boolean addChildrenAt(final int index,final Collection<T> items);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Removes a child 
	 * @param child
	 */
	public boolean removeChild(final T child);
	/**
	 * Removes a child located at the specified index
	 * @param item
	 * @return the child removed
	 */
	public T removeChildAt(final int index);
	/**
	 * Removes all children
	 */
	public void removeAllChilds();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are child
	 */
	public boolean hasChildren();
	/**
	 * Gets the child at the specified index.
	 * @param index the index to be retrieved
	 * @return the child at that index
	 */
	public T getChildAt(int index);
	/**
	 * Gets the number of children contained.
	 * @return child count.
	 */
	public int getChildCount();
	/**
	 * Gets the index of the specified child.
	 * @param child the child to be found
	 * @return the child's index, or <code>-1</code> if none is found
	 */
	public int getChildIndex(final T child);
	/**
	 * @return the child items collection
	 */
	public Collection<T> getChildren();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default List<T> asList() {
		return new List<T>() {
						private List<T> _getChildList() {
							return ( List<T>)HasChildren.this.getChildren();
						}
						////////// --- Get/Set
						@Override
						public T get(final int index) {
							return _getChildList() != null ? _getChildList().get(index) : null;
						}
						@Override
						public T set(final int index,final T element) {
							if (_getChildList() == null) throw new IndexOutOfBoundsException("No children");
							return _getChildList()
									   .set(index,element);
						}
						////////// --- Add
						@Override
						public boolean add(final T item) {
							return HasChildren.this.addChild(item);
						}
						@Override
						public void add(final int index,final T element) {
							HasChildren.this.addChildAt(index,element);
						}
						@Override @SuppressWarnings("unchecked")
						public boolean addAll(final Collection<? extends T> items) {
							return HasChildren.this.addChildren((Collection<T>)items);
						}
						@Override @SuppressWarnings("unchecked")
						public boolean addAll(final int index,final Collection<? extends T> items) {
							if (CollectionUtils.isNullOrEmpty(items)) return false;
							
							return HasChildren.this.addChildrenAt(index,(Collection<T>)items);
						}
						////////// --- Remove
						@Override
						public boolean remove(final Object o) {
							boolean outRemoved = _getChildList() != null ? _getChildList().remove(o) : false;
							return outRemoved;
						}
						@Override
						public T remove(final int index) {
							if (CollectionUtils.isNullOrEmpty(_getChildList())) return null;
							return _getChildList().remove(index);
						}	
						@Override
						public boolean removeAll(final Collection<?> items) {
							boolean outRemoved = _getChildList() != null ? _getChildList().removeAll(items) : false;
							return outRemoved;
						}
						@Override
						public boolean retainAll(final Collection<?> items) {
							return _getChildList() != null ? _getChildList().retainAll(items) : false;
						}
						@Override
						public void clear() {
							if (_getChildList() != null) _getChildList().clear();
						}
						////////// --- Collection
						@Override
						public boolean isEmpty() {
							return CollectionUtils.isNullOrEmpty(_getChildList());
						}
						@Override
						public int size() {
							return _getChildList() != null ? _getChildList().size() : 0;
						}
						@Override
						public boolean contains(final Object o) {
							return _getChildList() != null ? _getChildList().contains(o) : false;
						}
						@Override
						public boolean containsAll(final Collection<?> c) {
							return _getChildList() != null ? _getChildList().containsAll(c) : false;
						}
						@Override
						public int indexOf(final Object o) {
							return _getChildList() != null ? _getChildList().indexOf(o) : -1;
						}
						@Override
						public int lastIndexOf(final Object o) {
							return _getChildList() != null ? _getChildList().lastIndexOf(o) : -1;
						}
						@Override
						public ListIterator<T> listIterator() {
							return _getChildList() != null ? _getChildList().listIterator() 
														   : Collections.emptyListIterator();
						}
						@Override
						public ListIterator<T> listIterator(final int index) {
							return _getChildList() != null ? _getChildList().listIterator(index) 
														   : Collections.emptyListIterator();
						}
						@Override
						public List<T> subList(final int fromIndex,final int toIndex) {
							return _getChildList() != null ? _getChildList().subList(fromIndex,toIndex) 
															   : Collections.emptyList();
						}
						@Override
						public Iterator<T> iterator() {
							return _getChildList() != null ? _getChildList().iterator() 
														   : Collections.emptyIterator();
						}
						@Override
						public Object[] toArray() {
							return _getChildList() != null ? _getChildList().toArray() : new Object[] {};
						}
						@Override
						public <T2> T2[] toArray(final T2[] a) {
							return _getChildList() != null ? _getChildList().toArray(a) :null;
						}
			   };
	}
}
