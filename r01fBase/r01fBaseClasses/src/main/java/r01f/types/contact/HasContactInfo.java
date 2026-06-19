package r01f.types.contact;

import java.util.Collection;
import java.util.Collections;

public interface HasContactInfo 
		 extends HasMailAddresses,HasContactMails,
		 		 HasPhoneNumbers,HasContactPhones {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfo getContactInfo();
	public void setContactInfo(final ContactInfo info);
/////////////////////////////////////////////////////////////////////////////////////////
//	CONTACT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public default Collection<ContactPhone> getContactPhones() {
		return this.getContactInfo() != null ? this.getContactInfo().getContactPhones() : Collections.emptyList();
	}
	@Override
	public default Collection<Phone> getPhoneNumbers() {
		return this.getContactInfo() != null ? this.getContactInfo().getPhoneNumbers() : Collections.emptyList();
	}
	@Override
	public default Collection<ContactMail> getContactMails() {
		return this.getContactInfo() != null ? this.getContactInfo().getContactMails() : Collections.emptyList();
	}
	@Override
	public default Collection<EMail> getMailAddresses() {
		return this.getContactInfo() != null ? this.getContactInfo().getMailAddresses() : Collections.emptyList();
	}
}