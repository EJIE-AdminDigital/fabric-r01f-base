package r01f.util.types.ranges;

import com.google.common.collect.BoundType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.Range;

/**
 * Class to encapsulate Number parsing Range class.
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class RangeParseUtilsForNumbers {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Range<Byte> createByteRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  final String upperBound,final BoundType upperBoundType) {
		Byte lowerBoundByte = lowerBound != null ? Byte.valueOf(lowerBound) : null;
		Byte upperBoundByte = upperBound != null ? Byte.valueOf(upperBound) : null;
		return new Range<>(lowerBoundByte,lowerBoundType,
						   upperBoundByte,upperBoundType);
	}
	public static Range<Integer> createIntegerRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											   						final String upperBound,final BoundType upperBoundType) {
		Integer lowerBoundInt = lowerBound != null ? Integer.valueOf(lowerBound) : null;
		Integer upperBoundInt = upperBound != null ? Integer.valueOf(upperBound) : null;
		return new Range<>(lowerBoundInt,lowerBoundType,
						   upperBoundInt,upperBoundType);
	}
	public static Range<Short> createShortRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											   					final String upperBound,final BoundType upperBoundType) {
		Short lowerBoundShort = lowerBound != null ? Short.valueOf(lowerBound) : null;
		Short upperBoundShort = upperBound != null ? Short.valueOf(upperBound) : null;
		return new Range<>(lowerBoundShort,lowerBoundType,
							    upperBoundShort,upperBoundType);
	}
	public static Range<Long> createLongRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  final String upperBound,final BoundType upperBoundType) {
		Long lowerBoundLong = lowerBound != null ? Long.valueOf(lowerBound) : null;
		Long upperBoundLong = upperBound != null ? Long.valueOf(upperBound) : null;
		return new Range<>(lowerBoundLong,lowerBoundType,
						   upperBoundLong,upperBoundType);
	}
	public static Range<Double> createDoubleRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											   	 				  final String upperBound,final BoundType upperBoundType) {
		Double lowerBoundDouble = lowerBound != null ? Double.valueOf(lowerBound) : null;
		Double upperBoundDouble = upperBound != null ? Double.valueOf(upperBound) : null;
		return new Range<>(lowerBoundDouble,lowerBoundType,
						   upperBoundDouble,upperBoundType);
	}
	public static Range<Float> createFloatRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											   					final String upperBound,final BoundType upperBoundType) {
		Float lowerBoundFloat = lowerBound != null ? Float.valueOf(lowerBound) : null;
		Float upperBoundFloat = upperBound != null ? Float.valueOf(upperBound) : null;
		return new Range<>(lowerBoundFloat,lowerBoundType,
						   upperBoundFloat,upperBoundType);
	}
}
