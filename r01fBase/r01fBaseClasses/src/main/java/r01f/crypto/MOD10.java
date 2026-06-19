package r01f.crypto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MOD10 {
	/**
     * Computes the Mod10 of a given string (Luhn Algorithm)
     * asumming that the check digit (Mod10) will be the last digit of the string
     * The Luhn algorithm works like this:
     * 		[1] - doubling (multiply by 2) every EVEN digit counting from the right
     * 		[2] - if the doubling results in a number greater than 9, subtract 9 from it
     * 		[3] - sum all the digits (after doubling and adjusting)
     * 		[4] - the check digit is whatever number you need to add to the sum
	 *			  to make it a multiple of 10
     * BEWARE!!
	 *		If the check digit is INCLUDED in the input string, 
	 *		the doubling starts from the second to last digit (the last digit is the check digit)
 	 * 		BUT if the check digit is INCLUDED the routine ALWAYS return 0 
	 *		because the check digit is already included in the input string 
	 *		and the sum will be a multiple of 10
     * 
     * @param str the string to compute the Mod10 for asumming that the last digit will be the check digit
     * @return the Mod10 of the given string
     */
    public static boolean checkMod10(final String str) {
    	String strWithoutCheckDigit = str.substring(0,str.length()-1);
    	int checkDigit = computeMod10(strWithoutCheckDigit);
    	return checkDigit == Character.getNumericValue(str.charAt(str.length()-1));
    }
	/**
     * Computes the Mod10 of a given string (Luhn Algorithm)
     * asumming that the check digit (Mod10) will be the last digit of the string
     * The Luhn algorithm works like this:
     * 		[1] - doubling (multiply by 2) every EVEN digit counting from the right
     * 		[2] - if the doubling results in a number greater than 9, subtract 9 from it
     * 		[3] - sum all the digits (after doubling and adjusting)
     * 		[4] - the check digit is whatever number you need to add to the sum
	 *			  to make it a multiple of 10
     * BEWARE!!
	 *		If the check digit is INCLUDED in the input string, 
	 *		the doubling starts from the second to last digit (the last digit is the check digit)
 	 * 		BUT if the check digit is INCLUDED the routine ALWAYS return 0 
	 *		because the check digit is already included in the input string 
	 *		and the sum will be a multiple of 10
     * 
     * @param str the string to compute the Mod10 for asumming that the last digit will be the check digit
     * @return the Mod10 of the given string
     */
    public static int computeMod10(final String str) {
    	int sum = 0;
    	
        boolean shouldDouble = true;	// the check digit is NOT INCLUDED in the input string

        // Iterate from right to left
        for (int i = str.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(str.substring(i, i + 1));	// pick the digit
            if (shouldDouble) {
                n *= 2;				// [1] - double (multiply by 2) every even digit counting from the right
                if (n > 9) n -= 9;	// [2] - if the doubling results in a number greater than 9, subtract 9 from it
            }
            sum += n;				// [3] - sum all the digits (after doubling and adjusting)
            
            shouldDouble = !shouldDouble;	// flip the flag for the next digit (only the even digits are doubled)
        }

        // [4] - The check digit is whatever makes the sum divisible by 10
        int mod = sum % 10;
        return (mod == 0) ? 0 
        				  : (10 - mod);
    }
}
