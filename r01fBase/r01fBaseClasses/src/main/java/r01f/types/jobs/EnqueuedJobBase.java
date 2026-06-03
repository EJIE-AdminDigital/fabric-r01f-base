package r01f.types.jobs;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Base type for enqueued jobs
 */
@Accessors(prefix="_")
abstract class EnqueuedJobBase
	implements Serializable {

	private static final long serialVersionUID = 6992968162770926050L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Job id
	 */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter protected EnqueuedJobOID _oid;
	/**
	 * The timestamp the job was enqueued
	 */
	@MarshallField(as="enqueuedTimeStamp")
	@Getter @Setter protected long _enqueuedTimeStamp;
	/**
	 * The job status
	 */
	@MarshallField(as="status")
	@Getter @Setter protected EnqueuedJobStatus _status;
	/**
	 * Job detail
	 */
	@MarshallField(as="detail",escape=true)
	@Getter @Setter protected String _detail;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public Instant getEnqueuedDate() {
		return _enqueuedTimeStamp > 0 ? Instant.ofEpochMilli(_enqueuedTimeStamp) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJobBase() {
		// nothing
	}
	public EnqueuedJobBase(final EnqueuedJobOID jobOid,
						   final EnqueuedJobStatus status,
						   final String detail) {
		this(jobOid,System.currentTimeMillis(),
			 status,
			 detail);
	}
	public EnqueuedJobBase(final EnqueuedJobOID jobOid,final Instant enqueuedDate,
						   final EnqueuedJobStatus status,
						   final String detail) {
		this(jobOid,
			 enqueuedDate != null ? enqueuedDate.toEpochMilli() : System.currentTimeMillis(),
			 status,
			 detail);
	}
	public EnqueuedJobBase(final EnqueuedJobOID jobOid,final long enqueuedTimeStamp,
						   final EnqueuedJobStatus status) {
		_oid = jobOid;
		_enqueuedTimeStamp = enqueuedTimeStamp;
		_status = status;
	}
	public EnqueuedJobBase(final EnqueuedJobOID jobOid,final long enqueuedTimeStamp,
						   final EnqueuedJobStatus status,
						   final String detail) {
		this(jobOid,enqueuedTimeStamp,
			 status);
		_detail = detail;
	}
}
