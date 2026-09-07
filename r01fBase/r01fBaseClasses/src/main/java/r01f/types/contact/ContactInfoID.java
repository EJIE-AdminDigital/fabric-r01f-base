package r01f.types.contact;


import java.util.function.Function;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="contactInfoId")
public record ContactInfoID(@Getter String id)
   implements OIDTyped<String> { 	// usually this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
	
	public static ContactInfoID from(final String id) {
		return new ContactInfoID(id);
	}
	public static ContactInfoID forId(final String id) {
		return new ContactInfoID(id);
	}
	public static ContactInfoID valueOf(final String s) {
		return new ContactInfoID(s);
	}
	public static ContactInfoID fromString(final String s) {
		return ContactInfoID.forId(s);
	}
	public static Function<CharSequence,ContactInfoID> FROM_STRING_FUNC = str -> ContactInfoID.forId(str.toString());
	
	public static final ContactInfoID DEFAULT = ContactInfoID.forId("default");
	
	@Override
	public String asString() {
		return this.id;
	}
	@Override
	public String toString() {
		return this.id;
	}
}
