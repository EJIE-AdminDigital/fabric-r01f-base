package r01f.types.contact;

import java.util.Collection;

import r01f.facets.Facet;
import r01f.util.types.collections.CollectionUtils;

public interface HasMailAddresses
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public  Collection<EMail> getMailAddresses();
	
	public default boolean hasMailAddresses() {
		return CollectionUtils.hasData(this.getMailAddresses());
	}
	/**
	 * Check if there exists a {@link ContactMail} with the given email
	 * @param theEmail
	 * @return
	 */
	public default boolean hasMailAddress(final EMail theEmail) {
		return this.getMailAddresses()
				   .stream()
				   .filter(aMail -> aMail.equals(theEmail))
				   .count() > 0;				 
	}
	public default EMail getFirstMailAddress() {
		if (CollectionUtils.isNullOrEmpty(this.getMailAddresses())) return null;
		return this.getMailAddresses().iterator().next();
	}
	public default EMail getGoogleMailAddress() {
		if (CollectionUtils.isNullOrEmpty(this.getMailAddresses())) return null;
		return this.getMailAddresses()
				   .stream()
				   .filter(mail -> mail.isGoogleEMail())
				   .findFirst().orElse(null);
	}
	
	public default String getMailAddressesAsString() {
		return this.getMailAddresesJoinedWith("; ");
	}
	/**
	 * Returns the mail addresses as a comma separated list
	 * @param separator
	 * @return
	 */
	public default String getMailAddresesJoinedWith(final String separator) {
		Collection<EMail> mails = this.getMailAddresses();
		return CollectionUtils.hasData(mails)
					? CollectionUtils.of(mails)
									 .toStringSeparatedWith(separator)
				    : null;
	}
}
