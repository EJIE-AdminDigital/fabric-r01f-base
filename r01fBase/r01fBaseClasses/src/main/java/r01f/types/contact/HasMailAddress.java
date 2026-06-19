package r01f.types.contact;

import r01f.facets.Facet;

public interface HasMailAddress
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public  EMail getMailAddress();
	
	public default void setMailAddress(final EMail mailAddress) {
		throw new UnsupportedOperationException("Overrride this method if you want to set the mail address");
	}
}
