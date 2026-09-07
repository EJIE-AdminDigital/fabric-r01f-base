package r01f.facets;

import r01f.facets.delegates.LangDependentNamedDelegate;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsWrapper;

public interface HasLangDependentName
		 extends HasName {	// If an object has a name... it has a summary
	public LanguageTexts getNameByLanguage();
	public default void setNameByLanguage(final LanguageTexts langTexts) {
		if (this instanceof Enum) {	        
            // Conflict at enums. This methods is reached and the enumm cannot overwite this ( final at constructor )
			return; 
        }			
		throw new UnsupportedOperationException("Unsupported by default: override if needed");
	}
	
	public default LangDependentNamed asLangDependentNamed() {
		return new LangDependentNamedDelegate<>(this);
	}
	/**
	 * Just add:
	 * <pre class='brush:java'>
	 * 		@Getter private final transient LanguageTextsWrapper<T> _name = LanguageTextsWrapper.atHasLangDependentNamedFacet((T)this);
	 * </pre>
	 */
	public <T> LanguageTextsWrapper<T> getName();
	
	public default String getNameIn(final Language lang) {
		return this.getName()
				   .getInOrNull(lang);
	}
}