package r01f.file.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FilePropertiesBase;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreAPIWrapper;
import r01f.types.Path;


@Slf4j
public abstract class Files {
/////////////////////////////////////////////////////////////////////////////////////////
// 	WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an utility type that wraps a {@link FileStoreAPI}
	 * @param api
	 * @return
	 */
	public static FileStoreAPIWrapper wrap(final FileStoreAPI api) {
		return new FileStoreAPIWrapper(api);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the file size with it's unit
	 * <ul>
	 * 		<li>If the file size is less than 1024 bytes: 'x b.'</li>
	 * 		<li>If the file size is between 1024 bytes and 1048576 bytes :'x Kb.'</li>
	 * 		<li>If the file size is between 1048576 bytes and 1073741824 bytes : 'x Mb.'</li>
	 * 		<li>If the file size is greater than 1073741824 bytes: 'x Gb.'</li>
	 * </ul>
	 * @param fileBytes file size in bytes
	 * @return the formatted file size 
	 */
	public static String formatFileSize(final long fileBytes) {
		return FilePropertiesBase.formatSize(fileBytes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file name part
	 */
	public static String getName(final String fileName) {
		return Files.fileNameAndExtension(fileName)[0];
	}
	/**
	 * @return the file extension part
	 */
	public static String getExtension(final String fileName) {
		return Files.fileNameAndExtension(fileName)[1];
	}
	/**
	 * Splits a {@link String} with the file name and it's extension into a
	 * {@link String} array where the first entry is the file name (without extension)
	 * and the second entry is the extension (if the file has extension)
	 * @param fileName
	 * @return
	 */
	public static String[] fileNameAndExtension(final String fileName) {
		return FileNameAndExtension.of(fileName)
								   .getFileNameAndExtension().get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if the file is a symbolic link
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean isSymLink(final File file) throws IOException {
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir,
							 file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}
	/**
	 * Returns the symbolic link target if the file is a symbolic link; false otherwise
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Path symLinkTarget(final File file) throws IOException {
		if (!Files.isSymLink(file)) return null;
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir,
							 file.getName());
		}
		return Path.from(canon.getCanonicalFile());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Deletes the folder that contains the given file
	 * @param filePath
	 */
	public static void deleteFolderContainingFile(final Path filePath) {
		File containerFolder = null;
		try {
			File file = new File(filePath.asAbsoluteString()); 
			if (file.exists()) {
				containerFolder = file.getParentFile();
				log.info("Deleting folder at {}",containerFolder.getAbsolutePath());
				FileUtils.deleteQuietly(containerFolder);		// maybe? FileUtils.deleteDirectory(folder)
			}
		} catch (Throwable th) {
			log.error("Error trying to delete the folder with path={}",(containerFolder != null ? containerFolder.getAbsolutePath() : filePath.asAbsoluteString()));
		}
	}
}
