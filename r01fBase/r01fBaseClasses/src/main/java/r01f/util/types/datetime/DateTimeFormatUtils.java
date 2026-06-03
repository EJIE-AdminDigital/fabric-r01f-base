package r01f.util.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.util.types.Numbers;
import r01f.util.types.StringConverter;
import r01f.util.types.Strings;
import r01f.util.types.locale.Languages;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DateTimeFormatUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String FORMAT_PATTERN_FOR_ISO_INSTANT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";
	public static final String FORMAT_PATTERN_FOR_ISO_LOCAL_DATE = "yyyy-MM-dd";
	public static final String FORMAT_PATTERN_FOR_ISO_LOCAL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";
	public static final String FORMAT_PATTERN_FOR_ISO_LOCAL_TIME = "HH:mm:ss.SSSSSSS";
	public static final String FORMAT_PATTERN_FOR_ISO_OFFSET_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX";
	public static final String FORMAT_PATTERN_FOR_ISO_ZONED_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX'['VV']'";


	public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_INSTANT
											  										.withZone(ZoneId.of("UTC"));	// iso always at UTC
	// EPOCH FORMATTERS
	//		EPOCH SECONDS: a 10 digit number representing seconds since epoch
	//	 	EPOCH MILIS: a 13 digit number where the first 10 digits are seconds and the last 3 digits are milliseconds
	//		EPOCH NANOS: a 19 digit number where the first 10 digits are seconds and the last 9 digits are nanoseconds
	public static final DateTimeFormatter EPOCH_MILLIS_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
																	                .appendValue(ChronoField.INSTANT_SECONDS,10)	// Parse the first 10 digits as seconds
																	                .appendValue(ChronoField.MILLI_OF_SECOND,3)		// Parse the next digits as milliseconds
																	                .toFormatter();
	public static final DateTimeFormatter EPOCH_SECONDS_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
																					.appendValue(ChronoField.INSTANT_SECONDS)		// Parse the first 10 digits as seconds
																					.toFormatter();
