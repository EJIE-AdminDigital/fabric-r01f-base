package r01f.types.finantial;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.crypto.MOD97;
import r01f.locale.Country;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OID;
import r01f.types.payments.PaymentIDs.IsPaymentMedium;
import r01f.types.payments.PaymentMediumHint;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FinantialIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE INTERFACES
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsFinancialObjectID
		     extends OID {
		public String id();
		
		@Override
		public default String asString() {
			return this.id();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BANK
/////////////////////////////////////////////////////////////////////////////////////////	
////////// Bank
	@MarshallType(as="bankId")
	@Immutable
	public record BankID(String id)
	   implements IsFinancialObjectID {
		
		public static BankID valueOf(final String s) {
			return new BankID(s);
		}
		public static BankID forId(final String id) {
			return new BankID(id);
		}
		@Override
		public String toString() {
			return this.id;
		}
	    public static final Function<CharSequence,BankID> FROM_STRING_FUNC = str -> BankID.forId(str.toString());
	}
////////// Bank Account
	@MarshallType(as="bankAccountId")
	@Immutable
	public record BankAccountID(String id)
	   implements IsFinancialObjectID,
				  IsPaymentMedium {
		
		public static BankAccountID from(final String s) {
			return new BankAccountID(s);
		}
		public static BankAccountID valueOf(final String s) {
			return new BankAccountID(s);
		}
		public static BankAccountID forId(final String id) {
			return new BankAccountID(id);
		}
		@Override
		public String toString() {
			return this.id;
		}
	    public static final Function<CharSequence,BankAccountID> FROM_STRING_FUNC = str -> BankAccountID.forId(str.toString());
	
	    public boolean isValidIBAN() {
	    	return IBANAccountParts.isValidIBAN(this.id);
	    }
	    public String asIBANString() {
	    	return BankAccountID.asIBANString(this.id);
	    }
	    public IBANAccountParts getIBANParts() {
	    	return IBANAccountParts.parse(this.id);
	    }
	    public PaymentMediumHint getHint() {
	    	return PaymentMediumHint.of(this);
	    } 
	    /**
	     * Formats a raw IBAN string into groups of four separated by spaces.
	     * Example: "DE12345678901234567890" -> "DE12 3456 7890 1234 5678 90"
	     *
	     * @param iban The raw IBAN string (with or without existing spaces)
	     * @return A formatted IBAN string
	     */
	    public static String asIBANString(final String iban) {
	        // [1] - Remove any existing whitespace to start with a clean string
	        String cleanIban = iban.replace(" ","");
	        StringBuilder formatted = new StringBuilder();
	
	        // [2] - Loop through the string and insert a space every 4 characters
	        for (int i = 0; i < cleanIban.length(); i++) {
	            if (i > 0 && i % 4 == 0) {
	                formatted.append(" ");
	            }
	            formatted.append(cleanIban.charAt(i));
	        }
	        return formatted.toString()
	        				.toUpperCase();
	    }
		public static BankAccountID[] TEST_IBANs = new BankAccountID[] {
															    // Spanish IBANs
															    new BankAccountID("ES9121000418450200051332"),
															
															    // German IBANs
															    new BankAccountID("DE89370400440532013000"),
															
															    // French IBANs
															    new BankAccountID("FR1420041010050500013M02606"),
															
															    // Italian IBANs
															    new BankAccountID("IT60X0542811101000000123456"),
															
															    // Belgian IBANs
															    new BankAccountID("BE68539007547034"),
															
															    // Netherlands IBANs
															    new BankAccountID("NL91ABNA0417164300"),
															
															    // Portuguese IBANs
															    new BankAccountID("PT50000201231234567890154"),
															
															    // Austrian IBANs
															    new BankAccountID("AT611904300234573201"),
															
															    // Irish IBANs
															    new BankAccountID("IE29AIBK93115212345678"),
															
															    // Finnish IBANs
															    new BankAccountID("FI2112345600000785"),
															
															    // Greek IBANs
															    new BankAccountID("GR1601101250000000012300695")
														};
	}
    /**
     * An IBAN (International Bank Account Number) is like:
     * number of digits | meaning
     * -----------------+-----------------------------------------------
     * 2 letters (upper)| country code (ES | EN | FR | DE...)
     * 2 numbers        | check digits (MOD-97 of the rest of the IBAN
     * (variable length)| Bank identifier (depends on the country)
     * (variable length)| BBAN (basic bank account) -the account number-
     */
	@MarshallType(as="ibanParts")
	public record IBANAccountParts(@MarshallField(as="country",whenXml=@MarshallFieldAsXml(attr=true))
								   Country country,
								   @MarshallField(as="checkCode",whenXml=@MarshallFieldAsXml(attr=true))
								   int checkCode,
								   @MarshallField(as="bankId",whenXml=@MarshallFieldAsXml(attr=true))
								   BankID bankID,
								   @MarshallField(as="account",whenXml=@MarshallFieldAsXml(attr=true))
								   String accountNumber) {
		public static IBANAccountParts parse(final String iban) {
	        // [1] - Remove any existing whitespace to start with a clean string
	        String cleanIban = iban.replace(" ","");
	        
	        String countryCode = null;
	        String checkCodeStr = null;
	        String bankIdStr = null;
	        String accountNumberStr = null;
	        Matcher m = IBAN_PATTERN.matcher(cleanIban);
	        if (m.find()) {
	        	countryCode = m.group(1);
	        	checkCodeStr = m.group(2);
	        	bankIdStr = m.group(3);
	        	accountNumberStr = m.group(4);
	        } else {
	        	throw new IllegalArgumentException("iban=" + iban + " is NOT valid: does not match the IBAN format");
	        }
	        
	        if (!Country.canBeFromISO3166Alpha1Code(countryCode)) throw new IllegalArgumentException("iban=" + iban + " is NOT valid: the country code=" + countryCode + " is NOT valid!");
	        if (!Numbers.isInteger(checkCodeStr)) throw new IllegalArgumentException("iban " + iban + " is NOT valid: the check code=" + checkCodeStr + " is NOT valid!");
	        if (Strings.isNullOrEmpty(bankIdStr)) throw new IllegalArgumentException("iban=" + iban + " is NOT valid: the bank code=" + bankIdStr + " is NOT valid");
	        if (Strings.isNullOrEmpty(accountNumberStr)) throw new IllegalArgumentException("iban=" + iban + " is NOT valid: the account number='" + accountNumberStr + "' is NOT valid");
	        
	        return new IBANAccountParts(Country.fromISO3166Alpah1Code(countryCode),
	        						 	Integer.parseInt(checkCodeStr),
	        						 	BankID.forId(bankIdStr),
	        						 	accountNumberStr);
		}
		public static boolean isValidIBAN(final String iban) {
			boolean validFormat = Strings.isNOTNullOrEmpty(iban)
							   && IBAN_PATTERN.matcher(iban.replace(" ",""))
										   	  .matches();
			if (!validFormat) return false;
			try {
				IBANAccountParts ibanParts = IBANAccountParts.parse(iban);
				// try to compute the check code and compare with the one in the IBAN
				String ibanToValidate = String.format("%s00%s%s",	// the "00" is a placeholder for the check code to be computed
													  ibanParts.country.getIso3166_2(),
													  ibanParts.bankID,
													  ibanParts.accountNumber);
				int computedCheckCode = IBANAccountParts.computeCheckDigitsForIBAN(ibanToValidate);
				return computedCheckCode == ibanParts.checkCode;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	    /**
	     * The IBAN standard (ISO 7064) requires a specific rearrangement before the MOD-97 computing
	     * 	- IBAN Logic: the first 4 characters must be moved to the end of the string.
		 *  - General Logic: processes the string from left to right exactly as it is typed.
		 * To get the same result when validating an IBAN generated with this routing
		 * the iban number has to be rearranged
		 * 
	     * IMPORTANT:
	     *   - VALIDATION OF AN EXISTING IBAN: The result of MOD-97 must be 1
	     *   - GENERATION OF CHECK DIGITS OF AN IBAN: set the check digits to 00, perform the calculation
	     * @param iban
	     * @return
	     */
	    public static int computeCheckDigitsForIBAN(final String iban) {
	    	// Step 1: Rearrange for IBAN standard
			String rearranged = iban.substring(4) + 	// the part after the first 4 characters
								iban.substring(0, 4);	// the first 4 characters (country code + check digits=00)
			
			// Step 2: Use the General Routine
			//		   The standard ISO 7064 formula to generate the digits is:
			//		  	 checkDigits = 98 - (MOD-97 of the rearranged IBAN with check digits set to 00)
			return 98 - MOD97.computeMod97(rearranged);
	    }
	}
	public static final Pattern IBAN_PATTERN = Pattern.compile("^" + 
	        									  			   "([A-Z]{2})" + 			// country code
	        									  			   "([0-9]{2})" + 			// check digits
	        									  			   "([A-Z0-9]{4})" + 		// bban (bankid)
	        									  			   "([0-9]{7}[A-Z0-9]{0,16})" +	// account number 
															   "$");
}
