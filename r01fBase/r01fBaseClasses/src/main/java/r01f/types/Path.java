package r01f.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallConstructor;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * path abstraction, simply using a String encapsulation
 */
@MarshallType(as="path")
@Accessors(prefix="_")
@Immutable
public class Path
     extends PathBase<Path> {
	
	private static final long serialVersionUID = -4132364966392988245L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public Path() {
		super(new ArrayList<>());
	}
	public Path(final Collection<String> pathEls) {
		super(pathEls);
	}
	public Path(final Object... obj) {
		super(obj);
	}
	public Path(final Object obj) {
		super(obj);
	}
	public <P extends IsPath> Path(final P otherPath) {
		super(otherPath);
	}
	public Path(final String... elements) {
		super(elements);
	}
	public static PathFactory<Path> PATH_FACTORY = Path::new;
	
	@Override @SuppressWarnings("unchecked")
	public <P extends IsPath> PathFactory<P> getPathFactory() {
		return (PathFactory<P>)Path.PATH_FACTORY;
	}
	
	@MarshallConstructor
	public Path(final String element) {
	    super(element);	    
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	FACTORIES
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Factory fromId {@link String}
	 * @param id
	 * @return
	 */
	public static Path forId(final String id) {
		return Path.from(id);
	}
	/**
	 * Factory from {@link String}
	 * @param path
	 * @return
	 */
	public static Path valueOf(final String path) {
		return Path.from(path);
	}
	/**
	 * Fectory from path components
	 * @param elements
	 * @return
	 */
	public static Path of(final String... elements) {
		return Path.from(elements);
	}
	/**
	 * Factory from path components
	 * @param elements 
	 * @return the {@link Path} object
	 */
	public static Path from(final String... elements) {
		if (elements == null || elements.length == 0) return null;
		return new Path(elements);
	}
	/**
	 * Factory from other {@link Path} object
	 * @param other 
	 * @return the new {@link Path} object
	 */
	public static <P extends IsPath> Path from(final P other) {
		if (other == null) return null;
		Path outPath = new Path(other);
		return outPath;
	}
	/**
	 * Factory from an {@link Object} (the path is composed translating the {@link Object} to {@link String})
	 * @param obj 
	 * @return the {@link Path} object
	 */
	public static Path from(final Object... obj) {
		if (obj == null) return null;
		return new Path(obj);
	}
	/**
	 * Factory from a {@link File} object 
	 * @param file
	 * @return
	 */
	public static Path from(final File file) {
		if (file == null) return null;
		return new Path(file.getAbsolutePath());
	}
	/**
	 * Factory from a {@link java.nio.file.Path}
	 * @param nioPath
	 * @return
	 */
	public static Path from(final java.nio.file.Path nioPath) {
		if (nioPath == null) return null;
		return new Path(nioPath.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLONE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Path(_pathElements);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public Path withoutLastPathElement() {
		return PathBase.withoutLastPathElement(this.getPathFactory(),
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
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the folder path (all the path except the file)
	 * ... this assumes that the file name contains a DOT (.)
	 *     it this is NOT the case, the result is NOT accurate
	 * @return
	 */
	public Path getFolderPathAssumingFileWithExtension() {
		if (_pathElements.size() == 1 && this.isFilePath()) return null;
		if (_pathElements.size() == 1) return Path.from(_pathElements.get(0));
		return this.isFilePath() ? Path.from(this.getFirstNPathElements(_pathElements.size()-1)
							   		   .toArray(new String[_pathElements.size()-1]))
								 : this;
	}
	/**
	 * Returns the remaining path fragment begining where the given
	 * starting path ends
	 * ie: if path=/a/b/c/d
	 *     and startingPath = /a/b
	 *     ... this function will return /c/d
	 * @param startingPath
	 * @return
	 */
	public Path remainingPathFrom(final Path startingPath) {
		if (startingPath == null) return this;
		List<String> remainingPathEls = this.getPathElementsAfter(startingPath);
		return remainingPathEls != null ? new Path(remainingPathEls)
										: null;
	}
	/**
	 * Returns the url path AFTER the given prefix
	 * ie: if path=/foo/bar/baz.hml and prefix=/foo
	 * then, the returned path=/bar/baz.html
	 * If the path does NOT starts with the given prefix, it throws an IllegalStateException
	 * @param prefix
	 * @return
	 */
	public Path urlPathAfter(final Path prefix) {
		if (prefix == null) return this;
		return this.remainingPathFrom(prefix);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MATCH
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the path matches the given regex
	 * Beware that the matching is done against the path.asAbsoluteString() result
	 * @param p
	 * @return
	 */
	public boolean matches(final Pattern p) {
		String asStr = this.asAbsoluteString();
		Matcher m = p.matcher(asStr);
		return m.find();
	}
}
