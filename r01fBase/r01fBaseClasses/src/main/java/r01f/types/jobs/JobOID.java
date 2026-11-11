package r01f.types.jobs;

import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.JavaOIDDispenser;

@Immutable
@MarshallType(as="jobOid")
public record JobOID(String id) 
   implements EnqueuedJobOID {
    public static JobOID forId(final String id) {
        return new JobOID(id);
    }
    public static JobOID valueOf(final String str) {
        return new JobOID(str);
    }
	public static JobOID fromString(final String s) {
		return JobOID.forId(s);
	}
    public static JobOID supply() {
 		return JobOID.forId(JavaOIDDispenser.generateGUID());
 	}
    @Override
	public String toString() {
		return this.id;
	}
	@Override
	public String asString() {
		return this.id;
	}
}
