package r01f.types;

import java.util.concurrent.TimeUnit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class TimeUnits {
	public static String timeUnitAsString(final TimeUnit unit) {
		return switch(unit) {
					case DAYS: 			yield "d";
					case HOURS:			yield "h";
					case MINUTES:		yield "m";
					case SECONDS:		yield "s";
					case MILLISECONDS: 	yield "ms";	// 10^-3
					case MICROSECONDS:	yield "mus";// 10^-6	// really do not know what is the micro-second abbreviation
					case NANOSECONDS:	yield "ns";	// 10^-9
					default:			throw new IllegalArgumentException(unit + " is NOT a supported time unit!");
			   };
	}
	public static final String TIMEUNIT_PATTERN_STR = "(" +
															"d|day|days" + 
															"|" +
															"h|hour|hours" + 
															"|" +
															"m|min|mins|minute|minutes" + 
															"|" +
															"s|sec|secs|second|seconds" + 
															"|" +
															"ms|milli|millis|millisecond|milliseconds" + 
															"|" +
															"mus|micro|micros|microsecond|microseconds" + 
															"|" +
															"ns|nano|nanos|nanosecond|nanoseconds" + 
													 ")";
	
	public static TimeUnit fromString(final String unitStr) {
		return switch(unitStr) {
					case "d","day","days":						yield TimeUnit.DAYS;
					case "h","hour","hours":					yield TimeUnit.HOURS;
					case "m","min","mins","minute","minutes":	yield TimeUnit.MINUTES;
					case "s","sec","secs","second","seconds":	yield TimeUnit.SECONDS;
					case "ms","milli","millis","millisecond","milliseconds":	yield TimeUnit.MILLISECONDS;
					case "mus","micro","micros","microsecond","micorseconds":	yield TimeUnit.MICROSECONDS;
					case "ns","nano","nanos","nanosecond","nanoseconds":		yield TimeUnit.NANOSECONDS;
					default: throw new IllegalArgumentException(unitStr + " is NOT a TimeUnit abbr!");
			   };
	}
}
