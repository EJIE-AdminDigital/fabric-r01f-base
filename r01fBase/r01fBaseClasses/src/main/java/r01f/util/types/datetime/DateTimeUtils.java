
package r01f.util.types.datetime;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Range;

/**
 * Date utils
 * (see http://www.odi.ch/prog/design/datetime.php)
 */
@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public abstract class DateTimeUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static long DAY_MILIS = 24 * 60 * 60 * 1000;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean isJavaDateTimeClass(final Class<?> rangeClass) {
		return rangeClass == Date.class
			|| rangeClass == java.sql.Date.class
			|| rangeClass == Instant.class
			|| rangeClass == LocalDate.class
			|| rangeClass == LocalDateTime.class
			|| rangeClass == LocalTime.class
			|| rangeClass == OffsetDateTime.class
			|| rangeClass == ZonedDateTime.class
			|| rangeClass == LocalTime.class;
	}
    /**
     * Validates a given {@link ZoneId}
     * @param zoneIdAsString
     * @return
     */
    public static boolean isSupportedZoneId(final String zoneIdAsString) {
        return ZoneId.getAvailableZoneIds()
        			 .contains(zoneIdAsString);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if an object is a java.util.Date or a java.sql.Date
	 * @param obj
	 * @return
	 */
	public static <T> boolean isDate(final T obj) {
		return obj instanceof java.util.Date || obj instanceof java.sql.Date;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EPOCH see http://www.epochconverter.com/
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the epoch time
	 */
	public static long epochTimeStamp() {
		return Instant.now()
					  .toEpochMilli();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE CONVERSIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * Returns the Date as milis
	 * @param date
	 * @return
	 */
	public static long asMillis(final Date date) {
		return DateTimeUtils.asEpochTimeStamp(date);
	}
	/**
	 * Return a Date as an epoch timeStamp
	 * @param date
	 * @return
	 */
	public static long asEpochTimeStamp(final Date date) {
		if (date == null) return Long.MIN_VALUE;
		return date.getTime();
	}
	/**
	 * Returns the Date as a {@link Timestamp}
	 * @param date
	 * @return
	 */
	public static Timestamp asSqlTimestamp(final Date date) {
		if (date == null) return null;
		Timestamp outTS = new Timestamp(date.getTime());
		return outTS;
	}
	/**
	 * Returns the Date as a Calendar
	 * @param date the Date
	 * @return the returned Calendar
	 */
	public static GregorianCalendar asCalendar(final Date date) {
		return DateTimeUtils.asCalendar(date,TimeZone.getDefault());
	}
	/**
	 * Returns the Date as a Calendar
	 * @param date the Date
	 * @return the returned Calendar
	 */
	public static GregorianCalendar asCalendar(final Date date,final TimeZone timeZone) {
		if (date == null) return null;
		GregorianCalendar outCal = new GregorianCalendar(timeZone);
		outCal.setTime(date);
		return outCal;
	}
	/**
	 * Returns the Date as an {@link Instant}
	 * @param date
	 * @return
	 */
	public static Instant asInstant(final Date date) {
		if (date == null) return null;
		return date.toInstant();
	}
	/**
	 * Return the Date as a {@link LocalDate}
	 * @param date
	 * @param zoneId
	 * @return
	 */
	public static LocalDate asLocalDate(final Date date,final ZoneId zoneId) {
		if (date == null) return null;
		return date.toInstant()
				   .atZone(zoneId)
				   .toLocalDate();
	}
	/**
	 * Return the Date as a {@link LocalDateTime}
	 * @param date
	 * @param zoneId
	 * @return
	 */
	public static LocalDateTime asLocalDateTime(final Date date,final ZoneId zoneId) {
		if (date == null) return null;
		return date.toInstant()
				   .atZone(zoneId)
				   .toLocalDateTime();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	legacy java.util.Date
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a date from it's milis representation
	 * @param milis
	 * @return
	 */
	@Deprecated
	public static Date dateFromMillis(final long milis) {
		return DateTimeUtils.dateFromEpochTimeStamp(milis);
	}
	/**
	 * Returns a date from it's epoch timestamp representation
	 * @param epochTimeStamp
	 * @return
	 */
	@Deprecated
	public static Date dateFromEpochTimeStamp(final long epochTimeStamp) {
		if (epochTimeStamp == Long.MIN_VALUE) return null;
		return epochTimeStamp > 0 ? new Date(epochTimeStamp)
								  : new Date();	// now
	}
	/**
	 * Returns a {@link Date} from a calendar
	 * @param cal
	 * @return
	 */
	@Deprecated
	public static Date dateFromCalendar(final Calendar cal) {
		if (cal == null) return null;
		return cal.getTime();
	}
	/**
	 * Returns a {@link Date} from a {@link Instant}
	 * @param instant
	 * @return
	 */
	@Deprecated
	public static Date dateFromInstant(final Instant instant) {
		if (instant == null) return null;
		return Date.from(instant);
	}
	/**
	 * Returns a {@link Date} from a {@link LocalDate}
	 * @param localDate
	 * @param zoneId
	 * @return
	 */
	@Deprecated
	public static Date dateFromLocalDate(final LocalDate localDate,final ZoneId zoneId) {
		if (localDate == null) return null;
		return Date.from(localDate.atStartOfDay(zoneId)
								  .toInstant());
	}
	/**
	 * Returns a {@link Date} from a {@link LocalDateTime}
	 * @param localDateTime
	 * @param zoneId
	 * @return
	 */
	@Deprecated
	public static Date dateFromLocalDateTime(final LocalDateTime localDateTime,final ZoneId zoneId) {
		if (localDateTime == null) return null;
		return Date.from(localDateTime.atZone(zoneId)
									  .toInstant());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ROLLING
/////////////////////////////////////////////////////////////////////////////////////////
	public static Instant rollToEndOfDay(final Instant instant) {
		return instant.with(LocalTime.MAX);
	}
	public static Instant rollToStartOfDay(final Instant instant) {
		return instant.with(LocalTime.MIN);
	}
	public static LocalDateTime rollToEndOfDay(final LocalDateTime ldt) {
		return ldt.with(LocalTime.MAX);
	}
	public static LocalDateTime rollToStartOfDay(final LocalDateTime ldt) {
		return ldt.with(LocalTime.MIN);
	}
	public static LocalDateTime rollToEndOfMonth(final LocalDateTime ldt) {
		return ldt.with(TemporalAdjusters.lastDayOfMonth())
				  .with(LocalTime.MAX);
	}
	public static LocalDateTime rollToStartOfMonth(final LocalDateTime ldt) {
		return ldt.with(TemporalAdjusters.firstDayOfMonth())
				  .with(LocalTime.MIN);
	}
	public static LocalDateTime rollToEndOfYear(final LocalDateTime ldt) {
		return ldt.with(TemporalAdjusters.lastDayOfYear())
				  .with(LocalTime.MAX);
	}
	public static LocalDateTime rollToStartOfYear(final LocalDateTime ldt) {
		return ldt.with(TemporalAdjusters.firstDayOfYear())
				  .with(LocalTime.MIN);
	}
	public static LocalDate rollToEndOfMonth(final LocalDate ld) {
		return ld.with(TemporalAdjusters.lastDayOfMonth());
	}
	public static LocalDate rollToStartOfMonth(final LocalDate ld) {
		return ld.with(TemporalAdjusters.firstDayOfMonth());
	}
	public static LocalDate rollToEndOfYear(final LocalDate ld) {
		return ld.with(TemporalAdjusters.lastDayOfYear());
	}
	public static LocalDate rollToStartOfYear(final LocalDate ld) {
		return ld.with(TemporalAdjusters.firstDayOfYear());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DAYS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first day datetime (00:00:01)
	 * @param date
	 * @return
	 */
	public static LocalDateTime dayStartOf(final Instant date,final ZoneId zone) {
		return LocalDateTime.ofInstant(date,
									   zone)
							.with(LocalTime.MIN);
	}
	/**
	 * Returns the first day datetime (00:00:01)
	 * @param date
	 * @return
	 */
	public static LocalDateTime nextDayStartOf(final Instant date,final ZoneId zone) {
		return LocalDateTime.ofInstant(date,
									   zone)
							.plusDays(1)
							.with(LocalTime.MIN);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	WEEKS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first instant of a week given it's number within a year
	 * @param year
	 * @param weekOfYear
	 * @return
	 */
	public static LocalDate weekFirstDay(final Year year,final int weekOfYear,
										 final Locale locale) {
		DayOfWeek firstDayOfWeek = WeekFields.of(locale)
											 .getFirstDayOfWeek();
		return LocalDate.of(year.getValue(),1,1)	// first day of year
					  	.with(ChronoField.ALIGNED_WEEK_OF_YEAR,weekOfYear)
						.with(firstDayOfWeek);
	}
	/**
	 * Returns the first instant of a week given it's number within a year
	 * @param year
	 * @param weekOfYear
	 * @return
	 */
	public static LocalDate weekLastDay(final Year year,final int weekOfYear,
										final Locale locale) {
		DayOfWeek lastDayOfWeek = WeekFields.of(locale)
											.getFirstDayOfWeek()
											.plus(6);
		return LocalDate.of(year.getValue(),1,1)	// first day of year
					  	.with(ChronoField.ALIGNED_WEEK_OF_YEAR,weekOfYear)
						.with(lastDayOfWeek);
	}
	/**
	 * The first day of the week for the given locale
	 * @param locale
	 * @return
	 */
	public DayOfWeek firstDayOfWeek(final Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);
		return weekFields.getFirstDayOfWeek();
	}
	public DayOfWeek lastDayOfWeek(final Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);
		DayOfWeek firstDay = weekFields.getFirstDayOfWeek();
		return firstDay.plus(6);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MONTHS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first instant of a month
	 * @param year
	 * @param monthOfYear
	 * @return
	 */
	public static LocalDate monthFirstDay(final Year year,final Month monthOfYear) {
		return year.atMonth(monthOfYear)
				   .atDay(1);
	}
	/**
	 * Returns the first instant of a month
	 * @param year
	 * @param monthOfYear
	 * @return
	 */
	public static LocalDate monthLastDay(final Year year,final Month monthOfYear) {
		return year.atMonth(monthOfYear)
				   .atEndOfMonth();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a range of instants representing the day interval of the given instant
	 * @param instant
	 * @return
	 */
	public static Range<Instant> dayRangeOf(final Instant instant) {
		Instant dayStart = DateTimeUtils.rollToStartOfDay(instant);
		Instant nextDayStart = DateTimeUtils.rollToStartOfDay(instant.plus(Duration.ofDays(1)));
		return new Range<>(dayStart,nextDayStart);
	}
	/**
	 * Return a Range between now and ( now - amount/unit ), sample : Dates.rangeBeforeNow(5,ChronoUnit.MINUTES)
	 * @param amount
	 * @param unit
	 * @return
	 */
	public static Range<Instant> rangeBeforeNow(final int amount,final TemporalUnit unit,final ZoneOffset offset) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime before = now.minus(Duration.ofSeconds(unit.getDuration().getSeconds() * amount));
		return Range.closed(before.toInstant(offset),now.toInstant(offset));
	}
	public static Range<Instant> rangeBeforeNow(final int amount,final TemporalUnit unit) {
		// ..OffsetDateTime.now().getOffset(), returns system offset,  don't use ZoneId.systemDefault(), just returns the id.
		return DateTimeUtils.rangeBeforeNow(amount,unit,OffsetDateTime.now().getOffset());
	}
	/**
	 * Return a Range between now and ( now - amount/unit ), sample : Dates.rangeAfterNow(5,ChronoUnit.MINUTES)
	 * @param amount
	 * @param unit
	 * @return
	 */
	public static Range<Instant> rangeAfterNow(final int amount,final TemporalUnit unit,final ZoneOffset offset) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime after = now.minus(Duration.ofSeconds(unit.getDuration().getSeconds() * amount));
		return Range.closed(now.toInstant(offset),after.toInstant(offset));
	}
	public static Range<Instant> rangeAfterNow(final int amount,final TemporalUnit unit) {
		// ..OffsetDateTime.now().getOffset(), returns system offset,  don't use ZoneId.systemDefault(), just returns the id.
		return DateTimeUtils.rangeAfterNow(amount,unit,OffsetDateTime.now().getOffset());
	}
	/**
	 * Return a Range<Date> from a tst based Range (Range<Long>)
	 * @param amount
	 * @param unit
	 * @return
	 */
	public static Range<Instant> fromTSTRange(final Range<Long> range) {
		Instant lower =  Instant.ofEpochMilli(range.getLowerBound()); 	// lower 0 // Thursday, 1 January 1970 0:00:00
		Instant upper =  range.getUpperBound() == 0 ? Instant.now()   	// no sense  upper 0 , default: Instant.now
												 	: Instant.ofEpochMilli(range.getUpperBound());
		return new Range<>(lower,range.lowerBoundType(),upper,range.getUpperBoundType());
	}
}
