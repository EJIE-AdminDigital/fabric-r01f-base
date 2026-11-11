package ogov.communic.mediaregister.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ogov.communic.mediaregister.api.model.base.OG10MediaRegisterPersistableObjectWithIDBase;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterIDs.OG10MediaID;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterOIDs.OG10MediaOID;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterRefs.OG10MediaGroupRef;
import ogov.communic.mediaregister.api.model.oids.OG10MediaRegisterRefs.OG10MediaRef;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A media outlet (newspaper, radio station, tv channel, digital media...) registered in the media register
 * that usually belongs to a {@link OG10MediaGroup}
 */
@MarshallType(as="media")
@Accessors(prefix="_")
public class OG10Media
     extends OG10MediaRegisterPersistableObjectWithIDBase<OG10MediaOID,OG10MediaID,
                                                          OG10Media> {

    private static final long serialVersionUID = 4731586204918375562L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @MarshallField(as="name",escape=true)
    @Getter @Setter private String _name;

    @MarshallField(as="webSite",escape=true)
    @Getter @Setter private String _webSite;

    @MarshallField(as="mediaGroup")
    @Getter @Setter private OG10MediaGroupRef _mediaGroupRef;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    public OG10Media() {
        super();
    }
    public OG10Media(final OG10MediaOID oid,final OG10MediaID id) {
        super(oid,id);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public OG10MediaRef getReference() {
        return OG10MediaRef.from(_oid,_id);
    }
    public boolean belongsToMediaGroup() {
        return _mediaGroupRef != null;
    }
}
