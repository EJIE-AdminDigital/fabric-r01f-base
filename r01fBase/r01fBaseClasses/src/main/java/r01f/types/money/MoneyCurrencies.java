package r01f.types.money;

import java.util.Currency;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumWithCode;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum MoneyCurrencies
 implements EnumWithCode<Currency,MoneyCurrencies> {

	EUR (Currency.getInstance("EUR") ), // ISO 4217 format
	USD (Currency.getInstance("USD") );

	@Getter private final Class<Currency> _codeType = Currency.class;
	@Getter private final Currency _code;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static MoneyCurrencies fromName(final String name) {
		return EnumExtended.fromName(name,MoneyCurrencies.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + MoneyCurrencies.class + " element with name=" + name));
	}
	public static MoneyCurrencies from(final Currency code) {
		return EnumWithCode.fromCode(code,MoneyCurrencies.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + MoneyCurrencies.class + " element with code=" + code));
	}
}
