package r01f.types;

import java.nio.charset.Charset;

/**
 * Param value encoder / decoder interface
 */
public interface ParamValueEncoderDecoder {
	public String encodeValue(final String value,Charset charset);
	public default String encodeValue(final String value) {
		return this.encodeValue(value,Charset.defaultCharset());
	}
	
	public String decodeValue(final String value,Charset charset);
	public default String decodeValue(final String value) {
		return this.decodeValue(value,Charset.defaultCharset());
	}
}
