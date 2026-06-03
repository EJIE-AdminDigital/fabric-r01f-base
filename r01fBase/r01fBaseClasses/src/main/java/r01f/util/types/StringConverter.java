package r01f.util.types;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.types.StringBase64;
import r01f.util.types.datetime.DateTimeFormatUtils;
import r01f.util.types.locale.Languages;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringConverter {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Wraps a CharSequence to be able to apply filters to it when converting it to a String
	 * @param value
	 * @return
	 */
	public static StringConverterWrapper wrap(final CharSequence value) {
		return new StringConverterWrapper(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A filter to be used when returning the underlying {@link CharSequence}
	 * as a {@link String}
	 */
	public interface StringConverterFilter {
		public String filter(String str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a boolean
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param val
	 * @param defValue
	 * @return
	 */
	public static Boolean asBoolean(final CharSequence val,final Boolean defValue) {
		if (val == null) return defValue;

		String value = val.toString();
		if ((value.equalsIgnoreCase("true"))
		 || (value.equalsIgnoreCase("on"))
		 ||	(value.equalsIgnoreCase("yes"))
		 || (value.equalsIgnoreCase("si"))
		 ||	(value.equalsIgnoreCase("bai"))) {
			return Boolean.TRUE;
		} else if ((value.equalsIgnoreCase("false"))
				|| (value.equalsIgnoreCase("off"))
				|| (value.equalsIgnoreCase("no"))
				|| (value.equalsIgnoreCase("ez"))) {
			return Boolean.FALSE;
		} else {
			return defValue;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BYTE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a byte
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Byte asByte(final CharSequence value,final Byte defValue) {
		if (value == null) return defValue;
		return Byte.parseByte(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a char
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Character asChar(final CharSequence value,final Character defValue) {
		if (value == null) return defValue;
		return value.charAt(0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DOUBLE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a double
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Double asDouble(final CharSequence value,final Double defValue) {
		if (value == null) return defValue;
		return Double.parseDouble(value.toString());	// bear formated numbers
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLOAT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a float
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Float asFloat(final CharSequence value,final Float defValue) {
		if (value == null) return defValue;
		return Float.parseFloat(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTEGER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an integer
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Integer asInteger(final CharSequence value,final Integer defValue) {
		if (value == null) return defValue;
		return Integer.parseInt(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LONG
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a long
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Long asLong(final CharSequence value,final Long defValue) {
		if (value == null) return defValue;
		return Long.parseLong(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SHORT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a short
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Short asShort(final CharSequence value,final Short defValue) {
		if (value == null) return defValue;
		return Short.parseShort(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LEGACY DATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param format
	 * @param defValue
	 * @return
	 */
	@Deprecated
	public static Date asDate(final CharSequence value,final String format,
							  final Date defValue) {
		if (value == null) return defValue;
		Instant instant = StringConverter.asInstant(value.toString(),format,
													null,	// locale
													null);	// def value
		return instant != null ? Date.from(instant) : defValue;
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link Date} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param format
	 * @param defValue
	 * @return
	 */
	@Deprecated
	public static LocalDate asLocalDate(final CharSequence value,final String format,
							  	   		final LocalDate defValue) {
		if (value == null) return defValue;
		// TODO refactor this
		LocalDate outLDate = null;
		try {
			outLDate = LocalDate.parse(value,DateTimeFormatter.ofPattern(format));
		} catch (DateTimeParseException parseEx) {
			outLDate = defValue;
		}
		return outLDate;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE / TIME
/////////////////////////////////////////////////////////////////////////////////////////
////////// Instant
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from an String
	 * First it detects if the String is in the ISO8601 string representation at UTC
	 * or in the EPOCH Millis representation at UTC
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Instant asInstantFromISO8601OrEPOCHMillis(final CharSequence value,
															final Instant defValue) {
		Instant outInstant = null;

		if (Strings.isNOTNullOrEmpty(value) && Numbers.isLong(value)) {
			outInstant = Instant.ofEpochMilli(Long.parseLong(value.toString()));
		}
		if (outInstant == null) {
			outInstant = Strings.isNOTNullOrEmpty(value) ? StringConverter.asInstantFromISO8601(value,null) : null;
		}
		return outInstant != null ? outInstant : defValue;
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from it's ISO8601 string representation at UTC
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Instant asInstantFromISO8601(final CharSequence value,
											   final Instant defValue) {
		return StringConverter.asInstant(value,"iso",
										 null,			// no locale needed
										 defValue);
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from EPOCH millis
	 * @param millis
	 * @param defValue
	 * @return
	 */
	public static Instant asInstantFromEPOCHMillis(final String millisStr,
												   final Instant defValue) {
		if (millisStr == null) return defValue;
		if (!Numbers.isLong(millisStr.toString())) throw new IllegalArgumentException(millisStr + " is NOT EPOCH millis!!");
		return StringConverter.asInstantFromEPOCHMillis(Numbers.toLong(millisStr),
														defValue);
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from EPOCH millis
	 * @param millis
	 * @param defValue
	 * @return
	 */
	public static Instant asInstantFromEPOCHMillis(final long millis,
												   final Instant defValue) {
		return StringConverter.asInstant(Long.toString(millis),"millis",
										 null,			// no locale needed
										 defValue);
	}
	/**
	 * Gets an Instant (Instant is ALWAYS at UTC!!!) from it's string representation
	 * It returns null if the provided String representation cannot be parsed to a Date
	 * If the pattern parameter is "milis" or "millis" or "epoch" it assumes that the date string is epoch milliseconds
	 * If the pattern parameter is "seconds" it assumes that the date string is in epoch seconds
	 * If the pattern parameter is "iso", "iso8601" it assumes that the date string is in iso/utc format
	 * @param value
	 * @param pattern
	 * @param locale only meaningful if the pattern contains a localizeable text (ie: mon, 1st january 2026)
	 * @param defValue
	 * @return
	 */
	public static Instant asInstant(final CharSequence value,final String pattern,
									final Locale locale,
									final Instant defValue) {
		if (value == null) return defValue;

		// [1] - Get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(Instant.class,
																				   	   pattern,locale);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
							  	  	   Instant::from);
	}
////////// ZonedDateTime
	/**
	 * Gets a ZonedDateTime from it's ISO8601 string representation (2011-12-03T10:15:30+01:00[Europe/Paris])
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static ZonedDateTime asZonedDateTimeFromISO8601(final CharSequence value,
 														   final ZonedDateTime defValue) {
		if (value == null) return defValue;
		return DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(value,
														   ZonedDateTime::from);
	}
////////// OffsetDateTime
	/**
	 * Gets a OffsetDateTime from it's ISO8601 string representation (2011-12-03T10:15:30+01:00)
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static OffsetDateTime asOffsetDateTimeFromISO8601(final CharSequence value,
 														     final OffsetDateTime defValue) {
		if (value == null) return defValue;
		return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value,
															OffsetDateTime::from);
	}
////////// LocalDate
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDate} using the ISO format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static LocalDate asLocalDateFromISO8601(final CharSequence value,
												   final LocalDate defValue) {
		return StringConverter.asLocalDate(value,"iso",
										   null,	// no need for locale
										   defValue);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDate} from EPOCH days
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static LocalDate asLocalDateFromEPOCHDays(final CharSequence value,
													 final LocalDate defValue) {
		if (value == null) return defValue;
		return LocalDate.ofEpochDay(Long.parseLong(value.toString()));
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDate} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param pattern
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalDate asLocalDate(final CharSequence value,final String pattern,
										final Locale locale,
							  	   		final LocalDate defValue) {
		if (value == null) return defValue;

		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalDate.class,
																				   	   pattern,locale);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalDate::from);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDate} using the given style
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param style
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalDate asLocalDate(final CharSequence value,final FormatStyle style,
										final Locale locale,
							  	   		final LocalDate defValue) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedDate(style)
												 				  				.withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedDate(style);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalDate::from);
	}
////////// LocalDateTime
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDateTime} using the ISO format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static LocalDateTime asLocalDateTimeFromISO8601(final CharSequence value,
												   	   	   final LocalDateTime defValue) {
		return StringConverter.asLocalDateTime(value,"iso",
										   	   null,	// no need for locale
										   	   defValue);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDateTime} from EPOCH millis (at UTC!!!)
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static LocalDateTime asLocalDateTimeFromEPOCHMillis(final CharSequence value,
															   final LocalDateTime defValue) {
		if (value == null) return defValue;
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(value.toString())),
									   ZoneId.of("UTC"));
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDateTime} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param pattern
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalDateTime asLocalDateTime(final CharSequence value,final String pattern,
												final Locale locale,
							  	   				final LocalDateTime defValue) {
		if (value == null) return defValue;

		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalDateTime.class,
																				   	   pattern,locale);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalDateTime::from);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalDateTime} using the given style
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param style
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalDateTime asLocalDateTime(final CharSequence value,final FormatStyle style,
												final Locale locale,
							  	   				final LocalDateTime defValue) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedDateTime(style)
												 				  				.withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedDateTime(style);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalDateTime::from);
	}
////////// LocalTime
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalTime} using the ISO format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static LocalTime asLocalTimeFromISO8601(final CharSequence value,
												   final LocalTime defValue) {
		return StringConverter.asLocalTime(value,"iso",
										   null,	// no need for locale
										   defValue);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalTime} from EPOCH nanos of day
	 * @param millisStr
	 * @param defValue
	 * @return
	 */
	public static LocalTime asLocalTimeFromNanoOfDay(final CharSequence millisStr,
													 final LocalTime defValue) {
		if (millisStr == null) return defValue;
		if (!Numbers.isLong(millisStr.toString())) throw new IllegalArgumentException(millisStr + " is NOT EPOCH millis!!");
		return LocalTime.ofNanoOfDay(Long.parseLong(millisStr.toString()));
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link Instant} using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param pattern
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalTime asLocalTime(final CharSequence value,final String pattern,
										final Locale locale,
							  	   		final LocalTime defValue) {
		if (value == null) return defValue;

		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(LocalTime.class,
																				   	   pattern,locale);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalTime::from);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link LocalTime} using the given style
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param style
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	public static LocalTime asLocalTime(final CharSequence value,final FormatStyle style,
										final Locale locale,
							  	   		final LocalDateTime defValue) {
		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = locale != null ? DateTimeFormatter.ofLocalizedTime(style)
												 				  				.withLocale(locale)
												 			 : DateTimeFormatter.ofLocalizedTime(style);
		// [2] - Parse & return
		return dateTimeFormatter.parse(value,
									   LocalTime::from);
	}
////////// Generic
	/**
	 * Returns the given {@link CharSequence} as a {@link Temporal} type using the given format
	 * as a pattern to get the date
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param temporalType
	 * @param value
	 * @param pattern
	 * @param locale only meaningful if the patterns contains a localizeable text
	 * @param defValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Temporal> T asTemporalType(final Class<T> temporalType,
														final CharSequence value,final String pattern,
														final Locale locale,
														final T defValue) {
		if (value == null) return defValue;

		// [1] - get the DateTimeFormatter
		DateTimeFormatter dateTimeFormatter = DateTimeFormatUtils.dateTimeFormatterFor(temporalType,
																				   	   pattern,locale);

		// [2] - Parse & return
		if (temporalType == Instant.class) {
			return (T)dateTimeFormatter.parse(value,
											  Instant::from);
		} else if (temporalType == LocalDate.class) {
			return (T)dateTimeFormatter.parse(value,
											  LocalDate::from);
		} else if (temporalType == LocalDateTime.class) {
			return (T)dateTimeFormatter.parse(value,
											  LocalDateTime::from);
		} else if (temporalType == LocalTime.class) {
			return (T)dateTimeFormatter.parse(value,
											  LocalTime::from);
		} else if (temporalType == OffsetDateTime.class) {
			return (T)dateTimeFormatter.parse(value,
											  OffsetDateTime::from);
		} else if (temporalType == ZonedDateTime.class) {
			return (T)dateTimeFormatter.parse(value,
											  ZonedDateTime::from);
		}
		throw new IllegalArgumentException("Unsupported Temporal type: " + temporalType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Path}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Path asPath(final CharSequence value,final Path defValue) {
		if (value == null) return defValue;
		return Path.from(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Language
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link Language}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static Language asLanguageFromCountryCode(final CharSequence value,final Language defValue) {
		if (value == null) return defValue;
		return Languages.fromLanguageCode(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends Enum<E>> E asEnumElement(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = Enum.valueOf(enumType,value.toString());
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends Enum<E> & EnumWithCode<String,E>> E asEnumElementFromStringCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCode.fromCode(value.toString(),enumType)
								   .orElse(defValue);
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends Enum<E> & EnumWithCode<Integer,E>> E asEnumElementFromIntCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCode.fromCode(Integer.parseInt(value.toString()),
											 enumType)
								   .orElse(defValue);
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E extends Enum<E> & EnumWithCode<Long,E>> E asEnumElementFromLongCode(final CharSequence value,final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = EnumWithCode.fromCode(Long.parseLong(value.toString()),enumType)
								   .orElse(defValue);
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value
			}
		}
		return outE;
	}
	/**
	 * Returns the given {@link CharSequence} as an {@link Enum} of the given type using a transformer {@link Function}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param transform
	 * @param enumType
	 * @param defValue
	 * @return
	 */
	public static <E> E asEnumElementFromString(final CharSequence value,final Function<CharSequence,E> transform,
												final Class<E> enumType,final E defValue) {
		E outE = defValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				outE = transform.apply(value);
			} catch (IllegalArgumentException illArgEx) {
				outE = defValue;	// there's NO enum value
			}
		}
		return outE;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuilder}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static StringBuilder asStringBuilder(final CharSequence value,final StringBuilder defValue) {
		if (value == null) {
			return defValue;
		}
		return new StringBuilder(value);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuilder}
	 * @param value
	 * @return
	 */
	public static StringBuilder asStringBuilder(final CharSequence value) {
		return StringConverter.asStringBuilder(value,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING BUFFER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuffer}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static StringBuffer asStringBuffer(final CharSequence value,final StringBuffer defValue) {
		if (value == null) {
			return defValue;
		}
		return new StringBuffer(value);
	}
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBuffer}
	 * @param value
	 * @return
	 */
	public static StringBuffer asStringBuffer(final CharSequence value) {
		return StringConverter.asStringBuffer(value,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHAR ARRAY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a char array
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static char[] asCharArray(final CharSequence value,final char[] defValue) {
		if (value == null) return defValue;
		return value.toString().toCharArray();
	}
	/**
	 * Returns the given {@link CharSequence} as a char array
	 * @param value
	 * @return
	 */
	public static char[] asCharArray(final CharSequence value) {
		if (value == null) return "".toString().toCharArray();
		return value.toString().toCharArray();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	STRING BASE 64
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the given {@link CharSequence} as a {@link StringBase64}
	 * ...or the given default value if the wrapped {@link CharSequence} is null
	 * @param value
	 * @param defValue
	 * @return
	 */
	public static StringBase64 asStringBase64(final CharSequence value,final StringBase64 defValue) {
		if (value == null) return defValue;
		return StringBase64.fromBase64EncodedString(value.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided
	 * @param str
	 */
	public static InputStream asInputStream(final CharSequence str) {
		return StringConverter.asInputStream(str,Charset.defaultCharset());
	}
	/**
	 * Returns an {@link InputStream} to the buffer, that's the {@link String} as an {@link InputStream}
	 * the stream's byte charset is provided
	 * @param str
	 * @param charset
	 */
	public static InputStream asInputStream(final CharSequence str,
											final Charset charset) {
		if (str == null) return null;
		return new ByteArrayInputStream(str.toString().getBytes(charset));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Reader} to the buffer, that's the {@link String} as a {@link Reader}
	 * @param str
	 */
	public static Reader asReader(final CharSequence str) {
		if (str == null) return null;
		return new StringReader(str.toString());
	}
}
