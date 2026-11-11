package r01f.model;


import java.time.Instant;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.OidField;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.builders.facets.TrackableBuilder;
import r01f.model.facets.DirtyStateTrackableModelObject;
import r01f.model.facets.DirtyStateTrackableModelObject.HasDirtyStateTrackableModelObjectFacet;
import r01f.model.facets.delegates.DirtyStateTrackableModelObjectDelegate;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.oids.OID;
import r01f.oids.PersistableObjectOID;
import r01f.types.annotations.CompositionRelated;

@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public abstract class PersistableModelObjectBase<O extends PersistableObjectOID,
												 SELF_TYPE extends PersistableModelObjectBase<O,SELF_TYPE>>
		   implements PersistableModelObject<O>,					// can be persisted
		   			  HasDirtyStateTrackableModelObjectFacet {		// Changes in state can be tracked

	private static final long serialVersionUID = 6546937946507238664L;

/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Model object unique identifier
	 */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@OidField
	@Getter @Setter protected O _oid;
	/**
	 * Numeric unique id
	 */
	@MarshallField(as="numericId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _numericId;
	/**
	 * The field used to achieve the optimistic locking behavior at the persistence layer
	 * (see DBEntityBase)
	 * When persisting this object, optimistic locking is used (assumed that conflicts are unlike to happen)
	 * i.e. There are two web processes running in parallel, both processing the
	 * 		stock of an store item
	 * 		... let's say that initially we have stock=100
	 * 			----------[100]----------
	 * 			|						|
	 * 		  Load					   Load
	 *		    |-1						|-1
	 *		  [99]					  [99]
	 *		    |						|
	 *		  Save					  Save
	 *		    |---------[99]			|
	 *		  		      [99]----------| <---WTF!! the stock should have been 98
	 *		  									    but it ends being 99: WRONG!!
	 * To prevent this situation a last update timestamp or an incrementing version is used
	 * Every time a process want to update an entity it MUST tell us what the version is so
	 * if a conflict occurs it could be detected:
	 *
	 * 			----------[100]----------
	 * 			|	   (version=1)		|
	 * 			|						|
	 * 		  Load 					   Load
	 * 	   (version=1) 			   (version=1)
	 *		    |-1						|-1
	 *		  [99]					  [99]
	 *		    |		   			    |
	 *		  Save					    |
	 *	  (version=1)				    |
	 *		    |---------[99]		    |
	 *		  	   (version=2)	        |
	 *		  			|		      Save
	 *		  		CONFLICT!<--(Version=1)
	 *
	 * As seen, to be able to detect conflicts:
	 * 		- A version number (a timestamp) MUST be stored with the record
	 * 		- The version number MUST be loaded alongside the record and stored at the processing client
	 * 		- The version number MUST be send alongside the record in any update operation
	 * 		  so the received version could be compared with the provided one
	 */
	@MarshallField(as="entityVersion",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _entityVersion;
	/**
	 * Create & update info
	 */
	@CompositionRelated @ConvertToDirtyStateTrackable	// force this object trackable
	@MarshallField(as="trackingInfo")
	@Getter @Setter protected ModelObjectTracking _trackingInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistableModelObjectBase() {
		// default no-args constructor
	}
	public PersistableModelObjectBase(final PersistableModelObjectBase<O,SELF_TYPE> other) {
		_oid = other.getOid();
		_numericId = other.getNumericId();
	}
	public PersistableModelObjectBase(final O oid) {
		_oid = oid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE withOid(final O oid) {
		_oid = oid;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withNumericId(final long id) {
		_numericId = id;
		return (SELF_TYPE)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasOid
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public void unsafeSetOid(final OID oid) {
		this.setOid((O)oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DirtyStateTrackable facet accessor
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public DirtyStateTrackableModelObject asDirtyStateTrackable() {
		return new DirtyStateTrackableModelObjectDelegate<>((SELF_TYPE)this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TRACKABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public TrackableBuilder<SELF_TYPE,SELF_TYPE> builderForTrackable() {
		return new TrackableBuilder<>((SELF_TYPE)this,
									  (SELF_TYPE)this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Instant getCreatedAt() {
		return this.getTrackingInfo() != null ? this.getTrackingInfo().getCreatedAt()
									   		  : null;
	}
	@Override
	public Instant getLastUpdatedAt() {
		return this.getTrackingInfo() != null ? this.getTrackingInfo().getLastUpdatedAt() != null
														? this.getTrackingInfo().getLastUpdatedAt()
														: this.getTrackingInfo().getCreatedAt() != null
																? this.getTrackingInfo().getCreatedAt()
																: null
											   : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public <P extends PersistableModelObjectBase<O,SELF_TYPE>> void copyFrom(final P other) {
		if (other == null) return;
		_oid = other.getOid();
		_numericId = other.getNumericId();
		_entityVersion = other.getEntityVersion();
		if (other.getTrackingInfo() != null) {
			if (this.getTrackingInfo() == null) this.setTrackingInfo(new ModelObjectTracking());
			this.getTrackingInfo()
				.copyFrom(other.getTrackingInfo());
		} else {
			this.setTrackingInfo(null);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof PersistableModelObjectBase)) return false;

		PersistableModelObjectBase<?,?> other = (PersistableModelObjectBase<?,?>)obj;
		if (this.getEntityVersion() != other.getEntityVersion()) return false;
		if (this.getNumericId() != other.getNumericId()) return false;

		if (this.getOid() != null && this.getOid().isNOT(other.getOid())) return false;
		if (other.getOid() != null && other.getOid().isNOT(this.getOid())) return false;

		if (!Objects.equal(this.getTrackingInfo(),other.getTrackingInfo())) return false;
		return true;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this.getEntityVersion(),
								this.getNumericId(),
								this.getOid());
	}
}
