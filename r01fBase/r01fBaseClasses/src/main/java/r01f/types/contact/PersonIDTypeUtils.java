package r01f.types.contact;

public class PersonIDTypeUtils {

   
    private static final String SPANISH_NIF_NIE_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	    
	public static boolean isValidIdentityDocument(final PersonID personId) {	
		return isValidIdentityDocument(personId.asString());
	}
	  /**
     * Checks if the document is a valid DNI or NIE.
     */
    public static boolean isValidIdentityDocument(final String doc) {
        if (doc == null) return false;

        String cleanDoc = doc.toUpperCase().trim();
        return _isValidDNI(cleanDoc) || _isValidNIE(cleanDoc);
    }

  
    public static boolean _isValidDNI(String dni) {
        if (dni == null) return false;
        
        // 1. Trim & UpperCase
        String cleanDni = dni.trim().toUpperCase();

        // 2. Check format  8 digits, 1 letter.
        if (!cleanDni.matches("\\d{8}[A-Z]")) {
            return false;
        }

        // 3. Extract
        int number = Integer.parseInt(cleanDni.substring(0, 8));
        char providedLetter = cleanDni.charAt(8);

        // 4. Compute letter     
        char expectedLetter = SPANISH_NIF_NIE_LETTERS.charAt(number % 23);
        return providedLetter == expectedLetter;
    }
    
    

    /**
     * Checks if a NIE is valid.
     * Format: starts with X, Y or Z, followed by 7 digits and 1 letter (e.g., X1234567L)
     */
    public static boolean _isValidNIE(final String nie) {
        if (nie == null || !nie.matches("[XYZ]\\d{7}[A-Z]")) {
            return false;
        }

        // Replace the first letter with the corresponding numeric value
        char firstLetter = nie.charAt(0);
        String nieNumeric = switch (firstLetter) {
            case 'X' -> "0" + nie.substring(1);
            case 'Y' -> "1" + nie.substring(1);
            case 'Z' -> "2" + nie.substring(1);
            default -> throw new IllegalArgumentException("Invalid NIE");
        };

        int number = Integer.parseInt(nieNumeric.substring(0, 8));
        char correctLetter = SPANISH_NIF_NIE_LETTERS.charAt(number % 23);
        char nieLetter = nie.charAt(8);

        return correctLetter == nieLetter;
    }

  

    public static PersonIDType resolveDocumentType(final String docRaw) {
        if (docRaw == null) return PersonIDType.UNKNOWN;

        String doc = docRaw.trim().toUpperCase();

        if (doc.matches("^\\d{8}[A-Z]$")) return PersonIDType.SPANISH_NIF;

        return PersonIDType.UNKNOWN;
    }
///////////////////////////////////////////////////////
/// public
///////////////////////////////////////////////////////
   /* public static void main(String[] argv){
    	
    	DN00PersonID id = DN00PersonID.forId("90318409P");
    	System.out.println(isValidDocument(id));
    }
*/

}
