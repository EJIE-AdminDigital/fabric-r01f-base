package r01f.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.oids.OID;
import r01f.oids.PersistableObjectID;
import r01f.oids.PersistableObjectOID;

@Accessors(prefix="_")
public abstract class PersistableModelObjectWithIDBase<O extends PersistableObjectOID,I extends PersistableObjectID<O>,
										      		   SELF_TYPE extends PersistableModelObjectWithIDBase<O,I,SELF_TYPE>> 
			  extends PersistableModelObjectBase<O,SELF_TYPE> 
		   implements PersistableModelObjectWithID<O,I> {

    private static final long serialVersionUID = -3187286542277823727L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A business id
	 */
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected I _id;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    protected PersistableModelObjectWithIDBase() {
    	super();
    }
    protected PersistableModelObjectWithIDBase(final O oid) {
		super(oid);
	}
    protected PersistableModelObjectWithIDBase(final O oid,
											   final I id) {
		super(oid);
		_id = id;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public void unsafeSetId(final OID id) {
		_id = (I)id;
	}
}
