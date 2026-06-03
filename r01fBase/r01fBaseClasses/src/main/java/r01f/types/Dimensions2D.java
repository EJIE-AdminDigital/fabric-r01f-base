package r01f.types;

import java.io.Serializable;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Object dimensions (ie: an image).
 *
 * Example of use:
 * <pre>
 *     BufferedImage image = null;
 *     if (is != null) {
 * 	      image = ImageIO.read(is);
 *     }
 *     if (image != null) {
 * 	   	  int height = image.getHeight();
 * 	   	  int width = image.getWidth();
 * 	   	  return new Dimensions2D(width, height);
 *     }
 * </pre>
 */
@MarshallType(as="dimensions2D")
@Immutable
@Accessors(prefix="_")
public record Dimensions2D(@MarshallField(as="width",whenXml=@MarshallFieldAsXml(attr=true)) int width,
						   @MarshallField(as="height",whenXml=@MarshallFieldAsXml(attr=true)) int height)
  implements Serializable {
	private static final long serialVersionUID = -7580079139588296207L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Dimensions2D(@MarshallFrom("width") final int width,@MarshallFrom("height") final int height) {
		this.width = width;
		this.height = height;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTERs
/////////////////////////////////////////////////////////////////////////////////////////
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height();
	}
}
