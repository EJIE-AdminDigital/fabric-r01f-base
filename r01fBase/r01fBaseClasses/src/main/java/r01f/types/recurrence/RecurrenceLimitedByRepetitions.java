package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="recurrenceLimitedByRepetitions",typeId="limitedByRepetitions")
public record RecurrenceLimitedByRepetitions(// repeat daily, weekly, monthly, yearly
										     @MarshallField(as="frequency")
										     IsRecurrenceFrequency frequency,
										     // until when the recurrence is repeated (if not set, it is repeated indefinitely)
										     @MarshallField(as="repeatCount",
														    whenXml=@MarshallFieldAsXml(attr=true))
										     int repeatCount) 
   implements IsRecurrence {	
}
