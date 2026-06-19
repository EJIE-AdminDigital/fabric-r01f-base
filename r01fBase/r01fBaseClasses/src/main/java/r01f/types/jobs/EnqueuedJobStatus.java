package r01f.types.jobs;

import r01f.enums.EnumExtended;

/**
 * The status of an enqueued job
 */
public enum EnqueuedJobStatus 
 implements EnumExtended<EnqueuedJobStatus> {
	NOT_STARTED,				
	STARTED,
	PAUSED,
	FINALIZED_ERROR,
	FINALIZED_OK;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static EnqueuedJobStatus fromName(final String name) {
		return EnumExtended.fromName(name,EnqueuedJobStatus.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + EnqueuedJobStatus.class + " element with name=" + name));
	}
}
