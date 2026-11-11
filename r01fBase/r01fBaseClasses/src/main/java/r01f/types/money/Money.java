package r01f.types.money;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import lombok.Getter;
import r01f.debug.Debuggable;
import r01f.enums.EnumExtended;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 *  [ Working-with-monetary-amounts ]
 *	--------------------------------
 *	The amount of money, in the smallest denomination of the currency indicated by currency.
 *	For example, when currency is USD or EURO, amount is in cents (this "cents" are indicated as the 'base ' - cents or units -)
 *	Monetary amounts can be positive or negative
 *	// see f.e https://developer.squareup.com/docs/build-basics/common-data-types/working-with-monetary-amounts
 */
public record Money(@MarshallField(as="amount",
				   				   whenXml=@MarshallFieldAsXml(attr=true))
					@Getter long amount,
	
					@MarshallField(as="base",
								   whenXml=@MarshallFieldAsXml(attr=true))
					@Getter MoneyBase base,
					
					@MarshallField(as="currency",
								   whenXml=@MarshallFieldAsXml(attr=true))
				    @Getter Currency currency)	// ISO 4217 format)				 
   implements Comparable<Money>,
			  Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
// 	OPERATION
/////////////////////////////////////////////////////////////////////////////////////////
	public enum MoneyOperation 
	 implements EnumExtended<MoneyOperation> {
		ADD,
		SUBTRACT
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	MONEY BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public enum MoneyBase 
	 implements EnumExtended<MoneyBase> {
		CENTS,
		UNITS
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	UTILITY
/////////////////////////////////////////////////////////////////////////////////////////
	public long getAmountInCents() {
		if (MoneyBase.UNITS.equals(this.base)) return this.amount * 100;
		return this.amount;

	}
	public double getAmountInUnits() {
		if (MoneyBase.UNITS.equals(this.base)) return this.amount;
		return this.amount / 100D;

	}
	public String getAmountFormatted(final Locale currentLocale) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance(currentLocale);
		if (this.currency!=null && this.base!=null) {
			formatter.setCurrency(this.currency);
			return formatter.format(getAmountInUnits());
		}
		return formatter.format(0D);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final Money other) {
		return this.equals(other);
	}
	public boolean isNOT(final Money other) {
		return !this.equals(other);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public int compareTo(final Money o) {	
	    if (o == null) return 1;
	    if (this.currency.equals(o.currency)) throw new IllegalArgumentException("Cannot compare money with different currencies: " + this.currency + " vs " + o.currency);
	    
	    Long thisAmountInCents = this.getAmountInCents();
	    Long otherAmountInCents = o.getAmountInCents();
	    return thisAmountInCents.compareTo(otherAmountInCents);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder("money  >> ");
		sb.append(this.amount)
		  .append(" ")
		  .append(this.currency.getDisplayName() )
		  .append("(" + this.currency.getSymbol() + ")  ")
		  .append(" [" + this.base + "]  ");
		return sb.toString();
	}
	
}
