package r01f.types.contact;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

public interface HasContactPhones 
		 extends HasPhoneNumbers {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<ContactPhone> getContactPhones();
	public void setContactPhones(final Collection<ContactPhone> phones);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public default void addOrUpdateContactPhone(final ContactPhone phone) {
		if (phone == null) return;
		
		if (this.getContactPhones() == null) this.setContactPhones(Lists.newArrayList());
		// check if there exists another phone with the same number
		ContactPhone other = this.getContactPhoneFor(phone.getNumber());
		// update the existing one or add
		if (other != null) {
			other.updateFrom(phone);
		} else {
			this.getContactPhones()
				.add(phone);
		}
	}
	public default void addOrUpdateContactPhones(final Collection<ContactPhone> phones) {
		if (this.getContactPhones() == null) this.setContactPhones(Lists.newArrayList());
		if (CollectionUtils.hasData(phones)) {
			for (ContactPhone phone : phones) {
				this.addOrUpdateContactPhone(phone);
			}
		}
	}
	public default ContactPhone removeContactPhone(final ContactInfoUsage usage) {
		if (this.getContactPhones() == null) return null;
		
		ContactPhone outPhone = ContactMean.find(this.getContactPhones(),usage);
		if (outPhone != null) this.getContactPhones()
								  .remove(outPhone);
		return outPhone;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if there are phones associated with the contact info
	 */
	public default boolean hasContactPhones() {
		return CollectionUtils.hasData(this.getContactPhones());
	}
	/**
	 * @return true if there are phones of a certain type associated with the contact info
	 */
	public default boolean hasContactPhonesOfType(final ContactPhoneType type) {
		return this.hasContactPhones() ? this.getContactPhones()
									  .stream()
									  .anyMatch(aPhone -> aPhone != null
											 && aPhone.getType() == type)
								: false;
	}
	public default Collection<ContactPhone> getContactPhonesWithDefaultOneFirst() {
		return ContactMean.orderDefaultFirst(this.getContactPhones());
	}
	/**
	 * Returns the phones matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<ContactPhone> getContactPhonesMatching(final Predicate<ContactPhone> predicate) {
		return ContactMean.findMatching(this.getContactPhones(),predicate);
	}
	/**
	 * Returns the default contact phone.
	 * @param id
	 * @return
	 */
	public default ContactPhone getDefaultContactPhone() {
		ContactPhone phone = ContactMean.findDefault(this.getContactPhones());
		return phone != null ? phone : null;
	}
	/**
	 * Returns the default contact phone or any of them if any is set as the default one
	 * @return
	 */
	public default ContactPhone getDefaultContactPhoneOrAny() {
		ContactPhone phone = ContactMean.findDefault(this.getContactPhones());
		if (phone == null && CollectionUtils.hasData(this.getContactPhones())) phone = CollectionUtils.pickOneElement(this.getContactPhones());
		return phone != null ? phone : null;
	}
	/**
	 * Returns the default contact phone or any of them if any is set as the default one
	 * @return
	 */
	public default ContactPhone getDefaultContactPhoneOrAnyForType(final ContactPhoneType type) {
		ContactPhone phone = ContactMean.findDefaultForType(this.getContactPhones(),
												 			type);
		if (phone == null && hasContactPhonesOfType(type)) phone = ContactMean.findOtherThanDefaultForType(this.getContactPhones(),
																						 			type);
		return phone != null ? phone : null;
	}
	/**
	 * Returns the first {@link ContactPhone} with the given number
	 * @param phone
	 * @return
	 */
	public default ContactPhone getContactPhoneFor(final Phone phone) {
		return CollectionUtils.hasData(this.getContactPhones())
					? this.getContactPhones()
						  .stream()
						  .filter(aPhone -> aPhone != null
										 && aPhone.getNumber().equals(phone))
						  .findFirst().orElse(null)
					: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default Collection<Phone> getPhoneNumbers() {
		// order the phones so that the default one is the first of the list
		Collection<ContactPhone> orderedPhones = ContactMean.orderDefaultFirst(this.getContactPhones());
		// transform to phones
		return CollectionUtils.hasData(orderedPhones)
					? orderedPhones.stream()
						  		   .filter(contactPhone -> contactPhone.getNumber() != null)
						  		   .map(ContactPhone::getNumber)
						  		   .collect(Collectors.toList())
					: null;
	}
	/**
	 * Returns the phone numbers matching the given predicate
	 * @param type
	 * @return
	 */
	public default Collection<Phone> getPhoneNumbersMatching(final Predicate<ContactPhone> predicate) {
		return CollectionUtils.hasData(this.getContactPhones())
					? this.getContactPhones()
						  .stream()
						  .filter(contactPhone -> predicate.test(contactPhone))
						  .map(ContactPhone::getNumber)
						  .collect(Collectors.toList())
					 : null;
	}
	/**
	 * Returns a phone for an intended usage
	 * @param id
	 * @return
	 */
	public default Phone getPhoneNumber(final ContactInfoUsage usage) {
		ContactPhone phone = ContactMean.find(this.getContactPhones(),usage);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns a phone of type.
	 * @param id
	 * @return
	 */
	public default Phone getPhoneNumber(final ContactPhoneType type) {
		ContactPhone phone = ContactMean.find(this.getContactPhones(),type);
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns the default phone number
	 * @return
	 */
	public default Phone getDefaultPhoneNumber() {
		ContactPhone phone = ContactMean.findDefault(this.getContactPhones());
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns the default phone number or any of them if any is set as the default one
	 * @return
	 */
	public default Phone getDefaultPhoneNumberOrAny() {
		ContactPhone phone = ContactMean.findDefault(this.getContactPhones());
		if (phone == null && CollectionUtils.hasData(this.getContactPhones())) phone = CollectionUtils.pickOneElement(this.getContactPhones());
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns any not-default phone number
	 * @return
	 */
	public default Phone getPhoneNumberOtherThanDefault() {
		ContactPhone phone = ContactMean.findOtherThanDefault(this.getContactPhones());
		return phone != null ? phone.getNumber() : null;
	}
	/**
	 * Returns a phone number (if there is more than one it returns one of them randomly)
	 * @return
	 */
	public default Phone getPhoneNumber() {
		// Try to find the default
		Phone phone = this.getDefaultPhoneNumber();
		// ... if there's NO default, return any of them
		if (phone == null) {
			ContactPhone contactPhone = ContactMean.findOne(this.getContactPhones());
			phone = contactPhone != null ? contactPhone.getNumber() : null;
		}
		return phone;
	}
	/**
	 * Returns the no faxes separated with param separator
	 * @param separator
	 * @return
	 */
	public default String getNotFaxPhoneNumbersJoinedWith(final String separator) {
		Collection<Phone> phones = this.getPhoneNumbersMatching(contactPhone -> contactPhone.getType() != null
															  				 && contactPhone.getType().isNOT(ContactPhoneType.FAX));
		return CollectionUtils.hasData(phones)
					? CollectionUtils.of(phones)
									 .toStringSeparatedWith(separator)
				    : null;
	}
	/**
	 * Returns the faxes separated with param separator
	 * @param separator
	 * @return
	 */
	public default String getFaxPhoneNumbersJoinedWith(final String separator) {
		Collection<Phone> phones = this.getPhoneNumbersMatching(contactPhone -> contactPhone.getType() != null
															  				 && contactPhone.getType().is(ContactPhoneType.FAX));
		return CollectionUtils.hasData(phones)
					? CollectionUtils.of(phones)
									 .toStringSeparatedWith(separator)
				    : null;
	}
}