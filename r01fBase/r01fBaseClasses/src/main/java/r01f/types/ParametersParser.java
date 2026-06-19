package r01f.types;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Parameters parser interface 
 */
public interface ParametersParser {
	public ParamValueEncoderDecoder getParamValueEncoderDecoder(final Charset charset);
	public default ParamValueEncoderDecoder getParamValueEncoderDecoder() {
		return this.getParamValueEncoderDecoder(Charset.defaultCharset());
	}
	
	public Map<String,String> parse(final String paramsStr,
									final boolean decodeParamValues);
}