package r01f.types.url;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.patterns.Provider;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.ParametersParser;
import r01f.types.ParametersParserRegexBased;
import r01f.types.ParametersWrapperBase;
import r01f.util.types.StringConverter.StringConverterFilter;
import r01f.util.types.collections.CollectionUtils;


/**
 * Helper type to build url-string-encoded parameters that encapsulates all the string building stuff offering an api
 * that isolates user from string concat errors
 * @see ParametersWrapperBase
 */
@MarshallType(as="urlQueryString")
@Immutable
public class UrlQueryString
	 extends ParametersWrapperBase<UrlQueryString>
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 7152611686021530479L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlQueryString(final Map<String,String> params,
						  final ParametersParser paramsParser) {
		super(params,
			  paramsParser);
	}
	public UrlQueryString(final Map<String,String> params) {
		super(params);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static ParametersWrapperFactory<UrlQueryString> FACTORY = UrlQueryString::new;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new instance from a param {@link Map}
	 * @param params
	 * @return
	 */
	public static UrlQueryString from(final Map<String,String> params) {
		return new UrlQueryString(params);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final Iterable<UrlQueryStringParam> params) {
		Map<String,String> paramsMap = new HashMap<>();
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final Collection<UrlQueryStringParam> params) {
		if (CollectionUtils.isNullOrEmpty(params)) return new UrlQueryString(null);
		Map<String,String> paramsMap = new HashMap<>(params.size());
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a params list
	 * @param params
	 * @return
	 */
	public static UrlQueryString fromParams(final UrlQueryStringParam... params) {
		if (CollectionUtils.isNullOrEmpty(params)) return new UrlQueryString(null);
		Map<String,String> paramsMap = new HashMap<>(params.length);
		for (UrlQueryStringParam param : params) {
			paramsMap.put(param.getName(),param.getValue());
		}
		return new UrlQueryString(paramsMap);
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @param paramsParser
	 * @return
	 */
	public static UrlQueryString fromParamsString(final String paramsStr,
												  final ParametersParser paramsParser) {
		String theParamStr = paramsStr != null
						  && paramsStr.trim().startsWith("?") ? paramsStr.trim().substring(1)
								  							  : paramsStr;
		return ParametersWrapperBase._loadFromString(FACTORY,paramsParser,
											  		 theParamStr,
											  		 false);	// do not decode param values
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @param paramsParser
	 * @return
	 */
	public static UrlQueryString fromUrlEncodedParamsString(final String paramsStr,
															final ParametersParser paramsParser) {
		String theParamStr = paramsStr != null && paramsStr.trim().startsWith("?") ? paramsStr.trim().substring(1) : paramsStr;
		return ParametersWrapperBase._loadFromString(FACTORY,paramsParser,
													 theParamStr,
													 true);	// decode param values
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @return
	 */
	public static UrlQueryString fromParamsString(final String paramsStr) {
		return UrlQueryString.fromParamsString(paramsStr,
											   new ParametersParserRegexBased());				// not gwt-compatible by default
	}
	/**
	 * Creates a new instance form a full query string
	 * @param paramsStr
	 * @return
	 */
	public static UrlQueryString fromUrlEncodedParamsString(final String paramsStr) {
		return UrlQueryString.fromUrlEncodedParamsString(paramsStr,
														 new ParametersParserRegexBased());	// not gwt-compatible by default
	}
	public static UrlQueryString valueOf(final String paramsStr) {
		return UrlQueryString.fromParamsString(paramsStr);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SANITIZE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the query string param
	 * It's usually used with OWASP like:
	 * <pre class='brush:java'>
	 *		protected static PolicyFactory policy = Sanitizers.FORMATTING
	 *											  		.and(Sanitizers.BLOCKS);
	 *	//										  		.and(Sanitizers.LINKS);		// do NOT escape @ character
	 *		protected static StringConverterFilter SANITIZER_FILTER = (untrustedHtml) -> {
	 *																		String safeHtml = policy.sanitize(untrustedHtml);
	 *																		return safeHtml.replace("&#64;","@");		// mega-ñapa for emails
	 *																   }
	 * </pre>
	 * @param sanitizer
	 * @return
	 */
	public UrlQueryString sanitizeUsing(final StringConverterFilter sanitizer) {
		if (CollectionUtils.isNullOrEmpty(_params)) return this;		// nothing to sanitize

		Set<UrlQueryStringParam> unsafeParams = this.getQueryStringParams();
		Set<UrlQueryStringParam> safeParams = unsafeParams.stream()
														  .map(param -> param.sanitizeUsing(sanitizer))
														  .collect(Collectors.toSet());
		return UrlQueryString.fromParams(safeParams);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private final transient Memoized<Set<UrlQueryStringParam>> _qryStrParams =
									new Memoized<>() {
											@Override
											public Set<UrlQueryStringParam> supply() {
												Set<UrlQueryStringParam> outParams = null;
												if (UrlQueryString.this.hasParams()) {
													outParams = new HashSet<>(_params.size());
													for (Map.Entry<String,String> me : _params.entrySet()) {
														outParams.add(new UrlQueryStringParam(me.getKey(),me.getValue()));
													}
												}
												return outParams;
											}
									 };
	/**
	 * Returns the query string params
	 * @return
	 */
	public Set<UrlQueryStringParam> getQueryStringParams() {
		return _qryStrParams.get();
	}
	/**
	 * Return the query string params ordered by name
	 * @return
	 */
	public Set<UrlQueryStringParam> getQueryStringParamsOrdered() {
		Set<UrlQueryStringParam> unordered = this.getQueryStringParams();
		return CollectionUtils.hasData(unordered)
					? new LinkedHashSet<>(unordered.stream()
												   .sorted(Comparator.comparing(UrlQueryStringParam::getName))
												   .toList())
					: unordered;
	}
	/**
	 * Returns a query string param
	 * @param paramName
	 * @return
	 */
	public UrlQueryStringParam getQueryStringParam(final String paramName) {
		return this.getQueryStringParams()
				   .stream()
				   .filter(aParam -> aParam.getName().equals(paramName))
				   .findFirst().orElse(null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JOIN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an NEW instance of an {@link UrlQueryString} object joining
	 * this query string with the given one
	 * @param other
	 * @return
	 */
	public UrlQueryString joinWith(final UrlQueryString other) {
		if (other == null) return this;
		return ParametersWrapperBase.join(FACTORY,
										  this,other);
	}
	/**
	 * Adds a param to the query string
	 * @param name
	 * @param value
	 * @return
	 */
	public UrlQueryString add(final String name,final String value) {
		return ParametersWrapperBase.join(FACTORY,
										  this,
										  name,value);
	}
	/**
	 * Adds a param to the query string
	 * @param name
	 * @param paramValueProvider
	 * @return
	 */
	public UrlQueryString add(final String name,final Provider<String> paramValueProvider) {
		return ParametersWrapperBase.join(FACTORY,
										  this,
										  name,paramValueProvider.provideValue());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a NEW instance of an {@link UrlQueryString} object without
	 * the given param (if it's present)
	 * @param paramName
	 * @return
	 */
	public UrlQueryString withoutParam(final String paramName) {
		return ParametersWrapperBase.withoutParam(FACTORY,
										  		  this,
										  		  paramName);
	}
	/**
	 * Creates a NEW instance of an {@link UrlQueryString} object without
	 * the given param (if it's present)
	 * @param pattern
	 * @return
	 */
	public UrlQueryString withoutParamsMatching(final Pattern pattern) {
		return ParametersWrapperBase.withoutParamMatching(FACTORY,
										  		  		  this,
										  		  		  pattern);
	}
	/**
	 * Creates a NEW instance of an {@link UrlQueryString} object without
	 * the given param (if it's present)
	 * @param pattern
	 * @return
	 */
	public UrlQueryString withoutParamsMatching(final Predicate<String> filter) {
		return ParametersWrapperBase.withoutParamMatching(FACTORY,
										  		  		  this,
										  		  		  filter);
	}
}
