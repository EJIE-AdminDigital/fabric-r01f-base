package r01f.facets;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;

public interface HasLangDependentDisplayName 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the display name by language
	 * @return
	 */
	public LanguageTexts getDisplayNameByLanguage();
	
	public default void setDisplayNameByLanguage(final LanguageTexts name) {
		throw new UnsupportedOperationException("Override if set display name is supported");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default String getDisplayNameIn(final Language lang) {
		return this.getDisplayNameByLanguage()
				   .get(lang);
	}
}
