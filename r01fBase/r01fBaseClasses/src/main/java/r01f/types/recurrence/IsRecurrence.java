package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.types.datetime.TimeFrequency;

@MarshallPolymorphicTypeInfo
public interface IsRecurrence {
	public IsRecurrenceFrequency frequency();
	
	public default IsRecurrenceFrequency getFrequency() {
		return this.frequency();
	}
	public default TimeFrequency getTimeFrequency() {
		return this.frequency() != null ? this.frequency().getTimeFrequency()
										: null;
	}
}
