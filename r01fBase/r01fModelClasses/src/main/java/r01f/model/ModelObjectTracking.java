package r01f.model;

import java.io.Serializable;
import java.time.Instant;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityOIDs.UserOID;

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
	/**
	 * Object create date
	 */
	@MarshallField(as="createDate",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Instant _createDate = Instant.now();
	/**
	 * Creator user oid
	 */
	@MarshallField(as="creatorUserOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private UserOID _creatorUserOid;
	/**
	 * Creator user code
	 */
	@MarshallField(as="creatorUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LoginID _creatorUserCode;
	/**
	 * Object's create date
	 */
	@MarshallField(as="lastUpdate",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Instant _lastUpdateDate;
	/**
	 * Last update user oid
	 */
	@MarshallField(as="lastUpdaterUserOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private UserOID _lastUpdatorUserOid;
	/**
	 * Last update user code
	 */
	@MarshallField(as="lastUpdaterUserCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private LoginID _lastUpdatorUserCode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ModelObjectTracking() {
		// default no-args constructor
	}
	public ModelObjectTracking(final ModelObjectTracking other) {
		_createDate = other.getCreateDate();
		_creatorUserOid = other.getCreatorUserOid();
		_creatorUserCode = other.getCreatorUserCode();
		_lastUpdateDate = other.getLastUpdateDate();
		_lastUpdatorUserOid = other.getLastUpdatorUserOid();
		_lastUpdatorUserCode = other.getLastUpdatorUserCode();
	}
	public ModelObjectTracking(final UserOID creatorOid,final LoginID creator,final Instant createDate) {
		this(creatorOid,creator,createDate,
			 creatorOid,creator,createDate);
	}
	public ModelObjectTracking(final UserOID creatorOid,final LoginID creator,final Instant createDate,
							   final UserOID updatorOid,final LoginID lastUpdator,final Instant lastUpdateDate) {
		_createDate = createDate;
		_lastUpdateDate = lastUpdateDate;
		_creatorUserOid = creatorOid;
		_creatorUserCode = creator;
		_lastUpdatorUserOid = updatorOid;
		_lastUpdatorUserCode = lastUpdator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets that the provided user code has made an update just now
	 * @param userCode
	 * @return
	 */
	public ModelObjectTracking setModifiedBy(final UserOID userOid,final LoginID userCode) {
		this.setLastUpdatorUserOid(userOid);
		this.setLastUpdatorUserCode(userCode);
		this.setLastUpdateDate(Instant.now());
		return this;
	}
	public void copyFrom(final ModelObjectTracking other) {
		if (other == null) return;
		_createDate = other.getCreateDate();
		_creatorUserOid = other.getCreatorUserOid();
		_creatorUserCode = other.getCreatorUserCode();
		_lastUpdateDate = other.getLastUpdateDate();
		_lastUpdatorUserOid = other.getLastUpdatorUserOid();
		_lastUpdatorUserCode = other.getLastUpdatorUserCode();
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
			out.setCreateDate(Instant.now());
			out.setLastUpdateDate(out.getCreateDate());
			out.setCreatorUserOid(other.getCreatorUserOid() != null ? other.getCreatorUserOid()
															    	: securityContext != null
															       && securityContext.isForUser() ? securityContext.asForUser()
															    											   	   .getUserOid()
															    								  : null);
			out.setCreatorUserCode(other.getCreatorUserCode() != null ? other.getCreatorUserCode()
																  	  : securityContext != null ? securityContext.getLoginId()
																  	  							: null);
			out.setLastUpdatorUserCode(null);
		}
		else if (op == PersistencePerformedOperation.UPDATED) {
			out.setLastUpdateDate(Instant.now());
			out.setLastUpdatorUserOid(securityContext.isForUser() ? securityContext.asForUser()
																			       .getUserOid()
																  : null);
			out.setLastUpdatorUserCode(securityContext.getLoginId());
		}
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
		return Objects.equal(this.getCreateDate(),other.getCreateDate())
			&& Objects.equal(this.getCreatorUserOid(),other.getCreatorUserOid())
			&& Objects.equal(this.getCreatorUserCode(),other.getCreatorUserCode())
			&& Objects.equal(this.getLastUpdateDate(),other.getLastUpdateDate())
			&& Objects.equal(this.getLastUpdatorUserCode(),other.getLastUpdatorUserCode())
			&& Objects.equal(this.getLastUpdatorUserOid(),other.getLastUpdatorUserOid());

	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_createDate,_creatorUserCode,_creatorUserOid,
								_lastUpdateDate,_lastUpdatorUserCode,_lastUpdatorUserOid);
	}
}
