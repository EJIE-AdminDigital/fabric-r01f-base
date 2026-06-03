package r01f.util.types.ranges;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.google.common.collect.BoundType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.Range;
import r01f.util.types.Numbers;
import r01f.util.types.StringConverter;
import r01f.util.types.Strings;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class RangeParseUtilsForJavaTime {
/////////////////////////////////////////////////////////////////////////////////////////
//	PARSE
/////////////////////////////////////////////////////////////////////////////////////////
//////////////////// Date & SQL Date
	public static Range<Date> createDateRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  final String upperBound,final BoundType upperBoundType) {
		Range<Instant> iRange = RangeParseUtilsForJavaTime.createInstantRangeFromStringBounds(lowerBound,lowerBoundType,
																						      upperBound,upperBoundType);
		return iRange.map(Date::from);
	}
	public static Range<Date> createDateRangeFromEPOCHMillisAsStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 							   final String upperBound,final BoundType upperBoundType) {
		Range<Instant> iRange = RangeParseUtilsForJavaTime.createInstantRangeFromEPOCHMillisAsStringBounds(lowerBound,lowerBoundType,
																										   upperBound,upperBoundType);
		return iRange.map(Date::from);
	}
//////////////////// Instant
	public static Range<Instant> createInstantRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  		final String upperBound,final BoundType upperBoundType) {
		Instant lbound = null;
		Instant ubound = null;

		// [1] - Check if the bounds are EPOCH Millis
		if (Strings.isNOTNullOrEmpty(lowerBound) && Numbers.isLong(lowerBound)) {
			if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			lbound = Instant.ofEpochMilli(Long.parseLong(lowerBound));
		}
		if (Strings.isNOTNullOrEmpty(upperBound) && Numbers.isLong(upperBound)) {
			if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			ubound = Instant.ofEpochMilli(Long.parseLong(upperBound));
		}
		// [2] - Check if the bounds are ISO8601
		if (lbound == null && ubound == null) {
			lbound = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asInstantFromISO8601(lowerBound,null) : null;
			ubound = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asInstantFromISO8601(upperBound,null) : null;
		}
		// [3] - Return range
		return new Range<>(lbound,lowerBoundType,
						   ubound,upperBoundType);
	}
	public static Range<Instant> createInstantRangeFromEPOCHMillisAsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  		 final String upperBound,final BoundType upperBoundType) {
		if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound);
		if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: upperBound=" + upperBound);

		long lbound = Strings.isNOTNullOrEmpty(lowerBound) ? Long.parseLong(lowerBound) : null;
		long ubound = Strings.isNOTNullOrEmpty(upperBound) ? Long.parseLong(upperBound) : null;

		Instant lowerInstant = Instant.ofEpochMilli(lbound);
		Instant upperInstant = Instant.ofEpochMilli(ubound);
		return new Range<>(lowerInstant,lowerBoundType,
						   upperInstant,upperBoundType);
	}
	public static Range<Instant> createInstantRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 final String upperBound,final BoundType upperBoundType) {
		Instant lowerInstant = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asInstantFromISO8601(lowerBound,null) : null;
		Instant upperInstant = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asInstantFromISO8601(upperBound,null) : null;
		return new Range<>(lowerInstant,lowerBoundType,
						   upperInstant,upperBoundType);
	}
//////////////////// LocalDate
	public static Range<LocalDate> createLocalDateRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  			final String upperBound,final BoundType upperBoundType) {
		LocalDate lbound = null;
		LocalDate ubound = null;

		// [1] - Check if the bounds are EPOCH Millis
		if (Strings.isNOTNullOrEmpty(lowerBound) && Numbers.isLong(lowerBound)) {
			if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			lbound = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(lowerBound)),
										 ZoneId.of("UTC"));
		}
		if (Strings.isNOTNullOrEmpty(upperBound) && Numbers.isLong(upperBound)) {
			if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			ubound = LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(upperBound)),
										 ZoneId.of("UTC"));
		}
		// [2] - Check if the bounds are ISO8601
		if (lbound == null && ubound == null) {
			lbound = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asLocalDateFromISO8601(lowerBound,null) : null;
			ubound = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asLocalDateFromISO8601(upperBound,null) : null;
		}
		// [3] - Return range
		return new Range<>(lbound,lowerBoundType,
						   ubound,upperBoundType);
	}
	public static Range<LocalDate> createLocalDateRangeFromEPOCHMillisAsStringBounds(final String lowerBound,final BoundType lowerBoundType,
													   						  		 final String upperBound,final BoundType upperBoundType) {
		if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound);
		if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: upperBound=" + upperBound);

		long lbound = Strings.isNOTNullOrEmpty(lowerBound) ? Long.parseLong(lowerBound) : null;
		long ubound = Strings.isNOTNullOrEmpty(upperBound) ? Long.parseLong(upperBound) : null;

		LocalDate lDate = LocalDate.ofInstant(Instant.ofEpochMilli(lbound),
											  ZoneId.of("UTC"));	// epoch millis are ALWAYS UTC
		LocalDate uDate = LocalDate.ofInstant(Instant.ofEpochMilli(ubound),
											  ZoneId.of("UTC"));	// epoch millis are ALWAYS UTC
		return new Range<>(lDate,lowerBoundType,
						   uDate,upperBoundType);
	}
	public static Range<LocalDate> createLocalDateRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 	 final String upperBound,final BoundType upperBoundType) {
		LocalDate lowerInstant = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asLocalDateFromISO8601(lowerBound,null) : null;
		LocalDate upperInstant = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asLocalDateFromISO8601(upperBound,null) : null;
		return new Range<>(lowerInstant,lowerBoundType,
						   upperInstant,upperBoundType);
	}
