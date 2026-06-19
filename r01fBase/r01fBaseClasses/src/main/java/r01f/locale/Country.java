package r01f.locale;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumWithCode;

/**
 * Models countries supported by R01
 * see:
 * 		iso country codes: http://kirste.userpage.fu-berlin.de/diverse/doc/ISO_3166.html
 */
@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum Country
 implements EnumWithCode<Integer,Country> {
	// europe
	SPAIN			(724,"ES","ESP"),
	UNITED_KINGDOM	(826,"GB","GBR"),
	FRANCE			(250,"FR","FRA"),
	GERMANY			(276,"DE","DEU"),
	ITALY			(380,"IT","ITA"),
	PORTUGAL		(620,"PT","PRT"),
	POLAND			(616,"PL","POL"),
	SWEDEN			(752,"SE","SWE"),
	HUNGARY			(348,"HU","HUN"),
	CZECHIA			(203,"CZ","CZE"),
	ROMANIA			(642,"RO","ROU"),
	RUSSIA			(643,"RU","RUS"),
	AUSTRIA			(40,"AT","AUT"),
	GREECE			(300,"GR","GRC"),
	IRELAND			(372,"IE","IRL"),
	FINLAND			(246,"FI","FIN"),
	NETHERLANDS		(528,"NL","NLD"),
	BELGIUM			(56,"BE","BEL"),

	// america
	USA				(840,"US","USA"),

	// oceania
	KOREA_SOUTH		(410,"KR","KOR"),
	JAPAN			(392,"JP","JPN"),

	ANY				(0,null,null);

	public static Country DEFAULT = Country.SPAIN;		// TODO get it from properties

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<Integer> _codeType = Integer.class;
	@Getter private final Integer _code;

	// iso codes: see https://www.iso.org/iso-3166-country-codes.html > https://www.iso.org/obp/ui/#search
	@Getter private final String _iso3166_2;	// two letter code, e.g. ES for Spain
	@Getter private final String _iso3166_3;	// three letter code, e.g. ESP for Spain

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private Country(final int code,
					final String iso3166_alpha1,final String iso3166_alpha2) {
		_code = code;
		_iso3166_2 = iso3166_alpha1;
		_iso3166_3 = iso3166_alpha2;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Country fromName(final String name) {
		return EnumExtended.fromName(name,Country.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Country.class + " element with name=" + name));
	}
	public static Country fromString(final String s) {
		return Country.fromName(s);
	}
	public static Country fromCode(final int code) {
		return EnumWithCode.fromCode(code,Country.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Country.class + " element with code=" + code));
	}
	public static Country fromISO3166Alpah1Code(final String code) {
		return EnumExtended.matching(ty -> ty.getIso3166_2().equals(code),
									 Country.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Country.class + " element matching iso3166 alpha1 code=" + code));
	}
	public static Country fromISO3166Alpah2Code(final String code) {
		return EnumExtended.matching(ty -> ty.getIso3166_2().equals(code),
									 Country.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + Country.class + " element matching iso3166 alpha2 code=" + code));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CAN BE
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String name) {
		return EnumExtended.canBeFromName(name,Country.class);
	}
	public static boolean canBe(final int code) {
		return EnumWithCode.canBeFromCode(code,Country.class);
	}
	public static boolean canBeFromISO3166Alpha1Code(final String code) {
		return EnumSet.allOf(Country.class)
					  .stream()
					  .anyMatch(ty -> ty.getIso3166_2().equals(code));
	}
	public static boolean canBeFromISO3166Alpha2Code(final String code) {
		return EnumSet.allOf(Country.class)
					  .stream()
					  .anyMatch(ty -> ty.getIso3166_2().equals(code));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Set} of {@link Country}
	 * @return
	 */
	public static Set<Country> valueSet() {
		return new LinkedHashSet<>(Arrays.asList(Country.values()));
	}
}
