package r01f.model.security;

import java.time.Instant;

import r01f.oids.SecurityIDs.AuthenticationMethod;
import r01f.oids.SecurityIDs.IdentityProvider;
import r01f.oids.SecurityIDs.LoginID;
import r01f.oids.SecurityIDs.LoginIDFormat;
import r01f.oids.SecurityIDs.SecurityPrincipalID;
import r01f.oids.SecurityOIDs.LoginOID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;
import r01f.types.Range;

/**
 * Login >> Do not get confused and keep in mind!  
 * ------------------------------------------------------------------------------------
 *   1.   The Login Object represents the data required to authenticate in a system,
 *        the UserPasswordLogin , an ApiKey pair, etc.. *
 *        for example in a ApiKeyLogin , the LoginOID represents the 'ApiKeyOID' itself
 *
 *   2.  There is also an object for Google, Xlnets, although logically the data to authenticate in these systems is outside
 *
 *   2.  The login object does not represent in any case any  and object model to get the login data once logged in
 *       For that purpose exists the LoginEntry Object or in other systems the LoginSession.
 */
public interface IsLogin
		 extends IsSecurityPersistableObjectWithID<LoginOID,LoginID> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
////////// --- Login OID & ID
	public default LoginOID getLoginOid() {
		return this.getOid();
	}
	public default void setLoginOid(final LoginOID oid) {
		this.setOid(oid);
	}
	
	public default LoginID getLoginId() {
		return this.getId();
	}
	public default void setLoginId(final LoginID login) {
		this.setId(login);
	}
////////// --- STATUS
	public LoginStatus getStatus();
	public void setStatus(final LoginStatus status);
	
////////// --- DATA
	public IsLoginData getData();
	public void setData(final IsLoginData data);
	
////////// --- SECURITY PRINCIPAL	
	public SecurityPrincipalOID getSecurityPrincipalOid();
	public void setSecurityPrincipalOid(final SecurityPrincipalOID securityPrincipalOid);
	
	public SecurityPrincipalID getSecurityPrincipalId();
	public void setSecurityPrincipalId(final SecurityPrincipalID securityPrincipalId);
	
	public IdentityProvider getSecurityPrincipalSourceIdentityProvider();
	public void setSecurityPrincipalSourceIdentityProvider(final IdentityProvider identityProvider);
	
////////// --- AUTHENTICATION: identity broker used (if any) and authentication method
	public AuthenticationMethod getAuthenticationMethod();
	public void setAuthenticationMethod(final AuthenticationMethod authenticationMethod);
	
////////// --- VALIDITY RANGE
	public Range<Instant> getValidRange();
	public void setValidRange(final Range<Instant> validRange);
	
	public default boolean isValid() {
		Instant expiresAt = this.getValidRange() != null ? this.getValidRange().getUpperBound() : null;
		if (expiresAt == null) return false;	// no expiration date
		return expiresAt.isBefore(Instant.now());
	}
////////// --- FORMAT	
	public LoginIDFormat getIdFormat();
	public void setIdFormat(final LoginIDFormat type);
	
	public default boolean isEMailBased() {
		return this.getIdFormat() == LoginIDFormat.EMAIL;
	}
	public default boolean isPhoneBased() {
		return this.getIdFormat() == LoginIDFormat.PHONE;
	}
	public default boolean isAPIKeyBased() {
		return this.getIdFormat() == LoginIDFormat.API_KEY;
	}
	public default boolean isUserCodeBased() {
		return this.getIdFormat() == LoginIDFormat.PERSON_ID;
	}
	public default boolean isWorkPlaceCodeBased() {
		return this.getIdFormat() == LoginIDFormat.WORKPLACE_CODE;
	}
}
