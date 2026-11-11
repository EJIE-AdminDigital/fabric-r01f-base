package r01f.locale;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;


/**
 * Models a collection of texts in different languages
 */
public interface LanguageTexts
         extends Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts this view into a {@link LanguageTextsMapBacked} object
	 * @return
	 */
	public static LanguageTexts createNewFrom(final LanguageTexts other) {
		LanguageTexts out = new LanguageTextsMapBacked();
		other.getDefinedLanguages()
			 .forEach(lang -> out.add(lang,other.getFor(lang)));
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Behavior when a text in a language is not found
/////////////////////////////////////////////////////////////////////////////////////////	
	public enum LangTextNotFoundBehabior {
		RETURN_NULL,
		RETURN_DEFAULT_VALUE,
		THROW_EXCEPTION;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the behavior of the {@link LanguageTexts} collection when a required language text
	 * is NOT found
	 * @param behavior
	 */
	public void setLangTextNotFoundBehabior(final LangTextNotFoundBehabior behavior);
	/**
	 * Gets the behavior of the {@link LanguageTexts} collection when a required language text
	 * is NOT found
	 * @return
	 */
	public LangTextNotFoundBehabior getLangTextNotFoundBehabior();	
	/**
	 * If the {@link LangTextNotFoundBehabior} is RETURN_DEFAULT_VALUE this value is returned
	 * @return the default value
	 */
	public String getDefaultValue();
	/**
	 * If the {@link LangTextNotFoundBehabior} is RETURN_DEFAULT_VALUE this value is returned
	 * @param defaultValue
	 */
	public void setDefaultValue(final String defaultValue);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets a text in a language
	 * @param lang
	 * @param text
	 */
	public LanguageTexts add(final Language lang,final String text);
	/**
	 * Adds the same text for all languages
	 * @param text
	 */
	public default LanguageTexts addForAll(final String text) {
		for (Language lang : Language.values()) {
			this.add(lang,text);
		}
		return this;
	};
	/**
	 * A add() equivalent method
	 * @param lang
	 * @param text
	 */
	public default void set(final Language lang,final String text) {
		this.add(lang,text);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a text in a language
	 * @param lang the language
	 * @return the text in the provided language
	 */
	public default String get(final Language lang) {
		return this.getFor(lang);
	}
	/**
	 * Returns a text in a language
	 * @param lang the language
	 * @return the text in the provided language
	 */
	public String getFor(Language lang);
	/**
	 * Returns a text in a language or null if it does NOT exists
	 * @param lang
	 * @return
	 */
	public String getForOrNull(Language lang);
	/**
	 * Returns a text in a language or null if it does NOT exists
	 * @param lang
	 * @param def
	 * @return
	 */
	public default String getForOrDefault(final Language lang,final String def) {
		String out = this.getForOrNull(lang);
		return out != null ? out
						   : def;
	}
	/**
	 * Returns the text in the system's default language
	 * @return
	 */
	public default String getForSystemDefaultLanguage() {
		return this.getFor(Language.DEFAULT);
	}
	/**
	 * Returns a text in any (random) language
	 * @return
	 */
	public default String getAny() {
		Language anyLang = this.getDefinedLanguages()
							   .stream()
							   .findAny().orElse(null);
		return anyLang != null ? this.getFor(anyLang)
							   : null;
	}
	/**
	 * Returns the first text in any of the given langs
	 * @param lang
	 * @return
	 */
	public default String getInFirstAvailableOf(final Language... langs) {
		if (CollectionUtils.isNullOrEmpty(langs)) throw new IllegalArgumentException();
		String outText = null;
		for (Language lang : langs) {
			outText = this.get(lang);
			if (Strings.isNOTNullOrEmpty(outText)) break;
		}
		return outText;
	}
	/**
	 * Returns true if some text is defined for the lang
	 * @param lang the language
	 * @return false is NO text is defined for the lang; false otherwise
	 */
	public default boolean isTextDefinedFor(final Language... langs) {
		if (CollectionUtils.isNullOrEmpty(langs)) return false;
		boolean allLangsDefined = true;
		for (Language lang : langs) {
			String text = this.getForOrNull(lang);
			if (Strings.isNullOrEmpty(text)) {
				allLangsDefined = false;
				break;
			}
		}
		return allLangsDefined;
	}
	/**
	 * Returns true if there's text defined for any language
	 * @return
	 */
	public default boolean isTextDefinedForAnyLanguage() {
		String anyLangText = this.getAny();
		return Strings.isNOTNullOrEmpty(anyLangText);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the {@link Set} of {@link Language} that have some text associated with
	 */
	public Set<Language> getDefinedLanguages();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return a {@link Map} indexed by {@link Language} with the texts
	 */
	public default Map<Language,String> asMap() {
		Set<Language> langs = this.getDefinedLanguages();

		if (CollectionUtils.isNullOrEmpty(langs)) return null;

		// Return an INMUTABLE Map<Language,String>
		return langs.stream()
					.collect(Collectors.toMap(Function.identity(), 
											  (Function<? super Language,? extends String>)this::get));
	}
	public default LanguageTexts asLanguageTextsMapBacked() {
		if (this instanceof LanguageTextsMapBacked) return this;
		Map<Language,String> asMap = this.asMap();
		return new LanguageTextsMapBacked(asMap);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Merges both language texts: every language text NOT defined in this object is copied from the given one
	 * Note that if this object and the given one have a text defined for a certain language BUT the text content is NOT the same, 
	 * this object's language text is NOT overriden
	 * @param other
	 * @return this merged object
	 */
	public default LanguageTexts mergeWith(final LanguageTexts other) {
		if (other == null) return this;
		Set<Language> thisObjLangs = this.getDefinedLanguages();
		Set<Language> otherObjLangs = other.getDefinedLanguages();

		Set<Language> otherObjLangsNotInThis = Languages.difference(otherObjLangs,thisObjLangs);
		if (CollectionUtils.hasData(otherObjLangsNotInThis)) {
			for (Language otherObjLang : otherObjLangsNotInThis) {
				this.add(otherObjLang,other.get(otherObjLang));
			}
		}
		return this;
	}
}
