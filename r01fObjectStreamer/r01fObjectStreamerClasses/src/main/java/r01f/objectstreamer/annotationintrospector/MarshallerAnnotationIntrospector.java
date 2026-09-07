package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotationintrospector.MarshallerAnnotationIntrospectorUtil.MarshallerNameFor;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.util.types.Strings;
import tools.jackson.core.Version;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.annotation.JsonSerialize.Typing;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedConstructor;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.introspect.ObjectIdInfo;
import tools.jackson.databind.introspect.PotentialCreator;
import tools.jackson.databind.introspect.VisibilityChecker;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.util.NameTransformer;

/**
 * Annotation introspector
 *
 * The jackson annotation introspector type hierarchy is like:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[JacksonAnnotationIntrospector]			<-- this is where jackson json annotations are detected
 * 			|		|--[JacksonXmlAnnotationIntrospector] 	<-- this is where jackson xml annotations are detected
 * 			|--[JakartaXmlBindAnnotationIntrospector]	<-- this is where jakarta xml annotations are detected
 * 			|
 * 			|--[AnnotationIntrospectorPair]		<-- combine two AnnotationIntrospector instances so Jackson will consult one and then the other when inspecting annotations
 * </pre>
 *
 * Here, delegation is used instead inheritance:
 * <pre>
 * 		[AnnotationIntrospector]
 * 			|--[MarshallerAnnotationIntrospector]
 * 						|--(delegates)-> [JacksonAnnotationIntrospector]
 * <pre>
 */