//////////////////// LocalDateTime
	public static Range<LocalDateTime> createLocalDateTimeRangeFromStringBounds(final String lowerBound,final BoundType lowerBoundType,
											 				  					final String upperBound,final BoundType upperBoundType) {
		LocalDateTime lbound = null;
		LocalDateTime ubound = null;

		// [1] - Check if the bounds are EPOCH Millis
		if (Strings.isNOTNullOrEmpty(lowerBound) && Numbers.isLong(lowerBound)) {
			if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			lbound = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(lowerBound)),
										 	 ZoneId.of("UTC"));
		}
		if (Strings.isNOTNullOrEmpty(upperBound) && Numbers.isLong(upperBound)) {
			if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound + " upperBound=" + upperBound);
			ubound = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(upperBound)),
										 	 ZoneId.of("UTC"));
		}
		// [2] - Check if the bounds are ISO8601
		if (lbound == null && ubound == null) {
			lbound = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asLocalDateTimeFromISO8601(lowerBound,null) : null;
			ubound = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asLocalDateTimeFromISO8601(upperBound,null) : null;
		}
		// [3] - Return range
		return new Range<>(lbound,lowerBoundType,
						   ubound,upperBoundType);
	}
	public static Range<LocalDateTime> createLocalDateTimeRangeFromEPOCHMillisAsStringBounds(final String lowerBound,final BoundType lowerBoundType,
															   						  		 final String upperBound,final BoundType upperBoundType) {
		if (Strings.isNOTNullOrEmpty(lowerBound) && !Numbers.isLong(lowerBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: lowerBound=" + lowerBound);
		if (Strings.isNOTNullOrEmpty(upperBound) && !Numbers.isLong(upperBound)) throw new IllegalArgumentException("Cannot parse EPOCH millis range: upperBound=" + upperBound);

		long lbound = Strings.isNOTNullOrEmpty(lowerBound) ? Long.parseLong(lowerBound) : null;
		long ubound = Strings.isNOTNullOrEmpty(upperBound) ? Long.parseLong(upperBound) : null;

		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(lbound),
													ZoneId.of("UTC"));	// epoch millis are ALWAYS UTC
		LocalDateTime udt = LocalDateTime.ofInstant(Instant.ofEpochMilli(ubound),
													ZoneId.of("UTC"));	// epoch millis are ALWAYS UTC
		return new Range<>(ldt,lowerBoundType,
						   udt,upperBoundType);
	}
	public static Range<LocalDateTime> createLocalDateTimeRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 	 		 final String upperBound,final BoundType upperBoundType) {
		LocalDateTime ldt = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asLocalDateTimeFromISO8601(lowerBound,null) : null;
		LocalDateTime udt = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asLocalDateTimeFromISO8601(upperBound,null) : null;
		return new Range<>(ldt,lowerBoundType,
						   udt,upperBoundType);
	}
//////////////////// LocalTime
	public static Range<LocalTime> createLocalTimeRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 	 final String upperBound,final BoundType upperBoundType) {
		LocalTime lTime = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asLocalTimeFromISO8601(lowerBound,null) : null;
		LocalTime uTime = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asLocalTimeFromISO8601(upperBound,null) : null;
		return new Range<>(lTime,lowerBoundType,
						   uTime,upperBoundType);
	}
//////////////////// OffsetDateTime
	public static Range<OffsetDateTime> createOffsetDateTimeRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 	 		   final String upperBound,final BoundType upperBoundType) {
		OffsetDateTime lodt = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asOffsetDateTimeFromISO8601(lowerBound,null) : null;
		OffsetDateTime uodt = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asOffsetDateTimeFromISO8601(upperBound,null) : null;
		return new Range<>(lodt,lowerBoundType,
						   uodt,upperBoundType);
	}
//////////////////// ZonedDateTime
	public static Range<ZonedDateTime> createZonedDateTimeRangeFromISO8601AsStringBounds(final String lowerBound,final BoundType lowerBoundType,
												   						  	 	 		 final String upperBound,final BoundType upperBoundType) {
		ZonedDateTime lodt = Strings.isNOTNullOrEmpty(lowerBound) ? StringConverter.asZonedDateTimeFromISO8601(lowerBound,null) : null;
		ZonedDateTime uodt = Strings.isNOTNullOrEmpty(upperBound) ? StringConverter.asZonedDateTimeFromISO8601(upperBound,null) : null;
		return new Range<>(lodt,lowerBoundType,
						   uodt,upperBoundType);
	}
}
