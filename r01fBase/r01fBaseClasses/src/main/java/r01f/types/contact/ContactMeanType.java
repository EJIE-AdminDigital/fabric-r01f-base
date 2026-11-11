package r01f.types.contact;

import r01f.enums.EnumExtended;

public enum ContactMeanType
 implements EnumExtended<ContactMeanType> {
	EMAIL,
	PHONE,
	SOCIAL_NETWORK,
	WEB_SITE;
	
	@SuppressWarnings("unchecked")
	public static <C extends IsContactMean> ContactMeanType from(final C contactMean) {
		if (contactMean == null) return null;
		
		C theContactMean = contactMean instanceof OwnedContactMean<?>
								? ((OwnedContactMean<C>)contactMean).getId()
							    : contactMean;
		ContactMeanType outType = null;
		if (theContactMean instanceof EMail) {
			outType = EMAIL;
		} else if (contactMean instanceof Phone) {
			outType = PHONE;
		} else {
			throw new IllegalArgumentException("Contact mean type " + theContactMean.getClass().getName() + " is not supported");
		}
		return outType;
	}
	public static <C extends IsContactMean> ContactMeanType from(final Class<C> contactMeanType) {
		if (contactMeanType == null) return null;
		ContactMeanType outType = null;
		if (EMail.class.isAssignableFrom(contactMeanType)) {
			outType = EMAIL;
		} else if (Phone.class.isAssignableFrom(contactMeanType)) {
			outType = PHONE;
		} else {
			throw new IllegalArgumentException("Contact mean type " + contactMeanType.getName() + " is not supported");
		}
		return outType;
	}
}
