package r01f.xmlproperties;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import r01f.properties.PropertyWrapper;

/**
 * Wraps an xml property providing means to get typed properties
 */
public interface XMLPropertyWrapper
		 extends PropertyWrapper<Node> {
/////////////////////////////////////////////////////////////////////////////////////////
//	RAW DOM'S NODE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the xml node list containing the property
	 * BEWARE!! this method DOES NOT use the cache
	 * @return the xml node list containing the property
	 */
	public NodeList nodeList();

	@Override
	public default Iterator<Node> nodeListIterator() {
		return XMLPropertyWrapper.nodeListIterator(this.nodeList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Iterator<Node> nodeListIterator(final NodeList nodeList) {
		return new Iterator<>() {
						private int _currPos = 0;

						@Override
						public boolean hasNext() {
							return nodeList != null ? _currPos < nodeList.getLength()
													: false;
						}
						@Override
						public Node next() {
							Node outNode = nodeList != null ? nodeList.item(_currPos)
															: null;
							_currPos = _currPos + 1;
							return outNode;
						}
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
			   };
	}
	public static NodeList EMPTY_NODE_LIST = new NodeList() {
													@Override
													public Node item(final int index) {
														return null;
													}
													@Override
													public int getLength() {
														return 0;
													}
												};
}
