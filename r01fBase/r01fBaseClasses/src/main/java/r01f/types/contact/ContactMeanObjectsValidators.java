package r01f.types.contact;

import java.util.Collection;

public class ContactMeanObjectsValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Validates an email collection in string format as
	 * 		[email];[email];[email]....
	 * @param value
	 * @return
	 */
	public static boolean isValidEmails(final String value) {
		Collection<ContactMail> emails = EMail.emailsCollectionFromString(value);
		boolean allValid = true;
		for (ContactMail email : emails) {
			if (email.getMail() == null
			 || !email.getMail().isValid()) {
				allValid = false;
				break;
			}
		}
		return allValid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Validates a phone collection in string format as
	 * 		[phone];[phone];[phone]....
	 * @param value
	 * @return
	 */
	public static boolean isValidPhones(final String value) {
		Collection<ContactPhone> phones = Phone.phonesCollectionFromString(value);
		boolean allValid = true;
		for (ContactPhone phone : phones) {
			if (phone.getNumber() == null
			 || !phone.getNumber().isValid()) {
				allValid = false;
				break;
			}
		}
		return allValid;
	}
}
