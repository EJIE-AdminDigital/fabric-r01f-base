package r01f.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Splitter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.file.FileID;
import r01f.file.FileNameAndExtension;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.oids.OID;
import r01f.util.OSType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * path abstraction, simply using a String encapsulation
 */
@Accessors(prefix="_")
public abstract class PathBase<SELF_TYPE extends PathBase<SELF_TYPE>>
    	   implements IsPath,
    	   			  FileID,
    		   		  Iterable<String> {

	private static final long serialVersionUID = -2932591433085305985L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	@Getter protected final List<String> _pathElements;	// inmutable list
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PathBase(final Collection<String> pathEls) {
		if (pathEls == null) throw new IllegalArgumentException("The path elements collection cannot be null or empty");
		Collection<String> normalized = PathBase.normalize(pathEls);
		_pathElements = List.copyOf(normalized);	// inmutable!!
	}
	public PathBase(final String newPath) {
		this(Lists.newArrayList(newPath));
	}
	public PathBase(final Object obj) {
		this(PathBase.pathElementsFrom(obj));
	}
	public <P extends IsPath> PathBase(final P otherPath) {
		this(otherPath.getPathElements());
	}
	public PathBase(final String... elements) {
		this(Lists.newArrayList(elements));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static <P extends IsPath> P _createPathInstanceFromElements(final PathFactory<P> pathFactory,
																		final Collection<String> pathEls) {
		return pathFactory.createPathFrom(pathEls);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE THESE METHODS (to avoid having to cast like: apath.<PathType>joinedWith(...)
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P withoutLastPathElement() {
		return (P)PathBase.withoutLastPathElement(this.getPathFactory(),
											      this);
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P joinedWith(final Object... elements) {
		return (P)PathBase.join(this.getPathFactory(),
								this,elements);
	}
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> P prependedWith(final Object... elements) {
		return (P)PathBase.prepend(this.getPathFactory(),
								   this,elements);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TO STRING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		return this.asString();
	}
	@Override
	public String asString() {
		return PathBase.asString(this);
	}
	@Override
	public String asRelativeString() {
		return PathBase.asRelativeString(this);
	}
	@Override
	public String asAbsoluteString() {
		return PathBase.asAbsoluteString(this);
	}
	@Override
	public <P extends IsPath> String asAbsoluteStringFrom(final P parentPath) {
		return PathBase.asAbsoluteStringFrom(parentPath,
					   	   		  		     this);
	}
	@Override
	public <P extends IsPath> String asRelativeStringFrom(final P parentPath) {
		return PathBase.asRelativeStringFrom(parentPath,
					   	   		  		  	 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static String toString(final IsPath path) {
		return PathBase.asString(path);
	}
	public static String asString(final IsPath path) {
		return _asString(path.getPathElements());
	}
	public static String asRelativeString(final IsPath path) {
		return _asRelativeString(path.getPathElements());
	}
	public static String asAbsoluteString(final IsPath path) {
		return _asAbsoluteString(path.getPathElements());
	}
	public static String asAbsoluteStringFrom(final IsPath parentPath,
									  		  final IsPath path) {
		if (parentPath == null
		 || !parentPath.hasPathElements()) return path.asAbsoluteString();

		Collection<String> pathElements = new ArrayList<>(parentPath.getPathElements().size() + path.getPathElements().size());
		pathElements.addAll(parentPath.getPathElements());
		pathElements.addAll(path.getPathElements());
		return _asAbsoluteString(pathElements);
	}
	public static String asRelativeStringFrom(final IsPath parentPath,
									  		  final IsPath path) {
		if (parentPath == null
		 || !parentPath.hasPathElements()) return path.asRelativeString();

		Collection<String> pathElements = new ArrayList<>(parentPath.getPathElements().size() + path.getPathElements().size());
		pathElements.addAll(parentPath.getPathElements());
		pathElements.addAll(path.getPathElements());
		return _asRelativeString(pathElements);
	}
	public static <P extends IsPath> String asRelativeStringOrNull(final P path) {
		return path != null ? path.asRelativeString()
							: null;
	}
	public static <P extends IsPath> String asAbsoluteStringOrNull(final P path) {
		return path != null ? path.asAbsoluteString()
							: null;
	}
	public static String asRelativeStringOrNull(final String path) {

		return PathBase.asRelativeStringOrNull(Path.from(path));
	}
	public static String asAbsoluteStringOrNull(final String path) {
		return PathBase.asAbsoluteStringOrNull(Path.from(path));
	}
	/**
	 * Returns {@link String} with the path as an absolute path (not starting with /)
	 * @param pathElements
	 * @return
	 */
	protected static String _asRelativeString(final Collection<String> pathElements) {
		String outStr = _joinPathElements(pathElements);
		if (outStr == null) outStr = "";
		return outStr;
	}
	/**
	 * Returns {@link String} with the path as an absolute path (starting with /)
	 * @param pathElements
	 * @return
	 */
	protected static String _asAbsoluteString(final Collection<String> pathElements) {
		String outStr = _joinPathElements(pathElements);
		if (outStr == null) return "";
		if (outStr.matches("([a-zA-Z]:|http://|https://).*")) return outStr;	// d: or http://
		return "/" + outStr;
	}
	/**
	 * Composes a {@link String} with the path
	 * @param pathElements
	 * @return
	 */
	protected static String _asString(final Collection<String> pathElements) {
		return _asRelativeString(pathElements);
	}
	protected static String _joinPathElements(final Collection<String> pathElements) {
		String outStr = null;
		if (pathElements != null && !pathElements.isEmpty()) {
			outStr = pathElements.stream()
								 .filter(Objects::nonNull)
								 .collect(Collectors.joining("/"));	// Joiner.on('/')
						   											//		 .skipNulls()
						   											//		 .join(pathElements);
			outStr = outStr.replaceFirst("/\\?","?");									// fix query strings as foo/bar/?queryStr
			if (outStr.endsWith("?")) outStr = outStr.substring(0,outStr.length()-1);	// fix empty query strings
			if (outStr.matches("^http[s]?:\\/[^\\/].*")) { //fix url with only one / like http:/www.euskadi.eus to be http://www.euskadi.eus
				outStr = outStr.replaceFirst("/", "//");
			}
			
		}
		return outStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <P extends PathBase<?>> boolean startsWith(final P other) {
		boolean outStarts = true;
		Collection<String> otherTokens = other.getPathElements();
		if (_pathElements.size() < otherTokens.size()) return false;

		int i=0;
		for (String otherToken : otherTokens) {
			String thisToken = _pathElements.get(i);
			if (!thisToken.equals(otherToken)) {
				outStarts = false;
				break;
			}
			i = i+1;
		}
		return outStarts;
	}
	@Override
	public <P extends PathBase<?>> boolean endsWith(final P other) {
		boolean outEnds = true;
		final List<String> otherTokens = other.getPathElements();
		if (_pathElements.size() < otherTokens.size()) return false;

		int i=_pathElements.size()-1;
		for (String otherToken : Lists.reverse(otherTokens)) {
			String thisToken = _pathElements.get(i);
			if (!thisToken.equals(otherToken)) {
				outEnds = false;
				break;
			}
			i = i-1;
		}
		return outEnds;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	Path elements 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasPathElements() {
		return _pathElements != null && _pathElements.size() > 0;
	}
	@Override
	public Iterator<String> getPathElementsIterator() {
		return this.hasPathElements() ? _pathElements.iterator()
									  : null;
	}
	@Override
	public int getPathElementCount() {
		return this.hasPathElements() ? _pathElements.size()
									  : 0;
	}
	@Override
	public Iterator<String> iterator() {
		return this.getPathElementsIterator();
	}
	@Override
	public String getLastPathElement() {
		String outPathElement = this.hasPathElements() ? _pathElements.stream()
																	  .reduce((first,second) -> second)	// last element > Iterables.getLast(_pathElements,null)
																	  .orElse(null)
													   : null;
		return outPathElement;
	}
	@Override
	public String getFirstPathElement() {
		String outPathElement = this.hasPathElements() ? _pathElements.stream()
																	  .findFirst()
																	  .orElse(null) // first element > Iterables.getFirst(_pathElements,null)
													   : null;
		return outPathElement;
	}
	@Override
	public String getPathElementAt(final int pos) {
		String outPathElement = this.hasPathElements() ? _pathElements.stream()
																	  .skip(pos)
																	  .findFirst()
																	  .orElse(null)	// element at pos > Iterables.get(_pathElements,pos,null)
													   : null;
		return outPathElement;
	}
	@Override
	public List<String> getFirstNPathElements(final int num) {
		List<String> outPathElements = this.hasPathElements() ? _pathElements.subList(0,num)
															  : null;
		return outPathElements;
	}
	@Override
	public List<String> getPathElementsExceptLast() {
		if (_pathElements.size() == 1) return Lists.newArrayList();
		return this.getFirstNPathElements(_pathElements.size()-1);
	}
	@Override
	public List<String> getPathElementsFrom(final int pos) {
		List<String> outPathElements = this.hasPathElements() ? _pathElements.subList(pos,_pathElements.size())
															  : null;
		return outPathElements;
	} 
	@Override
	public List<String> getPathElementsWithin(final Range<Integer> range) {
		if (range.hasLowerBound() && !range.hasUpperBound()) return this.getPathElementsFrom(range.getLowerBound());
		if (!range.hasLowerBound() && range.hasUpperBound()) return this.getFirstNPathElements(range.getUpperBound());
		
		List<String> outPathElements = this.hasPathElements() ? _pathElements.subList(range.getLowerBound(),range.getUpperBound())
															  : null;
		return outPathElements;
	}
	@Override
	public <P extends PathBase<?>> List<String> getPathElementsAfter(final P prefix) {
		if (prefix == null
		 || CollectionUtils.isNullOrEmpty(_pathElements)
		 || this.getPathElementCount() < prefix.getPathElementCount()) return null;

		List<String> prefixTokens = prefix.getPathElements();
		
		// windows fix: skip [drive unit] letter 
		// maybe the prefix contains the [drive unit] letter and the current [path] does not (or the opposite)
		final boolean thisContainsDriveUnitLetterAndPrefixDoesNot = OSType.getOS() == OSType.WINDOWS
															    && !prefixTokens.get(0).endsWith(":")
															    && _pathElements.get(0).endsWith(":");
		final boolean prefixContainsDriveUnitLetterAndThisDoesNot = OSType.getOS() == OSType.WINDOWS
																 && prefixTokens.get(0).endsWith(":")
																 && !_pathElements.get(0).endsWith(":");
		
		// skip the prefix tokens
		return _pathElements.stream()
				   .skip(thisContainsDriveUnitLetterAndPrefixDoesNot ? 1 : 0)	// skikp the first item: the [drive unit] letter
				   .filter(new Predicate<String>() {
					   			int i = prefixContainsDriveUnitLetterAndThisDoesNot ? 1 : 0; 
					   			
								@Override
								public boolean test(final String token) {
									if (i < prefixTokens.size()) {
										if (!prefixTokens.get(i).equals(token)) throw new IllegalArgumentException("Path token " + token + " from " + PathBase.this.asAbsoluteString() +
																												   " is NOT at given prefix " + prefix);
										i = i + 1;
										return false;
									}
									// after the prefix
									return true;
								}
							})
				  .collect(Collectors.toList());
	}
	@Override
	public int getPathElementFirstPosition(final String pathElement) {
		int index = -1;

		if (pathElement == null
		 || CollectionUtils.isNullOrEmpty(_pathElements)) return index;

		int position = 0;
		for (String pathElem : this.getPathElements() ) {
			if (pathElem.equals(pathElement)) {
				index = position;
				break;
			}
			position++;
		}

		return index;
	}
	@Override
	public boolean containsPathElement(final String pathEl) {
		return this.hasPathElements() ? _pathElements.contains(pathEl)
									  : false;
	}
	@Override
	public boolean containsAllPathElements(final String... pathElsToCheck) {
		Path subPath = Path.from(pathElsToCheck);
		Path fullPath = Path.from(_pathElements.toArray(new String[_pathElements.size()]));
		return fullPath.asRelativeString().contains(subPath.asRelativeString());
	}
	public static Collection<String> pathElementsFrom(final Object obj) {
		if (obj == null) throw new IllegalArgumentException("Cannot build a path from null");
		Collection<String> outCol = null;
		if (obj.getClass().isArray()) {
			Object[] array = (Object[])obj;
			LinkedList<String> els = new LinkedList<>();
			for (Object objEl : array) {
				if (objEl == null) continue;
				if (objEl instanceof IsPath) {
					els.addAll(((IsPath)objEl).getPathElements());
				} else if (objEl instanceof Collection) {
					els.addAll(PathBase.pathElementsFrom(objEl));	// recurse
				} else {
					els.addAll(_normalizePathElement(objEl.toString()));
				}
			}
			outCol = Lists.newArrayList(els);
		}
		else if (obj instanceof Collection) {
			Collection<?> col = (Collection<?>)obj;
			Object[] array = col.toArray(new Object[col.size()]);
			outCol = PathBase.pathElementsFrom(array);				// recurse
		}
		else if (obj instanceof CanBeRepresentedAsString) {
			String str = ((CanBeRepresentedAsString)obj).asString();
			outCol = _normalizePathElement(str);
		}
		else {
			outCol = _normalizePathElement(obj.toString());
		}
		return outCol;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the path is pointing to a file
	 * (this is quite simple... it only checks if the last element of the path contains a dot)
	 * @return true if the last element of the path contains a dot (.)
	 */
	public boolean isFilePath() {
		String lastPathEl = this.getLastPathElement();
		return lastPathEl != null ? lastPathEl.contains(".")
								  : false;
	}
	/**
	 * Returns true if this is a path to a folder
	 * (this is quite simple... it only checks if the last element of the path does not contains a dot)
	 * @return true if the last element of the path does not contains a dot (.)
	 */
	public boolean isFolderPath() {
		return !this.isFilePath();
	}
	/**
	 * Returns the folder path (all the path except the file)
	 *		- file     		  > returns null
	 *		- folderA/file    > returns folderA
	 *		- folderA  		  > returns folderA
	 *		- folderA/folderB > returns folderA/folderB
	 * @return
	 */
	public Path getFolderPath() {
		if (_pathElements.size() == 1 && this.isFilePath()) return null;
		return this.isFilePath() ? _pathElements.size() == 1 ? null
															 : Path.from(this.getFirstNPathElements(_pathElements.size()-1)
																	 		 .toArray(new String[_pathElements.size()-1]))	// it's a file: return the parent folder				  
								 : Path.from(_pathElements);	// it's a folder... just return the folder
	}
	/**
	 * Returns the parent folder
	 *		- file     		  > returns null
	 *		- folderA/file    > returns folderA
	 *		- folderA  		  > returns null
	 *		- folderA/folderB > returns folderA
	 * @return
	 */
	public Path getParentFolderPath() {
		if (this.isFilePath()) return this.getFolderPath();
		return _pathElements.size() == 1 ? null
										 : Path.from(this.getFirstNPathElements(_pathElements.size()-1)
												 		 .toArray(new String[_pathElements.size()-1]));
	}
	/**
	 * The file name (if this is a path to a file)
	 * BEWARE! this method assumes that a file always contains EXTENSION;
	 *         ... otherwise it assumes it's a FOLDER and returns null
	 *         if your're sure that a file WITHOUT EXTENSION is a file
	 *         use {@link #getFileNameAssumingLastElementIsAFile()}
	 * If this is NOT a path to a file this returns null
	 * @return the file name with extension
	 */
	public String getFileName() {
		String lastPathEl = this.getLastPathElement();
		return lastPathEl != null && lastPathEl.contains(".") ? lastPathEl
															  : null;
	}
	/**
	 * Returns the file name assuming that the last path element is a file
	 * BEWARE that {@link #getFileName()} assumes that the last element is a file with extension
	 * @return
	 */
	public String getFileNameAssumingLastElementIsAFile() {
		return this.getLastPathElement();
	}
	/**
	 * The file name (if this is a path to a file)
	 * If this is NOT a path to a file this returns null
	 * @return the file name with extension
	 */
	public FileNameAndExtension getFileNameWithExtension() {
		String lastPathEl = this.getLastPathElement();
		return new FileNameAndExtension(lastPathEl);
	}
	/**
	 * The file name (if this is a path to a file)
	 * If this is NOT a path to a file this returns null
	 * @return the file name with extension
	 */
	public String getFileNameWithoutExtension() {
		String lastPathEl = this.getLastPathElement();
		return FileNameAndExtension.of(lastPathEl).getNameWithoutExtension();
	}
	/**
	 * The file extension (if this is a path to a file)
	 * If this is NOT a path to a file this returns null
	 * @return the file extension
	 */
	public String getFileExtension() {
		String fileName = this.getFileName();
		return fileName != null ? FileNameAndExtension.of(fileName).getExtension()
								: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns {@link String} with the path as an absolute path (not starting with /)
	 * @param pathElements
	 * @return
	 */
	protected static String asRelativeString(final LinkedList<String> pathElements) {
		String outStr = _joinPathElements(pathElements);
		if (outStr == null) outStr = "";
		return outStr;
	}
	/**
	 * Returns {@link String} with the path as an absolute path (starting with /)
	 * @param pathElements
	 * @return
	 */
	protected static String asAbsoluteString(final LinkedList<String> pathElements) {
		String outStr = _joinPathElements(pathElements);
		if (outStr == null) return "";
		if (outStr.matches("([a-zA-Z]:|http://|https://).*")) return outStr;	// d: or http://
		return "/" + outStr;
	}
	/**
	 * Composes a {@link String} with the path
	 * @param pathElements
	 * @return
	 */
	protected static String asString(final LinkedList<String> pathElements) {
		return PathBase.asRelativeString(pathElements);
	}
	private static String _joinPathElements(final LinkedList<String> pathElements) {
		String outStr = null;
		if (pathElements != null && pathElements.size() > 0) {
			outStr = pathElements.stream()
								 .filter(Objects::nonNull)
								 .collect(Collectors.joining("/"));	// Joiner.on('/')
						   											//		 .skipNulls()
						   											//		 .join(pathElements);
			outStr = outStr.replaceFirst("/\\?","?");									// fix query strings as foo/bar/?queryStr
			if (outStr.endsWith("?")) outStr = outStr.substring(0,outStr.length()-1);	// fix empty query strings
		}
		return outStr;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	NORMALIZE
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Collection<String> normalize(final Collection<String> els) {
		if (CollectionUtils.isNullOrEmpty(els)) return Lists.newArrayList();
		Collection<String> outNormalizedEls = Lists.newLinkedList();
		for (final String el : els) {
			Collection<String> normalizedEls = _normalizePathElement(el);
			outNormalizedEls.addAll(normalizedEls);
		}
		return outNormalizedEls;
	}
	/**
	 * @param path
	 * @return
	 */
	@SuppressWarnings("null")
	private static Collection<String> _normalizePathElement(final String path) {
		Collection<String> normalizedPathEls = null;
		if (!path.startsWith("http://") && !path.startsWith("https://")) {
			Collection<String> notNormalizedPathEls = Lists.newArrayList(Splitter.on("/")
																				 .split(path));
			normalizedPathEls = _normalizePathElements(notNormalizedPathEls);
		} else if (path.startsWith("http://")) {
			Collection<String> notNormalizedPathEls = Lists.newArrayList(Splitter.on("/")
																				 .split(path.substring("http://".length())));
			normalizedPathEls = Lists.newArrayList();
			normalizedPathEls.add("http:/");
			normalizedPathEls.addAll(_normalizePathElements(notNormalizedPathEls));
		} else if (path.startsWith("https://")) {
			Collection<String> notNormalizedPathEls = Lists.newArrayList(Splitter.on("/")
																				 .split(path.substring("https://".length())));
			normalizedPathEls = Lists.newArrayList();
			normalizedPathEls.add("https:/");
			normalizedPathEls.addAll(_normalizePathElements(notNormalizedPathEls));
		}
		return normalizedPathEls;
	}
	/**
	 * Normalizes a collections of path elements
	 * @param notNormalizedPathElements
	 * @return
	 */
	private static Collection<String> _normalizePathElements(final Collection<String> notNormalizedPathElements) {
		return CollectionUtils.hasData(notNormalizedPathElements) 
					? notNormalizedPathElements.stream()
											   .filter(notNormalized -> notNormalized != null 
											   						 && notNormalized.trim().length() > 0)
											   .map(PathBase::_normalizePathElementString)
											   .collect(Collectors.toList())
					: null;
	}
	/**
	 * Removes the leading or trailing / character from the element as string
	 * @param element
	 * @return
	 */
	private static String _normalizePathElementString(final String element) {
		if (element == null) return null;

		// trim spaces
		String outNormalizedElement = element.trim();

		// replace windows slash
		outNormalizedElement = _replaceWinSlash(outNormalizedElement);

		// remove leading /
		if (outNormalizedElement.startsWith("/")) {
			outNormalizedElement = _removeLeadingSlashes(outNormalizedElement);		// remove the leading /
		}
		// remove trailing /
		if (outNormalizedElement.endsWith("/")) {
			outNormalizedElement = _removeTrailingSlashes(outNormalizedElement);	// remove the trailing /
		}
		// remove duplicates /
		outNormalizedElement = _removeDuplicateSparators(outNormalizedElement);

		return outNormalizedElement;
	}
	private static String _removeLeadingSlashes(final String path) {
		String outPath = path;
		while (outPath.startsWith("/")) outPath = outPath.substring(1);
		return outPath;
	}
	private static String _removeTrailingSlashes(final String path) {
		String outPath = path;
		while (outPath.endsWith("/")) outPath = outPath.substring(0,outPath.length()-1);
		return outPath;
	}
	private static String _removeDuplicateSparators(final String path) {
		return path.replaceAll("/{1,}","/");		// replaces multiple / with a single /
	}
	private static String _replaceWinSlash(final String path) {
		return path.replaceAll("\\\\","/");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Removes an element from the tail
	 */
	protected static <P extends IsPath> P withoutLastPathElement(final PathFactory<P> pathFactory,
									 						     final P path) {
		if (path.getPathElements().size() == 0) return path;

		// get just some elements of the path 
		// 		Iterables.limit(path.getPathElements(),
		//				   		path.getPathElements().size()-1));
		Collection<String> newPathEls = path.getPathElements()
											.stream()
											.limit(path.getPathElements().size()-1)
											.toList();
	    return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PATH JOINER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static abstract class Paths2<P extends IsPath> {
		private final PathFactory<P> _pathFactory;

		/**
		 * Joins a variable length path elements to a given path
		 * @param elements the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final Object... elements) {
			return PathBase.join(_pathFactory,
							  	 elements);
		}
		/**
		 * Joins a variable length path elements to a given path
		 * @param path the parent path
		 * @param elements the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final P path,
					  final Object... elements) {
			return PathBase.join(_pathFactory,
							  	 path,
							  	 elements);
		}
		/**
		 * Joins a path element to a given path
		 * The path element can be anything, form another Path to an array of path element Strings or a Collection
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final P path,
					  final Object element) {
			return PathBase.join(_pathFactory,
							  	 path,
							  	 element);
		}
		/**
		 * Joins Collection of path elements to a given path
		 * @param path the parent path
		 * @param elements the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final P path,
					  final Collection<String> elements) {
			return PathBase.join(_pathFactory,
							  	 path,
							  	 elements);
		}
		/**
		 * Joins a path to another path
		 * @param path the parent path
		 * @param otherPath the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final P path,
					  final IsPath otherPath) {
			return PathBase.join(_pathFactory,
							  	 path,
							  	 otherPath);
		}
		/**
		 * Joins a string that can be a path element or a full path with the given path
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @param vars the vars to be substituted at the path element
		 * @return a new {@link IsPath} object
		 */
		public P joinCustomized(final P path,
						 	    final String element,final String... vars) {
			return PathBase.joinCustomized(_pathFactory,
										   path,
										   element,vars);
		}
		/**
		 * Joins a string that can be a path element or a full path with the given path
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P join(final P path,
					  final String element) {
			return PathBase.join(_pathFactory,
							  	 path,
							  	 element);
		}
		/**
		 * Prepends a variable length path elements to a given path
		 * @param path the parent path
		 * @param elements the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P prepend(final P path,
					  	 final Object... elements) {
			return PathBase.prepend(_pathFactory,
							  	 	path,
							  	 	elements);
		}
		/**
		 * Prepends a path element to a given path
		 * The path element can be anything, form another Path to an array of path element Strings or a Collection
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P prepend(final P path,
					     final Object element) {
			return PathBase.prepend(_pathFactory,
							  	 	path,
							  	 	element);
		}
		/**
		 * Prepends Collection of path elements to a given path
		 * @param path the parent path
		 * @param elements the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P prepend(final P path,
					     final Collection<String> elements) {
			return PathBase.prepend(_pathFactory,
							     	path,
							     	elements);
		}
		/**
		 * Prepends a path to another path
		 * @param path the parent path
		 * @param otherPath the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P prepend(final P path,
						 final IsPath otherPath) {
			return PathBase.prepend(_pathFactory,
								 	path,
								 	otherPath);
		}
		/**
		 * Prepends an element path (that can be a single element or a full path as a string) to another path
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @return a new {@link IsPath} object
		 */
		public P prepend(final P path,
						 final String element) {
			return PathBase.prepend(_pathFactory,
								 	path,
								 	element);
		}
		/**
		 * Prepends an element path (that can be a single element or a full path as a string) to another path
		 * @param path the parent path
		 * @param element the path elements to be joined with path
		 * @param vars the variables to be replaced a the element
		 * @return a new {@link IsPath} object
		 */
		public P prependCustomized(final P path,
								   final String element,final String... vars) {
			return PathBase.prependCustomized(_pathFactory,
										   	  path,
										   	  element,vars);
		}
		/**
		 * Removes an element from the tail
		 */
		public P withoutLastPathElement(final P path) {
			return PathBase.withoutLastPathElement(_pathFactory,
											   	   path);
		}
	}
	public static <P extends IsPath> Paths2<P> createPaths2(final PathFactory<P> pathFactory) {
		return new Paths2<>(pathFactory) {/* nothing */};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Note that join(pathType,Object...) is strictly the only method needed
// 	BUT many join methods are provided to AVOID excessive normalization
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Joins a variable length path elements to a given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
											   final Object... elements) {
		if (elements == null || elements.length == 0) return null;

		Collection<String> pathElsToAdd = PathBase.pathElementsFrom(elements);		// elements will be normalized at Paths.pathElementsFrom
		return _createPathInstanceFromElements(pathFactory,
											   pathElsToAdd);
	}
	/**
	 * Joins a variable length path elements to a given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
											   final P path,
											   final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return path;
		if (path == null) return PathBase.join(pathFactory,
										       elements);		// elements will be normalized

		Collection<String> pathElsToAdd = PathBase.pathElementsFrom(elements);		// elements will be normalized at Paths.pathElementsFrom
		return PathBase.join(pathFactory,
						  	 path,
						  	 pathElsToAdd);
	}
	/**
	 * Joins a path element to a given path
	 * The path element can be anything, form another Path to an array of path element Strings or a Collection
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
									 		   final P path,
									 		   final Object element) {
		if (element == null) return path;		// no changes
		if (path == null) return PathBase.join(pathFactory,
											   element);		// elements will be normalized

		Collection<String> pathElsToAdd = PathBase.pathElementsFrom(element);		// elements will be normalized at Paths.pathElementsFrom
		return PathBase.join(pathFactory,
						  	 path,
						  	 pathElsToAdd);
	}
	/**
	 * Joins Collection of path elements to a given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
									 		   final P path,
									 		   final Collection<String> elements) {
		if (elements == null || elements.size() == 0) return path;	// no changes
		if (path == null) return PathBase.join(pathFactory,
											   elements);			// elements will be normalized

		Collection<String> newPathEls = _joinCols12(path.getPathElements(),
											        _normalizePathElements(elements));	// elements NEEDS normalization
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Joins a string that can be a path element or a full path with the given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
									 		   final P path,
									 		   final String element) {
		if (Strings.isNullOrEmpty(element)) return path;	// no changes
		if (path == null) return PathBase.join(pathFactory,
											   element);		// element will be normalized

		Collection<String> newPathEls = _joinCols12(path.getPathElements(),
											        _normalizePathElement(element));		// elements NEEDS normalization
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Joins a path to another path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param otherPath the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P join(final PathFactory<P> pathFactory,
									 		   final P path,
									 		   final IsPath otherPath) {
		if (otherPath == null) return path;		// no changes
		if (path == null) return PathBase.join(pathFactory,
											   otherPath);

		Collection<String> newPathEls = _joinCols12(path.getPathElements(),
											        otherPath.getPathElements());			// elements DOES NOT NEED normalizations since they come from another (already normalized) path
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Joins a string that can be a path element or a full path with the given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @param vars the vars to be substituted at the path element
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P joinCustomized(final PathFactory<P> pathFactory,
									 		   		  	 final P path,
									 		   		  	 final String element,final String... vars) {
		if (Strings.isNullOrEmpty(element)) return path;	// no changes
		if (path == null) return PathBase.join(pathFactory,
											   _customize(element,(Object[])vars));	// will be normalized at Paths.join

		return PathBase.join(pathFactory,
						  	 path,
						  	 _customize(element,(Object[])vars));					// will be normalized at Paths.join
	}
	private static Collection<String> _joinCols12(final Collection<String> pathElements,
									 			  final Collection<String> pathElsToAdd) {
		if (pathElsToAdd == null || pathElsToAdd.size() == 0) return pathElements;	// nothing to add
		if (pathElements == null || pathElements.size() == 0) return pathElsToAdd;	// nothing to add

		// concat iterables > Iterables.concat(pathElements,pathElsToAdd)
		return Stream.of(pathElements,pathElsToAdd)
					 .flatMap(col -> col.stream())
					 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PREPEND
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Prepends a variable length path elements to a given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prepend(final PathFactory<P> pathFactory,
											   	  final P path,
											   	  final Object... elements) {
		if (CollectionUtils.isNullOrEmpty(elements)) return path;
		if (path == null) return PathBase.join(pathFactory,
										       elements);		// elements will be normalized

		Collection<String> pathElsToPrepend = PathBase.pathElementsFrom(elements);		// elements will be normalized at Paths.pathElementsFrom
		return PathBase.prepend(pathFactory,
						     	path,
						     	pathElsToPrepend);
	}
	/**
	 * Prepends a path element to a given path
	 * The path element can be anything, form another Path to an array of path element Strings or a Collection
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prepend(final PathFactory<P> pathFactory,
									 		      final P path,
									 		   	  final Object element) {
		if (element == null) return path;		// no changes
		if (path == null) return PathBase.join(pathFactory,
											   element);		// elements will be normalized

		Collection<String> pathElsToPrepend = PathBase.pathElementsFrom(element);		// elements will be normalized at Paths.pathElementsFrom
		return PathBase.prepend(pathFactory,
						  	 	path,
						  	 	pathElsToPrepend);
	}
	/**
	 * Joins Collection of path elements to a given path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param elements the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prepend(final PathFactory<P> pathFactory,
									 		   	  final P path,
									 		   	  final Collection<String> elements) {
		if (elements == null || elements.size() == 0) return path;	// no changes
		if (path == null) return PathBase.join(pathFactory,
											   elements);			// elements will be normalized

		Collection<String> newPathEls = _joinCols21(path.getPathElements(),
											        _normalizePathElements(elements));	// elements NEEDS normalization
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Prepends a path to another path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param otherPath the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prepend(final PathFactory<P> pathFactory,
									 		   	  final P path,
									 		   	  final IsPath otherPath) {
		if (otherPath == null) return path;		// no changes
		if (path == null) return  _createPathInstanceFromElements(pathFactory,
											   					  otherPath.getPathElements());		// elements DOES NOT NEED normalizations since they come from another (already normalized) path

		Collection<String> newPathEls = _joinCols21(path.getPathElements(),
												    otherPath.getPathElements());		// already normalized
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Prepends an element path (that can be a single element or a full path as a string) to another path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prepend(final PathFactory<P> pathFactory,
									 		   	  final P path,
									 		   	  final String element) {
		if (Strings.isNullOrEmpty(element)) return path;		// no changes
		if (path == null) return _createPathInstanceFromElements(pathFactory,
																 _normalizePathElement(element));	// element needs normalization

		Collection<String> newPathEls = _joinCols21(path.getPathElements(),
												    _normalizePathElement(element));	// element needs normalization
		return _createPathInstanceFromElements(pathFactory,
											   newPathEls);
	}
	/**
	 * Prepends an element path (that can be a single element or a full path as a string) to another path
	 * @param pathFactory a factory to create {@link IsPath} objects
	 * @param path the parent path
	 * @param element the path elements to be joined with path
	 * @param vars the variables to be replaced a the element
	 * @return a new {@link IsPath} object
	 */
	protected static <P extends IsPath> P prependCustomized(final PathFactory<P> pathFactory,
									 						final P path,
									 						final String element,final String... vars) {
		if (element == null || element.length() == 0) return path;		// no changes
		if (path == null) return _createPathInstanceFromElements(pathFactory,
																 _normalizePathElement(_customize(element,(Object[])vars)));	// element NEEDS normalization

		return PathBase.prepend(pathFactory,
							 	path,
							 	_customize(element,(Object[])vars));	// will be normalized at Paths.prepend
	}
	private static Collection<String> _joinCols21(final Collection<String> pathElements,
											      final Collection<String> pathElsToPrepend) {
		if (pathElements == null) throw new IllegalArgumentException();

		if (pathElsToPrepend == null || pathElsToPrepend.size() == 0) return pathElements;	// nothing to prepend
		// concat iterables > Iterables.concat(pathElsToPrepend,pathElements)
		return Stream.of(pathElsToPrepend,pathElements)
					 .flatMap(col -> col.stream())
					 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Customizes a {@link String} containing placeholders like {} for provided vars
	 * ie:
	 * <pre class='brush:java'>
	 * 		Strings.of("Hello {} today is {}","Alex","Saturday"}
	 * </pre>
	 * @param strToCustomize the {@link String} to be customized
	 * @param vars the placeholder's values
	 * @return an {@link StringBuffer} composed from the strToCustomize param replacing the placeholders with the provided values
	 */
	private static String _customize(final CharSequence strToCustomize,final Object... vars) {
		if (strToCustomize == null) return null;
		if (vars == null || vars.length == 0) return strToCustomize.toString();

		// reuse MessageFormatter from SL4FJ
		return r01f.util.types.Strings.customized(strToCustomize,vars);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	FileID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <O extends OID> boolean is(final O other) {
		return other instanceof PathBase ? ((PathBase<?>)this).equals(other)
										 : false;
	}
	@Override
	public <O extends OID> boolean isNOT(final O other) {
		return !this.is(other);
	}
	@Override @SuppressWarnings("null")
	public <O extends OID> boolean isIgnoringCase(final O other) {
		if (other == null) return false;
		if (!(other instanceof PathBase)) return false;

		PathBase<?> otherPath = (PathBase<?>)other;
		String thisPathStr = this.asAbsoluteString();
		String otherPathStr = otherPath.asAbsoluteString();
		if (thisPathStr == null && otherPathStr == null) return true;
		if (thisPathStr != null && otherPathStr != null) return false;
		if (thisPathStr == null && otherPathStr != null) return false;
		return thisPathStr.equalsIgnoreCase(otherPathStr);
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isContainedIn(final O... oids) {
		return oids != null ? this.isContainedIn(Lists.newArrayList(oids))
							: false;
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> boolean isNOTContainedIn(final O... oids) {
		return !this.isContainedIn(oids);
	}
	@Override
	public <O extends OID> boolean isContainedIn(final Iterable<O> oids) {
		boolean outContained = false;
		for (final O oid : oids) {
			if (this.is(oid)) {
				outContained = true;
				break;
			}
		}
		return outContained;
	}
	@Override
	public <O extends OID> boolean isNOTContainedIn(final Iterable<O> oids) {
		return !this.isContainedIn(oids);
	}
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O cast() {
		return (O)this;
	}
	@Override
	public int compareTo(final OID oid) {
		if (oid == null) return -1;
		return this.asAbsoluteString().compareTo(oid.asString());
	}
	public static <P extends PathBase<P>> Comparator<P> createComparatorAsString() {
																return (final P path1,final P path2) -> {
																			return path1 != null && path2 != null 
																						? path1.compareTo(path2)
																						: path1 != null && path2 == null
																								? 1
																								: path1 == null && path2 != null
																										? -1
																										: 0;	// both null
																		 };
														}
/////////////////////////////////////////////////////////////////////////////////////////
// 	Valid
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isValid() {
		return _pathElements != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OVERRIDEN METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((_pathElements == null) ? 0 : this.asAbsoluteString().hashCode());
		return result;
	}
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof PathBase)) return false;
		PathBase<?> other = (PathBase<?>)obj;
		String thisPath = this.asAbsoluteString();
		String otherPath = other.asAbsoluteString();
		return thisPath.equals(otherPath);
	}
}
