package r01f.util.types.locale;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.facets.HasLanguage;
import r01f.locale.Country;
import r01f.locale.I18NBundleAccess;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.enums.Enums;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * Language locale
 * (this methods are NOT in the {@link Languages} type because GWT does NOT support
 *  {@link Locale})
 */

public class Languages {
/////////////////////////////////////////////////////////////////////////////////////////
//  http://www.w3.org/International/articles/language-tags/
//	http://download1.parallels.com/SiteBuilder/Windows/docs/3.2/en_US/sitebulder-3.2-win-sdk-localization-pack-creation-guide/30801.htm
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Locale SPANISH_LOCALE = Languages.getLocale(Language.SPANISH,Country.SPAIN);
	public static final Locale BASQUE_LOCALE = Languages.getLocale(Language.BASQUE,Country.SPAIN);
	public static final Locale ENGLISH_LOCALE = Languages.getLocale(Language.ENGLISH,Country.UNITED_KINGDOM);
	public static final Locale FRENCH_LOCALE = Languages.getLocale(Language.FRENCH,Country.FRANCE);
	public static final Locale DEUTCH_LOCALE = Languages.getLocale(Language.DEUTCH,Country.GERMANY);
	public static final Locale ITALIAN_LOCALE = Languages.getLocale(Language.ITALIAN,Country.ITALY);
	public static final Locale PORTUGUESE_LOCALE = Languages.getLocale(Language.PORTUGUESE,Country.PORTUGAL);
	public static final Locale SWEDISH_LOCALE = Languages.getLocale(Language.SWEDISH,Country.SWEDEN);
	public static final Locale RUSSIAN_LOCALE = Languages.getLocale(Language.RUSSIAN,Country.RUSSIA);
	public static final Locale POLISH_LOCALE = Languages.getLocale(Language.POLISH,Country.POLAND);
	public static final Locale HUNGARIAN_LOCALE = Languages.getLocale(Language.HUNGARIAN,Country.HUNGARY);
	public static final Locale CZECH_LOCALE = Languages.getLocale(Language.CZECH,Country.CZECHIA);
	public static final Locale ROMANIAN_LOCALE = Languages.getLocale(Language.ROMANIAN,Country.ROMANIA);

