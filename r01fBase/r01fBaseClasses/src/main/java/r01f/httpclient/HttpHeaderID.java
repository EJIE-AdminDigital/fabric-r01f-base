package r01f.httpclient;

import lombok.NoArgsConstructor;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="httpHeaderID")
@NoArgsConstructor
public  class HttpHeaderID
	   extends OIDBaseMutable<String> {
	private static final long serialVersionUID = -3633101002845530783L;
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpHeaderID(final String id) {
		super(id);
	}
	public static HttpHeaderID forId(final String idAsString) {
		return new HttpHeaderID(idAsString);
	}
}