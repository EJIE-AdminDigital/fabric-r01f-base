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

@MarshallType(as="stringBase64")
public class StringBase64
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = -4115901126395440560L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Function<String,String> DEFAULT_ENCODER_FUNCION = input -> StringEncodeUtils.encodeBase64AsString(input.getBytes());
	public static final Function<String,String> URL_SAFE_ENCODER_FUNCION = input -> StringEncodeUtils.encodeBase64URLSafeAsString(input.getBytes());
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final String _stringBase64;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public StringBase64(final String stringBase64) {
		_stringBase64 = stringBase64;
	}
	public static StringBase64 fromBase64EncodedString(final String stringBase64) {
		return new StringBase64(stringBase64);
	}
	public static StringBase64 encode(final String rawString) {
		return StringBase64.encode(rawString,
								   DEFAULT_ENCODER_FUNCION);
	}
	public static StringBase64 encodeURLSafe(final String rawString) {
		return StringBase64.encode(rawString,
				                  URL_SAFE_ENCODER_FUNCION);
	}

	public static StringBase64 encode(final String rawString,
							  		  final Function<String,String> encoder) {
		if (rawString == null || encoder == null) throw new IllegalArgumentException();
		return new StringBase64(encoder.apply(rawString));
	}
	public static StringBase64 encode(final byte[] rawBytes) {
		if (rawBytes == null || rawBytes.length == 0) throw new IllegalArgumentException();
 		return new StringBase64(StringEncodeUtils.encodeBase64AsString(rawBytes));
	}
	public static StringBase64 encodeURLSafe(final byte[] rawBytes) {
		if (rawBytes == null || rawBytes.length == 0) throw new IllegalArgumentException();
 		return new StringBase64(StringEncodeUtils.encodeBase64URLSafeAsString(rawBytes));
	}

	@SneakyThrows
	public static StringBase64 deflateAndEncode(final String rawString) {
		return StringBase64.deflateAndEncode(rawString.getBytes()
								   			// DEFAULT_ENCODER_FUNCION,         //<<--- No Sense! was not used at this function
								   			// true
								   			 );
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncodeURLSafe(final String rawString) {
		return StringBase64.deflateAndEncodeURLSafe(rawString.getBytes()
				                                 //// URL_SAFE_ENCODER_FUNCION,  //<<--- No Sense! was not used at this function
				                                  // false
				                                  );
	}

	@SneakyThrows
	public static StringBase64 deflateAndEncode(final String rawString,
												//final Function<String,String> encoder, //<<--- No Sense! was not used at this function
												final boolean noWrap) {
		//if (rawString == null || encoder == null) throw new IllegalArgumentException();

		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawString.getBytes(),noWrap);

		// [2] - Encode base 64
		return StringBase64.encode(deflatedRawBytes);
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncode(final byte[] rawBytes) {
		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawBytes,true);

		// [2] - Encode base 64
		return StringBase64.encode(deflatedRawBytes);
	}
	@SneakyThrows
	public static StringBase64 deflateAndEncodeURLSafe(final byte[] rawBytes) {
		// [1] - Deflate
		byte[] deflatedRawBytes = _deflate(rawBytes,false);

		// [2] - Encode base 64
		return StringBase64.encodeURLSafe(deflatedRawBytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _stringBase64;
	}
	public byte[] getBytes() {
		return _stringBase64.getBytes();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DECODE
/////////////////////////////////////////////////////////////////////////////////////////
	public String decode() {
		return StringEncodeUtils.decodeBase64(_stringBase64)
								.toString();
	}
	public String decodeURLSafe() {
		return StringEncodeUtils.decodeURLBase64(_stringBase64)
								.toString();
	}
	public byte[] decodeAsBytes() {
		return StringEncodeUtils.decodeBase64(_stringBase64.getBytes());
	}

	public byte[] decodeAsURLSafeBytes() {
		return StringEncodeUtils.decodeURLBase64(_stringBase64.getBytes());
	}

	public InputStream decodeAsInputStream() {
		return new ByteArrayInputStream(this.decodeAsBytes());
	}

	public InputStream decodeAsURLSafeInputStream() {
		return new ByteArrayInputStream(this.decodeAsURLSafeBytes());
	}

	@SneakyThrows
	public String decodeAndInflate() {
		// [1] - Decode
		String deflatedString = this.decode();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedString.getBytes(),true);
		return new String(inflatedBytes);
	}

	@SneakyThrows
	public String decodeAsURLSafeAndInflate() {
		// [1] - Decode
		String deflatedString = this.decodeURLSafe();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedString.getBytes(),false); //Base64 ULRSafe must be no wrapped
		return new String(inflatedBytes);
	}

	@SneakyThrows
	public byte[] decodeAsBytesAndInflate() {
		// [1] - Decode
		byte[] deflatedBytes = this.decodeAsBytes();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedBytes,true);
		return inflatedBytes;
	}

	@SneakyThrows
	public byte[] decodeAsURLSafeAsBytesAndInflate() {
		// [1] - Decode
		byte[] deflatedBytes = this.decodeAsURLSafeBytes();

		// [2] - Inflate
		byte[] inflatedBytes = _inflate(deflatedBytes,false); //Base64 ULRSafe must be no wrapped
		return inflatedBytes;
	}

	@SneakyThrows
	public InputStream decodeAsInputStreamAndInflate() {
		return new ByteArrayInputStream(this.decodeAsBytesAndInflate());
	}

	@SneakyThrows
	public InputStream decodeAsURLSafeInputStreamAndInflate() {
		return new ByteArrayInputStream(this.decodeAsURLSafeAsBytesAndInflate());
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	INFLATE / DEFLATE
/////////////////////////////////////////////////////////////////////////////////////////
	private static byte[] _deflate(final byte[] inflatedBytes,  boolean nowrap) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION,
				                         nowrap);	// use a GZIP compatible version
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream,
																			 deflater);
		deflaterOutputStream.write(inflatedBytes);
		deflaterOutputStream.close();
		byte[] outDeflatedStringBytes = outputStream.toByteArray();

		return outDeflatedStringBytes;
	}
	private static byte[] _inflate(final byte[] deflatedBytes, boolean nowrap) throws IOException {
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
		if (!(obj instanceof StringBase64 b)) return false;
		return _stringBase64.equals(b.asString());
	}
	@Override
	public int hashCode() {
		return _stringBase64.hashCode();
	}
}
