package r01f.util.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.util.types.StringConverter;

/**
 * Usage:
 * <pre class='brush:java'>
 *		Instant now = Instant.now();
 *		String nowAsISOString = DateTimeMarshaller.forMarshalling(now)
 *												  .asISO8601String();
 *		Instant nowFromISOString = DateTimeMarshaller.forUnMarshallingFrom(nowAsISOString)
 *													 .asInstantAsummingISO8601Format()
 *													 .orElse(null);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class DateTimeMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public static Instant instantFromEPOCHMillis(final long epochMillis) {
		return StringConverter.asInstantFromEPOCHMillis(Long.toString(epochMillis),null);
	}
    public static LocalDate localDateFromEPOCDays(final long epochDays) {
    	return StringConverter.asLocalDateFromEPOCHDays(Long.toString(epochDays),null);
    }
    public static LocalDateTime localDateTimeFromEPOCHMillis(final long epochMillis) {
		return StringConverter.asLocalDateTimeFromEPOCHMillis(Long.toString(epochMillis),null);
	}
	public static DateTimeUnMarshallerFromString forUnMarshallingFrom(final String temporalAsString) {
        return new DateTimeMarshaller() { /* nothing */ }
        			.new DateTimeUnMarshallerFromString(temporalAsString);
    }
    public static InstantMarshallerToString forMarshalling(final Instant instant) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new InstantMarshallerToString(instant);
    }
    public static LocalDateMarshallerToString forMarshalling(final LocalDate localDate) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new LocalDateMarshallerToString(localDate);
    }
    public static LocalDateTimeMarshallerToString forMarshalling(final LocalDateTime localDateTime) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new LocalDateTimeMarshallerToString(localDateTime);
    }
    public static LocalTimeMarshallerToString forMarshalling(final LocalTime localTime) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new LocalTimeMarshallerToString(localTime);
    }
    public static OffsetDateTimeMarshallerToString forMarshalling(final OffsetDateTime offsetDateTime) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new OffsetDateTimeMarshallerToString(offsetDateTime);
    }
    public static ZonedDateTimeMarshallerToString forMarshalling(final ZonedDateTime zonedDateTime) {
    	return new DateTimeMarshaller() { /* nothing */ }
    				.new ZonedDateTimeMarshallerToString(zonedDateTime);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class DateTimeUnMarshallerFromString {
        private final CharSequence _temporalAsString;

        ////////// General
        public <T extends Temporal> Optional<T> asTemporalUsingPattern(final Class<T> temporalType,
																	   final String pattern,final Locale locale) {
			return Optional.ofNullable(StringConverter.asTemporalType(temporalType,
																 	  _temporalAsString,pattern,locale,
																 	  null));
		}
        ////////// Instant
        public Optional<Instant> asInstantAsummingISO8601OrEPOCHMillis() {
            return Optional.ofNullable(StringConverter.asInstantFromISO8601OrEPOCHMillis(_temporalAsString,
                                                                            			 null));
        }
        public Optional<Instant> asInstantAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asInstantFromISO8601(_temporalAsString,
                                                                            null));
        }
        public Optional<Instant> asInstantAsummingEPOCHMillisFormat() {
            return Optional.ofNullable(StringConverter.asInstantFromEPOCHMillis(_temporalAsString.toString(),
                                                                                null));
        }
        public Optional<Instant> asInstantUsingPattern(final String pattern,final Locale locale) {
            return Optional.ofNullable(StringConverter.asInstant(_temporalAsString,pattern,locale,
                                                                  null));
        }
        ////////// LocalDate
        public Optional<LocalDate> asLocalDateAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asLocalDateFromISO8601(_temporalAsString,
                                                                                 null));
        }
        public Optional<LocalDate> asLocalDateAsummingEPOCHDaysFormat() {
			return Optional.ofNullable(StringConverter.asLocalDateFromEPOCHDays(_temporalAsString,
																				 null));
		}
        public Optional<LocalDate> asLocalDateUsingPattern(final String pattern,final Locale locale) {
            return Optional.ofNullable(StringConverter.asLocalDate(_temporalAsString,pattern,locale,
                                                                   null));
        }
        public Optional<LocalDate> asLocalDateUsingStyle(final FormatStyle style,final Locale locale) {
            return Optional.ofNullable(StringConverter.asLocalDate(_temporalAsString,style,locale,
                                                                   null));
        }
        ////////// LocalDateTime
        public Optional<LocalDateTime> asLocalDateTimeAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asLocalDateTimeFromISO8601(_temporalAsString,
                                                                                  null));
        }
        public Optional<LocalDateTime> asLocalDateTimeAsummingEPOCHMillisFormat() {
        	return Optional.ofNullable(StringConverter.asLocalDateTimeFromEPOCHMillis(_temporalAsString.toString(),
																				      null));
        }
        public Optional<LocalDateTime> asLocalDateTimeUsingPattern(final String pattern,final Locale locale) {
            return Optional.ofNullable(StringConverter.asLocalDateTime(_temporalAsString,pattern,locale,
                                                                       null));
        }
        public Optional<LocalDateTime> asLocalDateTimeUsingStyle(final FormatStyle style,final Locale locale) {
            return Optional.ofNullable(StringConverter.asLocalDateTime(_temporalAsString,style,locale,
                                                                       null));
        }
        ////////// LocalTime
        public Optional<LocalTime> asLocalTimeAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asLocalTimeFromISO8601(_temporalAsString,
                                                                              null));
        }
        public Optional<LocalTime> asLocalTimeAsummingNanoOfDayFormat() {
			return Optional.ofNullable(StringConverter.asLocalTimeFromNanoOfDay(_temporalAsString.toString(),
																				null));
        }
        ////////// ZonedDateTime
        public Optional<ZonedDateTime> asZonedDateTimeAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asZonedDateTimeFromISO8601(_temporalAsString,
                                                                                  null));
        }
        ////////// ZonedDateTime
        public Optional<OffsetDateTime> asOffsetDateTimeAsummingISO8601Format() {
            return Optional.ofNullable(StringConverter.asOffsetDateTimeFromISO8601(_temporalAsString,
                                                                                   null));
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class InstantMarshallerToTemporal<T extends Temporal> {
    	private final T _temporal;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.format((Class<T>)_temporal.getClass(),
    										  _temporal,"iso",null);
    	}
    	public long asEPOHMillis() {
    		String epochStr = DateTimeFormatUtils.format((Class<T>)_temporal.getClass(),
    										  			 _temporal,"epochMillis",null);
    		return Long.parseLong(epochStr);
    	}
    	public String asStringUsingPattern(final String pattern,final Locale locale) {
    		return DateTimeFormatUtils.format((Class<T>)_temporal.getClass(),
    										  _temporal,pattern,locale);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class InstantMarshallerToString {
    	private final Instant _instant;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_instant);
    	}
    	public long asEPOHMillis() {
    		return DateTimeFormatUtils.formatAsEPOCHMillis(_instant);
    	}
    	public String asEPOCHMillisString() {
			return Long.toString(this.asEPOHMillis());
		}
    	public String asStringUsingPattern(final String pattern,final Locale locale) {
    		return DateTimeFormatUtils.format(_instant,pattern,locale);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class LocalDateMarshallerToString {
    	private final LocalDate _localDate;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_localDate);
    	}
    	public long asEPOHDays() {
    		return DateTimeFormatUtils.formatAsEPOCHDays(_localDate);
    	}
    	public String asStringUsingPattern(final String pattern,final Locale locale) {
    		return DateTimeFormatUtils.format(_localDate,pattern,locale);
    	}
    	public String asStringUsingStyle(final FormatStyle style,final Locale locale) {
    		return DateTimeFormatUtils.format(_localDate,style,locale);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class LocalDateTimeMarshallerToString {
    	private final LocalDateTime _localDateTime;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_localDateTime);
    	}
    	public long asEPOHMillis() {
			return DateTimeFormatUtils.formatAsEPOCHMillis(_localDateTime);
		}
    	public String asStringUsingPattern(final String pattern,final Locale locale) {
    		return DateTimeFormatUtils.format(_localDateTime,pattern,locale);
    	}
    	public String asStringUsingStyle(final FormatStyle style,final Locale locale) {
    		return DateTimeFormatUtils.format(_localDateTime,style,locale);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class LocalTimeMarshallerToString {
    	private final LocalTime _localTime;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_localTime);
    	}
    	public long asNanoOfDay() {
			return DateTimeFormatUtils.formatAsNanoOfDay(_localTime);
		}
    	public String asStringUsingPattern(final String pattern,final Locale locale) {
    		return DateTimeFormatUtils.format(_localTime,pattern,locale);
    	}
    	public String asStringUsingStyle(final FormatStyle style,final Locale locale) {
    		return DateTimeFormatUtils.format(_localTime,style,locale);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class OffsetDateTimeMarshallerToString {
    	private final OffsetDateTime _offsetDateTime;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_offsetDateTime);
    	}
    }
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class ZonedDateTimeMarshallerToString {
    	private final ZonedDateTime _zonedDateTime;

    	public String asISO8601String() {
    		return DateTimeFormatUtils.formatAsISO8601(_zonedDateTime);
    	}
    }
}
