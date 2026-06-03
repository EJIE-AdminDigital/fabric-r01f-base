package r01f.objectstreamer.custom;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.util.types.datetime.DateTimeMarshaller;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MarshallerCustomStreamersForDateTime {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected record DateTimeFormatInfo(String pattern,TimeZone timeZone,
										Locale locale) {
		protected static DateTimeFormatInfo from(final JsonFormat.Value jsonFormat) {
			String pattern = jsonFormat.getPattern();
			TimeZone timeZone = jsonFormat.getTimeZone();
			Locale locale = jsonFormat.getLocale();
			return new DateTimeFormatInfo(pattern,timeZone,
										  locale);
		}
		public static <T extends Temporal> DateTimeFormatInfo from(final DatabindContext dbCtxt,
																   final BeanProperty property,final Class<T> typeForDefaults) {
			JsonFormat.Value jsonFormat = property != null ? property.findPropertyFormat(dbCtxt.getConfig(),typeForDefaults)
														   : dbCtxt.getDefaultPropertyFormat(typeForDefaults);
			return DateTimeFormatInfo.from(jsonFormat);
		}
	}
	static abstract class JavaDateTimeSerializerBase<T extends Temporal,
												     SELF_TYPE extends JavaDateTimeSerializerBase<T,SELF_TYPE>>
		   extends StdSerializer<T> {

		protected BeanProperty _property;		// property being serialized

		public JavaDateTimeSerializerBase(final Class<T> type) {
			super(type);
		}
		public JavaDateTimeSerializerBase(final Class<T> type,
											   final BeanProperty property) {
			this(type);
			_property = property;
		}

		@Override
		public ValueSerializer<?> createContextual(final SerializationContext ctxt,
												   final BeanProperty property) {
			return _property == property ? this
										 : this.clone(property);
		}
		protected abstract SELF_TYPE clone(final BeanProperty property);
	}
	static abstract class JavaDateTimeDeSerializerBase<T extends Temporal,
													   SELF_TYPE extends JavaDateTimeDeSerializerBase<T,SELF_TYPE>>
		 		  extends StdDeserializer<T> {

		// property being deserialized
		protected BeanProperty _property;

		public JavaDateTimeDeSerializerBase(final Class<T> type) {
			super(type);
		}
		public JavaDateTimeDeSerializerBase(final Class<T> type,
												 final BeanProperty property) {
			this(type);
			_property = property;
		}

		protected abstract SELF_TYPE clone(final BeanProperty property);

		@Override
		public ValueDeserializer<?> createContextual(final DeserializationContext ctxt,
													 final BeanProperty property) {
			return _property == property ? this		// collections
										 : this.clone(property);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INSTANT SERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class InstantSerializer
		 		extends JavaDateTimeSerializerBase<Instant,
		 										   InstantSerializer> {

		public InstantSerializer() {
			super(Instant.class);
		}
		public InstantSerializer(final BeanProperty property) {
			super(Instant.class,
				  property);
		}
		@Override
		protected InstantSerializer clone(final BeanProperty property) {
			return new InstantSerializer(property);
		}

		@Override
		public void serialize(final Instant instant,
							  final JsonGenerator gen,final SerializationContext ctxt) {
			DateTimeFormatInfo formatInfo = DateTimeFormatInfo.from(ctxt,
																	_property,Instant.class);
	        gen.writeString(DateTimeMarshaller.forMarshalling(instant)
											  .asStringUsingPattern(formatInfo.pattern(),formatInfo.locale()));
	    }
	}
	public static class InstantDeserializer
		 		extends JavaDateTimeDeSerializerBase<Instant,
		 											 InstantDeserializer> {

		public InstantDeserializer() {
			super(Instant.class);
		}
		public InstantDeserializer(final BeanProperty property) {
			super(Instant.class,
				  property);
		}
		@Override
		protected InstantDeserializer clone(final BeanProperty property) {
			return new InstantDeserializer(property);
		}

		@Override
		public Instant deserialize(final JsonParser parser,
								   final DeserializationContext ctxt) {
			DateTimeFormatInfo formatInfo = DateTimeFormatInfo.from(ctxt,
																	_property,Instant.class);
			String dateStr = parser.getString();
			return DateTimeMarshaller.forUnMarshallingFrom(dateStr)
									 .asInstantUsingPattern(formatInfo.pattern(),formatInfo.locale())
									 .orElse(null);
		}
	}
}
