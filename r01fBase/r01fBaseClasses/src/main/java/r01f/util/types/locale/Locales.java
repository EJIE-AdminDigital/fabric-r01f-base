package r01f.util.types.locale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import r01f.locale.Country;
import r01f.locale.Language;
import r01f.patterns.Memoized;
import r01f.util.types.Strings;

public abstract class Locales {
	/**
	 * Map of language locales by country
	 */
	private static final Map<Country,List<Locale>> _localesByCountry = new ConcurrentHashMap<>();
	/**
	 * Map of country locales by language
	 */
	private static final ConcurrentMap<Language,List<Locale>> _localesByLanguage = new ConcurrentHashMap<>();
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts a String to a Locale
	 * <pre class='brush:java'>
	 *   	Locales.toLocale("") = new Locale("","")						// no language, no country
	 *   	Locales.toLocale("en") = new Locale("en","")					// language, no country
	 *   	Locales.toLocale("en_GB") = new Locale("en","GB")				// language & country
	 *   	Locales.toLocale("en_GB_xxx") = new Locale("en","GB","xxx")		// with VARIANT
	 * </pre>
	 * NOTE: The behaviour of the constructor with a VARIANT parameter changed between JDK1.3 and JDK1.4.
	 * 			- In JDK1.3 the constructor upper cases the VARIANT
	 * 			- In JDK1.4 the constructor DOES NOT upper cases the VARIANT
	 * 		 ... so the result from getVariant() may vary depending on the JDK
	 * 
	 * This methods does the following validations:
	 * 		- The language code must be lowercase
	 * 		- The country code must be uppercase
	 * 		- The separator must be an underscore (_)
	 * 		- The length must be correct
	 *
	 * @param locStr the locale String to convert, null returns null
	 * @return a Locale, null if null input
	 * @throws IllegalArgumentException if the string is an invalid format
	 * @see Locale#forLanguageTag(String)
	 */
	public static Locale toLocale(final String locStr) {
		if (locStr == null) return null;
		if (locStr.isEmpty()) return new Locale("", ""); 	// JDK 8 introduced an empty locale where all fields are blank
		if (locStr.contains("#")) throw new IllegalArgumentException("Invalid locale format: " + locStr); 	// Cannot handle Java 7 script & extensions
		if (locStr.length() < 2) throw new IllegalArgumentException("Invalid locale format: " + locStr);
		
		Locale outLocale = null;
		
		int locStrLen = locStr.length();
		
		// _ES or _ES_xxx
		if (locStr.charAt(0) == '_') {
			if (locStrLen < 3) throw new IllegalArgumentException("Invalid locale format: " + locStr);
			if (!Character.isUpperCase(locStr.charAt(1)) 
			 || !Character.isUpperCase(locStr.charAt(2))) throw new IllegalArgumentException("Invalid locale format: " + locStr);
			
			if (locStrLen != 3
			 && locStrLen < 5) throw new IllegalArgumentException("Invalid locale format: " + locStr);
			
			if (locStr.charAt(3) != '_') throw new IllegalArgumentException("Invalid locale format: " + locStr);
			
			outLocale = locStrLen == 3 ? new Locale("",
												    locStr.substring(1,3))	// _ES
									   : new Locale("",
											 	    locStr.substring(1,3),
											 	    locStr.substring(4));	// _ES_xxx
		} else {
			String[] split = locStr.split("_", -1);
			int occurrences = split.length - 1;
			switch (occurrences) {
				// es
				case 0:	
					if ((// language
					    Strings.isAllLowerCaseLetters(locStr) 
					    && 
					    (locStrLen == 2 || locStrLen == 3))
					   ) {
						outLocale = new Locale(locStr);
					} else {
						throw new IllegalArgumentException("Invalid locale format: " + locStr);
					}
				// es_ES
				case 1:	
					if ((// language
						 Strings.isAllLowerCaseLetters(split[0]) 
						 && 
						 (split[0].length() == 2 || split[0].length() == 3)
						)
					 && (// country
						 split[1].length() == 2 && Strings.isAllUpperCaseLetters(split[1])
						)) {
						outLocale = new Locale(split[0],split[1]);
					} else {
						throw new IllegalArgumentException("Invalid locale format: " + locStr);
					}
				// es_ES_xxx
				case 2:	
					if ((// language
						 Strings.isAllLowerCaseLetters(split[0])
						 && 
					     (split[0].length() == 2 || split[0].length() == 3)
					    )
					 && (// country
						 split[1].length() == 0 
					 	 || 
					 	 (split[1].length() == 2 && Strings.isAllUpperCaseLetters(split[1]))
					 	) 
					 && (// variant
						 split[2].length() > 0
						)) {
						outLocale = new Locale(split[0],split[1],split[2]);
					} else {
						throw new IllegalArgumentException("Invalid locale format: " + locStr);
					}
				default:
					throw new IllegalArgumentException("Invalid locale format: " + locStr);
			}
		}
		return outLocale;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Obtains the list of locales to search through
	 * The result list begins with the most specific locale, 
	 * then the next more general and so on
	 * The list will never contain the same locale twice.
	 * <pre>
	 * 		Locales.localeLookupList(Locale("fr","CA","xxx")) = [
	 * 																Locale("fr","CA","xxx"),
	 * 																Locale("fr","CA"), 
	 * 																Locale("fr")
	 * 															]
	 * </pre>
	 *
	 * @param locale the locale to start from
	 * @return the unmodifiable list of Locale objects, 0 being locale, not null
	 */
	public static List<Locale> localeLookupList(final Locale locale) {
		return localeLookupList(locale, locale);
	}
	/**
	 * Obtains the list of locales to search through
	 * The result list begins with the most specific locale, 
	 * then the next more general and so on, finishing with the default locale.
	 * The list will never contain the same locale twice.
	 * <pre>
	 * 		Locales.localeLookupList(Locale("fr","CA","xxx")) = [
	 * 																Locale("fr","CA","xxx"),
	 * 																Locale("fr","CA"), 
	 * 																Locale("fr")
	 * 															]
	 * </pre>
	 *
	 * @param locale the locale to start from, null returns empty list
	 * @param defaultLocale the default locale to use if no other is found
	 * @return the unmodifiable list of Locale objects, 0 being locale, not null
	 */
	public static List<Locale> localeLookupList(final Locale locale,final Locale defaultLocale) {
		List<Locale> outList = new ArrayList<>(4);
		if (locale != null) {
			outList.add(locale);
			if (locale.getVariant().length() > 0) {
				outList.add(new Locale(locale.getLanguage(),locale.getCountry()));
			}
			if (locale.getCountry().length() > 0) {
				outList.add(new Locale(locale.getLanguage(),""));
			}
			if (outList.contains(defaultLocale) == false) {
				outList.add(defaultLocale);
			}
		}
		return Collections.unmodifiableList(outList);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("null")
	private static final Memoized<List<Locale>> _availableLocalesList = Memoized.using(() -> new ArrayList<>(Arrays.asList(Locale.getAvailableLocales())));
	
	@SuppressWarnings("null")
	private static final Memoized<Set<Locale>> _availableLocalesSet = Memoized.using(() -> new HashSet<>(Arrays.asList(Locale.getAvailableLocales())));
	/**
	 * Returns an unmodifiable list of installed locales
	 * This method is a wrapper around {@link Locale#getAvailableLocales()}.
	 * It is more efficient, as the JDK method must create a new array each
	 * time it is called.
	 *
	 * @return the unmodifiable list of available locales
	 */
	public static List<Locale> availableLocaleList() {
		return _availableLocalesList.get();
	}
	/**
	 * Returns an unmodifiable list of installed locales
	 * This method is a wrapper around {@link Locale#getAvailableLocales()}.
	 * It is more efficient, as the JDK method must create a new array each
	 * time it is called.
	 *
	 * @return the unmodifiable set of available locales
	 */
	public static Set<Locale> availableLocaleSet() {
		return _availableLocalesSet.get();
	}
	/**
	 * Checks if the locale specified is in the list of available locales
	 *
	 * @param locale the Locale object to check if it is available
	 * @return true if the locale is a known locale
	 */
	public static boolean isAvailableLocale(final Locale locale) {
		return Locales.availableLocaleList()
					  .contains(locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtains the list of languages supported for a given country.
	 * Variant locales are removed
	 *
	 * @param country the 2 letter country code, null returns empty
	 * @return an unmodifiable List of Locale objects, not null
	 */
	public static List<Locale> languagesByCountry(final Country country) {
		if (country == null) return Collections.emptyList();
		
		List<Locale> localesOfCountry = _localesByCountry.get(country);
		if (localesOfCountry == null) {
			localesOfCountry = Locales.availableLocaleList()
									  .stream()
									  .filter(loc -> country.getIso3166_2().equals(loc.getCountry())
											  	  && loc.getVariant().isEmpty())
									  .toList();
			_localesByCountry.putIfAbsent(country,localesOfCountry);
			localesOfCountry = _localesByCountry.get(country);
		}
		return localesOfCountry;
	}
	/**
	 * Returns all countries supported for a given language
	 * Variant locales are removed
	 *
	 * @param lang the language
	 * @return an unmodifiable List of Locale objects, not null
	 */
	public static List<Locale> countriesByLanguage(final Language lang) {
		if (lang == null) return Collections.emptyList();
		
		List<Locale> localesOfLang = _localesByLanguage.get(lang);
		if (localesOfLang == null) {
			localesOfLang = Locales.availableLocaleList()
								   .stream()
								   .filter(loc -> lang.getIso639_1().equals(loc.getLanguage())
										   	   && Strings.isNOTNullOrEmpty(loc.getCountry())
										   	   && loc.getVariant().isEmpty())
								   .toList();
			_localesByLanguage.putIfAbsent(lang,localesOfLang);
			localesOfLang = _localesByLanguage.get(lang);
		}
		return localesOfLang;
	}
}
