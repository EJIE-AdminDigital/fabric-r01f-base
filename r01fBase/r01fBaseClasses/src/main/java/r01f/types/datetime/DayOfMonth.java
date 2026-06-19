package r01f.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="dayOfMonth")
public record DayOfMonth(int dayOfMonth)
   implements CanBeRepresentedAsString,
  			  Comparable<DayOfMonth> {
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(0[1-9]|[12]\\d|3[01])";	// do not match 32, etc
	public static final String REGEX_NOCAPTURE = "(?:0[1-9]|[12]\\d|3[01])";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfMonth(final MonthDay dayOfMonth) {
		this(dayOfMonth.getDayOfMonth());
	}
	public DayOfMonth(final int dayOfMonth) {
		if (dayOfMonth > 31 || dayOfMonth <= 0) throw new IllegalArgumentException(dayOfMonth + " is NOT a valid day of month");
		this.dayOfMonth = dayOfMonth;
	}
	public DayOfMonth(final Integer dayOfMonth) {
		this(dayOfMonth.intValue());
	}
	public DayOfMonth(final String dayOfMonthStr) {
		this(Integer.parseInt(dayOfMonthStr));
	}
	////////// Builders
	public static DayOfMonth of(final MonthDay dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth of(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	@Deprecated
	public static DayOfMonth of(final Date date) {
		return DayOfMonth.of(date.toInstant());
	}
	public static DayOfMonth of(final Instant instant) {
		return new DayOfMonth(MonthDay.from(instant.atZone(ZoneId.of("UTC"))));
	}
	public static DayOfMonth of(final LocalDate date) {
		return new DayOfMonth(date.getDayOfMonth());
	}
	public static DayOfMonth of(final LocalDateTime lDate) {
		return new DayOfMonth(lDate.getDayOfMonth());
	}
	public static DayOfMonth of(final OffsetDateTime date) {
		return new DayOfMonth(date.getDayOfMonth());
	}
	public static DayOfMonth of(final ZonedDateTime date) {
		return new DayOfMonth(date.getDayOfMonth());
	}
	public static DayOfMonth of(final int dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth valueOf(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth from(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth fromString(final String dayOfMonth) {
		return new DayOfMonth(dayOfMonth);
	}
	public static DayOfMonth nowAtUTC() {
		return DayOfMonth.nowAt(ZoneId.of("UTC"));
	}
	public static DayOfMonth nowAt(final ZoneId zone) {
		return DayOfMonth.of(ZonedDateTime.now(zone));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION & REPRESENTATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.dayOfMonth);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return this.dayOfMonth;
	}
	public MonthDay asJavaTimeMonthDayAt(final MonthOfYear monthOfYear) {
		return MonthDay.of(monthOfYear.asInteger(),this.dayOfMonth);
	}
	public MonthDay asJavaTimeMonthDayAt(final Month monthOfYear) {
		return MonthDay.of(monthOfYear,this.dayOfMonth);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getDayOfMonth() {
		return this.dayOfMonth;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
			return Strings.isNOTNullOrEmpty(str)
				 && Numbers.isInteger(str)
				 && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 31;
	}
	public static DayOfMonth maxDayOfMonthOf(final Year year,final MonthOfYear monthOfYear) {
		YearMonth yearMonth = YearMonth.of(year.asInteger(),monthOfYear.asInteger());
        return DayOfMonth.of(yearMonth.lengthOfMonth());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValidAt(final Year year,final MonthOfYear monthOfYear) {
		return DayOfMonth.isValidDayOfMonth(year,monthOfYear,this);
	}
	public static boolean isValidDayOfMonth(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		YearMonth yearMonth = YearMonth.of(year.asInteger(),monthOfYear.asInteger());
        int dayOfMonthMaxValue = yearMonth.lengthOfMonth();
		return dayOfMonthMaxValue >= dayOfMonth.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final DayOfMonth other) {
		return this.dayOfMonth == other.asInteger();
	}
	public boolean isNOT(final DayOfMonth other) {
		return !this.is(other);
	}
	public boolean isBefore(final DayOfMonth other) {
		return this.dayOfMonth < other.asInteger();
	}
	public boolean isAfter(final DayOfMonth other) {
		return this.dayOfMonth > other.asInteger();
	}
	public boolean isBeforeOrEqual(final DayOfMonth other) {
		return this.dayOfMonth <= other.asInteger();
	}
	public boolean isAfterOrEqual(final DayOfMonth other) {
		return this.dayOfMonth >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	previous / next
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfMonth nextDayOfMonth(final Year year,final MonthOfYear monthOfYear) {
		int maxDays = DayOfMonth.maxDayOfMonthOf(year,monthOfYear).asInteger();
		if (this.dayOfMonth == maxDays) return DayOfMonth.of(1);
		return DayOfMonth.of(this.dayOfMonth + 1);
	}
	public DayOfMonth prevDayOfMonth(final Year year,final MonthOfYear monthOfYear) {
		int maxDays = DayOfMonth.maxDayOfMonthOf(year,monthOfYear).asInteger();
		if (this.dayOfMonth == 1) return DayOfMonth.of(maxDays);
		return DayOfMonth.of(this.dayOfMonth - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INSTANT RANGE AT YEAR & MONTH
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final MonthOfYear monthOfYear) {
		LocalDate lDate = this.getAsLocalDateAt(year,monthOfYear);
		return lDate.atStartOfDay()
					.atZone(ZoneId.of("UTC"))
					.toInstant();
	}
	public Instant endInstantAt(final Year year,final MonthOfYear monthOfYear) {
		Instant startInstant = this.startInstantAt(year,monthOfYear);
		Instant endInstant = startInstant.plus(1,ChronoUnit.DAYS)
										 .minusNanos(1);
		return endInstant;
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final MonthOfYear monthOfYear) {
		return Range.closed(this.startInstantAt(year,monthOfYear),this.endInstantAt(year,monthOfYear));
	}
	public LocalDate getAsLocalDateAt(final Year year,final MonthOfYear monthOfYear) {
		return LocalDate.of(year.getYear(),monthOfYear.getMonthOfYear(),this.dayOfMonth);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<DayOfMonth> dayOfMonthIterableOf(final Year year,final MonthOfYear monthOfYear) {
		YearMonth yearMonth = YearMonth.of(year.asInteger(),monthOfYear.asInteger());
        final int dayOfMonthMaxValue = yearMonth.lengthOfMonth();
		return () -> new Iterator<>() {
							private int _curr = -1;

							@Override
							public boolean hasNext() {
								return _curr < dayOfMonthMaxValue;
							}
							@Override
							public DayOfMonth next() {
								if (!this.hasNext()) throw new IllegalStateException();
								_curr = _curr == -1 ? 0
													: _curr + 1;
								return DayOfMonth.of(_curr);
							}
							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
					};
	}
	public static Collection<DayOfMonth> dayOfMonthCollectionOf(final Year year,final MonthOfYear monthOfYear) {
		return StreamSupport.stream(DayOfMonth.dayOfMonthIterableOf(year,monthOfYear)
											  .spliterator(),
									false)	// not parallel stream
							.toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GUAVA DISCRETE DOMAIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guava's {@link DiscreteDomain} used to create a {@link Set} of {@link Year}s
	 * <pre class='brush:java'>
	 * 		ContiguousSet<DayOfMonth> daysOfMonth = ContiguousSet.create(Range.closed(DayOfMonth.of(1),DayOfMonth.of(15)),
	 *														 			 _createDiscreteDomainAt(Year.now(),MonthOfYear.JANUARY));
	 * </pre>
	 */
	private static DiscreteDomain<DayOfMonth> _createDiscreteDomainAt(final Year year,final MonthOfYear monthOfYear) {
		int maxDaysInMonth = DayOfMonth.maxDayOfMonthOf(year,monthOfYear)
									   .asInteger();
		return new DiscreteDomain<>() {
					@Override
					public DayOfMonth next(final DayOfMonth val) {
						if (val.is(DayOfMonth.of(maxDaysInMonth))) throw new IllegalArgumentException();
						return val.nextDayOfMonth(year,monthOfYear);
					}
					@Override
					public DayOfMonth previous(final DayOfMonth val) {
						if (val.is(DayOfMonth.of(0))) throw new IllegalArgumentException();
						return val.prevDayOfMonth(year,monthOfYear);
					}
					@Override
					public long distance(final DayOfMonth start,final DayOfMonth end) {
						return end.asInteger() - start.asInteger();
					}
			 };
	}
	public static ContiguousSet<DayOfMonth> createContiguousSetOf(final Year year,final MonthOfYear monthOfYear,final Range<DayOfMonth> range) {
		return DayOfMonth.createContiguousSetOf(year,monthOfYear,range.asGuavaRange());
	}
	public static ContiguousSet<DayOfMonth> createContiguousSetOf(final Year year,final MonthOfYear monthOfYear,final com.google.common.collect.Range<DayOfMonth> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
									_createDiscreteDomainAt(year,monthOfYear));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final DayOfMonth other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
