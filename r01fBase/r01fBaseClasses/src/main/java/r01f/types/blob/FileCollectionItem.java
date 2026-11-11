package r01f.types.blob;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.file.FileName;
import r01f.oids.OID;
import r01f.util.types.Strings;

/**
 * Models a file component row
 */
@Accessors(prefix="_")
public class FileCollectionItem {
//////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMddHHmmssSSS");

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter	private String _fileTitle;

	@Getter @Setter	private Instant _fileDate;

	@Getter @Setter	private Instant _creationDate;

	@Getter @Setter	private File _file;

	@Getter @Setter	private OID _relatedObjectOid;

	@Getter @Setter	private OID _blobOid;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public <O extends OID> String getBlobNameUsing(final O relatedObjectOid) {
		FileName fileName = FileName.of(_file);

		// pattern: fileName_relatedObjectOid_createDate.fileExtension
		return Strings.customized("{}{}{}{}",
								  fileName.getName(),
								  relatedObjectOid != null ? "_" + relatedObjectOid : "",
								  _creationDate != null ? "_" + FORMATTER.format(_creationDate) : "",
								  fileName.hasExtension() ? "." + fileName.getExtension() : "");
	}

	public String getFileName() {
		return _file.getName();
	}
}
