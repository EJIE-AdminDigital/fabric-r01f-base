package r01f.types.contact;


import java.util.function.Function;

import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@Immutable
@MarshallType(as="contactInfoId")
public class ContactInfoID 
     extends OIDBaseMutable<String> { 	// usually this should extend OIDBaseInmutable BUT it MUST have a default no-args constructor to be serializable
	
	private static final long serialVersionUID = -4048203299853024252L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfoID() {
		/* default no args constructor for serialization purposes */
	}
	public ContactInfoID(final String id) {
		super(id);	// normalize!!
	}
	public static ContactInfoID valueOf(final String s) {
		return new ContactInfoID(s);
	}
	public static ContactInfoID forId(final String id) {
		return new ContactInfoID(id);
	}
	public static Function<CharSequence,ContactInfoID> FROM_STRING_FUNC = str -> ContactInfoID.forId(str.toString());
	
	public static final ContactInfoID DEFAULT = ContactInfoID.forId("default");
}
