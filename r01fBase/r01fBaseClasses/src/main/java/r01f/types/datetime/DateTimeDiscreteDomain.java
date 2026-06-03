package r01f.types.datetime;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import com.google.common.collect.DiscreteDomain;

/**
 * Guava's {@link DiscreteDomain} for the given range
 * ie: if the range is 01/01/2023 to 05/01/2023 and the slot duration days is 1
 * 	   this function will return a {@link DiscreteDomain} like;
 * 			01/01/2023,02/01/2023,03/01/2023,04/01/2023,05/01/2023
 * Usage:
 * <pre class='brush:java'>
 *		// range
 *		Instant start =  Instant.now();
 *		Instant end = start.plus(1,ChronoUnit.DAYS);
 *		Range<Instant> range = Range.closed(start,end);
 *
 *		// discrete domain
 *		DiscreteDomain<Instant> discreteDomain = new DateTimeDiscreteDomain<Instant>(ChronoUnit.HOURS);
 *		Collection<Instant> dates = range.asCollectionUsingDiscreteDomain(discreteDomain);
 *		dates.forEach( d -> {
 *			System.out.println(d);
 *		});
 *
 *		Result : 2025-12-28T22:06:31.249080Z
 *				 2025-12-28T23:06:31.249080Z
 *				 2025-12-29T00:06:31.249080Z
 *				 2025-12-29T01:06:31.249080Z
 *				 2025-12-29T02:06:31.249080Z
 *				 2025-12-29T03:06:31.249080Z
 *				 2025-12-29T04:06:31.249080Z
 *				 2025-12-29T05:06:31.249080Z
 *				 2025-12-29T06:06:31.249080Z
 *				 2025-12-29T07:06:31.249080Z
 *				 2025-12-29T08:06:31.249080Z
 *				 2025-12-29T09:06:31.249080Z
 *				 2025-12-29T10:06:31.249080Z
 *				 2025-12-29T11:06:31.249080Z
 *				 2025-12-29T12:06:31.249080Z
 *				 2025-12-29T13:06:31.249080Z
 *				 2025-12-29T14:06:31.249080Z
 *				 2025-12-29T15:06:31.249080Z
 *				 2025-12-29T16:06:31.249080Z
 *				 2025-12-29T17:06:31.249080Z
 *				 2025-12-29T18:06:31.249080Z
 *				 2025-12-29T19:06:31.249080Z
 *				 2025-12-29T20:06:31.249080Z
 *				 2025-12-29T21:06:31.249080Z
 *				 2025-12-29T22:06:31.249080Z
 * </pre>
 *
 * BEWARE!!!
 * The ContiguousSet.create(range,discreteDomain) does NOT check that the created value on every next() call is INSIDE the range
 * ... so if the DiscreteDomain next() call retuns a value OVER the range end, the ContiguousSet can become INFINITE
 * ... sometime the  DiscreteDomain next() call MUST return the UPPER range value EXACTLY
 */
public class DateTimeDiscreteDomain<T extends Temporal & Comparable<? super T>>
  	 extends DiscreteDomain<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ChronoUnit _slotChronoUnit;
	private final long _units;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DateTimeDiscreteDomain(final ChronoUnit chronoUnit) {
		this(1,chronoUnit);
	}
	public DateTimeDiscreteDomain(final long units,final ChronoUnit chronoUnit) {
		if (chronoUnit == null) throw new IllegalArgumentException("chronoUnit is NOT correct!!");
		_units = units > 0 ? units : 1;
		_slotChronoUnit = chronoUnit;
	}
	public static DateTimeDiscreteDomain<Instant> forInstant(final ChronoUnit slotChronoUnit) {
		return new DateTimeDiscreteDomain<Instant>(slotChronoUnit);
	}
	public static DateTimeDiscreteDomain<LocalDate> forLocalDate(final ChronoUnit slotChronoUnit) {
		if (slotChronoUnit == null) throw new IllegalArgumentException("chronoUnit is NULL!");
		if (!(slotChronoUnit != ChronoUnit.YEARS
		   || slotChronoUnit != ChronoUnit.MONTHS
		   || slotChronoUnit != ChronoUnit.WEEKS
		   || slotChronoUnit != ChronoUnit.DAYS
		   || slotChronoUnit != ChronoUnit.HALF_DAYS))
			throw new IllegalArgumentException("chronoUnit is NOT correct!!");

		return new DateTimeDiscreteDomain<LocalDate>(slotChronoUnit);
	}
	public static DateTimeDiscreteDomain<LocalTime> forLocalTime(final ChronoUnit slotChronoUnit) {
		if (slotChronoUnit == null) throw new IllegalArgumentException("chronoUnit is NULL!");
		if (!(slotChronoUnit != ChronoUnit.HOURS
		   || slotChronoUnit != ChronoUnit.MINUTES
		   || slotChronoUnit != ChronoUnit.SECONDS
		   || slotChronoUnit != ChronoUnit.MILLIS))
			throw new IllegalArgumentException("chronoUnit is NOT correct!!");

		return new DateTimeDiscreteDomain<LocalTime>(slotChronoUnit);
	}
	public static DateTimeDiscreteDomain<OffsetDateTime> forOffsetDateTime(final ChronoUnit slotChronoUnit) {
		return new DateTimeDiscreteDomain<OffsetDateTime>(slotChronoUnit);
	}
	public static DateTimeDiscreteDomain<ZonedDateTime> forZonedDateTime(final ChronoUnit slotChronoUnit) {
		return new DateTimeDiscreteDomain<ZonedDateTime>(slotChronoUnit);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T next(final T value) {
		return (T)value.plus(_units,_slotChronoUnit);
	}
	@Override
	public T previous(final T value) {
		return (T)value.minus(_units,_slotChronoUnit);
	}
	@Override
	public long distance(final T start, final T end) {
		return _slotChronoUnit.between(start,end) / _units;
	}
}
