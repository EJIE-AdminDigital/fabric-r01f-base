package r01f.io;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Interface to be implemented by types iterating over a byte stream that will be processed in a chunked way
 * whether it's a byte array (ByteArrayChunkedIterator) or an InputStream (InputStreamChunkedIterator)
 * 
 * This is usefull when for a byte stream has to be chunked to be sent to a consummer (ie when uploading a file in a chunked way)
 * Usage:
 * <pre>
 *		ChunkedIterator bytesIt = new ChunkedInputStreamIterator(is);	 
 *		while (bytesIt.hasNext()) {
 *			// get a chunk of bytes
 *			int offset = bytesIt.getOffset();
 *			byte[] bytes = bytesIt.next();
 *
 *			// do something usefull with the chunk:
 *			uploadApi.uploadAttachmentFileChunk(documentOid,path,bytes,offset);
 *		}
 * </pre>
 */
public interface ChunkedStreamIterator 
         extends Iterator<byte[]>,
   		         Closeable {
        	 
		public int getOffset();	
}
