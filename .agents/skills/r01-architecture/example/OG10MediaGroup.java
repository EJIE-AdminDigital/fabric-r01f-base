package ogov.communic.mediaregister.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ogov.communic.mediaregister.api.model.base.OG10MediaRegisterPersistableObjectWithIDBase;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterIDs.OG10MediaGroupID;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterOIDs.OG10MediaGroupOID;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterRefs.OG10MediaGroupRef;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="mediaGroup")
@Accessors(prefix="_")
public class OG10MediaGroup 
     extends OG10MediaRegisterPersistableObjectWithIDBase<OG10MediaGroupOID,OG10MediaGroupID,
											 	   	   OG10MediaGroup> {
	
	private static final long serialVersionUID = -2277010493342957407L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name;
	                                                     
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public OG10MediaGroup() {
		super();
	}
	public OG10MediaGroup(final OG10MediaGroupOID oid,final OG10MediaGroupID id) {
		super(oid,id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public OG10MediaGroupRef getReference() {
		return OG10MediaGroupRef.from(_oid,_id);
	}
}