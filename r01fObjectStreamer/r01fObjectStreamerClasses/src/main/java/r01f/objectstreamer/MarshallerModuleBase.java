package r01f.objectstreamer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import r01f.crypto.Hash;
import r01f.file.FileName;
import r01f.file.FileNameAndExtension;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.mime.MimeType;
import r01f.objectstreamer.custom.MarshallerCustomStreamers.CanBeRepresentedAsStringDeserializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamers.CanBeRepresentedAsStringSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamers.OIDDeSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamers.OIDSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForDateTime.InstantDeserializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForDateTime.InstantSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForLanguageTexts.LanguageTextsDeserializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForLanguageTexts.LanguageTextsMapBackedDeserializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForLanguageTexts.LanguageTextsMapBackedSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForLanguageTexts.LanguageTextsSerializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForRange.RangeDeserializer;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForRange.RangeSerializer;
import r01f.oids.OID;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.types.AppVersion;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Color;
import r01f.types.JsonString;
import r01f.types.Range;
import r01f.types.StringBase64;
import r01f.types.TimeLapse;
import r01f.types.XmlString;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.DayOfWeek;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.SecondOfMinute;
import r01f.types.datetime.Time;
import r01f.types.datetime.WeekOfMonth;
import r01f.types.datetime.WeekOfYear;
import r01f.types.datetime.Year;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.collections.CollectionUtils;
import tools.jackson.core.Version;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.module.SimpleModule;

/**
 * see https://spin.atomicobject.com/2016/07/01/custom-serializer-jackson/
 */
public abstract class MarshallerModuleBase
	   		  extends SimpleModule {

	private static final long serialVersionUID = 803481859958735692L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final ClassgraphScanHandler _typeScan;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerModuleBase(final String name,final Version version,
								final Collection<JacksonSerializerAndDeserializerPair<?>> serAndDeserPairs,
								final ClassgraphScanHandler typeScan) {
		super(name,version);

		// type scanner
		_typeScan = typeScan;

		// naming strategy
		// BEWARE!!  problems with lombok's noargs constructors: https://github.com/FasterXML/jackson-databind/issues/1197
		//			 need to configure the mapper with: mapper.configure(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES,false);
		this.setNamingStrategy(new MarshallerJacksonPropertyNamingStrategy());

		// SERIALIZERS & DESERIALIZERS ================================================================
		if (CollectionUtils.hasData(serAndDeserPairs)) {
			serAndDeserPairs.forEach(serAndDeserPair -> serAndDeserPair.registerAt(this));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A custom Jackson {@link PropertyNamingStrategy} that just removes the underscore (_) used to
	 * name the private fields
	 * see http://www.javaroots.com/2013/03/how-to-use-naming-in-jackson.html
	 */
	public class MarshallerJacksonPropertyNamingStrategy
		 extends PropertyNamingStrategy {

		private static final long serialVersionUID = 7510871826090649773L;

		@Override
		public String nameForField(final MapperConfig<?> config,
								   final AnnotatedField field,
								   final String defaultName) {
			return defaultName.startsWith("_") ? defaultName.substring(1)
											   : super.nameForField(config,field,defaultName);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM SERIALIZER & DESERIALIZERS
/////////////////////////////////////////////////////////////////////////////////////////
	public record JacksonSerializerAndDeserializerPair<T>(Class<T> targetType,
												   	  	  ValueSerializer<T> serializer,ValueDeserializer<T> deSerializer) {
		public JacksonSerializerAndDeserializerPair {
			Objects.requireNonNull(targetType);
			Objects.requireNonNull(serializer);
			Objects.requireNonNull(deSerializer);
		}
		public void registerAt(final SimpleModule jacksonModule) {
			jacksonModule.addSerializer(this.targetType,this.serializer);
			jacksonModule.addDeserializer(this.targetType,this.deSerializer);
		}
	}
	@SuppressWarnings({ "unchecked","rawtypes" })
	public static Collection<JacksonSerializerAndDeserializerPair<?>> buildJacksonSerializerAndDeSerializersUsing(final ClassgraphScanHandler typeScan) {
		Collection<JacksonSerializerAndDeserializerPair<?>> outSerAndDeserPairs = new ArrayList<>();

		// OID serializer & deserializer
		// Register the Serializer & Deserializer to every OID implementation found in the classpath
		Collection<Class<? extends OID>> oidImplementingTypes = typeScan.scanForTypesImplementing(OID.class)
																	  	.stream()
																	  	.map(c -> (Class<? extends OID>)c)  // Explicit cast to Class<? extends OID> //.map(Class.class::cast)
																	  	.filter(ReflectionUtils::isInstanciable)
																	  	.collect(Collectors.toUnmodifiableSet());

		if (CollectionUtils.hasData(oidImplementingTypes)) {
			for (Class<? extends OID> oidImplementingType : oidImplementingTypes) {
				outSerAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(oidImplementingType,
																				   new OIDSerializer(oidImplementingType),new OIDDeSerializer(oidImplementingType)));
			}
		}
		// LanguageTexts serializer
		outSerAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(LanguageTexts.class,
																		   new LanguageTextsSerializer(),new LanguageTextsDeserializer()));
		outSerAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(LanguageTextsMapBacked.class,
																		   new LanguageTextsMapBackedSerializer(),new LanguageTextsMapBackedDeserializer()));

		// range serializer & deserializer
		outSerAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(Range.class,
																		   new RangeSerializer(),new RangeDeserializer()));

		// java date/time serializer & deserializer
		outSerAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(Instant.class,
																		   new InstantSerializer(),new InstantDeserializer()));

		// date/time serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(Year.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(MonthOfYear.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(DayOfMonth.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(DayOfWeek.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(Time.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(HourOfDay.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(MinuteOfHour.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(SecondOfMinute.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(WeekOfMonth.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(WeekOfYear.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(TimeLapse.class,outSerAndDeserPairs);

		// Url serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(Url.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(UrlQueryString.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(UrlQueryStringParam.class,outSerAndDeserPairs);

		// StringBase64 serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(StringBase64.class,outSerAndDeserPairs);
		
		// Hash
		_addCanBeRepresentedAsStringSerAndDeserPair(Hash.class,outSerAndDeserPairs);

		// MimeType serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(MimeType.class,outSerAndDeserPairs);

		// color serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(Color.class,outSerAndDeserPairs);

		// AppVersion serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(AppVersion.class,outSerAndDeserPairs);

		// contact serializer & desarializer
		_addCanBeRepresentedAsStringSerAndDeserPair(Phone.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(EMail.class,outSerAndDeserPairs);

		// FileName serializer & deserializer
		_addCanBeRepresentedAsStringSerAndDeserPair(FileName.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(FileNameAndExtension.class,outSerAndDeserPairs);
		
		// serialized json and xml 
		_addCanBeRepresentedAsStringSerAndDeserPair(JsonString.class,outSerAndDeserPairs);
		_addCanBeRepresentedAsStringSerAndDeserPair(XmlString.class,outSerAndDeserPairs);

		return outSerAndDeserPairs;
	}
	private static <T extends CanBeRepresentedAsString> void _addCanBeRepresentedAsStringSerAndDeserPair(final Class<T> targetType,
																								  		 final Collection<JacksonSerializerAndDeserializerPair<?>> serAndDeserPairs) {
		serAndDeserPairs.add(new JacksonSerializerAndDeserializerPair<>(targetType,
																		new CanBeRepresentedAsStringSerializer<>(targetType),new CanBeRepresentedAsStringDeserializer<T>(targetType)));
	}
}
