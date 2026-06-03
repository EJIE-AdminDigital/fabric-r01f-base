package r01f.types;

import java.io.Serializable;
import java.util.Objects;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="color")
@Immutable
@Accessors(prefix="_")
public record Color(String theCode) 
  implements CanBeRepresentedAsString,
  			 Serializable {
	private static final long serialVersionUID = 5293979687149623550L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public Color(@MarshallFrom("code") final String theCode) {
		Objects.requireNonNull(theCode);
		this.theCode = theCode;
	}
	public static Color from(final String code) {
		return new Color(code);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTER
/////////////////////////////////////////////////////////////////////////////////////////
	public String getCode() {
		return this.theCode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return this.getCode();
	}
	@Override
	public String toString() {
		return this.asString();
	}
}