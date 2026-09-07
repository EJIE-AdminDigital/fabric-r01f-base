package r01f.securitycontext;

import java.io.Serializable;
import java.util.HexFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.IsRole;
import r01f.guids.CommonOIDs.IsToken;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.StringHex;
import r01f.types.contact.EMail;
import r01f.types.contact.IsPersonID;
import r01f.types.contact.PersonID;
import r01f.types.contact.Phone;
import r01f.util.types.Passwords;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SecurityIDS {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsSecurityObjectID
   		     extends OIDTyped<String> {
		// just a marker interface
	}
	
	public interface IsIdentityBrokerObjectID
	   	     extends IsSecurityObjectID {
		// just a marker interface
		@SuppressWarnings("unchecked")
		public default <IDB extends IsIdentityBrokerObjectID>  IDB as(final Class<IDB> id) {
			return (IDB)this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="securityId")
	public record SecurityID(@Getter String id)
	   implements IsSecurityObjectID {
		public static SecurityID forId(final String id) {
			return new SecurityID(id);
		}
		public static SecurityID valueOf(final String id) {
			return new SecurityID(id);
		}
		public static SecurityID fromString(final String s) {
			return SecurityID.forId(s);
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
	@MarshallType(as="securityToken")
	public record SecurityToken(@Getter String id)
	   implements IsToken {
		private static final long serialVersionUID = -6056892755877680637L;

		public static SecurityToken from(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken forId(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken valueOf(final String id) {
			return new SecurityToken(id);
		}
		public static SecurityToken fromString(final String s) {
			return SecurityToken.forId(s);
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
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	public record SecurityProviderID(@Getter String id)
	   implements IsSecurityObjectID {

		public static SecurityProviderID from(final String id) {
			return new SecurityProviderID(id);
		}
		public static SecurityProviderID forId(final String idAsString) {
			return new SecurityProviderID(idAsString);
		}
		public static SecurityProviderID valueOf(final String id) {
			return new SecurityProviderID(id);
		}
		public static SecurityProviderID fromString(final String s) {
			return SecurityProviderID.forId(s);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}

		public static final SecurityProviderID USER_PASSWORD = SecurityProviderID.forId("usrpwd");
		public static final SecurityProviderID GOOGLE = SecurityProviderID.forId("google");
		public static final SecurityProviderID GILTZA = SecurityProviderID.forId("giltza");
		public static final SecurityProviderID XLNETS = SecurityProviderID.forId("xlnets");
		public static final SecurityProviderID OSAKIDETZA_AZURE_AD = SecurityProviderID.forId("osaazuread");
		public static final SecurityProviderID SAML = SecurityProviderID.forId("saml");
		public static final SecurityProviderID PAIRED_PHONE = SecurityProviderID.forId("pairedphone");
		public static final SecurityProviderID REGISTERED_DEVICE = SecurityProviderID.forId("registered-device");
		public static final SecurityProviderID API_KEY = SecurityProviderID.forId("api-key");
		public static final SecurityProviderID IDENTITY_BROKER = SecurityProviderID.forId("id-broker");
		
		public static final SecurityProviderID SYSTEM = SecurityProviderID.forId("system");		// ie: app login
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userGroupCode")
	public record UserGroupCode(@Getter String id)
	   implements IsSecurityObjectID {

		public static UserGroupCode from(final String id) {
			return new UserGroupCode(id);
		}
		public static UserGroupCode forId(final String id) {
			return new UserGroupCode(id);
		}
		public static UserGroupCode valueOf(final String id) {
			return UserGroupCode.forId(id);
		}
		public static UserGroupCode fromString(final String s) {
			return UserGroupCode.forId(s);
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
	@MarshallType(as="userRole")
	public record UserRole(@Getter String id)
	   implements IsSecurityObjectID,
				  IsRole {
		public static UserRole from(final String id) {
			return new UserRole(id);
		}
		public static UserRole forId(final String id) {
			return new UserRole(id);
		}
		public static UserRole valueOf(final String id) {
			return new UserRole(id);
		}
		public static UserRole fromString(final String s) {
			return UserRole.forId(s);
		}
		public static UserRole named(final String id) {
			return new UserRole(id);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		public static final UserRole MASTER = UserRole.named("MASTER");
		public static final UserRole HORIZONTAL_ADMIN = UserRole.named("HORIZONTAL_ADMIN");
	}
	@Immutable
	@MarshallType(as="password")
	public record Password(@Getter String id)
	   implements IsSecurityObjectID {

		public static Password from(final String id) {
			return new Password(id);
		}
		public static Password forId(final String id) {
			return new Password(id);
		}
		public static Password valueOf(final String id) {
			return Password.forId(id);
		}
		public static Password fromString(final String s) {
			return Password.forId(s);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		public PasswordHash hash() {
			return PasswordHash.fromPassword(Password.this);
		}
		public char[] toCharArray() {
			return this.asString().toCharArray();
		}
		public boolean matchesHash(final PasswordHash hash) {
			return Passwords.createWithDefaultCost()
							.authenticate(this,			// the password
										  hash);		// the hash
		}
		public static Password xor(final StringHex halfOne, final StringHex theOtherHalf) {
			if (halfOne.asString().length() !=
					theOtherHalf.asString().length()) {
				throw new IllegalArgumentException("Different length of halfs");
			}
			byte[] keyBytes1 = HexFormat.of().parseHex(halfOne.asString());
			byte[] keyBytes2 = HexFormat.of().parseHex(theOtherHalf.asString());

			byte[] keyBytesXOR = new byte[keyBytes1.length];
 
			for (int i=0 ;i < keyBytesXOR.length; i++) {
				int intValue =   Byte.valueOf(keyBytes1[i]).intValue() ^  Byte.valueOf(keyBytes2[i]).intValue();
				keyBytesXOR[i]=  Integer.valueOf(intValue).byteValue();
			}
			String encoded = HexFormat.of().formatHex(keyBytesXOR);
			return Password.valueOf(encoded);
		}

		public StringHex toHex() {
			return StringHex.fromHexEncodedString(this.getRaw());
		}

		public static final Password CHANGE_ME = Password.forId("/change__me/");
	}
	@Immutable
	@MarshallType(as="passwordHash")
	public record PasswordHash(@Getter String id)
	   implements OIDTyped<String> {
		public static PasswordHash from(final String id) {
			return new PasswordHash(id);
		}
		public static PasswordHash forId(final String id) {
			return new PasswordHash(id);
		}
		public static PasswordHash valueOf(final String id) {
			return PasswordHash.forId(id);
		}
		public static PasswordHash fromString(final String s) {
			return PasswordHash.forId(s);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		public static PasswordHash fromHash(final String hash) {
			return PasswordHash.forId(hash);
		}
		public static PasswordHash fromPassword(final String password) {
			return PasswordHash.fromPassword(new Password(password));
		}
		public static PasswordHash fromPassword(final Password password) {
			return Passwords.createWithDefaultCost()
							.hash(password);
		}
		public boolean matches(final Password password) {
			return Passwords.createWithDefaultCost()
							.authenticate(password,		// the received password
										  this);		// the stored hash
		}
		public char[] toCharArray() {
			return this.asString().toCharArray();
		}
		public byte[] getBytes() {
			return this.asString().getBytes();
		}
	}
	@Immutable
	@MarshallType(as="userAndPassword")
	public record LoginAndPassword(@MarshallField(as="loginId",
					   							  whenXml=@MarshallFieldAsXml(attr=true))
								   @Getter LoginID loginId,
								   @MarshallField(as="password",
								   				  whenXml=@MarshallFieldAsXml(attr=true))
								   @Getter Password password)
	   implements Serializable {
		public static LoginAndPassword from(final LoginID loginId,final Password password) {
			return new LoginAndPassword(loginId,
									   password);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ID's for data, login and authorizations of an user
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="loginId")
	public record LoginID(@Getter String id)
	   implements IsSecurityObjectID {

		public static LoginID from(final String id) {
			return new LoginID(id);
		}
		public static LoginID forId(final String id) {
			return new LoginID(id);
		}
		public static LoginID valueOf(final String id) {
			return new LoginID(id);
		}
		public static LoginID fromString(final String id) {
			return new LoginID(id);
		}
		public static LoginID fromAppCode(final AppCode appCode) {
			return LoginID.forId(appCode.asString());
		}
		public static LoginID fromPhone(final Phone phone) {
			return LoginID.forId(phone.asString());
		}
		public static LoginID fromPhone(final String phone) {
			if (!Phone.validate(phone)) throw new IllegalArgumentException(phone + " is NOT a valid phone!");
			return LoginID.fromPhone(Phone.of(phone));
		}
		public static LoginID fromEMail(final EMail mail) {
			return LoginID.forId(mail.asString());
		}
		public static LoginID fromPersonID(final PersonID personId) {
			if (!IsPersonID.isValidSpanishNIF(personId)) throw new IllegalArgumentException(personId + " is NOT a valid NIF!");
			return LoginID.forId(personId.asString());
		}

		public static LoginID fromEMail(final String mail) {
			if (!EMail.validate(mail)) throw new IllegalArgumentException(mail + " is NOT a valid email!");
			return LoginID.fromEMail(EMail.of(mail));
		}

		public static final LoginID ANONYMOUS = LoginID.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(ANONYMOUS);
		}
		public static final LoginID MASTER = LoginID.forId("master");
		public boolean isMaster() {
			return this.is(MASTER);
		}
		public static final LoginID ADMIN = LoginID.forId("admin");
		public boolean isAdmin() {
			return this.is(ADMIN);
		}
		public static final LoginID SYSTEM = LoginID.forId("system");
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

		public boolean isEMail() {
			EMail email = EMail.of(this.asString());
			return email != null && email.isValid();
		}
		public boolean isPhone() {
			Phone phone = Phone.of(this.asString());
			return phone != null && phone.isValid();
		}

		public EMail asEMail() {
			return EMail.of(this.getId());
		}
		public Phone asPhone() {
			return Phone.of(this.getId());
		}
		public AppCode asAppCode() {
			return AppCode.forId(this.getId());
		}
		
		public IsIdentityBrokerObjectID asIdentityProviderSecurityId() {
			return new IsIdentityBrokerObjectID() {				
						private static final long serialVersionUID = -8408648918753083953L;
						@Override
						public String getId() {					
							return LoginID.this.id();
						};
						@Override
						public String toString() {					
							return LoginID.this.id();
						}
						@Override
						public String asString() {
							return LoginID.this.id();
						}};
						
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="userLoginEntryID")
	public record UserLoginEntryID(@Getter String id)
  	   implements IsSecurityObjectID {
		
		public static UserLoginEntryID from(final String id) {
			return new UserLoginEntryID(id);
		}
		public static UserLoginEntryID forId(final String id) {
			return new UserLoginEntryID(id);
		}
		public static UserLoginEntryID valueOf(final String id) {
			return new UserLoginEntryID(id);
		}
		public static UserLoginEntryID fromString(final String id) {
			return new UserLoginEntryID(id);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}

		public static final UserLoginEntryID NOT_CONFIRMED = new UserLoginEntryID("NOT_CONFIRMED");

		public static final UserLoginEntryID LOGIN_OK = new UserLoginEntryID("LOGIN_OK");
		public static final UserLoginEntryID LOGIN_ERROR = new UserLoginEntryID("LOGIN_ERROR");

		public static final UserLoginEntryID PASSWORD_RECORVERY_REQUEST = new UserLoginEntryID("PASSWORD_RECOVERY_REQUEST");
		public static final UserLoginEntryID PASSWORD_CHANGED = new UserLoginEntryID("PASSWORD_CHANGED");

		public static final UserLoginEntryID IMPERSONATION_PENDING = new UserLoginEntryID("IMPERSONATION_PENDING");
		public static final UserLoginEntryID IMPERSONATION_NOT_VALID = new UserLoginEntryID("IMPERSONATION_NOT_VALID");

		public static final UserLoginEntryID PENDING_ON_AUTH_FACTOR_VALIDATION = new UserLoginEntryID("PENDING_ON_AUTH_FACTOR_VALIDATION");

		public static final UserLoginEntryID forUserNotConfirmed() {
			return NOT_CONFIRMED;
		}
		public static final UserLoginEntryID forLoginOk() {
			return LOGIN_OK;
		}
		public static final UserLoginEntryID forLoginError() {
			return LOGIN_ERROR;
		}
		public static final UserLoginEntryID forPasswordRecoveryRequest() {
			return PASSWORD_RECORVERY_REQUEST;
		}
		public static final UserLoginEntryID forPasswordChanged() {
			return PASSWORD_CHANGED;
		}
		public static final UserLoginEntryID forImpersonationPending() {
			return IMPERSONATION_PENDING;
		}
		public static final UserLoginEntryID forImpersonationNotValid() {
			return IMPERSONATION_NOT_VALID;
		}
		public static UserLoginEntryID forLoginPendingOnAuthFactor() {
			return PENDING_ON_AUTH_FACTOR_VALIDATION;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="authorizationCode")
	public record AuthorizationCode(@Getter String id)
	   implements IsSecurityObjectID {
		public static AuthorizationCode from(final String id) {
			return new AuthorizationCode(id);
		}
		public static AuthorizationCode forId(final String id) {
			return new AuthorizationCode(id);
		}
		public static AuthorizationCode valueOf(final String id) {
			return new AuthorizationCode(id);
		}
		public static AuthorizationCode fromString(final String id) {
			return new AuthorizationCode(id);
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
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="securityGroup")
	public record SecurityGroup(@Getter String id)
	   implements IsSecurityObjectID {
		public static SecurityGroup from(final String id) {
			return new SecurityGroup(id);
		}
		public static SecurityGroup forId(final String id) {
			return new SecurityGroup(id);
		}
		public static SecurityGroup valueOf(final String id) {
			return new SecurityGroup(id);
		}
		public static SecurityGroup fromString(final String id) {
			return new SecurityGroup(id);
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
}
