package r01f.model.services;

import java.util.Objects;

import com.google.common.base.Splitter;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableType;
import r01f.exceptions.EnrichedThrowableTypeBase;
import r01f.exceptions.EnrichedThrowableTypeBuilder;
import r01f.exceptions.ExceptionSeverity;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;

/**
 * core service error codes
 */
@MarshallType(as="coreServiceErrorType")
@Accessors(prefix="_")
public final class COREServiceErrorType
     	   extends EnrichedThrowableTypeBase {

	private static final long serialVersionUID = -5755442159737379091L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="origin",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final COREServiceErrorOrigin _origin;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public COREServiceErrorType(@MarshallFrom("origin")   final COREServiceErrorOrigin origin,
			                    @MarshallFrom("name")     final String name,
			                    @MarshallFrom("group")    final int group, @MarshallFrom("code") final int code,
			                    @MarshallFrom("severity") final ExceptionSeverity severity) {
		super(name,
			  group,code,
			  severity);
		_origin = origin;
	}
	public COREServiceErrorType(final COREServiceErrorType other,final int code) {
		this(other.getOrigin(),
			 other.getName(),
			 other.getGroup(),code,
			 other.getSeverity());
	}
	public static EnrichedThrowableTypeBuilder<COREServiceErrorType> originatedAt(final COREServiceErrorOrigin origin) {
		return new EnrichedThrowableTypeBuilder<COREServiceErrorType>() {
						@Override
						protected COREServiceErrorType _build(final String name,
															  final int group,final int code,
															  final ExceptionSeverity severity) {
							return new COREServiceErrorType(origin,
															   name,
														       group,code,
														       severity);
						}
			   };
	}
	public static EnrichedThrowableTypeBuilder<COREServiceErrorType> originatedAtClient() {
		return COREServiceErrorType.originatedAt(COREServiceErrorOrigin.CLIENT);
	}
	public static EnrichedThrowableTypeBuilder<COREServiceErrorType> originatedAtServer() {
		return COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER);
	}
	public static EnrichedThrowableTypeBuilder<COREServiceErrorType> unknownOrigin() {
		return COREServiceErrorType.originatedAt(COREServiceErrorOrigin.UNKNOWN);
	}
	public static COREServiceErrorType fromEncodedString(final String str) {
		String[] splitted = StringSplitter.using(Splitter.on("#"))
										  .at(str)
										  .toArray();
		if (splitted.length != 5) throw new IllegalArgumentException("the " + EnrichedThrowableType.class + " encoded string " + str + " does NOT have a valid format!!");
		return new COREServiceErrorType(COREServiceErrorOrigin.valueOf(splitted[0]),
										splitted[1],
										Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),
										ExceptionSeverity.valueOf(splitted[4]));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public String encodeAsString() {
		return Strings.customized("{}#{}#{}#{}#{}",
								  _origin,
								  _name,
								  _group,_code,
								  _severity);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isServerError() {
		return _origin != null ? _origin == COREServiceErrorOrigin.SERVER : false;
	}
	public boolean isClientError() {
		return _origin != null ? _origin == COREServiceErrorOrigin.CLIENT : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final COREServiceErrorType other) {
		return this.equals(other);
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != this.getClass()) return false;
		COREServiceErrorType other = (COREServiceErrorType)obj;
		return super.equals(other)
			&& _origin == other.getOrigin();
	}
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),
						 	_origin);
	}
	
}