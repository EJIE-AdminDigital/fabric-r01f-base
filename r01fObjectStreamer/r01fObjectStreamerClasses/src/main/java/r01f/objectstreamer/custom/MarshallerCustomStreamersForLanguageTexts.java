package r01f.objectstreamer.custom;

import java.util.Iterator;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.util.types.locale.Languages;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.dataformat.xml.ser.ToXmlGenerator;

@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MarshallerCustomStreamersForLanguageTexts {
/////////////////////////////////////////////////////////////////////////////////////////
//	 LanguageTexts Serializer & Deserializer BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class LanguageTextsSerializerBase<L extends LanguageTexts,
															 SELF_TYPE extends LanguageTextsSerializerBase<L,SELF_TYPE>>
						 extends StdSerializer<L> {

		protected BeanProperty _property;		// property being serialized

		public LanguageTextsSerializerBase(final Class<L> langTextsType) {
			super(langTextsType);
		}
		public LanguageTextsSerializerBase(final Class<L> langTextsType,
										   final BeanProperty property) {
			this(langTextsType);
			_property = property;
		}

		protected abstract SELF_TYPE clone(final BeanProperty property);

		@Override
		public ValueSerializer<?> createContextual(final SerializationContext ctxt,
												   final BeanProperty property) {
			return _property == property ? this
										 : this.clone(property);
		}
		@Override @SuppressWarnings("unchecked")
		public void serialize(final L value,
							  final JsonGenerator gen,final SerializationContext ctxt) {
			gen.writeStartObject();
			Map<Language,String> langTextsMap = (Map<Language,String>)value;	// for now assume it's a LanguageTextsMapBacked
			for (Map.Entry<Language,String> me : langTextsMap.entrySet()) {
				Language lang = me.getKey();
				String textInLang = me.getValue();
				if (gen instanceof ToXmlGenerator) {
					gen.writeName(lang.name());
					gen.writeRawValue(String.format("<![CDATA[%s]]>",textInLang));
				} else {
					gen.writeStringProperty(lang.name(),textInLang);
				}
			}
			gen.writeEndObject();
		}
	}
	public static abstract class LanguageTextsDeserializerBase<L extends LanguageTexts,
															   SELF_TYPE extends LanguageTextsDeserializerBase<L,SELF_TYPE>>
		 				 extends StdDeserializer<L> {

		protected BeanProperty _property;		// property being deserialized

		public LanguageTextsDeserializerBase(final Class<L> langTextsType) {
			super(langTextsType);
		}
		public LanguageTextsDeserializerBase(final Class<L> langTextsType,
										 	 final BeanProperty property) {
			this(langTextsType);
			_property = property;
		}

		protected abstract SELF_TYPE clone(final BeanProperty property);
		protected abstract L createLanguageTextsInstance(final Class<L> type);

		@Override
		public ValueDeserializer<?> createContextual(final DeserializationContext ctxt,
													 final BeanProperty property) {
			return _property == property ? this
										 : this.clone(property);
		}
		@Override @SuppressWarnings("unchecked")
		public L deserialize(final JsonParser parser,
							 final DeserializationContext ctxt) throws JacksonException {
			L outLangTexts = this.createLanguageTextsInstance((Class<L>)this.handledType());

			JsonNode node = parser.readValueAsTree(); 	// reads all tree!!
			for (Iterator<Map.Entry<String,JsonNode>> fIt = node.properties().iterator(); fIt.hasNext(); ) {
				Map.Entry<String,JsonNode> f = fIt.next();
				String langStr = f.getKey();
				String text = f.getValue().asString();
				if (Languages.canBe(langStr)) {
					outLangTexts.add(Languages.fromName(langStr),text);
				} else {
					log.error("NOT recognized language: {}",langStr);
				}
			}
			return outLangTexts;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LANGUAGE TEXTS SERIALIZERS & DESERIALIZERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static class LanguageTextsSerializer
				extends LanguageTextsSerializerBase<LanguageTexts,
													LanguageTextsSerializer> {
		public LanguageTextsSerializer() {
			super(LanguageTexts.class);
		}
		public LanguageTextsSerializer(final BeanProperty property) {
			super(LanguageTexts.class,
				  property);
		}
		@Override
		protected LanguageTextsSerializer clone(final BeanProperty property) {
			return new LanguageTextsSerializer(property);
		}
	}
	public static class LanguageTextsDeserializer
				extends LanguageTextsDeserializerBase<LanguageTexts,
													  LanguageTextsDeserializer> {

		public LanguageTextsDeserializer() {
			super(LanguageTexts.class);
		}
		public LanguageTextsDeserializer(final BeanProperty property) {
			super(LanguageTexts.class,
				  property);
		}
		@Override
		protected LanguageTextsDeserializer clone(final BeanProperty property) {
			return new LanguageTextsDeserializer(property);
		}
		@Override
		protected LanguageTexts createLanguageTextsInstance(final Class<LanguageTexts> type) {
			return new LanguageTextsMapBacked(); 	// for now asume that all LanguageTexts are Map backed
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LANGUAGE TEXTS MAP BACKED SERIALIZERS & DESERIALIZERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static class LanguageTextsMapBackedSerializer
				extends LanguageTextsSerializerBase<LanguageTextsMapBacked,
													LanguageTextsMapBackedSerializer> {
		public LanguageTextsMapBackedSerializer() {
			super(LanguageTextsMapBacked.class);
		}
		public LanguageTextsMapBackedSerializer(final BeanProperty property) {
			super(LanguageTextsMapBacked.class,
				  property);
		}
		@Override
		protected LanguageTextsMapBackedSerializer clone(final BeanProperty property) {
			return new LanguageTextsMapBackedSerializer(property);
		}
	}
	public static class LanguageTextsMapBackedDeserializer
				extends LanguageTextsDeserializerBase<LanguageTextsMapBacked,
													  LanguageTextsMapBackedDeserializer> {

		public LanguageTextsMapBackedDeserializer() {
			super(LanguageTextsMapBacked.class);
		}
		public LanguageTextsMapBackedDeserializer(final BeanProperty property) {
			super(LanguageTextsMapBacked.class,
				  property);
		}
		@Override
		protected LanguageTextsMapBackedDeserializer clone(final BeanProperty property) {
			return new LanguageTextsMapBackedDeserializer(property);
		}
		@Override
		protected LanguageTextsMapBacked createLanguageTextsInstance(final Class<LanguageTextsMapBacked> type) {
			return new LanguageTextsMapBacked(); 	// for now asume that all LanguageTexts are Map backed
		}
	}
}
