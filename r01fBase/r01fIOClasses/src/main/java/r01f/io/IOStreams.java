package r01f.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Streams utilities (wrap around apache commons io IOUtils)
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class IOStreams {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int DEFAULT_BUFFER_SIZE = 4 * 1024;		// 4K
/////////////////////////////////////////////////////////////////////////////////////////
//	CLOSE & FLUSH
/////////////////////////////////////////////////////////////////////////////////////////
	public static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) closeable.close();
		} catch (final IOException ioe) {
			// ignore
		}
	}
	public static void flushQuietly(final OutputStream os) {
		try {
			if (os != null) os.flush();;
		} catch (final IOException ioe) {
			// ignore
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  InputStream
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an {@link InputStream} as a byte array
	 * @param is
	 * @return the byte array from the {@link InputStream}
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] inputStreamBytes(final InputStream is) throws IOException {
		try {
			byte[] outBytes = IOUtils.toByteArray(is);
			return outBytes;
		} finally {
			IOStreams.closeQuietly(is);
		}
	}
	/**
	 * Loads the wrapped {@link InputStream} as a String
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamAsString(final InputStream is) throws IOException {
		return IOStreams.inputStreamAsString(is,Charset.defaultCharset());
	}
	/**
	 * Loads the wrapped {@link InputStream} as a String
	 * @param is
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamAsString(final InputStream is,final Charset charSet) throws IOException {
		Writer writer = null;
		if (is != null) {
			writer = new StringWriter();
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,charSet));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer,0,n);
				}
			} finally {
				IOStreams.closeQuietly(is);
			}
		}
		return writer != null ? writer.toString()
							  : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies the contents from the given {@link InputStream} to the given {@link OutputStream}
	 * @param in
	 * @param out
	 * @param closeWhenFinish true guarantees, that java.io.OutputStream.close() is called on the stream. false indicates, that only java.io.OutputStream.close() should be called finally
	 * @return Number of bytes, which have been copied.
	 */
	public static long copy(final InputStream in,final OutputStream out,
			 				final boolean closeWhenFinish) throws IOException {
		return IOStreams.copy(in,
						 	  out,
						 	  closeWhenFinish,
						 	  DEFAULT_BUFFER_SIZE);
	}
	/**
	 * Copies the contents from the given {@link InputStream} to the given {@link OutputStream}
	 * @param in
	 * @param out
	 * @param closeWhenFinish true guarantees, that java.io.OutputStream.close() is called on the stream. false indicates, that only java.io.OutputStream.close() should be called finally
	 * @param bufferSize
	 * @return Number of bytes, which have been copied.
	 */
	public static long copy(final InputStream in,final OutputStream out,
							final boolean closeWhenFinish,
							final int bufferSize) throws IOException {
		if (out == null) return 0;
		long totalCopied = 0;
		try {
			totalCopied = IOUtils.copy(in,out,
									   bufferSize);
//			byte[] pBuffer = new byte[bufferSize];
//			for (;;) {
//				int res = in.read(pBuffer);
//				if (res == -1) break;
//				if (res > 0) {
//					totalCopied += res;
//					if (out != null) out.write(pBuffer,0,res);
//				}
//			}
			if (out != null) out.flush();
		} finally {
			if (in != null) IOStreams.closeQuietly(in);
			if (closeWhenFinish) IOStreams.closeQuietly(out);
		}
		return totalCopied;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  READABLE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Copies all {@link Readable} content to a {@link Writer}
	 * @param r
	 * @param w
	 * @throws IOException
	 */
	public static void copy(final Readable r,final Writer w) throws IOException {
		IOStreams.copy(r,w,
					   DEFAULT_BUFFER_SIZE);
	}
	/**
	 * Copies all {@link Readable} content to a {@link Writer}
	 * @param r
	 * @param w
	 * @throws IOException
	 */
	public static void copy(final Readable r,final Writer w,
							final int buffSize) throws IOException {
		CharBuffer buff = CharBuffer.allocate(buffSize);
		char[] chars = buff.array();

		while (r.read(buff) != IOUtils.EOF) { // Read from channel until EOF
		  // Put the char buffer into drain mode, and write its contents
		  // to the Writer, reading them from the backing array.
		  buff.flip();
		  w.write(chars,
				  buff.position(),buff.remaining());
		  buff.clear(); 		// Clear the character buffer
		}
	}
	/**
	 * Writes chars from a {@link String} to bytes on an
	 * {@link OutputStream} using the specified character encoding using {@link String#getBytes(String)}.
	 * 
	 * Character encoding names can be found at http://www.iana.org/assignments/character-sets
	 * 
	 * @param str the {@link String} to write, null ignored
	 * @param os the {@link OutputStream} to write to
	 * @param charset the requested charset, null means platform default
	 * @throws IOException				 if an I/O error occurs
	 */
	public static void write(final String str,final OutputStream os,
							 final Charset charset) throws IOException {
		IOUtils.write(str,os,
					  charset);
	}
}
