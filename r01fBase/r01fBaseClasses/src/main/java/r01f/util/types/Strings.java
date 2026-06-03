package r01f.util.types;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import com.google.common.base.CharMatcher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;


/**
 * This type provides some utility static methods:
 * 			<pre class='brush:java'>
 * 				String myQuotedStr = Strings.quote("my unquoted str");
 * 				...
 * 				String customized = Strings.customized("{}:{}",
 * 													   "hello","Alex");
 * 				...
 * 				int anInt = Strings.asInteger("25");
 * 				...
 * 				Reader r = Strings.asReader("sadfasdfasd");
 * 			</pre>
 */
public class Strings {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String EMPTY = "";
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns as a byte array encoded on the system's default charset
	 * @param str
	 */
	public static byte[] getBytes(final CharSequence str) {
		if (str == null) return null;
		return str.toString().getBytes(Charset.defaultCharset());
	}
	/**
	 * Returns as a byte array encoded on the provided charset
	 * @param str
	 * @param charset the encoding
	 */
	public static byte[] getBytes(final CharSequence str,
								  final Charset charset) {
		if (str == null) return null;
		return str.toString().getBytes(charset);
	}
	/**
	 * Returns an array of {@link String}s with each line of the original {@link String}
	 * @param str
	 */
	public static String[] getLines(final CharSequence str) {
		if (str == null) return null;
		String[] outLines = str.toString().split("\\r?\\n");
		return outLines;
	}
	/**
	 * Returns the n first characters of a String with three dots,
	 * if String´s length lower than n return the String and if null returns an empty String
	 * @param str String
	 * @param n int
	 * @return String
	 */
	public static String getfirstNCharacters(final String str,
									 		 final int n) {
			return isNullOrEmpty(str) ? ""
									  : str.length() > n ? StringUtils.left(str,
															  				n) + "..."
														 : str;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOMIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Customizes a {@link String} containing placeholders like {} for provided vars
	 * ie:
	 * <pre class='brush:java'>
	 * 		Strings.of("Hello {} today is {}","Alex","Saturday"}
	 * </pre>
	 * @param strToCustomize the {@link String} to be customized
	 * @param vars the placeholder's values
	 * @return an {@link String} composed from the strToCustomize param replacing the placeholders with the provided values
	 */
	public static String customized(final CharSequence strToCustomize,final Object... vars) {
		return _customize(strToCustomize,vars);
	}
	/**
	 * Customizes a {@link String} containing placeholders like {} for provided vars
	 * ie:
	 * <pre class='brush:java'>
	 * 		Strings.of("Hello {} today is {}","Alex","Saturday"}
	 * </pre>
	 * @param strToCustomize the {@link String} to be customized
	 * @param vars the placeholder's values
	 * @return an {@link StringBuffer} composed from the strToCustomize param replacing the placeholders with the provided values
	 */
	private static String _customize(final CharSequence strToCustomize,final Object... vars) {
		if (strToCustomize == null) return null;
		if (vars == null || vars.length == 0) return strToCustomize.toString();

		// reuse MessageFormatter from SL4FJ
		return MessageFormatter.arrayFormat(strToCustomize.toString(),vars)
							   .getMessage();
//		// custom impl
//		String workStr = strToCustomize.toString();
//		for (Object var : vars) {
//			workStr = workStr.replaceFirst("\\{\\}",(var != null ? _matcherQuoteReplacement(var.toString())	// should be _objectToString(var) but it's problematic in GWT
//																 : "null"));
//		}
//		return new StringBuffer(workStr);
	}
	/**
	 * Copy of {@link Matcher#quoteReplacement(String)} to make it possible to use this with GWT
	 * @param s
	 * @return
	 * @see Matcher#quoteReplacement(String)
	 */
	@SuppressWarnings("unused")
	private static String _matcherQuoteReplacement(final String s) {
		if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1)) return s;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' || c == '$') sb.append('\\');
			sb.append(c);
		}
		return sb.toString();
	}
	public interface StringCustomizerVarsProvider {
		public Object[] provideVars();
	}
	public interface StringCustomizerVarsProviderConditioned
			 extends StringCustomizerVarsProvider {
		public boolean shouldAdd();
	}
	/**
	 * Customizes the underlyng string with variables are provided by the {@link StringCustomizerVarsProvider} instance
	 * The {@link StringCustomizerVarsProvider} instance can be in two flavours:
	 * <ul>
	 * 		<li>{@link StringCustomizerVarsProvider} if the provided string is added without evaluating any condition</li>
	 * 		<li>{@link StringCustomizerProviderVarsConditioned} if the provided string is added if the shouldAdd() method of the vars provider returns true</li>
	 * <ul>
	 * Sample code:
	 * <pre class="brush:java">
	 * 		String customized = Strings.customizedWith("the var value is {}",
	 * 												   new StringCustomizerVarsProviderConditioned() {
	 * 														@Override
	 * 														public boolean shouldAdd() {
	 * 															return true;		// any condition could be evaluated to guess if the string should be added or not
	 * 														}
	 * 														@Override
	 * 														public Object[] provideVars() {
	 * 															return new Object[] {"the value"};
	 * 														}
	 * 												  })
	 * 								   .asString();
	 * </pre>
	 * @param str
	 * @param varsProvider
	 * @return
	 */
	public static String customizedWith(final String str,
								 		final StringCustomizerVarsProvider varsProvider) {
		boolean shouldAdd = (varsProvider instanceof StringCustomizerVarsProviderConditioned) ? ((StringCustomizerVarsProviderConditioned)varsProvider).shouldAdd()
																							  : true;
		if (shouldAdd) {
			Object[] vars = varsProvider.provideVars();
			return Strings.customized(str,vars);
		}
		return str;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Validates if a string is null or empty: whitespace is NOT taken into account
	 * This method can be used in place of <code>sb.toString().trim().length()</code>,
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(final CharSequence str) {
		if (str == null || str.length() == 0) return true;		// true if null
		return CharMatcher.whitespace().matchesAllOf(str);
		/*
		// GWT does NOT supports Character.isWhitespace method
		boolean isEmpty = true;
		for (int i=0;i<str.length();i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				isEmpty = false;
				break;
			}
		}
		return isEmpty;
		*/
	}
	/**
	 * Validates if a string is null or empty: whitespace is NOT taken into account
	 * This method can be used in place of <code>sb.toString().trim().length()</code>,
	 * @param char
	 * @return
	 */
	public static boolean isNullOrEmpty(final char[] chars) {
		return Strings.isNullOrEmpty(new String(chars));
	}
	/**
	 * The reverse method of {@link Strings}{@link #isNullOrEmpty(CharSequence)}
	 * @param str the {@link CharSequence}
	 * @return true is the CharSequence is NOT null or empty
	 */
	public static boolean isNOTNullOrEmpty(final CharSequence str) {
		return !Strings.isNullOrEmpty(str);
	}
	/**
	 * The reverse method of {@link Strings}{@link #isNullOrEmpty(char[])}
	 * @param str the char array
	 * @return true is the char array NOT null or empty
	 */
	public static boolean isNOTNullOrEmpty(final char[] chars) {
		return !Strings.isNullOrEmpty(chars);
	}
	/**
	 * Checks if the CharSequence contains only lowercase characters
	 * if null or empty will return false
	 * <pre>
	 * 		StringUtils.isAllLowerCase(null) = false
	 * 		StringUtils.isAllLowerCase("") = false
	 * 		StringUtils.isAllLowerCase("  ") = false		// BEWARE!
	 * 		StringUtils.isAllLowerCase("abc") = true
	 * 		StringUtils.isAllLowerCase("abC") = false
	 * 		StringUtils.isAllLowerCase("ab c") = false		// BEWARE!!
	 * 		StringUtils.isAllLowerCase("ab1c") = false
	 * 		StringUtils.isAllLowerCase("ab/c") = false
	 * </pre>
	 *
	 * @param str the CharSequence to check, may be null
	 * @return true if only contains lowercase characters, and is non-null
	 */
	public static boolean isAllLowerCaseLetters(final CharSequence str) {
		if (Strings.isNullOrEmpty(str)) return false;
		boolean outLowerCase = true;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isLowerCase(str.charAt(i)) == false) {
				outLowerCase = false;
				break;
			}
		}
		return outLowerCase;
	}
	/**
	 * Checks if the CharSequence contains only uppercase characters
	 * if null or empty will return false
	 * <pre>
	 * 		StringUtils.isAllUpperCase(null) = false
	 * 		StringUtils.isAllUpperCase("") = false
	 * 		StringUtils.isAllUpperCase("  ") = false
	 * 		StringUtils.isAllUpperCase("ABC") = true
	 * 		StringUtils.isAllUpperCase("aBC") = false
	 * 		StringUtils.isAllUpperCase("A C") = false
	 * 		StringUtils.isAllUpperCase("A1C") = false
	 * 		StringUtils.isAllUpperCase("A/C") = false
	 * </pre>
	 *
	 * @param str the CharSequence to check, may be null
	 * @return true if only contains uppercase characters, and is non-null
	 */
	public static boolean isAllUpperCaseLetters(final CharSequence str) {
		if (Strings.isNullOrEmpty(str)) return false;
		boolean outUpperCase = true;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isUpperCase(str.charAt(i)) == false) {
				outUpperCase = false;
				break;
			}
		}
		return outUpperCase;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Concatenates a bunch of strings
	 * @param strs
	 * @return
	 */
	public static String concat(final CharSequence... strs) {
		if (strs == null || strs.length == 0) return null;
		StringBuilder sb = new StringBuilder();
		for (CharSequence currSeq : strs) sb.append(currSeq);
		return sb.toString();
	}
	/**
	 * Concatenates a bunch of Strings
	 * @param strs
	 * @return
	 */
	public static String concat(final String... strs) {
		if (strs == null || strs.length == 0) return null;
		StringBuilder sb = new StringBuilder();
		for (String s : strs) sb.append(s);
		return sb.toString();
	}
	/**
	 * Quotes the string (surrounds it with single quotes)
	 * @param s
	 * @return
	 */
	public static String quote(final String s) {
		return s != null ? Strings.concat("'",s,"'") : null;
	}
	/**
	 * Double quotes the string (surrounds it with double quotes)
	 * @param s
	 * @return
	 */
	public static String doubleQuoute(final String s) {
		return s != null ? Strings.concat("\"",s,"\"") : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the value if the first parameter is not null or the default value if it is
	 * @param s the value
	 * @param defaultValue the defaultValue
	 * @return the value or the default value
	 */
	public static String valueOrDefault(final String s,
										final String defaultValue) {
		return s != null ? s
						 : defaultValue;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Removes everything that's not a word (spaces, _, /, etc)
	 * @param str
	 * @return
	 */
	public static String removeWhitespace(final String str) {
		if (str == null) return str;
		StringBuffer sb = new StringBuffer();
		Pattern regex = Pattern.compile("[^\\w]");
		Matcher regexMatcher = regex.matcher(str);
		while (regexMatcher.find()) regexMatcher.appendReplacement(sb,"");
		regexMatcher.appendTail(sb);
		return sb.toString();
	}
	/**
	 * Removes all \n or \r character
	 * @param str
	 * @return
	 */
	public static String removeNewlinesOrCarriageRetuns(final String str) {
		if (str == null) return str;
		return str.replaceAll("[\n\r]","");
	}
	/**
	 * Replaces accentuated chars with their no-accentuated equivalents
	 * @param str
	 */
	@Deprecated 
	public static String removeAccents(final String str) {
		return Strings.replaceAccentedCharsByNonAccentedEquivalents(str);
	}
	public static String replaceAccentedCharsByNonAccentedEquivalents(final CharSequence str) {
		CharSequence cs = StringEncodeUtils.replaceAccentedCharsByNonAccentedEquivalents(str);
		return cs != null ? cs.toString()
						  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAPITALIZE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sets the first letter in upper case
	 * @param str
	 */
	public static String capitalizeFirstLetter(final String str) {
		if (str == null) return str;
		return StringUtils.capitalize(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PAD 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Adjust the string size appending chars at the left until the string size is the given one
	 * @param size
	 * @param character
	 * @return
	 */
	public static String leftPad(final String str,
						  		 final int size,final char character) {
		if (str == null) return str;
		return StringUtils.leftPad(str,size,character);
	}
	/**
	 * Adjust the string size appending chars at the right until the string size is the given one
	 * @param str
	 * @param size
	 * @param character
	 * @return
	 */
	public static String rightPad(final String str,
						   		  final int size,final char character) {
		if (str == null) return str;
		return StringUtils.rightPad(str,size,character);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TRIM
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Trims the given string so the returned string will be at least 
	 * of "max" length
	 * @param str
	 * @param max
	 * @return
	 */
	public static String trimToLength(final String str,final int max) {
		if (max <= 0) throw new IllegalArgumentException("trim length MUST be >= 0!");
		if (str == null) return null;
		if (str.length() == 0) return str;
		int currLength = str.length();
		int trimLength = currLength <= max ? currLength : max;
		return str.substring(0,trimLength);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a string consisting of a specific number of concatenated copies of an input string. For
	 * example, {@code repeat("hey", 3)} returns the string {@code "heyheyhey"}.
	 *
	 * @param string any non-null string
	 * @param count the number of times to repeat it; a nonnegative integer
	 * @return a string containing {@code string} repeated {@code count} times (the empty string if
	 *	 {@code count} is zero)
	 * @throws IllegalArgumentException if {@code count} is negative
	 */
	public static String repeat(final String str,final int count) {
		return com.google.common.base.Strings.repeat(str,count);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns a {@link String} comparator that takes the language (Locale) into account
	 * @param lang
	 * @return
	 */
	public static Comparator<String> comparatorIn(final Language lang) {
		return Languages.stringComparatorFor(lang);
	}
	public static StringIsContainedWrapper isContainedWrapper(final String str) {
		return new StringIsContainedWrapper(str);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class StringIsContainedWrapper {
		private final String _wrappedStr;

		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final Set<String> otherStrings) {
			return this.in(false,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final String... otherStrings) {
			return this.in(false,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * ignorign the {@link String} case
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean inIgnoringCase(final Set<String> otherStrings) {
			return this.in(true,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * ignorign the {@link String} case
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean inIgnoringCase(final String... otherStrings) {
			return this.in(true,otherStrings);
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @param ignoreCase if the check should take case into account
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final boolean ignoreCase,final String... otherStrings) {
			return this.in(ignoreCase,CollectionUtils.hasData(otherStrings) ? new HashSet<>(Arrays.asList(otherStrings)) : new HashSet<>());
		}
		/**
		 * Checks if the wrapped {@link String} is in the provided {@link Set} of {@link String}s
		 * @param otherStrings a {@link Set} of {@link Strings}
		 * @param ignoreCase if the check should take case into account
		 * @return true if the wrapped {@link String} is contained in the otherStrings {@link Set}
		 */
		public boolean in(final boolean ignoreCase,final Set<String> otherStrings) {
			if (CollectionUtils.isNullOrEmpty(otherStrings)) return false;
			boolean outIsIn = false;
			for (String s : otherStrings) {
				if (s != null) {
					outIsIn = ignoreCase ? _wrappedStr.equalsIgnoreCase(s)
										 : _wrappedStr.equals(s);
					if (outIsIn) break;
				}
			}
			return outIsIn;
		}
		/**
		 * Checks if the wrapped string contains any of the given strings
		 * @param otherStrings
		 * @return
		 */
		public boolean containsAny(final String... otherStrings) {
			return this.containsAny(false,otherStrings);
		}
		/**
		 * Checks if the wrapped string contains any of the given strings
		 * @param otherStrings
		 * @return
		 */
		public boolean containsAnyIgnoringCase(final String... otherStrings) {
			return this.containsAny(true,otherStrings);
		}
		/**
		 * Checks if the string contains any of the given strings
		 * @param ignoreCase
		 * @param otherStrings
		 * @return
		 */
		public boolean containsAny(final boolean ignoreCase,final String... otherStrings) {
			if (CollectionUtils.isNullOrEmpty(otherStrings)) return false;
			boolean outContains = false;
			for (String s : otherStrings) {
				if (s != null) {
					outContains = ignoreCase ? _wrappedStr.toLowerCase().contains(s.toLowerCase())
											 : _wrappedStr.contains(s);
					if (outContains) break;
				}
			}
			return outContains;
		}
	}
	
}