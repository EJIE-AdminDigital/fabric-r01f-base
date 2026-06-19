package r01f.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import r01f.mime.MimeType;
import r01f.util.types.collections.CollectionUtils;

public class HttpRequestBodyPublisherForMultipartForm {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private List<HttpRequestFormParameter> _formParameters;  	// params to be set to the server if GET o POST-FORM-URL-ENCODED
	/**
	 * Target Server Charset
	 */
	private final Charset _targetServerCharset;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////	
	public HttpRequestBodyPublisherForMultipartForm() {
		_targetServerCharset  =Charset.defaultCharset();
	}
	public  HttpRequestBodyPublisherForMultipartForm(final Charset targetServerCharset) {
		_targetServerCharset = targetServerCharset;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Establece un parametro de la llamada
	 * @param param el parametro
	 */
	public HttpRequestBodyPublisherForMultipartForm withPOSTFormParameter(final HttpRequestFormParameter param) {
		if (_formParameters == null) _formParameters = new ArrayList<>();
		_formParameters.add(param);
		return this;
	}
	/**
	 * Establece los parametros de la llamada
	 * @param params parametros
	 */
	public HttpRequestBodyPublisherForMultipartForm withPOSTFormParameters(final List<HttpRequestFormParameter> params) {
		_formParameters = params;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//BUILD
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpRequest.BodyPublisher build() {
		try {
			return HttpRequest.BodyPublishers.ofByteArray(_payloadtStream());
		} catch (final Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Writes the POSTed form into the body of the request sent to the server
//	See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
//
// IMPORTANT!!!		The x-www-form-urlencoded enctype is handled by HttpRequestFluentStatementForPOSTFormURLEncodedMethod type
//
// 	If the form is like:
//		<FORM action="http://server.com/cgi/handle"
//			enctype="multipart/form-data"
//			method="post">
//   				What is your name? 			<INPUT type="text" name="user_name">
//					What is your surname? 		<INPUT type="text" name="user_surname">
//   				What files are you sending? <INPUT type="file" name="selected_files">
//   		<INPUT type="submit" value="Send">
//			<INPUT type="reset">
// 		</FORM>
//
// If the user enters "Larry" and "Page" in the text inputs and selects only one file, the user agent might send back the following data:
//
//   	HEADER: Content-Type: multipart/form-data; boundary=AaB03x
//
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_name"
//
//   	Larry
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_surname"
//
//   	Page
//   	--AaB03x
//   	Content-Disposition: form-data; name="selected_files"; filename="file1.txt"
//   	Content-Type: text/plain
//
//   	... contents of file1.txt ...
//  	 --AaB03x--
//
//
// If the user enters "Larry" and "Page" in the text inputs, and selects two files "file1.txt" & "file2.gif", the user agent might send back the following data:
//
//		HEADER: Content-Type: multipart/form-data; boundary=AaB03x
//
//	   --AaB03x
//  	Content-Disposition: form-data; name="user_name"
//
//  	Larry
//   	--AaB03x
//   	Content-Disposition: form-data; name="user_surname"
//
//   	Page
//  	--AaB03x
//  	Content-Disposition: form-data; name="selected_files"
//  	Content-Type: multipart/mixed; boundary=BbC04y
//
//  	--BbC04y
//  	Content-Disposition: file; filename="file1.txt"
//  	Content-Type: text/plain
//
//  	... contents of file1.txt ...
//  	--BbC04y
//  	Content-Disposition: file; filename="file2.gif"
//  	Content-Type: image/gif
//  	Content-Transfer-Encoding: binary
//
//  	...contents of file2.gif...
//  	--BbC04y--
//  	--AaB03x--
//
/////////////////////////////////////////////////////////////////////////////////////////

	public static MimeType payloadContentType() {
		return new MimeType("multipart/form-data; boundary=**R01BOUNDR01**");
	}


	public   byte[] _payloadtStream() throws IOException {
		ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
		// Binary params
		Collection<HttpRequestFormParameter> binaryParams = _formParameters.stream()
																		   .filter(HttpRequestFormParameterForMultiPartBinaryData.class::isInstance)
																		   .toList();
		// Text params
		Collection<HttpRequestFormParameter> textParams = _formParameters.stream()
																	   	 .filter(HttpRequestFormParameterForText.class::isInstance)
																	   	 .toList();

		// Send params
		// Each param has the representation in the POST payload
		//		--boundary_start
		//		Content-Disposition: form-data; name="param_name"
		//
		//		param_value
		if (CollectionUtils.hasData(textParams)) {
			for (final HttpRequestFormParameter param : textParams) {
				dataOutputStream.write("--**R01BOUNDR01**\r\n".getBytes());
				dataOutputStream.write(param._serializeFormParam(_targetServerCharset,
													true));
			}
		}
		// Send the binary params in the second place; be carefull if there are more than one file
		if (CollectionUtils.hasData(binaryParams)) {
			for (final HttpRequestFormParameter param : binaryParams) {
				dataOutputStream.write("--**R01BOUNDR01**\r\n".getBytes());
				dataOutputStream.write(param._serializeFormParam(_targetServerCharset,
													true));
			}
		}
		// Finish the multipart
		dataOutputStream.write("--**R01BOUNDR01**--\r\n".getBytes());

		return dataOutputStream.toByteArray();
	 }
}
