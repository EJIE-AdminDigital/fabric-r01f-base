package r01f.properties;

import java.util.Iterator;
import java.util.function.Function;

import org.w3c.dom.Document;

import r01f.oids.CommonOIDs.IsAppComponent;
import r01f.types.Path;

public interface PropertiesForAppComponentAccessor<D,N> {
/////////////////////////////////////////////////////////////////////////////////////////
//	ENVIRONMENT
/////////////////////////////////////////////////////////////////////////////////////////
	public IsAppComponent getAppComponent();
/////////////////////////////////////////////////////////////////////////////////////////
//	 XML
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link Document} that backs the component's properties
	 * @return
	 */
	public D getDocument();
	/**
	 * @return the  node that matches the XPath expression and contains the properties
	 */
	public N node(final Path propPath);
	/**
	 * @return the list of xml nodes that match the XPath expression
	 */
	public Iterator<N> nodeListIterator(final Path propPath);
/////////////////////////////////////////////////////////////////////////////////////////
//	ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a property
	 * @param propPath property xPath
	 * @param nodeTransform xml node to property value transform
	 * @param defaultValue the property default value
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
	public <T> T getPropertyFromNode(final Path propPath,
							 		 final Function<N,T> nodeTransform,
							 		 final T defaultValue);
	/**
	 * Returns a property
	 * @param propPath property xPath
	 * @param nodeListTransform node list to property value transform
	 * @param defaultValue the property default value
	 * @return The property or <code>null</code> if the property does NOT exists
	 */
	public <T> T getPropertyFromNodeList(final Path propPath,
							 		 	 final Function<Iterator<N>,T> nodeListTransform,
							 		 	 final T defaultValue);
}
