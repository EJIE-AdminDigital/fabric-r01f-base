package r01f.types.jobs;


import java.time.Instant;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Models a job enqueued to be background-processed
 */
@MarshallType(as="enqueuedJob")
@Accessors(prefix="_")
public class EnqueuedJob
     extends EnqueuedJobBase {

	private static final long serialVersionUID = 5728115553001705582L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public EnqueuedJob() {
		// nothing
	}
	public EnqueuedJob(final EnqueuedJobOID jobOid,final Instant instant,
					   final EnqueuedJobStatus status) {
		this(jobOid,instant,
			 status,
		     null);	// no detail
	}
	public EnqueuedJob(final EnqueuedJobOID jobOid,final Instant instant,
					   final EnqueuedJobStatus status,
					   final String detail) {
		super(jobOid,instant,
			  status,
			  detail);
	}
	public EnqueuedJob(final EnqueuedJobOID jobOid,
					   final EnqueuedJobStatus status) {
		this(jobOid,
			 status,
			 null);		// no detail
	}
	public EnqueuedJob(final EnqueuedJobOID jobOid,
					   final EnqueuedJobStatus status,
					   final String detail) {
		super(jobOid,
			  status,
			  detail);
	}
}
