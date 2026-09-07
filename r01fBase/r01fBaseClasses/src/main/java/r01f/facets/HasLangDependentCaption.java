package r01f.facets;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;

/**
 * Models an object that has a caption depending on the language
 */
public interface HasLangDependentCaption 
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Gets the caption in all languages
	 * @return
	 */
	public LanguageTexts getCaptionByLanguage();
	
	public default void setCaptionByLanguage(final LanguageTexts name) {
		throw new UnsupportedOperationException("Override if set description is supported");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Gets the caption in the given language
	 * @return the caption
	 */
	public default String getCaptionIn(final Language lang) {
		return this.getCaptionByLanguage()
				   .get(lang);
				   
	}
}
