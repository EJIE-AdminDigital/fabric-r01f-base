package r01f.exceptions;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Interface for types representing an {@link EnrichedThrowable} sub-types
 * Usage:
 * <pre class='brush:java'>
 * 		@Accessors(prefix="_")
 * 		public class MyThrowableType
 * 		     extends EnrichedThrowableTypeBase {
 * 		}
 * 		...
 * 		Implement the EnrichedThrowableSubType interface (see {@link EnrichedThrowableSubTypeWrapper})
 * </pre>
 * @param <E>
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class EnrichedThrowableTypeBase 
	 	   implements EnrichedThrowableType,
	 	   			  Serializable {

	private static final long serialVersionUID = 3678346194695291108L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter protected final String _name;
	
	@MarshallField(as="group",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter protected final int _group;
	
	@MarshallField(as="code",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter protected final int _code;
	
	@MarshallField(as="severity",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter protected final ExceptionSeverity _severity;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean isIn(final EnrichedThrowableType... els) {
		if (CollectionUtils.isNullOrEmpty(els)) return false;
		boolean outIsIn = false;
		for (EnrichedThrowableType type : els) {
			if (this.is(type)) {
				outIsIn = true;
				break;
			}
		}
		return outIsIn;
	}
	@Override
	public boolean is(final EnrichedThrowableType el) {
		return this.equals(el);
	}
	@Override
	public boolean is(final int group,final int code) {
		return this.getGroup() != 0 && this.getGroup() == group
			&& this.getCode() != 0 && this.getCode() == code;
	}
	@Override
	public boolean hasSameSeverityAs(final EnrichedThrowableType other) {
		return _severity != null ? _severity.is(other.getSeverity()) : false;
	}
	@Override
	public boolean isMoreSeriousThan(final EnrichedThrowableType other) {
		return _severity != null ? _severity.isMoreSeriousThan(other.getSeverity()) : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return _name;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other.getClass() != this.getClass()) return false;
		EnrichedThrowableTypeBase otherType = (EnrichedThrowableTypeBase)other;
		return this.getCode() != 0 && this.getCode() == otherType.getCode()
			&& this.getGroup() != 0 && this.getCode() == otherType.getGroup()
			&& this.hasSameSeverityAs(otherType)
			&& this.getName() != null && this.getName().equals(otherType.getName());
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getGroup(),
							this.getCode(),
							this.getSeverity(),
							this.getName());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG                                                                          
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} group={} / code={} severity={}",
								  _name,
								  _group,_code,
								  _severity);
	}
}
