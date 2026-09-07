package r01f.types.pager;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="paginationRequest")
public record PaginationRequest(@MarshallField(as="firstItemPosition",
												   whenXml=@MarshallFieldAsXml(attr=true))
								@Getter int firstItemPosition,
								
								@MarshallField(as="requestedNumberOfItems",
											   whenXml=@MarshallFieldAsXml(attr=true))
								@Getter int requestedNumberOfItems) {
	// nothing
}
