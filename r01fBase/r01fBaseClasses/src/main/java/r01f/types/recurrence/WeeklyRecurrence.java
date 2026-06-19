package r01f.types.recurrence;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="weeklyEventRecurrence",typeId="weekly")
public record WeeklyRecurrence(// every X weeks
							   @MarshallField(as="everyXWeeks",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   int everyXWeeks,
							   // on which days of the week
							   @MarshallField(as="onMonday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onMonday,
							   @MarshallField(as="onTuesday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onTuesday,
							   @MarshallField(as="onWednesday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onWednesday,
							   @MarshallField(as="onThursday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onThursday,
							   @MarshallField(as="onFriday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onFriday,
							   @MarshallField(as="onSaturday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onSaturday,
							   @MarshallField(as="onSunday",
							   			  whenXml=@MarshallFieldAsXml(attr=true))
							   boolean onSunday) 
   implements IsRecurrenceFrequency {

}
