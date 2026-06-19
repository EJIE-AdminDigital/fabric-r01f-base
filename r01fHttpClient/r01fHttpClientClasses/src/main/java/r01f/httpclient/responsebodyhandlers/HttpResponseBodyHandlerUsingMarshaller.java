package r01f.httpclient.responsebodyhandlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.Marshaller;

/**
 * Usage
 * <pre class='brush:java'>
 *		HttpRequest request = HttpRequest.newBuilder(uri)
 *		   							 .header("Accept", "application/json")
 *		   							 .build();
 *   	MyObj obj = HttpClient.newHttpClient()
 *		   				  .send(request,HttpResponseBodyHandlerUsingMarshaller.using(marshaller)
 *		   																	  .toMarshallTo(MyObj.class))
 *		   				  .body()
 *		   				  .get();
 * </pre>
 */
@Slf4j
public class HttpResponseBodyHandlerUsingMarshaller<T>
  implements HttpResponse.BodyHandler<Supplier<T>> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	private final Class<T> _type;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	private HttpResponseBodyHandlerUsingMarshaller(final Marshaller marshaller,
												   final Class<T> type) {
		_marshaller = marshaller;
		_type = type;
	}
	public static HttpResponseBodyHandlerUsingMarshallerBuilderTypeStep using(final Marshaller marshaller) {
		return new HttpResponseBodyHandlerUsingMarshallerBuilderTypeStep(marshaller);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class HttpResponseBodyHandlerUsingMarshallerBuilderTypeStep {
		private final Marshaller _marshaller;
		
		public <T> HttpResponseBodyHandlerUsingMarshaller<T> toMarshallTo(final Class<T> type) {
			return new HttpResponseBodyHandlerUsingMarshaller<>(_marshaller,type);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public BodySubscriber<Supplier<T>> apply(final ResponseInfo responseInfo) {
		// [1]-Get a body subscriber
		HttpResponse.BodySubscriber<InputStream> bodyStreamSubscriber = HttpResponse.BodySubscribers.ofInputStream();
		
		// [2]-map the body stream Subscriber to a Supplier of the object that uses the marshaller to marshall
		// 	   from json to the object instance
		HttpResponse.BodySubscriber<Supplier<T>> outSubscriber = HttpResponse.BodySubscribers
																			 .mapping(bodyStreamSubscriber,
																					  // mapper
																					  theStream -> () -> {
																										try (InputStream is = theStream) {
																											
																											byte[] rawBytes = is.readAllBytes(); 
																									        String rawJson = new String(rawBytes);
																									        log.warn("..received json :{}" , rawJson);																									        
																											return _marshaller.forReading()
																															  .fromJson(new ByteArrayInputStream(rawBytes),_type);
																										} catch (IOException ioEx) {
																											throw new UncheckedIOException(ioEx);
																										}
																								   });
		return outSubscriber;
	}
}
