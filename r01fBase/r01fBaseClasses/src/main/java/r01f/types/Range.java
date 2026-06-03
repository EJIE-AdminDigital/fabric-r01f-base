package r01f.types;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.ranges.RangeParseUtils;
import r01f.util.types.ranges.RangeSerializeUtils;

/**
 * Wraps a Guava {@link com.google.common.collect.Range} in order to be serializable
 * If
 * <ul>
 * 		<li>"[" or "]" represents a lower or upper bound where the bound itself is included in the range</li>
 * 		<li>"(" or ")" represents a lower or upper bound where the bound itself is EXCLUDED from the range</li>
 * <li>
 * <pre>
 * 		(a..b)		open(C, C)
 * 		[a..b]		closed(C, C)
 * 		[a..b)		closedOpen(C, C)
 * 		(a..b]		openClosed(C, C)
 * 		(a..+oo)	greaterThan(C)
 * 		[a..+oo)	atLeast(C)
 * 		(-oo..b)	lessThan(C)
 * 		(-oo..b]	atMost(C)
 * 		(-oo..+oo)	all()
 * </pre>
 *
 * Usage:
 * <pre class='brush:java'>
 *		// Create an integer range
 *		Range<Integer> intRange = Range.open(2,3);
 *
 *		// Serialize to string
 *		String intRangeStr = intRange.toString();
 *
 *		// Convert back to range from String representation
 *		intRange = Range.parse(intRangeStr,Integer.class);
 * </pre>
 * @param <T>
 */
@Immutable
@MarshallType(as="range")
@Accessors(prefix="_")
@NoArgsConstructor
public class Range<T extends Comparable<? super T>>
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -5779835775897731838L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The range lower bound
	 */
	@Getter private T _lowerBound;
	/**
	 * The range upper bound
	 */
	@Getter private T _upperBound;
	/**
	 * The lower bound type
	 */
	@Getter private BoundType _lowerBoundType;
	/**
	 * The upper bound type
	 */
	@Getter private BoundType _upperBoundType;

