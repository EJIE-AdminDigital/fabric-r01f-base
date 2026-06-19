package r01f.types.contact;

import r01f.guids.OID;
import r01f.util.types.Strings;

public interface IsPersonID 
	     extends OID {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public String getId();
/////////////////////////////////////////////////////////////////////////////////////////
//	String conversion
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public default String asString() {
		return this.getId();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ID TYPE
/////////////////////////////////////////////////////////////////////////////////////////	
	public default PersonIDType getIdType() {
		return PersonIDType.from(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SPANISH NIF validation & type
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if the person id is a valid spanish NIF (DNI, NIE or CIF)
	 * @return
	 */
	public default boolean isValidSpanishNIF() {
		return new NIFValidator()
						.validate(this)
						.isValid();
	}
	/**
	 * Checks if the person id is a valid spanish NIF (DNI, NIE or CIF)
	 * @param personId
	 * @return
	 */
	public static boolean isValidSpanishNIF(final PersonID personId) {
		return personId.isValidSpanishNIF();
	}
	/**
	 * Checks if the person id is a valid spanish NIF (DNI, NIE or CIF)
	 * @param personIdStr
	 * @return
	 */
	public static boolean isValidSpanishNIF(final String personIdStr) {
		return NIFValidator.isValidNIF(personIdStr);
	}
	/**
	 * If it's a valid spanish NIF, returns the NIF type (DNI, NIE or CIF), otherwise throws an IllegalArgumentException
	 * @return
	 */
	public default NIFType getSpanishNIFType() {
		return NIFType.from(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PASSPORT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * There's NO single (universal) format for passport numbers, as each country has its own format.
	 * However, most passport numbers are alphanumeric and have a length between 6 and 9 characters.
	 * (see ICAO 9303 standard for machine-readable travel documents and [JMRTD=Java Machine Readable Travel Documents library])
	 * @return
	 */
	public default boolean canBePassport() {
		String id = this.getId();
		return IsPersonID.canBePassport(id);
	}
	/**
	 * There's NO single (universal) format for passport numbers, as each country has its own format.
	 * However, most passport numbers are alphanumeric and have a length between 6 and 9 characters.
	 * (see ICAO 9303 standard for machine-readable travel documents and [JMRTD=Java Machine Readable Travel Documents library])
	 * @param personId
	 * @return
	 */
	public static boolean canBePassport(final PersonID personId) {
		return personId.canBePassport();
	}
	/**
	 * There's NO single (universal) format for passport numbers, as each country has its own format.
	 * However, most passport numbers are alphanumeric and have a length between 6 and 9 characters.
	 * (see ICAO 9303 standard for machine-readable travel documents and [JMRTD=Java Machine Readable Travel Documents library])
	 * @param id
	 * @return
	 */
	public static boolean canBePassport(final String id) {
		return Strings.isNOTNullOrEmpty(id) && id.length() >= 6 && id.length() <= 9
			&& id.replaceAll("[\\s-]","")		// remove spaces and dashes
				 .toUpperCase()					// convert to upper case
				 .matches("^[A-Z0-9]{6,9}$");	// matches 99% of passport numbers (alphanumeric, 6 to 9 characters)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static String normalize(final String id) {
		return id.replaceAll("[^0-9a-zA-Z]","");	// Remove all non digit or letters
	}	
}
