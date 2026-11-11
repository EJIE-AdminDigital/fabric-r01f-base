package r01f.types.payments;

import r01f.enums.EnumExtended;


public enum PaymentStateCode 
 implements EnumExtended<PaymentStateCode> {
	COMPLETED,
	PENDING,
	REJECTED_BY_PAYMENT_PROCESSOR,
	CANCELLED_BY_ISSUER,
	ERROR_WHEN_PROCESSING_PAYMENT;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean isCompleted() {
		return this == COMPLETED;
	}
	public boolean isError() {
		return this == REJECTED_BY_PAYMENT_PROCESSOR
			|| this == CANCELLED_BY_ISSUER
			|| this == ERROR_WHEN_PROCESSING_PAYMENT;
	}
}
