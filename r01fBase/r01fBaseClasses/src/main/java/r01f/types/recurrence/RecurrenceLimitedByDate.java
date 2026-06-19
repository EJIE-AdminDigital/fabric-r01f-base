package r01f.types.recurrence;

import java.time.LocalDate;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="recurrenceLimitedByDate",typeId="limitedByDate")
public record RecurrenceLimitedByDate(// repeat daily, weekly, monthly, yearly
									  @MarshallField(as="frequency")
									  IsRecurrenceFrequency frequency,
									  // until when the recurrence is repeated (if not set, it is repeated indefinitely)
									  @MarshallField(as="repeatUntil",
												     whenXml=@MarshallFieldAsXml(attr=true))
									  LocalDate repeatUntil) 
   implements IsRecurrence {

}
