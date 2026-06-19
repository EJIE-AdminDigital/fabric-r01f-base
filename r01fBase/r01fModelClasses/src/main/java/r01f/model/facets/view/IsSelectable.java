package r01f.model.facets.view;

import r01f.facets.Facet;



/**
 * Models an object that's has a selectable behavior / facet (it can be selected / de-selected)
 * It's normally used as the model part of some TreeView
 */
public interface IsSelectable
	     extends Facet {
	/**
	 * @return true if it's selected either because it's a primary selection or because it's a secondary selection
	 */
	public boolean isSelected();
	/**
	 * Sets the selection status
	 * @param status
	 */
	public void setSelectionTo(final boolean status);
	/**
	 * Selects this item.
	 */
	public default void setSelected() {
		this.setSelectionTo(true);
	}
	/**
	 * DeSelect this item
	 */
	public default void setDeSelected() {
		this.setSelectionTo(false);
	}
	/**
	 * Toggles the selection status
	 */
	public default void toggleSelected() {
		this.setSelectionTo(!this.isSelected());
	}
}
