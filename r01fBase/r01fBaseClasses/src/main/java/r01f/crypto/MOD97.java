package r01f.crypto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MOD97 {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
     * Computes the MOD-97 of a string.
     * Converts letters to digits (A=10...Z=35) and processes the result as a large number.
     * 
     * @param str The string to process
     * @return The remainder after MOD-97
     */
    public static int computeMod97(final String input) {
    	// if the input string is ALL digits there's a java simpler alternative:
    	// int mod97 = new BigInteger(input).mod(BigInteger.valueOf(97))
    	//									.intValue();
    	
        long remainder = 0;
        String upperInput = input.toUpperCase();
        
        for (int i = 0; i < upperInput.length(); i++) {
            char c = upperInput.charAt(i);
            
            // Character.getNumericValue handles 0-9 and A-Z (10-35)
            int value = Character.getNumericValue(c);

            // Skip characters that are not alphanumeric (like spaces or dashes)
            if (value < 0) continue;

            if (value < 10) {
                // It's a single digit (0-9)
                remainder = (remainder * 10 + value) % 97;
            } else {
                // It's a letter (10-35). Process as two separate digits.
                // Example: 'E' is 14. We process 1 then 4.
                remainder = (remainder * 10 + (value / 10)) % 97;
                remainder = (remainder * 10 + (value % 10)) % 97;
            }
        }
        return (int)remainder;
    }
}
