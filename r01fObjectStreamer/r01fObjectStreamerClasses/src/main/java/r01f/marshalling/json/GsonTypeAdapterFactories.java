package r01f.marshalling.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.enums.EnumWithCode;
import r01f.guids.OID;
import r01f.reflection.ReflectionUtils;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.DayOfWeek;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.datetime.DateTimeMarshaller;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class GsonTypeAdapterFactories {
/////////////////////////////////////////////////////////////////////////////////////////
//  Single field objects
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see http://www.javacreed.com/gson-typeadapter-example/
	 * Gson TypeAdapterFactory used to avoid the OID's id field wrapping
	 * Gson by default serializes OIDs like:
	 * {
	 * 		oid {
	 * 			id : "xxxx"
	 *      }
	 * }
	 * ... but it's more elegant to serialize as:
	 * {
	 * 		oid : "xxx"
	 * }
	 */
	@RequiredArgsConstructor
	static class TypeAdapterFactoryForSingleFieldBase
	  implements TypeAdapterFactory  {
		private final Class<?> _type;
		private final String _fieldName;

		@Override
		public <T> TypeAdapter<T> create(final Gson gson,
										 final TypeToken<T> type) {
			if (!ReflectionUtils.isImplementing(type.getRawType(),_type)) return null;

		    final TypeAdapter<T> delegate = gson.getDelegateAdapter(this,type);
		    final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
		    return new TypeAdapter<T>() {
					      @Override
					      public void write(final JsonWriter out,final T value) throws IOException {
					    	  JsonElement tree = delegate.toJsonTree(value);
					    	  if (!(tree instanceof JsonObject)) {
					    		  elementAdapter.write(out,tree);
					    	  } else {
					    		  // instead of the object, serialize the id
					    		  elementAdapter.write(out,((JsonObject)tree).get(_fieldName));
					    	  }
					      }
					      @Override
					      public T read(final JsonReader in) throws IOException {
					          JsonElement tree = elementAdapter.read(in);
					          return delegate.fromJsonTree(tree);
					      }
		    };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	public static class GsonTypeAdapterFactoryForOID
		        extends TypeAdapterFactoryForSingleFieldBase {
		public GsonTypeAdapterFactoryForOID() {
			super(OID.class,
				  "id");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @see http://www.javacreed.com/gson-typeadapter-example/
	 * Gson TypeAdapterFactory used to serialize EnumWithCode enums as their code
	 * (gson by default serializes Enums as their names)
	 */
	public static class GsonTypeAdapterFactoryForEnumWithCode
		     implements TypeAdapterFactory  {

		@Override
		public <T> TypeAdapter<T> create(final Gson gson,
										 final TypeToken<T> type) {
			if (!ReflectionUtils.isImplementing(type.getRawType(),EnumWithCode.class)) return null;

			final TypeAdapter<T> delegate = gson.getDelegateAdapter(this,type);
		    final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
		    return new TypeAdapter<T>() {
					      @Override
					      public void write(final JsonWriter out,final T value) throws IOException {
					    	  JsonElement tree = delegate.toJsonTree(value);
					    	  if (!(tree instanceof JsonPrimitive)) {
					    		  elementAdapter.write(out,tree);
					    	  } else {
					    		  String codeAsString = ((EnumWithCode<?,?>)value).getCode().toString();
					    		  elementAdapter.write(out,new JsonPrimitive(codeAsString));
					    	  }
					      }
					      @Override
					      public T read(final JsonReader in) throws IOException {
					          JsonElement tree = elementAdapter.read(in);
					          return delegate.fromJsonTree(tree);
					      }
		    };
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static class YearTypeAdapterFactory
		        extends TypeAdapterFactoryForSingleFieldBase {
		public YearTypeAdapterFactory() {
			super(Year.class,
				  "year");
		}
	}
	public static class MonthOfYearTypeAdapterFactory
		        extends TypeAdapterFactoryForSingleFieldBase {
		public MonthOfYearTypeAdapterFactory() {
			super(MonthOfYear.class,
				  "monthOfYear");
		}
	}
	public static class DayOfMonthTypeAdapterFactory
		        extends TypeAdapterFactoryForSingleFieldBase {
		public DayOfMonthTypeAdapterFactory() {
			super(DayOfMonth.class,
				  "dayOfMonth");
		}
	}
	public static class DayOfWeekTypeAdapterFactory
		        extends TypeAdapterFactoryForSingleFieldBase {
		public DayOfWeekTypeAdapterFactory() {
			super(DayOfWeek.class,
				  "dayOfWeek");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	JAVA DATE TIME
/////////////////////////////////////////////////////////////////////////////////////////
	public static class GsonTypeAdapterForLegacyDate
	  		 implements JsonSerializer<Date>,
	  			 		JsonDeserializer<Date> {
	    @Override
	    public JsonElement serialize(final Date date,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(date.toInstant())
	        										   .asISO8601String());
	    }
	    @Override
	    public Date deserialize(final JsonElement jsonElement,
	                                 final Type type,
	                                 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        Instant instant = DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        									.asInstantAsummingISO8601Format()
	        									.orElse(null);
	        return instant != null ? Date.from(instant) : null;
	    }
	}
	public static class GsonTypeAdapterForInstant
	  		 implements JsonSerializer<Instant>,
	  			 		JsonDeserializer<Instant> {
	    @Override
	    public JsonElement serialize(final Instant date,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(date)
	        										   .asISO8601String());
	    }
	    @Override
	    public Instant deserialize(final JsonElement jsonElement,
	                               final Type type,
	                               final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asInstantAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
	public static class GsonTypeAdapterForLocalDate
	  		 implements JsonSerializer<LocalDate>,
	  			 		JsonDeserializer<LocalDate> {
	    @Override
	    public JsonElement serialize(final LocalDate localDate,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(localDate)
	        										   .asISO8601String()); // "yyyy-MM-dd"
	    }
	    @Override
	    public LocalDate deserialize(final JsonElement jsonElement,
	                                 final Type type,
	                                 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asLocalDateAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
	public static class GsonTypeAdapterForLocalDateTime
	  		 implements JsonSerializer<LocalDateTime>,
	  			 		JsonDeserializer<LocalDateTime> {
	    @Override
	    public JsonElement serialize(final LocalDateTime localDateTime,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(localDateTime)
	        										   .asISO8601String()); // "yyyy-MM-dd"
	    }
	    @Override
	    public LocalDateTime deserialize(final JsonElement jsonElement,
	                                 	 final Type type,
	                                 	 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asLocalDateTimeAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
	public static class GsonTypeAdapterForLocalTime
	  		 implements JsonSerializer<LocalTime>,
	  			 		JsonDeserializer<LocalTime> {
	    @Override
	    public JsonElement serialize(final LocalTime localDateTime,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(localDateTime)
	        										   .asISO8601String()); // "yyyy-MM-dd"
	    }
	    @Override
	    public LocalTime deserialize(final JsonElement jsonElement,
	                                 final Type type,
	                                 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asLocalTimeAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
	public static class GsonTypeAdapterForOffsetDateTime
	  		 implements JsonSerializer<OffsetDateTime>,
	  			 		JsonDeserializer<OffsetDateTime> {
	    @Override
	    public JsonElement serialize(final OffsetDateTime dateTime,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(dateTime)
	        										   .asISO8601String()); // "yyyy-MM-dd"
	    }
	    @Override
	    public OffsetDateTime deserialize(final JsonElement jsonElement,
	                                 	  final Type type,
	                                 	  final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asOffsetDateTimeAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
	public static class GsonTypeAdapterForZonedDateTime
	  		 implements JsonSerializer<ZonedDateTime>,
	  			 		JsonDeserializer<ZonedDateTime> {
	    @Override
	    public JsonElement serialize(final ZonedDateTime dateTime,
	                                 final Type type,
	                                 final JsonSerializationContext jsonSerializationContext) {
	        return new JsonPrimitive(DateTimeMarshaller.forMarshalling(dateTime)
	        										   .asISO8601String()); // "yyyy-MM-dd"
	    }
	    @Override
	    public ZonedDateTime deserialize(final JsonElement jsonElement,
	                                 	 final Type type,
	                                 	 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	        return DateTimeMarshaller.forUnMarshallingFrom(jsonElement.getAsJsonPrimitive().getAsString())
	        						 .asZonedDateTimeAsummingISO8601Format()
	        						 .orElse(null);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Legacy DATE
//	Copied from https://github.com/google/gson/blob/gson-parent-2.10.1/gson/src/main/java/com/google/gson/internal/bind/DateTypeAdapter.java
/////////////////////////////////////////////////////////////////////////////////////////
//	public static class DateTypeAdapterFactory
//		     implements TypeAdapterFactory  {
//
//		@Override @SuppressWarnings("unchecked")
//		public <T> TypeAdapter<T> create(final Gson gson,
//										 final TypeToken<T> type) {
//			if (!ReflectionUtils.isImplementing(type.getRawType(),Date.class)) return null;
//
//			return (TypeAdapter<T>)new DateTypeAdapter();
//		}
//	}
//	public static final class DateTypeAdapter
//			   		  extends TypeAdapter<Date> {
//		/**
//	     * List of 1 or more different date formats used for de-serialization attempts.
//	     * The first of them (default US format) is used for serialization as well.
//	     */
//		private final List<DateFormat> _dateFormats = new ArrayList<>();
//
//		public DateTypeAdapter() {
//			_dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT,Locale.US));
//			if (!Locale.getDefault().equals(Locale.US)) {
//				_dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT));
//			}
//			if (JavaVersion.isJava9OrLater()) {
//				_dateFormats.add(PreJava9DateFormatProvider.getUsDateTimeFormat(DateFormat.DEFAULT, DateFormat.DEFAULT));
//			}
//		}
//
//		@Override
//		public Date read(final JsonReader in) throws IOException {
//			if (in.peek() == JsonToken.NULL) {
//				in.nextNull();
//				return null;
//			}
//			return _deserializeToDate(in);
//		}
//		private Date _deserializeToDate(final JsonReader in) throws IOException {
//			String s = in.nextString();
//			synchronized (_dateFormats) {
//				for (DateFormat dateFormat : _dateFormats) {
//					try {
//						return dateFormat.parse(s);
//					} catch (ParseException ignored) {}
//				}
//			}
//			try {
//				return ISO8601Utils.parse(s,new ParsePosition(0));
//			} catch (ParseException e) {
//				throw new JsonSyntaxException("Failed parsing '" + s + "' as Date; at path " + in.getPreviousPath(), e);
//			}
//		}
//		@Override
//		public void write(final JsonWriter out,final Date value) throws IOException {
//			if (value == null) {
//				out.nullValue();
//				return;
//		    }
//			DateFormat dateFormat = _dateFormats.get(0);
//			String dateFormatAsString;
//			synchronized (_dateFormats) {
//				dateFormatAsString = dateFormat.format(value);
//			}
//			out.value(dateFormatAsString);
//		}
//	}
}
