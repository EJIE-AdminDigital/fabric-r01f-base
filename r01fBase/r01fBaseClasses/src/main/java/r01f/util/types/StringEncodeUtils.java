package r01f.util.types;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.common.io.BaseEncoding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.codec.Base45;
import r01f.codec.ZBase32;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringEncodeUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//	CODE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes a {@link String}
	 * @param str the string to be encoded
	 * @param encoding the encoding
	 * @return the provided {@link String} encoded
	 */
	public static CharSequence encode(final CharSequence str,final Charset encoding) {
		if (str == null) return null;
		Charset theEncoding = encoding != null ? encoding : Charset.defaultCharset();
		// Encode
		CharBuffer cb = CharBuffer.wrap(str);
		ByteBuffer bb = theEncoding.encode(cb);

		// Transform to a CharBuffer in order to get an CharSequence
		CharBuffer outCb = theEncoding.decode(bb);
		return outCb;
	}
	/**
	 * Transcodes a byte array
	 * @param bytes
	 * @param srcCharset
	 * @param dstCharset
	 * @return
	 * @throws CharacterCodingException
	 */
	public static byte[] transcode(final byte[] bytes,
								   final Charset srcCharset,final Charset dstCharset) throws CharacterCodingException {

		// Decode from srcCharset
		CharsetDecoder decoderSrc = srcCharset.newDecoder();
		decoderSrc.onMalformedInput(CodingErrorAction.IGNORE)
				  .onUnmappableCharacter(CodingErrorAction.IGNORE);
		ByteBuffer bbufSrc = ByteBuffer.wrap(bytes);
		CharBuffer cbufSrc = decoderSrc.decode(bbufSrc);

		// Encode to dstCharset
		CharsetEncoder encoderDst = dstCharset.newEncoder();
		encoderDst.onMalformedInput(CodingErrorAction.IGNORE)
				  .onUnmappableCharacter(CodingErrorAction.IGNORE);
		ByteBuffer bbufDst = encoderDst.encode(cbufSrc);
		byte[] bytesInDstCharset = bbufDst.array();

		// Return
		return bytesInDstCharset;
	}
	/**
	 * UTF-8 Encodes an {@link String}
	 * @param str
	 * @return the UTF-8 encoded provided {@link String}
	 */
	public static CharSequence encodeUTF(final CharSequence str) {
		return StringEncodeUtils.encode(str,Charset.forName("UTF-8"));
	}
	/**
	 * ISO-8859- encodes an {@link String}
	 * @param str
	 * @return the ISO-8859-1 encoded provided {@link String}
	 */
	public static CharSequence encodeISO8859(final CharSequence str) {
		return StringEncodeUtils.encode(str,Charset.forName("ISO-8859-1"));
	}
	/**
	 * According to http://en.wikipedia.org/wiki/Windows-1252 there are 27 NON-standard characters
	 * used by Microsoft's Windows-1252 character set that is supposed to be an ISO-8859-1 super-set
	 * This method replaces those characters with other from the ISO-8859-1 character set while other characters are removed
	 * References:
	 *	   http://en.wikipedia.org/wiki/Windows-1252
	 *	   http://www.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WindowsBestFit/bestfit1252.txt
	 *	   http://download-llnw.oracle.com/javase/tutorial/i18n/text/convertintro.html
	 * @param str the {@link String} to be converted
	 */
	public static CharSequence windows1252ToIso8859(final CharSequence str)  {
	   if (null == str) return null;
	   StringBuilder modif = new StringBuilder(str);	// Create an original string copy that will be modified
	   for (int i = 0; i < str.length(); i++)  {
		  int origCharAsInt = str.charAt(i);

		  switch (origCharAsInt) {
		  // replaced characters
		  case ('\u2018'):  modif.setCharAt(i,'\''); break;  // left single quote
		  case ('\u2019'):  modif.setCharAt(i,'\''); break;  // right single quote
		  case ('\u201A'):  modif.setCharAt(i,'\''); break;  // lower quotation mark

		  case ('\u201C'):  modif.setCharAt(i,'"'); break;  // left double quote
		  case ('\u201D'):  modif.setCharAt(i,'"'); break;  // right double quote
		  case ('\u201E'):  modif.setCharAt(i,'"'); break;  // double low quotation mark

		  case ('\u2039'):  modif.setCharAt(i,'\''); break;  // Single Left-Pointing Quotation Mark
		  case ('\u203A'):  modif.setCharAt(i,'\''); break;  // Single right-Pointing Quotation Mark

		  case ('\u02DC'):  modif.setCharAt(i,'~'); break;  // Small Tilde

		  case ('\u2013'):  modif.setCharAt(i,'-'); break;  // En Dash
		  case ('\u2014'):  modif.setCharAt(i,'-'); break;  // EM Dash

		  // Removed characters
		  case ('\u0178'):  modif.deleteCharAt(i); break;
		  case ('\u017E'):  modif.deleteCharAt(i); break;
		  case ('\u0153'):  modif.deleteCharAt(i); break;
		  case ('\u0161'):  modif.deleteCharAt(i); break;
		  case ('\u2122'):  modif.deleteCharAt(i); break;
		  case ('\u2022'):  modif.deleteCharAt(i); break;
		  case ('\u017D'):  modif.deleteCharAt(i); break;
		  case ('\u0152'):  modif.deleteCharAt(i); break;
		  case ('\u0160'):  modif.deleteCharAt(i); break;
		  case ('\u2030'):  modif.deleteCharAt(i); break;
		  case ('\u02C6'):  modif.deleteCharAt(i); break;
		  case ('\u2021'):  modif.deleteCharAt(i); break;
		  case ('\u2020'):  modif.deleteCharAt(i); break;
		  case ('\u2026'):  modif.deleteCharAt(i); break;
		  case ('\u0192'):  modif.deleteCharAt(i); break;
		  case ('\u20AC'):  modif.deleteCharAt(i); break;

		  // by default the character remains the same
		  default: modif.setCharAt(i,str.charAt(i)); 	break;
		  }
	   }
	   return modif;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Filters the input {@link String} to replace any occurrence of the characters of the provided array that are
	 * replaced by the chars also provided in the other array
	 * This method is useful specially to filter forbidden characters as '<' or '>' in HTML {@link String} that are
	 * replaced by '&gt;' y '&lt;'
	 * @param strToBeFiltered the input {@link String} some of whose characters are to be replaced
	 * @param charsToFilter the characters to be filtered
	 * @param charsFiltered the filtered characters substitutions
	 * @return
	 */
	public static CharSequence filterAndReplaceChars(final CharSequence strToBeFiltered,
													 final char[] charsToFilter,final String[] charsFiltered) {
		if (strToBeFiltered == null) return null;
		if (charsToFilter == null || charsFiltered == null) return strToBeFiltered;
		if (charsToFilter.length < charsFiltered.length) throw new IllegalArgumentException("the [charsToFilter] array and the [charsFiltered] array must have the SAME length!");

		// Copy the input String to a char[]
		char content[] = new char[strToBeFiltered.length()];
		for (int i = 0; i < strToBeFiltered.length(); i++) {
			content[i] = strToBeFiltered.charAt(i);
		}
		// filter chars and put the result in a StringBuffer
		StringBuilder result = new StringBuilder(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			boolean replaced = false;
			for (int j = 0; j < charsToFilter.length; j++) {
				if (content[i] == charsToFilter[j]) {
					result.append(charsFiltered[j]);
					replaced = true;
				}
			}
			if (!replaced) result.append(content[i]);
		}
		return result.toString();
	}
	/**
	 * Filters the input {@link String} to replace any occurrence of the characters of the provided array that are
	 * replaced by the string also provided
	 * In contrast to the {@link #filterAndReplaceChars(CharSequence, char[], String[])} method, this one
	 * replaces all occurrences of all the chars in the filter array by the SAME string
	 * @param strToBeFiltered the input {@link String} some of whose characters are to be replaced
	 * @param charsToFilter the characters to be filtered
	 * @param replacingStr the filtered string substitution
	 * @return
	 */
	public static CharSequence filterAndReplaceChars(final CharSequence strToBeFiltered,
													 final char[] charsToFilter,final String replacingStr) {
		if (strToBeFiltered == null) return null;
		if (charsToFilter == null || Strings.isNullOrEmpty(replacingStr)) return strToBeFiltered;

		// Copy the input String to a char[]
		char content[] = new char[strToBeFiltered.length()];
		for (int i = 0; i < strToBeFiltered.length(); i++) {
			content[i] = strToBeFiltered.charAt(i);
		}
		// filter chars and put the result in a StringBuffer
		StringBuilder result = new StringBuilder(content.length + 50);
		for (int i = 0; i < content.length; i++) {
			boolean replaced = false;
			for (int j = 0; j < charsToFilter.length; j++) {
				if (content[i] == charsToFilter[j]) {
					result.append(replacingStr);
					replaced = true;
				}
			}
			if (!replaced) result.append(content[i]);
		}
		return result.toString();
	}
	/**
	 * Filters chars
	 * @param strToBeFiltered
	 * @param isLegalCharPred
	 * @return
	 */
	public static CharSequence filterChars(final CharSequence strToBeFiltered,
										   final Predicate<Character> isLegalCharPred) {
		StringBuffer out = new StringBuffer(strToBeFiltered.length());

		for (int i = 0; i < strToBeFiltered.length(); i++) {
			char code = strToBeFiltered.charAt(i);
			if (isLegalCharPred.test(code)) {
				out.append(code);
			}
		}
		return out;
	}
	public static CharSequence replaceAccentedCharsByNonAccentedEquivalents(final CharSequence str) {
		if (str == null ) return str;
		// ver http://www.v3rgu1.com/blog/231/2010/programacion/eliminar-acentos-y-caracteres-especiales-en-java/
		// canonical decomposition is used:
		//		A character can be represented in many ways:
		//			- char c1 = 'á'							usual
		//			- char c2 = '\u00ed'					unicode
		//			- char[] c3 = {'\u0069', '\u0301'};		unicode en forma canonica
		//		Unicode canonical representation is composed by two chars: base letter + the accent
		//		this way á can be canonically represented as letter (\u0069) and accent (\u0301)
		String normalized = Normalizer.normalize(str,Normalizer.Form.NFD);
		// get ASCII chars
		Pattern pattern = Pattern.compile("[^\\p{ASCII}+]");
		return pattern.matcher(normalized).replaceAll("");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	URL ENCODE / DECODE www-form-urlencodec
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * @param str the {@link String} to be encoded
	 * @param charset
	 * @return the www-form-urlencoded {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence urlEncode(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		return URLEncoder.encode(str.toString(),charset);
	}
	/**
	 * Encodes in a www-form-urlencoded format using the default charset
	 * @param str the {@link String} to be encoded
	 * @return the www-form-urlencoded {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence urlEncode(final CharSequence str) {
		return StringEncodeUtils.urlEncode(str,Charset.defaultCharset());
	}
	/**
	 * Decodes a www-form-urlencodec {@link String} using the default charset
	 * @param str the {@link String} to be decoded
	 * @param charset
	 * @return the decoded {@link String}
	 * @throws DecoderException if the decoding could not be done
	 */
	public static CharSequence urlDecode(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		return URLDecoder.decode(str.toString(),charset);
	}
	/**
	 * Decodes a www-form-urlencodec {@link String} using the default charset
	 * @param str the {@link String} to be decoded
	 * @return the decoded {@link String}
	 * @throws DecoderException if the decoding could not be done
	 */
	public static CharSequence urlDecode(final CharSequence str) {
		return StringEncodeUtils.urlDecode(str,Charset.defaultCharset());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENCODE / DECODE BASE64
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes in Base64
	 * @param str the {@link String} to be encoded
	 * @param charset
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence encodeBase64(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String encodedStr = new String(Base64.getEncoder()
											 .encode(str.toString().getBytes(charset)));	// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
																							// beware new String(data) = new String(data,Charset.defaultCharset())


		return encodedStr;
	}
	/**
	 * Encodes in Base64
	 * @param str the {@link String} to be encoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence encodeBase64(final CharSequence str) {
		return StringEncodeUtils.encodeBase64(str,Charset.defaultCharset());
	}
	/**
	 * Encodes a byte array in a base64 String
	 * @param bytes The byte array to be encoded
	 * @return The base64 {@link String}
	 */
	@Deprecated
	public static String encodeBase64String(final byte[] bytes) {
		return StringEncodeUtils.encodeBase64AsString(bytes);
	}
	/**
	 * Encodes a byte array in a base64 String
	 * @param bytes The byte array to be encoded
	 * @return The base64 {@link String}
	 */
	public static String encodeBase64AsString(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.getEncoder()
					 .encodeToString(bytes);
	}

	/**
	 * Encodes a byte array in a base64 String based on Encoder.RFC4648_URLSAFE
	 * @param bytes The byte array to be encoded
	 * @return The base64 {@link String}
	 */
	public static String encodeBase64URLSafeAsString(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.getUrlEncoder()
					 .encodeToString(bytes);
	}


	/**
	 * Encodes in Base64
	 * @param str the byte array to be encoded
	 * @return the Base64 dta in byte array format
	 * @throws EncoderException if the encoding could not be done
	 */
	public static byte[] encodeBase64(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.getEncoder()
					 .encode(bytes);
	}
	/**
	 * Encodes in Base64
	 *
	 * @param bytes the bytes to be encoded
	 * @param isChunked if true, the encoder will chunk the base64 output into 76 character blocks
	 * @param urlSafe if true, the encoder will output '-' and '_' instead of the usual '+' and '/'
	 * @return the Base64 data in byte array format
	 * @throws EncoderException if the encoding could not be done
	 */
	@Deprecated
	public static byte[] encodeBase64(final byte[] bytes,final boolean isChunked,final boolean urlSafe) {
		return StringEncodeUtils.encodeBase64(bytes);
	}
	/**
	 * Decodes a Base64 {@link String}
	 * @param str the {@link String} to be decoded
	 * @param charset
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeBase64(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String encodedStr = new String(Base64.getDecoder()
				  							 .decode(str.toString().getBytes(charset)));// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
																						// beware new String(data) = new String(data,Charset.defaultCharset())
		return encodedStr;
	}


	/**
	 * Decodes a Base64 RFC4648_URLSAFE {@link String}
	 * @param str the {@link String} to be decoded
	 * @param charset
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeURLBase64(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String encodedStr = new String(Base64.getUrlDecoder()
				  							 .decode(str.toString().getBytes(charset)));// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
																						// beware new String(data) = new String(data,Charset.defaultCharset())
		return encodedStr;
	}

	/**
	 * Decodes a Base64 {@link String}
	 * @param str the {@link String} to be decoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeBase64(final CharSequence str) {
		return StringEncodeUtils.decodeBase64(str,Charset.defaultCharset());
	}


	/**
	 * Decodes a  Base64 RFC4648_URLSAFE {@link String}
	 * @param str the {@link String} to be decoded
	 * @return the Base64 {@link String}
	 * @throws EncoderException if the encoding could not be done
	 */
	public static CharSequence decodeURLBase64(final CharSequence str) {
		return StringEncodeUtils.decodeURLBase64(str,Charset.defaultCharset());
	}


	/**
	 * Decodes a Base64 byte array
	 * @param bytes The byte array to be decoded
	 * @return The decoded data in byte array format
	 */
	public static byte[] decodeBase64(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.getDecoder()
					 .decode(bytes);
	}
	/**
	 * Decodes a Base64 RFC4648_URLSAF byte array
	 * @param bytes The byte array to be decoded
	 * @return The decoded data in byte array format
	 */
	public static byte[] decodeURLBase64(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base64.getUrlDecoder()
					 .decode(bytes);
	}


/////////////////////////////////////////////////////////////////////////////////////////
// 	HEX ENCODE DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts to hexadecimal
	 * @param data
	 * @return
	 */
	public static CharSequence encodeHex(final CharSequence data,final Charset charset) {
		return StringEncodeUtils.encodeHex(data.toString().getBytes(charset));
	}
	/**
	 * Converts to hexadecimal
	 * @param data
	 * @return
	 */
	public static CharSequence encodeHex(final CharSequence data) {
		return StringEncodeUtils.encodeHex(data,Charset.defaultCharset());
	}
	/**
	 * Converts to hexadecimal
	 * @param data
	 * @return
	 */
	public static String encodeHex(final byte[] data) {
		return HexFormat.of().formatHex(data);
	}
	/**
	 * Decodes from hexadecimal
	 * @param data
	 * @return
	 */
	public static CharSequence decodeHex(final CharSequence data) {
		return new String(StringEncodeUtils.decodeHex(data.toString().toCharArray()));
	}
	/**
	 * Decodes from hexadecimal
	 * @param data
	 * @param charset
	 * @return
	 */
	public static byte[] decodeHex(final char[] data,final Charset charset) {
		byte[] hexStr = new String(data).getBytes(charset);
		return BaseEncoding.base16()
	                       .decode(new String(hexStr));

	}
	/**
	 * Decodes from hexadecimal
	 * @param data
	 * @param charset
	 * @return
	 */
	public static byte[] decodeHex(final char[] data) {
		return StringEncodeUtils.decodeHex(data,Charset.defaultCharset());
	}
	@Deprecated
	public static byte[] decodeHexString(final char[] data) {
		return StringEncodeUtils.decodeHex(data);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ZBASE32: https://www.dcode.fr/z-base-32-encoding
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encodes in z-base-32: https://www.dcode.fr/z-base-32-encoding
	 * z-base-32 encoding is a variant of base 32 (standard RFC 3548) proposed by Zooko O'Whielacronx (https://philzimmermann.com/docs/human-oriented-base-32-encoding.txt)
	 * and made to optimize the writing and the reading by the humans.
	 * z-base-32 code uses 32 characters ybndrfg8ejkmcpqxot1uwisza345h769
	 * ie. the 36 alphanumeric characters excluding 4 characters that can be confused with others: 0,l,v,2.
	 * The order of the alphabet is also changed (instead of abcdefghijklmnopqrstuvwxyz234567) in order to favor the common letters (vowels) and make the coded messages pronounceable.
	 * Also, the superfluous characters = appearing at the end of base32 and base64 codes are removed.
	 * @param str
	 * @return
	 */
	public static CharSequence encodeZBase32(final CharSequence str) {
		if (str == null) return null;
		ZBase32 zbase = new ZBase32();
		CharSequence encoded = zbase.encode(str.toString());
		return encoded;
	}
	/**
	 * Decodes in z-base-32: https://www.dcode.fr/z-base-32-encoding
	 * z-base-32 encoding is a variant of base 32 (standard RFC 3548) proposed by Zooko O'Whielacronx (https://philzimmermann.com/docs/human-oriented-base-32-encoding.txt)
	 * and made to optimize the writing and the reading by the humans.
	 * z-base-32 code uses 32 characters ybndrfg8ejkmcpqxot1uwisza345h769
	 * ie. the 36 alphanumeric characters excluding 4 characters that can be confused with others: 0,l,v,2.
	 * The order of the alphabet is also changed (instead of abcdefghijklmnopqrstuvwxyz234567) in order to favor the common letters (vowels) and make the coded messages pronounceable.
	 * Also, the superfluous characters = appearing at the end of base32 and base64 codes are removed.
	 * @param str
	 * @return
	 */
	public static CharSequence decodeZBase32(final CharSequence str) {
		if (str == null) return null;
		ZBase32 zbase = new ZBase32();
		CharSequence decoded = zbase.decode(str.toString());
		return decoded;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENCODE / DECODE BASE45
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	* Encodes in Base45
	* @param str the {@link String} to be encoded
	* @param charset
	* @return the Base45 {@link String}
	* @throws EncoderException if the encoding could not be done
	*/
	public static CharSequence encodeBase45(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String encodedStr = new String(Base45.getEncoder()
											.encode(str.toString().getBytes(charset)));	// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
															                          	// beware new String(data) = new String(data,Charset.defaultCharset())
		return encodedStr;
	}
	/**
	* Encodes in Base45
	* @param str the {@link String} to be encoded
	* @return the Base45 {@link String}
	* @throws EncoderException if the encoding could not be done
	*/
	public static CharSequence encodeBase45(final CharSequence str) {
		return StringEncodeUtils.encodeBase45(str,Charset.defaultCharset());
	}
	/**
	* Encodes a byte array in a Base45 String
	* @param bytes The byte array to be encoded
	* @return The Base45 {@link String}
	*/
	public static String encodeBase45AsString(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base45.getEncoder()
					 .encodeToString(bytes);
	}
	/**
	* Encodes in Base45
	* @param str the byte array to be encoded
	* @return the Base45 data in byte array format
	* @throws EncoderException if the encoding could not be done
	*/
	public static byte[] encodeBase45(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
		return Base45.getEncoder()
					 .encode(bytes);
	}

	/**
	* Decodes a Base45 {@link String}
	* @param str the {@link String} to be decoded
	* @param charset
	* @return the Base45 {@link String}
	* @throws EncoderException if the encoding could not be done
	*/
	public static CharSequence decodeBase45(final CharSequence str,final Charset charset) {
		if (str == null) return null;
		String encodedStr = new String(Base45.getDecoder()
											 .decode(str.toString().getBytes(charset)));// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
						                                                                // beware new String(data) = new String(data,Charset.defaultCharset())
		return encodedStr;
	}
	/**
	* Decodes a Base45 {@link String}
	* @param str the {@link String} to be decoded
	* @return the Base45 {@link String}
	* @throws EncoderException if the encoding could not be done
	*/
	public static CharSequence decodeBase45(final CharSequence str) {
	return StringEncodeUtils.decodeBase45(str,Charset.defaultCharset());
	}
	/**
	* Decodes a Base64 byte array
	* @param bytes The byte array to be decoded
	* @return The decoded data in byte array format
	*/
	public static byte[] decodeBase45(final byte[] bytes) {
		if (bytes == null || bytes.length == 0) return null;
			return Base45.getDecoder()
							.decode(bytes);
	}
}
