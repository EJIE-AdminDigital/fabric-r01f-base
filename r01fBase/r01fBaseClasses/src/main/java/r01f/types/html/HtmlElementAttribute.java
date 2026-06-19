package r01f.types.html;

import java.util.Objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.FactoryFrom;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.StringEscapeUtils;
import r01f.util.types.Strings;

@Immutable
@MarshallType(as="htmlElementAttribute")
@Accessors(prefix="_")
public class HtmlElementAttribute 
  implements CanBeRepresentedAsString {
	
	private static final long serialVersionUID = 4346957108570667683L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final String _id;
	
	@MarshallField(as="value",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final String _value;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlElementAttribute(@MarshallFrom("id") final String id,@MarshallFrom("value") final String val) {
		_id = id;
		_value = val;
	}
	public static HtmlElementAttribute create(final String id,final String val) {
		return new HtmlElementAttribute(id,val);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public <T> T getValueAs(final FactoryFrom<String,T> factory) {
		return factory.from(_value);
	}
	@Override
	public String asString() {
		return Strings.isNOTNullOrEmpty(_value) ? Strings.customized("{}='{}'",
								  									 _id,StringEscapeUtils.escapeHTML(_value))
												: "";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (this == o) return true;
		if (!(o instanceof HtmlElementAttribute)) return false;
		
		HtmlElementAttribute other = (HtmlElementAttribute)o;
		return _idEqs(other) && _valueEqs(other);
	}
	private boolean _idEqs(final HtmlElementAttribute other) {
		return this.getId() != null 
		    && other.getId() != null ? this.getId().equals(other.getId()) 
							 		 : this.getId() != null
							 		&& other.getId() == null ? false
							 						 		 : this.getId() == null
							 								&& other.getId() != null ? false
							 						 						 		 : true;	// both null
	}
	private boolean _valueEqs(final HtmlElementAttribute other) {
		return this.getValue() != null
		    && other.getValue() != null ? this.getValue().equals(other.getValue())
				  					    : this.getValue() != null
				  					   && other.getValue() == null ? false
				  							 					   : this.getValue() == null
				  							 					  && other.getValue() != null ? false
				  							 												  : true;	// both null
	}
	@Override
	public int hashCode() {
		return Objects.hash(_id,_value);
	}
	public boolean hasSameIdAs(final HtmlElementAttribute other) {
		return _idEqs(other);
	}
	public boolean isIdEqualTo(final String id) {
		return _id != null && id != null ? _id.equals(id)
										 : _id != null && id == null ? false
												 					 : _id == null && id != null ? false
												 							 					 : true;	// both null
	}
}
