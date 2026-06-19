package r01f.types.datetime;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;

@MarshallType(as="halfHourOfDay")
public record HalfHourOfDay(int hour,
							int halfHour)
   implements Serializable,
  			  CanBeRepresentedAsString,
  			  Comparable<HalfHourOfDay> {

	private static final long serialVersionUID = 8469048677426320587L;
/////////////////////////////////////////////////////////////////////////////////////////
//  REGEX
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String REGEX = "([0-9]|0[0-9]|1[0-9]|2[0-3])";
	public static final String REGEX_NOCAPTURE = "(?:[0-9]|0[0-9]|1[0-9]|2[0-3])";
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HalfHourOfDay(final int hour,final int halfHour) {
		if (hour >= 24 || hour < 0 || halfHour < 0 || halfHour > 1) throw new IllegalArgumentException("Not a valid half hour of day");
		this.hour = hour;
		this.halfHour = halfHour;
	}
	public HalfHourOfDay(final Integer hour,final Integer halfHour) {
		this(hour.intValue(),halfHour.intValue());
	}
	public HalfHourOfDay(final String hourOfDayStr,final String halfHour) {
		this(Integer.parseInt(hourOfDayStr),Integer.parseInt(halfHour));
	}
	////////// Builders
	public static HalfHourOfDay of(final String hourOfDay,final String halfHourOfDay) {
		return new HalfHourOfDay(hourOfDay,halfHourOfDay);
	}
	public static HalfHourOfDay of(final int hourOfDay,final int halfHourOfDay) {
		return new HalfHourOfDay(hourOfDay,halfHourOfDay);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.getHalfHourOfDay();
	}
	@Override
	public String asString() {
		return this.toString();
	}

	public String asStringPaddedWithZero() {
		return StringUtils.leftPad(this.asString(),5,'0');
	}

	public HalfHourOfDay nextHalfHour() {
		return this.halfHour == 1
			? new HalfHourOfDay(this.hour+1,0)
			: new HalfHourOfDay(this.hour,1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getHour() {
		return this.hour;
	}
	public int getHalfHour() {
		return this.halfHour;
	}
	public String getHalfHourOfDay() {
		return String.valueOf(this.hour)
					 .concat(":")
					 .concat(halfHour == 0 ? "00"
										   : "30");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
			return Strings.isNOTNullOrEmpty(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof HalfHourOfDay) return ((HalfHourOfDay)obj).getHalfHourOfDay().equals(this.getHalfHourOfDay());
		return false;
	}
	@Override
	public int hashCode() {
		return this.getHalfHourOfDay().hashCode();
	}
	@Override
	public int compareTo(final HalfHourOfDay other) {
		return this.compareTo(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////

	public static Iterable<HalfHourOfDay> dayHoursIterableWithin(final int start,final int end) {
		return new Iterable<>() {
						@Override
						public Iterator<HalfHourOfDay> iterator() {
							return new Iterator<>() {
											private int _curr = start;
											private boolean isHalf = false;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public HalfHourOfDay next() {
												if (!this.hasNext()) throw new IllegalStateException();
												HalfHourOfDay result = isHalf
																			? HalfHourOfDay.of(_curr,1)
																			: HalfHourOfDay.of(_curr,0);
												_curr = _curr == -1 ? 0
																	: isHalf
																		? _curr + 1
																		: _curr;

												isHalf = !isHalf;
												return result;
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
//
/////////////////////////////////////////////////////////////////////////////////////////
	/*public boolean is(final HalfHourOfDay other) {
		return _hourOfDay == other.asInteger();
	}
	public boolean isNOT(final HalfHourOfDay other) {
		return !this.is(other);
	}
	public boolean isBefore(final HalfHourOfDay other) {
		return _hourOfDay < other.asInteger();
	}
	public boolean isAfter(final HalfHourOfDay other) {
		return _hourOfDay > other.asInteger();
	}
	public boolean isBeforeOrEqual(final HalfHourOfDay other) {
		return _hourOfDay <= other.asInteger();
	}
	public boolean isAfterOrEqual(final HalfHourOfDay other) {
		return _hourOfDay >= other.asInteger();
	}*/
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	/*public static Iterable<HalfHourOfDay> dayHoursIterable() {
		return _dayHoursIterableWithin(0,24);
	}
	public static Iterable<HalfHourOfDay> dayHoursIterableFrom(final HalfHourOfDay start) {
		return _dayHoursIterableWithin(start.asInteger(),24);
	}
	public static Iterable<HalfHourOfDay> dayHoursIterableTo(final HalfHourOfDay end) {
		return _dayHoursIterableWithin(0,end.asInteger());
	}
	public static Iterable<HalfHourOfDay> dayHoursIterableWithin(final HalfHourOfDay start,final HalfHourOfDay end) {
		return _dayHoursIterableWithin(start.asInteger(),end.asInteger());
	}
	private static Iterable<HalfHourOfDay> _dayHoursIterableWithin(final int start,final int end) {
		return new Iterable<HalfHourOfDay>() {
						@Override
						public Iterator<HalfHourOfDay> iterator() {
							return new Iterator<HalfHourOfDay>() {
											private int _curr = start - 1;

											@Override
											public boolean hasNext() {
												return _curr < (end - 1);
											}
											@Override
											public HalfHourOfDay next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return HalfHourOfDay.of(_curr);
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
//
/////////////////////////////////////////////////////////////////////////////////////////
	public HalfHourOfDay nextHourOfDay() {
		if (_hourOfDay == 23) return HalfHourOfDay.of(0);
		return HalfHourOfDay.of(_hourOfDay + 1);
	}
	public HalfHourOfDay prevHourOfDay() {
		if (_hourOfDay == 0) return HalfHourOfDay.of(23);
		return HalfHourOfDay.of(_hourOfDay - 1);
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
	/*public static DiscreteDomain<HalfHourOfDay> DISCRETE_DOMAIN = new DiscreteDomain<HalfHourOfDay>() {
																		@Override
																		public HalfHourOfDay next(final HalfHourOfDay val) {
																			if (val.is(HalfHourOfDay.of(23))) throw new IllegalArgumentException();
																			return val.nextHourOfDay();
																		}
																		@Override
																		public HalfHourOfDay previous(final HalfHourOfDay val) {
																			if (val.is(HalfHourOfDay.of(0))) throw new IllegalArgumentException();
																			return val.prevHourOfDay();
																		}
																		@Override
																		public long distance(final HalfHourOfDay start,final HalfHourOfDay end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<HalfHourOfDay> createContiguousSetOf(final Range<HalfHourOfDay> range) {
		return HalfHourOfDay.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<HalfHourOfDay> createContiguousSetOf(final com.google.common.collect.Range<HalfHourOfDay> range) {
		if (!range.hasLowerBound() || !range.hasUpperBound()) throw new IllegalArgumentException("range MUST be a CLOSED range (it MUST have upper and lower bounds)!");
		if (range.upperEndpoint().isBefore(range.lowerEndpoint())) throw new IllegalArgumentException("range upper bound is AFTER the lower bound!!");
		return ContiguousSet.create(range,
									 DISCRETE_DOMAIN);
	}*/
}
