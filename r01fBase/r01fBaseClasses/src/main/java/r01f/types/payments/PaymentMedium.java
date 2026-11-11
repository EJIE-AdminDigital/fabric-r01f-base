package r01f.types.payments;

import r01f.enums.EnumExtended;

public enum PaymentMedium 
 implements EnumExtended<PaymentMedium> {
	PAYMENT_CARD,		// tarjeta de crédito/débito
	ACCOUNT_TRANSFER,	// transferencia bancaria
	BIZUM,				// bizum
	CASH,				// metalico
	DIRECT_DEBIT;		// domiciliación bancaria
}
