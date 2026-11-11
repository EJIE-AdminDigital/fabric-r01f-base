package r01f.properties;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.CommonOIDs.AppCode;
import r01f.oids.CommonOIDs.AppComponent;
import r01f.types.Path;

@MarshallType(as="propertyLocation")
@Accessors(prefix="_")
public class PropertyLocation
  implements Serializable {

	private static final long serialVersionUID = -7089215959762485973L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern RES_LOADER_PATTERN = Pattern.compile("([^/]+)$([^/]+)$(.+)");
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="appCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AppCode _appCode;

	@MarshallField(as="component",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AppComponent _component;

	@MarshallField(as="xPath")
	@Getter @Setter private Path _xPath;

/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public PropertyLocation() {
		// default no-args constructor
	}
	public PropertyLocation(final AppCode appCode,final AppComponent component,final Path path) {
		_appCode = appCode;
		_component = component;
		_xPath = path;
	}
	public PropertyLocation(final PropertyLocation other) {
		_appCode = other.getAppCode();
		_component = other.getComponent();
		_xPath = other.getXPath();
	}
	public static PropertyLocation createFor(final AppCode appCode,final AppComponent component,final Path path) {
		return new PropertyLocation(appCode,component,path);
	}
	/**
	 * Creates a {@link PropertyLocation} from it's id which is a String like:
	 * {appCode}${component}${xPath}
	 * @param id the identifier
	 * @return the {@link PropertyLocation} composed from the id
	 */
	public static PropertyLocation fromId(final String id) {
		// The location of the property where the ResourcesLoader definition (ResourcesLoaderDef)
		// is expressed like {appCode}${component}${xPath}
		PropertyLocation outLoc = null;
		Matcher m = RES_LOADER_PATTERN.matcher(id);
		if (m.find() && m.groupCount() == 3) {
			AppCode appCode = AppCode.forId(m.group(1));
			AppComponent component = AppComponent.forId(m.group(2));
			Path xPath = Path.from(m.group(3));
			outLoc = new PropertyLocation(appCode,component,xPath);
		}
		return outLoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return an id like {appCode}${component}${xPath}
	 */
	public String composeId() {
		return PropertyLocation.composeId(_appCode,_component,_xPath);
	}
	public static String composeId(final AppCode appCode,final AppComponent component,final Path xPath) {
		return appCode.asString() + "$" + component + "$" + xPath;
	}
}
