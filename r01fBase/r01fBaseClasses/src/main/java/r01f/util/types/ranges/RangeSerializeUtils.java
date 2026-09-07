package r01f.util.types.ranges;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.google.common.collect.BoundType;

import lombok.NoArgsConstructor;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.SecondOfMinute;
import r01f.types.datetime.Year;
import r01f.util.types.Strings;
import r01f.util.types.datetime.DateTimeFormatUtils;
import r01f.util.types.ranges.RangeParseUtils.RangeDef;

@NoArgsConstructor(access= lombok.AccessLevel.PRIVATE)
public abstract class RangeSerializeUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T extends Comparable<? super T>> String rangeAsString(final Range<T> range,
																		 final Class<T> dataType) {
		RangeDef rangeDef = null;

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
		Class<?> java_util_Date_class = Date.class;
		Class<?> java_sql_Date_class = java.sql.Date.class;
		Class<?> java_time_Instant_class = Instant.class;
		Class<?> java_time_LocalDate_class = LocalDate.class;
		Class<?> java_time_LocalDateTime_class = LocalDateTime.class;
		Class<?> java_time_LocalTime_class = LocalTime.class;
		Class<?> java_time_ZonedDateTime_class = ZonedDateTime.class;
		Class<?> java_time_OffsetDateTime_class = OffsetDateTime.class;
		
		Class<?> Year_class = Year.class;
		Class<?> MonthOfYear_class = MonthOfYear.class;
		Class<?> HourOfDay_class = r01f.types.datetime.HourOfDay.class;
		Class<?> MinuteOfHour_class = r01f.types.datetime.MinuteOfHour.class;
		Class<?> SecondOfMinute_class = r01f.types.datetime.SecondOfMinute.class;
		

		// date-time
		if (dataType == java_util_Date_class || dataType == java_sql_Date_class) {
			rangeDef = _toInstantBoundStrings(range.map(Date.class::cast)
											   	   .map(Date::toInstant));
		} else if (dataType == java_time_Instant_class) {
			rangeDef = _toInstantBoundStrings(range.map(Instant.class::cast));
		} else if (dataType == java_time_LocalDate_class) {
			rangeDef = _toLocalDateBoundStrings(range.map(LocalDate.class::cast));
		} else if (dataType == java_time_LocalDateTime_class) {
			rangeDef = _toLocalDateTimeBoundStrings(range.map(LocalDateTime.class::cast));
		} else if (dataType == java_time_LocalTime_class) {
			rangeDef = _toLocalTimeBoundStrings(range.map(LocalTime.class::cast));
		} else if (dataType == java_time_ZonedDateTime_class) {
			rangeDef = _toZonedDateTimeBoundStrings(range.map(ZonedDateTime.class::cast));
		} else if (dataType == java_time_OffsetDateTime_class) {
			rangeDef = _toOffsetDateTimeBoundStrings(range.map(OffsetDateTime.class::cast));
		// date time components
		} else if (dataType == Year_class) {
			rangeDef = _toYearBoundStrings(range.map(Year.class::cast));
		} else if (dataType == MonthOfYear_class) {
			rangeDef = _toMonthOfYearBoundStrings(range.map(MonthOfYear.class::cast));
		} else if (dataType == HourOfDay_class) {
			rangeDef = _toHourOfDayBoundStrings(range.map(HourOfDay.class::cast));
		} else if (dataType == MinuteOfHour_class) {
			rangeDef = _toMinuteOfHourBoundStrings(range.map(MinuteOfHour.class::cast));
		} else if (dataType == SecondOfMinute_class) {
			rangeDef = _toSecondOfMinuteBoundStrings(range.map(SecondOfMinute.class::cast));
		// numbers
		} else if (dataType == java_lang_Byte_class) {
			rangeDef = _toByteBoundStrings(range.map(Byte.class::cast));
		} else if (dataType == java_lang_Integer_class) {
			rangeDef = _toIntegerBoundStrings(range.map(Integer.class::cast));
		} else if (dataType == java_lang_Short_class) {
			rangeDef = _toShortBoundStrings(range.map(Short.class::cast));
		} else if (dataType == java_lang_Long_class ) {
			rangeDef = _toLongBoundStrings(range.map(Long.class::cast));
		} else if (dataType == java_lang_Double_class ) {
			rangeDef = _toDoubleBoundStrings(range.map(Double.class::cast));
		} else if (dataType == java_lang_Float_class) {
			rangeDef = _toFloatBoundStrings(range.map(Float.class::cast));
		} else {
			throw new IllegalArgumentException("Type " + dataType + " is NOT supported in Range");
		}
		String outStr = Strings.customized("{}{}..{}{}",
							   			   (rangeDef.getLowerBoundType() == BoundType.CLOSED ? "[" : "("),(rangeDef.getLowerBound() != null ? rangeDef.getLowerBound() : ""),
							   			   (rangeDef.getUpperBound() != null ? rangeDef.getUpperBound() : ""),(rangeDef.getUpperBoundType() == BoundType.CLOSED ? "]" : ")"));
		return outStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NUMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	private static RangeDef _toByteBoundStrings(final Range<Byte> range) {
		String lower = range.getLowerBound() != null ? Byte.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Byte.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toIntegerBoundStrings(final Range<Integer> range) {
		String lower = range.getLowerBound() != null ? Integer.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Integer.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toShortBoundStrings(final Range<Short> range) {
		String lower = range.getLowerBound() != null ? Short.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Short.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLongBoundStrings(final Range<Long> range) {
		String lower = range.getLowerBound() != null ? Long.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Long.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toDoubleBoundStrings(final Range<Double> range) {
		String lower = range.getLowerBound() != null ? Double.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Double.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toFloatBoundStrings(final Range<Float> range) {
		String lower = range.getLowerBound() != null ? Float.toString(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? Float.toString(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE-TIME
/////////////////////////////////////////////////////////////////////////////////////////
	private static RangeDef _toInstantBoundStrings(final Range<Instant> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLocalDateBoundStrings(final Range<LocalDate> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLocalDateTimeBoundStrings(final Range<LocalDateTime> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toLocalTimeBoundStrings(final Range<LocalTime> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toOffsetDateTimeBoundStrings(final Range<OffsetDateTime> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toZonedDateTimeBoundStrings(final Range<ZonedDateTime> range) {
		String lower = range.getLowerBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getLowerBound()) : null;
		String upper = range.getUpperBound() != null ? DateTimeFormatUtils.formatAsISO8601(range.getUpperBound()) : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE-COMPONENTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static RangeDef _toYearBoundStrings(final Range<Year> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toMonthOfYearBoundStrings(final Range<MonthOfYear> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toDayOfMonthBoundStrings(final Range<DayOfMonth> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toHourOfDayBoundStrings(final Range<HourOfDay> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toMinuteOfHourBoundStrings(final Range<MinuteOfHour> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
	private static RangeDef _toSecondOfMinuteBoundStrings(final Range<SecondOfMinute> range) {
		String lower = range.getLowerBound() != null ? range.getLowerBound().asString() : null;
		String upper = range.getUpperBound() != null ? range.getUpperBound().asString() : null;
		return new RangeDef(lower,range.getLowerBoundType(),
							upper,range.getUpperBoundType());
	}
}
