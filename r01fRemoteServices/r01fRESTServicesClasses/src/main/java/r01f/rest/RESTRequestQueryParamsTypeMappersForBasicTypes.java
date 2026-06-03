package r01f.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.rest.annotations.DateFormat;
import r01f.rest.annotations.DateTimeFormat;
import r01f.util.types.datetime.DateTimeMarshaller;


/**
 * Param Converter Provider :
 * --------------------------------------------------
 *  If you want JAX-RS to automatically convert parameters such as query params, path params, or others, you need to create a ParamConverter. Primitive types, strings, and types who define certain conversion methods,
 *  such as a valueOf(String) method, are automatically converted.
 *   @See https://blog.sebastian-daschner.com/entries/jaxrs-convert-params
 *
 * To add this provider, add it at RESTResourceConfigBootstrapBase :
 * <pre class='brush:java'>
 * 			public Set<Class<? extends ParamConverterProvider>> getRequestReceivedQueryPathParamsTypesMappers() {
 *				Set<Class<? extends ParamConverterProvider>> outMappers = super.getRequestReceivedQueryPathParamsTypesMappers();
 *				outMappers.add(RESTRequestQueryParamsTypeMappersForBasicTypes.class);
 *				return outMappers;
 *			}
 * </pre>
 */
@Provider
public class RESTRequestQueryParamsTypeMappersForBasicTypes
  implements ParamConverterProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(final Class<T> rawType,
											  final Type genericType,
											  final Annotation[] annotations) {
		ParamConverter<T> outParamConverter = null;

		// ...checks the customs type for custom converters ( currently just different Data types and formats...)
		if (Instant.class.equals(rawType)) {
			ParameterConverterForJavaInstant dateParameterConverter = new ParameterConverterForJavaInstant();

			for (Annotation annotation : annotations) {
				if (DateTimeFormat.class.equals(annotation.annotationType())) {
					dateParameterConverter.setCustomDateTimeFormat((DateTimeFormat) annotation);
				} else if (DateFormat.class.equals(annotation.annotationType())) {
					dateParameterConverter.setCustomDateFormat((DateFormat) annotation);
				}
			}
			outParamConverter = (ParamConverter<T>)dateParameterConverter;
		} else if (LocalDate.class.equals(rawType)) {
			ParameterConverterForJavaTimeLocalDate localDateParameterConverter = new ParameterConverterForJavaTimeLocalDate();
			for (Annotation annotation : annotations) {
				if (DateFormat.class.equals(annotation.annotationType())) {
					localDateParameterConverter.setCustomDateFormat((DateFormat) annotation);
				}
			}
			outParamConverter = (ParamConverter<T>)localDateParameterConverter;
		}
		return outParamConverter;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM CONVERTERS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public static class ParameterConverterForJavaTimeLocalDate
		     implements ParamConverter<LocalDate> {
		/////////////////////////////////////////////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////////////////////////////////////////////
			public static final String DEFAULT_FORMAT = DateFormat.DEFAULT_DATE;
		/////////////////////////////////////////////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////////////////////////////////////////////
			@Setter private DateFormat _customDateFormat;
		/////////////////////////////////////////////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////////////////////////////////////////////
			@Override
			public LocalDate fromString(final String date) {
				if (date==null) {
					return null;
				}
				String format = DEFAULT_FORMAT;
				if (_customDateFormat != null) {
					format = _customDateFormat.value();
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				try {
					return LocalDate.parse(date, formatter);
				} catch (DateTimeParseException ex) {
					throw new WebApplicationException(ex);
				}
			}
			@Override
			public String toString(final LocalDate date) {
				return DateTimeFormatter.ofPattern(DEFAULT_FORMAT).format(date);
			}
	}

	@Accessors(prefix="_")
	public class ParameterConverterForJavaInstant
	  implements ParamConverter<Instant> {
		/////////////////////////////////////////////////////////////////////////////////////////
	    //		CONSTANTS
		/////////////////////////////////////////////////////////////////////////////////////////
		public static final String DEFAULT_FORMAT = DateTimeFormat.DEFAULT_DATE_TIME;
		/////////////////////////////////////////////////////////////////////////////////////////
	    //		FIELDS
		/////////////////////////////////////////////////////////////////////////////////////////
		@Setter private DateTimeFormat _customDateTimeFormat;
		@Setter private DateFormat _customDateFormat;
		/////////////////////////////////////////////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////////////////////////////////////////////
		@Override
		public Instant fromString(final String string) {
			String format = DEFAULT_FORMAT;
			if (_customDateFormat != null) {
				format = _customDateFormat.value();
			} else if (_customDateTimeFormat != null) {
				format = _customDateTimeFormat.value();
			}
			return DateTimeMarshaller.forUnMarshallingFrom(string)
								     .asInstantUsingPattern(format,null)
									 .orElse(null);
		}
		@Override
		public String toString(final Instant date) {
			return new SimpleDateFormat(DEFAULT_FORMAT).format(date);
		}
	}
}
