package r01f.types.contact;

import java.util.Collection;

import r01f.facets.Facet;
import r01f.util.types.collections.CollectionUtils;

public interface HasPhoneNumbers
		 extends Facet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public Collection<Phone> getPhoneNumbers();
	
	public default boolean hasPhoneNumbers() {
		return CollectionUtils.hasData(this.getPhoneNumbers());
	}
	/**
	 * Checks if there exists a {@link ContactPhone} with the given number
	 * @param thePhone
	 * @return
	 */
	public default boolean hasPhoneNumber(final Phone thePhone) {
		if (hasPhoneNumbers()) {
			return this.getPhoneNumbers()
					   .stream()
					   .filter(aPhone -> aPhone.equals(thePhone))
					   .count() > 0;
		}
		return false;
	}
	public default Phone getFirstPhoneNumber() {
		return CollectionUtils.hasData(this.getPhoneNumbers()) ? this.getPhoneNumbers().iterator().next() : null;
	}
	public default String getPhoneNumbersAsString() {
		return this.getPhoneNumbersJoinedWith("; ");
	}
	/**
	 * Returns the mail addresses as a comma separated list
	 * @param separator
	 * @return
	 */
	public default String getPhoneNumbersJoinedWith(final String separator) {
		Collection<Phone> phones = this.getPhoneNumbers();
		return CollectionUtils.hasData(phones)
					? CollectionUtils.of(phones)
									 .toStringSeparatedWith(separator)
				    : null;
	}
}
