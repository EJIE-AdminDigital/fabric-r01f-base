package r01f.util.types.ranges;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.Range;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RangesTyped {
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPED RANGES
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static abstract class TypedRangeBase<T extends Comparable<T>> {
		@Getter private final Range<T> _range;

		public TypedRangeBase(final String rangeAsString,final Class<T> type) {
			_range = Range.parse(rangeAsString,type);
		}
		public TypedRangeBase(final Range<T> range) {
			_range = range;
		}
		@Override
		public String toString() {
			return _range.asString();
		}
		@Override
		public boolean equals(final Object obj) {
			return _range.equals(obj);
		}
		@Override
		public int hashCode() {
			return _range.hashCode();
		}
	}
	public static class InstantRange
		        extends TypedRangeBase<Instant> {
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static InstantRange valueOf(final String rangeAsString) {
			return new InstantRange(rangeAsString);
		}
		public InstantRange(final String rangeAsString) {
			super(rangeAsString,Instant.class);
		}
		public InstantRange(final Range<Instant> range) {
			super(range);
		}
	}
	public static class LongRange
				extends TypedRangeBase<Long> {
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static LongRange valueOf(final String rangeAsString) {
			return new LongRange(rangeAsString);
		}
		public LongRange(final String rangeAsString) {
			super(rangeAsString,Long.class);
		}
		public LongRange(final Range<Long> range) {
			super(range);
		}
	}
	public static class IntRange
				extends TypedRangeBase<Integer> {
		/**
		 * This factory method is MANDATORY since {@link Range} is mapped as a @PathParam
		 * at REST jesrouces.
		 * This types of params NEEDS either:
		 * 		<ul>
		 *			<li>Be a primitive type</li>
		 *			<li>Have a constructor that accepts a single String argument</li>
		 *			<li>Have a static method named valueOf or fromString that accepts a single String argument (see, for example, Integer.valueOf(String))</li>
		 *			<li>Have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.</li>
		 *			<li>Be List<T>, Set<T> or SortedSet<T>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.</li>
		 *		</ul>
		 * @param rangeAsString
		 */
		public static IntRange valueOf(final String rangeAsString) {
			return new IntRange(rangeAsString);
		}
		public IntRange(final String rangeAsString) {
			super(rangeAsString,Integer.class);
		}
		public IntRange(final Range<Integer> range) {
			super(range);
		}
	}
}
