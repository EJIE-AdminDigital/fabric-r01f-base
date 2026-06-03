package r01f.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="year")
public record Year(int year)
   implements CanBeRepresentedAsString,
  			  Comparable<Year> {

	private static final long serialVersionUID = 7658275370612790932L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "(19\\d{2}|20\\d{2})";
	public static final String REGEX_NOCAPTURE = "(?:19\\d{2}|20\\d{2})";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Year(final int year) {
		if (year < 0) throw new IllegalArgumentException(year + " is NOT a valid day year");
		this.year = year;
	}
	public Year(final java.time.Year year) {
   		this(year.getValue());
   	}
	public Year(final Integer year) {
		this(year.intValue());
	}
	public Year(final String yearStr) {
		this(Integer.parseInt(yearStr));
	}
	////////// Builders
	public static Year of(final java.time.Year year) {
		return new Year(year);
	}
	public static Year of(final String year) {
		return new Year(year);
	}
	@Deprecated
	public static Year of(final Date date) {
		return Year.of(date.toInstant());
	}
	public static Year of(final Instant instant) {
		return Year.of(java.time.Year.from(instant.atZone(ZoneId.of("UTC"))));
	}
	public static Year of(final LocalDate date) {
		return new Year(date.getYear());
	}
	public static Year of(final LocalDateTime date) {
		return new Year(date.getYear());
	}
	public static Year of(final OffsetDateTime date) {
		return new Year(date.getYear());
	}
	public static Year of(final ZonedDateTime date) {
		return new Year(date.getYear());
	}
	public static Year of(final int year) {
		return new Year(year);
	}
	public static Year nowAtUTC() {
		return Year.nowAt(ZoneId.of("UTC"));
	}
	public static Year nowAt(final ZoneId zone) {
		return Year.of(java.time.Year.now(zone));
	}
	public static Year valueOf(final String year) {
		return new Year(year);
	}
	public static Year from(final String year) {
		return new Year(year);
	}
	public static Year fromString(final String year) {
		return new Year(year);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getYear() {
		return year;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
   public static boolean canBe(final String str) {
         return Strings.isNOTNullOrEmpty(str)
                && Numbers.isInteger(str);
   }
/////////////////////////////////////////////////////////////////////////////////////////
//	conversions
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.year);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return this.year;
	}
	public String asStringInCentury() {
		return this.toString().substring(this.toString().length()-2,
										 this.toString().length());		// last 2 digits of the year
	}
	public String asStringInDecade() {
		return this.toString().substring(this.toString().length()-1,
										 this.toString().length());		// last digit of the year
	}
	public java.time.Year asYear() {
		return java.time.Year.of(this.year);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Year other) {
		return this.year == other.asInteger();
	}
	public boolean isNOT(final Year other) {
		return !this.is(other);
	}
	public boolean isBefore(final Year other) {
		return this.year < other.asInteger();
	}
	public boolean isAfter(final Year other) {
		return this.year > other.asInteger();
	}
	public boolean isBeforeOrEqual(final Year other) {
		return this.year <= other.asInteger();
	}
	public boolean isAfterOrEqual(final Year other) {
		return this.year >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Year minus(final int years) {
		return Year.of(this.year - years);
	}
	public Year plus(final int years) {
		return Year.of(this.year + years);
	}
	public Year nextYear() {
		return this.plus(1);
	}
	public Year prevYear() {
		return this.minus(1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstant() {
		return LocalDate.of(this.year,1,1)
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant();
	}
	public Instant endInstant() {
		// 1. Get the start of the NEXT year at UTC
        Instant nextYearStart = this.startInstant()
        							.plus(1,ChronoUnit.YEARS);
        // 2. Subtract 1 nanosecond to get the last instant of the current year
        return nextYearStart.minusNanos(1);
	}
	public Range<Instant> getAsInstantRange() {
        return Range.closed(this.startInstant(),this.endInstant());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the years within the given range
	 * @param range
	 * @return
	 */
	public static Collection<Year> yearsWithin(final Range<Year> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.getUpperBound().isBefore(range.getUpperBound())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");

		Collection<Year> years = new ArrayList<>();
		Year currYear = range.upperEndpoint();
		Year lowerLimitYear = range.lowerEndpoint();
		while (currYear.isAfter(lowerLimitYear)) {
			years.add(currYear);
			currYear = currYear.minus(1);
		}
		return years;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GUAVA DISCRETE DOMAIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guava's {@link DiscreteDomain} used to create a {@link Set} of {@link Year}s
	 * <pre class='brush:java'>
	 * 		ContiguousSet<Year> years = ContiguousSet.create(Range.closed(Year.of(1960),Year.now()),
															 Year.DISCRETE_DOMAIN);
	 * </pre>
	 */
	private static DiscreteDomain<Year> DISCRETE_DOMAIN = new DiscreteDomain<>() {
																@Override
																public Year next(final Year val) {
																	return val.nextYear();
																}
																@Override
																public Year previous(final Year val) {
																	return val.prevYear();
																}
																@Override
																public long distance(final Year start,final Year end) {
																	return end.asInteger() - start.asInteger();
																}
														 };
	public static ContiguousSet<Year> createContiguousSetOf(final Range<Year> range) {
		return Year.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<Year> createContiguousSetOf(final com.google.common.collect.Range<Year> range) {
		return ContiguousSet.create(range,
								    DISCRETE_DOMAIN);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof Year) return ((Year)obj).getYear() == this.year;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.year).hashCode();
	}
	@Override
	public int compareTo(final Year other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
