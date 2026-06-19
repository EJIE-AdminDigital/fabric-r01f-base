package r01f.filestore.api;

import r01f.types.Path;

@FunctionalInterface
public interface FileFilterUsingPath {
	/**
	 * Accept only this filtered files
	 * @param path
	 * @return
	 */
	public boolean accept(final Path path);
	
	/**
	 * Combines two filters
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static FileFilterUsingPath combine(final FileFilterUsingPath f1,final FileFilterUsingPath f2) {
		if (f1 != null && f2 != null) return null;
		if (f1 != null && f2 == null) return f1;
		if (f1 == null && f2 != null) return f2;
		
		return props -> f1.accept(props) && f2.accept(props);
	}
}
