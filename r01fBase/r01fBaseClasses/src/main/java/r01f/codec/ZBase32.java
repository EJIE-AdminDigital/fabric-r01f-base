package r01f.codec;

import java.util.ArrayList;
import java.util.List;

/**
 * https://www.crockford.com/base32.html
 * https://www.dcode.fr/z-base-32-encoding 
 * z-base-32 encoding is a variant of base 32 (standard RFC 3548) proposed by Zooko O'Whielacronx (https://philzimmermann.com/docs/human-oriented-base-32-encoding.txt)
 * and made to optimize the writing and the reading by the humans. 
 * z-base-32 code uses 32 characters ybndrfg8ejkmcpqxot1uwisza345h769 
 * ie. the 36 alphanumeric characters excluding 4 characters that can be confused with others: 0,l,v,2. 
 * The order of the alphabet is also changed (instead of abcdefghijklmnopqrstuvwxyz234567) in order to favor the common letters (vowels) and make the coded messages pronounceable.
 * Also, the superfluous characters = appearing at the end of base32 and base64 codes are removed.
 * 
 * Usage:
 * <pre class='brush:java'>
 *		ZBase32 zbase = new ZBase32();
 *		
 *		String message = "Hello, world!";
 *		String encoded = zbase.encode(message);
 *		System.out.println("Encoded: " + encoded);
 *		
 *		String decoded = zbase.decode(encoded);
 *		System.out.println("Decoded: " + decoded);
 * </pre>
 *
 */
