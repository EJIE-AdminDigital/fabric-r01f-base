package r01f.xmlproperties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Environment;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class XMLPropertiesEnv {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Guess the {@link Environment} value from a system wide property
	 * @return
	 */
	public static Environment guessEnvironmentFromSystemEnvProp() {
		return Environment.guessFromSystemProps();
	}
	/**
	 * Guess the {@link Environment} value from an attribute at the component's xml's root node
	 * @param compProps component properties
	 * @return
	 */
	public static Environment guessEnvironmentFromXMLProperties(final XMLPropertiesForAppComponentAccessor compProps) {
		String envProp = compProps.getPropertyFromNode(Path.from("/*/@env"),
											   		   xmlNode -> XMLUtils.nodeTextContent(xmlNode),
											   		   null);	// default value
		if (Strings.isNullOrEmpty(envProp)) envProp = compProps.getPropertyFromNode(Path.from("/*/@r01Env"),
																					xmlNode -> XMLUtils.nodeTextContent(xmlNode),
																					null);

		return Strings.isNOTNullOrEmpty(envProp) ? Environment.forId(envProp)
												 : Environment.DEFAULT;
	}
}
