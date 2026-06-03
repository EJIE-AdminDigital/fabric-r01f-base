package r01f.file.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import r01f.file.FileProperties;
import r01f.file.util.FolderContentReportDiff.FolderContentReportItem;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;

/**
 * Creates a report with a folder's contents
 * Usage:
 * <pre class='brush:java'>
 *		FolderContentReporter reporter = new FolderContentReporter(filerApi);
 * 		Collection<FolderContentReportItem> items = reporter.reportFrom(rootPath);
 * </pre>
 * To compute the differences between two reports:
 * <pre class='brush:java'>
 * 		FolderContentReportDiff diff = FolderContentReporter.diff(items1,items2);
 * </pre>
 */
@RequiredArgsConstructor
public class FolderContentReporter
	 extends FolderWalker<FolderContentReportItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final FileStoreFilerAPI _filerApi;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FolderContentReporter(FileStoreFilerAPI filerApi, FileFilter filter) {
		super(filter);
		_filerApi = filerApi;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a file report (a {@link Collection} of {@link FolderContentReportItem}s) 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public Set<FolderContentReportItem> reportFrom(final Path folder) throws IOException {
		Stream<FolderContentReportItem> outPropsStream = this.reportStreamFrom(folder);
		return outPropsStream.collect(Collectors.toSet());
	}
	/**
	 * Creates a file report (a {@link Stream} of {@link FolderContentReportItem}s) 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public Stream<FolderContentReportItem> reportStreamFrom(final Path folder) throws IOException {
		Stream.Builder<FolderContentReportItem> outPropsStreamBuilder = Stream.builder();
		this.walk(folder,
				  outPropsStreamBuilder);
		Stream<FolderContentReportItem> outPropsStream = outPropsStreamBuilder.build();
		return outPropsStream;
	}	
	/**
	 * Creates a diff from two reports
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static FolderContentReportDiff diff(final Set<FolderContentReportItem> r1,final Set<FolderContentReportItem> r2) {
		return FolderContentReportDiff.createFrom(r1,r2);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected FileProperties getFolderProperties(final Path folderPath) throws IOException {
		return _filerApi.getFolderProperties(folderPath);
	}
	@Override
	protected Collection<FileProperties> listFolderContents(final Path folderPath,
															final FileFilter filter) throws IOException {
		FileProperties[] outProps =  _filerApi.listFolderContents(folderPath,
																  filter,
																  false);
		return outProps != null ? Lists.newArrayList(outProps)
								: Lists.<FileProperties>newArrayList();
	}
	@Override
	protected void _handleFile(final FileProperties fileProps,final int depth,
							   final Stream.Builder<FolderContentReportItem> results) throws IOException {
		FolderContentReportItem item = FolderContentReportItem.from(fileProps);
		results.add(item);
	}
}
