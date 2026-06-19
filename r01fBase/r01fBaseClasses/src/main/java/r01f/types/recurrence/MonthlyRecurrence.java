package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.DayOfMonth;

@MarshallType(as="monthlyEventRecurrence",typeId="monthly")
public record MonthlyRecurrence(// every X months
								@MarshallField(as="everyXMonths",
										  whenXml=@MarshallFieldAsXml(attr=true))
								int everyXMonths,
								// on which day of the month
								@MarshallField(as="dayOfMonth",
										  whenXml=@MarshallFieldAsXml(attr=true))
								DayOfMonth dayOfMonth) 
   implements IsRecurrenceFrequency {

}
