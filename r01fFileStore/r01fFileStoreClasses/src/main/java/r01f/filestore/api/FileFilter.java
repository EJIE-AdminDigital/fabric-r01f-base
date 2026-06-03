package r01f.filestore.api;

import java.util.Arrays;

import r01f.file.FileProperties;
import r01f.types.Path;

public interface FileFilter
		 extends FileFilterUsingPath {
	/**
	 * Accept only this filtered files
	 * @param props
	 * @return
	 */
	public default boolean accept(final FileProperties props) {
		if (props == null) return false;
		return this.accept(props.getPath());
	}
	
	/**
	 * Combines a bunch of files
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static FileFilter combine(final FileFilter... filters) {
		if (filters == null) return null;
		if (filters.length == 1) return filters[0];
				
		return new FileFilter() {
						@Override
						public boolean accept(final Path path) {
							return Arrays.stream(filters)
										 // test the path on every filter
										 .map(filter -> filter.accept(path))
										 // accumulate (reduce) to a single result 
										 .reduce((testAccumulator,thisTest) -> testAccumulator && thisTest)
										 .get();
						}
						@Override
						public boolean accept(final FileProperties props) {
							return Arrays.stream(filters)
										 // test the path on every filter
										 .map(filter -> filter.accept(props))
										 // accumulate (reduce) to a single result 
										 .reduce((testAccumulator,thisTest) -> testAccumulator && thisTest)
										 .get();
						}
			
			   };
	}
}
