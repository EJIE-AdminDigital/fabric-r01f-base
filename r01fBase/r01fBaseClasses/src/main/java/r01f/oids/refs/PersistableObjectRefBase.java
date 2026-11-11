package r01f.oids.refs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.oids.PersistableObjectOID;
import r01f.util.types.Objects;

@Accessors(prefix="_")
public abstract class PersistableObjectRefBase<O extends PersistableObjectOID>
           implements IsPersistableObjectRef<O> {

    private static final long serialVersionUID = -3167331914287689392L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
////////// OID
    @MarshallField(as="oid",
                   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter protected O _oid;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public PersistableObjectRefBase() {
		// default no-args constructor
	}
    public <R extends PersistableObjectRefBase<O>> PersistableObjectRefBase(final R other) {
    	_oid = other.getOid();
    }
    public PersistableObjectRefBase(final O oid) {
		_oid = oid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////    
	@Override
	public String asString() {
		return String.format("%s",
							 this.getOid());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////    
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other.getClass() != this.getClass()) return false;
		
		PersistableObjectRefBase<?> otherRef = (PersistableObjectRefBase<?>)other;
		return Objects.areEqual(this.getOid(),otherRef.getOid(),
								(o1,o2) -> o1.is(o2));
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getOid());
	}
}
