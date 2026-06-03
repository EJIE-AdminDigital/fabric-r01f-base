package r01f.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="secondOfMinute")
public record SecondOfMinute(int secondOfMinute)
   implements CanBeRepresentedAsString,
  			  Comparable<SecondOfMinute> {

	private static final long serialVersionUID = 5446927630549647342L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-5][0-9])";
	public static final String REGEX_NOCAPTURE = "[0-5][0-9]";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecondOfMinute(final int secondOfMinute) {
		if (secondOfMinute >= 60 || secondOfMinute < 0) throw new IllegalArgumentException("Not a valid second of minute");
		this.secondOfMinute = secondOfMinute;
	}
	public SecondOfMinute(final Integer secondOfMinute) {
		this(secondOfMinute.intValue());
	}
	public SecondOfMinute(final String secondOfMinuteStr) {
		this(Integer.parseInt(secondOfMinuteStr));
	}
	////////// Builders
	public static SecondOfMinute of(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	@Deprecated
	public static SecondOfMinute of(final Date date) {
		return SecondOfMinute.of(date.toInstant());
	}
	public static SecondOfMinute of(final Instant instant) {
		return new SecondOfMinute(instant.atZone(ZoneId.of("UTC"))
										 .getSecond());
	}
	public static SecondOfMinute of(final LocalTime time) {
		return new SecondOfMinute(time.getSecond());
	}
	public static SecondOfMinute of(final LocalDateTime date) {
		return new SecondOfMinute(date.getSecond());
	}
	public static SecondOfMinute of(final ZonedDateTime date) {
		return new SecondOfMinute(date.getSecond());
	}
	public static SecondOfMinute of (final OffsetDateTime date) {
		return new SecondOfMinute(date.getSecond());
	}
	public static SecondOfMinute of(final int secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	public static SecondOfMinute nowAtUTC() {
		return SecondOfMinute.of(ZonedDateTime.now(ZoneId.of("UTC")));
	}
	public static SecondOfMinute nowAt(final ZoneId zone) {
		return SecondOfMinute.of(ZonedDateTime.now(zone));
	}
	public static SecondOfMinute valueOf(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
	public static SecondOfMinute fromString(final String secondOfMinute) {
		return new SecondOfMinute(secondOfMinute);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getSecondOfMinute() {
		return secondOfMinute;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
		 return Strings.isNOTNullOrEmpty(str)
			 && Numbers.isInteger(str)
			 && Integer.parseInt(str) >= 0 && Integer.parseInt(str) < 60;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	conversions
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.secondOfMinute);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return this.secondOfMinute;
	}
	public LocalDateTime getAsLocalDateTimeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
											  final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		return LocalDate.of(year.asInteger(),monthOfYear.asInteger(),dayOfMonth.asInteger())
						.atTime(hourOfDay.asInteger(),minuteOfHour.asInteger(),0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public SecondOfMinute nextSecondOfMinute() {
		if (this.secondOfMinute == 59) return SecondOfMinute.of(0);
		return SecondOfMinute.of(this.secondOfMinute + 1);
	}
	public SecondOfMinute prevSecondOfMinute() {
		if (this.secondOfMinute == 0) return SecondOfMinute.of(59);
		return SecondOfMinute.of(this.secondOfMinute - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final SecondOfMinute other) {
		return this.secondOfMinute == other.asInteger();
	}
	public boolean isNOT(final SecondOfMinute other) {
		return !this.is(other);
	}
	public boolean isBefore(final SecondOfMinute other) {
		return this.secondOfMinute < other.asInteger();
	}
	public boolean isAfter(final SecondOfMinute other) {
		return this.secondOfMinute > other.asInteger();
	}
	public boolean isBeforeOrEqual(final SecondOfMinute other) {
		return this.secondOfMinute <= other.asInteger();
	}
	public boolean isAfterOrEqual(final SecondOfMinute other) {
		return this.secondOfMinute >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,final HourOfDay hourOfDay,
								  final MinuteOfHour minuteOfHour) {
		return this.getAsLocalDateTimeAt(year,monthOfYear,dayOfMonth,hourOfDay,minuteOfHour)
				   .atZone(ZoneId.of("UTC"))
				   .toInstant();
	}
	public Instant endInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,final HourOfDay hourOfDay,
								final MinuteOfHour minuteOfHour) {
		Instant startInstant = this.startInstantAt(year,monthOfYear,dayOfMonth,hourOfDay,minuteOfHour);
		return startInstant.plusSeconds(60)
						   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
        return Range.closed(this.startInstantAt(year,monthOfYear,dayOfMonth,hourOfDay,minuteOfHour),this.endInstantAt(year,monthOfYear,dayOfMonth,hourOfDay,minuteOfHour));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<SecondOfMinute> yearMonthsIterable() {
		return new Iterable<>() {
						@Override
						public Iterator<SecondOfMinute> iterator() {
							return new Iterator<>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 60;
											}
											@Override
											public SecondOfMinute next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return SecondOfMinute.of(_curr);
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
	 * 		ContiguousSet<Year> years = ContiguousSet.create(Range.closed(Year.of(1960),Year.now()),
															 Year.DISCRETE_DOMAIN);
	 * </pre>
	 */
	public static DiscreteDomain<SecondOfMinute> DISCRETE_DOMAIN = new DiscreteDomain<>() {
																		@Override
																		public SecondOfMinute next(final SecondOfMinute val) {
																			if (val.is(SecondOfMinute.of(59))) throw new IllegalArgumentException();
																			return val.nextSecondOfMinute();
																		}
																		@Override
																		public SecondOfMinute previous(final SecondOfMinute val) {
																			if (val.is(SecondOfMinute.of(0))) throw new IllegalArgumentException();
																			return val.prevSecondOfMinute();
																		}
																		@Override
																		public long distance(final SecondOfMinute start,final SecondOfMinute end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<SecondOfMinute> createContiguousSetOf(final Range<SecondOfMinute> range) {
		return SecondOfMinute.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<SecondOfMinute> createContiguousSetOf(final com.google.common.collect.Range<SecondOfMinute> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
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
		if (obj instanceof SecondOfMinute) return ((SecondOfMinute)obj).getSecondOfMinute() == this.secondOfMinute;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.secondOfMinute).hashCode();
	}
	@Override
	public int compareTo(final SecondOfMinute other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
