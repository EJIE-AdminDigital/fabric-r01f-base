package r01f.model.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.common.reflect.TypeToken.TypeSet;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.reflection.scanner.ScannerForAnnotatedTypes;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Accessors(prefix="_")
public class TypeMetaDataInspector
  implements HasTypesMetaData {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	TypeMetaDataInspector() {
		// nothing
	}
	@SuppressWarnings("unused")
	private TypeMetaDataInspector(final AppCode appCode) {
		this(JavaPackage.of(appCode));
	}
	private TypeMetaDataInspector(final JavaPackage pckg) {
		this();
		this.findModelObjectDataAnnotatedTypesAt(pckg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SINGLETON
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private static enum TypeMetaDataInspectoringletonHolder {
		SINGLETON;

		@Getter private final TypeMetaDataInspector _instance;

		TypeMetaDataInspectoringletonHolder() {
			_instance = new TypeMetaDataInspector();
		}
	}
	/**
	 * @return the {@link TypeMetaDataInspector} singleton instance
	 */
	public static TypeMetaDataInspector singleton() {
		return TypeMetaDataInspectoringletonHolder.SINGLETON.getInstance();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ConcurrentMap<Class<? extends MetaDataDescribable>,TypeMetaData<? extends MetaDataDescribable>> _inspectedTypes = Maps.newConcurrentMap();

	@Getter private final Map<JavaPackage,Boolean> _initialized = Maps.newHashMapWithExpectedSize(2);

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds all @ModelObjectData annotated types at package {appCode}.model.*
	 * for each found type, the {@link #inspect(Class)} method is called
	 * A CACHE is used to avoid inspecting types all over again
	 *
	 * BEWARE! all types annotated with @ModelObjectData MUST implement {@link MetaDataDescribable}
	 * @param appCode
	 */
	public synchronized void findModelObjectDataAnnotatedTypesFor(final AppCode appCode) {
		this.findModelObjectDataAnnotatedTypesAt(JavaPackage.of(appCode));
	}
	/**
	 * Finds all @ModelObjectData annotated types at package {appCode}.model.*
	 * for each found type, the {@link #inspect(Class)} method is called
	 * A CACHE is used to avoid inspecting types all over again
	 *
	 * BEWARE! all types annotated with @ModelObjectData MUST implement {@link MetaDataDescribable}
	 * @param appCode
	 */
	@SuppressWarnings("unchecked")
	public synchronized void findModelObjectDataAnnotatedTypesAt(final JavaPackage pckg) {
		log.info("Finding @{} annotated types at {} java package",
				 ModelObjectData.class.getSimpleName(),pckg);

		// [0] - Avoid duplicate initialization
		Boolean initialized = _initialized.get(pckg);
		if (initialized != null
		&& initialized == true) {
			log.info("The [type metadata inspector] was already initialized! for {}",pckg);
//			throw new IllegalStateException("The inspector was already initialized! for " + apiAppAndModule);
		}

		// [1] - Find every type annotated with @ModelObjectData
		ClassgraphScanHandler scanHandler = new ClassgraphScanHandler(Lists.newArrayList(pckg));
		ScannerForAnnotatedTypes annotatedTypesScanner = new ScannerForAnnotatedTypes(scanHandler);
		Set<Class<?>> modelObjTypes = annotatedTypesScanner.findTypesAnnotated(ModelObjectData.class);

		// [2] - For every found type, look at the @ModelObjectData annotation and load the ModelObjectMetaData
		if (CollectionUtils.hasData(modelObjTypes)) {
			for (Class<?> modelObjType : modelObjTypes) {
				// if (ReflectionUtils.isAbstract(modelObjType)) continue;		// skip base types??
				if (!ReflectionUtils.isImplementing(modelObjType,MetaDataDescribable.class)) {
					log.warn("Type {} is annotated with @{} BUT it's NOT implementing {}",
							 modelObjType,ModelObjectData.class,MetaDataDescribable.class);
				}
				this.inspect((Class<? extends MetaDataDescribable>)modelObjType);
			}
		} else {
			log.warn("BEWARE!! Could NOT find any @{} annotated types at {} java package",
					 ModelObjectData.class.getSimpleName(),pckg);
		}

		// [3] - The inspector is initialized
		_initialized.put(pckg,true);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public <M extends MetaDataDescribable> boolean existsTypeMetaDataFor(final Class<M> hasMetaData) {
		TypeMetaData<M> outTypeMetaData = this.inspect(hasMetaData);
		return outTypeMetaData != null;
	}
	@Override
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final Class<M> hasMetaData) {
		TypeMetaData<M> outTypeMetaData = this.inspect(hasMetaData);
		if (outTypeMetaData == null) {
//			log.warn("There's NO type metadata info for type {}; returning metadata info for {}",
//					 hasMetaData,
//					 HasMetaDataForPersistableModelObject.class.getSimpleName());
//			outTypeMetaData = this.inspect(HasMetaDataForPersistableModelObject.class);
//			throw new IllegalStateException("There's NO type metadata info for type " + hasMetaData + " (maybe the " + hasMetaData + " object might implement " + CanBeRepresentedAsString.class + ")");
		}
		return outTypeMetaData;
	}
	@Override @SuppressWarnings("unchecked")
	public <M extends MetaDataDescribable> TypeMetaData<M> getTypeMetaDataFor(final long typeCode) {
		TypeMetaData<M> outTypeMetaData = null;
		for (TypeMetaData<? extends MetaDataDescribable> typeMetaData : _inspectedTypes.values()) {
			if (typeMetaData.getTypeMetaData().modelObjTypeCode() == typeCode) {
				outTypeMetaData = (TypeMetaData<M>)typeMetaData;
				break;
			}
		}
		return outTypeMetaData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inspects all {@link HasMetaDataForModelObject} types returning a {@link TypeMetaData} object that encapsulates
	 * the metadata info about the model object type
	 * The model object type (or one of the types in it's hierarchy) MUST be annotated with @ModelObjectData
	 * setting the type that holds the meta-data about the type
	 * <pre class='brush:java'>
	 * 		@ModelObjectData(MyBusinessObjecTypeMetaData.class)
	 * 		@Accessors(prefix="_")
	 * 		public class MyBusinessObjectType
	 * 		  implements ModelObject {		// extends MetaDataDescribable
	 * 			@Getter @Setter private MyOID _oid;
	 * 			@Getter @Setter private MyOtherModelObject _myOtherField;
	 * 		}
	 * </pre>
	 * The type that holds the metadata is roughly (not mandatory) similar to the model object
	 * BUT with meta-data information
	 * <pre class='brush:java'>
	 * 		@MetaDataForType(modelObjTypeCode = 100,
	 *						 description = {
	 *							@DescInLang(language=Language.SPANISH,value="My model object type"),
	 *							@DescInLang(language=Language.BASQUE,value="[eu] my model object type"),
	 *							@DescInLang(language=Language.ENGLISH,value="My model object type")
	 *						 })
	 * 		public class MyBusinessObjectType
	 * 		  implements HasModelObjectMetaData {
	 *				@MetaDataForField(description = {
	 *									@DescInLang(language=Language.SPANISH,value="Unique identifier"),
	 *									@DescInLang(language=Language.BASQUE,value="[eu] Unique identifier"),
	 *									@DescInLang(language=Language.ENGLISH,value="Unique identifier")
	 *								 },
	 *			   					 storage = @Storage(indexed=false))
	 * 				private MyOID _oid;
	 *
	 *				@MetaDataForField(description = {
	 *									@DescInLang(language=Language.SPANISH,value="Other field"),
	 *									@DescInLang(language=Language.BASQUE,value="[eu] Other field"),
	 *									@DescInLang(language=Language.ENGLISH,value="Other field")
	 *								 },
	 *			   					 storage = @Storage(indexed=false))
	 * 				private MyOtherModelObject _myOtherField;
	 * 		}
	 * </pre>
	 *
	 * @param type
	 * @return
	 */
	public <M extends MetaDataDescribable> TypeMetaData<M> inspect(final Class<M> type) {
		return _inspect(null,
						type);
	}
	@SuppressWarnings("unchecked")
	private <M extends MetaDataDescribable> TypeMetaData<M> _inspect(final FieldID fieldId,
								  									 final Class<M> type) {
		// [0] - check the cache
		TypeMetaData<M> outMetaData = (TypeMetaData<M>)_inspectedTypes.get(type);
		if (outMetaData != null) return outMetaData;

		// [1] - Build the type hierarchy outline
		TypeOutline typeOutline = TypeOutline.from(type);
		Collection<Class<?>> flatHierarchy = typeOutline.getNodesFromSpezializedToGeneralization();

		// [2] - find the @ModelObjectData on the given type
		ModelObjectData typeMetaDataRefAnnot = _findModelObjectMetaDataAnnot(flatHierarchy);
		if (typeMetaDataRefAnnot == null) {
			log.warn("The {} model object type or a type in it's hierarchy (super types or implemented interfaces) MUST be annotated with {} specifying the model object's metadata",
					 type,ModelObjectData.class);
			return null;
		}

		// [2] - Build the type metadata info using the @MetaDataForType annotation on the
		// 		 type containing the metadata
		Class<? extends HasMetaDataForModelObject> hasMetaDataType = typeMetaDataRefAnnot.value();
		MetaDataForType typeMetaDataAnnot = hasMetaDataType.getAnnotation(MetaDataForType.class);
//		if (typeMetaDataAnnot == null) throw new IllegalStateException(String.format("%s is supposed to contain metadata about %s BUT it's not annotated with %s",
//																					 hasMetaDataType,type,
//																					 MetaDataForType.class));

		// [3] - Build the facets inspecting the hierarchy recursively
		// 3.1 - Filter all types annotated with @ModelObjectData up in the hierarchy of the given type
		Set<Class<?>> facetTypes = flatHierarchy.stream()
												.filter(superType -> type != superType	//  beware to not include the type: stack overflow!!
																  && ReflectionUtils.typeAnnotation(superType,
																									ModelObjectData.class) != null)
												.collect(Collectors.toSet());

		// 3.2 - Transform the Set<Class<?>> of type facets to Set<TypeMetaData> recursively
		//		 calling inspect(...)
		Set<TypeMetaData<? extends MetaDataDescribable>> facetsTypeMetaData = facetTypes.stream()
																				   		.map(superType -> TypeMetaDataInspector.this.inspect((Class<? extends MetaDataDescribable>)superType))
																				   		.collect(Collectors.toSet());

		TypeMetaData<M> typeMetaData = new TypeMetaData<>(type,					// the metadata-described model object type
														  TypeToken.of(hasMetaDataType),		// the annotated type that contains the model object's metadata
														  typeMetaDataAnnot,		// the annotation info
														  facetsTypeMetaData);		// the type facets' metadata

		// [3] - Introspect fields of the type containing the metadata
		//		 (consider all fields, including those in the type hierarchy)
		@SuppressWarnings("rawtypes")
		TypeSet hasMetaDataTypeSet = TypeToken.of(hasMetaDataType)
											  .getTypes();
		Iterator<TypeToken<?>> typeTokenIt = hasMetaDataTypeSet.iterator();
		for (; typeTokenIt.hasNext(); ) {
			TypeToken<?> hasMetaDataTypeToken = typeTokenIt.next();

			// 3.1 - Fields
			Field[] fields = hasMetaDataTypeToken.getRawType()
												 .getDeclaredFields();
			Collection<TypeFieldMetaData> nodeFieldsMetaData = Stream.of(fields)
																	 // filter fields annotated with @MetaDataForField
																	 .filter(FIELD_ANNOTATED_FILTER)
																	 // transform to TypeFieldMetaData
																	 .map(field -> {
																				FieldMetaDataAnnotated<Field> an = new FieldMetaDataAnnotated<>(hasMetaDataTypeToken,
																																				field);
																				return _fieldMetaDataFor(fieldId,		// the type that contains the type being inspected
																										 typeMetaData,	// metadata about the type that contains the field
																										 hasMetaDataTypeToken,an);		// the meta-data type containing the field + field
																		  })
																	 .collect(Collectors.toList());
			if (CollectionUtils.hasData(nodeFieldsMetaData)) typeMetaData.getFieldsMetaData()
																		 .addAll(nodeFieldsMetaData);
			// 3.2 - Getter methods (used at interfaces)
			boolean isIface = ReflectionUtils.isInterface(hasMetaDataTypeToken.getRawType());
			boolean isAbstract = ReflectionUtils.isAbstract(hasMetaDataTypeToken.getRawType());
			if (!isIface && !isAbstract) continue;

			Method[] methods = hasMetaDataTypeToken.getRawType()
												   .getDeclaredMethods();
			Collection<TypeFieldMetaData> nodeMethodsMetaData = Stream.of(methods)
																	 // filter methods annotated with @MetaDataForField
																	 .filter(METHOD_ANNOTATED_FILTER)
																	 // transform to TypeFieldMetaData
																	 .map(method -> {
																				FieldMetaDataAnnotated<Method> an = new FieldMetaDataAnnotated<>(hasMetaDataTypeToken,
																																				 method);
																				return _fieldMetaDataFor(fieldId,		// the field that contains the type being inspected
																										 typeMetaData,	// metadata about the type that contains the field
																										 hasMetaDataTypeToken,an);		// the meta-data type containing the field + field
																		   })
																	 .collect(Collectors.toList());
			if (CollectionUtils.hasData(nodeMethodsMetaData)) typeMetaData.getFieldsMetaData()
																		  .addAll(nodeMethodsMetaData);

		}	// for

		// [4] - Cache
		_inspectedTypes.putIfAbsent(type,typeMetaData);

		// [5] - Return
		return typeMetaData;
	}
	@SuppressWarnings("unchecked")
	private TypeFieldMetaData _fieldMetaDataFor(final FieldID parentFieldId,		// recursive calls only: the field that contains a MetaDataDescribable object one of whose fields is being processed
												final TypeMetaData<? extends MetaDataDescribable> typeMetaData,
												final TypeToken<?> fieldContainerMetaDataType,final FieldMetaDataAnnotated<?> field) {
		// [1] - field id
		//			Patterns:
		//				- id={fieldId} 				> fields directly defined at the type that contains the meta-data
		//											  or at an r01 meta-data container type for a facet (ie: HasMetaDataForHasOIDModelObject)
		//				- id={facetId}.{fieldId}	> fields defined at a meta-data container type for a facet
		FieldID id = null;
//		MetaDataForType fieldContainerMetaDataTypeAnnot = fieldContainerMetaDataType.getRawType()
//																					.getAnnotation(MetaDataForType.class);
//		if (fieldContainerMetaDataTypeAnnot == null) throw new IllegalStateException(String.format("%s MUST be annotated with @%s",
//																								   fieldContainerMetaDataType,MetaDataForType.class.getSimpleName()));
		if (parentFieldId != null) {
			// it's a recursive call (a MetaDataDescribable field of another MetaDataDescribable object)
			id = FieldID.forId(Strings.customized("{}.{}",
												  parentFieldId,
												  field.getId()));
		} else {
			id = FieldID.forId(Strings.customized("{}",
												  field.getId()));
		}
		// [2] - field type
		Type fieldType = field.getType();
		TypeMetaData<? extends MetaDataDescribable> fieldTypeMetaData = fieldType instanceof Class
																			? ReflectionUtils.isImplementing((Class<?>)fieldType,MetaDataDescribable.class)
																						? _inspect(id,
																								   (Class<? extends MetaDataDescribable>)fieldType)		// BEWARE!! recursive call
																						: null
																			: null;
		// [3] - Return
		return new TypeFieldMetaData(typeMetaData,
									 id,
									 field.getMetadataForFieldAnnotation(),
									 fieldType,
									 fieldTypeMetaData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	WRAPS A Field or Method
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private class FieldMetaDataAnnotated<T> {
		@Getter private final TypeToken<?> _containerType;
		@Getter private final T _annotatedElement;	// can be either a Field or a getter Method
		@Getter private final MetaDataForField _metadataForFieldAnnotation;

		public FieldMetaDataAnnotated(final TypeToken<?> containerType,final T annotated) {
			_containerType = containerType;
			_annotatedElement = annotated;
			if (annotated instanceof Field) {
				_metadataForFieldAnnotation = ((Field)annotated).getAnnotation(MetaDataForField.class);
			}
			else if (annotated instanceof Method) {
				_metadataForFieldAnnotation = ((Method)annotated).getAnnotation(MetaDataForField.class);
			}
			else {
				throw new IllegalArgumentException(String.format("%s is NOT a %s or %s",
																 annotated.getClass(),
																 Field.class,Method.class));
			}
		}
		public String getId() {
			String outId = null;
			if (_annotatedElement instanceof Field) {
				String fieldName = ((Field)_annotatedElement).getName();
				outId = fieldName.startsWith("_") ? fieldName.substring(1) : fieldName;
			}
			else if (_annotatedElement instanceof Method) {
				String fieldName = null;
				String methodName = ((Method)_annotatedElement).getName();
				if (methodName.startsWith("get")) {
					fieldName = methodName.substring("get".length());
				} else if (methodName.startsWith("is")) {
					fieldName = methodName.substring("is".length());
				} else {
					throw new IllegalStateException(String.format("%s is NOT a valid method name",methodName));
				}
				// ensure the first letter is lower-case
				int strLen = fieldName.length();
				outId = new StringBuilder(strLen)
								.append(Character.toLowerCase(fieldName.charAt(0)))
								.append(fieldName.substring(1))
								.toString();
			}
			return outId;
		}
		public Type getType() {
			Type outType = null;

			if (_annotatedElement instanceof Field field) {
				if (field.getType() == Class.class) {
					outType = Class.class;
				} else {
					outType = _containerType.resolveType(field.getGenericType()).getType();
				}
			}
			else if (_annotatedElement instanceof Method method) {
				outType = _containerType.resolveType(method.getGenericReturnType()).getType();
			}
//			System.out.println(">>>> " + this.getId() + ": " + _containerType + " > " + outType);
			return outType;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ANNOTATION INSTROSPECTION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the @ModelObjectData annotation in a type's hierarchy
	 * @param typeOutline
	 * @return
	 */
	private static ModelObjectData _findModelObjectMetaDataAnnot(final Collection<Class<?>> flatHierarchy) {
		// recursively finds a ModelObjectData annotation:
		//		- If the object is NOT annotated with ModelObjectData annotation, all it's directly implemented interfaces are checked
		//		- ... if the ModelObjectData annotation is NOT found, it's super-type is checked recursively
		ModelObjectData outModelObjectData = null;
		for (Class<?> hierarchyType : flatHierarchy) {
			if (hierarchyType.isAnnotationPresent(ModelObjectData.class)) {
				outModelObjectData = hierarchyType.getAnnotation(ModelObjectData.class);
				break;
			}
		}
		return outModelObjectData;
	}
	public static final Predicate<Field> FIELD_ANNOTATED_FILTER = field -> field.getAnnotation(MetaDataForField.class) != null;
	public static final Predicate<Method> METHOD_ANNOTATED_FILTER = method -> {
																		boolean isGetter = (method.getName().startsWith("get") && method.getName().length() > "get".length())
																						|| (method.getName().startsWith("is") && method.getName().length() > "is".length());
																		boolean isAnnotated = method.getAnnotation(MetaDataForField.class) != null;
																		return isGetter && isAnnotated;
																	};
}
