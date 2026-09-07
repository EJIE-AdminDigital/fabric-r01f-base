package r01f.securitycontext;

import lombok.Getter;
import r01f.annotations.Immutable;
import r01f.facts.FactDimension;
import r01f.guids.JavaOIDDispenser;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.FactoryFrom;

public abstract class SecurityOIDs {

/////////////////////////////////////////////////////////////////////////////////////////
// Base marker interface
/////////////////////////////////////////////////////////////////////////////////////////

	public static interface IsSecurityObjectOID
					extends OIDTyped<String>,
							PersistableObjectOID,
							FactDimension {
		// just a marker interface
	}
	public interface IsIdentityBrokerObjectOID
	   	     extends IsSecurityObjectOID {
		// just a marker interface
		@SuppressWarnings("unchecked")
		public default <IDB extends IsIdentityBrokerObjectOID>  IDB as(final Class<IDB> id) {
			return (IDB)this;
		}
	}	
/////////////////////////////////////////////////////////////////////////////////////////
// 	Security Base OID's
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userOid")
	public record UserOID(@Getter String id)
	   implements IsSecurityObjectOID {

		public static final UserOID ALL = UserOID.forId("*");
		
//		public static UserOID from(final LoginID loginId) {
//			return new UserOID(loginId.asString());
//		}
		public static UserOID from(final String id) {
			return new UserOID(id);
		}
		public static UserOID forId(final String id) {
			return new UserOID(id);
		}
		public static UserOID valueOf(final String s) {
			return UserOID.forId(s);
		}
		public static UserOID fromString(final String s) {
			return UserOID.forId(s);
		}
		public static UserOID supply() {
			return UserOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<CharSequence,UserOID> FACTORY_FROM_STRING = oid -> UserOID.forId(oid.toString());
		
		public static final UserOID ANONYMOUS = UserOID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final UserOID MASTER = UserOID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final UserOID ADMIN = MASTER;
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
		public static final UserOID SYSTEM = UserOID.forId("sys");
		public boolean isSystem() {
			return this.is(SYSTEM);
		}
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
	@MarshallType(as="loginOid")
	public record LoginOID(@Getter String id)
	   implements IsSecurityObjectOID {

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
/////////////////////////////////////////////////////////////////////////////////////////
//	USER PASSWORD LOGIN
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userLoginEntryOid")
	public record UserLoginEntryOID(@Getter String id)
	   implements IsSecurityObjectOID {

		public static UserLoginEntryOID from(final String id) {
			return new UserLoginEntryOID(id);
		}
		public static UserLoginEntryOID valueOf(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID fromString(final String s) {
			return UserLoginEntryOID.forId(s);
		}
		public static UserLoginEntryOID forId(final String id) {
			return new UserLoginEntryOID(id);
		}
		public static UserLoginEntryOID supply() {
			return UserLoginEntryOID.forId(JavaOIDDispenser.supplyOid());
		}		
		public static FactoryFrom<String,UserLoginEntryOID> FACTORY_FROM_STRING = UserLoginEntryOID::forId;
		
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
	   implements IsSecurityObjectOID {
	
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
       implements IsSecurityObjectOID {
		
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
	public record UserAuthorizationOnResourceOID(@Getter String id)
	   implements IsSecurityObjectOID {
		
		public static UserAuthorizationOnResourceOID from(final String id) {
			return new UserAuthorizationOnResourceOID(id);
		}
		public static UserAuthorizationOnResourceOID forId(final String id) {
			return new UserAuthorizationOnResourceOID(id);
		}
		public static UserAuthorizationOnResourceOID valueOf(final String s) {
			return UserAuthorizationOnResourceOID.forId(s);
		}
		public static UserAuthorizationOnResourceOID fromString(final String s) {
			return UserAuthorizationOnResourceOID.forId(s);
		}
		public static UserAuthorizationOnResourceOID supply() {
			return UserAuthorizationOnResourceOID.forId(JavaOIDDispenser.supplyOid());
		}
		public static FactoryFrom<String,UserAuthorizationOnResourceOID> FACTORY_FROM_STRING = UserAuthorizationOnResourceOID::forId;
		
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
	   implements IsSecurityObjectOID {
		
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