/////////////////////////////////////////////////////////////////////////////////////////
//  NON-SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private transient com.google.common.collect.Range<T> _range;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T extends Comparable<? super T>> Range<T> wrap(final com.google.common.collect.Range<T> range) {
		return new Range<>(range);
	}
	public Range(final com.google.common.collect.Range<T> range) {
		_range = range;
		_upperBound = range.hasUpperBound() ? range.upperEndpoint() : null;
		_lowerBound = range.hasLowerBound() ? range.lowerEndpoint() : null;

		_upperBoundType = range.hasUpperBound() ? range.upperBoundType() : BoundType.OPEN;
		_lowerBoundType = range.hasLowerBound() ? range.lowerBoundType() : BoundType.OPEN;
	}
	public Range(final T lower,
				 final T upper) {
		this(lower,BoundType.CLOSED,
			 upper,BoundType.CLOSED);
	}
	public Range(final T lower,final BoundType lowerBoundType,
				 final T upper,final BoundType upperBoundType) {
		// store the lower and upper bounds
		_lowerBound = lower;
		_upperBound = upper;

		_lowerBoundType = lowerBoundType;
		_upperBoundType = upperBoundType;

		// Create the delegate
		if (_lowerBound != null && _upperBound != null) {
			if (lowerBoundType == BoundType.OPEN && upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.open(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.OPEN && upperBoundType == BoundType.CLOSED) {
				_range = com.google.common.collect.Range.openClosed(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.CLOSED) {
				_range = com.google.common.collect.Range.closed(_lowerBound,_upperBound);
			} else if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.closedOpen(_lowerBound,_upperBound);
			} else {
				throw new IllegalArgumentException("Both lower and upper bound types MUST be provided!");
			}
		} else if (_lowerBound != null) {
			if (lowerBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.greaterThan(_lowerBound);
			} else {
				_range = com.google.common.collect.Range.atLeast(_lowerBound);
			}
		} else if (_upperBound != null) {
			if (upperBoundType == BoundType.OPEN) {
				_range = com.google.common.collect.Range.lessThan(_upperBound);
			} else {
				_range = com.google.common.collect.Range.atMost(_upperBound);
			}
		} else {
			throw new IllegalArgumentException("Cannot create range, at least lower or upper bound SHOULD be not null");
		}
	}
	public static <T extends Comparable<? super T>> Range<T> range(final T lower,final BoundType lowerBoundType,
																   final T upper,final BoundType upperBoundType) {
		return new Range<>(lower,lowerBoundType,
						   upper,upperBoundType);
	}
	/**
	 * Creates an OPEN range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> open(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an open Range");
		return new Range<>(lower,BoundType.OPEN,
						   upper,BoundType.OPEN);
	}
	/**
	 * Creates an OPEN on the lower end and CLOSED on the upper end range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> openClosed(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an open-closed Range");
		return new Range<>(lower,BoundType.OPEN,
						   upper,BoundType.CLOSED);
	}
	/**
	 * Creates an CLOSED range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> closed(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in a closed Range");
		return new Range<>(lower,BoundType.CLOSED,
						   upper,BoundType.CLOSED);
	}
	/**
	 * Creates an CLOSED on the lower end and OPEN on the upper end range from its bounds
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> closedOpen(final T lower,final T upper) {
		if (lower == null || upper == null) throw new IllegalArgumentException("Both lower and upper bounds must be not null in an closed-open Range");
		return new Range<>(lower,BoundType.CLOSED,
						   upper,BoundType.OPEN);
	}
	/**
	 * Creates a greaterThan (>) range from its lower bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> greaterThan(final T lower) {
		if (lower == null) throw new IllegalArgumentException("lower bound must be not null in an greaterThan Range");
		return new Range<>(lower,BoundType.OPEN,
						   null,null);
	}
	/**
	 * Creates a atLeast (>=) range from its lower bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> atLeast(final T lower) {
		if (lower == null) throw new IllegalArgumentException("lower bound must be not null in an atLeast Range");
		return new Range<>(lower,BoundType.CLOSED,
						   null,null);
	}
	/**
	 * Creates a lessThan (<) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> lessThan(final T upper) {
		if (upper == null) throw new IllegalArgumentException("upper bound must be not null in an lessThan Range");
		return new Range<>(null,null,
						   upper,BoundType.OPEN);
	}
	/**
	 * Creates a atMost (<=) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> atMost(final T upper) {
		if (upper == null) throw new IllegalArgumentException("upper bound must be not null in an atMost Range");
		return new Range<>(null,null,
						   upper,BoundType.CLOSED);
	}
	/**
	 * Creates a atMost (<=) range from the upper bound
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static <T extends Comparable<? super T>> Range<T> all() {
		return new Range<>(null,null,
						   null,null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PARSE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Unsafe parse a range from its textual representation like lowerBound..upperBound
	 * It's unsafe since it can throw a {@link ClassCastException} if the dataType arg is not a {@link Comparable} type
	 * @param rangeStr
	 * @param dataType
	 * @return
	 */
	public static Range<?> unsafeParse(final String rangeStr,
									   final Class<?> dataType) {
		return RangeParseUtils.unsafeParse(rangeStr,
										   dataType);
	}
	/**
	 * Parses a range from its textual representation like lowerBound..upperBound
	 * @param rangeStr
	 * @param dataType
	 */
	public static <T extends Comparable<? super T>> Range<T> parse(final String rangeStr,
							 	 	  					   		   final Class<T> dataType) {
		return RangeParseUtils.parse(rangeStr,
									 dataType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the range as a Google Guava {@link com.google.common.collect.Range}
	 */
	public com.google.common.collect.Range<T> asGuavaRange() {
		return _range;
	}
	/**
	 * As a collection of the values between the range. Must provide a class implementing {@link import com.google.common.collect.DiscreteDomain}<T>
	 *
	 * BEWARE!!!
	 * The ContiguousSet.create(range,discreteDomain) does NOT check the range on every next() call
	 * ... so if the DiscreteDomain next() call retuns a value OVER the range end the ContiguousSet can become INFINITE
	 * ... sometime the  DiscreteDomain next() call MUST return the UPPER range value EXACTLY
	 * @param discreteDomain
	 * @return
	 */
	public Collection<T> asCollectionUsingDiscreteDomain(final DiscreteDomain<T> discreteDomain) {
		long distance = discreteDomain.distance(_lowerBound,_upperBound);
		if ( distance >= 0 ) return ContiguousSet.create(this.asGuavaRange(),discreteDomain);
		throw new IllegalArgumentException(" Not valid lowerBound or upperBound, or not contigous");
	}
	@Override
	public String asString() {
		Class<T> dataType = Range.guessDataType(this);
		return RangeSerializeUtils.rangeAsString(this,dataType);
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATA TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the data type of the range
	 */
	public Class<T> getDataType() {
		return Range.guessDataType(this);
	}
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> Class<T> guessDataType(final Range<T> range) {
		Class<T> outDataType = null;
		if (range.getLowerBound() != null) {
			outDataType = (Class<T>)range.getLowerBound().getClass();
		} else if (range.getUpperBound() != null) {
			outDataType = (Class<T>)range.getUpperBound().getClass();
		} else {
			throw new IllegalStateException("NO lower or upper bound set!");
		}
		return outDataType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAP
/////////////////////////////////////////////////////////////////////////////////////////
	public <T2 extends Comparable<? super T2>> Range<T2> map(final Function<T,T2> mapper) {
		T2 newLowerBound = this.hasLowerBound() ? mapper.apply(this.lowerEndpoint()) : null;
		T2 newUpperBound = this.hasUpperBound() ? mapper.apply(this.upperEndpoint()) : null;
		return new Range<T2>(newLowerBound,this.getLowerBoundType(),		// do not use this.lowerBound() or this.upperBound()!!!
							 newUpperBound,this.getUpperBoundType());		// do not use this.lowerBound() or this.upperBound()!!!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof com.google.common.collect.Range) {
			com.google.common.collect.Range<?> otherGuavaRange = (com.google.common.collect.Range<?>)obj;
			return _range != null ? _range.equals(otherGuavaRange)
								  : false;
		}
		else if (obj instanceof Range) {
			Range<?> otherRange = (Range<?>)obj;
			return _range != null ? otherRange.asGuavaRange() != null ? _range.equals(otherRange.asGuavaRange())
																	  : true
								  : false;
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return _range.hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Guava Range DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasLowerBound() {
		return _range.hasLowerBound();
	}
	public T lowerEndpoint() {
		return _range.lowerEndpoint();
	}
	public BoundType lowerBoundType() {
		return _range.lowerBoundType();
	}
	public boolean hasUpperBound() {
		return _range.hasUpperBound();
	}
	public T upperEndpoint() {
		return _range.upperEndpoint();
	}
	public BoundType upperBoundType() {
		return _range.upperBoundType();
	}
	public boolean isEmpty() {
		return _range.isEmpty();
	}
	public boolean contains(final T value) {
		return _range.contains(value);
	}
	public boolean containsAll(final Iterable<? extends T> values) {
		return _range.containsAll(values);
	}
	public boolean encloses(final com.google.common.collect.Range<T> otherRange) {
		return _range.encloses(otherRange);
	}
	public boolean encloses(final r01f.types.Range<T> otherRange) {
		return _range.encloses(otherRange.asGuavaRange());
	}
	public boolean isConnected(final com.google.common.collect.Range<T> otherRange) {
		return _range.isConnected(otherRange);
	}
	public boolean isConnected(final r01f.types.Range<T> otherRange) {
		return _range.isConnected(otherRange.asGuavaRange());
	}
	public com.google.common.collect.Range<T> intersection(final com.google.common.collect.Range<T> connectedRange) {
		return _range.intersection(connectedRange);
	}
	public r01f.types.Range<T> intersection(final r01f.types.Range<T> connectedRange) {
		return Range.wrap(_range.intersection(connectedRange.asGuavaRange()));
	}
	public com.google.common.collect.Range<T> span(final com.google.common.collect.Range<T> other) {
		return _range.span(other);
	}
	public r01f.types.Range<T> span(final r01f.types.Range<T> other) {
		return Range.wrap(_range.span(other.asGuavaRange()));
	}
	public com.google.common.collect.Range<T> canonical(final DiscreteDomain<T> domain) {
		return _range.canonical(domain);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Predicate DELEGATED
/////////////////////////////////////////////////////////////////////////////////////////
//	public Predicate<T> and(final Predicate<? super T> otherPred) {
//		return _range.and(otherPred);
//	}
//	public boolean test(final T otherPred) {
//		return _range.test(otherPred);
//	}
//	public Predicate<T> negate() {
//		return _range.negate();
//	}
//	public Predicate<T> or(final Predicate<? super T> otherPred) {
//		return _range.or(otherPred);
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final <T extends Comparable<? super T>> Comparator<Range<T>> createComparatorByLowerBound() {
		return (r1,r2) -> {
					if (r1 == null && r2 == null) return 0;
					if (r1 != null && r2 == null) return -1;
					if (r1 == null && r2 != null) return 1;
					T l1 = r1.getLowerBound();
					T l2 = r2.getLowerBound();
					T u1 = r1.getUpperBound();
					T u2 = r2.getUpperBound();
					if (l1 == null && l2 == null) {
						if (u1 == null && u2 == null) return 0;
						if (u1 != null && u2 == null) return -1;// 2 do not have upper bound
						if (u1 == null && u2 != null) return 1;	// 1 do not have upper bound
						return u1.compareTo(u2);
					}
					if (l1 != null && l2 == null) return -1;	// 2 do not have lower bound
					if (l1 == null && l2 != null) return 1;		// 1 do not have lower bound
					return l1.compareTo(l2);
				};
	}
}
