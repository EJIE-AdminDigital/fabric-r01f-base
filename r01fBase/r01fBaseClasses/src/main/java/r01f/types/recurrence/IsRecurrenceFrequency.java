package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.types.datetime.TimeFrequency;

@MarshallPolymorphicTypeInfo
public interface IsRecurrenceFrequency {
	public default TimeFrequency getTimeFrequency() {
		return switch (this) {
					case DailyRecurrence daily -> TimeFrequency.DAILY;
					case WeeklyRecurrence weekly -> TimeFrequency.WEEKLY;
					case MonthlyRecurrence monthly -> TimeFrequency.MONTHLY;
					case YearlyRecurrence yearly -> TimeFrequency.YEARLY;
					default -> throw new IllegalArgumentException("Unexpected value: " + this);
			  };
	}
}