/////////////////////////////////////////////////////////////////////////////////////////
//	FORMAT STYLE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the language-dependent format from the provided map
	 * @param lang
	 * @param formatStyle
	 * @return
	 */
	public static DateTimeFormatter localDateFormatterFor(final Language lang,final FormatStyle formatStyle) {
		return DateTimeFormatter.ofLocalizedDate(formatStyle)
						  		.withLocale(Languages.getLocale(lang));
	}
	/**
	 * Gets the language-dependent format from the provided map
	 * @param lang
	 * @param formatStyle
	 * @return
	 */
	public static DateTimeFormatter localDateTimeFormatterFor(final Language lang,final FormatStyle formatStyle) {
		return DateTimeFormatter.ofLocalizedDateTime(formatStyle)
						  		.withLocale(Languages.getLocale(lang));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE TIME
/////////////////////////////////////////////////////////////////////////////////////////
////////// Legacy Date
	/**
	 * Returns a date formated as ISO8601 (yyyy-MM-dd'T'HH:mm'Z') GMT (greenwich meridian time) / UTC (coordinate universal time) time
	 * @param date
	 * @return
	 */
	@Deprecated
	public static String formatAsISO8601AtUTC(final Date date) {
		// ISO 8601 with UTC timezone (e.g., "2024-12-20T10:30:45Z")
		// String iso8601 = date.toInstant()	// instant is always in UTC
		//					 .toString();
		return DateTimeFormatUtils.formatAsISO8601(date.toInstant());
	}
////////// Generic Temporal
	/**
	 * Gets the Date formated
	 * If the pattern parameter is "milis" or "millis" or "epoch" it returns epoch milliseconds as a long
	 * If the pattern parameter is "seconds" it returns epoch seconds as long
	 * If the pattern parameter is "iso", "iso8601" it returns ths instant in iso/utc format
	 * @param type
	 * @param value
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static <T extends Temporal> String format(final Class<T> type,
													 final T value,final String pattern,
													 final Locale locale) {
		if (value == null) return null;

		// [1] - Get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(type,
																					   pattern,
																					   locale);
		// [2] - format
		return dateTimeFormatter.format(value);
	}
////////// Instant
	/**
	 * Returns a date formated as ISO8601 (yyyy-MM-dd'T'HH:mm'Z') GMT (greenwich meridian time) / UTC (coordinate universal time) time
	 * @param instant
	 * @return
	 */
	public static String formatAsISO8601(final Instant instant) {
		return DateTimeFormatUtils.format(instant,"iso",
										  null);
	}
	/**
	 * Returns a date formated as millis (Instant is ALWAYS at UTC!!!)
	 * @param instant
	 * @return
	 */
	public static long formatAsEPOCHMillis(final Instant instant) {
		String millisStr = DateTimeFormatUtils.format(instant,"millis",
										  			  null);	// no need for locale
		return Strings.isNOTNullOrEmpty(millisStr) && Numbers.isLong(millisStr) ? Long.parseLong(millisStr) : null;
	}
	/**
	 * Gets the Date formated
	 * If the pattern parameter is "milis" or "millis" or "epoch" it returns epoch milliseconds as a long
	 * If the pattern parameter is "seconds" it returns epoch seconds as long
	 * If the pattern parameter is "iso", "iso8601" it returns ths instant in iso/utc format
	 * @param instant
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String format(final Instant instant,final String pattern,
								final Locale locale) {
		if (instant == null) return null;

		// [1] - Get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(Instant.class,
																					   pattern,
																					   locale);
		// [2] - format
		return dateTimeFormatter.format(instant);
	}
////////// LocalDate
	/**
	 * Returns a date formated as ISO8601 (yyyy-MM-dd)
	 * @param ldate
	 * @param zone
	 * @return
	 */
	public static String formatAsISO8601(final LocalDate ldate) {
		return ldate.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	/**
	 * Returns a local date as EPOCH days
	 * @param ldate
	 * @return
	 */
	public static long formatAsEPOCHDays(final LocalDate ldate) {
		return ldate.toEpochDay();
	}
	/**
	 * Returns a local date formatted as an String
	 * @param ldate
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String format(final LocalDate ldate,final String pattern,
							    final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalDate.class,
																					   pattern,
																					   locale);
		// [2] - Format
		return dateTimeFormatter.format(ldate);
	}
	/**
	 * Returns a local date formatted as an String with the given style
	 * @param ldate
	 * @param style
	 * @param locale
	 * @return
	 */
	public static String format(final LocalDate ldate,final FormatStyle style,
								final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedDate(style)
												 				  .withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedDate(style);
		// [2] - Format
		return dateTimeFormatter.format(ldate);
	}
////////// LocalDateTime
	/**
	 * Returns a date formated as ISO8601 (yyyy-MM-dd'T'HH:mm:ss)
	 * @param ldateTime
	 * @param zone
	 * @return
	 */
	public static String formatAsISO8601(final LocalDateTime ldateTime) {
		return ldateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
	/**
	 * Returns a date formated as millis (ALWAYS at UTC!!!)
	 * @param instant
	 * @return
	 */
	public static long formatAsEPOCHMillis(final LocalDateTime dt) {
		return DateTimeFormatUtils.formatAsEPOCHMillis(dt.atZone(ZoneOffset.UTC)	// creates a ZonedDateTime at UTC
						     							 .toInstant());
	}
	/**
	 * Returns a local date formatted as an String
	 * @param ldateTime
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String format(final LocalDateTime ldateTime,final String pattern,
							    final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalDateTime.class,
																					   pattern,
																					   locale);
		// [2] - Format
		return dateTimeFormatter.format(ldateTime);
	}
	/**
	 * Returns a local datetime formatted as an String with the given style
	 * @param ldateTime
	 * @param style
	 * @param locale
	 * @return
	 */
	public static String format(final LocalDateTime ldateTime,final FormatStyle style,
								final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedDateTime(style)
												 				  				.withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedDateTime(style);
		// [2] - Format
		return dateTimeFormatter.format(ldateTime);
	}
////////// LocalTime
	/**
	 * Returns a time formated as ISO8601 (HH:mm:ss)
	 * @param lTime
	 * @param zone
	 * @return
	 */
	public static String formatAsISO8601(final LocalTime lTime) {
		return lTime.format(DateTimeFormatter.ISO_TIME);
	}
	/**
	 * Returns a local time as nano of day
	 * @param lTime
	 * @return
	 */
	public static long formatAsNanoOfDay(final LocalTime lTime) {
		return lTime.toNanoOfDay();
	}
	/**
	 * Returns a local date formatted as an String
	 * @param lTime
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String format(final LocalTime lTime,final String pattern,
							    final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalTime.class,
																					   pattern,
																					   locale);
		// [2] - Format
		return dateTimeFormatter.format(lTime);
	}
	/**
	 * Returns a local time formatted as an String with the given style
	 * @param ltime
	 * @param style
	 * @param locale
	 * @return
	 */
	public static String format(final LocalTime ltime,final FormatStyle style,
								final Locale locale) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedTime(style)
																				.withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedTime(style);
		// [2] - Format
		return dateTimeFormatter.format(ltime);
	}
////////// ZonedDateTime / OffsetDateTime
	/**
	 * Returns a date formated as ISO8601 (2011-12-03T10:15:30+01:00[Europe/Paris])
	 * @param instant
	 * @return
	 */
	public static String formatAsISO8601(final ZonedDateTime zonedDateTime) {
		// ISO 8601 with specific timezone (e.g., "2024-12-20T11:30:45+01:00")
		return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	/**
	 * Returns a date formated as ISO8601 (2011-12-03T10:15:30+01:00)
	 * @param instant
	 * @return
	 */
	public static String formatAsISO8601(final OffsetDateTime zonedDateTime) {
		// ISO 8601 with specific timezone (e.g., "2024-12-20T11:30:45+01:00")
		return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T extends Temporal> DateTimeFormatter dateTimeFormatterFor(final Class<T> type,
																			  final String pattern,final Locale locale) {
		DateTimeFormatter dateTimeFormatter = null;

		// [1] - If the given pattern is not null/empty, use it
		if (Strings.isNOTNullOrEmpty(pattern)) {
			if (pattern.equalsIgnoreCase("iso")
		     || pattern.equalsIgnoreCase("iso8601")) {
				// iso formatter
				dateTimeFormatter = ISO_DATETIMEFORMATTERS.get(type);
				if (dateTimeFormatter == null) throw new IllegalArgumentException("Unsupported temporal type: " + type);
			}
			else if (pattern.equalsIgnoreCase("millis") || pattern.equalsIgnoreCase("milis")
					|| pattern.equalsIgnoreCase("epoch") || pattern.equalsIgnoreCase("epochMillis")) {		// bug with millis WTF!
				// epoch millis	formatter (only for Instant)
				if (type != Instant.class) throw new IllegalArgumentException("EPOCH millis pattern is only supported for Instant type");

				dateTimeFormatter = DateTimeFormatUtils.EPOCH_MILLIS_DATETIME_FORMATTER;
			}
			else if (pattern.equalsIgnoreCase("seconds") || pattern.equalsIgnoreCase("secs")
				  || pattern.equalsIgnoreCase("epochSeconds")) {
				// seconds formatter	(only for Instant)
				if (type != Instant.class) throw new IllegalArgumentException("EPOCH millis pattern is only supported for Instant type");

				dateTimeFormatter = DateTimeFormatUtils.EPOCH_SECONDS_DATETIME_FORMATTER;
			}
			else {
				// use the given pattern
				dateTimeFormatter = locale != null ? DateTimeFormatter.ofPattern(pattern,locale)
												   : DateTimeFormatter.ofPattern(pattern);
			}
		}
		// [2] - Else, use the default formatters
		else {
			dateTimeFormatter = DEFAULT_DATETIMEFORMATTERS.get(type)
														  .apply(locale);
			if (dateTimeFormatter == null) throw new IllegalArgumentException("Unsupported temporal type: " + type);
		}
		// Instant is ALWAYS at UTC!!!
		if (type == Instant.class) dateTimeFormatter = dateTimeFormatter.withZone(ZoneId.of("UTC"));

		// [99] - return
		return dateTimeFormatter;
	}
	private static final Map<Class<? extends Temporal>,
							 DateTimeFormatter> ISO_DATETIMEFORMATTERS = Map.of(
																				Instant.class,DateTimeFormatter.ISO_INSTANT,
																				LocalDate.class,DateTimeFormatter.ISO_LOCAL_DATE,
																				LocalDateTime.class,DateTimeFormatter.ISO_LOCAL_DATE_TIME,
																				LocalTime.class,DateTimeFormatter.ISO_LOCAL_TIME,
																				OffsetDateTime.class,DateTimeFormatter.ISO_OFFSET_DATE_TIME,
																				ZonedDateTime.class,DateTimeFormatter.ISO_ZONED_DATE_TIME
																		   );
	private static final Map<Class<? extends Temporal>,
							 Function<Locale,DateTimeFormatter>> DEFAULT_DATETIMEFORMATTERS = Map.of(
																										Instant.class,locale -> DateTimeFormatter.ISO_INSTANT,
																										LocalDate.class,locale -> locale != null ? DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
												 				  											  														.withLocale(locale)
												 				  											  									 : DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT),
																										LocalDateTime.class,locale -> locale != null ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
												 				  											  															.withLocale(locale)
												 				  											  										 : DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT),
																										LocalTime.class,locale -> locale != null ? DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
												 				  											  														.withLocale(locale)
												 				  											  									 : DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT),
																										OffsetDateTime.class,locale -> DateTimeFormatter.ISO_OFFSET_DATE_TIME,
																										ZonedDateTime.class,locale -> DateTimeFormatter.ISO_ZONED_DATE_TIME
																								   );
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Reformats a date as string to another format
	 * @param dateStr the date in the source format
	 * @param sourcePattern the source format
	 * @param targetPattern the target format
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @return
	 */
	public static String reformat(final String dateStr,
								  final String sourcePattern,final String targetPattern) {
		return DateTimeFormatUtils.reformat(dateStr,
											sourcePattern,targetPattern,
											Locale.getDefault());
	}
	/**
	 * Reformats a date as string to another format
	 * @param dateStr the date in the source format
	 * @param sourcePattern the source format
	 * @param targetPattern the target format
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @return
	 */
	public static String reformat(final String dateStr,
								  final String sourcePattern,final String targetPattern,
								  final Locale locale) {
		String theOldFmt = Strings.isNullOrEmpty(sourcePattern) ? FORMAT_PATTERN_FOR_ISO_INSTANT : sourcePattern;
		String theNewFmt = Strings.isNullOrEmpty(targetPattern) ? FORMAT_PATTERN_FOR_ISO_INSTANT : targetPattern;

		Instant instant = StringConverter.asInstant(dateStr,theOldFmt,
													locale,
													null);	// default value
		return DateTimeFormatUtils.format(instant,
											   theNewFmt,locale);
	}
}
