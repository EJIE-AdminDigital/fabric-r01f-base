package r01f.oids;

/**
 * The oid of something that can be scheduled
 */
public interface CanBeScheduledOID
		 extends OID {
	public Class<?> getScheduleableObjectType();
}
