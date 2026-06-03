package r01f.crypto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MOD11 {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Computes the MOD-11 control digit for a given string of digits and weights.
	 * For a 10 digit input string, the weights are applied as follows:
	 * <pre class='brush:java'>
	 * 		int mod11 = MOD11.computeMod11("1234567890", 								// input string
	 * 									   new int[] {1, 2, 4, 8, 5, 10, 9, 7, 3, 6},	// weights
	 * 									   false);										// do not cycle weights, input string length must match weights length
	 * </pre>
	 * @param str
	 * @param weights
	 * @param cycleWeights
	 * @return
	 */
	public static int computeMod11(final String str,
								   final int[] weights,final boolean cycleWeights) {
		if (str == null || !str.matches("\\d+")) throw new IllegalArgumentException("input string must contain only digits");
		if (str.length() != weights.length
		 && !cycleWeights) throw new IllegalArgumentException("input string length must match weights length");

        int sum = 0;

        // Multiply every digit of the input string by it's correspondent weight
        int weightIndex = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            int currDigit = Character.getNumericValue(str.charAt(i));
            sum += currDigit * weights[weightIndex];
            
            weightIndex++;

            // cyclic weights
            if (weightIndex >= weights.length) weightIndex = 0;
        }

        // Module 11
        int out = 11 - sum % 11;

        // Standard AEB/CSB adjustments
        if (out == 10) return 1;
        if (out == 11) return 0;
        
        return out;
    }
}
