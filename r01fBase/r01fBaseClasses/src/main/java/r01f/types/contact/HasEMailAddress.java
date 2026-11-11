package r01f.types.contact;

import r01f.facets.Facet;

public interface HasEMailAddress
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public  EMail getEMailAddress();
	
	public default void setEMailAddress(final EMail mailAddress) {
		throw new UnsupportedOperationException("Overrride this method if you want to set the mail address");
	}
}
