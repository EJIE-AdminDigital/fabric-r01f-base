package r01f.types.pager;

import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="pagingPosition")
public record PagingPosition(/**
							  * Total number of results 
							  */
							 @MarshallField(as="totalItemsCount",
										    whenXml=@MarshallFieldAsXml(attr=true))
							 int _totalItemsCount,
							 /**
							  * This results page initial row number
							  */
							 @MarshallField(as="startPosition",
										    whenXml=@MarshallFieldAsXml(attr=true))
							 int _startPosition,
							 /**
							  * This results page final row number
							  */
							 @MarshallField(as="endPosition",
										    whenXml=@MarshallFieldAsXml(attr=true))
							 int _endPosition,
							 /**
							  * The number of requested items (usually the page size)
							  */
							 @MarshallField(as="requestedNumberOfItems",
										    whenXml=@MarshallFieldAsXml(attr=true))
							 int _requestedNumberOfItems) {
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTERS
/////////////////////////////////////////////////////////////////////////////////////////
	public int getTotalItemsCount() {
		return _totalItemsCount;
	}
	public int getStartPosition() {
		return _startPosition;
	}
	public int getEndPosition() {
		return _endPosition;
	}
	public int getRequestedNumberOfItems() {
		return _requestedNumberOfItems;
	}
}
