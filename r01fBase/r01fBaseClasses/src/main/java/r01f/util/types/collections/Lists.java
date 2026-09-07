package r01f.util.types.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class Lists {
/////////////////////////////////////////////////////////////////////////////////////////
//	IS NULL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a list is null or empty
	 */
	public static boolean isNullOrEmpty(final List<?> list) {
		return list == null || list.isEmpty();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final E... elements) {
		if (elements == null || elements.length == 0) return new ArrayList<>();
		// beware huge arrays
		int theSize = elements.length;
		if (theSize > Integer.MAX_VALUE) {
		  theSize = Integer.MAX_VALUE;
		} else if (theSize < Integer.MIN_VALUE) {
		  theSize = Integer.MIN_VALUE;
		}		
		ArrayList<E> outList = new ArrayList<>(theSize);
		Collections.addAll(outList,elements);
		return outList;
	}
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final Iterable<? extends E> elements) {
	return elements != null ? StreamSupport.stream(elements.spliterator(),
												   false)	// not parallel
										   .collect(Collectors.toCollection(ArrayList::new))
							: new ArrayList<>();			   
	}
	/**
	 * Creates a mutable ArrayList instance containing the given elements.
	 */
	public static <E> List<E> newArrayList(final Iterator<? extends E> elements) {
		return elements != null ? StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements,0),
													   false)	// not parallel
											   .collect(Collectors.toCollection(ArrayList::new))
							   	: new ArrayList<>();		   
	}
	/**
	 * Creates a mutable, empty ArrayList instance.
	 */
	public static <E> List<E> newArrayList() {
		return new ArrayList<>();
	}
	/**
	 * Creates an ArrayList instance backed by an array of the exact size specified; equivalent to ArrayList.ArrayList(int).
	 */
	public static <E> List<E> newArrayListWithCapacity(final int size) {
		return new ArrayList<>(size);
	}
	/**
	 * Creates an empty LinkedList instance.
	 */
	public static <E> List<E> newLinkedList() {
		return new LinkedList<E>();
	}
	/**
	 * Creates a LinkedList instance containing the given elements.
	 */
	public static <E> List<E> newLinkedList(final Iterable<? extends E> elements) {
	    return elements != null
	    			? StreamSupport.stream(elements.spliterator(),false)
	    						   .collect(Collectors.toCollection(LinkedList::new))
	    			: new LinkedList<>();
	}
	/**
	 * Creates a LinkedList instance containing the given elements.
	 */
	public static <E> LinkedList<E> newLinkedList(final Iterator<? extends E> elements) {
		if (elements == null || !elements.hasNext()) return null;
		
		@SuppressWarnings("unchecked")
		LinkedList<E> list = (LinkedList<E>)newLinkedList();
		do {
			list.add(elements.next());
		} while (elements.hasNext());
		return list;
	}
	/**
	 * Returns consecutive sublists of a list, each of the same size (the final list may be smaller).
	 */
	public static <E> List<List<E>> partition(final List<E> list,final int size) {
		return com.google.common.collect.Lists.partition(list,size);
	}
	/**
	 * Returns a reversed view of the specified list.
	 */
	public static <E> List<E> reverse(final List<E> list) {
		return com.google.common.collect.Lists.reverse(list);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SIBLINGS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns all the element's siblings in the list (every other element in the list)
	 * @param theList
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblings(final List<E> theList,final E element) {
		if (theList.size() == 1) return null;		// there's only one child (this item), so this item do not have any siblings
		List<E> outSiblings = new ArrayList<>(theList.size()-1);
		for (E item : theList) {
			if (item == element) {
				continue;	// this item does not belong to the siblings collection
			}
			outSiblings.add(item);
		}
		return outSiblings;
	}
	/**
	 * Returns a list with all siblings elements AFTER the provided one
	 * If the element is the one and only element or the last element in the list null is returned
	 * @param allSiblings
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblingsAfter(final List<E> allSiblings,final E element) {
		if (allSiblings.size() == 1) return null;	// there's only item (this item), so this item do not have any siblings
		
		int thisItemIndex = allSiblings.indexOf(element);
		int numOfSiblingsAfter = allSiblings.size() - (thisItemIndex+1);
		if (numOfSiblingsAfter == 0) return null;		// this is the last child... no siblings after
		
		List<E> outSiblingsAfter = new ArrayList<>(numOfSiblingsAfter);
		for (int i=thisItemIndex+1; i < allSiblings.size(); i++) {
			outSiblingsAfter.add(allSiblings.get(i));
		}
		return outSiblingsAfter;
	}
	/**
	 * Returns the next element in the list AFTER the provided one
	 * If the element is the one and only element or the last element in the list null is returned
	 * @param allSiblings
	 * @param element
	 * @return
	 */
	public static <E> E getNextSibling(final List<E> allSiblings,final E element) {
		List<E> siblingsAfter = Lists.getSiblingsAfter(allSiblings,element);
		return siblingsAfter != null ? siblingsAfter.get(0) : null;
	}
	/**
	 * Returns a list with all the siblings elements BEFORE the provided one
	 * If the element is the one and only element or the first element in the list null is returned
	 * @param allSiblings
	 * @param element
	 * @return
	 */
	public static <E> List<E> getSiblingsBefore(final List<E> allSiblings,final E element) {
		if (allSiblings.size() == 1) return null;	// there's only one item (this item), so this item do not have any siblings
		
		int thisItemIndex = allSiblings.indexOf(element);
		int numOfSiblingsBefore = thisItemIndex;
		if (numOfSiblingsBefore == 0) return null;		// this is the first child... no siblings before
		
		List<E> outSiblingsAfter = new ArrayList<>(numOfSiblingsBefore);
		for (int i=0; i < thisItemIndex; i++) {
			outSiblingsAfter.add(allSiblings.get(i));
		}
		return outSiblingsAfter;
	}
	/**
	 * Returns the previous element in the list BEFORE the provided one
	 * If the element is the one and only element or the first element in the list null is returned
	 * @param allSiblings
	 * @param element
	 * @return
	 */
	public static <E> E getPrevSibling(final List<E> allSiblings,final E element) {
		List<E> siblingsBefore = Lists.getSiblingsBefore(allSiblings,element);
		return siblingsBefore != null ? siblingsBefore.get(siblingsBefore.size() - 1) : null;
	}
}
