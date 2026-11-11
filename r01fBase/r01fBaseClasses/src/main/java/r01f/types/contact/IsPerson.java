package r01f.types.contact;

import r01f.util.types.Strings;

public interface IsPerson {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public default <C extends IsContactMean> boolean hasContactMean(final C contactMean) {
		if (contactMean instanceof EMail email) {
			return this.hasEMail(email);
		} else if (contactMean instanceof Phone phone) {
			return this.hasPhoneNumber(phone);
		}
		throw new IllegalArgumentException(Strings.customized("'{}' is not a {} or {} instance",
				 											   this.getClass(),EMail.class,Phone.class));
	}
	@SuppressWarnings("unchecked")
	public default <C extends IsContactMean> C getDefaultContactMeanOrAny(final Class<? extends IsContactMean> contactMeanType) {
		if (EMail.class.isAssignableFrom(contactMeanType)) {
			return (C)this.getDefaultEMailOrAny();
		} else if (Phone.class.isAssignableFrom(contactMeanType)) {
			return (C)this.getDefaultPhoneNumberOrAny();
		}
		throw new IllegalArgumentException(Strings.customized("'{}' is not a {} or {} instance",
				 											   this.getClass(),EMail.class,Phone.class));
	}
	@SuppressWarnings("unchecked")
	public default <C extends IsContactMean> C getDefaultContactMeanOrNull(final Class<? extends IsContactMean> contactMeanType) {
		if (EMail.class.isAssignableFrom(contactMeanType)) {
			return (C)this.getDefaultEMailOrNull();
		} else if (Phone.class.isAssignableFrom(contactMeanType)) {
			return (C)this.getDefaultPhoneNumberOrNull();
		}
		throw new IllegalArgumentException(Strings.customized("'{}' is not a {} or {} instance",
				 											   this.getClass(),EMail.class,Phone.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default boolean hasEMail(final EMail email ) {
		if (!(this instanceof HasEMailAddress)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					  this.getClass(),HasEMailAddress.class));
		boolean hasEmail = false;
		if (this instanceof HasContactEMails hasContactEMails) {
			hasEmail = hasContactEMails.hasEMailAddress(email);
		} else if (this instanceof HasEMailAddresses hasEMailAddreses) {
			hasEmail = hasEMailAddreses.hasEMailAddress(email);
		} else {
			EMail personEMail = ((HasEMailAddress)this).getEMailAddress();
			hasEmail = personEMail != null 
				    && personEMail.is(email);
		}
		return hasEmail;
	}
	public default boolean hasPhoneNumber(final Phone phone ) {
		if (!(this instanceof HasPhoneNumber)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					 this.getClass(),HasPhoneNumber.class));
		boolean hasPhone = false;
		if (this instanceof HasContactPhones hasContactPhones) {
			hasPhone = hasContactPhones.hasPhoneNumber(phone);
		} else if (this instanceof HasPhoneNumbers hasPhoneNumbers) {
			hasPhone = hasPhoneNumbers.hasPhoneNumber(phone);
		} else {
			Phone personPhone = ((HasPhoneNumber)this).getPhoneNumber();
			hasPhone = personPhone != null 
				    && personPhone.is(phone);
		}
		return hasPhone;
	}
	public default Phone getDefaultPhoneNumberOrAny() {
		if (!(this instanceof HasPhoneNumber)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					 this.getClass(),HasPhoneNumber.class));
		Phone personPhone = null;
		if (this instanceof HasContactPhones hasContactPhones) {
			personPhone = hasContactPhones.getDefaultPhoneNumberOrAny();
		} else {
			personPhone = ((HasPhoneNumber)this).getPhoneNumber();
		}
		return personPhone;
	}	
	public default Phone getDefaultPhoneNumberOrNull() {
		if (!(this instanceof HasPhoneNumber)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					 this.getClass(),HasPhoneNumber.class));
		Phone personPhone = null;
		if (this instanceof HasContactPhones hasContactPhones) {
			personPhone = hasContactPhones.getDefaultPhoneNumber();
		} else {
			personPhone = ((HasPhoneNumber)this).getPhoneNumber();
		}
		return personPhone;
	}
	public default EMail getDefaultEMailOrAny() {
		 if (!(this instanceof HasEMailAddress)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					   this.getClass(),HasEMailAddress.class));
		 EMail personEmail = null;
		 if (this instanceof HasContactEMails hasContactEMails) {
			 personEmail = hasContactEMails.getDefaultEMailAddressOrAny();
		 } else {
			 personEmail = ((HasEMailAddress)this).getEMailAddress();
		 }
		 return personEmail;
	}
	public default EMail getDefaultEMailOrNull() {
		 if (!(this instanceof HasEMailAddress)) throw new IllegalArgumentException(Strings.customized("'{}' is not a {} instance",
				 																					   this.getClass(),HasEMailAddress.class));
		 EMail personEmail = null;
		 if (this instanceof HasContactEMails hasContactEMails) {
			 personEmail = hasContactEMails.getDefaultEMailAddress();
		 } else {
			 personEmail = ((HasEMailAddress)this).getEMailAddress();
		 }
		 return personEmail;
	}
}
