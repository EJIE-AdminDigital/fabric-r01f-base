package r01f.types.datetime;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="minuteOfHour")
public record MinuteOfHour(int minuteOfHour)
   implements Serializable,
  			  CanBeRepresentedAsString,
  			  Comparable<MinuteOfHour> {

	private static final long serialVersionUID = -6492644548389808923L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-5][0-9])";
	public static final String REGEX_NOCAPTURE = "[0-5][0-9]";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MinuteOfHour(final int minuteOfHour) {
		if (minuteOfHour >= 60 || minuteOfHour < 0) throw new IllegalArgumentException("Not a valid minute of hour");
		this.minuteOfHour = minuteOfHour;
	}
	public MinuteOfHour(final Integer minuteOfHour) {
		this(minuteOfHour.intValue());
	}
	public MinuteOfHour(final String minuteOfHourStr) {
		this(Integer.parseInt(minuteOfHourStr));
	}
	////////// Builders
	public static MinuteOfHour of(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	@Deprecated
	public static MinuteOfHour of(final Date date) {
		return MinuteOfHour.of(date.toInstant());
	}
	public static MinuteOfHour of(final Instant instant) {
		return new MinuteOfHour(instant.atZone(ZoneId.of("UTC"))
									   .getMinute());
	}
	public static MinuteOfHour of(final LocalTime time) {
		return new MinuteOfHour(time.getMinute());
	}
	public static MinuteOfHour of(final LocalDateTime dateTime) {
		return new MinuteOfHour(dateTime.getMinute());
	}
	public static MinuteOfHour of(final ZonedDateTime zonedDateTime) {
		return new MinuteOfHour(zonedDateTime.getMinute());
	}
	public static MinuteOfHour of(final OffsetDateTime zonedDateTime) {
		return new MinuteOfHour(zonedDateTime.getMinute());
	}
	public static MinuteOfHour of(final int minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour valueOf(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour fromString(final String minuteOfHour) {
		return new MinuteOfHour(minuteOfHour);
	}
	public static MinuteOfHour now() {
		return MinuteOfHour.of(Instant.now());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getMinuteOfHour() {
		return minuteOfHour;
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
//	CONVERSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.minuteOfHour);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return this.minuteOfHour;
	}
	public LocalDateTime getAsLocalDateTimeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,final HourOfDay hourOfDay) {
		return LocalDate.of(year.asInteger(),monthOfYear.asInteger(),dayOfMonth.asInteger())
						.atTime(hourOfDay.asInteger(),this.minuteOfHour,0,0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public MinuteOfHour nextMinuteOfHour() {
		if (this.minuteOfHour == 59) return MinuteOfHour.of(0);
		return MinuteOfHour.of(this.minuteOfHour + 1);
	}
	public MinuteOfHour prevMinuteOfHour() {
		if (this.minuteOfHour == 0) return MinuteOfHour.of(59);
		return MinuteOfHour.of(this.minuteOfHour - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final MinuteOfHour other) {
		return this.minuteOfHour == other.asInteger();
	}
	public boolean isNOT(final MinuteOfHour other) {
		return !this.is(other);
	}
	public boolean isBefore(final MinuteOfHour other) {
		return this.minuteOfHour < other.asInteger();
	}
	public boolean isAfter(final MinuteOfHour other) {
		return this.minuteOfHour > other.asInteger();
	}
	public boolean isBeforeOrEqual(final MinuteOfHour other) {
		return this.minuteOfHour <= other.asInteger();
	}
	public boolean isAfterOrEqual(final MinuteOfHour other) {
		return this.minuteOfHour >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
								  final HourOfDay hourOfDay) {
		return this.getAsLocalDateTimeAt(year,monthOfYear,dayOfMonth,hourOfDay)
				   .atZone(ZoneId.of("UTC"))
				   .toInstant();
	}
	public Instant endInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
								final HourOfDay hourOfDay) {
		Instant startInstant = this.startInstantAt(year,monthOfYear,dayOfMonth,hourOfDay);
		return startInstant.plusSeconds(60)
						   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,final HourOfDay hourOfDay) {
        return Range.closed(this.startInstantAt(year,monthOfYear,dayOfMonth,hourOfDay),this.endInstantAt(year,monthOfYear,dayOfMonth,hourOfDay));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<MinuteOfHour> hourMinutesIterable() {
		return new Iterable<>() {
						@Override
						public Iterator<MinuteOfHour> iterator() {
							return new Iterator<>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 60;
											}
											@Override
											public MinuteOfHour next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return MinuteOfHour.of(_curr);
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
	public static DiscreteDomain<MinuteOfHour> DISCRETE_DOMAIN = new DiscreteDomain<>() {
																		@Override
																		public MinuteOfHour next(final MinuteOfHour val) {
																			if (val.is(MinuteOfHour.of(59))) throw new IllegalArgumentException();
																			return val.nextMinuteOfHour();
																		}
																		@Override
																		public MinuteOfHour previous(final MinuteOfHour val) {
																			if (val.is(MinuteOfHour.of(0))) throw new IllegalArgumentException();
																			return val.prevMinuteOfHour();
																		}
																		@Override
																		public long distance(final MinuteOfHour start,final MinuteOfHour end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<MinuteOfHour> createContiguousSetOf(final Range<MinuteOfHour> range) {
		return MinuteOfHour.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<MinuteOfHour> createContiguousSetOf(final com.google.common.collect.Range<MinuteOfHour> range) {
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
		if (obj instanceof MinuteOfHour) return ((MinuteOfHour)obj).getMinuteOfHour() == this.minuteOfHour;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.minuteOfHour).hashCode();
	}
	@Override
	public int compareTo(final MinuteOfHour other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
