package r01f.xmlproperties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import r01f.properties.PropertiesForAppComponentAccessor;

public interface XMLPropertiesForAppComponentAccessor
		 extends PropertiesForAppComponentAccessor<Document,Node> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the xml {@link Document} that backs the component's properties
	 * @return
	 */
	public default Document getXmlDocument() {
		return this.getDocument();
	}
}
