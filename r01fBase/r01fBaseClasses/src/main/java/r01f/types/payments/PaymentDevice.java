package r01f.types.payments;

import r01f.enums.EnumExtended;

public enum PaymentDevice 
 implements EnumExtended<PaymentDevice> {
	WEB_BROWSER,		
	MOBILE_APP,	
	IN_PERSON_FINANCIAL_ENTITY,
	IN_PERSON_OTHER,	
	ATM;
}
