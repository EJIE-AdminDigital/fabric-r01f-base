package r01f.facets;

import java.util.Collection;

import r01f.facets.delegates.LangDependentNamedDelegate;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsWrapper;

/**
 * Interface for objects that can have names in multiple languages
 * Usage:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public class MyLangDepNamedType
 * 		  implements HasLangDependentNamedFacet {
 *
 * 			@MarshallField(as="nameByLang",escape=true)
 *			@Getter @Setter private LanguageTexts _nameByLanguage;
 *
 *			@Getter private final LanguageTextsWrapper<MT01WeatherForecastTown> _name = LanguageTextsWrapper.atHasLang(this);
 *
 *			@Override
 *			public LangDependentNamed asLangDependentNamed() {
 *				return new LangDependentNamedDelegate<MyLangDepNamedType>(this);
 *			}
 *		}
 * </pre>
 */
public interface LangDependentNamed {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasLangDependentFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasLangDependentNamedFacet
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
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a name in a {@link Language}
	 * @param lang
	 * @param name
	 */
	public void setNameIn(final Language lang,final String name);
	/**
	 * Gets the name in a provided language
	 * @param lang
	 * @return
	 */
	public String getNameIn(Language lang);
	/**
	 * @return the list of languages in which the name is available
	 */
	public Collection<Language> getAvailableLanguages();
}
