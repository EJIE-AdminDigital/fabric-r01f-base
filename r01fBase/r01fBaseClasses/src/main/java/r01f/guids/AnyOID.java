package r01f.guids;

import lombok.Getter;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Any oid
 */
@MarshallType(as="anyOid")
public record AnyOID(@Getter String id) 
   implements OIDTyped<String> {
	
	public static AnyOID forId(final String id) {
		return new AnyOID(id);
	}
	public static AnyOID valueOf(final String str) {
		return new AnyOID(str);
	}
	public static AnyOID fromString(final String s) {
		return AnyOID.forId(s);
	}
	public static AnyOID supply() {
		return AnyOID.forId(JavaOIDDispenser.generateGUID());
	}
	@Override
	public String toString() {
		return this.id;
	}
	@Override
	public String asString() {
		return this.id();
	}
}
