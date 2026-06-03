package r01f.types.url;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.PathBase;
import r01f.types.PathFactory;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Represents a {@link Path} in a {@link URL}.
 * A URL consists of five parts: the scheme, subdomain, top-level domain, second-level domain, and subdirectory.
 * UrlPath represents the subdirectory part.
 * ie: for url http://www.euskadi.eus/gobierno-vasco/tramites-servicios/ urlPath is gobierno-vasco/tramites-servicios/
 */
@Immutable
@MarshallType(as="urlPath")
public class UrlPath
	 extends Path
  implements IsUrlPath {
	private static final long serialVersionUID = -4132364966392988245L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * True is the trailing slash must be preserved
	 * (sometimes the trailing slash is important and MUST be preserved)
	 */
	private final boolean _preserveTrailingSlash;
	/**
	 * true if the path ends with a trailing slash (ie: /foo/bar/)
	 * -this is usually important in url paths-
	 */
	private final boolean _trailingSlash;
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTERS & SETTERS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasTrailingSlash() {
		return _trailingSlash;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath() {
		this(false);		// do NOT preserve the trailing slash
	}
	public UrlPath(final boolean preserveTrailingSlash) {
		super(new ArrayList<>());
		_trailingSlash = false;
		_preserveTrailingSlash = preserveTrailingSlash;
	}
	public UrlPath(final Collection<String> pathEls) {
		this(false,		// do not preserve trailing slash
			 pathEls);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Collection<String> pathEls) {
		super(pathEls);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(pathEls)
								? pathEls.stream()
										 .reduce((first,second) -> second)
										 .orElse(null)
										 .endsWith("/")
								: false;
	}
	public  UrlPath(final String... elements) {
		this(false,		// do not preserve trailing slash
			 elements);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final String... elements) {
		super(elements);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(elements) && elements[elements.length-1] != null
								? elements[elements.length-1].toString().endsWith("/")
								: false;
	}
	public UrlPath(final Object... objs) {
		this(false,		// do not preserve trailing slash
			  objs);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Object... objs) {
		super(objs);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = CollectionUtils.hasData(objs) && objs[objs.length-1] != null
								? objs[objs.length-1].toString().endsWith("/")
								: false;
	}
	public UrlPath(final Object obj) {
		this(false,		// do not preserve trailing slash
			 obj);
	}
	public UrlPath(final boolean preserveTrailingSlash,
				   final Object obj) {
		super(obj);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = obj != null ? obj.toString().endsWith("/") : false;
	}
	public <P extends IsPath> UrlPath(final P otherPath) {
		this(false,			// do not preserve trailing slash
			 otherPath);
	}
	public <P extends IsPath> UrlPath(final boolean preserveTrailingSlash,
									  final P otherPath) {
		super(otherPath);
		_preserveTrailingSlash = preserveTrailingSlash;
		_trailingSlash = otherPath != null ? otherPath.asString().endsWith("/")
										   : false;
	}
	public static final PathFactory<UrlPath> URL_PATH_FACTORY = _createUrlPathFactory(false);
	public static final PathFactory<UrlPath> URL_PATH_FACTORY_PRESERVE_TRAILING_SLASH = _createUrlPathFactory(true);
	private static final PathFactory<UrlPath> _createUrlPathFactory(final boolean preserveTrailingSlash) {
		return elements -> new UrlPath(preserveTrailingSlash,
						   			   elements);
	}
	private final Memoized<PathFactory<UrlPath>> _memoizedPathFactory = new Memoized<>() {
																				@Override
																				public PathFactory<UrlPath> supply() {
																					return _createUrlPathFactory(_preserveTrailingSlash);
																				}
																		};
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)_memoizedPathFactory.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIES
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlPathBuilderPreserveTrailingSlash preservingTrailingSlash() {
		return new UrlPathBuilderPreserveTrailingSlash(true);
	}
	public static UrlPathBuilderPreserveTrailingSlash notPreservingTrailingSlash() {
		return new UrlPathBuilderPreserveTrailingSlash(false);
	}
	@RequiredArgsConstructor
	public static class UrlPathBuilderPreserveTrailingSlash {
		private final boolean _preserveTrailingSlash;

		public UrlPath from(final String... elements) {
			if (CollectionUtils.isNullOrEmpty(elements)) return null;
			return new UrlPath(_preserveTrailingSlash,
							   elements);
		}
		public <P extends IsPath> UrlPath from(final P other) {
			if (other == null) return null;
			UrlPath outPath = new UrlPath(_preserveTrailingSlash,
										  other);
			return outPath;
		}
		public UrlPath from(final Object... obj) {
			if (obj == null) return null;
			return new UrlPath(_preserveTrailingSlash,
							   obj);
		}
		public UrlPath from(final URL url) {
			if (url == null) return null;
			return new UrlPath(_preserveTrailingSlash,
							   url.toString());
		}
	}
	/**
	 * Factory from {@link String}
	 * @param path
	 * @return
	 */
	public static UrlPath valueOf(final String path) {
		return new UrlPath(path);
	}
	/**
	 * Factory from path, with preserveTrailingSlash option
	 * @param preserveTrailingSlash, path,
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final boolean preserveTrailingSlash, final String path) {
		return new UrlPath(preserveTrailingSlash, path);
	}
	/**
	 * Factory from path components
	 * @param elements
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final String... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return null;
		return new UrlPath(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> UrlPath from(final P other) {
		if (other == null) return null;
		UrlPath outPath = new UrlPath(other);
		return outPath;
	}
	/**
	 * Factory from an {@link Object} (the path is composed translating the {@link Object} to {@link String})
	 * @param obj
	 * @return the {@link Path} object
	 */
	public static UrlPath from(final Object... obj) {
		if (obj == null) return null;
		return new UrlPath(obj);
	}
	/**
	 * Factory from a {@link URL} object
	 * @param url
	 * @return
	 */
	public static UrlPath from(final URL url) {
		if (url == null) return null;
		return new UrlPath(url.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Path} with a normalized path (checks and replaces to conforming 
	 * characters, truncates excessive length) from a {@link String}
	 * @param String
	 * @return UrlPath
	 */
	public static <U extends UrlPath> UrlPath normalize(final U urlPath) {
		if (urlPath == null) return null;
		return UrlPath.normalize(urlPath.asString());
	}
	/**
	 * Returns a {@link Path} with a normalized path (checks and replaces to conforming 
	 * characters, truncates excessive length) from a {@link String}
	 * @param String
	 * @return UrlPathStr
	 */
	public static String normalizeString(final String urlPathStr) {
		if (Strings.isNullOrEmpty(urlPathStr)) return null;
		
		// Transform all characters to lowercase
		String outUrlPathStr = urlPathStr.toLowerCase();

		// Firstly, all whitespace characterss are replaced to hyphens
		outUrlPathStr = outUrlPathStr.replaceAll(" ", "-");

		// Next, accented vowels are changed to normal ones
		outUrlPathStr = outUrlPathStr.replaceAll("[\\u00E1\\u00E0\\u00E2\\u00E4]","a"); // áàâä
		outUrlPathStr = outUrlPathStr.replaceAll("[\\u00E9\\u00E8\\u00EA\\u00EB]","e"); // éèêë
		outUrlPathStr = outUrlPathStr.replaceAll("[\\u00ED\\u00EC\\u00EE\\u00EF]","i"); // íìîï
		outUrlPathStr = outUrlPathStr.replaceAll("[\\u00F3\\u00F2\\u00F4\\u00F6]","o"); // óòôö
		outUrlPathStr = outUrlPathStr.replaceAll("[\\u00FA\\u00F9\\u00FB\\u00FC]","u"); // úùûü

		// The ñ character is replaced to n
		outUrlPathStr = outUrlPathStr.replaceAll("ñ","n");

		// Special characters are removed
		outUrlPathStr = outUrlPathStr.replaceAll("[^a-z0-9_/+-]", "-"); // ONLY alphabet letters, numbers, hyphens, underscores, the plus and slash characters are allowed

		// Preceding and trailing hyphens are removed
		String hyphenPattern = "^-*(.*[^-]+)-*$";
		if ( outUrlPathStr.matches( hyphenPattern )) {
			Pattern pattern = Pattern.compile( hyphenPattern );
			Matcher matcher = pattern.matcher(outUrlPathStr);
			matcher.find();
			outUrlPathStr = matcher.group(1);
		}
		outUrlPathStr = outUrlPathStr.replaceAll("/-", "/");
		
		// Replace when two or more hyphens are consecutive. Eg: where--is-my-cheese
		outUrlPathStr = outUrlPathStr.replaceAll("(-)\\1+", "-");

		// Replace when there's a hyphen after a slash "/" character
		outUrlPathStr = outUrlPathStr.replaceAll("/-", "/");

		// Replace when there's consecutive slash characters
		// Caso test-alias//a-recortar-por-tamaño/// por test-alias/a-recortar-por-tamaño/
		outUrlPathStr = outUrlPathStr.replaceAll("//+","/");

		// Add trailing slash if not present
		if (!outUrlPathStr.endsWith("/")) {
			outUrlPathStr = outUrlPathStr + "/";
		}

		// Remove preceding slash if present
		if (outUrlPathStr.startsWith("/")) {
			outUrlPathStr = outUrlPathStr.replaceAll("^/+", "");
		}

		// Each subfolder is limited to a maximum of 255 characters
		if (outUrlPathStr.indexOf('/') > -1) {
			String[] urlToReturnParts = outUrlPathStr.split("/");
			StringBuilder finalUrlToReturn = new StringBuilder();
			for(int i = 0; i<urlToReturnParts.length ; i++ ) {
				if (urlToReturnParts[i].length() <= 255) {
					finalUrlToReturn.append(urlToReturnParts[i]).append('/');
				} else {
					// Trim to allowed length and remove trailing special characters
					String folderName = urlToReturnParts[i].substring(0,254);
					while(folderName.endsWith("-") || folderName.endsWith("_") || folderName.endsWith("+") || folderName.endsWith("|")) {
						folderName = folderName.substring(0, folderName.length()-1);
					}
					finalUrlToReturn.append(folderName).append('/');
				}
			}
			outUrlPathStr = finalUrlToReturn.toString();
		}

		// trim to ALIAS_MAX_SIZE 
		if (outUrlPathStr.length() > 400) {
			outUrlPathStr = outUrlPathStr.substring(0, 400);
			int lastSeparator = outUrlPathStr.lastIndexOf('-');
			int lastPath = outUrlPathStr.lastIndexOf('/');
			if (lastSeparator > lastPath) {
				// Case test-alias/to-trim-because-too-long/ is trimmed to test-alias/to-trim-because/
				outUrlPathStr = outUrlPathStr.substring(0, lastSeparator) + "/";
			} else {
				// Case test-alias/to-trim/becausetoolong/ is trimmed to test-alias/to-trim/
				outUrlPathStr = outUrlPathStr.substring(0, lastPath + 1);
			}
		}
		return outUrlPathStr;
	}
	/**
	 * Returns a {@link Path} with a normalized path (checks and replaces to conforming 
	 * characters, truncates excessive length) from a {@link String}
	 * @param String
	 * @return UrlPathStr
	 */
	public static UrlPath normalize(final String urlPathStr) {
		String outUrlPathStr = UrlPath.normalizeString(urlPathStr);
		return UrlPath.from(true,	// preserveTrailingSlash
							outUrlPathStr);
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public UrlPath withoutLastPathElement() {
		return PathBase.withoutLastPathElement(this.getPathFactory(),
											   this);
	}
	@Override @SuppressWarnings("unchecked")
	public UrlPath joinedWith(final Object... elements) {
		return PathBase.join(this.getPathFactory(),
						     this,elements);
	}
	@Override @SuppressWarnings("unchecked")
	public UrlPath prependedWith(final Object... elements) {
		return PathBase.prepend(this.getPathFactory(),
								this,elements);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		if (!_preserveTrailingSlash) return super.asString();
		return _trailingSlash ? super.asString() + "/"
							  : super.asString();
	}
	@Override
	public String asRelativeString() {
		if (!_preserveTrailingSlash) return super.asRelativeString();
		return _trailingSlash ? super.asRelativeString() + "/"
							  : super.asRelativeString();
	}
	@Override
	public String asAbsoluteString() {
		if (!_preserveTrailingSlash) return super.asAbsoluteString();
		return _trailingSlash ? super.asAbsoluteString() + "/"
							  : super.asAbsoluteString();
	}
	@Override
	public <P extends IsPath> String asAbsoluteStringFrom(final P parentPath) {
		if (!_preserveTrailingSlash) return super.asAbsoluteStringFrom(parentPath);
		return _trailingSlash ? super.<P>asAbsoluteStringFrom(parentPath) + "/"
							  : super.<P>asAbsoluteStringFrom(parentPath);
	}
	@Override
	public <P extends IsPath> String asRelativeStringFrom(final P parentPath) {
		if (!_preserveTrailingSlash) return super.asRelativeStringFrom(parentPath);
		return _trailingSlash ? super.<P>asRelativeStringFrom(parentPath) + "/"
							  : super.<P>asRelativeStringFrom(parentPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asAbsoluteStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asAbsoluteStringIncludingQueryString(queryString,
														 false);
	}
	@Override
	public String asAbsoluteStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   	    this.asAbsoluteString(),queryString.asString(encodeParamValues))
								   : this.asAbsoluteString();
	}
	@Override
	public String asRelativeStringIncludingQueryStringEncoded(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 true);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString) {
		return this.asRelativeStringIncludingQueryString(queryString,
														 false);
	}
	@Override
	public String asRelativeStringIncludingQueryString(final UrlQueryString queryString,
													   final boolean encodeParamValues) {
		return queryString != null ? Strings.customized("{}?{}",
												   		this.asRelativeString(),queryString.asString(encodeParamValues))
								   : this.asRelativeString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static Collection<Object> _sanitize(final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) throw new IllegalArgumentException();
		return Stream.of(elements)
					 .filter(el -> el != null)
					 .map(el -> {
							if (!(el instanceof String)) return el; 
							
							String elStr = el.toString();
							
							// remove query string or anchor if present
							int pQ = elStr.indexOf('?');
							if (pQ >= 0) elStr = elStr.substring(0,pQ);
							
							int pA = elStr.indexOf('#');
							if (pA >= 0) elStr = elStr.substring(0,pA);
							
							return elStr;
					})
					.filter(el -> el instanceof String
										? ((String)el).length() > 0
									    : true)
					.collect(Collectors.toList());

	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public UrlPath remainingPathFrom(final UrlPath startingPath) {
		Collection<String> remainingPathEls = this.getPathElementsAfter(startingPath);
		UrlPath outUrlPath = remainingPathEls != null ? new UrlPath(_preserveTrailingSlash,
																    remainingPathEls)
													  : null;
		return outUrlPath;
	}
	/**
	 * Returns the url path AFTER the given prefix
	 * ie: if path=/foo/bar/baz.hml and prefix=/foo
	 * then, the returned path=/bar/baz.html
	 * If the path does NOT starts with the given prefix, it throws an IllegalStateException
	 * @param prefix
	 * @return
	 */
	public UrlPath urlPathAfter(final UrlPath prefix) {
		return this.remainingPathFrom(prefix);
	}
}
