package r01f.oids;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.facts.FactDimension;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.FactoryFrom;

public abstract class SecurityOIDs {

/////////////////////////////////////////////////////////////////////////////////////////
// Base marker interface
/////////////////////////////////////////////////////////////////////////////////////////

	public static interface IsSecurityObjectOID
					extends OIDTyped<String>,
							FactDimension {
		// just a marker interface
	}
	public static interface IsSecurityPersistableObjectOID
					extends PersistableObjectOID,
							IsSecurityObjectOID {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	Security Base OID's
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The [security principal] is an entity that usually resides in an external [identity provider] (ie google, facebook, etc) 
	 * or [identity broker] (ie a corporate OKTA or KeyCloak) 
	 * where it has:
	 * 		- a unique identifier (the [security principal] OID)
	 * 		- a business identifier (the [security principal] ID) like the email address
	 * So the [security principal] is determined by:
	 * 		- the [security principal] OID
	 * 		- the [identity provider] ID (ie google, facebook, etc) or the [identity broker] ID (ie corporate OKTA or KeyCloak)
	 * 
	 * The [security principal] ID is:
	 *  - linked to a business concept like an EMail, a PersonID, a Phone, etc (it's not an OID)
	 *   		- Google uses the EMail
	 *   		- Giltza uses the PersonID
	 *   		- etc
	 * 	- unique 
	 * The [security principal] OID is a randomly generated OID by the [identity provider] or [identity broker]
	 * 
	 * Usually in the [authorization] process, in the first moment, just the [security principal] ID is known 
	 * and that ID is used to retrieve the full [security principal] data from the [identity provider] or [identity broker]
	 * that contains the [security principal] OID and other data 
	 */
	@Immutable
	@MarshallType(as="securityPrincipalOid")
	public record SecurityPrincipalOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {

		public static SecurityPrincipalOID from(final String id) {
			return new SecurityPrincipalOID(id);
		}
		public static SecurityPrincipalOID forId(final String id) {
			return new SecurityPrincipalOID(id);
		}
		// Commented to avoid jackson conflicting from-String creators error
		// public static SecurityPrincipalOID valueOf(final String s) {
		//		return SecurityPrincipalOID.forId(s);
		// }
		public static SecurityPrincipalOID fromString(final String s) {
			return SecurityPrincipalOID.forId(s);
		}
		public static SecurityPrincipalOID supply() {
			return SecurityPrincipalOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<CharSequence,SecurityPrincipalOID> FACTORY_FROM_STRING = oid -> SecurityPrincipalOID.forId(oid.toString());
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
	@Immutable
	@MarshallType(as="principalImpersonationOid")
	public record SecurityPrincipalImpersonationOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {

		public static SecurityPrincipalImpersonationOID from(final String id) {
			return new SecurityPrincipalImpersonationOID(id);
		}
		public static SecurityPrincipalImpersonationOID valueOf(final String s) {
			return SecurityPrincipalImpersonationOID.forId(s);
		}
		public static SecurityPrincipalImpersonationOID fromString(final String s) {
			return SecurityPrincipalImpersonationOID.forId(s);
		}
		public static SecurityPrincipalImpersonationOID forId(final String id) {
			return new SecurityPrincipalImpersonationOID(id);
		}
		public static SecurityPrincipalImpersonationOID supply() {
			return SecurityPrincipalImpersonationOID.forId(JavaOIDDispenser.supplyOid());
		}		
		public static FactoryFrom<String,SecurityPrincipalImpersonationOID> FACTORY_FROM_STRING = SecurityPrincipalImpersonationOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOGIN
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="loginOid")
	public record LoginOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {

		public static final LoginOID ALL = LoginOID.forId("*");
		
		public static LoginOID from(final String id) {
			return new LoginOID(id);
		}
		public static LoginOID forId(final String id) {
			return new LoginOID(id);
		}
		public static LoginOID valueOf(final String s) {
			return LoginOID.forId(s);
		}
		public static LoginOID fromString(final String s) {
			return LoginOID.forId(s);
		}
		public static LoginOID supply() {
			return LoginOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,LoginOID> FACTORY_FROM_STRING = LoginOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
	@Immutable
	@MarshallType(as="loginEntryOid")
	public record LoginEntryOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {

		public static LoginEntryOID from(final String id) {
			return new LoginEntryOID(id);
		}
		public static LoginEntryOID valueOf(final String s) {
			return LoginEntryOID.forId(s);
		}
		public static LoginEntryOID fromString(final String s) {
			return LoginEntryOID.forId(s);
		}
		public static LoginEntryOID forId(final String id) {
			return new LoginEntryOID(id);
		}
		public static LoginEntryOID supply() {
			return LoginEntryOID.forId(JavaOIDDispenser.supplyOid());
		}		
		public static FactoryFrom<String,LoginEntryOID> FACTORY_FROM_STRING = LoginEntryOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PAIRED PHONE LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="pairedPhoneLoginConsentOid")
	public record PairedPhoneLoginConsentOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {
	
		public static PairedPhoneLoginConsentOID from(final String id) {
			return new PairedPhoneLoginConsentOID(id);
		}
		public static PairedPhoneLoginConsentOID forId(final String id) {
			return new PairedPhoneLoginConsentOID(id);
		}
		public static PairedPhoneLoginConsentOID valueOf(final String s) {
			return PairedPhoneLoginConsentOID.forId(s);
		}
		public static PairedPhoneLoginConsentOID fromString(final String s) {
			return PairedPhoneLoginConsentOID.forId(s);
		}
		public static PairedPhoneLoginConsentOID supply() {
			return PairedPhoneLoginConsentOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,PairedPhoneLoginConsentOID> FACTORY_FROM_STRING = PairedPhoneLoginConsentOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	API-KEY LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="apiKeyVersionOid")
	public record ApiKeyVersionOID(@Getter String id)
       implements IsSecurityPersistableObjectOID {
		
		public static ApiKeyVersionOID from(final String id) {
			return new ApiKeyVersionOID(id);
		}
		public static ApiKeyVersionOID forId(final String id) {
			return new ApiKeyVersionOID(id);
		}
		public static ApiKeyVersionOID valueOf(final String s) {
			return ApiKeyVersionOID.forId(s);
		}
		public static ApiKeyVersionOID fromString(final String s) {
			return ApiKeyVersionOID.forId(s);
		}
		public static ApiKeyVersionOID supply() {
			return ApiKeyVersionOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,ApiKeyVersionOID> FACTORY_FROM_STRING = ApiKeyVersionOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	USER AUTHORIZATION ON RESOURCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authorizationOnResourceOid")
	public record AuthorizationOnResourceOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {
		
		public static AuthorizationOnResourceOID from(final String id) {
			return new AuthorizationOnResourceOID(id);
		}
		public static AuthorizationOnResourceOID forId(final String id) {
			return new AuthorizationOnResourceOID(id);
		}
		public static AuthorizationOnResourceOID valueOf(final String s) {
			return AuthorizationOnResourceOID.forId(s);
		}
		public static AuthorizationOnResourceOID fromString(final String s) {
			return AuthorizationOnResourceOID.forId(s);
		}
		public static AuthorizationOnResourceOID supply() {
			return AuthorizationOnResourceOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,AuthorizationOnResourceOID> FACTORY_FROM_STRING = AuthorizationOnResourceOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTHORIZATION TARGET RESOURCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authorizationTargetResourceOid")
	public record AuthorizationTargetResourceOID(@Getter String id)
	   implements IsSecurityPersistableObjectOID {
		
		public static AuthorizationTargetResourceOID from(final String id) {
			return new AuthorizationTargetResourceOID(id);
		}
		public static AuthorizationTargetResourceOID forId(final String id) {
			return new AuthorizationTargetResourceOID(id);
		}
		public static AuthorizationTargetResourceOID valueOf(final String s) {
			return AuthorizationTargetResourceOID.forId(s);
		}
		public static AuthorizationTargetResourceOID fromString(final String s) {
			return AuthorizationTargetResourceOID.forId(s);
		}
		public static AuthorizationTargetResourceOID supply() {
			return AuthorizationTargetResourceOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,AuthorizationTargetResourceOID> FACTORY_FROM_STRING = AuthorizationTargetResourceOID::forId;
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
