package r01f.types.contact;

import java.util.Collection;
import java.util.Collections;

public interface HasContactInfo 
		 extends HasEMailAddresses,HasContactEMails,
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
	public default Collection<ContactEMail> getContactEMails() {
		return this.getContactInfo() != null ? this.getContactInfo().getContactEMails() : Collections.emptyList();
	}
	@Override
	public default Collection<EMail> getEMailAddresses() {
		return this.getContactInfo() != null ? this.getContactInfo().getEMailAddresses() : Collections.emptyList();
	}
}