package r01f.types.datetime;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

@MarshallType(as="this.hourOfDay")
public record HourOfDay(int hourOfDay)
   implements Serializable,
  			  CanBeRepresentedAsString,
  			  Comparable<HourOfDay> {

	private static final long serialVersionUID = 8445517567471520680L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-9]|0[0-9]|1[0-9]|2[0-3])";
	public static final String REGEX_NOCAPTURE = "(?:[0-9]|0[0-9]|1[0-9]|2[0-3])";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HourOfDay(final int hourOfDay) {
		if (hourOfDay >= 24 || hourOfDay < 0) throw new IllegalArgumentException("Not a valid hour of day");
		this.hourOfDay = hourOfDay;
	}
	public HourOfDay(final Integer hourOfDay) {
		this(hourOfDay.intValue());
	}
	public HourOfDay(final String hourOfDayStr) {
		this(Integer.parseInt(hourOfDayStr));
	}
	////////// Builders
	public static HourOfDay of(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	@Deprecated
	public static HourOfDay of(final Date date) {
		return HourOfDay.of(date.toInstant());
	}
	public static HourOfDay of(final Instant instant) {
		LocalTime time = instant.atZone(ZoneId.of("UTC"))
								.toLocalTime();
		return HourOfDay.of(time);
	}
	public static HourOfDay of(final LocalTime time) {
		return new HourOfDay(time.getHour());
	}
	public static HourOfDay of(final LocalDateTime time) {
		return new HourOfDay(time.getHour());
	}
	public static HourOfDay of(final ZonedDateTime date) {
		return new HourOfDay(date.getHour());
	}
	public static HourOfDay of(final OffsetDateTime date) {
		return new HourOfDay(date.getHour());
	}
	public static HourOfDay of(final int hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay nowAtUTC() {
		return HourOfDay.nowAt(ZoneId.of("UTC"));
	}
	public static HourOfDay nowAt(final ZoneId zone) {
		return HourOfDay.of(LocalTime.now(zone));
	}
	public static HourOfDay valueOf(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
	public static HourOfDay fromString(final String hourOfDay) {
		return new HourOfDay(hourOfDay);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public HourOfDay nextHourOfDay() {
		if (this.hourOfDay == 23) return HourOfDay.of(0);
		return HourOfDay.of(this.hourOfDay + 1);
	}
	public HourOfDay prevHourOfDay() {
		if (this.hourOfDay == 0) return HourOfDay.of(23);
		return HourOfDay.of(this.hourOfDay - 1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getHourOfDay() {
		return this.hourOfDay;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
			return Strings.isNOTNullOrEmpty(str)
				 && Numbers.isInteger(str)
				 && Integer.parseInt(str) >= 0 && Integer.parseInt(str) < 24;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	conversion
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.hourOfDay);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),2,'0');
	}
	public int asInteger() {
		return this.hourOfDay;
	}
	public LocalDateTime getAsLocalDateTimeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		return LocalDate.of(year.asInteger(),monthOfYear.asInteger(),dayOfMonth.asInteger())
						.atTime(this.hourOfDay,0,0,0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final HourOfDay other) {
		return this.hourOfDay == other.asInteger();
	}
	public boolean isNOT(final HourOfDay other) {
		return !this.is(other);
	}
	public boolean isBefore(final HourOfDay other) {
		return this.hourOfDay < other.asInteger();
	}
	public boolean isAfter(final HourOfDay other) {
		return this.hourOfDay > other.asInteger();
	}
	public boolean isBeforeOrEqual(final HourOfDay other) {
		return this.hourOfDay <= other.asInteger();
	}
	public boolean isAfterOrEqual(final HourOfDay other) {
		return this.hourOfDay >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE RANGE AT
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		return this.getAsLocalDateTimeAt(year,monthOfYear,dayOfMonth)
				   .atZone(ZoneId.of("UTC"))
				   .toInstant();
	}
	public Instant endInstantAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		Instant startInstant = this.startInstantAt(year,monthOfYear,dayOfMonth);
		return startInstant.plus(1,ChronoUnit.HOURS)
						   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		Instant startInstant = this.startInstantAt(year,monthOfYear,dayOfMonth);
		Instant endInstant = this.endInstantAt(year,monthOfYear,dayOfMonth);
        return Range.closed(startInstant,endInstant);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<HourOfDay> dayHoursIterable() {
		return _dayHoursIterableWithin(0,24);
	}
	public static Iterable<HourOfDay> dayHoursIterableFrom(final HourOfDay start) {
		return _dayHoursIterableWithin(start.asInteger(),24);
	}
	public static Iterable<HourOfDay> dayHoursIterableTo(final HourOfDay end) {
		return _dayHoursIterableWithin(0,end.asInteger());
	}
	public static Iterable<HourOfDay> dayHoursIterableWithin(final HourOfDay start,final HourOfDay end) {
		return _dayHoursIterableWithin(start.asInteger(),end.asInteger());
	}
	private static Iterable<HourOfDay> _dayHoursIterableWithin(final int start,final int end) {
		return new Iterable<>() {
						@Override
						public Iterator<HourOfDay> iterator() {
							return new Iterator<>() {
											private int _curr = start - 1;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public HourOfDay next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return HourOfDay.of(_curr);
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
	public static DiscreteDomain<HourOfDay> DISCRETE_DOMAIN = new DiscreteDomain<>() {
																		@Override
																		public HourOfDay next(final HourOfDay val) {
																			if (val.is(HourOfDay.of(23))) throw new IllegalArgumentException();
																			return val.nextHourOfDay();
																		}
																		@Override
																		public HourOfDay previous(final HourOfDay val) {
																			if (val.is(HourOfDay.of(0))) throw new IllegalArgumentException();
																			return val.prevHourOfDay();
																		}
																		@Override
																		public long distance(final HourOfDay start,final HourOfDay end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<HourOfDay> createContiguousSetOf(final Range<HourOfDay> range) {
		return HourOfDay.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<HourOfDay> createContiguousSetOf(final com.google.common.collect.Range<HourOfDay> range) {
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
		if (obj instanceof HourOfDay) return ((HourOfDay)obj).getHourOfDay() == this.hourOfDay;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.hourOfDay).hashCode();
	}
	@Override
	public int compareTo(final HourOfDay other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
