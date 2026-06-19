package r01f.types.money;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.comparable.ComparableObject;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.comparable.ComparableObjectUtils;

/**
 *  [ Working-with-monetary-amounts ]
 *	 --------------------------------
 *	The amount of money, in the smallest denomination of the currency indicated by currency.
 *		For example, when currency is USD or EURO, amount is in cents (this "cents" are indicated as the 'base ' - cents or units -)
 *	Monetary amounts can be positive or negative
 *	// see f.e https://developer.squareup.com/docs/build-basics/common-data-types/working-with-monetary-amounts
 */
@NoArgsConstructor
@Accessors(prefix="_")
public class Money 
  implements Serializable,
			 Debuggable,
			 ComparableObject<Money>,Comparable<Money> {
	
	private static final long serialVersionUID = 3057858118010148006L;	
/////////////////////////////////////////////////////////////////////////////////////////
//	 FIELDS
////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="amount",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private long _amount;
	
	@MarshallField(as="base",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MoneyBase _base;
	
	@MarshallField(as="currency",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Currency _currency;	// ISO 4217 format
	
/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Money(final long amount,final MoneyBase base,final Currency currency) {
		_amount = amount;
		_base = base;
		_currency = currency;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	public enum MoneyOperation {
		ADD,
		SUBTRACT
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	MONEY BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public enum MoneyBase {
		CENTS,
		UNITS
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	UTILITY
/////////////////////////////////////////////////////////////////////////////////////////
	public long getAmountInCents() {
		if (MoneyBase.UNITS.equals(_base)) {
			return _amount * 100;
		}
		return _amount;

	}
	public double getAmountInUnits() {
		if (MoneyBase.UNITS.equals(_base)) {
			return _amount;
		}
		return _amount / 100D;

	}
	public String getAmountFormatted(final Locale currentLocale) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance(currentLocale);
		if (_currency!=null && _base!=null) {
			formatter.setCurrency(_currency);
			return formatter.format(getAmountInUnits());
		}
		return formatter.format(0D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isSameObject(final Money other) {
		return  _amount == other.getAmount()
						 && ComparableObjectUtils.compare(_base,other.getBase())
						 && ComparableObjectUtils.compare(_currency,other.getCurrency());
	}
	@Override
	public int compareTo(final Money o) {	
	    if (o == null) {
	        return 1;
	    }	  
	    if (this.isSameObject(o)) {
	        return 0;
	    } else {
	    	return 1;
	    }

	  
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder("money  >> ");
		sb.append(_amount)
		  .append(" ")
		  .append(_currency.getDisplayName() )
		  .append("(" + _currency.getSymbol() + ")  ")
		  .append(" [" + _base + "]  ");
		return sb.toString();
	}
	
}
