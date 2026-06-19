package r01f.util.types.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.patterns.CommandOn;
import r01f.patterns.ProviderFrom;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionWrappers.WrappedCollection;
import r01f.util.types.collections.CollectionWrappers.WrappedSortableCollection;
import r01f.util.types.collections.MapsWrappers.WrappedMap;


/**
 * Collections / Map / Sets utilities
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class CollectionUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR INTERFACES OF Maps or Collections
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface CollectionMutatorMethods<V> {
		public boolean add(V obj);
		public boolean addAll(Collection<? extends V> c); 
		public void clear();
		public boolean remove(Object o);
		public boolean removeAll(Collection<?> c);
		public boolean retainAll(Collection<?> c);
	}
	public static interface MapMutatorMethods<K,V> {
		public void clear();
		public V put(K key,V value);
		public void putAll(Map<? extends K,? extends V> m);
		public V remove(Object key); 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an {@link Iterable} from an {@link Iterator} instance
	 * @param iterator
	 * @return
	 */
	public static <T> Iterable<T> iterableFrom(final Iterator<T> iterator) {
		return new Iterable<>() {
						@Override
						public Iterator<T> iterator() {
							return iterator;
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API for Lists
/////////////////////////////////////////////////////////////////////////////////////////
	public static <T> WrappedCollection<T> of(final WrappedCollection<T> aCol) {
		return aCol;
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection
	 * @param aCol the collection to wrap
	 */
	public static <T> WrappedCollection<T> of(final Collection<T> aCol) {
		if (aCol instanceof WrappedCollection) {
			return CollectionUtils.of((WrappedCollection<T>)aCol);
		}
		return new WrappedCollection<>(aCol);
	}
	/**
	 * Wraps an iterator 
	 * @param colIt
	 * @return
	 */
	public static <T> WrappedCollection<T> of(final Iterator<T> colIt) {		
		return new WrappedCollection<>(colIt);
	}
	/**
	 * Wraps an iterable 
	 * @param colIt
	 * @return
	 */
	public static <T> WrappedCollection<T> of(final Iterable<T> colIt) {		
		return new WrappedCollection<>(colIt);
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection 
	 * @param aCol the collection to wrap
	 */
	public static <T extends Comparable<? super T>> WrappedSortableCollection<T> ofSortable(final Collection<T> aCol) {
		return new WrappedSortableCollection<>(aCol);
	}	
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection
	 * @param objCol the collection to wrap
	 */	
	@SuppressWarnings("unchecked")
	public static <T> WrappedCollection<T> of(final T... objCol) {
		List<T> theList = Lists.newArrayList(objCol);
		return new WrappedCollection<>(theList);
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collections
	 * @param <T>
	 * @param objArrays
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> WrappedCollection<T> of(final T[]... objArrays) {
		// count elements
		int elCount = 0;
		for (T[] objArray : objArrays) {
			if (objArray != null) {
				elCount = elCount + objArray.length;
			}
		}
		// create the collection
		Collection<T> theList = elCount > 0 ? new ArrayList<>(elCount)
											: Lists.newArrayList();
		// add all elements
		for (T[] objArray : objArrays) {
			if (CollectionUtils.hasData(objArray)) {
				Stream.of(objArray)
															 .forEach(obj -> theList.add(obj));
			}
		}
		// return
		return new WrappedCollection<>(theList);
	}
	/**
	 * Wraps a collection providing a fluent-api with some functions over the collection 
	 * @param objCol the collection to wrap
	 */	
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> WrappedSortableCollection<T> ofSortable(final T... objCol) {
		List<T> theList = Lists.newArrayList(objCol);
		return new WrappedSortableCollection<>(theList);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API for Maps
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps a Map providing a fluent-api with some functions over the Map
	 * @param aMap the Map to wrap
	 */		
	public static <K,V> WrappedMap<K,V> of(final Map<K,V> aMap) {
		return new WrappedMap<>(aMap);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API for Sets
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Wraps some Sets providing a fluent-api with some functions over the Sets
	 * @param sets the sets 
	 */
	@SuppressWarnings("unchecked")
	public static <T> SetOperations<T> setOperationsOf(final Set<T>... sets) {
		return new SetOperations<>(sets);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ALL ITEMS NULL
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if all array's items are null
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> boolean allItemsNull(final T[] array) {
		return CollectionUtils.hasData(array)
					? Stream.of(array)
							.filter(Objects::nonNull)	// not null elements
							.findFirst().orElse(null) == null
					: false;	// no elements
	}
	/**
	 * Checks if all collection's items are null
	 * @param theCol
	 * @return
	 */
	public static boolean allItemsNull(final Collection<?> theCol) {
		return CollectionUtils.hasData(theCol)
					? Stream.of(theCol)
							.filter(Objects::nonNull)	// not null elements
							.findFirst().orElse(null) == null
					: false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS TO CHECK COLLECTIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if an array is null or empty
	 * @param array the array
	 * @return true if the array is null or empty
	 */
	public static <T> boolean isNullOrEmpty(final T[] array) {
		return array == null || array.length == 0;
	}
	/**
	 * Checks if a Map is null or empty
	 * @param theMap the Map
	 * @return true if the Map is null or empty
	 */
	public static boolean isNullOrEmpty(final Map<?,?> theMap) {
		return (theMap == null || theMap.size() == 0);
	}
//	/**
//	 * Checks if the {@link Multimap} is null or empty
//	 * @param theMultimap the Map
//	 * @return true if the {@link Multimap} is null or empty
//	 */
//	public static boolean isNullOrEmpty(final Multimap<?,?> theMultimap) {
//		return (theMultimap == null || theMultimap.size() == 0);
//	}
	/**
	 * Checks if the {@link List} is null or empty
	 * @param theMap the list
	 * @return true if the {@link List} is null or empty
	 */	
	public static boolean isNullOrEmpty(final Collection<?> theCol) {
		return (theCol == null || theCol.size() == 0);
	}
	/**
	 * Checks if the {@link Iterable} is null or empty
	 * @param theIt the list
	 * @return true if the {@link Iterable} is null or empty
	 */	
	public static boolean isNullOrEmpty(final Iterable<?> theIt) {
		return (theIt == null || CollectionUtils.sizeOf(theIt) == 0);
	}
	/**
	 * Checks if the {@link Iterator} is null or empty
	 * @param theIt the list
	 * @return true if the {@link Iterator} is null or empty
	 */	
	public static boolean isNullOrEmpty(final Iterator<?> theIt) {
		return (theIt == null || CollectionUtils.sizeOf(theIt) == 0);
	}
	/**
	 * Checks if the {@link Iterable} has entries
	 * @param theIt the {@link Map}
	 * @return true if the {@link Map} has at least one entry
	 */
	public static boolean hasData(final Iterable<?> theIt) {
		return theIt != null ? CollectionUtils.sizeOf(theIt) > 0 : false;
	}
	/**
	 * Checks if the {@link Iterable} has entries
	 * @param theIt the {@link Map}
	 * @return true if the {@link Map} has at least one entry
	 */
	public static boolean hasData(final Iterator<?> theIt) {
		return theIt != null ? CollectionUtils.sizeOf(theIt) > 0 : false;
	}
	/**
	 * Checks if the {@link Map} has entries
	 * @param theMap the {@link Map}
	 * @return true if the {@link Map} has at least one entry
	 */
	public static boolean hasData(final Map<?,?> theMap) {
		return !CollectionUtils.isNullOrEmpty(theMap);
	}
//	/**
//	 * Checks if the {@link Multimap} has entries
//	 * @param theMap the {@link Multimap}
//	 * @return true if the {@link Multimap} has at least one entry
//	 */
//	public static boolean hasData(final Multimap<?,?> theMultimap) {
//		return !CollectionUtils.isNullOrEmpty(theMultimap);
//	}
	/**
	 * Checks if the array has elements
	 * @param array the array
	 * @return true if the array has at least one element
	 */
	public static <T> boolean hasData(final T[] array) {
		return !CollectionUtils.isNullOrEmpty(array);
	}
	/**
	 * Checks if the {@link Collection} has elements
	 * @param theCol the {@link Collection}
	 * @return true if the {@link Collection} has at least one element
	 */
	public static boolean hasData(final Collection<?> theCol) {
		return !CollectionUtils.isNullOrEmpty(theCol);
	}
	public static boolean hasData(final int[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final long[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final double[] array) {
		return array != null && array.length > 0;
	}
	public static boolean hasData(final float[] array) {
		return array != null && array.length > 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SIZE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the size of any Iterable object
	 * @param iterable
	 * @return
	 */
	public static int sizeOf(final Iterable<?> iterable) {
		int outSize = 0;
		if (iterable instanceof Collection) {
			outSize = ((Collection<?>)iterable).size();
		} else {
			for (Object ignored : iterable) {
				outSize++;
			}
		}
		return outSize;
	}
	/**
	 * Returns the size of any Iterable object
	 * @param iterable
	 * @return
	 */
	public static int sizeOf(final Iterator<?> iterator) {
		int outSize = 0;
		while (iterator.hasNext()) {
			iterator.next();
			outSize++;
		}
		return outSize;
	}
	/**
	 * Safely returns the size of a {@link Iterator}
	 * @param col
	 * @return
	 */
	public static int safeSize(final Iterator<?> col) {
		return col != null ? CollectionUtils.sizeOf(col) : 0;
	}
	/**
	 * Safely returns the size of a {@link Iterable}
	 * @param col
	 * @return
	 */
	public static int safeSize(final Iterable<?> col) {
		return col != null ? CollectionUtils.sizeOf(col) : 0;
	}
	/**
	 * Safely returns the size of a {@link Map}
	 * @param map
	 * @return
	 */
	public static int safeSize(final Map<?,?> map) {
		return map != null ? map.size() : 0;
	}
	/**
	 * Safely returns the size of an array
	 * @param arr
	 * @return
	 */
	public static int safeSize(final Object[] arr) {
		return CollectionUtils.hasData(arr) ? arr.length : 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first element of a collection
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T firstOf(final Collection<?> theCol) {
		if (theCol == null) {
			throw new IllegalArgumentException("The collection MUST not be null");
		}
		return (T)theCol.stream()
					  	.findFirst().orElse(null);		
	}
	/**
	 * Returns the last element of a collection
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lastOf(final Collection<?> theCol) {
		if (theCol == null) {
			throw new IllegalArgumentException("The collection MUST not be null");
		}
		return (T)theCol.stream()
						.skip(theCol.size() - 1)
					  	.findFirst().orElse(null);		
	}
	/**
	 * Picks the one and only element form the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is thrown
	 * @param theCol
	 * @return
	 */
	public static <T> T pickOneAndOnlyElement(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			throw new IllegalStateException("The collection is null or empty");
		}
		if (theCol.size() > 1) {
			throw new IllegalStateException("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		}
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element form the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is thrown
	 * if the collection is empty it returns null
	 * @param theCol
	 * @return 
	 */
	public static <T> T pickOneAndOnlyElementOrNull(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			return null;
		}
		if (theCol.size() > 1) {
			throw new IllegalStateException("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		}
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is throw with the message provided
	 * @param theCol
	 * @param errMsg
	 * @param vars
	 * @return
	 */
	public static <T> T pickOneAndOnlyElement(final Collection<?> theCol,
											  final String errMsg,final Object... vars) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			throw new IllegalStateException("The collection is null or empty");
		}
		if (theCol.size() > 1) {
			throw new IllegalStateException(Strings.customized(errMsg,vars));
		}
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a {@link IllegalStateException} is throw with the message provided
	 * if the collection is empty it returns null
	 * @param theCol
	 * @param errMsg
	 * @param vars
	 * @return
	 */
	public static <T> T pickOneAndOnlyElementOrNull(final Collection<?> theCol,
											  		final String errMsg,final Object... vars) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			return null;
		}
		return CollectionUtils.<T>pickOneAndOnlyElement(theCol,errMsg,vars);
	}
	/**
	 * Picks the one and only element from the collection
	 * If the collection has more than one element, a custom exception is thrown
	 * @param <T>
	 * @param theCol
	 * @param throableProvider
	 * @return
	 * @throws Throwable 
	 */
	public static <T,E extends Throwable> T pickOneAndOnlyElementOrThrow(final Collection<?> theCol,
													 					 final ProviderFrom<String,E> throableProvider) throws E {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			throw throableProvider.from("The collection is null or empty");
		}
		if (theCol.size() > 1) {
			throw throableProvider.from("The one and only element of a collection has been requested but the collection has MORE THAN ONE element");
		}
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/** 
	 * Picks one element from the collection 
	 * @param theCol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T pickOneElement(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			throw new IllegalStateException("The collection is null or empty");
		}
		T outEntry = null;
		for (Object entry : theCol) {
			outEntry = (T)entry;
			break;
		}
		return outEntry;
	}
	/**
	 * Picks one element from the collection 
	 * @param theCol
	 * @return
	 */
	public static <T> T pickOneElementOrNull(final Collection<?> theCol) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			return null;
		}
		return CollectionUtils.<T>pickOneElement(theCol);
	}
	/**
	 * Returns the element at the given position 
	 * @return
	 */
	public static <T> T pickElementAt(final Collection<T> theCol,
									  final int index) {
		if (CollectionUtils.isNullOrEmpty(theCol)) {
			throw new IllegalStateException("The collection is empty");
		}
		if (theCol.size() + 1 < index) {
			throw new IllegalStateException("The collection just has " + theCol.size() + " items; cannot get item at " + index);
		}
		return _pickElementAt(theCol,index);
	}
	/**
	 * Returns the element at the given position or null 
	 * @return
	 */
	public static <T> T pickElementAtOrNull(final Collection<T> theCol,
									  		final int index) {
		if (CollectionUtils.isNullOrEmpty(theCol)
		 || theCol.size() + 1 < index) {
			return null;
		}
		return _pickElementAt(theCol,index);
	}
	private static <T> T _pickElementAt(final Collection<T> theCol,final int index) {
		int i=0;
		T outEl = null;
		for (T el : theCol) {
			outEl = el;
			if (i == index) {
				break;
			}
			i++;
		}
		return outEl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PARA CHECKEAR EL TIPO DE COLECCIONES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if a type is an array
	 * @param type the type
	 * @return true if the type is an array, false if not
	 */
	public static boolean isArray(final Class<?> type) {
		return type.isArray();
	}
	/**
	 * Checks if an array is composed of elements of a primitive type
	 * @param type the array type
	 * @return true if the elements type is a primitive type
	 */
	public static boolean isObjectsArray(final Class<?> type) {
		return type.isArray() && !type.getComponentType().isPrimitive();
	}	
	/**
	 * Checks if a type implements the {@link Map} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	public static boolean isMap(final Class<?> type) {
		return Map.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	}
	/**
	 * Checks if a type implements the {@link Collection} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	public static boolean isCollection(final Class<?> type) {
		return Collection.class.isAssignableFrom(type);	// ReflectionUtils.isImplementing
	} 
	/**
	 * Checks if a type implements the {@link List} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	public static boolean isList(final Class<?> type) {
		return List.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	}
	/**
	 * Checks if a type implements the {@link Set} interface
	 * @param type the type
	 * @return true if the interface is implemented
	 */
	public static boolean isSet(final Class<?> type) {
		return Set.class.isAssignableFrom(type);		// ReflectionUtils.isImplementing
	} 
	/**
	 * Checks if a type implements the {@link Map} or the {@link Collection} interfaces
	 * @param type the type
	 * @return true if one of the interface is implemented
	 */
	public static boolean isMapOrCollection(final Class<?> type) {
		return CollectionUtils.isMap(type) || CollectionUtils.isCollection(type);
	}
	/**
	 * Returns the collection type as it's inteface, ie, if the type is extending a Map, it returns {@link Map}
	 * or if the type is extending a Collection, it returns {@link Collection}
	 * @param obj 
	 * @return List.class if the object is a list and Map.class if the object is a Map; null if it's not neither a List, nor a Map
	 */
	public static Class<?> getCollectionType(final Class<?> type) {
		Class<?> colType = null;
		if (CollectionUtils.isMap(type)) {
			colType = Map.class;
		} else if (CollectionUtils.isSet(type)) {
			colType = Set.class;
		} else if (CollectionUtils.isList(type)) {
			colType = List.class;
		} else if (CollectionUtils.isCollection(type)) {
			colType = List.class;
		}
		return colType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONVERSION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts a Map of Strings into a Properties type
	 * @param mapOfStrings
	 * @return
	 */
	public static Properties toProperties(final Map<String,String> mapOfStrings) {
		Properties outProps = null;
		if (CollectionUtils.hasData(mapOfStrings)) {
			outProps = new Properties();
			for (Map.Entry<String,String> me : mapOfStrings.entrySet()) {
				outProps.setProperty(me.getKey(),me.getValue());
			}
		}
		return outProps;
	}
	/**
	 * Converts a Properties into a Map<String,String> type
	 * @param props properties
	 * @return
	 */
	public static Map<String,String> toMap(final Properties props) {
		Map<String,String> outMap = null;
		if (CollectionUtils.hasData(props)) {
			outMap = new HashMap<>(props.size());
			Set<Map.Entry<Object,Object>> entries = props.entrySet();
			for (Map.Entry<Object,Object> entry : entries) {
				outMap.put(entry.getKey().toString(),
						   entry.getValue().toString());
			}
		}
		return outMap;
	}
	/**
	 * Converts a Collection to an array
	 * @param col the {@link Collection}
	 * @return the array
	 */
	@SuppressWarnings({"unchecked","serial"})
	public static <T> T[] toArray(final Collection<T> col) {
		TypeToken<T> tr = new TypeToken<>() {/* nothing */};
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(tr.getRawType(),col.size()))
													: null;
		return outArray;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Collection<?> col,final Class<?> type) {
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(type,col.size()))
													: null;
		return outArray;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Collection<T> col,final TypeToken<T> typeRef) {
		T[] outArray = CollectionUtils.hasData(col) ? col.toArray((T[])Array.newInstance(typeRef.getRawType(),col.size()))
													: null;
		return outArray;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the {@link Collection} elements as a comma-separated {@link String}
	 * @param col
	 * @return
	 */
	public static String toStringCommaSeparated(final Object... col) {
		return CollectionUtils.toStringCommaSeparated(Lists.newArrayList(col));
	}
	/**
	 * Returns the {@link Collection} elements as a comma-separated {@link String}
	 * @param col
	 * @return
	 */
	public static String toStringCommaSeparated(final Collection<?> col) {
		return CollectionUtils.toStringSeparatedWith(col,',');
	}
	/**
	 * Returns the {@link Collection} elements as a char-separated {@link String}
	 * @param col
	 * @param ch
	 * @return 
	 */
	public static String toStringSeparatedWith(final Object[] col,final char ch) {
		return CollectionUtils.toStringSeparatedWith(Lists.newArrayList(col),ch);
	}
	/**
	 * Returns the {@link Collection} elements as a char-separated {@link String}
	 * @param col
	 * @param ch
	 * @return 
	 */
	public static String toStringSeparatedWith(final Collection<?> col,final char ch) {
		return CollectionUtils.toStringSeparatedWith(col,Character.toString(ch));
	}
	/**
	 * Returns the {@link Collection} elements as a {@link String}-separated {@link String}
	 * @param col
	 * @param joiner
	 * @return
	 */
	public static String toStringSeparatedWith(final Collection<?> col,final String joiner) {
		if (CollectionUtils.isNullOrEmpty(col)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it = col.iterator(); it.hasNext(); ) {
			Object el = it.next();
			if (el == null) {
				sb.append("null");
			} else if (el instanceof CanBeRepresentedAsString) {
				sb.append(((CanBeRepresentedAsString)el).asString());
			} else {
				sb.append(el.toString());
			}
			if (it.hasNext()) {
				sb.append(joiner);
			}
		}
		return sb.toString();			
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Cast GUAVA's function to be used to cast all elements in a Collection:
	 * <pre class='brush:java'>
	 * 		List<TypeParent> originalList = Lists.newArrayList();
	 * 		List<TypeChild> theList = Collections2.transform(originalList, 
	 *												  		 new CastFunction<TypeParent,TypeChild>());
	 * </pre>
	 */
	private static class CastFunction<F,T>
	  implements Function<F,T> {
		@Override @SuppressWarnings("unchecked")
		public T apply(final F from) {
			return (T)from;
		}
	}
	/**
	 * Unsafe cast from Collection<F> to Collection<T>
	 * @param col
	 * @return
	 */
	public static <F,T> Collection<T> cast(final Collection<F> col) {
		return col.stream()
				  .map(new CastFunction<F,T>())
				  .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Executes a command on every object of a collection
	 * @param col
	 * @param cmd
	 */
	public static <T> void executeOn(final Collection<T> col,
								 	 final CommandOn<T> cmd) {
		if (cmd != null && CollectionUtils.hasData(col)) {
			for (T obj : col) {
				cmd.executeOn(obj);
			}
		}
	}
}
