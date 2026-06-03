package r01f.types.contact;

import r01f.facets.Facet;

public interface HasPhoneNumber
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Phone getPhoneNumber();
	
	public default void setPhoneNumber(final Phone phoneNumber) {
		throw new UnsupportedOperationException("Overrride this method if you want to set the phone number");
	}
}
