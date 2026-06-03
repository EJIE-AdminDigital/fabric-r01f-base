package r01f.facets;

public interface HasLangIndependentDescription 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the name
	 * @return
	 */
	public String getDescription();
	/**
	 * Sets a name 
	 */
	public default void setDescription(final String name) {
		throw new UnsupportedOperationException("Override if set description is supported");
	}
}
