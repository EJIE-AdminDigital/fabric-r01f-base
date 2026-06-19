package r01f.types;

import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Default parameters parser using regex
 */
abstract class ParametersParserBase 
    implements ParametersParser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ParamValueEncoderDecoder getParamValueEncoderDecoder(final Charset charset) {
		return new ParamValueEncoderDecoder() {
						@Override
						public String encodeValue(final String value,final Charset charset) {
							return _urlEncodeNoThrow(value,charset).toString();
						}
						@Override
						public String decodeValue(final String value,final Charset charset) {
							return _urlEncodeNoThrow(value,charset).toString();
						}
			   };
	}
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * it does NOT throw any exception if the encoding cannot be done
	 * @param str
	 * @return
	 */
	private static CharSequence _urlEncodeNoThrow(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String outEncoded = URLEncoder.encode(str.toString(),charset);
		return outEncoded;
	}
}
