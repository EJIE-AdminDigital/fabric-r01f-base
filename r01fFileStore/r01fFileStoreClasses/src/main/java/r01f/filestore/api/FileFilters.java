package r01f.filestore.api;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import r01f.file.FileProperties;
import r01f.filestore.api.local.LocalFileProperties;

public class FileFilters {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static java.io.FileFilter ioFileFilterFor(final FileFilter filter) {
		return new java.io.FileFilter() {
						@Override
						public boolean accept(final File file) {
							if (filter == null) return true;
							try {
								FileProperties fileProps = LocalFileProperties.from(file);
								return filter.accept(fileProps);
							} catch (IOException ioEx) {
								ioEx.printStackTrace();
								return false;
							}
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static Predicate<FileProperties> predicateFrom(final FileFilter filter) {
		return new Predicate<>() {
						@Override
						public boolean test(final FileProperties props) {
							if (filter == null) return true;
							return filter.accept(props);
						}
			   };
	}
}
