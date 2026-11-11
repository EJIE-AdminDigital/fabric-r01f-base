package r01f.rest;

import java.time.Instant;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.util.types.datetime.DateTimeMarshaller;

/**
 * Since a Jersey resource method param received from the query string (@QueryParam) or from the resource path (@PathParam)
 * can only be maped to a custom type if this type has:
 * <ul>
 * 	<li>A constructor from a String</li>
 *  <li>A valueOf(String) method<li>
 * </ul>
 * ... and {@link Instant} type do not fall into any of theese categories, a custom type must be used
 * (see http://codahale.com/what-makes-jersey-interesting-parameter-classes/)
 */
@Accessors(prefix="_")
public class RESTDateParam {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Instant _theDate;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTDateParam(final String dateMillisStr) {
		_theDate = DateTimeMarshaller.forUnMarshallingFrom(dateMillisStr)
									 .asInstantAsummingISO8601OrEPOCHMillis()
									 .orElse(null);
	}
	public RESTDateParam(final Instant date) {
		_theDate = date;
	}
}
