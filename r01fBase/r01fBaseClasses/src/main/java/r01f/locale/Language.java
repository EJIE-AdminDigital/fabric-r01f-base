package r01f.locale;


import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumWithCode;
import r01f.util.types.collections.CollectionUtils;

/**
 * Models languages supported by R01
 * see:
 * 		iso country codes: https://www.iso.org/iso-3166-country-codes.html
 * 		iso languages: https://www.loc.gov/standards/iso639-2/php/code_list.php
 */
@Accessors(prefix="_")
public enum Language
 implements EnumWithCode<Integer,Language> {
	// europe
	SPANISH		(10,"es","spa"),
	BASQUE		(11,"eu","eus"),
	ENGLISH		(20,"en","eng"),
	FRENCH		(30,"fr","fra"),
	DEUTCH		(40,"de","deu"),
	ITALIAN		(58,"it","ita"),
	PORTUGUESE	(59,"pt","por"),
	POLISH		(51,"pl","pol"),
	SWEDISH		(52,"sv","swe"),
	HUNGARIAN	(53,"hu","hun"),
	CZECH		(54,"cs","ces"),
	ROMANIAN	(55,"ro","ron"),
	RUSSIAN		(57,"ru","rus"),

	// oceania
	KOREAN		(50,"ko","kor"),
	JAPANESE	(56,"ja","jpn"),

	ANY			(0,null,null);

	public static Language DEFAULT = Language.SPANISH;		// TODO get it from properties

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<Integer> _codeType = Integer.class;
	@Getter private final Integer _code;

	// iso codes: see https://www.loc.gov/standards/iso639-2/php/code_list.php
	@Getter private final String _iso639_1;
	@Getter private final String _iso639_2;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private Language(final int code,
					 final String iso639_1,final String iso639_2) {
		_code = code;
		_iso639_1 = iso639_1;
		_iso639_2 = iso639_2;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Language fromName(final String name) {
		return EnumExtended.fromName(name,Language.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Language.class + " element with name=" + name));
	}
	public static Language fromString(final String s) {
		return Language.fromName(s);
	}
	public static Language fromCode(final int code) {
		return EnumWithCode.fromCode(code,Language.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Language.class + " element with code=" + code));
	}
	public static Language fromISO639Alpha1Code(final String code) {
		return EnumExtended.matching(l -> l.getIso639_1().equals(code),
									 Language.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Language.class + " elemente matching iso639_1 code=" + code));
	}
	public static Language fromISO639Alpha2Code(final String code) {
		return EnumExtended.matching(l -> l.getIso639_2().equals(code),
									 Language.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Language.class + " elemente matching iso639_2 code=" + code));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String name) {
		return EnumExtended.canBeFromName(name,Language.class);
	}
	public static boolean canBe(final int code) {
		return EnumWithCode.canBeFromCode(code,Language.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static String pattern() {
		return CollectionUtils.toStringSeparatedWith(Stream.of(Language.values())
														   .filter(lang -> lang != ANY)
														   .collect(Collectors.toList()),
													 '|');
	}	
	public static String patternForIso639_1() {
		return CollectionUtils.toStringSeparatedWith(Stream.of(Language.values())
														   .filter(lang -> lang != ANY)
														   .map(lang -> lang.getIso639_1())
														   .collect(Collectors.toList()),
													 '|');
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Set} of {@link Language}
	 * @return
	 */
	public static Set<Language> valueSet() {
		return new LinkedHashSet<>(Arrays.asList(Language.values()));
	}
}
