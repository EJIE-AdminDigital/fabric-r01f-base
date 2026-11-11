package r01f.oids;

import java.io.Serializable;
import java.util.HexFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.enums.EnumExtended;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.CommonOIDs.AppCode;
import r01f.oids.CommonOIDs.IsRole;
import r01f.oids.CommonOIDs.IsToken;
import r01f.oids.SecurityOIDs.IsSecurityPersistableObjectOID;
import r01f.oids.SecurityOIDs.LoginOID;
import r01f.oids.SecurityOIDs.SecurityPrincipalOID;
import r01f.types.StringBase64;
import r01f.types.StringHex;
import r01f.types.contact.EMail;
import r01f.types.contact.IsPersonID;
import r01f.types.contact.PersonID;
import r01f.types.contact.Phone;
import r01f.util.types.Passwords;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SecurityIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsSecurityObjectID
   		     extends OIDTyped<String> {
		// just a marker interface
	}
	public interface IsSecurityPersistableObjectID<O extends IsSecurityPersistableObjectOID>
			 extends PersistableObjectID<O>,
			 		 IsSecurityObjectID {
		// just a marker interface
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
		public static SecurityToken fromBase64(final StringBase64 base64) {
			return SecurityToken.forId(base64.asString());
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
//	IDENTITY PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public enum IdentityProvider
	 implements EnumExtended<IdentityProvider> {
		GOOGLE,
		GILTZA,
		XLNETS,
		ELKARLAN_AZURE_AD,ELKARLAN2_AZURE_AD,
		OSAKIDETZA_AZURE_AD,OSAKIDETZA_PD_AZURE_AD,
		FABRIC,
		TEST;
		
		public static IdentityProvider fromString(final String id) {
			return EnumExtended.fromName(id,IdentityProvider.class)
							   .orElseThrow(() -> new IllegalArgumentException(id + " is NOT a valid " + IdentityProvider.class.getSimpleName()));
		}
		public static IdentityProvider forId(final String id) {
			return IdentityProvider.fromString(id);
		}
		public static IdentityProvider fromName(final String id) {
			return IdentityProvider.fromString(id);
		}
	}
	@Accessors(prefix="_")
	public enum IdentityBroker
	 implements EnumExtended<IdentityBroker> {
		OKTA,
		KEYCLOAK,
		FABRIC;
		
		public static IdentityBroker fromString(final String id) { 
			return EnumExtended.fromName(id,IdentityBroker.class)
							   .orElseThrow(() -> new IllegalArgumentException(id + " is NOT a valid " + IdentityBroker.class.getSimpleName()));
		}
		public static IdentityBroker forId(final String id) {
			return IdentityBroker.fromString(id);
		}
		public static IdentityBroker fromName(final String id) {
			return IdentityBroker.fromString(id);
		}
	}
	/**
	 * Authentication method in a borad sense
	 *	Authentication
	 *	│
	 *	├── How is the principal proved?
	 *	│   ├── Password
	 *	│   ├── Passkey
	 *	│   ├── Certificate
	 *	│   ├── Biometrics
	 *	│   └── OTP
	 *	│
	 *	└── How is the authentication result communicated?
	 *	    ├── SAML
	 *	    └── OIDC
	 */
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public enum AuthenticationMethod
	 implements EnumExtended<AuthenticationMethod> {
		USER_PASSWORD,
		CERTIFICATE,
		OAUTH_CLIENT_CREDENTIALS,	// client credentials (client_id + client_secret)
		OAUTH_AUTH_CODE_FLOW,		// authorization code with PKCE (Proof Key for Code Exchange)
		API_KEY,
		SAML,
		PAIRED_PHONE,
		REGISTERED_DEVICE,
		UNKNOWN;
		
		public static AuthenticationMethod fromString(final String id) {
			return EnumExtended.fromName(id,AuthenticationMethod.class)
							   .orElseThrow(() -> new IllegalArgumentException(id + " is NOT a valid " + AuthenticationMethod.class.getSimpleName()));
		}
		public static AuthenticationMethod forId(final String id) {
			return AuthenticationMethod.fromString(id);
		}
		public static AuthenticationMethod fromName(final String id) {
			return AuthenticationMethod.fromString(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY PRINCIPAL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * An ID linked to a [security principal] that usually:
	 *  - It's linked to a business concept like an EMail, a PersonID, a Phone, etc (it's not an OID)
	 *   		- Google uses the EMail
	 *   		- Giltza uses the PersonID
	 *   		- etc
	 * 	- Is unique 
	 * The [security principal] OID is a randomly generated OID by the [identity provider] or [identity broker]
	 * 
	 * Usually in the [authorization] process, in the first moment, just the [security principal] ID is known 
	 * and that ID is used to retrieve the full [security principal] data from the [identity provider] or [identity broker]
	 * that contains the [security principal] OID and other data 
	 */
	public record SecurityPrincipalID(@Getter String id)
	   implements IsSecurityPersistableObjectID<SecurityPrincipalOID> {
		public static SecurityPrincipalID from(final String id) {
			return new SecurityPrincipalID(id);
		}
		public static SecurityPrincipalID forId(final String id) {
			return new SecurityPrincipalID(id);
		}
		public static SecurityPrincipalID valueOf(final String id) {
			return new SecurityPrincipalID(id);
		}
		public static SecurityPrincipalID fromString(final String s) {
			return SecurityPrincipalID.forId(s);
		}
		
		public static <I extends IsPersonID> SecurityPrincipalID fromPersonId(final I personId) {
			return SecurityPrincipalID.forId(personId.asString());
		}
		public static SecurityPrincipalID fromLoginId(final LoginID loginId) {
			return SecurityPrincipalID.forId(loginId.asString());
		}
		
		public LoginID asLoginId() {
			return LoginID.forId(this.id);
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
	@MarshallType(as="securityRole")
	public record SecurityRole(@Getter String id)
	   implements IsSecurityObjectID,
				  IsRole {
		public static SecurityRole from(final String id) {
			return new SecurityRole(id);
		}
		public static SecurityRole forId(final String id) {
			return new SecurityRole(id);
		}
		public static SecurityRole valueOf(final String id) {
			return new SecurityRole(id);
		}
		public static SecurityRole fromString(final String s) {
			return SecurityRole.forId(s);
		}
		public static SecurityRole named(final String id) {
			return new SecurityRole(id);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		public static final SecurityRole MASTER = SecurityRole.named("MASTER");
		public static final SecurityRole HORIZONTAL_ADMIN = SecurityRole.named("HORIZONTAL_ADMIN");
	}
	@Immutable
	@MarshallType(as="secret")
	public record Secret(@Getter String id)
	   implements IsSecurityObjectID {

		public static Secret from(final String id) {
			return new Secret(id);
		}
		public static Secret forId(final String id) {
			return new Secret(id);
		}
		public static Secret valueOf(final String id) {
			return Secret.forId(id);
		}
		public static Secret fromString(final String s) {
			return Secret.forId(s);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		public char[] toCharArray() {
			return this.asString().toCharArray();
		}
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
	   implements IsSecurityPersistableObjectID<LoginOID> {

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
	}
	public enum LoginIDFormat 
	  implements EnumExtended<LoginIDFormat> {
		EMAIL,
		PHONE,
		API_KEY,
		PERSON_ID,
		WORKPLACE_CODE,
		UNKNOWN;
		
		public static LoginIDFormat fromLogin(final LoginID login) {
			if (login.isEMail()) return EMAIL;
			if (login.isPhone()) return PHONE;
			if (IsPersonID.isValidSpanishNIF(login.getId())) return PERSON_ID;
			if (IsPersonID.canBePassport(login.getId())) return PERSON_ID;
			return UNKNOWN;
		}
	}
	@Immutable
	@MarshallType(as="loginEntryEvent")
	public enum LoginEntryEvent
  	 implements EnumExtended<LoginEntryEvent>  {
		NOT_CONFIRMED,
		
		LOGIN_OK,
		LOGIN_ERROR,
		
		PASSWORD_RECORVERY_REQUEST,
		PASSWORD_CHANGED,
		PASSWORD_RESET,

		AWAITING_AUTHN_SECOND_FACTOR_VALIDATION;
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
/////////////////////////////////////////////////////////////////////////////////////////
//	FILESYSTEM
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
}
