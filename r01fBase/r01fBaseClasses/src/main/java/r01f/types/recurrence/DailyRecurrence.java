package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="dailyEventRecurrence",typeId="daily")
public record DailyRecurrence(// every X days
							  @MarshallField(as="everyXDays",
											 whenXml=@MarshallFieldAsXml(attr=true))
							  int everyXDays) 
   implements IsRecurrenceFrequency {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static DailyRecurrence everyXDays(final int everyXDays) {
		return new DailyRecurrence(everyXDays);
	}
}
