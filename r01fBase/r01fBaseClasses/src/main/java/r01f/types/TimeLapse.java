package r01f.types;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

/**
 * Represents some time interval o lapse
 * Usage:
 * <pre class='brush:java'>
 * 		TimeLapse timeLapse = TimeLapse.createFor("5s");
 * 		long milis = timeLapse.get();
 * </pre>
 */
@MarshallType(as="timeLapse")
public record TimeLapse(long lapse,TimeUnit unit)
   implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 8201041020863160970L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Pattern TIMELAPSE_PATTERN = Pattern.compile("\\s*([0-9]+)\\s*" +
																    TimeUnits.TIMEUNIT_PATTERN_STR + "?" +	// (d|h|m|s|ms|mus|ns)
																    "\\s*");
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public TimeLapse(final long lapse,final TimeUnit unit) {
		this.lapse = lapse;
		this.unit = unit != null ? unit : TimeUnit.MILLISECONDS;
	}
	public TimeLapse(final long millis) {
		this(millis,TimeUnit.MILLISECONDS);
	}
	public TimeLapse(final String timeLapseSpec) {
		this(_lapseFromTimeLapseAsString(timeLapseSpec),_timeUnitFromTimeLapseAsString(timeLapseSpec));
	}
	public TimeLapse(final Duration duration) {
		this(duration.toMillis(),TimeUnit.MILLISECONDS);
	}
	private static long _lapseFromTimeLapseAsString(final String timeLapseSpec) {
		long theLapse = -1;
		Matcher m = TIMELAPSE_PATTERN.matcher(timeLapseSpec);
		if (m.matches()) {
			theLapse = Long.parseLong(m.group(1));
		} else if (Numbers.isLong(timeLapseSpec)) {
			theLapse = Long.parseLong(timeLapseSpec);
		}
		return theLapse;
	}
	private static TimeUnit _timeUnitFromTimeLapseAsString(final String timeLapseSpec) {
		TimeUnit theUnit = TimeUnit.MILLISECONDS;
		Matcher m = TIMELAPSE_PATTERN.matcher(timeLapseSpec);
		if (m.matches()) {
			String unitStr = m.groupCount() == 2 ? m.group(2) : null;
			theUnit = Strings.isNullOrEmpty(unitStr)
							? TimeUnit.MILLISECONDS
							: TimeUnits.fromString(unitStr);
		} else {
			throw new IllegalArgumentException("The time lapse spec " + timeLapseSpec + " is NOT valid! It should match the pattern " + TIMELAPSE_PATTERN.pattern());
		}
		return theUnit;
	}

	public static TimeLapse valueOf(final String timeSpec) {
		return new TimeLapse(timeSpec);
	}
	/**
	 * Creates a {@link TimeLapse} from millis
	 * @param millis 
	 * @return
	 */
	public static TimeLapse createForMillis(final long millis) {
		return new TimeLapse(millis);
	}
	/**
	 * Creates a {@link TimeLapse} from units
	 * @param units
	 * @param unit
	 * @return
	 */
	public static TimeLapse createFor(final long lapse,final TimeUnit unit) {
		return new TimeLapse(lapse,unit);
	}
	/**
	 * Creates a {@link TimeLapse} from a textual spec like some of the following
	 * <ul>
	 * 		<li>1d for one day</li>
	 * 		<li>1h for one hour</li>
	 * 		<li>30m for 30 minutes</li>
	 * 		<li>100s for 100 seconds</li>
	 * </ul>
	 * @param timeSpec the spec
	 * @return
	 */
	public static TimeLapse createFor(final String timeLapseSpec) {
		return new TimeLapse(timeLapseSpec);
	}
	/**
	 * Creates a {@link TimeLapse} from a textual spec like some of the following
	 * <ul>
	 * 		<li>1d for one day</li>
	 * 		<li>1h for one hour</li>
	 * 		<li>30m for 30 minutes</li>
	 * 		<li>100s for 100 seconds</li>
	 * </ul>
	 * @param timeLapseSpec the spec
	 * @return
	 */
	public static TimeLapse of(final String timeLapseSpec) {
		return TimeLapse.createFor(timeLapseSpec);
	}
	@Deprecated
	public static TimeLapse of(final long millis) {
		return TimeLapse.ofMillis(millis);
	}
	public static TimeLapse ofMillis(final long millis) {
		return new TimeLapse(millis);
	}
	public static TimeLapse ofSeconds(final long secons) {
		return TimeLapse.of(secons,TimeUnit.SECONDS);
	}
	public static TimeLapse ofMinutes(final long minutes) {
		return TimeLapse.of(minutes,TimeUnit.MINUTES);
	}
	public static TimeLapse of(final long lapse,final TimeUnit unit) {
		return new TimeLapse(lapse,unit);
	}
	public static TimeLapse of(final Duration duration) {
		return new TimeLapse(duration);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public long getLapse() {
		return this.lapse();
	}
	public TimeUnit getUnit() {
		return this.unit();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	public long asMillis() {
		return this.unit.toMillis(this.lapse);
	}
	public long asSeconds() {
		return this.unit.toSeconds(this.lapse);
	}
	public long asMinutes() {
		return this.unit.toMinutes(this.lapse);
	}
	public Duration asDuration() {
		return Duration.ofMillis(this.asMillis());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return _toString(this.lapse(),this.unit());
	}
	@Override
	public String toString() {
		return this.asString();
	}
	private static String _toString(final long lapse,final TimeUnit unit) {
		return Strings.customized("{}{}",
							 	  lapse,TimeUnits.timeUnitAsString(unit));
	}
	public String asMillisString() {
		return _toString(this.asMillis(),TimeUnit.MILLISECONDS);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASH CODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof TimeLapse other)) return false;
		
		return this.asMillis() == other.asMillis();
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.asMillis());
	}
}
