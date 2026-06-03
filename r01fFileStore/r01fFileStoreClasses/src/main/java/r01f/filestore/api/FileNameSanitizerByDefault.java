package r01f.filestore.api;

import r01f.util.types.StringEncodeUtils;
import r01f.util.types.StringEscapeUtils;

/**
 * Default {@link FileNameSanitizer}
 */
public class FileNameSanitizerByDefault 
  implements FileNameSanitizer {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String sanitizeName(final String fileName) {
		return _sanitize(fileName);
	}
	@Override
	public String sanitizeExtension(final String extension) {
		return _sanitize(extension);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private String _sanitize(final String str) {
		if (str == null) return null;
		
		CharSequence theFileName = str;
		
		// unescape html encoded entities (ie &lt; &acute;...)
		theFileName = StringEscapeUtils.unescapeHTML(theFileName);
		
		// replace accents by non-accented equivalent
		theFileName = StringEncodeUtils.replaceAccentedCharsByNonAccentedEquivalents(str);
		
		// replace illegal chars by _
		theFileName = StringEncodeUtils.filterAndReplaceChars(theFileName,
															  new char[] {' ','\n','\r','\t',
																	  	  '[',']','#','@','%','&','$','~',
																	  	  '/','\\','=','(',')','!','º','ª','?','¿','^','*',':',';','ç','<','>'},
															  "_")
									   .toString();
		// do not allow .. or .
		if (theFileName.equals("..")) theFileName = "__";
		if (theFileName.equals(".")) theFileName = "_";
		// do not allow ~
		
		return theFileName.toString();
	}
}
