package r01f.facets;

public interface HasLangIndependentDisplayName 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the name
	 * @return
	 */
	public String getDisplayName();
	/**
	 * Sets a name 
	 */
	public default void setDisplayName(final String name) {
		throw new UnsupportedOperationException("Override if set display name is supported");
	}
}
