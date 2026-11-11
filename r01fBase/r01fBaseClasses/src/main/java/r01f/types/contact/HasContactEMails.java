package r01f.types.contact;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

public interface HasContactEMails 
		 extends HasEMailAddresses {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<ContactEMail> getContactEMails();
	public void setContactEMails(final Collection<ContactEMail> mails);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void addOrUpdateContactEMail(final ContactEMail mail) {
		if (mail == null) return;
		
		if (this.getContactEMails() == null) this.setContactEMails(Lists.newArrayList());
		// check if there exists another mail with the same address
		ContactEMail other = this.getContactEMailFor(mail.getMail());
		// update the existing or add
		if (other != null) {
			other.updateFrom(mail);
		} else {
			this.getContactEMails()
				.add(mail);
		}
	}
	public default void addOrUpdateContactEMails(final Collection<ContactEMail> mails) {
		if (this.getContactEMails() == null) this.setContactEMails(Lists.newArrayList());
		if (CollectionUtils.hasData(mails)) {
			for (ContactEMail mail : mails) {
				this.addOrUpdateContactEMail(mail);
			}
		}
	}
	public default ContactEMail removeContactEMail(final ContactInfoUsage usage) {
		ContactEMail outMail = IsContactMean.find(this.getContactEMails(),usage);
		if (outMail != null) this.getContactEMails()
								 .remove(outMail);
		return outMail;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return true if there are mail address associated with the contact info
	 */
	public default boolean hasContactEMails() {
		return CollectionUtils.hasData(this.getContactEMails());
	}
	public default Collection<ContactEMail> getContactEMailsWithDefaultOneFirst() {
		return IsContactMean.orderDefaultFirst(this.getContactEMails());
	}
	/**
	 * Returns the mails matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<ContactEMail> getContactEMailsMatching(final Predicate<ContactEMail> predicate) {
		return IsContactMean.findMatching(this.getContactEMails(),predicate);
	}
	/**
	 *
	 * @return true if there are none private mail addresses associated with the contact info
	 */
	public default boolean hasNonePrivateContactEMails() {
		if (hasContactEMails()) {
			for (ContactEMail contactMail : this.getContactEMails()) {
				if (!contactMail.isPrivate()) {
					return true;
				}
			};
		}
		return false;
	}
	/**
	 * Returns the first {@link ContactEMail} with the given email
	 * @param email
	 * @return
	 */
	public default ContactEMail getContactEMailFor(final EMail email) {
		if (CollectionUtils.isNullOrEmpty(this.getContactEMails())) return null;
		return this.getContactEMails().stream()
							.filter(mail -> mail != null
										 && mail.getMail().equals(email))
							.findFirst().orElse(null);
	}
	public default ContactEMail getGoogleContactEMail() {
		if (CollectionUtils.isNullOrEmpty(this.getContactEMails())) return null;
		return this.getContactEMails()
				   .stream()
				   .filter(mail -> mail != null
				  			    && mail.getMail().isGoogleEMail())
				   .findFirst().orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default Collection<EMail> getEMailAddresses() {
		// order the mails so that the default one is the first of the list
		Collection<ContactEMail> orderedMails = IsContactMean.orderDefaultFirst(this.getContactEMails());
		// transform to mails
		Collection<EMail> mails = CollectionUtils.hasData(orderedMails)
										? orderedMails.stream()
											  		  .filter(contactMail -> contactMail.getMail() != null)
											  		  .map(ContactEMail::getMail)
											  		  .collect(Collectors.toList())
										 : null;
		
		return mails;
	}
	/**
	 * Returns the email addresses matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<EMail> getEMailAddressesMatching(final Predicate<ContactEMail> predicate) {
		return CollectionUtils.hasData(this.getContactEMails())
					? this.getContactEMails()
						  .stream()
						  .filter(contactMail -> predicate.test(contactMail))
						  .map(ContactEMail::getMail)
						  .collect(Collectors.toList())
					 : null;
	}
	/**
	 * Returns an email address for an intended usage
	 * @param id
	 * @return
	 */
	public default EMail getEMailAddress(final ContactInfoUsage usage) {
		ContactEMail mail = IsContactMean.find(this.getContactEMails(),usage);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns an email addess for an intended usage or any if the requested one
	 * does not exists
	 * @param usage
	 * @return
	 */
	public default EMail getEMailAddressOrAny(final ContactInfoUsage usage) {
		EMail outMail = this.getEMailAddress(usage);
		if (outMail == null && CollectionUtils.hasData(this.getContactEMails())) {
			ContactEMail mail = CollectionUtils.pickOneElement(this.getContactEMails());
			if (mail != null) outMail = mail.getMail();
		}
		return outMail;
	}
	/**
	 * Returns the default email address
	 * @return
	 */
	public default EMail getDefaultEMailAddress() {
		ContactEMail mail = IsContactMean.findDefault(this.getContactEMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns the default email address or any of them if none is set as default one
	 * @return
	 */
	public default EMail getDefaultEMailAddressOrAny() {
		ContactEMail mail = IsContactMean.findDefault(this.getContactEMails());
		if (mail == null && CollectionUtils.hasData(this.getContactEMails())) mail = CollectionUtils.pickOneElement(this.getContactEMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address other than default
	 * @return
	 */
	public default EMail getEMailAddressOtherThanDefault() {
		ContactEMail mail = IsContactMean.findOtherThanDefault(this.getContactEMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public default EMail getEMailAddress() {
		// Try to find the default
		EMail mail = this.getDefaultEMailAddress();
		// ... if there's NO default, return any of them
		if (mail == null) {
			ContactEMail contactMail = IsContactMean.findOne(this.getContactEMails());
			mail = contactMail != null ? contactMail.getMail() : null;
		}
		return mail;
	}
}