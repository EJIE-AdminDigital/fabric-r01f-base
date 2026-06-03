package r01f.types.datetime;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="weekOfYear")
public record WeekOfMonth(int weekOfMonth)
   implements Serializable,
  			  CanBeRepresentedAsString,
  			  Comparable<WeekOfMonth> {

	private static final long serialVersionUID = 8445517567471520680L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(1|2|3|4|5)";
	public static final String REGEX_NOCAPTURE = "(?:1|2|3|4|5)";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WeekOfMonth(final int weekOfMonth) {
		if (weekOfMonth > 5 || weekOfMonth < 0) throw new IllegalArgumentException(weekOfMonth + " is NOT a valid week of month");
		this.weekOfMonth = weekOfMonth;
	}
	public WeekOfMonth(final Integer weekOfMonth) {
		this(weekOfMonth.intValue());
	}
	public WeekOfMonth(final String weekOfMonthStr) {
		this(Integer.parseInt(weekOfMonthStr));
	}
	////////// Builders
	public static WeekOfMonth of(final int weekOfMonth) {
		return new WeekOfMonth(weekOfMonth);
	}
	public static WeekOfMonth of(final String weekOfMonthStr) {
		return new WeekOfMonth(weekOfMonthStr);
	}
	@Deprecated
	public static WeekOfMonth of(final Date date) {
		return WeekOfMonth.of(date.toInstant(),
							  Locale.getDefault());
	}
	public static WeekOfMonth of(final Instant instant,
								 final Locale locale) {
		LocalDate date = LocalDateTime.ofInstant(instant,ZoneId.of("UTC"))	// instant is always in UTC
									  .toLocalDate();
		return new WeekOfMonth(date.get(WeekFields.of(locale)
											 	 .weekOfMonth()));
	}
	public static WeekOfMonth of(final LocalDate date,
								 final Locale locale) {
		return new WeekOfMonth(date.get(WeekFields.of(locale)
											 	  .weekOfMonth()));
	}
	public static WeekOfMonth of(final LocalDateTime date,
								 final Locale locale) {
		return new WeekOfMonth(date.get(WeekFields.of(locale)
											 	  .weekOfMonth()));
	}
	public static WeekOfMonth of(final OffsetDateTime date,
								 final Locale locale) {
		return new WeekOfMonth(date.get(WeekFields.of(locale)
												  .weekOfMonth()));
	}
	public static WeekOfMonth of(final ZonedDateTime date,
								 final Locale locale) {
		return new WeekOfMonth(date.get(WeekFields.of(Locale.getDefault())
												  .weekOfMonth()));
	}
	public static WeekOfMonth valueOf(final String weekOfYear) {
		return new WeekOfMonth(weekOfYear);
	}
	public static WeekOfMonth fromString(final String weekOfYear) {
		return new WeekOfMonth(weekOfYear);
	}
	public static WeekOfMonth nowAtUTC(final Locale locale) {
		return WeekOfMonth.nowAt(ZoneId.of("UTC"),locale);
	}
	public static WeekOfMonth nowAt(final ZoneId zone,
									final Locale locale) {
		LocalDate date = LocalDateTime.ofInstant(Instant.now(),zone)
									  .toLocalDate();
		return WeekOfMonth.of(date,
							  locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getWeekOfMonth() {
		return weekOfMonth;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
		 return Strings.isNOTNullOrEmpty(str)
			 && Numbers.isInteger(str)
			 && Integer.parseInt(str) >= 0 && Integer.parseInt(str) <= 5;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the number of weeks in a month (depends on whether the week's first day is monday or sunday)
	 * @param year
	 * @param monthOfYear
	 * @param locale
	 * @return
	 */
	public static int numberOfWeeksInMonth(final Year year,final MonthOfYear monthOfYear,
										   final Locale locale) {
        YearMonth yearMonth = monthOfYear.asYearMonth(year);

        WeekFields weekFields = WeekFields.of(locale);	// Define week rules (e.g., US starts on Sunday, ISO starts on Monday)
        LocalDate lastDay = yearMonth.atEndOfMonth();	// Get the last day of that month
        return lastDay.get(weekFields.weekOfMonth());	// Get the week number of the last day
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	conversion
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.weekOfMonth);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return this.weekOfMonth;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final WeekOfMonth other) {
		return this.weekOfMonth == other.asInteger();
	}
	public boolean isNOT(final WeekOfMonth other) {
		return !this.is(other);
	}
	public boolean isBefore(final WeekOfMonth other) {
		return this.weekOfMonth < other.asInteger();
	}
	public boolean isAfter(final WeekOfMonth other) {
		return this.weekOfMonth > other.asInteger();
	}
	public boolean isBeforeOrEqual(final WeekOfMonth other) {
		return this.weekOfMonth <= other.asInteger();
	}
	public boolean isAfterOrEqual(final WeekOfMonth other) {
		return this.weekOfMonth >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	operations
/////////////////////////////////////////////////////////////////////////////////////////
	public WeekOfMonth nextWeekOfMonth(final Year year,final MonthOfYear monthOfYear,
									   final Locale locale) {
		int maxWeeks = WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
														locale);
		if (this.weekOfMonth == maxWeeks) return WeekOfMonth.of(0);
		return WeekOfMonth.of(this.weekOfMonth + 1);
	}
	public WeekOfMonth prevWeekOfMonth(final Year year,final MonthOfYear monthOfYear,
									   final Locale locale) {
		int maxWeeks = WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
														locale);
		if (this.weekOfMonth == 0) return WeekOfMonth.of(maxWeeks);
		return WeekOfMonth.of(this.weekOfMonth - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	validation
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValidAt(final Year year,final MonthOfYear monthOfYear,
							 final Locale locale) {
		return this.weekOfMonth <= WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
															   locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INSTANT RANGE (iso = week starts on MONDAY)
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year) {
		LocalDate date = LocalDate.of(year.asInteger(),1,1) // Pick any date in the year (year / monthOfYear / dayOfMonth)
								  .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,this.weekOfMonth)
								  .with(WeekFields.ISO.dayOfWeek(),1); 	// 1 = Monday
		return date.atStartOfDay()
				   .toInstant(ZoneOffset.UTC);
	}
	public Instant endInstantAt(final Year year) {
		return this.startInstantAt(year)
				   .plus(7,ChronoUnit.DAYS)
				   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year) {
		return Range.closed(this.startInstantAt(year),this.endInstantAt(year));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INSTANT RANGE (localized week start)
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final MonthOfYear monthOfYear,
								  final Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);
		LocalDate firstDayOfMonth = LocalDate.of(year.getYear(),monthOfYear.getMonthOfYear(),1);
		LocalDate startOfWeekDate = firstDayOfMonth.with(weekFields.weekOfMonth(),this.weekOfMonth)
										           .with(weekFields.dayOfWeek(),1);
		return startOfWeekDate.atStartOfDay(ZoneOffset.UTC)
							  .toInstant();
	}
	public Instant endInstantAt(final Year year,final MonthOfYear monthOfYear,
								final Locale locale) {
		return this.startInstantAt(year,monthOfYear,locale)
				   .plus(6,ChronoUnit.DAYS);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final MonthOfYear monthOfYear,
											  final Locale locale) {
		return Range.closed(this.startInstantAt(year,monthOfYear,locale),this.endInstantAt(year,monthOfYear,locale));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<WeekOfMonth> weekOfMonthIterableOf(final Year year,final MonthOfYear monthOfYear,
															  final Locale locale) {
		int maxWeeks = WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
														locale);
		return _weekOfMonthIterableWithin(0,maxWeeks);
	}
	public static Iterable<WeekOfMonth> weekOfMonthIterableFrom(final Year year,final MonthOfYear monthOfYear,final WeekOfMonth start,
															    final Locale locale) {
		int maxWeeks = WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
														locale);
		return _weekOfMonthIterableWithin(start.asInteger(),maxWeeks);
	}
	public static Iterable<WeekOfMonth> weekOfMonthIterableTo(final WeekOfMonth end) {
		return _weekOfMonthIterableWithin(0,end.asInteger());
	}
	public static Iterable<WeekOfMonth> weekOfMonthIterableWithin(final WeekOfMonth start,final WeekOfMonth end) {
		return _weekOfMonthIterableWithin(start.asInteger(),end.asInteger());
	}
	private static Iterable<WeekOfMonth> _weekOfMonthIterableWithin(final int start,final int end) {
		return new Iterable<>() {
						@Override
						public Iterator<WeekOfMonth> iterator() {
							return new Iterator<>() {
											private int _curr = start - 1;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public WeekOfMonth next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return WeekOfMonth.of(_curr);
											}
											@Override
											public void remove() {
												throw new UnsupportedOperationException();
											}
								   };
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GUAVA DISCRETE DOMAIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guava's {@link DiscreteDomain} used to create a {@link Set} of {@link Year}s
	 * <pre class='brush:java'>
	 * 		ContiguousSet<WeekOfYear> weeksOfYear = ContiguousSet.create(Range.closed(WeekOfYear.of(1),WeekOfYear.now()),
	 *														 			 _createDiscreteDomainAt(Year.now()));
	 * </pre>
	 */
	private static DiscreteDomain<WeekOfMonth> _createDiscreteDomainAt(final Year year,final MonthOfYear monthOfYear,
																	   final Locale locale) {
		int maxWeeks = WeekOfMonth.numberOfWeeksInMonth(year,monthOfYear,
														locale);
		return new DiscreteDomain<>() {
					@Override
					public WeekOfMonth next(final WeekOfMonth val) {
						if (val.is(WeekOfMonth.of(maxWeeks))) throw new IllegalArgumentException();
						return val.nextWeekOfMonth(year,monthOfYear,
												   locale);
					}
					@Override
					public WeekOfMonth previous(final WeekOfMonth val) {
						if (val.is(WeekOfMonth.of(0))) throw new IllegalArgumentException();
						return val.prevWeekOfMonth(year,monthOfYear,
												   locale);
					}
					@Override
					public long distance(final WeekOfMonth start,final WeekOfMonth end) {
						return end.asInteger() - start.asInteger();
					}
			 };
	}
	public static ContiguousSet<WeekOfMonth> createContiguousSetOf(final Year year,final MonthOfYear monthOfYear,final Range<WeekOfMonth> range,
																   final Locale locale) {
		return WeekOfMonth.createContiguousSetOf(year,monthOfYear,range.asGuavaRange(),
												 locale);
	}
	public static ContiguousSet<WeekOfMonth> createContiguousSetOf(final Year year,final MonthOfYear monthOfYear,final com.google.common.collect.Range<WeekOfMonth> range,
																   final Locale locale) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
									_createDiscreteDomainAt(year,monthOfYear,
															locale));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof WeekOfMonth) return ((WeekOfMonth)obj).getWeekOfMonth() == this.weekOfMonth;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.weekOfMonth).hashCode();
	}
	@Override
	public int compareTo(final WeekOfMonth other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
