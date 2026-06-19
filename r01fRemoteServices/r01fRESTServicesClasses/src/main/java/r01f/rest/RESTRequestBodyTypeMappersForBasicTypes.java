package r01f.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collection;

import com.google.common.collect.Lists;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import lombok.extern.slf4j.Slf4j;
import r01f.io.util.StringPersistenceUtils;
import r01f.reflection.ReflectionUtils;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.datetime.DateTimeMarshaller;

/**
 * Type mappers for user types
 */
@Slf4j
public class RESTRequestBodyTypeMappersForBasicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	Date (only usable for Dates sent in the BODY (POST / PUT)
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class DateRequestBodyTypeMapperBase
		  	          implements MessageBodyReader<Instant> {
		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			boolean outReadable = false;
			if (type.equals(Instant.class)) {
			     outReadable = true;
			}
			return outReadable;
		}
		@Override
		public Instant readFrom(final Class<Instant> type,final Type genericType,
							 	final Annotation[] annotations,
							 	final MediaType mediaType,
							 	final MultivaluedMap<String,String> httpHeaders,
							 	final InputStream entityStream) throws IOException,
							   										   WebApplicationException {
			log.trace("reading {} type",type.getName());
			String dateMillisStr = StringPersistenceUtils.load(entityStream);
			Instant outDate = null;
			if (Strings.isNOTNullOrEmpty(dateMillisStr)) {
				outDate = DateTimeMarshaller.forUnMarshallingFrom(dateMillisStr)
											.asInstantAsummingISO8601OrEPOCHMillis()
											.orElse(null);
			}
			return outDate;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Range (only usable for Dates sent in the BODY (POST / PUT)
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class DateRangeRequestBodyTypeMapperBase
		  	          implements MessageBodyReader<Range<Instant>> {
		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			boolean outReadable = false;
			if (type.equals(Range.class)) {
			     outReadable = true;
			}
			return outReadable;
		}
		@Override
		public Range<Instant> readFrom(final Class<Range<Instant>> type,final Type genericType,
							 	 	   final Annotation[] annotations,
							 	 	   final MediaType mediaType,
							 	 	   final MultivaluedMap<String,String> httpHeaders,
							 	 	   final InputStream entityStream) throws IOException,
							   											   	  WebApplicationException {
			log.trace("reading {} type",type.getName());
			String rangeAsString = StringPersistenceUtils.load(entityStream);
			Range<Instant> outRange = null;
			if (Strings.isNOTNullOrEmpty(rangeAsString)) {
				outRange = Range.parse(rangeAsString,Instant.class);
			}
			return outRange;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class MarshalledObjectRequestBodyTypeMapper<T>
		  	 		  implements MessageBodyReader<T> {

		private final Class<?> _mappedType;
		private final Collection<MediaType> _mediaTypes;

		public MarshalledObjectRequestBodyTypeMapper(final Class<?> mappedType,
												 final MediaType... mediaTypes) {
			this(mappedType,
				 CollectionUtils.hasData(mediaTypes) ? Lists.newArrayList(mediaTypes) : null);
		}
		public MarshalledObjectRequestBodyTypeMapper(final Class<?> mappedType,
												 final Collection<MediaType> mediaTypes) {
			_mappedType = mappedType;
			_mediaTypes = mediaTypes;
		}

		public abstract r01f.objectstreamer.Marshaller getObjectsMarshaller();

		@Override
		public boolean isReadable(final Class<?> type,final Type genericType,
								  final Annotation[] annotations,
								  final MediaType mediaType) {
			// every application/xml received params are transformed to java in this type
			return this.isCompatible(mediaType)
				&& ReflectionUtils.isImplementing(type,_mappedType);
		}
		@Override
		public T readFrom(final Class<T> type,final Type genericType,
						  final Annotation[] annotations,
						  final MediaType mediaType,
						  final MultivaluedMap<String,String> httpHeaders,
						  final InputStream entityStream) throws IOException,
							  								     WebApplicationException {
			log.trace("reading {} type", type.getName());
			// xml -> java
			String xml = StringPersistenceUtils.load(entityStream);
			log.trace(" ... xml load:{}", xml);
			T outObj = null;
			if (Strings.isNOTNullOrEmpty(xml)) {
				if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
					log.trace(" ... MEDIA TYPE:JSON");
					outObj = this.getObjectsMarshaller().forReading().fromJson(xml, type);
				} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
					log.trace(" ... MEDIA TYPE:XML");
					outObj = this.getObjectsMarshaller().forReading().fromXml(xml, type);
				} else {
					throw new IllegalArgumentException("Received media type is not compatible");
				}
			}
			log.trace(" ... response:{}", outObj);
			return outObj;
		}
		protected boolean isCompatible(final MediaType mediaType) {
			if (CollectionUtils.isNullOrEmpty(_mediaTypes)) return true;
			boolean outCompatible = false;
			for (MediaType m : _mediaTypes) {
				if (mediaType.isCompatible(m)) {
					outCompatible = true;
					break;
				}
			}
			return outCompatible;
		}
	}
}
