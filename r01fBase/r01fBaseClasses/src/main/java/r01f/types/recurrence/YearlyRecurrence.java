package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;

@MarshallType(as="yearlyEventRecurrence",typeId="yearly")
public record YearlyRecurrence(// every X months
							   @MarshallField(as="everyXYears",
									  whenXml=@MarshallFieldAsXml(attr=true))
							   int everyXYears,
							   // on which month
							   @MarshallField(as="monthOfYear",
									  whenXml=@MarshallFieldAsXml(attr=true))
							   MonthOfYear monthOfYear,
							   // on which day of the month
							   @MarshallField(as="dayOfMonth",
									  whenXml=@MarshallFieldAsXml(attr=true))
							   DayOfMonth dayOfMonth) 
   implements IsRecurrenceFrequency {

}
