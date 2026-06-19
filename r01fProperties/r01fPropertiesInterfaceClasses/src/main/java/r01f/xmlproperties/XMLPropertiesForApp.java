package r01f.xmlproperties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import r01f.properties.PropertiesForApp;

/**
 * Manages properties for an app code<br/>
 * Provides access to each app's component's properties
 */
public interface XMLPropertiesForApp
		 extends PropertiesForApp<Document,Node,
		 			              XMLPropertiesForAppComponentAccessor,
		 						  XMLPropertiesForAppComponent> {
	// just extends
}