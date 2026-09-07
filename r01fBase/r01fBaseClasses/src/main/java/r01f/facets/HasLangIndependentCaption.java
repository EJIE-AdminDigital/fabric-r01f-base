package r01f.facets;

/**
 * Models an object that has a caption
 */
public interface HasLangIndependentCaption 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return the caption
	 */
	public String getCaption();
	/**
	 * Sets the caption
	 * @param name
	 */
	public void setCaption(final String name);
}
