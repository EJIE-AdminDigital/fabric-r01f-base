package r01f.types.contact;

import java.util.Collection;

import r01f.util.types.collections.CollectionUtils;

public interface HasEMailAddresses
		 extends HasEMailAddress {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public  Collection<EMail> getEMailAddresses();
	
	public default boolean hasEMailAddresses() {
		return CollectionUtils.hasData(this.getEMailAddresses());
	}
	/**
	 * Check if there exists a {@link ContactEMail} with the given email
	 * @param theEmail
	 * @return
	 */
	public default boolean hasEMailAddress(final EMail theEmail) {
		if (hasEMailAddresses()) {
			return this.getEMailAddresses()
					   .stream()
					   .filter(aMail -> aMail.equals(theEmail))
					   .count() > 0;
		}
		return false;
	}
	public default EMail getFirstEMailAddress() {
		if (CollectionUtils.isNullOrEmpty(this.getEMailAddresses())) return null;
		return this.getEMailAddresses().iterator().next();
	}
	public default EMail getGoogleEMailAddress() {
		if (CollectionUtils.isNullOrEmpty(this.getEMailAddresses())) return null;
		return this.getEMailAddresses()
				   .stream()
				   .filter(mail -> mail.isGoogleEMail())
				   .findFirst().orElse(null);
	}
	
	public default String getEMailAddressesAsString() {
		return this.getEMailAddresesJoinedWith("; ");
	}
	/**
	 * Returns the mail addresses as a comma separated list
	 * @param separator
	 * @return
	 */
	public default String getEMailAddresesJoinedWith(final String separator) {
		Collection<EMail> mails = this.getEMailAddresses();
		return CollectionUtils.hasData(mails)
					? CollectionUtils.of(mails)
									 .toStringSeparatedWith(separator)
				    : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default EMail getEMailAddress() {
		return this.getFirstEMailAddress();
	}
	@Override
	public default void setEMailAddress(final EMail mailAddress) {
		throw new UnsupportedOperationException("Overrride this method if you want to set the mail address");
	}
}
