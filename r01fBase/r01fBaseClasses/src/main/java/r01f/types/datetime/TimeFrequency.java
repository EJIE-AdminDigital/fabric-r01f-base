package r01f.types.datetime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum TimeFrequency
 implements EnumExtended<TimeFrequency> {
	DAILY(1),
	WEEKLY(7),
	MONTHLY(30),
	QUARTERLY(90),
	YEARLY(365);
	
	@Getter private final int _numberOfDays;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final TimeFrequency fromName(final String name) {
		return EnumExtended.fromName(name,TimeFrequency.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + TimeFrequency.class + " element with name=" + name));
	}
	public static final TimeFrequency fromNumberOfDays(final int num) {
		TimeFrequency outTF = null;
		for (TimeFrequency tf : TimeFrequency.values()) {
			if (tf.getNumberOfDays() == num) {
				outTF = tf;
				break;
			}
		}
		if (outTF == null) throw new IllegalArgumentException("The number of days " + num + " does NOT match any of the time frequencies: " + CollectionUtils.toStringCommaSeparated(TimeFrequency.values()));
		return outTF;
	}
}
