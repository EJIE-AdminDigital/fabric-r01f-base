package r01f.file;

import java.io.File;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Path;
import r01f.util.types.StringEncodeUtils;
import r01f.util.types.Strings;

/**
 * Models a file name like myFile.ext
 */
@Immutable
@MarshallType(as="fileName")
@Accessors(prefix="_")
public class FileName
  implements CanBeRepresentedAsString {
	
	private static final long serialVersionUID = -7901960255575168878L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final String _fileName;
	
	@MarshallIgnoredField
	private final transient FileNameAndExtensionParser _fileNameAndExtensionParser;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FileName(@MarshallFrom("fileName") final String fileName) {
		_fileName = fileName;
		_fileNameAndExtensionParser = new FileNameAndExtensionParserUsingRegEx();
	}
	public static FileName of(final File f) {
		return FileName.of(f.getName());
	}
	public static FileName of(final String fileName) {
		return new FileName(fileName);
	}
	public static FileName of(final Path path) {
		String fileName = path.getFileName();
		return FileName.of(fileName);
	}
	public static FileName valueOf(final String fileName) {
		return new FileName(fileName);
	}
	public static FileName of(final String name,final String extension) {
		return Strings.isNOTNullOrEmpty(extension) ? new FileName(name + "." + extension)
								 				   : new FileName(name);
	}
	public static FileName sanitized(final FileName fileName) {
		String name = fileName.getFileName();
		String ext = fileName.getExtension();
		return FileName.of(FileName.sanitizeName(name),ext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  NAME & EXTENSION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file name part
	 */
	public String getName() {
		return _fileNameAndExtension.get()[0];
	}
	public String getNameSanitized() {
		return FileName.sanitizeName(this.getName());
	}
	/**
	 * @return the file extension part
	 */
	public String getExtension() {
		return _fileNameAndExtension.get()[1];
	}
	public boolean hasExtension() {
		return Strings.isNOTNullOrEmpty(this.getExtension());
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return _fileName;
	}
	@Override
	public String toString() {
		return _fileName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sanitizes the name part of the file removing accents and non standard characters
	 * @param fileName
	 * @return
	 */
	public static String sanitizeName(final String name) {
		if (name == null) return null;
		
		CharSequence iso = StringEncodeUtils.windows1252ToIso8859(name);
		CharSequence withoutAccents = StringEncodeUtils.replaceAccentedCharsByNonAccentedEquivalents(iso);
		CharSequence withoutPoints = StringEncodeUtils.filterAndReplaceChars(withoutAccents,new char[] {'.'},new String[] {"_"});
		return withoutPoints.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof FileName) ) return false;
		
		FileName otherFileName = (FileName)obj;
		return _fileName != null ? _fileName.equals(otherFileName.getFileName())
								 : otherFileName.getFileName() != null ? false
										 							   : true;
	}
	@Override
	public int hashCode() {
		return _fileName != null ? _fileName.hashCode()
								 : super.hashCode();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private transient Memoized<String[]> _fileNameAndExtension = new Memoized<String[]>() {
																		@Override
																		public String[] supply() {
																			return _fileNameAndExtensionParser.parseFileNameAndExtension(_fileName);
																		}
													   			 };
}
