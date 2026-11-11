package r01f.types.payments;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.finantial.FinantialIDs.BankAccountID;
import r01f.types.payments.PaymentIDs.IsPaymentMedium;
import r01f.types.payments.PaymentIDs.PaymentCardID;

/**
 * A hint of the payment medium (e.g. the last 4 digits of the credit card, the bank account number, etc)
 * This is just a hint for the user, it doesn't have to be the actual bank account number or credit card number
 */
@MarshallType(as="paymentMediumHint")
public record PaymentMediumHint(String hint) {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public <M extends IsPaymentMedium> PaymentMediumHint(final M medium) {
		this(switch(medium) {
				case BankAccountID bankAccountId -> PaymentMediumHint.bankAccountHint(bankAccountId);
				case PaymentCardID paymentCardId -> PaymentMediumHint.paymentCardHint(paymentCardId);
				default -> throw new IllegalArgumentException("Unsupported payment medium type: " + medium.getClass().getName());
			 });
		
	}
	public PaymentMediumHint(final BankAccountID bankAccountId) {
		this(bankAccountId != null 
					? PaymentMediumHint.bankAccountHint(bankAccountId)
 				    : null);	
	}
	public PaymentMediumHint(final PaymentCardID paymentCardId) {
		this(paymentCardId != null 
					? PaymentMediumHint.paymentCardHint(paymentCardId)
 				    : null);	
	}
	public static PaymentMediumHint of(final BankAccountID bankAccountId) {
		return new PaymentMediumHint(bankAccountId);
	}
	public static PaymentMediumHint of(final PaymentCardID paymentCardId) {
		return new PaymentMediumHint(paymentCardId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static String bankAccountHint(final BankAccountID bankAccountId) {
		return bankAccountId != null 
					? String.format("%s ***** %s",
									bankAccountId.getIBANParts()
												 .bankID(),
									bankAccountId.asString()
								  			 .substring(bankAccountId.asString().length()-6))	// last 6 digits of the bank account number (e.g. "******123456")
 				    : null;	
	}
	public static String paymentCardHint(final PaymentCardID paymentCardId) {
		return paymentCardId != null 
					? String.format("%s ***** %s",
									paymentCardId.getCardType(),
									paymentCardId.asString()
								  			     .substring(paymentCardId.asString().length()-4))	// last 4 digits of the payment card number (e.g. "******123456")
 				    : null;	
	}
}
