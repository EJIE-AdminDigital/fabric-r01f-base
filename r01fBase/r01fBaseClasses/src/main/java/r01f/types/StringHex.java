package r01f.types;


import java.util.function.Function;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;

@MarshallType(as="stringHex")
public record StringHex(String stringHex)
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 6567130928730254124L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,String> DEFAULT_ENCODER_FUNCION = input -> StringEncodeUtils.encodeHex(input.getBytes());
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public StringHex(final String stringHex) {
		if (Strings.isNullOrEmpty(stringHex)) throw new IllegalArgumentException();
		this.stringHex = stringHex.toUpperCase();
	}
	public static StringHex fromHexEncodedString(final String stringHex) {
		if (!isHexNumber(stringHex)) {
			throw new IllegalArgumentException(Strings.customized("the raw string '{}' "
																	+ "not represents  hex encoded string!!!!", stringHex));
		}
		return new StringHex(stringHex);
	}
	public static StringHex encode(final String rawString) {
		return StringHex.encode(rawString,
								DEFAULT_ENCODER_FUNCION);
	}
	public static StringHex encode(final String rawString,
							  	   final Function<String,String> encoder) {
		if (rawString == null || encoder == null) {
			throw new IllegalArgumentException();
		}
		return new StringHex(encoder.apply(rawString));
	}
	public static StringHex encode(final byte[] rawBytes) {
		if (rawBytes == null || rawBytes.length == 0) {
			throw new IllegalArgumentException();
		}
 		return new StringHex(StringEncodeUtils.encodeHex(rawBytes));
	}	
	public static boolean isHexNumber (final String hexEncodedString) {		
		return hexEncodedString.matches("^[0-9a-fA-F]+$");
		
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.stringHex;
	}
	@Override
	public String toString() {
		return  asString();
	}
	public byte[] getBytes() {
		return this.stringHex.getBytes();
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	public String decode() {
		return StringEncodeUtils.decodeHex(this.stringHex.toCharArray())
								.toString();
	}
	public byte[] decodeAsBytes() {
		return StringEncodeUtils.decodeHex(this.stringHex.toCharArray());
	}
}
