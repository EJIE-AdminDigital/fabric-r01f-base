package r01f.types.datetime;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
public record WeekOfYear(int weekOfYear)
   implements Serializable,
  			  CanBeRepresentedAsString,
  			  Comparable<WeekOfYear> {

	private static final long serialVersionUID = 8445517567471520680L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-2])";
	public static final String REGEX_NOCAPTURE = "(?:[0-9]|0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-2])";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WeekOfYear(final int weekOfYear) {
		if (weekOfYear > 53 || weekOfYear < 0) throw new IllegalArgumentException(weekOfYear + " is NOT a valid week of year");
		this.weekOfYear = weekOfYear;
	}
	public WeekOfYear(final Integer weekOfYear) {
		this(weekOfYear.intValue());
	}
	public WeekOfYear(final String weekOfYearStr) {
		this(Integer.parseInt(weekOfYearStr));
	}
	////////// Builders
	public static WeekOfYear of(final int weekOfYear) {
		return new WeekOfYear(weekOfYear);
	}
	public static WeekOfYear of(final String weekOfYear) {
		return new WeekOfYear(weekOfYear);
	}
	@Deprecated
	public static WeekOfYear of(final Date date) {
		return WeekOfYear.of(date.toInstant(),
							 Locale.getDefault());
	}
	public static WeekOfYear of(final Instant instant,
								final Locale locale) {
		LocalDate date = LocalDateTime.ofInstant(instant,ZoneId.of("UTC"))	// instant is ALWAYS in UTC
									  .toLocalDate();
		return new WeekOfYear(date.get(WeekFields.of(locale)
											 	 .weekOfYear()));
	}
	public static WeekOfYear of(final LocalDate date,
								final Locale locale) {
		return new WeekOfYear(date.get(WeekFields.of(locale)
											 	 .weekOfYear()));
	}
	public static WeekOfYear of(final LocalDateTime date,
								final Locale locale) {
		return new WeekOfYear(date.get(WeekFields.of(locale)
											 	 .weekOfYear()));
	}
	public static WeekOfYear of(final OffsetDateTime date,
								final Locale locale) {
		return new WeekOfYear(date.get(WeekFields.of(locale)
												 .weekOfYear()));
	}
	public static WeekOfYear of(final ZonedDateTime date,
								final Locale locale) {
		return new WeekOfYear(date.get(WeekFields.of(locale)
												 .weekOfYear()));
	}
	public static WeekOfYear valueOf(final String weekOfYear) {
		return new WeekOfYear(weekOfYear);
	}
	public static WeekOfYear fromString(final String weekOfYear) {
		return new WeekOfYear(weekOfYear);
	}
	public static WeekOfYear nowAtUTC(final Locale locale) {
		return WeekOfYear.nowAt(ZoneId.of("UTC"),
								locale);
	}
	public static WeekOfYear nowAt(final ZoneId zone,
								   final Locale locale) {
		LocalDate date = LocalDateTime.ofInstant(Instant.now(),zone)
									  .toLocalDate();
		return WeekOfYear.of(date,
							 locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getWeekOfYear() {
		return weekOfYear;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
		 return Strings.isNOTNullOrEmpty(str)
			 && Numbers.isInteger(str)
			 && Integer.parseInt(str) >= 0 && Integer.parseInt(str) <= 53;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the number of weeks in a year (depends on whether the week's first day is monday or sunday)
	 * @param year
	 * @return
	 */
	public static int numberOfWeeksInYear(final Year year,
										  final Locale locale) {
		LocalDate lastDayOfYear = LocalDate.of(year.asInteger(),12,31);	// last dey of year
		return lastDayOfYear.get(WeekFields.of(locale)
										   .weekOfYear());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	conversion
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.weekOfYear);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return this.weekOfYear;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final WeekOfYear other) {
		return this.weekOfYear == other.asInteger();
	}
	public boolean isNOT(final WeekOfYear other) {
		return !this.is(other);
	}
	public boolean isBefore(final WeekOfYear other) {
		return this.weekOfYear < other.asInteger();
	}
	public boolean isAfter(final WeekOfYear other) {
		return this.weekOfYear > other.asInteger();
	}
	public boolean isBeforeOrEqual(final WeekOfYear other) {
		return this.weekOfYear <= other.asInteger();
	}
	public boolean isAfterOrEqual(final WeekOfYear other) {
		return this.weekOfYear >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	operations
/////////////////////////////////////////////////////////////////////////////////////////
	public WeekOfYear nextWeekOfYear(final Year year,
									 final Locale locale) {
		int maxWeeks = WeekOfYear.numberOfWeeksInYear(year,locale);
		if (this.weekOfYear == maxWeeks) return WeekOfYear.of(0);
		return WeekOfYear.of(this.weekOfYear + 1);
	}
	public WeekOfYear prevWeekOfYear(final Year year,
									 final Locale locale) {
		int maxWeeks = WeekOfYear.numberOfWeeksInYear(year,locale);
		if (this.weekOfYear == 0) return WeekOfYear.of(maxWeeks);
		return WeekOfYear.of(this.weekOfYear - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	validation
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValidAt(final Year year,
							 final Locale locale) {
		return this.weekOfYear <= WeekOfYear.numberOfWeeksInYear(year,locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INSTANT RANGE (iso = week starts on MONDAY)
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year) {
		LocalDate date = LocalDate.of(year.asInteger(),1,1) // Pick any date in the year (year / monthOfYear / dayOfMonth)
								  .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,this.weekOfYear)
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
	public Instant startInstantAt(final Year year,
								  final Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);
		LocalDate date = LocalDate.of(year.asInteger(),1,1) // Pick any date in the year (year / monthOfYear / dayOfMonth)
								  .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,this.weekOfYear)
								  .with(weekFields.dayOfWeek(),1); 	// 1 = Monday or sunday (depends on the locale)
		return date.atStartOfDay()
				   .toInstant(ZoneOffset.UTC);
	}
	public Instant endInstantAt(final Year year,
								final Locale locale) {
		return this.startInstantAt(year,locale)
				   .plus(6,ChronoUnit.DAYS);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,
											  final Locale locale) {
		return Range.closed(this.startInstantAt(year,locale),this.endInstantAt(year,locale));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<WeekOfYear> weekOfYearIterableOf(final Year year,
															final Locale locale) {
		int maxWeeks = WeekOfYear.numberOfWeeksInYear(year,locale);
		return _weekOfYearIterableWithin(0,maxWeeks);
	}
	public static Iterable<WeekOfYear> weekOfYearIterableFrom(final Year year,final WeekOfYear start,
															  final Locale locale) {
		int maxWeeks = WeekOfYear.numberOfWeeksInYear(year,locale);
		return _weekOfYearIterableWithin(start.asInteger(),maxWeeks);
	}
	public static Iterable<WeekOfYear> weekOfYearIterableTo(final WeekOfYear end) {
		return _weekOfYearIterableWithin(0,end.asInteger());
	}
	public static Iterable<WeekOfYear> weekOfYearIterableWithin(final WeekOfYear start,final WeekOfYear end) {
		return _weekOfYearIterableWithin(start.asInteger(),end.asInteger());
	}
	private static Iterable<WeekOfYear> _weekOfYearIterableWithin(final int start,final int end) {
		return new Iterable<>() {
						@Override
						public Iterator<WeekOfYear> iterator() {
							return new Iterator<>() {
											private int _curr = start - 1;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public WeekOfYear next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return WeekOfYear.of(_curr);
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
	private static DiscreteDomain<WeekOfYear> _createDiscreteDomainAt(final Year year,
																	  final Locale locale) {
		int maxWeeks = WeekOfYear.numberOfWeeksInYear(year,locale);
		return new DiscreteDomain<>() {
					@Override
					public WeekOfYear next(final WeekOfYear val) {
						if (val.is(WeekOfYear.of(maxWeeks))) throw new IllegalArgumentException();
						return val.nextWeekOfYear(year,locale);
					}
					@Override
					public WeekOfYear previous(final WeekOfYear val) {
						if (val.is(WeekOfYear.of(0))) throw new IllegalArgumentException();
						return val.prevWeekOfYear(year,locale);
					}
					@Override
					public long distance(final WeekOfYear start,final WeekOfYear end) {
						return end.asInteger() - start.asInteger();
					}
			 };
	}
	public static ContiguousSet<WeekOfYear> createContiguousSetOf(final Year year,final Range<WeekOfYear> range,
																  final Locale locale) {
		return WeekOfYear.createContiguousSetOf(year,range.asGuavaRange(),
												locale);
	}
	public static ContiguousSet<WeekOfYear> createContiguousSetOf(final Year year,final com.google.common.collect.Range<WeekOfYear> range,
																  final Locale locale) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
									_createDiscreteDomainAt(year,locale));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof WeekOfYear) return ((WeekOfYear)obj).getWeekOfYear() == this.weekOfYear;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.weekOfYear).hashCode();
	}
	@Override
	public int compareTo(final WeekOfYear other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
