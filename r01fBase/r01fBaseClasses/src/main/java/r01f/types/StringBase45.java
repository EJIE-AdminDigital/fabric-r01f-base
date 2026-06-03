package r01f.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import lombok.SneakyThrows;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringEncodeUtils;


@MarshallType(as="stringBase45")
public class StringBase45
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -1943008748673857090L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,String> DEFAULT_ENCODER_FUNCION = input -> StringEncodeUtils.encodeBase45AsString(input.getBytes());
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _stringBase45;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public StringBase45(final String stringBase45) {
		_stringBase45 = stringBase45;
	}
	public static StringBase45 fromBase45EncodedString(final String stringBase45) {
		return new StringBase45(stringBase45);
	}
	public static StringBase45 encode(final String rawString) {
		return StringBase45.encode(rawString,
								   DEFAULT_ENCODER_FUNCION);
	}
	public static StringBase45 encode(final String rawString,
							  		  final Function<String,String> encoder) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();
		return new StringBase45(encoder.apply(rawString));
	}
	public static StringBase45 encode(final byte[] rawBytes) {
		if (rawBytes == null || rawBytes.length == 0) throw new IllegalArgumentException();
 		return new StringBase45(StringEncodeUtils.encodeBase45AsString(rawBytes));
	}

	@SneakyThrows
	public static StringBase45 deflateAndEncode(final String rawString) {
		return StringBase45.deflateAndEncode(rawString,
								   			 DEFAULT_ENCODER_FUNCION,
								   			 false);
	}

	@SneakyThrows
	public static StringBase45 deflateAndEncode(final String rawString,final boolean nowrap) {
		return StringBase45.deflateAndEncode(rawString,
								   			 DEFAULT_ENCODER_FUNCION,
								   			 nowrap);
	}

	@SneakyThrows
	public static StringBase45 deflateAndEncode(final String rawString,
												final Function<String,String> encoder,final boolean nowrap) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();

		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawString.getBytes(),nowrap);

		// [2] - Encode base 45
		return StringBase45.encode(deflatedRawBytes);
	}
	@SneakyThrows
	public static StringBase45 deflateAndEncode(final byte[] rawBytes) {
		return deflateAndEncode(rawBytes,false);
	}
	@SneakyThrows
	public static StringBase45 deflateAndEncode(final byte[] rawBytes,final boolean nowrap) {
		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawBytes,nowrap);

		// [2] - Encode base 45
		return StringBase45.encode(deflatedRawBytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _stringBase45;
	}
	public byte[] getBytes() {
		return _stringBase45.getBytes();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	public String decode() {
		return StringEncodeUtils.decodeBase45(_stringBase45)
								.toString();
	}
	public byte[] decodeAsBytes() {
		return StringEncodeUtils.decodeBase45(_stringBase45.getBytes());
	}
	public InputStream decodeAsInputStream() {
		return new ByteArrayInputStream(this.decodeAsBytes());
	}

	@SneakyThrows
	public String decodeAndInflate() {
		return decodeAndInflate(false);
	}

	@SneakyThrows
	public String decodeAndInflate(final boolean nowrap) {
		// [1] - Decode
		String deflatedString = this.decode();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedString.getBytes(),nowrap);
		return new String(inflatedBytes);
	}
	@SneakyThrows
	public byte[] decodeAsBytesAndInflate() {
		return decodeAsBytesAndInflate(false) ;
	}
	@SneakyThrows
	public byte[] decodeAsBytesAndInflate(final boolean nowrap) {
		// [1] - Decode
		byte[] deflatedBytes = this.decodeAsBytes();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedBytes,nowrap);
		return inflatedBytes;
	}
	@SneakyThrows
	public InputStream decodeAsInputStreamAndInflate() {
		return new ByteArrayInputStream(this.decodeAsBytesAndInflate());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INFLATE / DEFLATE
/////////////////////////////////////////////////////////////////////////////////////////
	private static byte[] _deflate(final byte[] inflatedBytes,  final boolean nowrap) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION,
				                         nowrap);	// use or not a GZIP compatible version, by default no
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream,
																			 deflater);
		deflaterOutputStream.write(inflatedBytes);
		deflaterOutputStream.close();
		byte[] outDeflatedStringBytes = outputStream.toByteArray();

		return outDeflatedStringBytes;
	}

	/**
	 * If the parameter 'nowrap' is true then the ZLIB header and checksum fields will not be used.
	 *  This provides compatibility with the compression format used by both GZIP and PKZIP.
		Note: When using the 'nowrap' option it is also necessary to providean extra "dummy" byte as input. This is required by the ZLIB nativelibrary in order to support certain optimizations.

	 */
	private static byte[] _inflate(final byte[] deflatedBytes, final boolean nowrap) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(nowrap);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(outputStream,
																			 inflater);
		inflaterOutputStream.write(deflatedBytes);
		inflaterOutputStream.close();
		byte[] outInflatedStringBytes = outputStream.toByteArray();

		return outInflatedStringBytes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof StringBase45 b)) return false;
		return _stringBase45.equals(b.asString());
	}
	@Override
	public int hashCode() {
		return _stringBase45.hashCode();
	}
}