	public static final Locale KOREAN_LOCALE = Languages.getLocale(Language.KOREAN,Country.KOREA_SOUTH);
	public static final Locale JAPANESE_LOCALE = Languages.getLocale(Language.JAPANESE,Country.JAPAN);
/////////////////////////////////////////////////////////////////////////////////////////
//  LANGUAGE NAME TRANSLATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Map<Language,LanguageTexts> LANGUAGE_NAMES = new HashMap<>();
	static {
		LANGUAGE_NAMES.put(Language.SPANISH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Español")
									.add(Language.BASQUE,"Gaztelania")
									.add(Language.ENGLISH,"Spanish"));
		LANGUAGE_NAMES.put(Language.BASQUE,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Euskera")
									.add(Language.BASQUE,"Euskara")
									.add(Language.ENGLISH,"Basque"));
		LANGUAGE_NAMES.put(Language.ENGLISH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Inglés")
									.add(Language.BASQUE,"Ingelesa")
									.add(Language.ENGLISH,"English"));
		LANGUAGE_NAMES.put(Language.DEUTCH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Alemán")
									.add(Language.BASQUE,"Alemaniera")
									.add(Language.ENGLISH,"Deutch"));
		LANGUAGE_NAMES.put(Language.FRENCH,
						   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									.add(Language.SPANISH,"Francés")
									.add(Language.BASQUE,"Frantsesa")
									.add(Language.ENGLISH,"French"));
		// TODO completar
	}
	/**
	 * Use a I18N bundle to get a language name
	 * <pre class='brush:java'>
	 * 		String name = Languages.nameUsing(i18n)
	 * 							   .of(lang);
	 * </pre>
	 * @param i18n
	 * @return
	 */
	public static LanguageNameUsingI18N nameUsing(final I18NBundleAccess i18n) {
		return new LanguageNameUsingI18N(i18n);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class LanguageNameUsingI18N {
		private final I18NBundleAccess _i18n;
		public String of(final Language lang) {
			String outName = _i18n.getMessage("language." + lang.getIso639_1());
			return outName;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds a {@link Language} from the {@link Locale}
	 * @param loc
	 * @return
	 */
	public static Language of(final Locale loc) {
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (Languages.getLocale(lang).getLanguage().equals(loc.getLanguage())) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the language code (es, eu, en...)
	 * @param language
	 * @return
	 */
	public static Language of(final String language) {
		String theLang = language.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (theLang.equals("cz")) theLang = "cs";	// bug: czech republic was incorrectly represented as cz BUT really it's cs-CZ
			if (Languages.getLocale(lang).getLanguage().equals(theLang)) {
				outLang = lang;
				break;
			}
		}
		if (outLang == null
		 && (theLang.equalsIgnoreCase("--")
		  || theLang.equalsIgnoreCase("any")
		  || theLang.equalsIgnoreCase("all"))) outLang = Language.ANY;
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the language code (es, eu, en...) and the country code (ES, FR, EN...)
	 * @param language
	 * @param country
	 * @return
	 */
	public static Language of(final String language,
							  final String country) {
		String theLang = language.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;

			Locale loc = Languages.getLocale(lang);
			if (loc.getLanguage().equals(theLang)
			 && loc.getCountry().equals(country)) {
				outLang = lang;
				break;
			}
		}
		if (outLang == null && (theLang.equals("--") || theLang.equals("any"))) outLang = Language.ANY;
		return outLang;
	}
	public static Language fromName(final String name) {
		return Language.fromName(name);
	}
	public static Language fromNameOrThrow(final String name) {
		Language outLang = Languages.fromName(name);
		if (outLang == null) throw new IllegalArgumentException(Throwables.message("{} is NOT a valid {}",name,Language.class));
		return outLang;
	}
	public static Language fromCountryCodeLowercase(final String langCode) {
		return Languages.fromLanguageCode(langCode);
	}
	public static Language fromLanguage(final String lang) {
		return Languages.fromLanguageCode(lang);
	}
	public static Language fromLanguageLowercase(final String lang) {
		return Languages.fromLanguageCode(lang);
	}
	public static Language fromLanguageCode(final String langCode) {
		if (langCode == null) throw new IllegalArgumentException("Not a valid lang code (null)");
		Language outLang = Languages.of(langCode.substring(0,2));
		if (outLang == null
		 && (langCode.equalsIgnoreCase("--")
				|| langCode.equalsIgnoreCase("any")
				|| langCode.equalsIgnoreCase("all"))) outLang = Language.ANY;
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the iso 639_1 code
	 * @param language
	 * @return
	 */
	public static Language fromISO639_1(final String iso) {
		String theIso = iso.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (lang.getIso639_1().equals(theIso)) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	/**
	 * Builds a {@link Language} from the iso 639_2 code
	 * @param language
	 * @return
	 */
	public static Language fromISO639_2(final String iso) {
		String theIso = iso.toLowerCase().trim();
		Language outLang = null;
		for (Language lang : Language.values()) {
			if (lang == Language.ANY) continue;
			if (lang.getIso639_2().equals(theIso)) {
				outLang = lang;
				break;
			}
		}
		return outLang;
	}
	public static boolean canBe(final String name) {
		return Language.canBe(name);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a locale
	 * @param lang
	 * @param country
	 * @return
	 */
	public static Locale getLocale(final Language lang,final Country country) {
		return new Locale(lang.getIso639_1(),country.getIso3166_2());
	}
	/**
	 * Gets a locale
	 * @param lang
	 * @return
	 */
	public static Locale getLocale(final Language lang) {
		Locale outLocale = null;
		outLocale = switch (lang) {
						case ANY -> throw new IllegalStateException("unknown language");
						case BASQUE -> Languages.BASQUE_LOCALE;
						case DEUTCH -> Languages.DEUTCH_LOCALE;
						case ENGLISH -> Languages.ENGLISH_LOCALE;
						case FRENCH -> Languages.FRENCH_LOCALE;
						case SPANISH -> Languages.SPANISH_LOCALE;
						case KOREAN -> Languages.KOREAN_LOCALE;
						case POLISH -> Languages.POLISH_LOCALE;
						case SWEDISH -> Languages.SWEDISH_LOCALE;
						case HUNGARIAN -> Languages.HUNGARIAN_LOCALE;
						case CZECH -> Languages.CZECH_LOCALE;
						case ROMANIAN -> Languages.ROMANIAN_LOCALE;
						case JAPANESE -> Languages.JAPANESE_LOCALE;
						case RUSSIAN -> Languages.RUSSIAN_LOCALE;
						case ITALIAN -> Languages.ITALIAN_LOCALE;
						case PORTUGUESE -> Languages.PORTUGUESE_LOCALE;
						default -> Languages.getLocale(Language.DEFAULT);
		};
		return outLocale;
	}
	/**
	 * @return the language
	 */
	public static String language(final Language lang) {
		if (lang == Language.ANY) return "any";
		return Languages.getLocale(lang)
						.getLanguage();
	}
	/**
	 * @return the language
	 */
	public static String languageLowerCase(final Language lang) {
		return Languages.language(lang)
						.toLowerCase();
	}
	/**
	 * @return the language
	 */
	public static String languageUpperCase(final Language lang) {
		return Languages.language(lang)
						.toUpperCase();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a list of the countries
	 * @return ie: es,eu,en,fr,...
	 */
	public static String[] countries() {
		return _collectionBuilder(lang -> Languages.getLocale(lang).getCountry());
	}
	/**
	 * Returns a regular expression to match the countries
	 * @param captureGroup if the regular expression conforms a capture group
	 * @return (es|eu|en|fr...) if captureGroup=true and (?:es|eu|en|fr...) if captureGroup=false
	 */
	public static String countryMatchRegEx(final boolean captureGroup) {
		String[] countries = Languages.countries();
		return _matchRegEx(countries,
						   captureGroup);
	}
	/**
	 * Returns a list of the languages
	 * @return ie: es,eu,en,fr,...
	 */
	public static String[] languages() {
		return _collectionBuilder(lang -> Languages.getLocale(lang).getLanguage());
	}
	/**
	 * Returns the language name in a given language
	 * (ie: spanish in Language.SPANISH is "Español"
	 * @param lang
	 * @return
	 */
	public static String languageNameIn(final Language lang) {
		LanguageTexts names = LANGUAGE_NAMES.get(lang);
		if (names == null) return Languages.language(lang);
		String nameIn = names.get(lang);
		if (nameIn == null) return Languages.language(lang);
		return nameIn;
	}
	/**
	 * Returns a regular expression to match the language
	 * @param captureGroup if the regular expression conforms a capture group
	 * @return (es|eu|en|fr...) if captureGroup=true and (?:es|eu|en|fr...) if captureGroup=false
	 */
	public static String languageMatchRegEx(final boolean captureGroup) {
		String[] languages = Languages.languages();
		return _matchRegEx(languages,
						   captureGroup);
	}
	private static String[] _collectionBuilder(final Function<Language,String> valFunc) {
		String[] out = new String[Language.values().length-1];	// Language.UNKNOWN do not have locale
		int i=0;
		for (Language l : Language.values()) {
			if (l == Language.ANY) continue;
			if (Languages.getLocale(l) != null) out[i++] = valFunc.apply(l);
		}

		return out;
	}
	private static String _matchRegEx(final String[] col,
									  final boolean captureGroup) {
		StringBuilder sb = new StringBuilder(2 + col.length*3);	// ( + 2 chars, the country and the separator)
		sb.append(captureGroup ? "(" : "(?:");
		for (int i=0; i<col.length; i++) {
			sb.append(col[i]);
			if (i < col.length-1) sb.append("|");
		}
		sb.append(")");
		return sb.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Fakes a translation
	 * If the provided text is for example 'my text' and the language is ENGLISH,
	 * the returned fake translation is '(en) my text'
	 * @param text
	 * @param lang
	 * @return
	 */
	public static String fakeTranslate(final String text,
									   final Language lang) {
		return new StringBuilder(text.length() + 5)
					  .append("(").append(lang.getIso639_2()).append(") ")
					  .append(text)
					  .toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link String} comparator that takes the language into account
	 * when comparing
	 * @param lang
	 * @return
	 */
	public static Comparator<String> stringComparatorFor(final Language lang) {
		// use a collator to compare the names taking the locale into account
		Collator collator = Collator.getInstance(Languages.getLocale(lang));	// string comparator
		collator.setStrength(Collator.PRIMARY);

		return (arg0,arg1) -> collator.compare(arg0,arg1);
	}
	/**
	 * Returns a comparator that is aware of the Language order in the {@link Language} enum
	 * @return
	 */
	public static Comparator<Language> comparator() {
		return (o1,o2) -> o1.ordinal() == o2.ordinal()
									? 0
									: o1.ordinal() > o2.ordinal() ? 1
																  : -1;
	}
	/**
	 * Returns a comparator that in the first instance is aware of the given order and
	 * if the language is NOT in that list it ujses the order in the {@link Language} enuim
	 * @param prefixedOrder
	 * @return
	 */
	public static Comparator<Language> comparatorWithFirstElementsdOrder(final List<Language> prefixedOrder) {
		Comparator<Language> defComparator = Languages.comparator();
		return (o1,o2) -> {
							int o1Index = prefixedOrder.indexOf(o1);
							int o2Index = prefixedOrder.indexOf(o2);
							return o1Index >= 0 && o2Index >= 0 ? Integer.compare(o1Index,o2Index)
																: o1Index >= 0 ? -1					// if o1 exists at the prefixed order and o2 does NOT, o1 is first
																			   : o2Index >= 0 ? 1	// if o2 exists at the prefixed order and o1 does NOT, o2 is first
																					   		  : defComparator.compare(o1,o2);
					   };
	}
	/**
	 * Returns a comparator that in the first instance returns [basque] and [spanish] and afterwards
	 * the rest of Languages
	 * @return
	 */
	public static Comparator<Language> comparatorWithBasqueAndSpanishFirst() {
		return Languages.comparatorWithFirstElementsdOrder(Lists.newArrayList(Language.BASQUE,Language.SPANISH));
	}
	/**
	 * Returns an ordered copy of the given language collection using the also given {@link Comparator}
	 * @param langs
	 * @param comparator
	 * @return
	 */
	@SuppressWarnings("null")
	public static Set<Language> order(final Collection<Language> langs,final Comparator<Language> comparator) {
		return new LinkedHashSet<>(langs.stream()
										.sorted(comparator)
										.toList());
	}
	/**
	 * Returns an ordered copy of the given language collection returning the given languages in the first places
	 * @param langs
	 * @return
	 */
	@SuppressWarnings("null")
	public static Set<Language> orderWithPrefixedFirstElementsOrder(final Collection<Language> langs,final List<Language> prefixedOrder) {
		return new LinkedHashSet<>(langs.stream()
										.sorted(Languages.comparatorWithFirstElementsdOrder(prefixedOrder))
										.toList());
	}
	/**
	 * Returns an ordered copy of the given language collection returning [basque] and [spaninsh] in the first places
	 * @param langs
	 * @return
	 */
	@SuppressWarnings("null")
	public static Set<Language> orderWithBasqueAndSpanishFirst(final Collection<Language> langs) {
		return new LinkedHashSet<>(langs.stream()
										.sorted(Languages.comparatorWithBasqueAndSpanishFirst())
										.toList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Filter a collection of objects extending HasLanguage so it return only
	 * the objects with the given language
	 * @param links
	 * @return
	 */
	public static <L extends HasLanguage> Collection<L> filter(final Collection<L> hasLangCol,
															   final Language lang) {
		return CollectionUtils.hasData(hasLangCol)
					? hasLangCol.stream()
								.filter(link -> link.getLanguage().is(lang))
								.collect(Collectors.toList())
					: null;
	}
	/**
	 * Returns languages in l1 NOT present in l2
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static Set<Language> difference(final Set<Language> l1,final Set<Language> l2) {
		return Enums.difference(l1,l2);
	}
	/**
	 * Returns languages present both in l1 and l2
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static Set<Language> intersection(final Set<Language> l1,final Set<Language> l2) {
		return Enums.intersection(l1,l2);
	}
	/**
	 * Returns the union of both language sets
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static Set<Language> union(final Set<Language> l1,final Set<Language> l2) {
		return Enums.union(l1,l2);
	}
}
