package r01f.types.blob;

import java.io.InputStream;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.StringBase64;

/**
 * Models a image encoded as a Base64 string
 */
@MarshallType(as="image")
@Accessors(prefix="_")
public class ImageBase64 
  implements Serializable {

	private static final long serialVersionUID = 7753809367604902529L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name")
	@Getter @Setter	private String _name;
	
	@MarshallField(as="base64data",escape=true)
	@Getter @Setter	private StringBase64 _base64Data;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ImageBase64() {
		// default no-args constructor
	}
	public ImageBase64(final String name,final StringBase64 base64Data) {
		_name = name;
		_base64Data = base64Data;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public byte[] getImageAsByteArray() {
		return _base64Data != null ? _base64Data.decodeAsBytes()
									: null;
	}
	public InputStream getImageInputStream() {
		return _base64Data != null ? _base64Data.decodeAsInputStream()
								   : null;
	}
}
