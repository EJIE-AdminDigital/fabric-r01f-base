package r01f.util.types.ranges;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.collect.BoundType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.datetime.DateTimeFormatUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class RangeFormatUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date RANGE FORMATTING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the Date ranges formated at UTC
	 * @param dateRanges
	 * @param dateFormat
	 * @param locale
	 * @return
	 */
	public static String formatDateRanges(final Collection<Range<Date>> dateRanges,
										  final String dateFormat,final Locale locale) {
		return RangeFormatUtils.formatAtUTCInstantRanges(dateRanges.stream()
																   .map(dateRange -> dateRange.map(date -> date.toInstant()))
																   .collect(Collectors.toList()),
														 dateFormat,locale);
	}
	/**
	 * Gets the Date range formated at UTC
	 * @param dateRange
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String formatDateRange(final Range<Date> dateRange,
										 final String pattern,final Locale locale) {
		return RangeFormatUtils.formatAtUTCInstantRange(dateRange.map(date -> date.toInstant()),
												   	    pattern,locale);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Instant RANGE FORMATTING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * @param dateRanges
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCInstantRanges(final Collection<Range<Instant>> dateRanges,
											 	  final String pattern,final Locale locale) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Range<Instant>> rIt = dateRanges.iterator(); rIt.hasNext(); ) {
			Range<Instant> r = rIt.next();
			sb.append(RangeFormatUtils.formatAtUTCInstantRange(r,
														  	   pattern,locale));
			if (rIt.hasNext()) sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * Gets the Instant range formated at UTC
	 * @param instantRange
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCInstantRange(final Range<Instant> instantRange,
												 final String pattern,final Locale locale) {
		return Strings.customized("{}{}..{}{}",
								  instantRange.lowerBoundType() == BoundType.CLOSED ? "[" : "(",
								  instantRange.lowerEndpoint() != null ? DateTimeFormatUtils.format(instantRange.lowerEndpoint(),
										  																 pattern,locale)
										  							   : "",
								  instantRange.upperEndpoint() != null ? DateTimeFormatUtils.format(instantRange.getUpperBound(),
										  																 pattern,locale)
										  							   : "",
								  instantRange.upperBoundType() == BoundType.CLOSED ? "]" : ")");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LocalDate RANGE FORMATTING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the LocalDate ranges formated at UTC
	 * @param dateRanges
	 * @param zoneId the zone of the dates at the range
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCLocalDateRanges(final Collection<Range<LocalDate>> dateRanges,final ZoneId zoneId,
											   		final String pattern,final Locale locale) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Range<LocalDate>> rIt = dateRanges.iterator(); rIt.hasNext(); ) {
			Range<LocalDate> r = rIt.next();
			sb.append(RangeFormatUtils.formatAtUTCLocalDateRange(r,zoneId,
																 pattern,locale));
			if (rIt.hasNext()) sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * Gets the LocalDate range formated at UTC
	 * @param dateRange
	 * @param zoneId the zone of the dates at the range
	 * @param dateFormat
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCLocalDateRange(final Range<LocalDate> dateRange,final ZoneId zoneId,
											  	   final String dateFormat,final Locale locale) {
		return Strings.customized("{}{}..{}{}",
								  dateRange.lowerBoundType() == BoundType.CLOSED ? "[" : "(",
								  dateRange.lowerEndpoint() != null ? DateTimeFormatUtils.format(dateRange.lowerEndpoint()
										  																	   .atStartOfDay(zoneId)
										  																	   .toInstant(),
										  															  dateFormat,locale)
										  							: "",
								  dateRange.upperEndpoint() != null ? DateTimeFormatUtils.format(dateRange.getUpperBound()
										  																	   .atStartOfDay(zoneId)
										  																	   .toInstant(),
										  															  dateFormat,locale)
										  							: "",
								  dateRange.upperBoundType() == BoundType.CLOSED ? "]" : ")");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LocalDateTime RANGE FORMATTING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the LocalDateTime ranges formated at UTC
	 * @param dateRanges
	 * @param zoneId the zone of the dates at the range
	 * @param pattern
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCLocalDateTimeRanges(final Collection<Range<LocalDateTime>> dateRanges,final ZoneId zoneId,
												   		final String pattern,final Locale locale) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Range<LocalDateTime>> rIt = dateRanges.iterator(); rIt.hasNext(); ) {
			Range<LocalDateTime> r = rIt.next();
			sb.append(RangeFormatUtils.formatAtUTCLocalDateTimeRange(r,zoneId,
																pattern,locale));
			if (rIt.hasNext()) sb.append(",");
		}
		return sb.toString();
	}
	/**
	 * Gets the LocalDateTime range formated at UTC
	 * @param dateRange
	 * @param zoneId
	 * @param dateFormat
	 * @param locale
	 * @return
	 */
	public static String formatAtUTCLocalDateTimeRange(final Range<LocalDateTime> dateRange,final ZoneId zoneId,
												  	   final String dateFormat,final Locale locale) {
		return Strings.customized("{}{}..{}{}",
								  dateRange.lowerBoundType() == BoundType.CLOSED ? "[" : "(",
								  dateRange.lowerEndpoint() != null ? DateTimeFormatUtils.format(dateRange.lowerEndpoint()
										  																	   .atZone(zoneId)
										  																	   .toInstant(),
										  															  dateFormat,locale)
										  							: "",
								  dateRange.upperEndpoint() != null ? DateTimeFormatUtils.format(dateRange.getUpperBound()
										  																	   .atZone(zoneId)
										  																	   .toInstant(),
										  															  dateFormat,locale)
										  							: "",
								  dateRange.upperBoundType() == BoundType.CLOSED ? "]" : ")");
	}
}
