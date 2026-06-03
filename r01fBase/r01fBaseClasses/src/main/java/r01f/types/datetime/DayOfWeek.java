package r01f.types.datetime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@MarshallType(as="dayOfWeek")
public record DayOfWeek(int dayOfWeek)
  implements CanBeRepresentedAsString,
  			 Comparable<DayOfWeek> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfWeek(final java.time.DayOfWeek dayOfWeek) {
		this(dayOfWeek.getValue());
	}
	public DayOfWeek(final int dayOfWeek) {
		if (dayOfWeek <= 0 || dayOfWeek > 7) throw new IllegalArgumentException("Not a valid day of week");
		this.dayOfWeek = dayOfWeek;
	}
	public DayOfWeek(final Integer dayOfWeek) {
		this(dayOfWeek.intValue());
	}
	public DayOfWeek(final String dayOfWeekStr) {
		this(Integer.parseInt(dayOfWeekStr));
	}
	////////// Builders
	public static DayOfWeek of(final java.time.DayOfWeek dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek of(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	@Deprecated
	public static DayOfWeek of(final Date date) {
		return DayOfWeek.of(date.toInstant(),ZoneId.systemDefault());
	}
	@Deprecated
	public static DayOfWeek of(final Date date,final ZoneId zone) {
		return DayOfWeek.of(date.toInstant(),zone);
	}
	public static DayOfWeek of(final Instant instant,final ZoneId zone) {
		LocalDate date = LocalDateTime.ofInstant(instant,
												 zone)
									  .toLocalDate();
		java.time.DayOfWeek dow = date.getDayOfWeek();	// natively follows the ISO-8601 standard
		return new DayOfWeek(dow.getValue());
	}
	public static DayOfWeek of(final LocalDate date) {
		java.time.DayOfWeek dow = date.getDayOfWeek();	// natively follows the ISO-8601 standard
		return new DayOfWeek(dow.getValue());
	}
	public static DayOfWeek of(final LocalDateTime dateTime) {
		java.time.DayOfWeek dow = dateTime.getDayOfWeek();	// natively follows the ISO-8601 standard
		return new DayOfWeek(dow.getValue());
	}
	public static DayOfWeek of(final OffsetDateTime date) {
		java.time.DayOfWeek dow = date.getDayOfWeek();	// natively follows the ISO-8601 standard
		return new DayOfWeek(dow.getValue());
	}
	public static DayOfWeek of(final ZonedDateTime date) {
		java.time.DayOfWeek dow = date.getDayOfWeek();	// natively follows the ISO-8601 standard
		return new DayOfWeek(dow.getValue());
	}
	public static DayOfWeek of(final int dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek nowAtUTC() {
		return DayOfWeek.nowAt(ZoneId.of("UTC"));
	}
	public static DayOfWeek nowAt(final ZoneId zone) {
		return DayOfWeek.of(ZonedDateTime.now(zone));
	}
	public static DayOfWeek valueOf(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
	public static DayOfWeek fromString(final String dayOfWeek) {
		return new DayOfWeek(dayOfWeek);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	conversion
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return Long.toString(this.dayOfWeek);
	}
	@Override
	public String asString() {
		return this.toString();
	}
	public int asInteger() {
		return this.dayOfWeek;
	}
	public java.time.DayOfWeek asDayWeek() {
		return java.time.DayOfWeek.of(this.dayOfWeek);	// ALWAYS ISO-8601: 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday, 7=Sunday
	}
	public int asIntegerStartingOnMonday() {
		return this.dayOfWeek == 1 ? 7					// sunday becomes 7
							   	   : this.dayOfWeek - 1;// monday becomes 1 and so on
	}
	public int asIntegerStartingOnSunday() {
		return this.dayOfWeek;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTERS
/////////////////////////////////////////////////////////////////////////////////////////
	public int getDayOfWeek() {
		return this.dayOfWeek;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBe(final String str) {
		 return Strings.isNOTNullOrEmpty(str)
			 && Numbers.isInteger(str)
			 && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 7;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	operations
/////////////////////////////////////////////////////////////////////////////////////////
	public DayOfWeek minus(final int num) {
		int newVal = this.dayOfWeek - num;
		if (newVal <= 0) newVal = 7 - Math.abs(newVal);
		return DayOfWeek.of(newVal);
	}
	public DayOfWeek plus(final int months) {
		int newVal = this.dayOfWeek + months;
		if (newVal > 7) newVal = 7 - newVal;
		return DayOfWeek.of(newVal);
	}
	public DayOfWeek nextDayOfWeek() {
		return this.dayOfWeek < 7 ? DayOfWeek.of(this.dayOfWeek+1)
							  : DayOfWeek.of(1);
	}
	public DayOfWeek prevDayOfWeek() {
		return this.dayOfWeek > 1 ? DayOfWeek.of(this.dayOfWeek-1)
							  : DayOfWeek.of(7);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final DayOfWeek other) {
		return this.dayOfWeek == other.asInteger();
	}
	public boolean isNOT(final DayOfWeek other) {
		return !this.is(other);
	}
	public boolean isBefore(final DayOfWeek other) {
		return this.dayOfWeek < other.asInteger();
	}
	public boolean isAfter(final DayOfWeek other) {
		return this.dayOfWeek > other.asInteger();
	}
	public boolean isBeforeOrEqual(final DayOfWeek other) {
		return this.dayOfWeek <= other.asInteger();
	}
	public boolean isAfterOrEqual(final DayOfWeek other) {
		return this.dayOfWeek >= other.asInteger();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	instant range at year/weekOfYear (iso = week starts on MONDAY)
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final WeekOfYear weekOfYear) {
		LocalDate monday = LocalDate.of(year.getYear(),1,1) // Anchor in the year
					                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,weekOfYear.asInteger())
					                .with(WeekFields.ISO.dayOfWeek(),1); // 1 = Monday
		return monday.atStartOfDay()
					 .atZone(ZoneId.of("UTC"))
					 .toInstant();
	}
	public Instant endInstantAt(final Year year,final WeekOfYear weekOfYear) {
		Instant startInstant = this.startInstantAt(year,weekOfYear);
		return startInstant.plus(7,ChronoUnit.DAYS)	// next week's start instant
						   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final WeekOfYear weekOfYear) {
		return Range.closed(this.startInstantAt(year,weekOfYear),this.endInstantAt(year, weekOfYear));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	instant range at year/weekOfYear (localized)
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant startInstantAt(final Year year,final WeekOfYear weekOfYear,
								  final Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);
		LocalDate monday = LocalDate.of(year.getYear(),1,1) // Anchor in the year
					                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,weekOfYear.asInteger())
					                .with(weekFields.dayOfWeek(),1); // 1 = Monday or sunday depending on locale
		return monday.atStartOfDay()
					 .atZone(ZoneId.of("UTC"))
					 .toInstant();
	}
	public Instant endInstantAt(final Year year,final WeekOfYear weekOfYear,
								final Locale locale) {
		Instant startInstant = this.startInstantAt(year,weekOfYear);
		return startInstant.plus(7,ChronoUnit.DAYS)	// next week's start instant
						   .minusNanos(1);
	}
	public Range<Instant> getAsInstantRangeAt(final Year year,final WeekOfYear weekOfYear,
											  final Locale locale) {
		return Range.closed(this.startInstantAt(year,weekOfYear,locale),this.endInstantAt(year,weekOfYear,locale));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ITERABLE
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterable<DayOfWeek> weekDaysIterable() {
		return new Iterable<>() {
						@Override
						public Iterator<DayOfWeek> iterator() {
							return new Iterator<>() {
											private int _curr = -1;

											@Override
											public boolean hasNext() {
												return _curr < 7;
											}
											@Override
											public DayOfWeek next() {
												if (!this.hasNext()) throw new IllegalStateException();
												_curr = _curr == -1 ? 0
																	: _curr + 1;
												return DayOfWeek.of(_curr);
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
	 *														 Year.DISCRETE_DOMAIN);
	 * </pre>
	 */
	private static DiscreteDomain<DayOfWeek> DISCRETE_DOMAIN = new DiscreteDomain<>() {
																		@Override
																		public DayOfWeek next(final DayOfWeek val) {
																			if (val.is(DayOfWeek.of(7))) throw new IllegalArgumentException();
																			return val.nextDayOfWeek();
																		}
																		@Override
																		public DayOfWeek previous(final DayOfWeek val) {
																			if (val.is(DayOfWeek.of(1))) throw new IllegalArgumentException();
																			return val.prevDayOfWeek();
																		}
																		@Override
																		public long distance(final DayOfWeek start,final DayOfWeek end) {
																			return end.asInteger() - start.asInteger();
																		}
																 };
	public static ContiguousSet<DayOfWeek> createContiguousSetOf(final Range<DayOfWeek> range) {
		return DayOfWeek.createContiguousSetOf(range.asGuavaRange());
	}
	public static ContiguousSet<DayOfWeek> createContiguousSetOf(final com.google.common.collect.Range<DayOfWeek> range) {
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
		if (obj instanceof DayOfWeek) return ((DayOfWeek)obj).getDayOfWeek() == this.dayOfWeek;
		return false;
	}
	@Override
	public int hashCode() {
		return Integer.valueOf(this.dayOfWeek).hashCode();
	}
	@Override
	public int compareTo(final DayOfWeek other) {
		return Integer.valueOf(this.asInteger())
						.compareTo(Integer.valueOf(other.asInteger()));
	}
}
