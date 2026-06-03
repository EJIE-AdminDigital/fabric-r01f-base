package r01f.util.types;

import java.text.NumberFormat;

public class Currencies {
	/**
	 * Formats a money amount
	 * @param money
	 * @return
	 */
	public static String formatMoney(final double money) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(money);
	}
}
