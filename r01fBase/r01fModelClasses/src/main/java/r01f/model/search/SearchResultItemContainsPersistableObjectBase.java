package r01f.model.search;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObjectTracking;
import r01f.model.PersistableModelObject;
import r01f.model.PersistableObject;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.oids.OID;
import r01f.oids.PersistableObjectOID;


@Accessors(prefix="_")
public abstract class SearchResultItemContainsPersistableObjectBase<O extends PersistableObjectOID,M extends PersistableModelObject<O>>
    	   implements SearchResultItemContainsPersistableObject<O,M> {

	private static final long serialVersionUID = -2917189770601857300L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="modelObject")
	@Getter @Setter protected M _modelObject;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  HasOID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public O getOid() {
		return _modelObject.getOid();
	}
	@Override
	public void unsafeSetOid(final OID oid) {
		_modelObject.unsafeSetOid(oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <U extends PersistableModelObject<?>> void unsafeSetModelObject(final U modelObject) {
		_modelObject = (M)modelObject;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public Class<M> getModelObjectType() {
		return (Class<M>)_modelObject.getClass();
	}
	@Override
	public <U extends PersistableObject> void unsafeSetModelObjectType(final Class<U> modelObjectType) {
		// nothing
	}
	@Override
	public long getModelObjectTypeCode() {
		return TypeMetaDataInspector.singleton()
									.getTypeMetaDataFor(this.getModelObjectType())
									.getTypeMetaData()
									.modelObjTypeCode();
	}
	@Override
	public void setModelObjectTypeCode(final long code) {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public long getEntityVersion() {
		return _modelObject.getEntityVersion();
	}
	@Override
	public void setEntityVersion(final long version) {
		_modelObject.setEntityVersion(version);
	}
	@Override
	public long getNumericId() {
		return _modelObject.getNumericId();
	}
	@Override
	public void setNumericId(final long numericId) {
		_modelObject.setNumericId(numericId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public ModelObjectTracking getTrackingInfo() {
		return _modelObject.getTrackingInfo();
	}
	@Override
	public void setTrackingInfo(final ModelObjectTracking trackingInfo) {
		// do nothing!
	}
}
