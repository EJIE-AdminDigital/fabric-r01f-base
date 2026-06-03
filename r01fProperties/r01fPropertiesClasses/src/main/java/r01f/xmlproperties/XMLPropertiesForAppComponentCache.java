package r01f.xmlproperties;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.properties.PropertiesForAppComponentCache;
import r01f.types.Path;

/**
 * A cache for an app-component's properties
 * This object uses a {@link XMLPropertiesForAppComponentAccessor} behind the scenes
 * that handles access to the underlying XML Document when the property is not cached
 */
@Slf4j
@Accessors(prefix="_")
  class XMLPropertiesForAppComponentCache
extends PropertiesForAppComponentCache<Node,XMLPropertiesForAppComponentAccessor> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForAppComponentCache(final XMLPropertiesForAppComponentAccessor props,
											 // cache
											 final boolean useCache) {
		super(props,
			  useCache);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GETTER
/////////////////////////////////////////////////////////////////////////////////////////
	public XMLPropertiesForAppComponentAccessor getXMLDocumentAccessor() {
		return this.getPropertiesAccessor();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	XML
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the xml node list that contains a property
	 * BEWARE this method DOES NOT use the cache!!
	 * @param path
	 * @return
	 */
	public NodeList nodeList(final Path path) {
		return new NodeList() {

						private final List<Node> _nodeList = StreamSupport.stream(Spliterators.spliteratorUnknownSize(_props.nodeListIterator(path),Spliterator.ORDERED),
																				  false)
    																	  .toList();
						@Override
						public Node item(int index) {
							return _nodeList.get(index);
						}
						@Override
						public int getLength() {
							return _nodeList.size();
						}
			   };
	}
}
