package r01f.util.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class StringSplitter {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final Splitter _splitter;
	private final CharSequence _str;
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public static StringSplitterStringStep using(final Splitter splitter) {
		return new StringSplitterStringStep(splitter);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class StringSplitterStringStep {
		private final Splitter _splitter;
		
		public StringSplitter at(final String str) {
			return new StringSplitter(_splitter,str) { /* ignore */ };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Finds an element and returns it's index in the case it exists
	 * @param element
	 * @return 
	 */
	public int indexOf(final String element) {
		int outIndex = -1;
		Iterable<String> it = this.split();
		if (it != null) {
			int i = 0;
			for (String s : it) {
				if (s.equals(element)) {
					outIndex = i;
					break;
				}
				i++;
			}
		}
		return outIndex;
	}
	/**
	 * Splits
	 * @return ant iterator over the chunks
	 */
	public Iterable<String> split() {
		if (_str == null) return null;
		return _splitter.split(_str);
	}
	/**
	 * Returns the number of chunks
	 * @return
	 */
	public int count() {
		if (_str == null) return 0;
		return this.toArray().length;
	}
	/**
	 * The chunks array
	 * @return the chunk iterator as an array
	 */
	public String[] toArray() {
		List<String> out = this.toList();
		return out.toArray(new String[out.size()]);
	}
	/**
	 * The chunks as a {@link Collection}
	 * @return the iterator as a {@link Collection}
	 */
	public Collection<String> toCollection() {
		return this.toList();
	}
	/**
	 * The chunks as a {@link Collection}
	 * @return the iterator as a {@link Collection}
	 */
	public List<String> toList() {
		return this.stream()
				   .toList();
	}
	/**
	 * The chunks as a {@link Set}
	 * @return the iterator as a {@link Set}
	 */
	public Set<String> toSet() {
		return new LinkedHashSet<>(this.toCollection());
	}
	/**
	 * Return an stream
	 * @return
	 */
	public Stream<String> stream() {
		Iterable<String> iterable = this.split();
		return iterable != null ? StreamSupport.stream(iterable.spliterator(),
													   false)
								: Stream.empty();
	}
	/**
	 * Returns one of the element of the splitted string
	 * @param groupNum the num
	 * @return 
	 */
	public String group(final int groupNum) {
		String outGroup = null;
		Iterable<String> ib = this.split();
		if (ib != null && groupNum >= 0) {
			int i = 1;
			Iterator<String> it = ib.iterator();
			if (it.hasNext()) {
				do {
					outGroup = it.next();
					i++;
				} while (i <= groupNum && it.hasNext());
			}
		}
		return outGroup;
	}
}
