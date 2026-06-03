package r01f.types.money;

import java.util.Currency;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.types.money.Money.MoneyBase;

/**

 ////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] argv) {
		// Create 100 cents of euro.
		MoneyBuilder.create(100)
						.cents()
							.of(MoneyCurrencies.EUR);
	}

 * @author PCI
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MoneyBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static MoneyBuilderBaseStep create(final long amount) {
		return new MoneyBuilder() { /* nothing */ }.new MoneyBuilderBaseStep(amount);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class MoneyBuilderBaseStep {
		private final long _amount;

		public MoneyBuilderBaseStepCurrencyStep cents() {
			return new MoneyBuilderBaseStepCurrencyStep(_amount,MoneyBase.CENTS);
		}
		public MoneyBuilderBaseStepCurrencyStep units() {
			return new MoneyBuilderBaseStepCurrencyStep(_amount,MoneyBase.UNITS);
		}

		public MoneyBuilderBuilderStep euro() {
			return new MoneyBuilderBuilderStep(_amount,MoneyBase.UNITS,MoneyCurrencies.EUR.getCode());
		}
		public MoneyBuilderBuilderStep usd() {
			return new MoneyBuilderBuilderStep(_amount,MoneyBase.UNITS,MoneyCurrencies.USD.getCode());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class MoneyBuilderBaseStepCurrencyStep {
		private final long  _amount;
		private final MoneyBase  _base;

		public MoneyBuilderBuilderStep of(final Currency currency) {
			return new MoneyBuilderBuilderStep(_amount,_base,currency);
		}

		public MoneyBuilderBuilderStep of(final MoneyCurrencies currency) {
			return new MoneyBuilderBuilderStep(_amount,_base,currency.getCode());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class MoneyBuilderBuilderStep {
		private final long  _amount;
		private final MoneyBase  _base;
		private final Currency  _currency;

		public Money build() {
			return new Money(_amount,_base , _currency);
		}
	}

}
