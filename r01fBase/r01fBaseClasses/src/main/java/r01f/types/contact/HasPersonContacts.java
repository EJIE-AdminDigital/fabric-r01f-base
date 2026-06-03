package r01f.types.contact;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import r01f.util.types.collections.CollectionUtils;

public interface HasPersonContacts {
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<PersonalDataWithContactInfo> getContacts();
	public void setContacts(final Collection<PersonalDataWithContactInfo> contacts);
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTACT
/////////////////////////////////////////////////////////////////////////////////////////
	public default Collection<ContactPhone> getPhones() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactPhones()
											   					   .stream())
									    .collect(Collectors.toList())
					: Collections.emptyList();
	}
	public default Collection<Phone> getPhoneNumbers() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactPhoneNumbers()
											   					   .stream())
									    .collect(Collectors.toList())
					: Collections.emptyList();
	}
	public default String getPhonesAsString() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactPhoneNumbers()
											   					   .stream())
									    .map(Phone::asString)
									    .collect(Collectors.joining("; "))
					: "";
	}
	public default Collection<ContactMail> getEmails() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactMails()
											   					   .stream())
									    .collect(Collectors.toList())
					: Collections.emptyList();
	}
	public default Collection<EMail> getMailAddresses() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactMailAddresses()
											   					   .stream())
									    .collect(Collectors.toList())
					: Collections.emptyList();
	}
	public default String getEmailsAsString() {
		return CollectionUtils.hasData(this.getContacts())
					? this.getContacts().stream()
									    .flatMap(contact -> contact.getContactMailAddresses()
											   					   .stream())
									    .map(EMail::asString)
									    .collect(Collectors.joining("; "))
					: "";
	}
}
