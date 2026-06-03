package r01f.facets;

import r01f.locale.Language;

/**
 * Interface for language dependent model objects
 */
public interface HasLanguage 
	     extends HasLang,
	     		 Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Gets the language
	 * @return
	 */
	public Language getLanguage();
	/**
	 * Sets the language
	 * @param lang
	 */
	public default void setLanguage(final Language lang) {
		throw new UnsupportedOperationException("unsupported by default: override if necessary!");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IMPLEMENT ALSO HasLang
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default Language getLang() {
		return this.getLanguage();
	}
	@Override
	public default void setLang(final Language lang) {
		this.setLanguage(lang);
	}
}
