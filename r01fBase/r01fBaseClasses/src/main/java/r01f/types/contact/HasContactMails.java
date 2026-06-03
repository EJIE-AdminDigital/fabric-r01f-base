package r01f.types.contact;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

public interface HasContactMails 
		 extends HasMailAddresses {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<ContactMail> getContactMails();
	public void setContactMails(final Collection<ContactMail> mails);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void addOrUpdateContactMail(final ContactMail mail) {
		if (mail == null) return;
		
		if (this.getContactMails() == null) this.setContactMails(Lists.newArrayList());
		// check if there exists another mail with the same address
		ContactMail other = this.getContactMailFor(mail.getMail());
		// update the existing or add
		if (other != null) {
			other.updateFrom(mail);
		} else {
			this.getContactMails()
				.add(mail);
		}
	}
	public default void addOrUpdateContactMails(final Collection<ContactMail> mails) {
		if (this.getContactMails() == null) this.setContactMails(Lists.newArrayList());
		if (CollectionUtils.hasData(mails)) {
			for (ContactMail mail : mails) {
				this.addOrUpdateContactMail(mail);
			}
		}
	}
	public default ContactMail removeContactMail(final ContactInfoUsage usage) {
		ContactMail outMail = ContactMean.find(this.getContactMails(),usage);
		if (outMail != null) this.getContactMails()
								 .remove(outMail);
		return outMail;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * @return true if there are mail address associated with the contact info
	 */
	public default boolean hasContactMails() {
		return CollectionUtils.hasData(this.getContactMails());
	}
	public default Collection<ContactMail> getContactMailsWithDefaultOneFirst() {
		return ContactMean.orderDefaultFirst(this.getContactMails());
	}
	/**
	 * Returns the mails matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<ContactMail> getContactMailsMatching(final Predicate<ContactMail> predicate) {
		return ContactMean.findMatching(this.getContactMails(),predicate);
	}
	/**
	 *
	 * @return true if there are none private mail addresses associated with the contact info
	 */
	public default boolean hasNonePrivateContactMails() {
		if (hasContactMails()) {
			for (ContactMail contactMail : this.getContactMails()) {
				if (!contactMail.isPrivate()) {
					return true;
				}
			};
		}
		return false;
	}
	/**
	 * Returns the first {@link ContactMail} with the given email
	 * @param email
	 * @return
	 */
	public default ContactMail getContactMailFor(final EMail email) {
		if (CollectionUtils.isNullOrEmpty(this.getContactMails())) return null;
		return this.getContactMails().stream()
							.filter(mail -> mail != null
										 && mail.getMail().equals(email))
							.findFirst().orElse(null);
	}
	public default ContactMail getGoogleContactMail() {
		if (CollectionUtils.isNullOrEmpty(this.getContactMails())) return null;
		return this.getContactMails()
				   .stream()
				   .filter(mail -> mail != null
				  			    && mail.getMail().isGoogleEMail())
				   .findFirst().orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default Collection<EMail> getMailAddresses() {
		// order the mails so that the default one is the first of the list
		Collection<ContactMail> orderedMails = ContactMean.orderDefaultFirst(this.getContactMails());
		// transform to mails
		Collection<EMail> mails = CollectionUtils.hasData(orderedMails)
										? orderedMails.stream()
											  		  .filter(contactMail -> contactMail.getMail() != null)
											  		  .map(ContactMail::getMail)
											  		  .collect(Collectors.toList())
										 : null;
		
		return mails;
	}
	/**
	 * Returns the email addresses matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<EMail> getMailAddressesMatching(final Predicate<ContactMail> predicate) {
		return CollectionUtils.hasData(this.getContactMails())
					? this.getContactMails()
						  .stream()
						  .filter(contactMail -> predicate.test(contactMail))
						  .map(ContactMail::getMail)
						  .collect(Collectors.toList())
					 : null;
	}
	/**
	 * Returns an email address for an intended usage
	 * @param id
	 * @return
	 */
	public default EMail getMailAddress(final ContactInfoUsage usage) {
		ContactMail mail = ContactMean.find(this.getContactMails(),usage);
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns an email addess for an intended usage or any if the requested one
	 * does not exists
	 * @param usage
	 * @return
	 */
	public default EMail getMailAddressOrAny(final ContactInfoUsage usage) {
		EMail outMail = this.getMailAddress(usage);
		if (outMail == null && CollectionUtils.hasData(this.getContactMails())) {
			ContactMail mail = CollectionUtils.pickOneElement(this.getContactMails());
			if (mail != null) outMail = mail.getMail();
		}
		return outMail;
	}
	/**
	 * Returns the default email address
	 * @return
	 */
	public default EMail getDefaultMailAddress() {
		ContactMail mail = ContactMean.findDefault(this.getContactMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns the default email address or any of them if none is set as default one
	 * @return
	 */
	public default EMail getDefaultMailAddressOrAny() {
		ContactMail mail = ContactMean.findDefault(this.getContactMails());
		if (mail == null && CollectionUtils.hasData(this.getContactMails())) mail = CollectionUtils.pickOneElement(this.getContactMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address other than default
	 * @return
	 */
	public default EMail getMailAddressOtherThanDefault() {
		ContactMail mail = ContactMean.findOtherThanDefault(this.getContactMails());
		return mail != null ? mail.getMail() : null;
	}
	/**
	 * Returns a mail address (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public default EMail getMailAddress() {
		// Try to find the default
		EMail mail = this.getDefaultMailAddress();
		// ... if there's NO default, return any of them
		if (mail == null) {
			ContactMail contactMail = ContactMean.findOne(this.getContactMails());
			mail = contactMail != null ? contactMail.getMail() : null;
		}
		return mail;
	}
}