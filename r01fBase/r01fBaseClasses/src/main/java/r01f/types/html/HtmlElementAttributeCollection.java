package r01f.types.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

@MarshallType(as="htmlAttributeCollection")
public class HtmlElementAttributeCollection
	 extends LinkedHashSet<HtmlElementAttribute>
  implements CanBeRepresentedAsString {

	private static final long serialVersionUID = 1365057717313591673L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlElementAttributeCollection() {
		// default no-args constructor
	}
	// required by the marshaller
	public HtmlElementAttributeCollection(final int length) {
		super(length);
	}
	public HtmlElementAttributeCollection(final HtmlElementAttribute... items) {
		if (CollectionUtils.hasData(items)) {
			this.addAll(Arrays.asList(items));
		}
	}
	public HtmlElementAttributeCollection(final Collection<HtmlElementAttribute> items) {
		if (CollectionUtils.hasData(items)) {
			this.addAll(items);
		}
	}
	public static HtmlElementAttributeCollection create() {
		return new HtmlElementAttributeCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlElementAttributeCollection addAttributes(final HtmlElementAttribute... attrs) {
		if (CollectionUtils.hasData(attrs)) return this.addAttributes(Arrays.asList(attrs));
		return this;
	}
	public HtmlElementAttributeCollection addAttributes(final Collection<HtmlElementAttribute> attrs) {
		if (CollectionUtils.hasData(attrs)) {
			this.addAll(attrs);
		}
		return this;
	}
	public boolean removeAttribute(final HtmlElementAttribute attr) {
		if (this.size() == 0) return false;

		Collection<HtmlElementAttribute> itemsToBeRemoved = new ArrayList<>();
		// find the items to be removed
		for (HtmlElementAttribute item : this) {
			if (attr.equals(item)) {
				itemsToBeRemoved.add(item);
			}
		}
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed;
	}
	public boolean removeAttributeWithId(final String id) {
		if (this.size() == 0) return false;

		Collection<HtmlElementAttribute> itemsToBeRemoved = Lists.newArrayList();
		// find the items to be removed
		for (HtmlElementAttribute item : this) {
			if (item.isIdEqualTo(id)) {
				itemsToBeRemoved.add(item);
			}
		}
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeConvertedAsString
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		if (CollectionUtils.isNullOrEmpty(this)) return "";
		StringBuilder str = new StringBuilder();
		for (Iterator<HtmlElementAttribute> attrIt = this.iterator(); attrIt.hasNext(); ) {
			String attrAsString = attrIt.next().asString();
			if (Strings.isNOTNullOrEmpty(attrAsString)) {
				str.append(attrAsString);
			}
			if (attrIt.hasNext() && Strings.isNOTNullOrEmpty(attrAsString)) {
				str.append(" ");
			}
		}
		return str.toString();
	}
}
