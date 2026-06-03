package r01f.codec;

/**
 * Base91, stylized as basE91, is a form of binary-to-text encoding scheme developed by Joachim Henke
 *   It divides the binary data stream into 13-bit packets which are then encoded in 2 ASCII characters
 *   Assuming eight bits per ASCII character, on average the encoded size will be ³⁄₁₆ larger than the original.
 *   That is more efficient than the ¹⁄₃ increase of base64 (which uses four characters to represent three bytes of data)
 *  and ¹⁄₄ increase of base85 (which uses five characters to represent four bytes of data).
 *
 * Usage:
 * <pre class='brush:java'>
 *		Base91 base91 = new Base91(); *
 *		String message = "Hello, world!";
 *		String encoded = base91.encode(message);
 *		System.out.println("Encoded: " + encoded); *
 *		String decoded = base91.decode(encoded);
 *		System.out.println("Decoded: " + decoded);
 * </pre>
 */
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Modified version of Jochaim Henke’s original code from http://base91.sourceforge.net/
 * @author Joachim Henke (Original version)
 * @author Benedikt Waldvogel (Modifications)
 * @author R01F (Modifications)
 */
public class Base91 {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	static final float AVERAGE_ENCODING_RATIO = 1.2297f;
	static final byte[] ENCODING_TABLE;
	static final byte[] DECODING_TABLE;
	static final int BASE;
/////////////////////////////////////////////////////////////////////////////////////////
//	INIT CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	static {
		String ts = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"";
		ENCODING_TABLE = ts.getBytes(StandardCharsets.ISO_8859_1);
		BASE = ENCODING_TABLE.length;
		assert BASE == 91;

		DECODING_TABLE = new byte[256];
		for (int i = 0; i < 256; ++i)
			DECODING_TABLE[i] = -1;

		for (int i = 0; i < BASE; ++i)
			DECODING_TABLE[ENCODING_TABLE[i]] = (byte) i;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	public CharSequence encode(final String in) {
		if (in == null) return null;
		return new String(this.encode(in.getBytes()));
	}
	public CharSequence encode(final CharSequence in) {
		if (in == null) return null;
		return new String(this.encode(in.toString().getBytes()));	// beware charset!! str.getBytes() = str.getBytes(Charset.defaultCharset())
	}
	public byte[] encode(final byte[] data) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Base91OutputStream base91OutputStream = new Base91OutputStream(out);
		try {
			base91OutputStream.write(data);
			base91OutputStream.flush();
		} catch (final Throwable t) {
			throw new RuntimeException("Failed to encode", t);
		}
		return out.toByteArray();
	}
	public CharSequence decode(final CharSequence in) {
		if (in == null) return null;
		return this.decode(in.toString());
	}
	public CharSequence decode(final String in) {
		if (in == null) return null;
		return new String(this.decode(in.getBytes()));
	}
	public byte[] decode(final byte[] data) {
		int dbq = 0;
		int dn = 0;
		int dv = -1;

		int estimatedSize = Math.round(data.length / AVERAGE_ENCODING_RATIO);
		ByteArrayOutputStream output = new ByteArrayOutputStream(estimatedSize);

		for (int i = 0; i < data.length; ++i) {
			assert DECODING_TABLE[data[i]] != -1;
			if (dv == -1)
				dv = DECODING_TABLE[data[i]];
			else {
				dv += DECODING_TABLE[data[i]] * BASE;
				dbq |= dv << dn;
				dn += (dv & 8191) > 88 ? 13 : 14;
				do {
					output.write((byte) dbq);
					dbq >>= 8;
					dn -= 8;
				} while (dn > 7);
				dv = -1;
			}
		}

		if (dv != -1) {
			output.write((byte) (dbq | dv << dn));
		}

		return output.toByteArray();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INTERNALLY USED OUTPUTSTREAM
/////////////////////////////////////////////////////////////////////////////////////////	
	class Base91OutputStream 
  extends FilterOutputStream {
		private int ebq = 0;
		private int en = 0;

		public Base91OutputStream(final OutputStream out) {
			super(out);
		}

		@Override
		public void write(final int b) throws IOException {
			ebq |= (b & 255) << en;
			en += 8;
			if (en > 13) {
				int ev = ebq & 8191;

				if (ev > 88) {
					ebq >>= 13;
					en -= 13;
				} else {
					ev = ebq & 16383;
					ebq >>= 14;
					en -= 14;
				}
				out.write(ENCODING_TABLE[ev % BASE]);
				out.write(ENCODING_TABLE[ev / BASE]);
			}
		}
		@Override
		public void write(final byte[] data, final int offset, final int length) throws IOException {
			for (int i = offset; i < length; ++i) {
				write(data[i]);
			}
		}
		@Override
		public void flush() throws IOException {
			if (en > 0) {
				out.write(ENCODING_TABLE[ebq % BASE]);
				if (en > 7 || ebq > 90) {
					out.write(ENCODING_TABLE[ebq / BASE]);
				}
			}
			super.flush();
		}
	}
}