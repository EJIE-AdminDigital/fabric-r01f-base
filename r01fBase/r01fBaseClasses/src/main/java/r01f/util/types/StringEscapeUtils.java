package r01f.util.types;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.NumericEntityUnescaper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringEscapeUtils {
	/**
	 * Escapes the characters in a JSon string
	 * According to the RFC. JSON is pretty liberal: The only characters that must be escaped are \, ", and control codes (anything less than U+0020).
	 * see http://stackoverflow.com/questions/3020094/how-should-i-escape-strings-in-json
	 * @return
	 */
	public static CharSequence escapeJSON(final CharSequence str) {
		char c = 0;
		int i;
		int len = str.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = str.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				sb.append('\\');
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
			   sb.append("\\r");
			   break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb;
	}
	/**
	 * Escapes the characters in a String using HTML entities.
	 * For example: "bread" & "butter" becomes &quot;bread&quot; &amp; &quot;butter&quot;.
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	public static CharSequence escapeHTML(final CharSequence str) {
		return org.apache.commons.text.StringEscapeUtils.escapeHtml4(str.toString());
	}
	/**
	 * Escapes the characters in a String using HTML entities BUT ignores HTML tags like <p> or </p>
	 * For example: "bread" & "butter" becomes &quot;bread&quot; &amp; &quot;butter&quot;.
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	public static CharSequence escapeHTMLButNOTTags(final CharSequence str) {
		return ESCAPE_HTML4_BUT_NOT_TAGS.translate(str);
	}	
	// Method above doesn't escape double quotes, this alternative method escapes ALL double quotes, use carefully with HTML strings.
	public static CharSequence escapeHTMLAndDoubleQuotesButNotTags(final CharSequence str) {
		return ESCAPE_HTML4_BUT_NOT_TAGS.translate(str)
										.toString()
										.replaceAll("\"", "&quot;");
	}
	/**
	 * Escapes the double quotes characters in a String.
	 * For example: "bread" & "butter" => &quot;bread&quot; &amp; &quot;butter&quot;. 
	 */
	public static CharSequence escapeDoubleQuotes(final CharSequence str) {
		return str == null ? null : str.toString()
									   .replaceAll("\"", "&quot;");
	}
	// this is a "clone" of the StringEscapeUtils#ESCAPE_HTML4 but removing some characters like <, >, & 
	private static final CharSequenceTranslator ESCAPE_HTML4_BUT_NOT_TAGS = new AggregateTranslator(
																					// new LookupTranslator(EntityArrays.BASIC_ESCAPE),
																					new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
																					new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE));
	/**
	 * Escapes the characters in a String using XML entities.
	 * For example: "bread" & "butter" => &quot;bread&quot; &amp; &quot;butter&quot;.
	 * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
	 * Note that unicode characters greater than 0x7f are currently escaped to their numerical \\u equivalent. 
	 */
	public static CharSequence escapeXML(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.escapeXml10(_buffer.toString());
	}
	/**
	 * Escapes the characters in a {@code String} using Java String rules.
	 * For example: ""áéíóú"" => \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6.
	 */
	public static CharSequence escapeJava(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.escapeJava(_buffer.toString());
	}
	/**
	 * Escapes the characters in a {@code String} using Java String rules.
	 * For example: ""áéíóú"" => \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6.
	 */
	public static CharSequence escapeEcmaScript(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(_buffer.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * UnEscapes the characters in a String using HTML entities.
	 * For example: &quot;bread&quot; &amp; &quot;butter&quot; ==> "bread" & "butter"
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	public static CharSequence unescapeHTML(final CharSequence str) {
		return org.apache.commons.text.StringEscapeUtils.unescapeHtml4(str.toString());
	}
	/**
	 * UnEscapes the characters in a String using HTML entities.
	 * For example: &quot;bread&quot; &amp; &quot;butter&quot; ==> "bread" & "butter"
	 * Supports all known HTML 4.0 entities, including funky accents. 
	 * Note that the commonly used apostrophe escape character (&apos;) is not a legal entity and so is not supported).
	 */
	public static CharSequence unescapeHTMLButNotTags(final CharSequence str) {
		return UNESCAPE_HTML4_BUT_NOT_TAGS.translate(str);
	}	
	// Method above doesn't unescape double quotes, this alternative method unescapes ALL double quotes, use carefully with HTML strings.
	public static CharSequence unescapeHTMLAndDoubleQuotesButNotTags(final CharSequence str) {
		return UNESCAPE_HTML4_BUT_NOT_TAGS.translate(str)
										  .toString()
										  .replaceAll("&quot;", "\"");
	}
	public static CharSequence unescapeDoubleQuotes(final CharSequence str) {
		return str == null ? null : str.toString()
									   .replaceAll("&quot;", "\"");
	}

	// this is a "clone" of the StringEscapeUtils#UNESCAPE_HTML4 but removing some characters like <, >, & 
	public static final CharSequenceTranslator UNESCAPE_HTML4_BUT_NOT_TAGS = new AggregateTranslator(
																					//new LookupTranslator(EntityArrays.BASIC_UNESCAPE),
																					new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE),
																					new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE),
																					new NumericEntityUnescaper());
	/**
	 * UnEscapes the characters in a String using XML entities.
	 * For example: &quot;bread&quot; &amp; &quot;butter&quot; ==> "bread" & "butter"
	 * Supports only the five basic XML entities (gt, lt, quot, amp, apos). Does not support DTDs or external entities.
	 * Note that unicode characters greater than 0x7f are currently escaped to their numerical \\u equivalent. 
	 */
	public static CharSequence unescapeXML(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.unescapeXml(_buffer.toString());
	}
	/**
	 * UnEscapes the characters in a {@code String} using Java String rules.
	 * For example: \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6. ==> "áéíóú"
	 */
	public static CharSequence unescapeJava(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.unescapeJava(_buffer.toString());
	}
	/**
	 * UnEscapes the characters in a {@code String} using Java String rules.
	 * For example: \u00E1\u00E9\u00ED\u00F3\u00FA\u00F1\u00F6. ==> "áéíóú"
	 */
	public static CharSequence unescapeEcmaScript(final CharSequence _buffer) {
		return org.apache.commons.text.StringEscapeUtils.unescapeEcmaScript(_buffer.toString());
	}
}
