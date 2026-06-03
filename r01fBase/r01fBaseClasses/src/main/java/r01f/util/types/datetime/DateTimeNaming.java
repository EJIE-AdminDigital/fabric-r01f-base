package r01f.util.types.datetime;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Country;
import r01f.locale.Language;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DateTimeNaming {
/////////////////////////////////////////////////////////////////////////////////////////
//	DAY OF WEEK NAMES
/////////////////////////////////////////////////////////////////////////////////////////
	public static String getDayOfWeekName(final DayOfWeek dayOfWeek,final Language language,final Country country) {
		Locale loc = new Locale(language.getIso639_1(),country.getIso3166_2());
		return DateTimeNaming.getDayOfWeekName(dayOfWeek,loc);
	}
	public static String getDayOfWeekName(final DayOfWeek dayOfWeek,final Locale loc) {
		// full name (usually lowercase in CLDR for Spanish)
		String full = dayOfWeek.getDisplayName(TextStyle.FULL,loc);        	// e.g. "lunes"
		return Character.toUpperCase(full.charAt(0)) + full.substring(1); 	// "Lunes"
	}
	public static String getDayOfWeekShortName(final DayOfWeek dayOfWeek,final Language language,final Country country) {
		Locale loc = new Locale(language.getIso639_1(),country.getIso3166_2());
		return DateTimeNaming.getDayOfWeekShortName(dayOfWeek,loc);
	}
	public static String getDayOfWeekShortName(final DayOfWeek dayOfWeek,final Locale loc) {
		// short name (usually lowercase in CLDR for Spanish)
		String shortName = dayOfWeek.getDisplayName(TextStyle.SHORT,loc);        	// e.g. "lun."
		return Character.toUpperCase(shortName.charAt(0)) + shortName.substring(1); // "Lun."
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MONTH NAMES
/////////////////////////////////////////////////////////////////////////////////////////
	public static String getMonthName(final Month month,final Language language,final Country country) {
		Locale loc = new Locale(language.getIso639_1(),country.getIso3166_2());
		return DateTimeNaming.getMonthName(month,loc);
	}
	public static String getMonthName(final Month month,final Locale loc) {
		// full name (usually lowercase in CLDR for Spanish)
		String full = month.getDisplayName(TextStyle.FULL,loc);        	  // e.g. "enero"
		return Character.toUpperCase(full.charAt(0)) + full.substring(1); // "Enero"
	}
	public static String getMonthShortName(final Month month,final Language language,final Country country) {
		Locale loc = new Locale(language.getIso639_1(),country.getIso3166_2());
		return DateTimeNaming.getMonthShortName(month,loc);
	}
	public static String getMonthShortName(final Month month,final Locale loc) {
		// short name (usually lowercase in CLDR for Spanish)
		String shortName = month.getDisplayName(TextStyle.SHORT,loc);        	// e.g. "ene."
		return Character.toUpperCase(shortName.charAt(0)) + shortName.substring(1); // "Ene."
	}
}