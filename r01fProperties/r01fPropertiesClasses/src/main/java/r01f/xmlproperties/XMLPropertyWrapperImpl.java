package r01f.xmlproperties;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.enums.EnumWithCode;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Environment;
import r01f.guids.CommonOIDs.Token;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;
import r01f.properties.PropertyWrapper;
import r01f.reflection.ReflectionUtils;
import r01f.resources.ResourcesLoaderDef;
import r01f.resources.ResourcesLoaderDefBuilder;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.Password;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.securitycontext.SecurityIDS.UserRole;
import r01f.types.JavaPackage;
import r01f.types.JavaTypeName;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.enums.Enums;
import r01f.util.types.Numbers;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;
import r01f.xml.XMLStringSerializer;
import r01f.xml.XMLUtils;

/**
 * Wraps a xml property providing means to get typed properties
 * This wrapper uses a cache behind the scenes that provides access to the properties
 * In order to create a {@link XMLPropertyWrapper} instance:
 * 		[1] - Create a object that provides access to the underlying XML Document
 * 			  an instance of {@link XMLPropertiesForAppComponentAccessor}
 *
 * 		[2] - Create an instance of a component properties cache: {@link XMLPropertiesForAppComponentCache}
 * 			  This cache uses the {@link XMLPropertiesForAppComponentAccessor} to access
 * 			  the underlying XML Document when the property is NOT cached
 */
