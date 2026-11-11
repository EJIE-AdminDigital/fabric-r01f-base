package r01f.model.security.context;

/**
 * Interface for objects containing a {@link SecurityContext}
 */
public interface HasSecurityContext {
	public <U extends SecurityContext> U getSecurityContext();
}
