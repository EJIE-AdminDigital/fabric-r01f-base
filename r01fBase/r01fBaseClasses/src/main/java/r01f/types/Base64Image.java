package r01f.types;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.mime.MimeType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="image")
@Accessors(prefix="_")
@NoArgsConstructor
@AllArgsConstructor
public class Base64Image
  implements Serializable {

	private static final long serialVersionUID = 84594669257090530L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="base64image")
	@Getter @Setter private StringBase64 _base64image;
	
	@MarshallField(as="mimeType",
				   escape=true,
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MimeType _mimeType;
}
