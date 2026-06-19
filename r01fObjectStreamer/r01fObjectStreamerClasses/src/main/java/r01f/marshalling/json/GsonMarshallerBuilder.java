package r01f.marshalling.json;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import r01f.marshalling.json.GsonTypeAdapterFactories.DayOfMonthTypeAdapterFactory;
import r01f.marshalling.json.GsonTypeAdapterFactories.DayOfWeekTypeAdapterFactory;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterFactoryForEnumWithCode;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterFactoryForOID;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForInstant;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForLegacyDate;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForLocalDate;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForLocalDateTime;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForLocalTime;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForOffsetDateTime;
import r01f.marshalling.json.GsonTypeAdapterFactories.GsonTypeAdapterForZonedDateTime;
import r01f.marshalling.json.GsonTypeAdapterFactories.MonthOfYearTypeAdapterFactory;
import r01f.marshalling.json.GsonTypeAdapterFactories.YearTypeAdapterFactory;
import r01f.patterns.IsBuilder;

public class GsonMarshallerBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builder
	 * <b>BEWARE!</b>
	 * It's advisable to inject the JSonMarshaller using GUICE since the created object
	 * is cached as a singleton
	 * @return the {@link GsonMarshaller}
	 */
	public static Gson createGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonBuilder.serializeNulls();
		gsonBuilder.registerTypeAdapter(Date.class,new GsonTypeAdapterForLegacyDate());
		gsonBuilder.registerTypeAdapter(Instant.class,new GsonTypeAdapterForInstant());
		gsonBuilder.registerTypeAdapter(LocalDate.class,new GsonTypeAdapterForLocalDate());
		gsonBuilder.registerTypeAdapter(LocalDateTime.class,new GsonTypeAdapterForLocalDateTime());
		gsonBuilder.registerTypeAdapter(LocalTime.class,new GsonTypeAdapterForLocalTime());
		gsonBuilder.registerTypeAdapter(OffsetDateTime.class,new GsonTypeAdapterForOffsetDateTime());
		gsonBuilder.registerTypeAdapter(ZonedDateTime.class,new GsonTypeAdapterForZonedDateTime());

		gsonBuilder.registerTypeAdapterFactory(new YearTypeAdapterFactory());
		gsonBuilder.registerTypeAdapterFactory(new MonthOfYearTypeAdapterFactory());
		gsonBuilder.registerTypeAdapterFactory(new DayOfMonthTypeAdapterFactory());
		gsonBuilder.registerTypeAdapterFactory(new DayOfWeekTypeAdapterFactory());

		gsonBuilder.registerTypeAdapterFactory(new GsonTypeAdapterFactoryForOID());

		gsonBuilder.registerTypeAdapterFactory(new GsonTypeAdapterFactoryForEnumWithCode());

		gsonBuilder.setFieldNamingStrategy(f -> f.getName().startsWith("_") ? f.getName().substring(1) : f.getName());	// FieldNamingStrategy to remove '_' prefix from field names
		return gsonBuilder.create();
	}
	public static GsonMarshaller create() {
		return new GsonMarshaller(GsonMarshallerBuilder.createGson());
	}
}
