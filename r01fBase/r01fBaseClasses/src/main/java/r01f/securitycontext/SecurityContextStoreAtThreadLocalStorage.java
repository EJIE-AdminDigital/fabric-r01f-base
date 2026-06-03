package r01f.securitycontext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages the {@link SecurityContext} attached to the [thread local] storage
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SecurityContextStoreAtThreadLocalStorage {
/////////////////////////////////////////////////////////////////////////////////////////
//	STATIC FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static ThreadLocal<SecurityContext> SECURITY_CONTEXT = new ThreadLocal<SecurityContext>();

/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////	
	// Remove
	public static void remove() {
		if (log.isTraceEnabled()) {
			log.trace("... remove:{}", SECURITY_CONTEXT.get());
		}
		SECURITY_CONTEXT.remove();
	}
	// set
	public static <S extends SecurityContext> void set(final S ctx) {
		if (log.isTraceEnabled()) {
			log.trace("... set:{}", ctx);
		}
		SECURITY_CONTEXT.set(ctx);
	}
	@SuppressWarnings("unchecked")
	public static <S extends SecurityContext> S get() {
		return (S)SECURITY_CONTEXT.get();
	}
}
