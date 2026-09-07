package r01f.xmlproperties;

import java.util.Iterator;
import java.util.function.Function;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.IsAppComponent;
import r01f.types.Path;

@Slf4j
@Accessors(prefix="_")
     class XMLPropertiesForAppComponentAccessorImpl
implements XMLPropertiesForAppComponentAccessor {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final IsAppComponent _appComponent;
	@Getter private final Document _document;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	XMLPropertiesForAppComponentAccessorImpl(final IsAppComponent component,
											 final Document xmlDocument) {
		_appComponent = component;
		_document = xmlDocument;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	XML
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Node node(final Path xPath) {
		return (Node)_getPropertyNode(xPath,XPathConstants.NODE);
	}
	@Override
	public Iterator<Node> nodeListIterator(final Path xPath) {
		NodeList xmlNodeList = this.nodeList(xPath);
		return xmlNodeList != null ? XMLPropertyWrapper.nodeListIterator(xmlNodeList)
								   : XMLPropertyWrapper.nodeListIterator(XMLPropertyWrapper.EMPTY_NODE_LIST);
	}
	public NodeList nodeList(final Path xPath) {
		String xPathStr = xPath.asString();
		String effXPath = !xPathStr.endsWith("/child::*") ? xPathStr.concat("/child::*")
														  : xPathStr;
		return (NodeList)_getPropertyNode(Path.from(effXPath),XPathConstants.NODESET);
	}
	/**
	 * Returns the XML's DOM's {@link Node} obtained applying the given xpath expression to the app/component properties XML.
	 * @param component
	 * @param propXPath
	 * @param returnType the {@link XPath} returned type (boolean, number, string, node o nodeSet).
	 * @return The DOM {@link Node} or null if the node is NOT found
	 */
	private Object _getPropertyNode(final Path propXPath,
						   			final QName returnType) {
		String thePropXPath = null;
		try {
			Object outObj = null;
			thePropXPath = propXPath.asString().trim();
			if (thePropXPath.startsWith("/")) thePropXPath = thePropXPath.substring(1);

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			XPathExpression xPathExpr = xPath.compile(thePropXPath);
			if (returnType == XPathConstants.NODE) {
				outObj = xPathExpr.evaluate(_document,
											XPathConstants.NODE);
			} else if (returnType == XPathConstants.NODESET) {
				outObj = xPathExpr.evaluate(_document,
											XPathConstants.NODESET);
			} else if (returnType == XPathConstants.STRING) {
				outObj = xPathExpr.evaluate(_document,
											XPathConstants.STRING);
			} else if (returnType == XPathConstants.NUMBER) {
				outObj = xPathExpr.evaluate(_document,
											XPathConstants.NUMBER);
			} else if (returnType == XPathConstants.BOOLEAN) {
				outObj = xPathExpr.evaluate(_document,
											XPathConstants.BOOLEAN);
			}
			return outObj;
		} catch (XPathExpressionException xPathEx) {
			log.warn("Error retrieving property at {} for {}/{}",
					 thePropXPath,_appComponent);
			xPathEx.printStackTrace(System.out);
		}
		return null;	// the property could NOT be loaded
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <T> T getPropertyFromNode(final Path propXPath,
							 		 final Function<Node,T> nodeTransform,
							 		 final T defaultValue) {
		Node xmlNode = this.node(propXPath);
		return xmlNode != null ? nodeTransform != null ? nodeTransform.apply(xmlNode)
													   : null
							   : null;
	}
	@Override
	public <T> T getPropertyFromNodeList(final Path propXPath,
							 		 	 final Function<Iterator<Node>,T> nodeListTransform,
							 		 	 final T defaultValue) {
		NodeList xmlNodeList = this.nodeList(propXPath);
		Iterator<Node> nodeIt = XMLPropertyWrapper.nodeListIterator(xmlNodeList);
		return xmlNodeList != null ? nodeListTransform != null ? nodeListTransform.apply(nodeIt)
													   		   : null
								   : null;
	}
}
