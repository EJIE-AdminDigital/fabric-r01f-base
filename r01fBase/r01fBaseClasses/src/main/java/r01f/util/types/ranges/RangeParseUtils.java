package r01f.util.types.ranges;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.BoundType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.Range;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RangeParseUtils {
	/**
	 * Unsafe parse a range from its textual representation like lowerBound..upperBound
	 * It's unsafe since it can throw a {@link ClassCastException} if the dataType arg is not a {@link Comparable} type
	 * @param rangeStr
	 * @param dataType
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static Range<?> unsafeParse(final String rangeStr,
									   final Class<?> dataType) {
		Class<? extends Comparable> comparableDataType = (Class<? extends Comparable>)dataType;
		return Range.parse(rangeStr,
						   comparableDataType);
	}
	/**
	 * Parses a range from its textual representation like lowerBound..upperBound
	 * @param rangeStr
	 * @param dataType
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T extends Comparable<? super T>> Range<T> parse(final String rangeStr,
							 	 	  					   		   final Class<T> dataType) {
		Object outRange = null;
		// Compilation errors:  Incomparable Types
		// [javac] /softbase_ejie/aplic/r01fb/tmp/compileLib/r01fbClasses/src/r01f/types/Range.java:407:
		// incomparable types: java.lang.Class<T> and java.lang.Class<java.util.Date>
	    // [javac] 		if (dataType == java.util.Date.class || dataType == java.sql.Date.class) {

		// numbers
		Class<?> java_lang_Byte_class = Byte.class;
		Class<?> java_lang_Integer_class = Integer.class;
		Class<?> java_lang_Short_class = Short.class;
		Class<?> java_lang_Long_class = Long.class;
		Class<?> java_lang_Double_class = Double.class;
		Class<?> java_lang_Float_class = Float.class;

		// date-time
		Class<?> java_time_Instant_class = Instant.class;
		Class<?> java_time_LocalDate_class = LocalDate.class;
		Class<?> java_time_LocalDateTime_class = LocalDateTime.class;
		Class<?> java_time_LocalTime_class = LocalTime.class;
		Class<?> java_time_OffsetDateTime_class = OffsetDateTime.class;
		Class<?> java_time_ZonedDateTime_class = ZonedDateTime.class;
		
		Class<?> Year_class = r01f.types.datetime.Year.class;
		Class<?> MonthOfYear_class = r01f.types.datetime.MonthOfYear.class;
		Class<?> HourOfDay_class = r01f.types.datetime.HourOfDay.class;
		Class<?> MinuteOfHour_class = r01f.types.datetime.MinuteOfHour.class;
		Class<?> SecondOfMinute_class = r01f.types.datetime.SecondOfMinute.class;

		RangeDef bounds = RangeParseUtils.parseBounds(rangeStr);
		// numbers
		if (dataType == java_lang_Byte_class) {
			outRange = RangeParseUtilsForNumbers.createByteRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
									   											 bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Integer_class) {
			outRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
									  						   						bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Short_class) {
			outRange = RangeParseUtilsForNumbers.createShortRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
																 				  bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Long_class) {
			outRange = RangeParseUtilsForNumbers.createLongRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
									   											 bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType ==java_lang_Double_class) {
			outRange = RangeParseUtilsForNumbers.createDoubleRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
										 						  				   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_lang_Float_class) {
			outRange = RangeParseUtilsForNumbers.createFloatRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
																 				  bounds.getUpperBound(),bounds.getUpperBoundType());
		// date-time
		} else if (dataType == java.util.Date.class
				|| dataType == java.sql.Date.class) {
			outRange = RangeParseUtilsForJavaTime.createDateRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
																 				  bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_Instant_class) {
			outRange = RangeParseUtilsForJavaTime.createInstantRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
																					 bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalDate_class) {
			outRange = RangeParseUtilsForJavaTime.createLocalDateRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  		  				   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalDateTime_class) {
			outRange = RangeParseUtilsForJavaTime.createLocalDateTimeRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  			  	   bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_LocalTime_class) {
			outRange = RangeParseUtilsForJavaTime.createLocalTimeRangeFromISO8601AsStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  		  				   			bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_OffsetDateTime_class) {
			outRange = RangeParseUtilsForJavaTime.createOffsetDateTimeRangeFromISO8601AsStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	   		   						      bounds.getUpperBound(),bounds.getUpperBoundType());
		} else if (dataType == java_time_ZonedDateTime_class) {
			outRange = RangeParseUtilsForJavaTime.createZonedDateTimeRangeFromISO8601AsStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  							bounds.getUpperBound(),bounds.getUpperBoundType());
			
		// r01f date-time
		} else if (dataType == Year_class) {
			Range<Integer> iRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  						 bounds.getUpperBound(),bounds.getUpperBoundType());
			outRange = iRange.map(Year::of);
		} else if (dataType == MonthOfYear_class) {
			Range<Integer> iRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  						 bounds.getUpperBound(),bounds.getUpperBoundType());
			outRange = iRange.map(MonthOfYear::of);
		} else if (dataType == HourOfDay_class) {
			Range<Integer> iRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  						 bounds.getUpperBound(),bounds.getUpperBoundType());
			outRange = iRange.map(HourOfDay::of);
		} else if (dataType == MinuteOfHour_class) {
			Range<Integer> iRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  						 bounds.getUpperBound(),bounds.getUpperBoundType());
			outRange = iRange.map(MinuteOfHour::of);
		} else if (dataType == SecondOfMinute_class) {
			Range<Integer> iRange = RangeParseUtilsForNumbers.createIntegerRangeFromStringBounds(bounds.getLowerBound(),bounds.getLowerBoundType(),
															  	  		  						 bounds.getUpperBound(),bounds.getUpperBoundType());
			outRange = iRange.map(r01f.types.datetime.SecondOfMinute::of);
		} else {
			throw new IllegalArgumentException("Type " + dataType + " is NOT supported in Range");
		}
		return (Range<T>)outRange;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern RANGE_PATTERN = Pattern.compile("(?:\\(|\\[)(.+)?\\.\\.(.+)?(?:\\]|\\))");	// (?:\(|\[).+\.\..+(?:\]|\))
																											// Samples:
																											// 		(XX..YY)
																											//		..YY]
																											//		[XX..YY)
	public static RangeDef parseBounds(final String rangeStr) {
		RangeDef outBounds = null;
		Matcher m = RANGE_PATTERN.matcher(rangeStr);
		if (m.matches()) {
			outBounds = new RangeDef();
			if (m.groupCount() == 2) {
				if (rangeStr.startsWith("(") || rangeStr.startsWith("..")) {
					outBounds.setLowerBoundType(BoundType.OPEN);
				} else if (rangeStr.startsWith("[")) {
					outBounds.setLowerBoundType(BoundType.CLOSED);
				} else {
					throw new IllegalArgumentException(rangeStr + " is NOT a valid range string representation!");
				}
				if (rangeStr.endsWith(")") || rangeStr.endsWith("..")) {
					outBounds.setUpperBoundType(BoundType.OPEN);
				} else if (rangeStr.endsWith("]")) {
					outBounds.setUpperBoundType(BoundType.CLOSED);
				} else {
					throw new IllegalArgumentException(rangeStr + " is NOT a valid range string representation!");
				}
				outBounds.setLowerBound(m.group(1));
				outBounds.setUpperBound(m.group(2));
			} else if (rangeStr.startsWith("(..") || rangeStr.startsWith("..")) {
				outBounds.setLowerBoundType(BoundType.OPEN);
				outBounds.setUpperBound(m.group(1));
			} else if (rangeStr.startsWith("[..")) {
				outBounds.setLowerBoundType(BoundType.CLOSED);
				outBounds.setUpperBound(m.group(1));
			} else if (rangeStr.endsWith("..)") || rangeStr.endsWith("..")) {
				outBounds.setUpperBoundType(BoundType.OPEN);
				outBounds.setLowerBound(m.group(1));
			} else if (rangeStr.endsWith("..]")) {
				outBounds.setUpperBoundType(BoundType.CLOSED);
				outBounds.setLowerBound(m.group(1));
			} else {
				throw new IllegalArgumentException(rangeStr + " is NOT a valid range!");
			}
		} else {
			throw new IllegalArgumentException("The range string representation: " + rangeStr + " does NOT match the pattern " + RANGE_PATTERN.pattern());
		}
		return outBounds;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	static class RangeDef {
		@Getter @Setter private String _lowerBound;
		@Getter @Setter private BoundType _lowerBoundType;
		@Getter @Setter private String _upperBound;
		@Getter @Setter private BoundType _upperBoundType;
	}
}
