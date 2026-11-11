package r01f.objectstreamer.custom;

import java.lang.annotation.AnnotationFormatError;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForCanBeRepresentedAsString.CanBeRepresentedAsStringDeserializerBase;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForCanBeRepresentedAsString.CanBeRepresentedAsStringSerializerBase;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForCanBeRepresentedAsString.PolymorphicCanBeRepresentedAsStringDeserializerBase;
import r01f.objectstreamer.custom.MarshallerCustomStreamersForCanBeRepresentedAsString.PolymorphicCanBeRepresentedAsStringSerializerBase;
import r01f.oids.OID;
import r01f.patterns.FactoryFromType;
import r01f.reflection.ReflectionUtils;
import r01f.types.CanBeRepresentedAsString;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.cfg.MapperConfigBase;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;

@Slf4j
@NoArgsConstructor(access= lombok.AccessLevel.PRIVATE)
public abstract class MarshallerCustomStreamers {
/////////////////////////////////////////////////////////////////////////////////////////
//	UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	static String typeRootName(final MapperConfigBase<?,?> cfg,
							   final Class<?> type) {
		AnnotatedClass objTypeAnnotated = AnnotatedClassResolver.resolveWithoutSuperTypes(cfg,
																					  	  type);
		if (!objTypeAnnotated.hasAnnotation(MarshallType.class)) throw new AnnotationFormatError(String.format("Type %s does NOT have the @%s annotation",
																											   type,MarshallType.class.getSimpleName()));
		return objTypeAnnotated.getAnnotation(MarshallType.class).as();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class CanBeRepresentedAsStringSerializer<T extends CanBeRepresentedAsString>
				extends CanBeRepresentedAsStringSerializerBase<T,CanBeRepresentedAsStringSerializer<T>> {
		public CanBeRepresentedAsStringSerializer(final Class<T> oidType) {
			super(oidType);
		}
		public CanBeRepresentedAsStringSerializer(final Class<T> objType,
							 					  final BeanProperty property) {
			super(objType,
				  property);
		}
		@Override
		protected CanBeRepresentedAsStringSerializer<T> cloneFrom(final Class<T> objType,final BeanProperty property) {
			return new CanBeRepresentedAsStringSerializer<T>(objType,property);
		}
	}
	public static class CanBeRepresentedAsStringDeserializer<T extends CanBeRepresentedAsString>
		 		extends CanBeRepresentedAsStringDeserializerBase<T,CanBeRepresentedAsStringDeserializer<T>> {

		public CanBeRepresentedAsStringDeserializer(final Class<T> objType) {
			super(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  });
		}
		public CanBeRepresentedAsStringDeserializer(final Class<T> objType,final FactoryFromType<T> objFactory,
							   						final BeanProperty property) {
			super(objType,objFactory,
				  property);
		}
		@Override
		protected CanBeRepresentedAsStringDeserializer<T> cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property) {
			return new CanBeRepresentedAsStringDeserializer<T>(objType,objFactory,property);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PolymorphicCanBeRepresentedAsString SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class PolymorphicCanBeRepresentedAsStringSerializer<T extends CanBeRepresentedAsString>
				extends PolymorphicCanBeRepresentedAsStringSerializerBase<T,PolymorphicCanBeRepresentedAsStringSerializer<T>> {
		public PolymorphicCanBeRepresentedAsStringSerializer(final Class<T> oidType) {
			this(oidType,
				 "value");
		}
		public PolymorphicCanBeRepresentedAsStringSerializer(final Class<T> oidType,
															 final String valuePropName) {
			super(oidType,
				  valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringSerializer(final Class<T> objType,
							 					  			 final BeanProperty property,
							 					  			 final String valuePropName) {
			super(objType,
				  property,
				  valuePropName);
		}
		@Override
		protected PolymorphicCanBeRepresentedAsStringSerializer<T> cloneFrom(final Class<T> objType,final BeanProperty property) {
			return new PolymorphicCanBeRepresentedAsStringSerializer<T>(objType,property,
																		_valuePropName);
		}
	}
	public static class PolymorphicCanBeRepresentedAsStringDeserializer<T extends CanBeRepresentedAsString>
		 		extends PolymorphicCanBeRepresentedAsStringDeserializerBase<T,PolymorphicCanBeRepresentedAsStringDeserializer<T>> {
		public PolymorphicCanBeRepresentedAsStringDeserializer(final Class<T> objType) {
			this(objType,
				 "value");
		}
		public PolymorphicCanBeRepresentedAsStringDeserializer(final Class<T> objType,
															   final String valuePropName) {
			super(objType,new FactoryFromType<T>() {
									@Override
									public T create(final Class<? extends T> type,final Object... args) {
										return ReflectionUtils.<T>createInstanceFromString(type,(String)args[0]);
									}
						  },
				 valuePropName);
		}
		public PolymorphicCanBeRepresentedAsStringDeserializer(final Class<T> objType,final FactoryFromType<T> objFactory,
							   								   final BeanProperty property,
							   								   final String valuePropName) {
			super(objType,objFactory,
				  property,
				  valuePropName);
		}
		@Override
		protected PolymorphicCanBeRepresentedAsStringDeserializer<T> cloneFrom(final Class<T> objType,final FactoryFromType<T> objFactory,final BeanProperty property) {
			return new PolymorphicCanBeRepresentedAsStringDeserializer<T>(objType,objFactory,property,
																		  _valuePropName);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class OIDSerializer<O extends OID>
				extends PolymorphicCanBeRepresentedAsStringSerializerBase<O,OIDSerializer<O>> {
		public OIDSerializer(final Class<O> oidType) {
			super(oidType,
				  "value");
		}
		public OIDSerializer(final Class<O> oidType,
							 final BeanProperty property) {
			super(oidType,
				  property,
				  "value");
		}
		@Override
		protected OIDSerializer<O> cloneFrom(final Class<O> objType,final BeanProperty property) {
			return new OIDSerializer<O>(objType,property);
		}
	}
	public static class OIDDeSerializer<O extends OID>
		 		extends PolymorphicCanBeRepresentedAsStringDeserializerBase<O,OIDDeSerializer<O>> {

		public OIDDeSerializer(final Class<O> oidType) {
			super(oidType,
				 // factory from
				 (type,args) -> ReflectionUtils.<O>createInstanceFromString(type,(String)args[0]),
				 // value prop name
				 "value");
		}
		public OIDDeSerializer(final Class<O> oidType,final FactoryFromType<O> objFactory,
							   final BeanProperty property) {
			super(oidType,objFactory,
				  property,
				  "value");
		}
		@Override
		protected OIDDeSerializer<O> cloneFrom(final Class<O> objType,final FactoryFromType<O> objFactory,final BeanProperty property) {
			return new OIDDeSerializer<O>(objType,objFactory,property);
		}
	}
}
