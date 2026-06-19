package r01f.types.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="tags")
@Accessors(prefix="_")
public class StringTagList 
	 extends TagList<String> {

	private static final long serialVersionUID = 4219370572494826625L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public StringTagList() {
		// no-args constructor
	}
	/**
	 * @param tags
	 */
	public StringTagList(final Collection<String> tags) {
		super(tags);
	}
	/**
	 * @param tags
	 */
	public StringTagList(final String... tags) {
		super(tags);
	}
	/**
	 * @param size
	 */
	public StringTagList(final int size) {
		// marshaller needs this method
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void addAll(final String... tags) {
		if (CollectionUtils.hasData(tags)) this.addAll(new ArrayList<>(Arrays.asList(tags)));
	}
}
