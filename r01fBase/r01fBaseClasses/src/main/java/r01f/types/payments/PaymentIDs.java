package r01f.types.payments;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.crypto.MOD10;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.OID;
import r01f.util.types.Strings;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public abstract class PaymentIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE INTERFACES
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsPaymentObjectID
		     extends OID {
		public String id();
		
		@Override
		public default String asString() {
			return this.id();
		}
	}
	public interface IsPaymentMedium {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PAYMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="paymentProcessorId")
	@Immutable
	public record PaymentProcessorID(String id)
	   implements IsPaymentObjectID {
		
		public static PaymentProcessorID valueOf(final String s) {
			return new PaymentProcessorID(s);
		}
		public static PaymentProcessorID forId(final String id) {
			return new PaymentProcessorID(id);
		}
		@Override
		public String toString() {
			return this.id;
		}
	    public static final Function<CharSequence,PaymentProcessorID> FROM_STRING_FUNC = str -> PaymentProcessorID.forId(str.toString());
	}
	@MarshallType(as="paymentTransactionId")
	@Immutable
	public record PaymentTransactionID(String id)
	   implements IsPaymentObjectID {
		
		public static PaymentTransactionID valueOf(final String s) {
			return new PaymentTransactionID(s);
		}
		public static PaymentTransactionID forId(final String id) {
			return new PaymentTransactionID(id);
		}
		@Override
		public String toString() {
			return this.id;
		}
	    public static final Function<CharSequence,PaymentTransactionID> FROM_STRING_FUNC = str -> PaymentTransactionID.forId(str.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CARDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="paymentCardId")
	@Immutable
	public record PaymentCardID(String id)
       implements IsPaymentObjectID,
				  IsPaymentMedium {
		
		public static PaymentCardID valueOf(final String s) {
			return new PaymentCardID(s);
		}
		public static PaymentCardID forId(final String id) {
			return new PaymentCardID(id);
		}
	    public static final Function<CharSequence,PaymentCardID> FROM_STRING_FUNC = str -> PaymentCardID.forId(str.toString());
	    
	    
	    @Override
		public boolean isValid() {
	    	return Strings.isNOTNullOrEmpty(this.id())
	    		&& this.getCardType() != null
	    		&& MOD10.checkMod10(this.asStringSanitized());
	    }
	    public String asStringSanitized() {
	    	return PaymentCardID.sanitizeCardNumber(this.id());
	    }
	    public PaymentCardType getCardType() {
	    	return PaymentCardType.detectCardTypeOf(this)
    								  .orElse(null);
	    }
	    public static String sanitizeCardNumber(final String cardNumber) {
	    	return Strings.isNullOrEmpty(cardNumber) ? cardNumber 
	    											 : cardNumber.trim()
																 .replaceAll("\\D","");	// Remove any non-digit characters (spaces, hyphens)
	    }
	    public PaymentMediumHint getHint() {
	    	return PaymentMediumHint.of(this);
	    } 
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public enum PaymentCardType {
		VISA(List.of(Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$"))), 		 		// starts with 4, length 13 or 16
		MASTERCARD(List.of(Pattern.compile("^5[1-5][0-9]{14}$"))), 			 		// starts with 51-55, length 16
		DISCOVER(List.of(Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$"))), 		// starts with 6011 or 65, length 16
		AMEX(List.of(Pattern.compile("^3[47][0-9]{13}$"))), 						// starts with 34 or 37, length 15
		JCB(List.of(Pattern.compile("^(?:2131|1800|35\\d{3})\\d{11}$"))), 			// starts with 2131, 1800, or 35 followed by 3 digits, length 15 or 16
		DINERS_CLUB(List.of(Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{11}$")));	// starts with 300-305, 36, or 38, length 14
		
		@Getter private final Collection<Pattern> _matchingPatterns;
		
		public static Optional<PaymentCardType> detectCardTypeOf(final PaymentCardID cardNumber) {
			return EnumSet.allOf(PaymentCardType.class)
						  .stream()
						  .filter(type -> type.getMatchingPatterns().stream()
								 			  .anyMatch(pattern -> pattern.matcher(cardNumber.asStringSanitized())
								 					  					  .matches()))
						  .findFirst();
		}
	}
}