@Slf4j
public final class XMLPropertyWrapperImpl
		implements XMLPropertyWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForAppComponentCache _cache;
	private final Path _xPath;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertyWrapperImpl(final XMLPropertiesForAppComponentCache componentPropsCache,
								  final Path xPath) {
		_cache = componentPropsCache;
		_xPath = xPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RAW DOM'S NODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Node node() {
		return _cache.node(_xPath);
	}
	@Override
	public NodeList nodeList() {
		return _cache.nodeList(_xPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean exist() {
		return _cache.existProperty(_xPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link String}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public String asString(final String defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> XMLUtils.nodeTextContent(xmlNode),	// transform the xml node to an string
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LIST OF STRINGS & PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link List} of {@link String}s
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public List<String> asListOfStrings(final List<String> defaultVal) {
		// [1]-Fix the xpath
		String effXPathStr = (_xPath != null
						   && !_xPath.asString().endsWith("/child::*"))
									? _xPath.asString().concat("/child::*")
									: _xPath != null ? _xPath.asString() : "";
		// [2]-Get the string list
		Path effXPath = Strings.isNOTNullOrEmpty(effXPathStr) ? Path.from(effXPathStr) : null;
		return effXPath != null
					? _cache.getPropertyFromNodeList(_xPath,
										  			 nodeIt -> {
										  				// a) get a the list of strings indexed by xml tag
										  				Map<String,List<String>> map = NODE_LIST_TO_MAP.apply(nodeIt);

										  				if (CollectionUtils.isNullOrEmpty(map)) return null;

										  				// b) put the Strings in a list
														List<String> outList = new ArrayList<String>(map.size());
														for (Map.Entry<String,List<String>> me : map.entrySet()) {
															if (me.getValue() == null) continue;
															for (String str : me.getValue()) {
																outList.add(str);
															}
														}
														return outList;
										  			 },
										  			 defaultVal)
					: defaultVal;
	}
	/**
	 * Gets a property as a {@link Properties}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Properties asProperties(final Properties defaultVal) {
		return _cache.getPropertyFromNodeList(_xPath,
								  			  xmlNodeList -> {
										  			// a) get a the list of strings indexed by xml tag
										  			Map<String,List<String>> map = NODE_LIST_TO_MAP.apply(xmlNodeList);

										  			if (CollectionUtils.isNullOrEmpty(map)) return null;
														Properties outProps = new Properties();
														for (Map.Entry<String,List<String>> me : map.entrySet()) {
															if (me.getValue() != null && me.getValue().size() == 1) {
																outProps.put(me.getKey(),me.getValue().get(0));
															} else {
																StringBuilder sb = new StringBuilder();
																for (Iterator<String> it = me.getValue().iterator(); it.hasNext(); ) {
																	sb.append(Strings.customized("[{}]",
																								 it.next()));
																	if (it.hasNext()) sb.append(',');
																}
																outProps.put(me.getKey(),sb.toString());
															}
														}
														return outProps;
								  			  },
								  			  defaultVal);
	}
  	private static final Function<Iterator<Node>,
  								  Map<String,List<String>>> NODE_LIST_TO_MAP = nodeIt -> {
									 												if (nodeIt == null || !nodeIt.hasNext()) return Maps.newHashMap();

									 												Map<String,List<String>> outMap = new HashMap<>();
									 												while (nodeIt.hasNext()) {
									 													Node node = nodeIt.next();

																						if (XMLUtils.isTextNode(node)) continue;

																						// Get the item name
																						String nodeName = node.getLocalName() != null ? node.getLocalName() : node.getNodeName();

																						// Get the item value
																						String nodeStrValue = null;
																						Node contentNode = node.getFirstChild();
																						if (XMLUtils.isTextNode(contentNode)) {
																							nodeStrValue = contentNode.getTextContent();
																						} else {
																							nodeStrValue = XMLStringSerializer.writeNodeNoFail(contentNode,null);
																						}
																						// Put the item at the Map
																						if (nodeStrValue != null) {
																							List<String> currItemValue = outMap.get(nodeName);
																							if (currItemValue == null) {
																								currItemValue = new ArrayList<>();
																								outMap.put(nodeName,currItemValue);
																							}
																							currItemValue.add(nodeStrValue);
																							outMap.put(nodeName,currItemValue);
																						}
									 												}
																					return outMap;
																			   };
/////////////////////////////////////////////////////////////////////////////////////////
//  NUMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Number}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Number asNumber(final Number defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String numStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												Number out = null;
												try {
													out = NumberUtils.createNumber(numStr);
												} catch (NumberFormatException nfEx) {
													log.warn("Property {} from component {}: {} cannot be converted to a Number!",
															 _xPath,_cache.getAppComponent(),numStr);
												}
												return out;
										  },
										  defaultVal);

	}
	/**
	 * Gets a property as an {@link Integer}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public int asInteger(final int defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String numStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												int out = defaultVal;
												if (Numbers.isInteger(numStr)) {
													out = Numbers.toInt(numStr);
												} else {
													log.warn("Property {} from component {}: {} cannot be converted to int",
															 _xPath,_cache.getAppComponent(),numStr);
												}
												return out;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link Long}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public long asLong(final long defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String numStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												long out = defaultVal;
												if (Numbers.isLong(numStr)) {
													out = Numbers.toLong(numStr);
												} else {
													log.warn("Property {} from component {}: {} cannot be converted to long",
															 _xPath,_cache.getAppComponent(),numStr);
												}
												return out;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link Double}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public double asDouble(final double defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String numStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												double out = defaultVal;
												if (Numbers.isDouble(numStr)) {
													out = Numbers.toDouble(numStr);
												} else {
													log.warn("Property {} from component {}: {} cannot be converted to double",
															 _xPath,_cache.getAppComponent(),numStr);
												}
												return out;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link Float}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public float asFloat(final float defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String numStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												float out = defaultVal;
												if (Numbers.isFloat(numStr)) {
													out = Numbers.toFloat(numStr);
												} else {
													log.warn("Property {} from component {}: {} cannot be converted to float",
															 _xPath,_cache.getAppComponent(),numStr);
												}
												return out;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOLEAN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Boolean}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public boolean asBoolean(final boolean defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String boolStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												boolean out = defaultVal;
												if (Strings.isNOTNullOrEmpty(boolStr)) {
													if (Strings.isContainedWrapper(boolStr)
															   .in("true","1")) {
														out = true;
													} else if (Strings.isContainedWrapper(boolStr)
																	  .in("false","0")) {
														out = false;
													}
												}
												return out;
										  },
										  defaultVal);
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
	@Override
	public Language asLanguageFromCode(final Language defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String langStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(langStr) ? Languages.fromLanguageCode(langStr) : null;
										  },
										  defaultVal);
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
	@Override
	public Environment asEnvironment(final Environment defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String envStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(envStr) ? Environment.forId(envStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * The property value as an {@link AppCode} object or the default value if the
	 * property is not found
	 * @param defaultVal
	 * @return
	 */
	@Override
	public AppCode asAppCode(final AppCode defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String appCodeStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(appCodeStr) ? AppCode.forId(appCodeStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  USER / PASSWORD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link LoginID}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public LoginID asLoginId(final LoginID defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String loginIdStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(loginIdStr) ? LoginID.forId(loginIdStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link Password}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Password asPassword(final Password defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String pwdStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(pwdStr) ? Password.forId(pwdStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link UserRole}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public UserRole asUserRole(final UserRole defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String userRoleStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(userRoleStr) ? UserRole.forId(userRoleStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends OID> O asOID(final FactoryFrom<String,O> oidFactory,
								   final O defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String oidStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(oidStr) ? oidFactory.from(oidStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TOKEN
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Token}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Token asToken(final Token defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String tokenStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(tokenStr) ? Token.forId(tokenStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link SecurityToken}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public SecurityToken asSecurityToken(final SecurityToken defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String tokenStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(tokenStr) ? SecurityToken.forId(tokenStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RegEx
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a regex {@link Pattern}
	 * @param defaultPattern the default pattern if the property is not defined at the xml properties file
	 * @return
	 */
	@Override
	public Pattern asRegexPattern(final Pattern defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String patternStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(patternStr) ? Pattern.compile(patternStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EMAIL & PHONE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link EMail}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public EMail asEMail(final EMail defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String emailStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(emailStr) ? EMail.create(emailStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * The property value as an {@link Phone}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Phone asPhone(final Phone defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String phoneStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(phoneStr) ? Phone.create(phoneStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TIMELAPSE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property value as an {@link TimeLapse}
	 * @param defaultVal
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public TimeLapse asTimeLapse(final TimeLapse defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String timeLapseStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(timeLapseStr) ? TimeLapse.createFor(timeLapseStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HOST & URL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the property as a {@link Host}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Host asHost(final Host defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String hostStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(hostStr) ? Host.of(hostStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets the property as a {@link Url}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Url asUrl(final Url defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String urlStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(urlStr) ? Url.from(urlStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PATH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link UrlPath}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public UrlPath asUrlPath(final UrlPath defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String urlStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(urlStr) ? new UrlPath(PropertyWrapper.replacePathVars(urlStr)) : null;
										  },
										  defaultVal != null ? new UrlPath(PropertyWrapper.replacePathVars(defaultVal.asString())) : null);
	}
	/**
	 * Gets a property as a {@link Path}
	 * @param defaultVal the default value to be returned if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Path asPath(final Path defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String pathStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(pathStr) ? new Path(PropertyWrapper.replacePathVars(pathStr)) : null;
										  },
										  defaultVal != null ? new Path(PropertyWrapper.replacePathVars(defaultVal.asString())) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHARSET
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Charset}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Charset asCharset(final Charset defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String outCharsetStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(outCharsetStr) ? Charset.forName(outCharsetStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JAVA CLASS TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a {@link Class}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public Class<?> asType(final Class<?> defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String typeName = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(typeName) ? ReflectionUtils.typeFromClassName(typeName) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link JavaPackage}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public JavaTypeName asJavaTypeName(final JavaTypeName defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String typeNameStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(typeNameStr) ? new JavaTypeName(typeNameStr) : null;
										  },
										  defaultVal);
	}
	/**
	 * Gets a property as a {@link JavaPackage}
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public JavaPackage asJavaPackage(final JavaPackage defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String pckgStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(pckgStr) ? new JavaPackage(pckgStr) : null;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPLEX OBJECTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a property as a model object transforming from the object's XML to model object using the provided {@link Marshaller}
	 * @param marshaller the {@link Marshaller} used to transform the XML to a Java object
	 * @param type the returned object's type
	 * @return the object
	 */
	@Override
	public <T> T asObject(final Marshaller marshaller,
						  final Class<T> type) {
		return _cache.getPropertyFromNode(_xPath,
								   		  node -> marshaller.forReading()
														 	.fromXml(XMLUtils.asString(node),
														 			 type),
								   		  null);	// default val
	}
	/**
	 * Gets a property as a model object transforming from the object's XML to model object using the provided {@link Marshaller}
	 * @param marshaller the {@link Marshaller} used to transform the XML to a Java object
	 * @param typeToken the returned object's type
	 * @return the object
	 */
	@Override
	public <T> T asObject(final Marshaller marshaller,
						  final TypeToken<T> typeToken) {
		return _cache.getPropertyFromNode(_xPath,
								   		  node -> marshaller.forReading()
														 	.fromXml(XMLUtils.asString(node),
														 			 typeToken),
								   		  null);	// default val
	}
	/**
	 * Returns the property as an object transforming the node to an object
	 * using a {@link Function}
	 * @param transformFuncion
	 * @return
	 */
	@Override
	public <T> T asObject(final Function<Node,T> transformFuncion) {
		return _cache.getPropertyFromNode(_xPath,
								   		  node -> transformFuncion.apply(node),
								   		  null);	// default val
	}
	/**
	 * Returns the property as an object build from the property string value
	 * @param objFactory
	 * @return
	 */
	@Override
	public <T> T asObjectFromString(final FactoryFrom<String,T> objFactory,
									final T defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String valueAsString = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												return Strings.isNOTNullOrEmpty(valueAsString) ? objFactory.from(valueAsString) : null;
										  },
										  defaultVal);
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
	@Override
	public <T> Collection<T> asObjectList(final Function<Node,T> transformFunction,
										  final Collection<T> defaultVal) {
		return _cache.getPropertyFromNodeList(_xPath,
											  nodeIt -> {
													Collection<T> outObjs = null;
													if (nodeIt != null && nodeIt.hasNext()) {
														outObjs = StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodeIt,Spliterator.ORDERED),
																					   false)
																			   .map(node -> transformFunction.apply(node))
																			   .toList();
													} else {
														outObjs = defaultVal;
													}
													return outObjs;
											  },
											  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENUM
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public <E extends Enum<E>> E asEnumElement(final Class<E> enumType,
											   final E defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String enumAsStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												E outE = null;
												if (Strings.isNOTNullOrEmpty(enumAsStr)) {
													try {
														outE = Enum.valueOf(enumType,enumAsStr);
													} catch (IllegalArgumentException illArgEx) {
														log.warn(enumAsStr + " is NOT a valid value for enum type " + enumType);
													}
												}
												return outE;
										  },
										  defaultVal);
	}
	/**
	 * Returns the property as an {@link Enum} element
	 * @param enumType the {@link Enum} type
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public <E extends Enum<E>> E asEnumElementIgnoringCase(final Class<E> enumType,
														   final E defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String enumAsStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												E outE = null;
												if (Strings.isNOTNullOrEmpty(enumAsStr)) {
													try {
														outE = Enums.of(enumType)
																	.fromNameIgnoringCase(enumAsStr)
																	.orElse(defaultVal);
													} catch (IllegalArgumentException illArgEx) {
														log.warn(enumAsStr + " is NOT a valid value for enum type " + enumType);
													}
												}
												return outE;
										  },
										  defaultVal);
	}
	/**
	 * Returns the property as an enum element using the xml value as the enum code
	 * (the enum MUST implement EnumWithCode<String,E>
	 * @param enumType
	 * @param defaultVal the default value if the property is not defined at the xml properties file
	 * @return the property value or the default value if the property is NOT found
	 */
	@Override
	public <E extends Enum<E> & EnumWithCode<String,E>> E asEnumFromCode(final Class<E> enumType,
																		 final E defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												String enumAsStr = XMLUtils.nodeTextContent(xmlNode);	// transform the xml node to an string
												E outE = null;
												if (Strings.isNOTNullOrEmpty(enumAsStr)) {
													try {
														outE = EnumWithCode.fromCode(enumAsStr,enumType)
																	       .orElse(defaultVal);
													} catch (IllegalArgumentException illArgEx) {
														log.warn(enumAsStr + " is NOT a valid code for enum type " + enumType);
													}
												}
												return outE;
										  },
										  defaultVal);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RESOURCES LOADER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the property as a {@link ResourcesLoaderDef}
	 * @param defaultVal
	 * @return
	 */
	@Override
	public ResourcesLoaderDef asResourcesLoaderDef(final ResourcesLoaderDef defaultVal) {
		return _cache.getPropertyFromNode(_xPath,
										  xmlNode -> {
												return ResourcesLoaderDefBuilder.from(xmlNode);
										  },
										  defaultVal);
	}
}
