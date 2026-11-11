package r01f.oids.refs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.oids.PersistableObjectID;
import r01f.oids.PersistableObjectOID;
import r01f.util.types.Objects;

@Accessors(prefix="_")
public abstract class PersistableObjectWithIDRefBase<O extends PersistableObjectOID,I extends PersistableObjectID<O>>
			  extends PersistableObjectRefBase<O>
           implements IsPersistableObjectWithIDRef<O,I> {

    private static final long serialVersionUID = -3167331914287689392L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter protected I _id;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public PersistableObjectWithIDRefBase() {
		// default no-args constructor
	}
    public <R extends PersistableObjectWithIDRefBase<O,I>> PersistableObjectWithIDRefBase(final R other) {
    	super(other);
		_id = other.getId();
    }
    public PersistableObjectWithIDRefBase(final O oid) {
    	super(oid);
    }
    public PersistableObjectWithIDRefBase(final I id) {
    	super();
    	_id = id;
    }
	public PersistableObjectWithIDRefBase(final O oid,final I id) {
		this(oid);
		_id = id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AS STRING
/////////////////////////////////////////////////////////////////////////////////////////    
	@Override
	public String asString() {
		return String.format("%s;%s",
							 this.getOid(),this.getId());
	}
	@Override
	public String toString() {
		return this.asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean equalsEitherOidOrId(final PersistableObjectWithIDRefBase<?,?> other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other.getClass() != this.getClass()) return false;
		
		PersistableObjectWithIDRefBase<?,?> otherRef = other;
		return super.equals(otherRef)								// equals by oid
			|| Objects.areEqual(this.getId(),otherRef.getId(),		// equals by id
								(i1,i2) -> i1.is(i2));
	}
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other.getClass() != this.getClass()) return false;
		
		PersistableObjectWithIDRefBase<?,?> otherRef = (PersistableObjectWithIDRefBase<?,?>)other;
		return super.equals(otherRef)
			&& Objects.areEqual(this.getId(),otherRef.getId(),
								(i1,i2) -> i1.is(i2));
	}
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),
							this.getId());
	}
}
