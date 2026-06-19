package r01f.objectstreamer.custom;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.patterns.FactoryFromType;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.collections.CollectionUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.xml.deser.FromXmlParser;
import tools.jackson.dataformat.xml.ser.ToXmlGenerator;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class MarshallerCustomStreamersForCanBeRepresentedAsString {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String TYPE_ID_PROPERTY_NAME = "typeId";

/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class CanBeRepresentedAsStringSerializerBase<T extends CanBeRepresentedAsString,
																		SELF_TYPE extends CanBeRepresentedAsStringSerializerBase<T,SELF_TYPE>>
						 extends ValueSerializer<T> {

		protected final Class<T> _objType;		// type being serialized
		protected BeanProperty _property;		// property being serialized

		public CanBeRepresentedAsStringSerializerBase(final Class<T> objType) {
			_objType = objType;
		}
		public CanBeRepresentedAsStringSerializerBase(final Class<T> objType,
													  final BeanProperty property) {
			this(objType);
			_property = property;
		}
		/**
		 * Creates a new serializer
		 * @param objType
		 * @param property
		 * @return
		 */
		protected abstract SELF_TYPE cloneFrom(final Class<T> objType,final BeanProperty property);

		@Override
		public ValueSerializer<?> createContextual(final SerializationContext ctxt,
												   final BeanProperty property) {
			return _property == property ? this	// collections
										 : this.cloneFrom(_objType,
												 		  property);
		}
		@Override
		public void serialize(final T value,
							  final JsonGenerator gen,final SerializationContext ctxt) {
			// a) serialize the object being a field of a container object
			if (_property != null) {
				gen.writeString(value.asString());
			// b) serialize the object itself as { [rootName] : [value] }
			} else if (gen instanceof ToXmlGenerator) {
				// if serializing as xml DO NOT include the root name since it's already included
				gen.writeString(value.asString());
			} else {
				String typeRootName = MarshallerCustomStreamers.typeRootName(ctxt.getConfig(),
																   			 _objType);
				gen.writeStartObject();
				gen.writeName(typeRootName);
				gen.writeString(value.asString());
				gen.writeEndObject();
			}
		}
	}
	public static abstract class CanBeRepresentedAsStringDeserializerBase<T extends CanBeRepresentedAsString,
																		  SELF_TYPE extends CanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE>>
						 extends ValueDeserializer<T> {
		// object factory
		protected final Class<T> _objType;		// type being deserialized
		protected final FactoryFromType<T> _objFactory;

		// property being deserialized
		protected BeanProperty _property;

		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
														final FactoryFromType<T> objFactory) {
			_objType = objType;
			_objFactory = objFactory;
		}
		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
														final FactoryFromType<T> objFactory,
														final BeanProperty property) {
			this(objType,objFactory);
			_property = property;
		}
		public CanBeRepresentedAsStringDeserializerBase(final Class<T> objType) {
			_objType = objType;
			_objFactory = (type,args) -> {	// factory from type
								Preconditions.checkArgument(CollectionUtils.hasData(args) && args.length == 1 && args[0] instanceof String,
															"No string representation was handed to create the object!");
								return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
							};
		}
		/**
		 * Creates a new serializer
		 * @param objType
		 * @param objFactory
		 * @param property
		 * @return
		 */
		protected abstract SELF_TYPE cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property);

		@Override @SuppressWarnings("unchecked")
		public ValueDeserializer<?> createContextual(final DeserializationContext ctxt,
													 final BeanProperty property) {
			return _property == property ? this	// collections
										 : this.cloneFrom(_objType,
												 		  _objFactory,
												 		  property);
		}
		@Override @SuppressWarnings("unchecked")
		public T deserialize(final JsonParser parser,
							 final DeserializationContext ctxt) throws JacksonException {
			String objAsString = null;				// the obj as string

			JsonNode node = parser.readValueAsTree(); 	// reads all tree!!

			// [1] - Read the object string representation depending on the serialized format
			// a) the object was serialized being a field of a container object as [value]
			if (node.getNodeType() == JsonNodeType.STRING) {
				objAsString = node.asString();
			}
			// b) the object was serialized itself as as { [rootName] : [value] }
			else {
				ObjectNode objNode = (ObjectNode)node;

				// {
				//		[type's root name] : [value]
				// }
				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(1);
				assert objNode.properties().size() == 1;
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.properties().iterator(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					propValues.put(me.getKey(),me.getValue().asString());
				}
				if (parser instanceof FromXmlParser) {
					// if serialized as xml, the node only contains the value without property name:  <rootName>value</rootName>
					objAsString = propValues.get("");	// no property
				} else {
					String typeRootName = MarshallerCustomStreamers.typeRootName(ctxt.getConfig(),
																	   			 _objType);
					objAsString = propValues.get(typeRootName);
				}
			}

			// [2] - Get the oid type
			T outObj = _objFactory.create(_objType,objAsString); 	// ReflectionUtils.createInstanceFromString(objType,objAsString);
			return outObj;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	POLYMORPHIC CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class PolymorphicCanBeRepresentedAsStringSerializerBase<T extends CanBeRepresentedAsString,
																				   SELF_TYPE extends PolymorphicCanBeRepresentedAsStringSerializerBase<T,SELF_TYPE>>
						 extends CanBeRepresentedAsStringSerializerBase<T,SELF_TYPE> {

		// the serialized property name for the string representation of the object
		protected final String _valuePropName;

		public PolymorphicCanBeRepresentedAsStringSerializerBase(final Class<T> objType,
							 						  		 	 final String valuePropName) {
			super(objType);
			_valuePropName = valuePropName;
		}
		public PolymorphicCanBeRepresentedAsStringSerializerBase(final Class<T> objType,
															 	 final BeanProperty property,
															 	 final String valuePropName) {
			super(objType,
				  property);
			_valuePropName = valuePropName;
		}
		@Override
		public void serializeWithType(final T value,
									  final JsonGenerator gen,final SerializationContext ctxt,
									  final TypeSerializer typeSer) throws JacksonException {	// polymorphic serialization
			// [1] - Type id
			//		 BEWARE!! Just abstract types or interfaces
			//				  It's IMPORTANT that both MarshallerAnnotationIntrospector#findPolymorphicTypeInfo(...) which finds the @MarshallPolymorphicTypeInfo annotation
			//				  and MarshallerAnnotationIntrospector#findTypeResolverBuilder(...) which finds the TypeResolverBuilder RETURN null for concrete types
			//		 ... for concrete types the typeId is not written
			WritableTypeId typeId = typeSer.typeId(value,JsonToken.START_OBJECT);
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsAttribute(true);	// ... the dataTypeId attr
			typeSer.writeTypePrefix(gen,ctxt,
									typeId);
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsAttribute(false);	// ... the object itself is NOT an attribute

			// this is where the value goes
			// {
			//		typeId="theTypeId",
			//		value="12341234"
			// }
			gen.writeName("value");

			// [2] - value
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsUnwrapped(true);
			gen.writeString(value.asString());
			if (gen instanceof ToXmlGenerator) ((ToXmlGenerator)gen).setNextIsUnwrapped(false);

			// [3] - type suffix
			typeId.wrapperWritten = !gen.canWriteTypeId();
			typeSer.writeTypeSuffix(gen,ctxt,
									typeId);
		}
	}
	public static abstract class PolymorphicCanBeRepresentedAsStringDeserializerBase<T extends CanBeRepresentedAsString,
																					 SELF_TYPE extends PolymorphicCanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE>>
		 				 extends CanBeRepresentedAsStringDeserializerBase<T,SELF_TYPE> {

		// the serialized property name for the string representation of the object
		protected final String _valuePropName;

		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,final FactoryFromType<T> objFactory,
							   								   	   final String valuePropName) {
			super(objType,objFactory);
			_valuePropName = valuePropName;
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,final FactoryFromType<T> objFactory,
															   	   final BeanProperty property,
															   	   final String valuePropName) {
			this(objType,objFactory,
				 valuePropName);
			_property = property;
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
							   								   	   final String valuePropName) {
			this(objType,(type,args) -> ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]),	// factory from
				 valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringDeserializerBase(final Class<T> objType,
															   	   final BeanProperty property,
															   	   final String valuePropName) {
			this(objType,(type,args) -> ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]),	// factory from
				 property,
				 valuePropName);
		}
		@Override @SuppressWarnings("unchecked")
		public T deserialize(final JsonParser parser,final DeserializationContext ctxt) throws JacksonException {
			// [0] - Get the object type
			//		 (use the property type if available, otherwise use the handed type)
			Class<? extends T> objType = null;		// the obj type
			if (_property != null) {
				if (_property.getType().isContainerType()
				 || _property.getType().isArrayType()) {
					objType = (Class<? extends T>)_property.getType().getContentType().getRawClass();
				} else {
					objType = (Class<? extends T>)_property.getType().getRawClass();
				}
			}
			// if the object type extracted from the property is not usabe, use the one handed to the deserializer
			if ((objType == null || !ReflectionUtils.isInstanciable(objType))
				&&
				(_objType != null && ReflectionUtils.isInstanciable(_objType))) {
				objType = _objType;
			}

			// [1] - Read the object string representation depending on the serialized format
			String objAsStringVal = null;			// the obj as string

			JsonNode node = parser.readValueAsTree(); 	// reads all tree!!
			if (node.getNodeType() == JsonNodeType.STRING) {
				// 1.1) the object string representation was serialized like a simple string
				//		ie: oid="OID-1234"
				objAsStringVal = node.asString();
			} else {
				// 1.2) the object string representation was serialized with the type info
				//		ie: {
				//				typeId="testOid",
				//				value="OID-123
				//		    }
				//			or in xml: <oid typeId="myOidType">OID-1234</oid>
				ObjectNode objNode = (ObjectNode)node;

				Map<String,String> propValues = Maps.newLinkedHashMapWithExpectedSize(2);
				for (Iterator<Map.Entry<String,JsonNode>> fIt = objNode.properties().iterator(); fIt.hasNext(); ) {
					Map.Entry<String,JsonNode> me = fIt.next();
					propValues.put(me.getKey(),me.getValue().asString());
				}
				if (parser instanceof FromXmlParser) {
					// if serialized as xml, the node only contains the value without property name:  <rootName>value</rootName>
					objAsStringVal = propValues.get("");	// no property name
				} else {
					objAsStringVal = propValues.get(_valuePropName);
					if (objAsStringVal == null
					 && propValues.size() == 1) {
						objAsStringVal = propValues.values()
												   .iterator()
												   .next();
					}
				}
			}
			// [2] - Get the oid type
			if (objType == null) ctxt.reportInputMismatch(this,"Could NOT guess object type for value=%s",objAsStringVal);

			T outObj = _objFactory.create(objType,objAsStringVal); 	// ReflectionUtils.createInstanceFromString(objType,objAsString);

			return outObj;
		}
	}
}
