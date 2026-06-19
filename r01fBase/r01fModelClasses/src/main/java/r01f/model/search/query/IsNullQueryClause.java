package r01f.model.search.query;

import com.google.common.base.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.FieldID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Usage:
 * <pre class='brush:java'>
 * 		NullQueryClause<AppCode> eq = NullQueryClause.forField("myField")
 * 													 .of(AppCode.forId("r01"));
 * </pre>
 *
 * @param <T>
 */
@MarshallType(as="nullClause")
@Accessors(prefix="_")
@NoArgsConstructor
public class IsNullQueryClause
	 extends QueryClauseBase { 

	private static final long serialVersionUID = 5313898684928391593L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="isNull")
	@Getter @Setter private boolean _isNull;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	IsNullQueryClause(final FieldID fieldId,
					final boolean isNull) {
		super(fieldId);
		_isNull = isNull;
	}
	public static IsNullQueryClauseBuilder forField(final FieldID fieldId) {
		return new IsNullQueryClauseBuilder(fieldId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public QueryClauseType getClauseType() {
		return QueryClauseType.NULL;
	}	
	@Override @SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V)(Boolean)_isNull;
	}
	@Override @SuppressWarnings("unchecked")
	public <V> Class<V> getValueType() {
		return (Class<V>)Boolean.class;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof IsNullQueryClause)) return false;
		
		if (!super.equals(obj)) return false;	// checks fieldId
		
		IsNullQueryClause otherNull = (IsNullQueryClause)obj;
		return _isNull == otherNull.isNull();		// both null
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getFieldId(),_isNull);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class IsNullQueryClauseBuilder {
		private final FieldID _fieldId;
		
		public IsNullQueryClause isNull(final boolean isNull) {
			return new IsNullQueryClause(_fieldId,
									   isNull);
		}

	}
}
