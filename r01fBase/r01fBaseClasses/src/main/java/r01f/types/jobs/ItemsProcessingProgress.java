package r01f.types.jobs;

import java.io.Serializable;
import java.time.Instant;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Encapsulates info about an item flow processing progress 
 */
@Accessors(prefix="_")
public record ItemsProcessingProgress(// the syndication process start date
									  @MarshallField(as="startedAt",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
									 				whenXml=@MarshallFieldAsXml(attr=true))
									  Instant startedAt,
									  // total items to be processed and currently processed items are used to calculate the progress of the syndication
									  @MarshallField(as="totalItemsToBeProcessed",
									 			    whenXml=@MarshallFieldAsXml(attr=true))
									  long totalItemsToBeProcessed,
									  // currently processed items
									  @MarshallField(as="alreadyProcessedItems",
									 			    whenXml=@MarshallFieldAsXml(attr=true))
									  long alreadyProcessedItems) 
   implements Serializable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant getStartedAt() {
		return this.startedAt;
	}
	public long getTotalItemsToBeProcessed() {
		return this.totalItemsToBeProcessed;
	}
	public long getAlreadyProcessedItems() {
		return this.alreadyProcessedItems;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasFinished() {
		return this.alreadyProcessedItems == this.totalItemsToBeProcessed;
	}
	public float getProgrees() {
		return this.totalItemsToBeProcessed > 0 ? this.alreadyProcessedItems / (float)this.totalItemsToBeProcessed 
											: 0;
	}
}
