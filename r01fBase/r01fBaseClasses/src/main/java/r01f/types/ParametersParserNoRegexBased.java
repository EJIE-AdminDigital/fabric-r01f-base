package r01f.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

import com.google.common.base.Splitter;

import r01f.util.types.Strings;

/**
 * Default parameters parser using regex
 */
public class ParametersParserNoRegexBased 
     extends ParametersParserBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Map<String,String> parse(final String paramsStr,
									final boolean decodeParamValues) {
		if (Strings.isNullOrEmpty(paramsStr)) return null;
		
		Map<String,String> paramMap = new HashMap<>();
		Iterable<String> params = Splitter.on(ParametersWrapperBase.DEFAULT_PARAM_SPLIT_CHAR)
										  .split(paramsStr.trim());
		Iterator<String> paramsIt = params.iterator();
		while (paramsIt.hasNext()) {
			String param = paramsIt.next();
			Iterable<String> paramNameAndValueIt = Splitter.on('=')
										  				   .split(paramsStr.trim());
			String[] paramNameAndValue = StreamSupport.stream(paramNameAndValueIt.spliterator(),
															  false)
													  .toArray(String[]::new);
			if (paramNameAndValue.length == 2) {
				String paramName = paramNameAndValue[0].trim();
				String paramValue = paramNameAndValue[1].trim();
				String theParamValue = decodeParamValues ? this.getParamValueEncoderDecoder().decodeValue(paramValue)
														 : paramValue;
				paramMap.put(paramName,theParamValue);
				
			} else if (paramNameAndValue.length == 1) {
				String paramName = param;
				String theParamValue = "";
				
				paramMap.put(paramName,theParamValue);
			} 
		}
		return paramMap;
	}
}
