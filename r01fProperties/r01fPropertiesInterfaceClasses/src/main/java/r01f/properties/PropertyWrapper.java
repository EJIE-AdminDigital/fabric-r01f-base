package r01f.properties;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.reflect.TypeToken;

import r01f.enums.EnumWithCode;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
import r01f.oids.CommonOIDs.AppCode;
import r01f.oids.CommonOIDs.Environment;
import r01f.oids.CommonOIDs.Token;
import r01f.oids.OID;
import r01f.oids.SecurityIDs.LoginID;
import r01f.oids.SecurityIDs.Password;
import r01f.oids.SecurityIDs.SecurityRole;
import r01f.oids.SecurityIDs.SecurityToken;
import r01f.patterns.FactoryFrom;
import r01f.reflection.ReflectionUtils;
import r01f.resources.ResourcesLoaderDef;
import r01f.types.JavaPackage;
import r01f.types.JavaTypeName;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.OSType;
import r01f.util.types.Strings;

/**
 * Wraps an document's property providing means to get typed properties
 */
public interface PropertyWrapper<N> {
/////////////////////////////////////////////////////////////////////////////////////////
//	RAW NODE ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the document's node containing the property
	 * BEWARE!! this method DOES NOT use the cache
	 * @return the document's node containing the property
	 */
	public N node();
	/**
	 * Returns an iterator of the document's node list containing the property
	 * BEWARE!! this method DOES NOT use the cache
	 * @return a document's node list iterator
	 */
	public Iterator<N> nodeListIterator();
	/**
	 * Returns an iterable of the document's node list containing the property
	 * BEWARE!! this method DOES NOT use the cache
	 * @return a document's node list iterable
	 */
	public default Iterable<N> nodeListIterable() {
		return new Iterable<>() {
						Iterator<N> _iterator = PropertyWrapper.this.nodeListIterator();

						@Override
						public Iterator<N> iterator() {
							return _iterator;
						}
			   };
	}
	/**
	 * Returns a stream of the document's node list containing the property
	 * @return
	 */
	public default Stream<N> nodeStream() {
		return StreamSupport.stream(this.nodeListIterable()
										.spliterator(),
									false);	// not parallel
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a property exists
	 * @return true if the property exists, false otherwise
	 */
	public boolean exist();
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link String}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public String asString(final String defaultVal);
	/**
	 * Gets a property as a {@link String}
	 * @return
	 */
	public default String asString() {
		return this.asString((String)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST OF STRINGS & PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link List} of {@link String}s
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public List<String> asListOfStrings(final List<String> defaultVal);
	/**
	 * Gets a property as a {@link List} of {@link String}s
	 * @return
	 */
	public default List<String> asListOfStrings() {
		return this.asListOfStrings((List<String>)null);
	}
	/**
	 * Gets a property as a {@link List} of {@link String}s
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default List<String> asListOfStrings(final String... defaultStrings) {
		List<String> defaultVal = defaultStrings != null ? Arrays.asList(defaultStrings) : null;
		return this.asListOfStrings(defaultVal);
	}
	/**
	 * Gets a property as a {@link Properties}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Properties asProperties(final Properties defaultVal);
	/**
	 * Gets a property as a {@link Properties}
	 * @return
	 */
	public default Properties asProperties() {
		return this.asProperties((Properties)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NUMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Number}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Number asNumber(final Number defaultVal);
	/**
	 * Gets a property as a {@link Number}
	 * @return
	 */
	public default Number asNumber() {
		return this.asNumber((Number)null);
	}
	/**
	 * Gets a property as an {@link Integer}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public int asInteger(final int defaultVal);
	/**
	 * Gets a property as an {@link Integer}
	 * @return
	 */
	@SuppressWarnings("null")
	public default int asInteger() {
		return this.asInteger((Integer)null);
	}
	/**
	 * Gets a property as a {@link Long}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public long asLong(final long defaultVal);
	/**
	 * Gets a property as a {@link Long}
	 * @return
	 */
	@SuppressWarnings("null")
	public default long asLong() {
		return this.asLong((Long)null);
	}
	/**
	 * Gets a property as a {@link Double}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public double asDouble(final double defaultVal);
	/**
	 * Gets a property as a {@link Double}
	 * @return
	 */
	@SuppressWarnings("null")
	public default double asDouble() {
		return this.asDouble((Double)null);
	}
	/**
	 * Gets a property as a {@link Float}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public float asFloat(final float defaultVal);
	/**
	 * Gets a property as a {@link Float}
	 * @return
	 */
	@SuppressWarnings("null")
	public default float asFloat() {
		return this.asFloat((Float)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Boolean}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public boolean asBoolean(final boolean defaultVal);
	/**
	 * Gets a property as a {@link Boolean}
	 * @return
	 */
	public default boolean asBoolean() {
		return this.asBoolean(false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LANGUAGE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link Language} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public Language asLanguageFromCode(final Language defaultVal);
	/**
	 * The property value as an {@link Language} object
	 * @return
	 */
	public default Language asLanguageFromCode() {
		return this.asLanguageFromCode((Language)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENVIRONMENT & APPCODE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link Environment} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public Environment asEnvironment(final Environment defaultVal);
	/**
	 * The property value as an {@link Environment} object
	 * @return
	 */
	public default Environment asEnvironment() {
		return this.asEnvironment((Environment)null);
	}
	/**
	 * The property value as an {@link Environment} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public default Environment asEnvironment(final String defaultVal) {
		Environment outEnv = this.asEnvironment();
		return outEnv != null ? outEnv : Environment.forId(defaultVal);
	}
	/**
	 * The property value as an {@link AppCode} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public AppCode asAppCode(final AppCode defaultVal);
	/**
	 * The property value as an {@link AppCode} object
	 * @return
	 */
	public default AppCode asAppCode() {
		return this.asAppCode((AppCode)null);
	}
	/**
	 * The property value as an {@link AppCode} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public default AppCode asAppCode(final String defaultVal) {
		return this.asAppCode(Strings.isNOTNullOrEmpty(defaultVal) ? AppCode.forId(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  USER / PASSWORD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link LoginID}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public LoginID asLoginId(final LoginID defaultVal);
	/**
	 * Gets a property as a {@link LoginID}
	 * @return
	 */
	public default LoginID asLoginId() {
		return this.asLoginId((LoginID)null);
	}
	/**
	 * Gets a property as a {@link LoginID}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default LoginID asLoginId(final String defaultVal) {
		return this.asLoginId(Strings.isNOTNullOrEmpty(defaultVal) ? LoginID.forId(defaultVal) : null);
	}
	/**
	 * Gets a property as a {@link Password}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Password asPassword(final Password defaultVal);
	/**
	 * Gets a property as a {@link Password}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Password asPassword() {
		return this.asPassword((Password)null);
	}
	/**
	 * Gets a property as a {@link Password}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Password asPassword(final String defaultVal) {
		return this.asPassword(Strings.isNOTNullOrEmpty(defaultVal) ? Password.forId(defaultVal) : null);
	}
	/**
	 * Gets a property as a {@link SecurityRole}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public SecurityRole asUserRole(final SecurityRole defaultVal);
	/**
	 * Gets a property as a {@link SecurityRole}
	 * @return
	 */
	public default SecurityRole asUserRole() {
		return this.asUserRole((SecurityRole)null);
	}
	/**
	 * Gets a property as a {@link SecurityRole}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default SecurityRole asUserRole(final String defaultVal) {
		return this.asUserRole(Strings.isNOTNullOrEmpty(defaultVal) ? SecurityRole.forId(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property as an OID
	 * @param <O>
	 * @param oidFactory
	 * @param defaultVal
	 * @return
	 */
	public <O extends OID> O asOID(final FactoryFrom<String,O> oidFactory,
								   final O defaultVal);
	/**
	 * The property as an OID
	 * @param <O>
	 * @param oidFactory
	 * @return
	 */
	@SuppressWarnings("null")
	public default <O extends OID> O asOID(final FactoryFrom<String,O> oidFactory) {
		return this.asOID(oidFactory,
						  (O)null);
	}
	/**
	 * The property value as an {@link OID} object
	 * @return
	 */
	public default <O extends OID> O asOID(final Class<O> oidType) {
		return this.asOID(oid -> _createOIDFromString(oidType,oid));	// factory from
	}
	/**
	 * The property value as an {@link OID} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	public default <O extends OID> O asOID(final Class<O> oidType,
								   		   final O defaultVal) {
		return this.asOID(oid -> _createOIDFromString(oidType,oid),	// factory from
						  defaultVal);
	}
	private static <O extends OID> O _createOIDFromString(final Class<O> oidType,
														  final String oidAsString) {
		O outOid = null;
		try {
			outOid = ReflectionUtils.createInstanceFromString(oidType,oidAsString);
		} catch (Throwable th) {
			th.printStackTrace(System.out);
		}
		if (outOid == null) outOid = ReflectionUtils.<O>invokeStaticMethod(oidType,
																		   "forId",
																		   new Class<?>[] {String.class},
																		   new Object[] {oidAsString});
		return outOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TOKEN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Token}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Token asToken(final Token defaultVal);
	/**
	 * Gets a property as a {@link Token}
	 * @return
	 */
	public default Token asToken() {
		return this.asToken((Token)null);
	}
	/**
	 * Gets a property as a {@link Token}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Token asToken(final String defaultVal) {
		return this.asToken(Strings.isNOTNullOrEmpty(defaultVal) ? Token.forId(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SECURITY TOKEN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link SecurityToken}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public SecurityToken asSecurityToken(final SecurityToken defaultVal);
	/**
	 * Gets a property as a {@link SecurityToken}
	 * @return
	 */
	public default SecurityToken asSecurityToken() {
		return this.asSecurityToken((SecurityToken)null);
	}
	/**
	 * Gets a property as a {@link SecurityToken}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default SecurityToken asSecurityToken(final String defaultVal) {
		return this.asSecurityToken(Strings.isNOTNullOrEmpty(defaultVal) ? SecurityToken.forId(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RegEx
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a regex {@link Pattern}
	 * @param defaultPattern the default pattern if the property is not defined at the properties file
	 * @return
	 */
	public Pattern asRegexPattern(final Pattern defaultVal);
	/**
	 * Gets a property as a regex {@link Pattern}
	 * @return
	 */
	public default Pattern asRegexPattern() {
		String outPatternStr = this.asString();
		return Strings.isNOTNullOrEmpty(outPatternStr) ? Pattern.compile(outPatternStr) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EMAIL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link EMail}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	public EMail asEMail(final EMail defaultVal);
	/**
	 * The property value as an {@link EMail}
	 * @return
	 */
	public default EMail asEMail() {
		return this.asEMail((EMail)null);
	}
	/**
	 * The property value as an {@link EMail}
	 * @param defaultVal
	 * @return
	 */
	public default EMail asEMail(final String defaultVal) {
		return this.asEMail(Strings.isNOTNullOrEmpty(defaultVal) ? EMail.create(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PHONE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link Phone}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	public Phone asPhone(final Phone defaultVal);
	/**
	 * The property value as an {@link EMail}
	 * @return
	 */
	public default Phone asPhone() {
		return this.asPhone((Phone)null);
	}
	/**
	 * The property value as an {@link Phone}
	 * @param defaultVal
	 * @return
	 */
	public default Phone asPhone(final String defaultVal) {
		return this.asPhone(Strings.isNOTNullOrEmpty(defaultVal) ? Phone.create(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TIMELAPSE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link TimeLapse}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	public TimeLapse asTimeLapse(final TimeLapse defaultVal);
	/**
	 * The property value as an {@link TimeLapse}
	 * @return
	 */
	public default TimeLapse asTimeLapse() {
		String timeLapse = this.asString();
		return Strings.isNOTNullOrEmpty(timeLapse) ? TimeLapse.createFor(timeLapse) : null;
	}
	/**
	 * The property value as an {@link TimeLapse}
	 * @param defaultVal
	 * @return
	 */
	public default TimeLapse asTimeLapse(final String defaultVal) {
		return this.asTimeLapse(Strings.isNOTNullOrEmpty(defaultVal) ? TimeLapse.createFor(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HOST & URL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the property as a {@link Host}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Host asHost(final Host defaultVal);
	/**
	 * Gets the property as a {@link Host}
	 * @return
	 */
	public default Host asHost() {
		return this.asHost((Host)null);
	}
	/**
	 * Gets the property as a {@link Url}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Url asUrl(final Url defaultVal);
	/**
	 * Gets the property as a {@link Url}
	 * @return
	 */
	public default Url asUrl() {
		return this.asUrl((Url)null);
	}
	/**
	 * Gets the property as a {@link Url}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Url asUrl(final String defaultVal) {
		return this.asUrl(Strings.isNOTNullOrEmpty(defaultVal) ? Url.from(defaultVal) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link UrlPath}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public UrlPath asUrlPath(final UrlPath defaultVal);
	/**
	 * Gets a property as a {@link PaUrlPathth}
	 * @return
	 */
	public default UrlPath asUrlPath() {
		return this.asUrlPath((UrlPath)null);
	}
	/**
	 * Gets a property as a {@link UrlPath}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default UrlPath asUrlPath(final String defaultVal) {
		return this.asUrlPath(UrlPath.from(Strings.isNOTNullOrEmpty(defaultVal) ? PropertyWrapper.replacePathVars(defaultVal) : null));
	}
	/**
	 * Gets a property as a {@link Path}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Path asPath(final Path defaultVal);
	/**
	 * Gets a property as a {@link Path}
	 * @return
	 */
	public default Path asPath() {
		return this.asPath((Path)null);
	}
	/**
	 * Gets a property as a {@link Path}
	 * @param defaultVal the default value to be returned if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Path asPath(final String defaultVal) {
		return this.asPath(Path.from(Strings.isNOTNullOrEmpty(defaultVal) ? PropertyWrapper.replacePathVars(defaultVal) : null));
	}
	/**
	 * Gets a property as a {@link Path}
	 * @param valByOS
	 * @return
	 */
	public default Path asPath(final PropertyDefaultValueByOS<Path> valByOS) {
		Path outPath = this.asPath();
		return outPath != null ? outPath : valByOS.getFor(OSType.getOS());
	}
	static String replacePathVars(final String pathAsString) {
		if (Strings.isNullOrEmpty(pathAsString)) return null;

		String outPathAsString = pathAsString;
		OSType osType = OSType.getOS();
		if (pathAsString.startsWith("%ROOT%")) {
			if (osType == OSType.Linux || osType == OSType.NIX) {
				outPathAsString = pathAsString.replace("%ROOT%","/");
			} else if (osType == OSType.WINDOWS) {
				outPathAsString = pathAsString.replace("%ROOT%","d:/");
			}
		}
		return outPathAsString;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHARSET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Charset}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Charset asCharset(final Charset defaultVal);
	/**
	 * Gets a property as a {@link Charset}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default Charset asCharset() {
		return this.asCharset((Charset)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JAVA CLASS TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Class}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public Class<?> asType(final Class<?> defaultVal);
	/**
	 * Gets a property as a {@link Class}
	 * @return
	 */
	public default Class<?> asType() {
		return this.asType((Class<?>)null);
	}
	/**
	 * Gets a property as a {@link JavaPackage}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public JavaTypeName asJavaTypeName(final JavaTypeName defaultVal);
	/**
	 * Gets a property as a JavaPackage
	 * @return
	 */
	public default JavaTypeName asJavaTypeName() {
		return this.asJavaTypeName((JavaTypeName)null);
	}
	/**
	 * Gets a property as a {@link JavaPackage}
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public JavaPackage asJavaPackage(final JavaPackage defaultVal);
	/**
	 * Gets a property as a JavaPackage
	 * @return
	 */
	public default JavaPackage asJavaPackage() {
		return this.asJavaPackage((JavaPackage)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPLEX OBJECTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a model object transforming from the object's document to model object using the provided {@link Marshaller}
	 * @param marshaller the {@link Marshaller} used to transform the document to a Java object
	 * @param type the returned object's type
	 * @return the object
	 */
	public <T> T asObject(final Marshaller marshaller,
						  final Class<T> type);
	/**
	 * Gets a property as a model object transforming from the object's document to model object using the provided {@link Marshaller}
	 * @param marshaller the {@link Marshaller} used to transform the document to a Java object
	 * @param typeToken the returned object's type
	 * @return the object
	 */
	public <T> T asObject(final Marshaller marshaller,
						  final TypeToken<T> typeToken);
	/**
	 * Returns the property as an object transforming the node to an object
	 * using a {@link Function}
	 * @param transformFuncion
	 * @return
	 */
	public <T> T asObject(final Function<N,T> transformFuncion);
	/**
	 * Returns the property as an object build from the property string value
	 * @param objFactory
	 * @return
	 */
	public <T> T asObjectFromString(final FactoryFrom<String,T> objFactory,
									final T defaultVal);
	/**
	 * Returns the property as an object build from the property string value
	 * @param objFactory
	 * @return
	 */
	@SuppressWarnings("null")
	public default <T> T asObjectFromString(final FactoryFrom<String,T> objFactory) {
		return this.asObjectFromString(objFactory,
									   (T)null);
	}
	/**
	 * Returns the property as an object build from the property string value
	 * To do so, the object type MUST implement an static valueOf(String) method
	 * or an static fromString(String) method
	 * @param objType
	 * @return
	 */
	public default <T> T asObjectFromString(final Class<T> objType) {
		return this.asObjectFromString(valAsString -> ReflectionUtils.<T>createInstanceFromString(objType,
														   										  valAsString));
	}
	/**
	 * Returns the property as an object build from the property string value
	 * To do so, the object type MUST implement an static valueOf(String) method
	 * or an static fromString(String) method
	 * @param objType
	 * @param defaultValue the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public default <T> T asObjectFromString(final Class<T> objType,
									final T defaultValue) {
		return this.asObjectFromString(valAsString -> ReflectionUtils.<T>createInstanceFromString(objType,
														   										  valAsString),
									   defaultValue);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OBJECT LIST
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Transforms the child nodes into a collection of objects
	 * @param transformFunction
	 * @param defaultVal
	 * @return
	 */
	public <T> Collection<T> asObjectList(final Function<N,T> transformFunction,
										  final Collection<T> defaultVal);
	/**
	 * Transforms the child nodes into a collection of objects
	 * @param transformFunction
	 * @return
	 */
	public default <T> Collection<T> asObjectList(final Function<N,T> transformFunction) {
		return this.asObjectList(transformFunction,
								 (Collection<T>)null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public <E extends Enum<E>> E asEnumElement(final Class<E> enumType,
											   final E defaultVal);
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @return
	 */
		@SuppressWarnings("null")
	public default <E extends Enum<E>> E asEnumElement(final Class<E> enumType) {
		return this.asEnumElement(enumType,
								  (E)null);
	}
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @return
	 */
		@SuppressWarnings("null")
	public default <E extends Enum<E>> E asEnumElementOrThrow(final Class<E> enumType) {
		E out = this.asEnumElement(enumType,
								   (E)null);
		if (out == null) throw new IllegalArgumentException(this.asString() + " is NOT a valid value for enum type " + enumType);
		return out;
	}
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public <E extends Enum<E>> E asEnumElementIgnoringCase(final Class<E> enumType,
														   final E defaultVal);
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @return
	 */
	@SuppressWarnings("null")
	public default <E extends Enum<E>> E asEnumElementIgnoringCase(final Class<E> enumType) {
		return this.asEnumElementIgnoringCase(enumType,
											  (E)null);
	}
	/**
	 * Returns the property as an enum element using the node value as the enum code
	 * (the enum MUST implement EnumWithCode<String,E>
	 * @param enumType
	 * @return
	 */
	@SuppressWarnings("null")
	public default <E extends Enum<E> & EnumWithCode<String,E>> E asEnumFromCode(final Class<E> enumType) {
		return this.asEnumFromCode(enumType,
								   (E)null);
	}
	/**
	 * Returns the property as an enum element using the node value as the enum code
	 * (the enum MUST implement EnumWithCode<String,E>
	 * @param enumType
	 * @param defaultVal the default value if the property is not defined at the properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	public <E extends Enum<E> & EnumWithCode<String,E>> E asEnumFromCode(final Class<E> enumType,
																		 final E defaultVal);
/////////////////////////////////////////////////////////////////////////////////////////
//	RESOURCES LOADER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property as a {@link ResourcesLoaderDef}
	 * @param defaultVal
	 * @return
	 */
	public ResourcesLoaderDef asResourcesLoaderDef(final ResourcesLoaderDef defaultVal);
	/**
	 * Returns the property as a {@link ResourcesLoaderDef}
	 * @return
	 */
	public default ResourcesLoaderDef asResourcesLoaderDef() {
		return this.asResourcesLoaderDef((ResourcesLoaderDef)null);
	}
}
