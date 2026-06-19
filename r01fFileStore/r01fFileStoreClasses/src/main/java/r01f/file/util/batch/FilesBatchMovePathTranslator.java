package r01f.file.util.batch;

import r01f.types.Path;

public interface FilesBatchMovePathTranslator {
/////////////////////////////////////////////////////////////////////////////////////////
//  ABSOLUTE PATHS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns source ABSOLUTE path of a file to be moved given it's source RELATIVE path
	 * @param relPath the relative path of the source file to be moved
	 * @return
	 */
	public Path sourceAbsolutePathFromSourceRelativePath(final Path relPath);
	/**
	 * Returns destination ABSOLUTE path of a file to be moved given it's source RELATIVE path
	 * @param relPath the relative path of the source file to be moved
	 * @return
	 */
	public Path destinationAbsolutePathFromSourceRelativePath(final Path relPath);
/////////////////////////////////////////////////////////////////////////////////////////
//	RELATIVE PATHS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns source RELATIVE path of a file to be checked out given it's source ABSOLUTE path
	 * @param absPath the absolute path of the source file to be checked out
	 * @return
	 */
	public Path sourceRelativePathFromSourceAbsolutePath(final Path absPath);
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static FilesBatchMovePathTranslator createDefaultPathTranslatorFromBasePath(final Path basePath) {
		return new FilesBatchMovePathTranslator() {
							@Override
							public Path sourceAbsolutePathFromSourceRelativePath(final Path relPath) {
								return basePath.joinedWith(relPath);
							}
							@Override
							public Path destinationAbsolutePathFromSourceRelativePath(final Path relPath) {
								return basePath.joinedWith(relPath);
							}
							@Override
							public Path sourceRelativePathFromSourceAbsolutePath(final Path absPath) {
								return absPath.remainingPathFrom(basePath);
							}
					};
	}
}
