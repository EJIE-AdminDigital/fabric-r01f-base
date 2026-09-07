package r01f.types.hierarchy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import r01f.util.types.collections.CollectionUtils;

/**
 * Simple {@link HasChildren} delegate
 * @param <T>
 */
public abstract class HasChildrenListBackedBase<T>
		   implements HasChildren<T> {
		   			  // List<T> {		// do NOT implement List/Collection > problems while marshalling since it's detected as a Collection<Collection> that cannot be marshalled to XML by Jackson
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract List<T> getChildList();
	public abstract void setChildList(final List<T> children);
	
	// can be overridden to create a different list type
	protected List<T> _createChildList() {
		return new LinkedList<>();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ADD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean addChild(final T item) {
		if (item == null) return false;
		
		if (this.getChildList() == null) this.setChildList(_createChildList());
		return this.getChildList().add(item);
	}
	@Override
	public void addChildAt(final int index,final T child) {
		if (this.getChildList() == null) this.setChildList(_createChildList());
		List<T> list = this.getChildList();
		list.add(index,child);
	}
	@Override
	public boolean addChildren(final Collection<T> items) {
		if (CollectionUtils.isNullOrEmpty(items)) return false;
		
		if (this.getChildList() == null) this.setChildList(_createChildList());
		return this.getChildList().addAll(items);
	}
	@Override
	public boolean addChildrenAt(final int index,final Collection<T> items) {
		if (CollectionUtils.isNullOrEmpty(items)) return false;
		
		if (this.getChildList() == null) this.setChildList(_createChildList());
		return this.getChildList().addAll(index,items);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean removeChild(final T child) {
		if (this.getChildList() == null) return false;
		return this.getChildList().remove(child);
	}
	@Override
	public T removeChildAt(final int index) {
		if (this.getChildList() == null) return null;
		return this.getChildList().remove(index);
	}
	@Override
	public void removeAllChilds() {
		if (this.getChildList() == null) return;
		this.getChildList().clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean hasChildren() {
		return CollectionUtils.hasData(this.getChildList());
	}
	@Override
	public T getChildAt(final int index) {
		if (this.getChildList() == null) return null;
		List<T> list = this.getChildList();
		return list.get(index);
	}
	@Override
	public int getChildCount() {
		return CollectionUtils.hasData(this.getChildList()) ? this.getChildList().size()
															: 0;
	}
	@Override
	public int getChildIndex(final T child) {
		if (this.getChildList() == null) return -1;
		List<T> list = this.getChildList();
		return list.indexOf(child);
	}
	@Override
	public Collection<T> getChildren() {
		return this.getChildList();
	}
}
