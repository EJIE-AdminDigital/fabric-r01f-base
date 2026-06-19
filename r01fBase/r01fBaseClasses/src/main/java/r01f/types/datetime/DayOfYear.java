package r01f.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

@MarshallType(as="dayOfYear")
public record DayOfYear(int dayOfYear)
   implements CanBeRepresentedAsString,
  			  Comparable<DayOfYear> {

	private static final long serialVersionUID = -6824269185202942077L;

/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(0[1-9]|[12]\\d|3[01])";	// do not match 32, etc
	public static final String REGEX_NOCAPTURE = "(?:0[1-9]|[12]\\d|3[01])";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfYear(final int dayOfYear) {
		if (dayOfYear > 365 || dayOfYear <= 0) throw new IllegalArgumentException(dayOfYear + " is NOT a valid day of year");
		this.dayOfYear = dayOfYear;
	}
	public DayOfYear(final Integer dayOfYear) {
		this(dayOfYear.intValue());
	}
	public DayOfYear(final String dayOfYearStr) {
		this(Integer.parseInt(dayOfYearStr));
	}
	////////// Builders
	public static DayOfYear of(final String dayOfYearStr) {
		return new DayOfYear(dayOfYearStr);
	}
	@Deprecated
	public static DayOfYear of(final Date date) {
		return DayOfYear.of(date.toInstant());
	}
	public static DayOfYear of(final Instant instant) {
		return DayOfYear.of(instant.atZone(ZoneId.of("UTC")));
	}
	public static DayOfYear of(final LocalDate date) {
		return new DayOfYear(date.getDayOfYear());
	}
	public static DayOfYear of(final LocalDateTime lDate) {
		return new DayOfYear(lDate.getDayOfYear());
	}
	public static DayOfYear of(final OffsetDateTime date) {
		return new DayOfYear(date.getDayOfYear());
	}
	public static DayOfYear of(final ZonedDateTime date) {
		return new DayOfYear(date.getDayOfYear());
	}
	public static DayOfYear of(final int dayOfYear) {
		return new DayOfYear(dayOfYear);
	}
	public static DayOfYear valueOf(final String dayOfYear) {
		return new DayOfYear(dayOfYear);
	}
	public static DayOfYear from(final String dayOfYearStr) {
		return new DayOfYear(dayOfYearStr);
	}
	public static DayOfYear fromString(final String dayOfYearStr) {
		return new DayOfYear(dayOfYearStr);
	}
	public static DayOfYear nowAtUTC() {
		return DayOfYear.nowAt(ZoneId.of("UTC"));
	}
	public static DayOfYear nowAt(final ZoneId zone) {
		return DayOfYear.of(ZonedDateTime.now(zone));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getDayOfYear() {
		return this.dayOfYear;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
			return Strings.isNOTNullOrEmpty(str)
				 && Numbers.isInteger(str)
				 && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 31;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION & REPRESENTATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.dayOfYear);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringLeftPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),3,'0');
	}
	public int asInteger() {
		return this.dayOfYear;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isValidAt(final Year year) {
		if (year.asYear().isLeap()) {
			return this.dayOfYear >= 1 && this.dayOfYear <= 366;
		} else {
			return this.dayOfYear >= 1 && this.dayOfYear <= 365;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final DayOfYear other) {
		return this.dayOfYear == other.asInteger();
	}
	public boolean isNOT(final DayOfYear other) {
		return !this.is(other);
	}
	public boolean isBefore(final DayOfYear other) {
		return this.dayOfYear < other.asInteger();
	}
	public boolean isAfter(final DayOfYear other) {
		return this.dayOfYear > other.asInteger();
	}
	public boolean isBeforeOrEqual(final DayOfYear other) {
		return this.dayOfYear <= other.asInteger();
	}
	public boolean isAfterOrEqual(final DayOfYear other) {
		return this.dayOfYear >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	previous / next
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfYear nextDayOfYear(final Year year) {
		DayOfYear outDayOfYear = null;
		if (this.dayOfYear == 365) {
			if (year.asYear().isLeap()) {
				outDayOfYear = DayOfYear.of(366);
			} else {
				throw new IllegalStateException("Cannot get the next day of year for " + this + " because the year " + year + " is NOT a leap year!!");
			}
		} else {
			DayOfYear.of(this.dayOfYear + 1);
		}
		return outDayOfYear;
	}
	public DayOfYear prevDayOfYear(final Year year) {
		if (this.dayOfYear == 1) throw new IllegalStateException("Cannot get the previous day of year for " + this + " because it's the first day of year!!");
		return DayOfYear.of(this.dayOfYear - 1);
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
		return LocalDate.of(year.getYear(),monthOfYear.getMonthOfYear(),this.dayOfYear);
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<DayOfYear> dayOfMonthIterableOf(final Year year,final MonthOfYear monthOfYear) {
		YearMonth yearMonth = YearMonth.of(year.asInteger(),monthOfYear.asInteger());
        final int dayOfMonthMaxValue = yearMonth.lengthOfMonth();
		return () -> new Iterator<>() {
							private int _curr = -1;

							@Override
							public boolean hasNext() {
								return _curr < dayOfMonthMaxValue;
							}
							@Override
							public DayOfYear next() {
								if (!this.hasNext()) throw new IllegalStateException();
								_curr = _curr == -1 ? 0
													: _curr + 1;
								return DayOfYear.of(_curr);
							}
							@Override
							public void remove() {
								throw new UnsupportedOperationException();
							}
					};
	}
	public static Collection<DayOfYear> dayOfMonthCollectionOf(final Year year,final MonthOfYear monthOfYear) {
		return StreamSupport.stream(DayOfYear.dayOfMonthIterableOf(year,monthOfYear)
											  .spliterator(),
									false)	// not parallel stream
							.toList();
	}
	
	public static LocalDate toLocalDate(final DayOfYear dayOfYear, final Year lastDecade, final Year lastYearOfDecade) {
	    // [1]. Reconstruct the full accrual year (e.g., 2000 + 26 = 2026)
	    int accrualYear = 2000 + lastDecade.asInteger();
	    
	    // [2]. Get the base decade of the accrual year (e.g., 2026 -> 2020)
	    int baseDecade = (accrualYear / 10) * 10;
	    
	    //[3]. Add the limit year digit using the modulo operator for safety
	    int computedYear = baseDecade + (lastYearOfDecade.asInteger() % 10);
	    
	    //[4]. Handle decade overflow (e.g., accrual in 2029 and payment in 2030)
	    if (computedYear < accrualYear) {
	        computedYear += 10;
	    }	  
	    //[5]. Build the native LocalDate
	    return LocalDate.ofYearDay(computedYear, dayOfYear.dayOfYear());
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
	private static DiscreteDomain<DayOfYear> _createDiscreteDomainAt(final Year year) {
		int maxDaysInYear = year.asYear().isLeap() ? 366 : 365;
		return new DiscreteDomain<>() {
					@Override
					public DayOfYear next(final DayOfYear val) {
						if (val.is(DayOfYear.of(maxDaysInYear + 1))) throw new IllegalArgumentException();
						return val.nextDayOfYear(year);
					}
					@Override
					public DayOfYear previous(final DayOfYear val) {
						if (val.is(DayOfYear.of(0))) throw new IllegalArgumentException();
						return val.prevDayOfYear(year);
					}
					@Override
					public long distance(final DayOfYear start,final DayOfYear end) {
						return end.asInteger() - start.asInteger();
					}
			 };
	}
	public static ContiguousSet<DayOfYear> createContiguousSetOf(final Year year,final Range<DayOfYear> range) {
		return DayOfYear.createContiguousSetOf(year,range.asGuavaRange());
	}
	public static ContiguousSet<DayOfYear> createContiguousSetOf(final Year year,final com.google.common.collect.Range<DayOfYear> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
									_createDiscreteDomainAt(year));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof DayOfYear) return ((DayOfYear)obj).getDayOfYear() == this.dayOfYear;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.dayOfYear).hashCode();
	}
	@Override
	public int compareTo(final DayOfYear other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
