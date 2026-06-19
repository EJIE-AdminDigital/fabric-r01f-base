package r01f.objectstreamer.custom;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.ranges.RangeSerializeUtils;
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
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.dataformat.xml.ser.ToXmlGenerator;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class MarshallerCustomStreamersForRange {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String TYPE_ID_PROPERTY_NAME = "typeId";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static class RangeSerializer<T extends Comparable<T>>
				extends StdSerializer<Range<T>> {

		protected BeanProperty _property;			// property being deserialized

		public RangeSerializer() {
			super(Range.class);
		}
		public RangeSerializer(final BeanProperty property) {
			this();
			_property = property;
		}

		@Override
		public ValueSerializer<?> createContextual(final SerializationContext ctxt,
												   final BeanProperty property) {
			return _property == property ? this		// collections
										 : new RangeSerializer<T>(property);
		}
		@Override
		public void serialize(final Range<T> value,
							  final JsonGenerator gen,final SerializationContext ctxt) {
			// [0] - Guess the range data type
			Class<T> dataType = Range.guessDataType(value);
			String rangeDataTypeId = dataType.getSimpleName();	// BEWARE!! the data type id is the SIMPLE NAME of the class
			String rangeSpec = RangeSerializeUtils.rangeAsString(value,dataType);

			// [1] - Serialize
			if (_property != null) {
				// a) serialize the object being a field of a container object
				if (gen instanceof ToXmlGenerator) {
					// --- XML
					ToXmlGenerator xgen = (ToXmlGenerator)gen;
					MarshallField fmAnn = _property.getAnnotation(MarshallField.class);
					if (fmAnn != null
					 && fmAnn.whenXml() != null
					 && fmAnn.whenXml().attr()) {	// attribute
						// serialize as an XML attribute as [range_type_id]:[range_spec]
						xgen.setNextIsAttribute(true);
						xgen.writeString(String.format("%s:%s",
							   			 			   rangeDataTypeId,rangeSpec));
					} else {
						// serialize the object itself as an XML element <propName dataTypeId=[typeId]>[rangeSpec]</propName>
						_writeRangeTypedWrapped(rangeDataTypeId,rangeSpec,
										 		gen);
					}
				} else {
					// --- JSON
					gen.writeString(String.format("%s:%s",
						   			 			  rangeDataTypeId,rangeSpec));
				}
			}
			else {
				// b) serialize the object itself as
				//		{
				//			[rootName] : {
				//				typeId=[typeId],
				//				rangeSpec=[value]
				//			}
				//		}
				_writeRangeTypedWrapped(rangeDataTypeId,rangeSpec,
								 		gen);
			}
		}
		private static void _writeRangeTypedWrapped(final String rangeDataTypeId,final String rangeSpec,
									  		 		final JsonGenerator gen) {
			gen.writeStartObject();
			_writeRangeTyped(rangeDataTypeId,rangeSpec,
							 gen);
			gen.writeEndObject();
		}
		private static void _writeRangeTyped(final String rangeDataTypeId,final String rangeSpec,
									  		 final JsonGenerator gen) {
			//		{
			//			[rootName] : {
			//				typeId=[typeId],
			//				rangeSpec=[value]
			//			}
			//		}
			if (!(gen instanceof ToXmlGenerator)) {
				// typeId
				gen.writeName(RANGE_TYPE_ID_PROPERTY_NAME);
				gen.writeString(rangeDataTypeId);
				// range spec
				gen.writeName(RANGE_SPEC_PROPERTY_NAME);
				gen.writeString(rangeSpec);
			}
			// <propName dataTypeId=[typeId]>[rangeSpec]</propName>
			else if (gen instanceof ToXmlGenerator) {
				ToXmlGenerator xgen = (ToXmlGenerator)gen;
				// ... the dataTypeId attr
				xgen.setNextIsAttribute(true);
				xgen.writeName(RANGE_TYPE_ID_PROPERTY_NAME);
				xgen.writeString(rangeDataTypeId);
				// ... the range spec
				xgen.setNextIsAttribute(false);
				xgen.setNextIsUnwrapped(true);
				xgen.writeName("fake_field_name");	// this field name is NEVER written to the XML output since NEXT IS UNWRAPPED was previously set
				xgen.writeString(rangeSpec);
			}
		}
	}
	private final static String RANGE_TYPE_ID_PROPERTY_NAME = TYPE_ID_PROPERTY_NAME;
	private final static String RANGE_SPEC_PROPERTY_NAME = "spec";

	private static final String NUMBER_TYPES_RANGE_PATTERN = Byte.class.getSimpleName() + "|" + Byte.class.getName() + "|" +
														     Integer.class.getSimpleName() + "|" + Integer.class.getName() + "|" +
														     Long.class.getSimpleName() + "|" + Long.class.getName() + "|" +
														     Short.class.getSimpleName() + "|" + Short.class.getName() + "|" +
														     Double.class.getSimpleName() + "|" + Double.class.getName() + "|" +
														     Float.class.getSimpleName() + "|" + Float.class.getName();
	// BEWARE!!!!!!!!!
	//      BEFORE JAVA 8 the data type identifier used in the Pattern was like:
	//           Date|LocalDate|LocalDateTime|LocalTime:[lowerBound,upperBound]
	//      that asumes that the JODA Time types are used
	//
	//      WITH JAVA 8 OR HIGHER the data type identifier used in the Pattern is like:
	//      	Date|LocalDate|LocalDateTime|LocalTime|java.time.LocalDate|java.time.LocalDateTime|java.time.LocalTime:[lowerBound,upperBound]
	//
	//      asumming that:
	//      	- if the data type is a JODA Time type, the simple name is used: LocalDate, LocalDateTime, LocalTime
	//      	- if the data type is a JAVA TIME type, the FQN is used: java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime
	private static final String DATE_TIME_TYPES_RANGE_PATTERN = Date.class.getName() + "|" + Date.class.getSimpleName() + "|" +
															    java.sql.Date.class.getName() + "|" +
																Instant.class.getName() + "|" + Instant.class.getSimpleName() + "|" +
															    OffsetDateTime.class.getName() + "|" + OffsetDateTime.class.getSimpleName() + "|" +
															    ZonedDateTime.class.getName() + "|" + ZonedDateTime.class.getSimpleName() + "|" +
															    LocalDate.class.getName() + "|" + LocalDate.class.getSimpleName() + "|" +
															    LocalDateTime.class.getName() + "|" + LocalDateTime.class.getSimpleName() + "|" +
															    LocalTime.class.getName() + "|" + LocalTime.class.getSimpleName();
	private static final Pattern RANGE_SPEC_PATTERN = Pattern.compile("(" +
																				// numbers
																			   NUMBER_TYPES_RANGE_PATTERN +
																			   "|" +
																			   // date-time
																			   DATE_TIME_TYPES_RANGE_PATTERN +
																	  ")" +
																	  ":" +
																	  "(.+)");

	public static class RangeDeserializer<T extends Comparable<T>>
		 		extends StdDeserializer<Range<T>> {

		// property being deserialized
		protected BeanProperty _property;

		public RangeDeserializer() {
			super(Range.class);
		}
		public RangeDeserializer(final BeanProperty property) {
			this();
			_property = property;
		}
		@Override
		public ValueDeserializer<?> createContextual(final DeserializationContext ctxt,
													 final BeanProperty property) {
			return _property == property ? this		// collections
										 : new RangeDeserializer<T>(property);
		}
		@Override
		public Range<T> deserialize(final JsonParser parser,
							 		final DeserializationContext ctxt) throws JacksonException {
			String rangeTypeId = null;				// the range type
			String rangeSpec = null;				// the range as string

			JsonNode node = parser.readValueAsTree(); 	// reads all tree!!

			// [1] - Read the object string representation depending on the serialized format
			if (node.getNodeType() == JsonNodeType.STRING) {
				// a) the object was serialized being a field of a container object as [range_type_id]:[range_spec]
				String rangeSerialized = node.asString();
				// [verify] - nulls values
				if (Strings.isNullOrEmpty(rangeSerialized)) return  null;

				Matcher m = RANGE_SPEC_PATTERN.matcher(rangeSerialized);
				if (m.find()) {
					rangeTypeId = m.group(1);	// type id
					rangeSpec = m.group(2);		// range spec
				} else {
					ctxt.reportInputMismatch(this,"Bad range definition: %s. Range expression MUST match %s",
												  rangeSerialized,RANGE_SPEC_PATTERN);
				}
			}
			else {
				// b) the object was serialized itself as:
				//		{
				//			spec: [range_spec],
				//			typeId: [range_type_id]
				//		}
				ObjectNode objNode = (ObjectNode)node;

				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(2);
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.properties().iterator(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					if (Strings.isNOTNullOrEmpty(me.getKey())) {
						propValues.put(me.getKey(),me.getValue().asString());
					} else {
						// ... when deserialized from xml as <propName typeId=[rangeTypeId]>[rangeSpec]</propName>
						//	   the property name is ""
						propValues.put(RANGE_SPEC_PROPERTY_NAME,me.getValue().asString());
					}
				}
				rangeTypeId = propValues.get(RANGE_TYPE_ID_PROPERTY_NAME);
				rangeSpec = propValues.get(RANGE_SPEC_PROPERTY_NAME);
			}

			// [2] - Get the oid type
			Range<T> outRange = _createRange(rangeSpec,rangeTypeId);
			return outRange;
		}
		@SuppressWarnings("unchecked")
		private Range<T> _createRange(final String rangeSpec,
									  final String rangeTypeId) {
			Class<?> rangeDataType = null;
			// date time
			if (rangeTypeId.equals(Date.class.getSimpleName()) || rangeTypeId.equals(Date.class.getName())) {
				rangeDataType = Date.class;
			} else if (rangeTypeId.equals(Instant.class.getSimpleName()) || rangeTypeId.equals(Instant.class.getName())) {
				rangeDataType = Instant.class;
			} else if (rangeTypeId.equals(ZonedDateTime.class.getSimpleName())) {
				rangeDataType = ZonedDateTime.class;
			} else if (rangeTypeId.equals(OffsetDateTime.class.getSimpleName())) {
				rangeDataType = OffsetDateTime.class;
			}
			// BEWARE!!!!!!!!!
			//      BEFORE JAVA 8 the data type identifier used in the Pattern was like:
			//           Date|LocalDate|LocalDateTime|LocalTime:[lowerBound,upperBound]
			//      that asumes that the JODA Time types are used
			//
			//      WITH JAVA 8 OR HIGHER the data type identifier used in the Pattern is like:
			//      	Date|LocalDate|LocalDateTime|LocalTime|java.time.LocalDate|java.time.LocalDateTime|java.time.LocalTime:[lowerBound,upperBound]
			//
			//      asumming that:
			//      	- if the data type is a JODA Time type, the simple name is used: LocalDate, LocalDateTime, LocalTime
			//      	- if the data type is a JAVA TIME type, the FQN is used: java.time.LocalDate, java.time.LocalDateTime, java.time.LocalTime
			else if (rangeTypeId.equals(LocalDate.class.getSimpleName()) || rangeTypeId.equals(LocalDate.class.getName())) {
				rangeDataType = LocalDate.class;
			} else if (rangeTypeId.equals(LocalDateTime.class.getSimpleName()) || rangeTypeId.equals(LocalDateTime.class.getName())) {
				rangeDataType = LocalDateTime.class;
			} else if (rangeTypeId.equals(LocalTime.class.getSimpleName())) {
				rangeDataType = LocalTime.class;
			}
			// numbers
			else if (rangeTypeId.equals(Integer.class.getSimpleName())) {
				rangeDataType = Integer.class;
			} else if (rangeTypeId.equals(Long.class.getSimpleName())) {
				rangeDataType = Long.class;
			} else if (rangeTypeId.equals(Short.class.getSimpleName())) {
				rangeDataType = Short.class;
			} else if (rangeTypeId.equals(Double.class.getSimpleName())) {
				rangeDataType = Double.class;
			} else if (rangeTypeId.equals(Float.class.getSimpleName())) {
				rangeDataType = Float.class;
			} else {
				try {
					rangeDataType = Class.forName(rangeTypeId);
				} catch (ClassNotFoundException e) {
					// This can't happen
				}
			}
			Range<T> outRange = Range.parse(rangeSpec,
								   			(Class<T>)rangeDataType);
			return outRange;
		}
	}
}
