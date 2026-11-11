package r01f.oids;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.annotations.Immutable;
import r01f.internal.Env;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.SecurityIDs.LoginID;
import r01f.util.types.Strings;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CommonOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * OID of a void oid
	 */
	@Immutable
	@MarshallType(as="voidOid")
	public record VoidOID(@Getter String id)
	   implements OIDTyped<String> {

		public static VoidOID forId(final String id) {
			return new VoidOID(id);
		}
		public static VoidOID valueOf(final String str) {
			return new VoidOID(str);
		}
		public static VoidOID fromString(final String s) {
			return VoidOID.forId(s);
		}
		public static VoidOID supply() {
			return VoidOID.forId(JavaOIDDispenser.generateGUID());
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
//	APPS
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="usageId")
	public record UsageID(@Getter String id)
	   implements OIDTyped<String> {
		
		public static final UsageID DEFAULT = UsageID.forId("default");
		
		public static UsageID from(final String id) {
			return new UsageID(id);
		}
		public static UsageID forId(final String id) {
			return new UsageID(id);
		}
		public static UsageID valueOf(final String id) {
			return new UsageID(id);
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
	@MarshallType(as="systemId")
	public record SystemID(@Getter String id)
	   implements OIDTyped<String> {
		
		public static final SystemID DEFAULT = SystemID.forId("default");
		
		public static SystemID from(final String id) {
			return new SystemID(id);
		}
		public static SystemID forId(final String id) {
			return new SystemID(id);
		}
		public static SystemID valueOf(final String id) {
			return new SystemID(id);
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
	public interface IsAppIdentifier
  		     extends OIDTyped<String> {
		// just extend
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsAppCode
  		     extends IsAppIdentifier {
		public default AppCode asAppCode() {
			return AppCode.forId(this.asString());
		}
	} 
	/**
	 * AppCode
	 */
	@MarshallType(as="appCode")
	public record AppCode(@Getter String id)
	   implements IsAppCode {
		
		public static AppCode from(final String id) {
			return new AppCode(id);
		}
		public static AppCode forId(final String id) {
			return new AppCode(id);
		}
		public static AppCode valueOf(final String id) {
			return AppCode.forId(id);
		}
		public static AppCode fromString(final String id) {
			return new AppCode(id);
		}
		public static AppCode forIdOrNull(final String id) {
			if (id == null) return null;
			return new AppCode(id);
		}
		public static AppCode named(final String id) {
			return AppCode.forId(id);
		}
		public static AppCode forLogin(final LoginID loginId) {
			return AppCode.forId(loginId.asString());
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
	public interface IsAppComponent
  		     extends IsAppIdentifier {
		public default AppComponent asAppComponent() {
			return AppComponent.forId(this.asString());
		}
	} 
	/**
	 * AppCode component
	 */
	@MarshallType(as="appComponent")
	public record AppComponent(@Getter String id)
	   implements IsAppComponent {
		
		public static final AppComponent DEFAULT = AppComponent.forId("default");
		public static final AppComponent NO_COMPONENT = AppComponent.forId("_no_component_");
		
		public static AppComponent from(final String id) {
			return new AppComponent(id);
		}
		public static AppComponent forId(final String id) {
			return new AppComponent(id);
		}
		public static AppComponent valueOf(final String id) {
			return AppComponent.forId(id);
		}
		public static AppComponent fromString(final String id) {
			return new AppComponent(id);
		}
		public static AppComponent forIdOrNull(final String id) {
			if (id == null) return null;
			return new AppComponent(id);
		}
		public static AppComponent named(final String id) {
			return AppComponent.forId(id);
		}
		public static AppComponent compose(final IsAppComponent one,final IsAppComponent other) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,other));
		}
		public static AppComponent compose(final IsAppComponent one,final IsAppComponent two,final IsAppComponent three) {
			return AppComponent.forId(Strings.customized("{}.{}.{}",
														 one,two,three));
		}
		public static AppComponent compose(final IsAppComponent one,final String two) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,two));
		}
		public static AppComponent compose(final String one,final String three) {
			return AppComponent.forId(Strings.customized("{}.{}",
														 one,three));
		}
		public static AppComponent compose(final String one,final String two,final String three) {
			return AppComponent.forId(Strings.customized("{}.{}.{}",
														 one,two,three));
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
//	WORKPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="workPlaceCode")
	public record WorkPlaceCode(@Getter String id)
	   implements OIDTyped<String> {
		
		public static WorkPlaceCode from(final String id) {
			return new WorkPlaceCode(id);
		}
		public static WorkPlaceCode forId(final String id) {
			return new WorkPlaceCode(id);
		}
		public static WorkPlaceCode valueOf(final String id) {
			return WorkPlaceCode.forId(id);
		}
		public static WorkPlaceCode fromString(final String s) {
			return WorkPlaceCode.forId(s);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		
		public static final WorkPlaceCode UNKNOWN = WorkPlaceCode.forId("unknown");
		public boolean isUnknown() {
			return this.is(WorkPlaceCode.UNKNOWN);
		}
		public static final WorkPlaceCode ANONYMOUS = WorkPlaceCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(WorkPlaceCode.ANONYMOUS);
		}
	}
	@Immutable
	@MarshallType(as="buildingCode")
	public record BuildingCode(@Getter String id)
	   implements OIDTyped<String> {

		public static BuildingCode from(final String id) {
			return new BuildingCode(id);
		}
		public static BuildingCode forId(final String id) {
			return new BuildingCode(id);
		}
		public static BuildingCode valueOf(final String id) {
			return BuildingCode.forId(id);
		}
		public static Role fromString(final String s) {
			return Role.forId(s);
		}
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
		
		public static final BuildingCode ANONYMOUS = BuildingCode.forId("anonymous");
		public boolean isAnonymous() {
			return this.is(BuildingCode.ANONYMOUS);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsRole
					extends OID {
		// just a marker interface
	}
	@Immutable
	@MarshallType(as="role")
	public record Role(@Getter String id)
	   implements OIDTyped<String>,
	   			  IsRole {
		
		public static Role from(final String id) {
			return new Role(id);
		}
		public static Role forId(final String id) {
			return new Role(id);
		}
		public static Role valueOf(final String id) {
			return new Role(id);
		}
		public static Role fromString(final String s) {
			return Role.forId(s);
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
//	GENERIC TOKEN
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsToken
			 extends OIDTyped<String> {
		public default byte[] getBytes() {
			return this.asString().getBytes();
		}
	}
	@Immutable
	@MarshallType(as="token")
	public record Token(@Getter String id)
	   implements IsToken {
		
		public static Token from(final String id) {
			return new Token(id);
		}
		public static Token forId(final String id) {
			return new Token(id);
		}
		public static Token valueOf(final String id) {
			return new Token(id);
		}
		public static Token fromString(final String s) {
			return Token.forId(s);
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
//	ENVIRONMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Tenant identifier
	 */
	@Immutable
	@MarshallType(as="tenantId")
	public record TenantID(@Getter String id)
	   implements OIDTyped<String> {
		
		public static final TenantID DEFAULT = TenantID.forId("default");

		public static TenantID from(final String id) {
			return new TenantID(id);
		}
		public static TenantID forId(final String id) {
			return new TenantID(id);
		}
		public static TenantID valueOf(final String s) {
			return TenantID.forId(s);
		}
		public static TenantID fromString(final String s) {
			return TenantID.forId(s);
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
	@MarshallType(as="env")
	public record Environment(@Getter String id)
	   implements OIDTyped<String> {
		
		public static Environment DEFAULT = Environment.forId("default");

		public static Environment forId(final String id) {
			return new Environment(id);
		}
		public static Environment valueOf(final String id) {
			return Environment.forId(id);
		}
		public static Environment fromString(final String s) {
			return Environment.forId(s);
		}
		public Env getEnv() {
			return Env.from(this);
		}
		public boolean isLocal() {
			return this.getId().toLowerCase().startsWith("loc");
		}
		public static Environment guessFromSystemProps() {
			String envProp = null;  
			// 1... Try to Read from virtual machine properties.	  
			envProp = _readEnvPropFrom(System.getProperties());  
			
			// 2. ... Try Read from machine environment  
			//(  	...another option could be to pass, a env name as System.getenv("env_type")..
			if (Strings.isNullOrEmpty(envProp)) {
				envProp = _readEnvPropFrom(System.getenv());
			}
			// 3. ... Try Read from files at resoure stream into classloader.
			if (Strings.isNullOrEmpty(envProp)) {
				envProp = _readEnvPropFrom();
			}	
			if (Strings.isNOTNullOrEmpty(envProp)) {
				log.info("\n\nR01Env property SET to '{}'",
						 envProp);
				return Environment.forId(envProp);
			} 
			log.info("\n\nR01Env property NOT SET defaulting to '{}'\n", Environment.DEFAULT);
			
			return Environment.DEFAULT;
		}	
		/**
		 * Used when loaded inside a docker instance
		 * The docker instance has a properties file like:
		 * <pre>
		 * 		r01Env.properties > R01Env=dev
		 * 							R01Home=/r01
		 * 							...
		 * </pre>
		 * or
		 * <pre>
		 * 		R01_ENV.properties > R01-ENV=dev
		 * 							 R01Home=/r01
		 * 							 ...
		 * </pre>
		 * @return
		 */
		private static String _readEnvPropFrom() {
		  	  String envProp = null;
			  envProp = _readEnvPropFrom("R01ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("r01Env.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("R01Env.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("R01_ENV.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("r01_env.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("R01-ENV.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("r01-env.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("ENV.properties");
			  if (Strings.isNullOrEmpty(envProp)) envProp = _readEnvPropFrom("env.properties");
			  
			  if (Strings.isNOTNullOrEmpty(envProp)) {
				  log.warn("... found ENV prop={} at a properties file named [r01Env.properties]",
						   envProp);
			  }
			  return envProp;
		}
		private static String _readEnvPropFrom(final Map<String,String> propertiesAsMap) {
		  	  String envProp = null;
			  envProp = System.getProperty("R01ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("r01Env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("R01Env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("R01_ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("r01_env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("R01-ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("r01-env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = propertiesAsMap.get("env");
			  return envProp;
		}
		private static String _readEnvPropFrom(final Properties properties) {
			  String envProp = null;
			  envProp = System.getProperty("R01ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("r01Env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("R01Env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("R01_ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("r01_env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("R01-ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("r01-env");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("ENV");
			  if (Strings.isNullOrEmpty(envProp)) envProp = properties.getProperty("env");
			  return envProp;
		}
		private static String _readEnvPropFrom(final String fileNameAsResourceStream) {
			try (InputStream is = Environment.class.getClassLoader()
												   .getResourceAsStream(fileNameAsResourceStream)) {
				if (is != null ) {
					Properties propFromFile = new Properties();
					propFromFile.load(is);
					propFromFile.list(System.out);
					return _readEnvPropFrom(propFromFile);
				}
			} catch (final Throwable th) {
				// log
				log.error(" Unable to read : " + fileNameAsResourceStream  + th.getLocalizedMessage());
			}
			return null;	   
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
	@MarshallType(as="execContextOid")
	public record ExecContextId(@Getter String id)
	    implements OIDTyped<String> {

		public static ExecContextId forId(final String id) {
			return new ExecContextId(id);
		}
		public static ExecContextId valueOf(final String str) {
			return new ExecContextId(str);
		}
		public static ExecContextId fromString(final String s) {
			return ExecContextId.forId(s);
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
//	WEB
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="webSessionId")
	public record WebSessionID(@Getter String id)
	   implements OIDTyped<String> {
		
		public static WebSessionID forId(final String id) {
			return new WebSessionID(id);
		}
		public static WebSessionID valueOf(final String id) {
			return WebSessionID.forId(id);
		}
		public static WebSessionID fromString(final String s) {
			return WebSessionID.forId(s);
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
//	PROPERTY ID
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="propertyId")
	public record PropertyID(@Getter String id)
	   implements OIDTyped<String> {
		
		public static PropertyID forId(final String id) {
			return new PropertyID(id);
		}
		public static PropertyID valueOf(final String id) {
			return PropertyID.forId(id);
		}
		public static PropertyID fromString(final String s) {
			return PropertyID.forId(s);
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
