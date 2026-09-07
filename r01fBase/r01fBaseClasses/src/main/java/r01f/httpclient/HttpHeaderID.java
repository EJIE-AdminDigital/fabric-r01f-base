package r01f.httpclient;

import lombok.Getter;
import r01f.guids.JavaOIDDispenser;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="httpHeaderID")
public record HttpHeaderID(@Getter String id)
   implements OIDTyped<String> {
	
	public static HttpHeaderID forId(final String idAsString) {
		return new HttpHeaderID(idAsString);
	}
	public static HttpHeaderID valueOf(final String str) {
		return new HttpHeaderID(str);
	}
	public static HttpHeaderID fromString(final String s) {
		return HttpHeaderID.forId(s);
	}
	public static HttpHeaderID supply() {
		return HttpHeaderID.forId(JavaOIDDispenser.generateGUID());
	}
	@Override
	public String toString() {
		return this.id;
	}
	@Override
	public String asString() {
		return this.id;
	}
}