@Slf4j
public class MarshallerAnnotationIntrospector
	 extends AnnotationIntrospector {

	private static final long serialVersionUID = -8142099488664415041L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// polymorphic types handler
	protected final MarshallerPolymorphicTypes _polymorphicTypes;

	// delegated jackson introspector
	protected final JacksonAnnotationIntrospector _delegatedJacksonAnnotationIntrospector;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public MarshallerAnnotationIntrospector(final ClassgraphScanHandler typeScan) {
		_polymorphicTypes = new MarshallerPolymorphicTypes(typeScan);
		_delegatedJacksonAnnotationIntrospector = new JacksonAnnotationIntrospector();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Version version() {
		return MarshallerPackageVersion.VERSION; // VersionUtil.versionFor(MarshallerAnnotationIntrospector.class);
	}
	@Override
	public boolean isAnnotationBundle(final Annotation ann) {
		return _delegatedJacksonAnnotationIntrospector.isAnnotationBundle(ann);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OBJECT ID
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectIdInfo findObjectIdInfo(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findObjectIdInfo(config,ann);
	}
	@Override
	public ObjectIdInfo findObjectReferenceInfo(final MapperConfig<?> config,final Annotated ann,
												final ObjectIdInfo objectIdInfo) {
		return _delegatedJacksonAnnotationIntrospector.findObjectReferenceInfo(config,ann,
																			   objectIdInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CLASS / TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String findClassDescription(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findClassDescription(config,ac);
	}
	@Override
	public PropertyName findRootName(final MapperConfig<?> config,final AnnotatedClass ac) {
		return MarshallerAnnotationIntrospectorUtil.findName(config,ac,
						 							 		 MarshallerNameFor.TYPE_ROOT,
						 							 		 _delegatedJacksonAnnotationIntrospector);
	}
	@Override
	public Object findNamingStrategy(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findNamingStrategy(config,ac);
	}
	@Override
	public Object findEnumNamingStrategy(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findEnumNamingStrategy(config,ac);
	}
	@Override
	public Boolean isIgnorableType(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.isIgnorableType(config,ac);
	}
    @Override
	public JsonIgnoreProperties.Value findPropertyIgnoralByName(final MapperConfig<?> config,final Annotated ann) {
    	return _delegatedJacksonAnnotationIntrospector.findPropertyIgnoralByName(config,ann);
    }
    @Override
	public JsonIncludeProperties.Value findPropertyInclusionByName(final MapperConfig<?> config,final Annotated ann) {
    	return _delegatedJacksonAnnotationIntrospector.findPropertyInclusionByName(config,ann);
    }
	@Override
	public Object findFilterId(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findFilterId(config,ann);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTY AUTO DETECTION
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public VisibilityChecker findAutoDetectVisibility(final MapperConfig<?> config,final AnnotatedClass ac,
    												  final VisibilityChecker checker) {
        return _delegatedJacksonAnnotationIntrospector.findAutoDetectVisibility(config,ac,
        																		checker);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	ANNOTATIONS FOR POLYMORPHIC TYPE HANDLING
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public JsonTypeInfo.Value findPolymorphicTypeInfo(final MapperConfig<?> config,final Annotated ann) {
		JsonTypeInfo.Value jsonTypeInfo = _polymorphicTypes.findJsonTypeInfoFor(ann);
		return jsonTypeInfo != null ? jsonTypeInfo
									: _delegatedJacksonAnnotationIntrospector.findPolymorphicTypeInfo(config,ann);
	}
	@Override
	public List<NamedType> findSubtypes(final MapperConfig<?> config,final Annotated ann) {
		List<NamedType> outTypes = _polymorphicTypes.findSubtypes(config,ann);
		
		return outTypes != null ? outTypes
								: _delegatedJacksonAnnotationIntrospector.findSubtypes(config,ann);	// if not found, delegate
	}
	@Override
	public String findTypeName(final MapperConfig<?> config,final AnnotatedClass ac) {
		String typeId = _polymorphicTypes.findPolymorphicTypeIdFromType(ac.getRawType());
		
		return Strings.isNOTNullOrEmpty(typeId) ? typeId
												: _delegatedJacksonAnnotationIntrospector.findTypeName(config,ac);
	}
	@Override
	public Boolean isTypeId(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.isTypeId(config,am);
	}
    @Override
	public Object findTypeIdResolver(final MapperConfig<?> config,final Annotated ann) {
    	return _delegatedJacksonAnnotationIntrospector.findTypeIdResolver(config,ann);	// finds the annotation @JsonTypeIdResolver
    }
	@Override
	public Object findTypeResolverBuilder(final MapperConfig<?> config,final Annotated ann) {
		Object outTypeResolverBuilder = _polymorphicTypes.findTypeResolverBuilder(config,ann);
		return outTypeResolverBuilder != null ? outTypeResolverBuilder
											  :	_delegatedJacksonAnnotationIntrospector.findTypeResolverBuilder(config,ann);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	General member (field, method/constructor) annotations
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String findImplicitPropertyName(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.findImplicitPropertyName(config,am);
	}
    @Override
	public PropertyName findRenameByField(final MapperConfig<?> config,final AnnotatedField af,
    									  final PropertyName implName) {
    	return _delegatedJacksonAnnotationIntrospector.findRenameByField(config,af,
    																	 implName);
    }
	@Override
	public String findPropertyDescription(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyDescription(config,ann);
	}
	@Override
	public Access findPropertyAccess(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyAccess(config,ann);
	}
	@Override
	public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config,
												 final AnnotatedMethod setter1,final AnnotatedMethod setter2) {
		return _delegatedJacksonAnnotationIntrospector.resolveSetterConflict(config,
																			 setter1,setter2);
	}
	@Override
	public Integer findPropertyIndex(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyIndex(config,ann);
	}
	@Override
	public ReferenceProperty findReferenceType(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.findReferenceType(config,am);
	}
	@Override
	public boolean hasIgnoreMarker(final MapperConfig<?> config,final AnnotatedMember am) {
		// [0] - only check fields!
		if (!(am instanceof AnnotatedField) ) return false; // _delegatedJacksonAnnotationIntrospector.hasIgnoreMarker(am);

		boolean isIgnorable = false;

		// [1] - if transient field, ignore
		AnnotatedField field = (AnnotatedField)am;
		isIgnorable = Modifier.isTransient(field.getModifiers());

		// [2] - check @FieldMarshallIgnored annotation
		if (isIgnorable == false) isIgnorable = am.hasAnnotation(MarshallIgnoredField.class);
		//		 ... or the default Jackson annotations
		if (isIgnorable == false) isIgnorable = _delegatedJacksonAnnotationIntrospector.hasIgnoreMarker(config,am);


		return isIgnorable;
	}
	@Override
	public Boolean hasRequiredMarker(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.hasRequiredMarker(config,am);
	}
	@Override
	public String findPropertyDefaultValue(final MapperConfig<?> config,final Annotated ann) {
		if (!ann.hasAnnotation(MarshallField.class)) return _delegatedJacksonAnnotationIntrospector.findPropertyDefaultValue(config,ann);	// not annotated

		// Do NOT mind annotated getters
		if (!(ann instanceof AnnotatedField annF) ) return null;

		// has MarshallField annotation
		MarshallField mf = ann.getAnnotation(MarshallField.class);
		String outDefaultValue = mf.defaultValue();
		return Strings.isNOTNullOrEmpty(outDefaultValue) ? outDefaultValue
														 : _delegatedJacksonAnnotationIntrospector.findPropertyDefaultValue(config,ann);
	}
	@Override
	public JacksonInject.Value findInjectableValue(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.findInjectableValue(config,am);
	}
	@Override
	public Class<?>[] findViews(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findViews(config,ann);
	}
	@Override
	public PropertyName findWrapperName(final MapperConfig<?> config, final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findWrapperName(config,ann);
	}
	@Override
	public NameTransformer findUnwrappingNameTransformer(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.findUnwrappingNameTransformer(config,am);
	}
////////// Format
	@Override
	public JsonFormat.Value findFormat(final MapperConfig<?> config,final Annotated ann) {
		if (!ann.hasAnnotation(MarshallField.class)) return _delegatedJacksonAnnotationIntrospector.findFormat(config,ann);	// not annotated

		// Do NOT mind annotated getters
		if (!(ann instanceof AnnotatedField annF) ) return null;

		// has MarshallField annotation
		MarshallField mf = ann.getAnnotation(MarshallField.class);

		// Check MarshallField annotation
		// Find the format
		JsonFormat.Value outFormat = null;
		// a date
		if (mf.dateFormat() != null) {
			// direct date
			if (MarshallerAnnotationIntrospectorUtil.isDateType(annF.getRawType())) {
				outFormat = MarshallerAnnotationIntrospectorUtil.dateFormatOf(annF,mf.dateFormat());
			}
			// maybe a collection of Dates
			else if (MarshallerAnnotationIntrospectorUtil.isCollectionOrArrayType(annF.getRawType())) {
				// due to type erasure the Collection type cannot be guessed
				outFormat = MarshallerAnnotationIntrospectorUtil.dateFormatOf(annF,mf.dateFormat());
			}
		}
		// Return
		JsonFormat.Value delegatedFormat = _delegatedJacksonAnnotationIntrospector.findFormat(config,ann);
		if (outFormat != null && delegatedFormat != null) {
			outFormat = delegatedFormat.withOverrides(delegatedFormat);	// merge both
		} else if (outFormat == null) {
			// just the delegated one
			outFormat = delegatedFormat;
		}
		return outFormat;

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Object findSerializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializer(config,ann);
	}
	@Override
	public Object findKeySerializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findKeySerializer(config,ann);
	}
	@Override
	public Object findContentSerializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findContentSerializer(config,ann);
	}
	@Override
	public Object findNullSerializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findNullSerializer(config,ann);
	}
	@Override
	public Typing findSerializationTyping(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationTyping(config,ann);
	}
	@Override
	public Object findSerializationConverter(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationConverter(config,ann);
	}
	@Override
	public Object findSerializationContentConverter(final MapperConfig<?> config,final AnnotatedMember ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationContentConverter(config,ann);
	}
	@Override
	public JsonInclude.Value findPropertyInclusion(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findPropertyInclusion(config,ann);
	}
////////// Serialization: type refinements
	@Override
	public JavaType refineSerializationType(final MapperConfig<?> config,final Annotated ann,
											final JavaType baseType) {
		return _delegatedJacksonAnnotationIntrospector.refineSerializationType(config,ann,
																			   baseType);
	}
////////// Class annotations
	@Override
	public String[] findSerializationPropertyOrder(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationPropertyOrder(config,ac);
	}
	@Override
	public Boolean findSerializationSortAlphabetically(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSerializationSortAlphabetically(config,ann);
	}
	@Override
	public void findAndAddVirtualProperties(final MapperConfig<?> config,final AnnotatedClass ac,
											final List<BeanPropertyWriter> properties) {
		_delegatedJacksonAnnotationIntrospector.findAndAddVirtualProperties(config,ac,
																			properties);
	}
	@Override
	public Boolean hasAsKey(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.hasAsKey(config,ann);
	}
	@Override
	public Boolean hasAsValue(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.hasAsValue(config,ann);
	}
	@Override
	public Boolean hasAnyGetter(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.hasAnyGetter(config,ann);
	}
	@Override
    public String[] findEnumValues(final MapperConfig<?> config,final AnnotatedClass annotatedClass,
            					   final Enum<?>[] enumValues,final String[] names) {
		return _delegatedJacksonAnnotationIntrospector.findEnumValues(config,annotatedClass,
																	  enumValues,names);
	}
    @Override
	public void findEnumAliases(final MapperConfig<?> config, final AnnotatedClass annotatedClass,
            					final Enum<?>[] enumValues,final String[][] aliases) {
		_delegatedJacksonAnnotationIntrospector.findEnumAliases(config,annotatedClass,
																enumValues, aliases);
	}
    @Override
	public Enum<?> findDefaultEnumValue(final MapperConfig<?> config,final AnnotatedClass ac,
    									final Enum<?>[] enumValues) {
    	return _delegatedJacksonAnnotationIntrospector.findDefaultEnumValue(config,ac,
    																		enumValues);
    }
////////// Property annotations
	@Override
	public PropertyName findNameForSerialization(final MapperConfig<?> config,final Annotated ann) {
		return MarshallerAnnotationIntrospectorUtil.findName(config,ann,
						 							 		 MarshallerNameFor.PROPERTY_SERIALIZATION,
						 							 		 _delegatedJacksonAnnotationIntrospector);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Object findDeserializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializer(config,ann);
	}
	@Override
	public Object findKeyDeserializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findKeyDeserializer(config,ann);
	}
	@Override
	public Object findContentDeserializer(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findContentDeserializer(config,ann);
	}
	@Override
	public Object findDeserializationConverter(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationConverter(config,ann);
	}
	@Override
	public Object findDeserializationContentConverter(final MapperConfig<?> config,final AnnotatedMember am) {
		return _delegatedJacksonAnnotationIntrospector.findDeserializationContentConverter(config,am);
	}
	@Override
	public JavaType refineDeserializationType(final MapperConfig<?> config,final Annotated ann,
											  final JavaType baseType) {
		return _delegatedJacksonAnnotationIntrospector.refineDeserializationType(config,
																				 ann,baseType);
	}
////////// Values instantiation / creators
	@Override
	public Mode findCreatorAnnotation(final MapperConfig<?> config,final Annotated ann) {
		// If any constructor param is annotated with @MarshallFrom() then
		// the constructor should be annotated with @JsonCreator
		// ... but this annotation can be AVOIDED
		//
		//		@Marshall(as="myType")
		// 		public class MyType {
		//			private final String _myProp;
		//
		//			@JsonCreator	<---- Avoid having to set this annotation
		//			public MyType(@MarshallFrom("myProp") String myProp) {
		//				_myProp = myProp;
		//			}
		// If ALL the constructor params are annotated with @MarshallFrom, just simulate the @JsonCreator behavior

		Mode outMode = null;
		if (ann instanceof AnnotatedConstructor annConstructor) {
			boolean allParamsAnnotatedWithMarshallFrom = true;
			for (int i=0; i < annConstructor.getParameterCount(); i++) {
				AnnotatedParameter annParam = annConstructor.getParameter(i);
				if (annParam.getAnnotation(MarshallFrom.class) == null) {
					allParamsAnnotatedWithMarshallFrom = false;	// not all params are annotated with @MarshallFrom
					break;
				}
			}
			outMode = allParamsAnnotatedWithMarshallFrom ? Mode.PROPERTIES : null;
		}
		// if outMode == null just delegate
		return outMode != null ? outMode
							   : _delegatedJacksonAnnotationIntrospector.findCreatorAnnotation(config,ann);
	}
	@Override
	public PotentialCreator findPreferredCreator(final MapperConfig<?> config,final AnnotatedClass ac,
            									 final List<PotentialCreator> declaredConstructors,final List<PotentialCreator> declaredFactories,
            									 final Optional<PotentialCreator> zeroParamsConstructor) {
		return _delegatedJacksonAnnotationIntrospector.findPreferredCreator(config,ac,
																			declaredConstructors,declaredFactories,
																			zeroParamsConstructor);
	}
	@Override
	public Object findValueInstantiator(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findValueInstantiator(config,ac);
	}
	@Override
	public Class<?> findPOJOBuilder(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findPOJOBuilder(config,ac);
	}
	@Override
	public JsonPOJOBuilder.Value findPOJOBuilderConfig(final MapperConfig<?> config,final AnnotatedClass ac) {
		return _delegatedJacksonAnnotationIntrospector.findPOJOBuilderConfig(config,ac);
	}
////////// Property annotations
	@Override
	public PropertyName findNameForDeserialization(final MapperConfig<?> config,final Annotated ann) {
		return MarshallerAnnotationIntrospectorUtil.findName(config,ann,
						 							 		 MarshallerAnnotationIntrospectorUtil.MarshallerNameFor.PROPERTY_DESERIALIZATION,
						 							 		 _delegatedJacksonAnnotationIntrospector);
	}
	@Override
	public Boolean hasAnySetter(final MapperConfig<?> config,final Annotated a) {
		return _delegatedJacksonAnnotationIntrospector.hasAnySetter(config,a);
	}
	@Override
	public JsonSetter.Value findSetterInfo(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findSetterInfo(config,ann);
	}
	@Override
	public Boolean findMergeInfo(final MapperConfig<?> config,final Annotated ann) {
		return _delegatedJacksonAnnotationIntrospector.findMergeInfo(config,ann);
	}
}
