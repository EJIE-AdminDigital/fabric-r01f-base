package r01f.types.pager;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="paginationResponse")
public record PaginationResponse(// Total of items matching the search criteria
								     @MarshallField(as="totalItemsCount",
								     			   whenXml=@MarshallField.MarshallFieldAsXml(attr=true))
								     @Getter int totalItemsCount,
								     // This results page initial row number
								     @MarshallField(as="startPosition",
								             	   whenXml=@MarshallField.MarshallFieldAsXml(attr=true))
								     @Getter int startPosition,
								      // This results page final row number
								     @MarshallField(as="endPosition",
								             	   whenXml=@MarshallField.MarshallFieldAsXml(attr=true))
								     @Getter int endPosition,
								      // The number of requested items (usually the page size)
								     @MarshallField(as="requestedNumberOfItems",
								             	   whenXml=@MarshallField.MarshallFieldAsXml(attr=true))
								     @Getter int requestedNumberOfItems) {
	// nothing
}