public class ZBase32 {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected char[] _encoding = "ybndrfg8ejkmcpqxot1uwisza345h769".toCharArray();
	protected byte[] _decoding;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public ZBase32() {
		_decoding = new byte[0x80];
		for (int i = 0; i < _encoding.length; i++) {
			_decoding[_encoding[i]] = (byte)i;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENCODE / DECODE
/////////////////////////////////////////////////////////////////////////////////////////	
	public CharSequence encode(final String in) {
		if (in == null) return null;
		
		return this.encode(in.getBytes());
	}
	public CharSequence encode(final CharSequence in) {
		if (in == null) return null;
		
		return this.encode(in.toString().getBytes());	// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
	}
	public CharSequence encode(final byte[] in) {
		if (in == null) return null;
		
		byte[] input = in;
		StringBuilder output = new StringBuilder();
		
		int special = input.length % 5;
		int normal  = input.length - special;
		
		for (int i = 0; i < normal; i += 5) {
			output.append(_encoding[((input[i] & 0xff) >> 3) & 0x1f]);
			output.append(_encoding[(((input[i] & 0xff) << 2) | ((input[i + 1] & 0xff) >> 6)) & 0x1f]);
			output.append(_encoding[((input[i + 1] & 0xff) >> 1) & 0x1f]);
			output.append(_encoding[(((input[i + 1] & 0xff) << 4) | ((input[i + 2] & 0xff) >> 4)) & 0x1f]);
			output.append(_encoding[(((input[i + 2] & 0xff) << 1) | ((input[i + 3] & 0xff) >> 7)) & 0x1f]);
			output.append(_encoding[((input[i + 3] & 0xff) >> 2) & 0x1f]);
			output.append(_encoding[(((input[i + 3] & 0xff) << 3) | ((input[i + 4] & 0xff) >> 5)) & 0x1f]);
			output.append(_encoding[(input[i + 4] & 0xff) & 0x1f]);
		}
		
		switch(special) {
		case 1:
			output.append(_encoding[((input[normal] & 0xff) >> 3) & 0x1f]);
			output.append(_encoding[((input[normal] & 0xff) >> 2) & 0x1f]);
			output.append("======");
			break;
		case 2:
			output.append(_encoding[((input[normal] & 0xff) >> 3) & 0x1f]);
			output.append(_encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]);
			output.append(_encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]);
			output.append(_encoding[((input[normal + 1] & 0xff) << 4) & 0x1f]);
			output.append("====");
			break;
		case 3:
			output.append(_encoding[((input[normal] & 0xff) >> 3) & 0x1f]);
			output.append(_encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]);
			output.append(_encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]);
			output.append(_encoding[(((input[normal + 1] & 0xff) << 4) | ((input[normal + 2] & 0xff) >> 4)) & 0x1f]);
			output.append(_encoding[((input[normal + 2] & 0xff) << 1) & 0x1f]);
			output.append("===");
			break;
		case 4:
			output.append(_encoding[((input[normal] & 0xff) >> 3) & 0x1f]);
			output.append(_encoding[(((input[normal] & 0xff) << 2) | ((input[normal + 1] & 0xff) >> 6)) & 0x1f]);
			output.append(_encoding[((input[normal + 1] & 0xff) >> 1) & 0x1f]);
			output.append(_encoding[(((input[normal + 1] & 0xff) << 4) | ((input[normal + 2] & 0xff) >> 4)) & 0x1f]);
			output.append(_encoding[(((input[normal + 2] & 0xff) << 1) | ((input[normal + 3] & 0xff) >> 7)) & 0x1f]);
			output.append(_encoding[((input[normal + 3] & 0xff) >> 2) & 0x1f]);
			output.append(_encoding[((input[normal + 3] & 0xff) << 3) & 0x1f]);
			output.append("=");
			break;
		}
		return output.toString();
	}
	public CharSequence decode(final CharSequence in) {
		if (in == null) return null;
		return this.decode(in.toString());
	}
	public CharSequence decode(final String in) {
		if (in == null) return null;
		
		int expOrgSize = (int)Math.floor(in.length() / 1.6);
		int expPadSize = ((int)Math.ceil(expOrgSize / 5.0)) * 8;
		
		StringBuilder s= new StringBuilder(in);
		for (int i = 0; i < expPadSize; i++) {
			s.append("=");
		}
		
		char[] data	= s.toString().toLowerCase().toCharArray();
		int dataLen	= data.length;
		while (dataLen > 0) {
			if (!_ignore(data[dataLen - 1])) break;
			
			dataLen--;
		}
		
		List<Byte> output = new ArrayList<>();
		int i = 0;
		int e = dataLen - 8;
		for (i = _next(data, i, e); i < e; i = _next(data, i, e)) {
			byte b1 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b2 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b3 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b4 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b5 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b6 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b7 = _decoding[data[i++]];
			i = _next(data,i,e);
			byte b8 = _decoding[data[i++]];
			
			output.add((byte) ((b1 << 3) | (b2 >> 2)));
			output.add((byte) ((b2 << 6) | (b3 << 1) | (b4 >> 4)));
			output.add((byte) ((b4 << 4) | (b5 >> 1)));
			output.add((byte) ((b5 << 7) | (b6 << 2) | (b7 >> 3)));
			output.add((byte) ((b7 << 5) | b8));
		}
		
		if (data[dataLen - 6] == '=') {
			output.add((byte) (((_decoding[data[dataLen - 8]]) << 3) | (_decoding[data[dataLen - 7]] >> 2)));
		} else if (data[dataLen - 4] == '=') {
			output.add((byte) (((_decoding[data[dataLen - 8]]) << 3) | (_decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((_decoding[data[dataLen - 7]]) << 6) | (_decoding[data[dataLen - 6]] << 1) | (_decoding[data[dataLen - 5]] >> 4)));
		} else if (data[dataLen - 3] == '=') {
			output.add((byte) (((_decoding[data[dataLen - 8]]) << 3) | (_decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((_decoding[data[dataLen - 7]]) << 6) | (_decoding[data[dataLen - 6]] << 1) | (_decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((_decoding[data[dataLen - 5]]) << 4) | (_decoding[data[dataLen - 4]] >> 1)));
		} else if (data[dataLen - 1] == '=') {
			output.add((byte) (((_decoding[data[dataLen - 8]]) << 3) | (_decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((_decoding[data[dataLen - 7]]) << 6) | (_decoding[data[dataLen - 6]] << 1) | (_decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((_decoding[data[dataLen - 5]]) << 4) | (_decoding[data[dataLen - 4]] >> 1)));
			output.add((byte) (((_decoding[data[dataLen - 4]]) << 7) | (_decoding[data[dataLen - 3]] << 2) | (_decoding[data[dataLen - 2]] >> 3)));
		} else {
			output.add((byte) (((_decoding[data[dataLen - 8]]) << 3) | (_decoding[data[dataLen - 7]] >> 2)));
			output.add((byte) (((_decoding[data[dataLen - 7]]) << 6) | (_decoding[data[dataLen - 6]] << 1) | (_decoding[data[dataLen - 5]] >> 4)));
			output.add((byte) (((_decoding[data[dataLen - 5]]) << 4) | (_decoding[data[dataLen - 4]] >> 1)));
			output.add((byte) (((_decoding[data[dataLen - 4]]) << 7) | (_decoding[data[dataLen - 3]] << 2) | (_decoding[data[dataLen - 2]] >> 3)));
			output.add((byte) (((_decoding[data[dataLen - 2]]) << 5) | (_decoding[data[dataLen - 1]])));
		}
		
		byte[] b = _toPrimitive(output.toArray(new Byte[0]));
		return _trim(new String(b));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String _trim(final String s) {
		char[] c = s.toCharArray();
		int end  = c.length;
		
		for (int i = c.length - 1; i >= 0; i--) {
			if ((c[i]) != 0) break;
			end = i;
		}
		return s.substring(0, end);
	}
	private static int _next(final char[] data,final int i,final int e) {
		int outI = i;
		while ((outI < e) && _ignore(data[outI])) outI++;
		return outI;
	}
	private static boolean _ignore(final char c) {
		return (c == '\n') || (c == '\r') || (c == '\t') || (c == ' ') || (c == '-');
	}
	private static byte[] _toPrimitive(final Byte[] bytes) {
		byte[] result = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}
}
