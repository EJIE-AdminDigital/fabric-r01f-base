package r01f.objectstreamer.annotationintrospector;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.types.JavaPackage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.TypeIdResolver;
import tools.jackson.databind.jsontype.TypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import tools.jackson.databind.type.SimpleType;

/**
 * An helper class that handles polymorphic types annotated with @MarshallPolymorphicTypeInfo
 */
@Slf4j
@RequiredArgsConstructor
class MarshallerPolymorphicTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// Type scanner
	private final ClassgraphScanHandler _typeScan;

	private final MarshallerPolymorphicTypesCache _typeCache;

	private final boolean _log = log.isTraceEnabled();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerPolymorphicTypes(final ClassgraphScanHandler typeScan) {
		_typeScan = typeScan;
		_typeCache = new MarshallerPolymorphicTypesCache(typeScan);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Given a type:
	 * 		1) find the nearest super-type annotated with @MarshallPolymorphicTypeInfo
	 * 		2) find the subtypes of the super-type annotated with @MarshallPolymorphicTypeInfo
	 * @param config
	 * @param ann
	 * @return
	 */
	public List<NamedType> findSubtypes(final MapperConfig<?> config,final Annotated ann) {
		// [0] - Find the type to be handled
		//		 (when collections, handle the collection elements type)
		Class<?> type = null;
		if (ann.getType() != null) {
			if (ann.getType().isCollectionLikeType() || ann.getType().isArrayType()) {
				type = ann.getType()
						  .getContentType()
						  .getRawClass();
			} else {
				type = ann.getType().getRawClass();
			}
		} else {
			type = ann.getRawType();
		}
		if (MarshallerAnnotationIntrospectorUtil.isNOTWithinAcceptedPackages(JavaPackage.of(type),
				 															 _typeScan.getAcceptedJavaPackages())) {
			return null;
		}

		List<NamedType> outTypes = _typeCache.getSubTypesAlongsideWithTypeIdOfSuperType(type);
		return outTypes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Polymorphic type id
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the polymorphic type id for a given subtype
	 * @param type
	 * @return
	 */
	public String findPolymorphicTypeIdFromType(final Class<?> type) {
		return _typeCache.getTypeIdFor(type);
	}
	public Class<?> findPolymorphicTypeFromTypeId(final String typeId) {
		Class<?> outTypeWithId = _typeCache.getTypeWithId(typeId);
		if (outTypeWithId == null) log.warn("<polymorphic resolution> could NOT resolve the type from the typeId={}",typeId);
		return outTypeWithId;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	polymorphic type info
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
	public JsonTypeInfo.Value findJsonTypeInfoFor(final Annotated ann) {
		// [1] - get the java type
		JavaType javaType = ann.getType();
		if (javaType == null) return null;

		// BEWARE GENERIC OBJECTS!!!
		// Due to type erasure, when dealing with generic object types like:
		//		@MarshallType(as="myGenericClass")
		//		public class MyGenericClass<T> {
		//			@MarshallField(as="aField")
		//			private T _aField;
		//		}
		// ... with field like _value that due to generics type erasure will be seen as Object.class
		//	   at runtime
		// ... so is MANDATORY to provide a JsonTypeInfo for Object.class (otherwise this method will return null and polymorphic deserialization will fail)
		if (javaType.getRawClass() == Object.class) {
			// BEWARE!! EDGE CASE for GENERIC TYPES:
			//		If MyGenericClass is parameterized with objects like Year / MonthOfYear / DayOfMonth... that are serialized as simple values (not objects) like:
			//		{
			//			"aField":"2026"
			//		}
			//		the JsonTypeInfo MUST NOT be included because since if so, the serialized format would be:
			//		{
			//			"aField": {
			//				"typeId":"year",
			//				"value": "2026"
			//			}
			//		}
			//		... and the ONLY way to know that is checking if the FIELD is annotated with
			//			@MarshallPolymorphicTypeInfo(includeTypeInfo==NEVER)
			MarshallPolymorphicTypeInfo marshallPolymorphicTypeInfo = ann.getAnnotation(MarshallPolymorphicTypeInfo.class);
			if (ann instanceof AnnotatedField
		     && marshallPolymorphicTypeInfo != null && marshallPolymorphicTypeInfo.includeTypeInfo() == MarshalTypeInfoIncludeCase.NEVER) {
					return null;	// NO type info
			}

			// in the usual case where MyGenericClass is parameterized with a "complex" object,
			// just return a DEFAULT JsonTypeInfo (there's no way to get JsonInfo from java.lang.Object)
			return JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME,
									 		    JsonTypeInfo.As.PROPERTY,
									 		    "typeId",	// cannot get the property name from anywhere... hardcode it
									 		    null,
									 		    false,
									 		    Boolean.FALSE);
		}

		// BEWARE!!! only abstract classes and interfaces can have JsonTypeInfo!!!
		if (javaType.isPrimitive()
	     || MarshallerAnnotationIntrospectorUtil.isNOTWithinAcceptedPackages(JavaPackage.of(javaType.getRawClass()),
				 															 _typeScan.getAcceptedJavaPackages())) return null;

		// [2] - Get the JsonTypeInfo FROM the MarshallPolymorphicTypeInfo annotation
		//		 at the supertype
		MarshallPolymorphicTypeInfo typeInfo = _typeCache.getMarshallPolymorphicTypeInfo(javaType.getRawClass());
		if (typeInfo == null) return null;
		JsonTypeInfo.Value jsonTypeInfoVal = _buildJsonTypeInfoFrom(typeInfo);

		// [3] - Check if the JsonTypeInfo is available
		//		 BEWARE!! this is important (ALX: do not remove this check!!... do not konow why but if removed polymorphic deserialization fails)
		boolean isJsonTypeInfoAvailable = _isJsonTypeInfoAvailableFor(javaType,typeInfo);
		if (!isJsonTypeInfoAvailable) jsonTypeInfoVal = null;	// not available

		// [99] - log & return
		if (_log) log.warn("<polymorphic resolution> resolved JsonTypeInfo for {}{} type={} > {}",
						   ann.getClass().getSimpleName(),(ann instanceof AnnotatedField annF ? "=" + annF.getName() : ""),
						   javaType,
						   jsonTypeInfoVal);
		return jsonTypeInfoVal;
	}
	private static boolean _isJsonTypeInfoAvailableFor(final JavaType javaType,
													   final MarshallPolymorphicTypeInfo typeInfo) {
		if (typeInfo == null) return false;

		if (typeInfo.includeTypeInfo() == MarshalTypeInfoIncludeCase.NEVER) return false;
		if (typeInfo.includeTypeInfo() == MarshalTypeInfoIncludeCase.ALWAYS) return true;
		if (typeInfo.includeTypeInfo() == MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE
		 && MarshallerAnnotationIntrospectorUtil.isNotInstanciable(javaType)) {
			return true;
		}
		return false;
	}
	private static JsonTypeInfo.Value _buildJsonTypeInfoFrom(final MarshallPolymorphicTypeInfo mpi) {
		// the property name in the serialized subtypes that identifies the concrete type (ie: typeId)
		String typeIdPropertyName = mpi.typeIdPropertyName();

		// is type info available when deserializing?
//		boolean typeInfoAvailableWhenDeserializing = mpi.typeInfoAvailableWhenDeserializing();

		// create JsonTypeInfo.Value
		return JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NAME,		// always use NAME as type identifier
																		//		- NONE
																		//		- NAME (the usual one) a logical type name (a type identifier like typeId) is used to identify the concrete type
																		//		- CLASS the full class name is used as type identifier
																		//		- SIMPLE_CLASS the simple class name is used as type identifier
																		//		- MINIMAL_CLASS the simple class name plus part of the package name is used as type identifier
																		//		- CUSTOM a custom type id resolver is used
								 		    JsonTypeInfo.As.PROPERTY,	// type info is included as a property of the serialized object (json/xml)
								 							 			// the property is DOES NOT an object's property!
								 		  								// (the property value WILL NOT be assigned to a DE-serialized object's property)
								 							 			//  ...it's just used to perform the polymorphic DE-serialization)
								 		    typeIdPropertyName,			// the property name that contains the type identifier
								 		    null,						// defaultImpl
								 		    false,						// - if this property is TRUE the [type identifier] VALUE will be passed
					 		  											//	 as part of the stream to the DE-serializer (the type that performs the stream deserialization)
																		// 	 ie: the typeId property is included in stream so the deserializer can see it
																		// - if this property is FALSE the [type identifier] VALUE will be handled INTERNALLY at the TypeDeserializer
					 		  											//	 (the class that identifies the concrete type) and will NOT visible by the DE-serializer
					 		  											// 	 (the type that performs the stream deserialization)
																		// 	 ie: the typeId property is NOT included in stream so the deserializer cannot see it)
								 		    Boolean.TRUE);				// always require type id for subtypes

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	Polymorphic Type resolver
/////////////////////////////////////////////////////////////////////////////////////////
	// ---------------------------------------------------------------
	// CACHE----------------------------------------------------------
	// ---------------------------------------------------------------
	// A cache of sub-types of MarshallPolymorphicType-Annotated types
	// 		Map<SuperType,List<SubTypes>>
	private final Map<Class<?>,Optional<TypeResolverBuilder<?>>> _typeResolverBuilderByJavaType = new ConcurrentHashMap<>();	// concurrent map that accepts null values

	/**
	 * If an abstract / interface java type, a type resolver is needed:
	 * 		a property that gives a hint about the "real" type is "injected" in the
	 * 		serialized format and used by the deserializer to create the "real" type instance
	 * @param config
	 * @param ann
	 * @return
	 */
	public TypeResolverBuilder<?> findTypeResolverBuilder(final MapperConfig<?> config,
														  final Annotated ann) {
		// [1] - get the java type
		JavaType javaType = ann.getType();
		if (javaType == null) return null;

		if (javaType.isPrimitive()
		 || javaType.isArrayType()
		 || MarshallerAnnotationIntrospectorUtil.isNOTWithinAcceptedPackages(JavaPackage.of(javaType.getRawClass()),
				 															 _typeScan.getAcceptedJavaPackages())) return null;	// primitives do NOT have type resolvers

		// [2] - try the cache
		Optional<TypeResolverBuilder<?>> cachedTypeResolverBuilder = _typeResolverBuilderByJavaType.get(javaType.getRawClass());
		TypeResolverBuilder<?> outTypeResolverBuilder = cachedTypeResolverBuilder != null ? cachedTypeResolverBuilder.orElse(null)
																				   		  : null;
		if (cachedTypeResolverBuilder != null) {
			if (_log) log.warn("<polymorphic resolution> (CACHED) resolver builder for java type={} > {} ",
							   javaType,outTypeResolverBuilder);
			return outTypeResolverBuilder;	// was in cache
		}

		// [3] - Create the TypeResolverBuilder
		//		 BEWARE!!! only abstract classes and interfaces can have type id resolver!!!
		if (MarshallerAnnotationIntrospectorUtil.isNotInstanciable(javaType)) {
			JsonTypeInfo.Value jsonTypeInfo = this.findJsonTypeInfoFor(ann);	// only for not instanciable types
			outTypeResolverBuilder = jsonTypeInfo != null ? new MarshallerTypeResolverBuilder(jsonTypeInfo)
														  : null;
		}
		if (_log) log.warn("<polymorphic resolution> resolver builder for java type={} > {}",
						   javaType,outTypeResolverBuilder);
		// [5] - cache
		_typeResolverBuilderByJavaType.put(javaType.getRawClass(),Optional.ofNullable(outTypeResolverBuilder));

		// [99] - return
		return outTypeResolverBuilder;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public class MarshallerTypeResolverBuilder
		 extends StdTypeResolverBuilder {

	    public MarshallerTypeResolverBuilder(final JsonTypeInfo.Value settings) {
	    	super(settings);
	    }
		@Override
		protected TypeIdResolver idResolver(final DatabindContext ctxt,
											final JavaType baseType,
											final PolymorphicTypeValidator subtypeValidator,final Collection<NamedType> subtypes,
											final boolean forSer,final boolean forDeser) {
			// always use a custom type id resolver
			return new MarshallerTypeResolver(baseType.getRawClass());
		}
	}
	@RequiredArgsConstructor(access= AccessLevel.PRIVATE)
	public class MarshallerTypeResolver
		 extends TypeIdResolverBase {

		private static final long serialVersionUID = 4269801490230132450L;

		private final Class<?> _baseType;

	    @Override
	    public JavaType typeFromId(final DatabindContext ctxt,final String id)  throws JacksonException {
	    	// Find a concrete type extending the base type for the provided type id
	    	// TO-BE-INVESTIGATED (not important):	this should work
	    	//		Class<?> concreteTypeFromId = MarshallerPolymorphicTypes.objTypeFromId(ctxt,
	    	//	    																	   _baseType,id)
	    	Class<?> concreteTypeFromId = MarshallerPolymorphicTypes.this.findPolymorphicTypeFromTypeId(id);
	    	if (_log) log.warn("<polymorphic resolution> Type FROM TypeId: baseType={} > id={} -> concreteType={}",
	    					   _baseType,id,concreteTypeFromId);
	    	return concreteTypeFromId != null ? SimpleType.constructUnsafe(concreteTypeFromId) : null;
	    }
		@Override
		public String idFromValue(final DatabindContext ctxt,final Object value) throws JacksonException {
			String outId = value != null ? MarshallerPolymorphicTypes.this.findPolymorphicTypeIdFromType(value.getClass())
										 : null;
			if (_log) log.warn("<polymorphic resolution> TypeId FROM Value: baseType={} > value={} -> id={}",
							   _baseType,value,outId);
			return outId;
		}
		@Override
		public String idFromValueAndType(final DatabindContext ctxt,final Object value,final Class<?> suggestedType) throws JacksonException {
			String outId = MarshallerPolymorphicTypes.this.findPolymorphicTypeIdFromType(value.getClass());
			if (outId == null) outId = MarshallerPolymorphicTypes.this.findPolymorphicTypeIdFromType(suggestedType);
			if (_log) log.warn("<polymorphic resolution> TypeId FROM Value and SuggestedType: baseType={} > value={} (suggestedType={}) -> id={}",
							   _baseType,value,suggestedType,outId);
			return outId;
		}
		@Override
		public Id getMechanism() {
			return JsonTypeInfo.Id.CUSTOM;
		}
	}
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> objTypeFromId(final DatabindContext ctxt,
										 			   final Class<T> baseType,final String typeId) {
		// find the base type
		AnnotatedClass annotatedClass = AnnotatedClassResolver.resolveWithoutSuperTypes(ctxt.getConfig(),
																						baseType);
		// find the base type subtypes
		List<NamedType> subtypes = ctxt.getAnnotationIntrospector()
									   .findSubtypes(ctxt.getConfig(),	// this should call MarshallerPolymorphicTypeInfoAnnotationIntrospector.findSubtypes()
													 annotatedClass);	// that uses a CACHE to speed up things
		// find the type for the provided type id
		Class<?> outType = null;
		for (NamedType type: subtypes) {
			if (type.getName().equals(typeId)) {
				outType = type.getType();
			}
		}
		return (Class<? extends T>)outType;
	}

}
