package r01f.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import r01f.util.types.collections.CollectionUtils;

public class HttpRequestBodyPublisherForURLEncodedForm {
/////////////////////////////////////////////////////////////////////////////////////////
//  MEMMBERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Params to be POSTed to the server
	 */
	private List<HttpRequestFormParameter> _formParameters;
	/**
	 * Target Server Charset
	 */
	private final Charset _targetServerCharset;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpRequestBodyPublisherForURLEncodedForm() {
		_targetServerCharset = Charset.defaultCharset();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a POSTed params
	 * @param params the params
	 */
	public HttpRequestBodyPublisherForURLEncodedForm withPOSTFormParameters(final HttpRequestFormParameter... params) {
		if (_formParameters == null) _formParameters = new ArrayList<>();
		if (CollectionUtils.hasData(params)) {
			_formParameters = new ArrayList<>(params.length);
			for (HttpRequestFormParameter param : params) {
				_formParameters.add(param);
			}
		}
		return this;
	}
	/**
	 * Sets the POSTed params
	 * @param params parametros
	 */
	public HttpRequestBodyPublisherForURLEncodedForm withPOSTFormParameters(final List<HttpRequestFormParameter> params) {
		_formParameters = params;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILD
/////////////////////////////////////////////////////////////////////////////////////////
	public HttpRequest.BodyPublisher build() {
		try {
			return HttpRequest.BodyPublishers.ofByteArray(_payloadtStream());
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  Writes the POSTed form into the body of the request sent to the server
//	See http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
//
// IMPORTANT!!!		The multipart/form-data enctype is handled by HttpRequestFluentStatementForPOSTMultiPartMethod type
//
// 	If the form is like:
//		<FORM action="http://server.com/cgi/handle"
//            enctype="application/x-www-form-urlencoded"
//            method="post">
//   				What is your name? 			<INPUT type="text" name="user_name">
//					What is your surname? 		<INPUT type="text" name="user_surname">
//   		<INPUT type="submit" value="Send">
//			<INPUT type="reset">
// 		</FORM>
//
// If the user enters "Larry" and "Page" in the text inputs, the user agent might send back the following data:
//
//   	HEADER: Content-Type: application/x-www-form-urlencoded
//
//		user_name=Larry&user_surname=Page
//
/////////////////////////////////////////////////////////////////////////////////////////

	public byte[]  _payloadtStream() throws IOException {
		ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
		// Binary params are not allowed in a POST of a form in its url-encoded form
		Collection<HttpRequestFormParameter> binaryParams = _formParameters.stream()
																		   .filter(HttpRequestFormParameterForMultiPartBinaryData.class::isInstance)
																		   .toList();
		if (CollectionUtils.hasData(binaryParams)) throw new IOException("An form-url-encoded POST method cannot be used when posting a binary param!");

		// Encode params in its url-encoded form
		for (Iterator<HttpRequestFormParameter> paramIt = _formParameters.iterator(); paramIt.hasNext(); ) {
			HttpRequestFormParameter param = paramIt.next();
			byte[] paramBytes = param._serializeFormParam(_targetServerCharset,
														  false);
			dataOutputStream.write(paramBytes);
			if (paramIt.hasNext()) dataOutputStream.write("&".getBytes());
		}
		return dataOutputStream.toByteArray();
	}
}
