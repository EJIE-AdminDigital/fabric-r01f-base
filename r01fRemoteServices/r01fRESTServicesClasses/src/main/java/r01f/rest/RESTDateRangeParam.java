package r01f.rest;


import java.time.Instant;

import r01f.types.Range;

/**
 * Since a Jersey resource method param received from the query string (@QueryParam) or from the resource path (@PathParam)
 * can only be maped to a custom type if this type has:
 * <ul>
 * 	<li>A constructor from a String</li>
 *  <li>A valueOf(String) method<li>
 * </ul>
 * ... and {@link Date} type do not fall into any of theese categories, a custom type must be used
 * (see http://codahale.com/what-makes-jersey-interesting-parameter-classes/)
 */
public class RESTDateRangeParam {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final Range<Instant> _theDateRange;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTDateRangeParam(final String dateRangeStr) {
		_theDateRange = Range.parse(dateRangeStr,Instant.class);
	}
	public RESTDateRangeParam(final Range<Instant> dateRange) {
		_theDateRange = dateRange;
	}
	public Range<Instant> asDateRange() {
		return _theDateRange;
	}
}
