package r01f.util.types.ranges;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.BoundType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * {@link Range} of some types used at REST resources where a type with a single String arg is needed
 * to build the @PathParam mapped resource function parameters
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class Ranges {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a default closed range within the given bounds
	 * @param <T>
	 * @param low
	 * @param up
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> rangeFrom(final T low,final T up) {
		Range<T> outRange = null;
		if (low != null && up != null) {
			outRange = Range.closed(low,up);
		} else if (low != null && up == null) {
			outRange = Range.atLeast(low);
		} else if (low == null && up != null) {
			outRange = Range.atMost(up);
		} else {
			outRange = Range.all();
		}
		return outRange;
	}
	public static <T extends Comparable<? super T>> Range<T> rangeFrom(final T low, final BoundType lowBoundType,
																	   final T up, final BoundType upBoundType) {
		return new Range<>(low, lowBoundType, up, upBoundType);
	}
	public static <T extends Comparable<? super T>> com.google.common.collect.Range<T> guavaRangeFrom(final T low,final T up) {
		com.google.common.collect.Range<T> outRange = null;
		if (low != null && up != null) {
			outRange = com.google.common.collect.Range.closed(low,up);
		} else if (low != null && up == null) {
			outRange = com.google.common.collect.Range.atLeast(low);
		} else if (low == null && up != null) {
			outRange = com.google.common.collect.Range.atMost(up);
		} else {
			outRange = com.google.common.collect.Range.all();
		}
		return outRange;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE MAPPING
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T1 extends Comparable<? super T1>,T2 extends Comparable<? super T2>>
				  Range<T2> mapRange(final Range<T1> range,
									 final Function<T1,T2> mapper) {
		T2 low = range.hasLowerBound() ? mapper.apply(range.lowerEndpoint()) : null;
		T2 up = range.hasUpperBound() ? mapper.apply(range.upperEndpoint()) : null;
		return new Range<T2>(low,range.lowerBoundType(),
							  up,range.upperBoundType());
	}
	public static <T1 extends Comparable<? super T1>,T2 extends Comparable<? super T2>>
				  com.google.common.collect.Range<T2> mapGuavaRange(final com.google.common.collect.Range<T1> range,
									 						   		final Function<T1,T2> mapper) {
		T2 low = range.hasLowerBound() ? mapper.apply(range.lowerEndpoint()) : null;
		T2 up = range.hasUpperBound() ? mapper.apply(range.upperEndpoint()) : null;
		return com.google.common.collect.Range.range(low,range.lowerBoundType(),
													 up,range.upperBoundType());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RANGE MERGING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Merges ranges
	 * ie: given these date ranges ordered by lower bound
	 * 		range1  range2  range3  range4  range5  range6
	 *         |       |
	 *         |       |
	 *                 |      |
	 *                        |                        |
	 *                        |                        |
	 *                                         |
	 *                                 |       |
	 *                                 |       |
	 *                                 |       |
	 *     this method will return a single range that elapses all ranges
	 * @param dateRanges
	 * @return
	 */
    public static <T extends Comparable<T>> Collection<Range<T>> mergeRanges(final Collection<Range<T>> ranges) {
        if (ranges.isEmpty()) return null;

		Collection<Range<T>> mergedRanges = new ArrayList<>();

		// Sort the ranges by their start values
		List<Range<T>> theRanges = Lists.newArrayList(ranges);
		theRanges.sort(Comparator.comparing(Range::lowerEndpoint));	// compare by the lowerendpoint

		Range<T> currentRange = theRanges.get(0);
		for (int i = 1; i < theRanges.size(); i++) {
			Range<T> nextRange = theRanges.get(i);

			// If the current range overlaps with the next range, then merge them.
			if (currentRange.isConnected(nextRange)) {
				currentRange = currentRange.span(nextRange);	// merge
			} else {
				// Otherwise, add the current range to the merged ranges and start a new range.
				mergedRanges.add(currentRange);
				currentRange = nextRange;
			}
		}
		// Add the last range to the merged ranges.
		mergedRanges.add(currentRange);

		return mergedRanges;
	}
	/**
	 * Merges adjacent ranges
	 * ie: given these ranges ordered by lower endpoint
	 * 		range1  range2  range3  range4  range5  range6
	 *         |       |
	 *         |       |
	 *                 |      |
	 *                        |                        |
	 *                        |                        |
	 *                                         |
	 *                                 |       |
	 *                                 |       |
	 *                                 |       |
	 *     this method will return the following ranges
	 *     	- rangeA: range1 & range3 & range5
	 *      - rangeB: range2 & range6
	 *      - rangeC: range4
	 *     elapses all ranges
	 * @param dateRanges
	 * @return
	 */
    public static <T extends Comparable<T>> Collection<Range<T>> mergeAdjacentRanges(final Collection<Range<T>> ranges) {
        if (ranges.isEmpty()) return null;

		Collection<Range<T>> mergedRanges = new ArrayList<>();

		// Sort the ranges by their start values
		List<Range<T>> theRanges = Lists.newArrayList(ranges);
		theRanges.sort(Comparator.comparing(Range::lowerEndpoint));	// compare by the lowerendpoint

		Range<T> currentRange = theRanges.get(0);
		for (int i = 1; i < theRanges.size(); i++) {
			Range<T> nextRange = theRanges.get(i);

			// If the current range overlaps with the next range, then merge them.
			if (currentRange.getUpperBound().equals(nextRange.getLowerBound())) {
				currentRange = currentRange.span(nextRange);	// merge
			} else {
				// Otherwise, add the current range to the merged ranges and start a new range.
				mergedRanges.add(currentRange);
				currentRange = nextRange;
			}
		}
		// Add the last range to the merged ranges.
		mergedRanges.add(currentRange);

		return mergedRanges;
	}
	/**
	 * Groups the date ranges by non overlapping consecutive ranges
	 * ie: given these ranges ordered by lower bound
	 * 		range1  range2  range3  range4  range5  range6
	 *         |       |
	 *         |       |
	 *                 |      |
	 *                        |                        |
	 *                        |                        |
	 *                                         |
	 *                                 |       |
	 *                                 |       |
	 *                                 |       |
	 *
	 *     this method will return the following collections
	 *     of non overlapping ranges
	 *     		- collection1: range1,range3,range4
	 *      	- collection2: range2,range5,range6
	 *
	 *     NOTE: if the ranges were ordered by length first
	 *     		 and by lower bound second
	 * 		range5  range2  range3  range4  range1  range6
	 *                 |                       |
	 *                 |                       |
	 *                 |      |
	 *                        |                        |
	 *                        |                        |
	 *         |
	 *         |                       |
	 *         |                       |
	 *         |                       |
	 *
	 * 	   this method will return the following collections
	 *     of non overlapping ranges:
	 *      	- collection1: range5,range2,range6
	 *      	- collection2: range3,range4,range1
	 *     ...which is the SAME
	 * @param ranges
	 * @return
	 */
	public static <T extends Comparable<T>> Collection<Collection<Range<T>>> nonOverlappingRangeGroups(final List<Range<T>> ranges) {
		Collection<Collection<Range<T>>> outGroups = Lists.newArrayList();

		// clone the ranges so it's NOT modified
		List<Range<T>> theRanges = Lists.newArrayList(ranges);
		theRanges.sort(Comparator.comparing(Range::lowerEndpoint));	// sort by lower endpoint

		for (Range<T> range : theRanges) {
			// try to add the range to any of the collections of not connected date ranges
			// ... or to a new one if no suitable one is found
			boolean added = false;
			for (Collection<Range<T>> currNonOverlappingRanges : outGroups) {
				boolean fits = _fitsInRangesCollectionWithoutOverlappingAny(range,
										  			 		 	   			currNonOverlappingRanges);
				if (fits) {
					// fits in an existing adjacent date ranges
					currNonOverlappingRanges.add(range);	// remember that the adjacent date ranges collection is SORTED by start date
					added = true;
					break;
				}
			}
			if (!added) {
				Collection<Range<T>> newNonOverlappingRanges = Sets.newTreeSet(Comparator.comparing(Range::lowerEndpoint));	// beware! ordered by start date
				newNonOverlappingRanges.add(range);
				outGroups.add(newNonOverlappingRanges);
			}
		}
		return outGroups;
	}
	/**
	 * Checks if the given date range can be "inserted" in the given date ranges collection
	 * so there's NO overlapping between date ranges
	 * ie: if the adjacent date ranges collection is like
	 * 			+----A----+                 +-----B-----+  +---C-----+
	 * 	   and the given date range is:
	 * 			             +-----N-------+
	 * 	   the function will return TRUE since [N] fits between [A] and [B]
	 *     BUT if the given date range is:
	 *     		                   +-------N------+
	 *     the function will return FALSE since [N] does NOT fit in any empty slot
	 * @param range
	 * @param ranges
	 * @return
	 */
	private static <T extends Comparable<T>> boolean _fitsInRangesCollectionWithoutOverlappingAny(final Range<T> range,
										   		 		 	   									  final Collection<Range<T>> ranges) {
		boolean outFits = false;
		if (CollectionUtils.isNullOrEmpty(ranges)) {
			outFits = true;
		} else {
			for (int i=0; i < ranges.size(); i++) {
				if (i < (ranges.size() - 1)) {
					// otherRange1: +--------+
					// otherRange2:                          +-------+
					//   dateRange:             +---------+
					Range<T> otherRange1 = Iterables.get(ranges,i);
					Range<T> otherRange2 = Iterables.get(ranges,i+1);
					outFits = _isLessOrEqual(otherRange1.upperEndpoint(),range.lowerEndpoint())
						   && _isGreaterOrEqual(otherRange2.lowerEndpoint(),range.upperEndpoint());
				} else {
					// otherRange: +--------+
					//  dateRange:             +-------+
					Range<T> otherRange = Iterables.get(ranges,i);
					outFits = _isLessOrEqual(otherRange.upperEndpoint(),range.lowerEndpoint());
				}
				if (outFits) break;
			}
		}
		return outFits;
	}
	private static <T extends Comparable<T>> boolean _isLessOrEqual(final T a,final T b) {
		return a.compareTo(b) <= 0;
	}
	private static <T extends Comparable<T>> boolean _isGreaterOrEqual(final T a,final T b) {
		return a.compareTo(b) >= 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Validates that the time span represented by the given {@link Range} does not exceed a maximum
	 * number of {@link ChronoUnit} units.
	 * <p>
	 * Behavior details:
	 * <ul>
	 *   <li>If the range has no lower bound, the result is invalid.</li>
	 *   <li>If the range has no upper bound, {@link Instant#now()} is used as upper bound.</li>
	 *   <li>The elapsed amount is computed as complete units (floor), using {@link Duration}:
	 *       DAYS, HOURS, MINUTES, SECONDS, MILLIS, NANOS.</li>
	 *   <li>If computed units are greater than {@code numUnits}, the result is invalid; otherwise valid.</li>
	 * </ul>
	 *
	 * @param range the {@link Range} of {@link Instant} to validate
	 * @param numUnits maximum allowed number of units inside the range
	 * @param unit unit used to measure the range span
	 * @return an {@link ObjectValidationResult} that is valid when the range contains at most
	 *         {@code numUnits} of {@code unit}
	 *
	 * @throws IllegalArgumentException if lower/upper instants are null, upper bound is before lower bound,
	 *                                  or an unsupported {@code unit} is provided
	 *
	 * @apiNote This utility counts complete elapsed units (for example, 47 hours == 1 day when unit is DAYS),
	 *          not calendar boundaries.
	 *
	 * <p><b>Usage example:</b></p>
	 * <pre>{@code
	 * 		Range<Instant> last48Hours = Range.closed(Instant.now().minus(48, ChronoUnit.HOURS),Instant.now());
	 * 		ObjectValidationResult<Range<Instant>> result = DN00DateTimeUtils.doesRangeContainsAtMostXChronoUnits(last48Hours,2,ChronoUnit.DAYS);
	 * }</pre>
	 * @param range
	 * @param numUnits
	 * @param unit
	 * @return
	 */
	public static ObjectValidationResult<Range<Instant>> doesRangeContainsAtMostXChronoUnits(final Range<Instant> range,
													   								  		 final long numUnits,final ChronoUnit unit) {
		if (range == null) return ObjectValidationResultBuilder.on(range)
															   .isNotValidBecause("the range is null");
		if (!range.hasLowerBound()) return ObjectValidationResultBuilder.on(range)
																		.isNotValidBecause("the range {} does NOT contain lower bound",
																						   range);
		Instant lowerBound = range.getLowerBound();
		Instant upperBound = range.hasUpperBound() ? range.getUpperBound()
												   : Instant.now();
		long unitsWithin = _chronoUnitsWithin(lowerBound,upperBound,
											  unit);
		return unitsWithin > numUnits ? ObjectValidationResultBuilder.on(range)
																	 .isNotValidBecause("the range {} contains more than the allowed maximum {} {}",
																						range,numUnits,unit)
									  : ObjectValidationResultBuilder.on(range)
									  								 .isValid();
	}
	private static long _chronoUnitsWithin(final Instant from, final Instant to,
										   final ChronoUnit unit) {
		if (from == null || to == null) throw new IllegalArgumentException("Range bounds cannot be null");
		if (to.isBefore(from)) throw new IllegalArgumentException("Range upper bound cannot be before lower bound");
		Duration duration = Duration.between(from,to);
		return switch(unit) {
					case DAYS -> duration.toDays();
					case HOURS -> duration.toHours();
					case MINUTES -> duration.toMinutes();
					case SECONDS -> duration.toSeconds();
					case MILLIS -> duration.toMillis();
					case NANOS -> duration.toNanos();
					default -> throw new IllegalArgumentException("Unexpected value: " + unit);
					
			   };
	}
}
