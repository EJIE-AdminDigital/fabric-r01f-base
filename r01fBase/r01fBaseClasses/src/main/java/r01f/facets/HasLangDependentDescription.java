package r01f.facets;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;

public interface HasLangDependentDescription 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the description by language
	 * @return
	 */
	public LanguageTexts getDescriptionByLanguage();
	
	public default void setDescriptionByLanguage(final LanguageTexts name) {
		throw new UnsupportedOperationException("Override if set description is supported");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default String getDescriptionIn(final Language lang) {
		return this.getDescriptionByLanguage()
				   .get(lang);
	}
}
