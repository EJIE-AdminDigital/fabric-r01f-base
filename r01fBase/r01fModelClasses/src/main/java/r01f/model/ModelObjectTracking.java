package r01f.model;

import java.io.Serializable;
import java.time.Instant;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.security.context.SecurityContext;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;

/**
 * Object's Tracking info (author/a, create date, update date, etc)
 */
@MarshallType(as="tracking")
@Accessors(prefix="_")
public class ModelObjectTracking
  implements Serializable {

	private static final long serialVersionUID = 2286660970580116262L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
////////// --- CREATION
	/**
	 * Object create date
	 */
	@MarshallField(as="createdAt",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Instant _createdAt = Instant.now();
	/**
	 * Creator [security principal] oid
	 */
	@MarshallField(as="creatorSecurityPrincipalOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityPrincipalOID _creatorSecurityPrincipalOid;
	/**
	 * Creator [security principal] id
	 */
	@MarshallField(as="creatorSecurityPrincipalId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityPrincipalID _creatorSecurityPrincipalId;
	
////////// --- LAST UPDATE
	/**
	 * Object's create date
	 */
	@MarshallField(as="lastUpdatedAt",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Instant _lastUpdatedAt;
	/**
	 * Last update [security principal] oid
	 */
	@MarshallField(as="lastUpdaterSecurityPrincipalOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityPrincipalOID _lastUpdaterSecurityPrincipalOid;
	/**
	 * Last update [security principal] id
	 */
	@MarshallField(as="lastUpdaterSecurityPrincipalId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityPrincipalID _lastUpdaterSecurityPrincipalId;
	
////////// --- IMPERSONATION
	/**
	 * The [principal] impersonating (the impersonated principal is the one in the creator/lastUpdater fields)
	 */
	@MarshallField(as="impersonatinSecurityPrincipalOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private SecurityPrincipalOID _impersonatingSecurityPrincipalOid;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTracking() {
		// default no-args constructor
	}
	public ModelObjectTracking(final ModelObjectTracking other) {
		// create
		_createdAt = other.getCreatedAt();
		_creatorSecurityPrincipalOid = other.getCreatorSecurityPrincipalOid();
		_creatorSecurityPrincipalId = other.getCreatorSecurityPrincipalId();
		// last update
		_lastUpdatedAt = other.getLastUpdatedAt();
		_lastUpdaterSecurityPrincipalOid = other.getLastUpdaterSecurityPrincipalOid();
		_lastUpdaterSecurityPrincipalId = other.getLastUpdaterSecurityPrincipalId();
		// impersonation
		_impersonatingSecurityPrincipalOid = other.getImpersonatingSecurityPrincipalOid();
	}
	public ModelObjectTracking(final SecurityPrincipalOID creatorSecurityPrincipalOid,final SecurityPrincipalID creatorSecurityPrincipalId,final Instant createdAt) {
		this(creatorSecurityPrincipalOid,creatorSecurityPrincipalId,createdAt,
			 creatorSecurityPrincipalOid,creatorSecurityPrincipalId,createdAt);	// Updater is the same as creator
	}
	public ModelObjectTracking(final SecurityPrincipalOID creatorSecurityPrincipalOid,final SecurityPrincipalID creatorSecurityPrincipalId,final Instant createdAt,
							   final SecurityPrincipalOID UpdaterSecurityPrincipalOid,final SecurityPrincipalID UpdaterSecurityPrincipaId,final Instant lastUpdatedAt) {
		this(creatorSecurityPrincipalOid,creatorSecurityPrincipalId,createdAt,
			 UpdaterSecurityPrincipalOid,UpdaterSecurityPrincipaId,lastUpdatedAt,
			 null);
	}
	public ModelObjectTracking(final SecurityPrincipalOID creatorSecurityPrincipalOid,final SecurityPrincipalID creatorSecurityPrincipalId,final Instant createdAt,
							   final SecurityPrincipalOID UpdaterSecurityPrincipalOid,final SecurityPrincipalID UpdaterSecurityPrincipalId,final Instant lastUpdatedAt,
							   final SecurityPrincipalOID impersonatingSecurityPrincipalOid) {
		// create
		_createdAt = createdAt;
		_creatorSecurityPrincipalOid = creatorSecurityPrincipalOid;
		_creatorSecurityPrincipalId = creatorSecurityPrincipalId;
		// last update
		_lastUpdatedAt = lastUpdatedAt;
		_lastUpdaterSecurityPrincipalOid = UpdaterSecurityPrincipalOid;
		_lastUpdaterSecurityPrincipalId = UpdaterSecurityPrincipalId;
		// impersonation
		_impersonatingSecurityPrincipalOid = impersonatingSecurityPrincipalOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets that the provided user code has made an update just now
	 * @param userCode
	 * @return
	 */
	public ModelObjectTracking setModifiedBy(final SecurityPrincipalOID securityPrincipalOid,final SecurityPrincipalID userCode) {
		this.setLastUpdaterSecurityPrincipalOid(securityPrincipalOid);
		this.setLastUpdaterSecurityPrincipalId(userCode);
		this.setLastUpdatedAt(Instant.now());
		return this;
	}
	public void copyFrom(final ModelObjectTracking other) {
		if (other == null) return;
		// create
		_createdAt = other.getCreatedAt();
		_creatorSecurityPrincipalOid = other.getCreatorSecurityPrincipalOid();
		_creatorSecurityPrincipalId = other.getCreatorSecurityPrincipalId();
		// last update
		_lastUpdatedAt = other.getLastUpdatedAt();
		_lastUpdaterSecurityPrincipalOid = other.getLastUpdaterSecurityPrincipalOid();
		_lastUpdaterSecurityPrincipalId = other.getLastUpdaterSecurityPrincipalId();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MERGE
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTrackingMergePersistencePerformedOperationStep mergeWith(final ModelObjectTracking other) {
		return new ModelObjectTrackingMergePersistencePerformedOperationStep(other);
	}
	@RequiredArgsConstructor
	public class ModelObjectTrackingMergePersistencePerformedOperationStep {
		private final ModelObjectTracking _other;
		public ModelObjectTracking whenCreatedBy(final SecurityContext securityContext) {
			return _merge(_other,
						  PersistencePerformedOperation.CREATED,
						  securityContext);
		}
		public ModelObjectTracking whenUpdatedBy(final SecurityContext securityContext) {
			return _merge(_other,
						  PersistencePerformedOperation.UPDATED,
						  securityContext);
		}
		public ModelObjectTrackingMergesecurityContextStep when(final PersistencePerformedOperation op) {
			return new ModelObjectTrackingMergesecurityContextStep(_other,op);
		}
	}
	@RequiredArgsConstructor
	public class ModelObjectTrackingMergesecurityContextStep {
		private final ModelObjectTracking _other;
		private final PersistencePerformedOperation _op;

		public ModelObjectTracking by(final SecurityContext securityContext) {
			return _merge(_other,
						  _op,
						  securityContext);
		}
	}
	private ModelObjectTracking _merge(final ModelObjectTracking other,
									   final PersistencePerformedOperation op,
									   final SecurityContext securityContext) {
		if (other == null) return new ModelObjectTracking(this);	// clone

		// clone
		ModelObjectTracking out = new ModelObjectTracking(this);

		// and update
		if (op == PersistencePerformedOperation.CREATED) {
			out.setCreatedAt(Instant.now());
			out.setLastUpdatedAt(out.getCreatedAt());
			out.setCreatorSecurityPrincipalOid(other.getCreatorSecurityPrincipalOid() != null ? other.getCreatorSecurityPrincipalOid()
															    	: securityContext != null ? securityContext.getSecurityPrincipal()
															    											   .getOid()
															    			    			  : null);
			out.setCreatorSecurityPrincipalId(other.getCreatorSecurityPrincipalId() != null ? other.getCreatorSecurityPrincipalId()
																  	  : securityContext != null ? securityContext.getSecurityPrincipalId()
																  	  							: null);
			out.setLastUpdaterSecurityPrincipalId(null);
		}
		else if (op == PersistencePerformedOperation.UPDATED) {
			out.setLastUpdatedAt(Instant.now());
			out.setLastUpdaterSecurityPrincipalOid(securityContext != null ? securityContext.getSecurityPrincipal()
																							.getOid()
											    			  : null);
			out.setLastUpdaterSecurityPrincipalId(securityContext.getSecurityPrincipalId());
		}
		// impersonation
		out.setImpersonatingSecurityPrincipalOid(other.getImpersonatingSecurityPrincipalOid());
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ModelObjectTracking)) return false;

		ModelObjectTracking other = (ModelObjectTracking)obj;
		return Objects.equal(this.getCreatedAt(),other.getCreatedAt())
			&& Objects.equal(this.getCreatorSecurityPrincipalOid(),other.getCreatorSecurityPrincipalOid())
			&& Objects.equal(this.getCreatorSecurityPrincipalId(),other.getCreatorSecurityPrincipalId())
			&& Objects.equal(this.getLastUpdatedAt(),other.getLastUpdatedAt())
			&& Objects.equal(this.getLastUpdaterSecurityPrincipalId(),other.getLastUpdaterSecurityPrincipalId())
			&& Objects.equal(this.getLastUpdaterSecurityPrincipalOid(),other.getLastUpdaterSecurityPrincipalOid())
			&& Objects.equal(this.getImpersonatingSecurityPrincipalOid(),other.getImpersonatingSecurityPrincipalOid());
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_createdAt,_creatorSecurityPrincipalId,_creatorSecurityPrincipalOid,
								_lastUpdatedAt,_lastUpdaterSecurityPrincipalId,_lastUpdaterSecurityPrincipalOid,
								_impersonatingSecurityPrincipalOid);
	}
}
