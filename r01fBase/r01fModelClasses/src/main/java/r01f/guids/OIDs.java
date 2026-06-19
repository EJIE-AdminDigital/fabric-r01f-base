package r01f.guids;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.annotations.OidField;
import r01f.exceptions.Throwables;
import r01f.facets.HasID;
import r01f.facets.HasOID;
import r01f.model.ModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable.HasVersionableFacet;
import r01f.model.metadata.FieldMetaDataForOID;
import r01f.model.metadata.HasMetaDataForHasIDModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.patterns.FactoryFrom;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	SUPPLY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Generates a GUID
	 * @return
	 */
	public static String supplyOid() {
		return JavaOIDDispenser.supplyOid();
	}
	/**
	 * Generates a short GUID
	 * @return
	 */
	public static String supplyShortOid() {
		return JavaOIDDispenser.supplyShortOid();
	}
	/**
	 * Invokes the supply() method to generate a new oid
	 * @param oidType
	 * @return
	 */
	public static <O extends OID> O supplyOid(final Class<O> oidType) {
		O oid = ReflectionUtils.<O>invokeStaticMethod(oidType,
									   			   	  OID.STATIC_SUPPLIER_METHOD_NAME,
									   			   	  new Class<?>[] {},new Object[] {});
		return oid;
	}
	/**
	 * Returns the OID's fragment at the given position
	 * @param <O>
	 * @param oid
	 * @param pos
	 * @return
	 */
	public static <O extends OID> String fragmentAt(final O oid,final int pos) {
		return JavaOIDDispenser.fragmentAt(oid.asString(),pos);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZATION
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "unchecked","null" })
	public static <O extends OID> Collection<String> toStringCollection(final O... oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return Lists.newArrayList();
		return OIDs.toStringCollection(Lists.newArrayList(oids));
	}
	public static <O extends OID> Collection<String> toStringCollection(final Collection<O> oids) {
		if (CollectionUtils.isNullOrEmpty(oids)) return Lists.newArrayList();
		return oids.stream()
				   .map(OID::asString)
				   .collect(Collectors.toList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	SET OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the oid in a {@link HasOID} object
	 * @param hasOid
	 * @param oidType
	 * @param serializedOid
	 */
	public static void setOidFromSerializedFormat(final HasOID<? extends OID> hasOid,
												  final Class<? extends OID> oidType,
												  final String serializedOid) {
		OID oid = ReflectionUtils.invokeStaticMethod(oidType,
									   			     OID.STATIC_FACTORY_METHOD_NAME,
									   			     new Class<?>[] {String.class},new Object[] {serializedOid});
		hasOid.unsafeSetOid(oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	// CACHE: Oid type by model object type
	private static Cache<Class<? extends ModelObject>,Class<? extends OID>> MODEL_OBJS_OID_TYPES =  CacheBuilder.newBuilder()
																											    .softValues()
																											    .maximumSize(200)
																											    .build();
	/**
	 * Returns the model object's oid type
	 * @param modelObjType
	 * @return
	 * @throws IllegalStateException if the type does NOT have a oid field
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeFor(final Class<M> modelObjType) {
		return OIDs.oidTypeOrThrowFor(modelObjType);
	}
	/**
	 * Returns the model object's oid type
	 * @param modelObjType
	 * @return
	 * @throws IllegalStateException if the type does NOT have a oid field
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrThrowFor(final Class<M> modelObjType) {
		return _oidTypeFor(modelObjType,
						   true);	// throw an exception
	}
	/**
	 * Returns the model object's oid type
	 * @param modelObjType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrDefaultFor(final Class<M> modelObjType,
																					 final Class<O> defType) {
		Class<O> outOidType = _oidTypeFor(modelObjType,
						   				  false);	// do not throw an exception
		return outOidType != null ? outOidType
								  : defType;
	}
	/**
	 * Returns the model object's oid type
	 * @param modelObjType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> oidTypeOrNullFor(final Class<M> modelObjType) {
		return OIDs.oidTypeOrDefaultFor(modelObjType,
										null);	// default value
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _oidTypeFor(final Class<M> modelObjType,
																			  final boolean strict) {
		// An oid type by model object type cache is used to avoid excessive Reflection usage
		Class<O> oidType = (Class<O>)MODEL_OBJS_OID_TYPES.getIfPresent(modelObjType);

		// Try to guess the oid type
		if (oidType == null) {
			// a) use the type info
			oidType = _guessOidTypeFromTypeParameter(modelObjType);
		}
		if (oidType == null) {
			// b) use the model object metadata
			oidType = _guessOidTypeFromModelObjectMetaData(modelObjType);
		}
		if (oidType == null) {
			// c) use an annotated field
			oidType = _guessOidTypeFromAnnotatedField(modelObjType);
		}

		// If the oid type was NOT found:
		if (strict && oidType == null) throw new IllegalStateException(Throwables.message("Could NOT guess the oid type for the {} type: either the {} field metadata was NOT present, neither the type has a @{} annotated field",
							 								   		   					  modelObjType,
							 								   		   					  HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID.getToken(),
							 								   		   					  OidField.class.getSimpleName()));

		// Put the oid type in the cache
		if (oidType != null) MODEL_OBJS_OID_TYPES.put(modelObjType,oidType);
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromAnnotatedField(final Class<M> modelObjType) {
		Class<O> oidType = null;
		FieldAnnotated<OidField>[] oidFieldsAnnotated = ReflectionUtils.fieldsAnnotated(modelObjType,OidField.class);
		if (CollectionUtils.isNullOrEmpty(oidFieldsAnnotated)) return null;
		if (oidFieldsAnnotated.length > 1) throw new IllegalStateException(Throwables.message("The {} type does have more than a singe field annotated with {}",
																							  modelObjType,OidField.class));
		Field oidField = oidFieldsAnnotated[0].getField();
		oidType = (Class<O>)ReflectionUtils.fieldType(modelObjType,oidField);
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromModelObjectMetaData(final Class<M> modelObjType) {
		Class<O> oidType = null;
		TypeMetaData<M> metaData = TypeMetaDataInspector.singleton()
													 	.getTypeMetaDataFor(modelObjType);
		if (metaData == null) throw new IllegalStateException(String.format("Could NOT find the type meta data for %s",modelObjType));
		FieldMetaDataForOID oidFieldMetaData = metaData.findFieldByIdOrThrow(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID)
															.asFieldMetaData();
		if (oidFieldMetaData != null) {
			oidType = (Class<O>)oidFieldMetaData.getDataType();
		}
		return oidType;
	}
	@SuppressWarnings("unchecked")
	private static <O extends OID,M extends ModelObject> Class<O> _guessOidTypeFromTypeParameter(final Class<M> modelObjType) {
		if (!ReflectionUtils.isImplementing(modelObjType,HasOID.class)) return null;

		// find a guava's TypeToken for the interface implementing HasOID
		TypeToken<? super M> tt = TypeToken.of(modelObjType)
										   // get all interfaces
										   .getTypes()
										   .interfaces()
										   // filter the one implementing HasOID
										   .stream()
										   .filter(ifaceType -> ifaceType.getRawType() == HasOID.class)
										   // pick the first :-(
										   .findFirst().orElse(null);
		// resolve the type parameter (ie O at HasOID<O>) at the TypeToken
		Class<?> theOidType = tt != null ? tt.resolveType(HasOID.class.getTypeParameters()[0])
											 .getRawType()
							 			 : null;
		return (Class<O>)theOidType;

		// the java's way of doing it > BUT it only works if the superclass is the OID-parametrized one!!!!
		/*
	    Type superclassType = modelObjType.getGenericSuperclass();
	    if (superclassType == null || !ParameterizedType.class.isAssignableFrom(superclassType.getClass())) return null;
	    oidType = (Class<O>)((ParameterizedType)modelObjType.getGenericSuperclass()).getActualTypeArguments()[0];
	    */
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	IDs
/////////////////////////////////////////////////////////////////////////////////////////
	// CACHE: Oid type by model object type
	private static Cache<Class<? extends ModelObject>,Class<? extends OID>> MODEL_OBJS_ID_TYPES =  CacheBuilder.newBuilder()
																											   .softValues()
																											   .maximumSize(200)
																											   .build();
	/**
	 * Returns the model object's id type
	 * @param modelObjType
	 * @return
	 * @throws IllegalStateException if the type does NOT have an id field
	 */
	public static <O extends OID,M extends ModelObject> Class<O> idTypeFor(final Class<M> modelObjType) {
		return OIDs.idTypeOrThrowFor(modelObjType);
	}
	/**
	 * Returns the model object's id type
	 * @param modelObjType
	 * @return
	 * @throws IllegalStateException if the type does NOT have an id field
	 */
	public static <O extends OID,M extends ModelObject> Class<O> idTypeOrThrowFor(final Class<M> modelObjType) {
		return _idTypeFor(modelObjType,
						  true);	// throw an exception
	}
	/**
	 * Returns the model object's id type
	 * @param modelObjType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> idTypeOrDefaultFor(final Class<M> modelObjType,
																					final Class<O> defType) {
		Class<O> outOidType = _idTypeFor(modelObjType,
						   				 false);	// do not throw an exception
		return outOidType != null ? outOidType
								  : defType;
	}
	/**
	 * Returns the model object's oid type
	 * @param modelObjType
	 * @param defType the default type
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> Class<O> idTypeOrNullFor(final Class<M> modelObjType) {
		return OIDs.idTypeOrDefaultFor(modelObjType,
									   null);	// default value
	}
	@SuppressWarnings("unchecked")
	private static <I extends OID,M extends ModelObject> Class<I> _idTypeFor(final Class<M> modelObjType,
																			  final boolean strict) {
		// An oid type by model object type cache is used to avoid excessive Reflection usage
		Class<I> idType = (Class<I>)MODEL_OBJS_ID_TYPES.getIfPresent(modelObjType);

		// Try to guess the oid type
		if (idType == null) {
			// a) use the type info
			idType = _guessIdTypeFromTypeParameter(modelObjType);
		}
		if (idType == null) {
			// b) use the model object metadata
			idType = _guessIdTypeFromModelObjectMetaData(modelObjType);
		}
		// If the oid type was NOT found:
		if (strict && idType == null) throw new IllegalStateException(Throwables.message("Could NOT guess the id type for the {} type: either the {} field metadata was NOT present",
							 								   		   					  modelObjType,
							 								   		   					  HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID.getToken()));

		// Put the oid type in the cache
		if (idType != null) MODEL_OBJS_ID_TYPES.put(modelObjType,idType);
		return idType;
	}
	@SuppressWarnings("unchecked")
	private static <I extends OID,M extends ModelObject> Class<I> _guessIdTypeFromModelObjectMetaData(final Class<M> modelObjType) {
		Class<I> idType = null;
		TypeMetaData<M> metaData = TypeMetaDataInspector.singleton()
													 	.getTypeMetaDataFor(modelObjType);
		if (metaData == null) throw new IllegalStateException(String.format("Could NOT find the type meta data for %s",modelObjType));
		FieldMetaDataForOID idFieldMetaData = metaData.findFieldByIdOrThrow(HasMetaDataForHasIDModelObject.SEARCHABLE_METADATA.ID)
													  .asFieldMetaData();
		if (idFieldMetaData != null) {
			idType = (Class<I>)idFieldMetaData.getDataType();
		}
		return idType;
	}
	@SuppressWarnings("unchecked")
	private static <I extends OID,M extends ModelObject> Class<I> _guessIdTypeFromTypeParameter(final Class<M> modelObjType) {
		if (!ReflectionUtils.isImplementing(modelObjType,HasID.class)) return null;

		// find a guava's TypeToken for the interface implementing HasOID
		TypeToken<? super M> tt = TypeToken.of(modelObjType)
										   // get all interfaces
										   .getTypes()
										   .interfaces()
										   // filter the one implementing HasOID
										   .stream()
										   .filter(ifaceType -> ifaceType.getRawType() == HasID.class)
										   // pick the first :-(
										   .findFirst().orElse(null);
		// resolve the type parameter (ie O at HasOID<O>) at the TypeToken
		Class<?> theIdType = tt != null ? tt.resolveType(HasID.class.getTypeParameters()[0])
											.getRawType()
							 			: null;
		return (Class<I>)theIdType;

		// the java's way of doing it > BUT it only works if the superclass is the OID-parametrized one!!!!
		/*
	    Type superclassType = modelObjType.getGenericSuperclass();
	    if (superclassType == null || !ParameterizedType.class.isAssignableFrom(superclassType.getClass())) return null;
	    oidType = (Class<O>)((ParameterizedType)modelObjType.getGenericSuperclass()).getActualTypeArguments()[0];
	    */
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OID FOR MODEL OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an OID for a given model object
	 * @param <O>
	 * @param <M>
	 * @param entityType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID,M extends ModelObject> O createOIDFor(final Class<M> entityType,
																	   final String oidAsString) {
		Class<O> oidType = OIDs.oidTypeFor(entityType);
		return OIDs.createOIDFromString(oidType,oidAsString);
	}
	/**
	 * Creates an {@link OIDForVersionableModelObject} from the oid and version date
	 * @param entityType
	 * @param oid
	 * @param date
	 * @return
	 */
	public static <O extends OIDForVersionableModelObject & PersistableObjectOID,M extends PersistableModelObject<O> & HasVersionableFacet>
				  O createOIDForVersionableModelObject(final Class<M> entityType,
													   final VersionIndependentOID oid,final VersionOID version) {
		// [1] - Guess the oid type
		Class<O> oidType = _guessOidTypeFromModelObjectMetaData(entityType);


		// [2] - Use the static valueOf method of the OID type
		O outOid = ReflectionUtils.<O>invokeStaticMethod(oidType,
													  	 "valueOf",
													  	 new Class<?>[] {String.class,String.class},
													  	 new Object[] {oid.asString(),version.asString()});
		return outOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OID FROM STRING
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an OID from it's serialized as {@link String} value
	 * @param oidType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID> O createOIDFromString(final String oidAsString,
														final FactoryFrom<String,O> oidFactory) {
		return OIDFromString.createOIDFromString(oidAsString,
												 oidFactory);
	}
	/**
	 * Creates an OID factory from an String that uses reflection to create the OID instance
	 * @param <O>
	 * @param oidType
	 * @return
	 */
	public static <O extends OID> FactoryFrom<String,O> createOIDFactoryFromStringFor(final Class<O> oidType) {
		return OIDFromString.createOIDFactoryFromStringFor(oidType);
	}
	/**
	 * Creates an OID from it's serialized as {@link String} value
	 * @param oidType
	 * @param oidAsString
	 * @return
	 */
	public static <O extends OID> O createOIDFromString(final Class<O> oidType,
														final String oidAsString) {
		return OIDs.createOIDFactoryFromStringFor(oidType)
				   .from(oidAsString);
	}
	/**
	 * Creates a versionable OID from it's serialized as {@link String}s value
	 * @param oidType
	 * @param versionIndependentOidAsString
	 * @param versionOidAsString
	 * @return
	 */
	public static <O extends OID> O createVersionableOIDFromString(final Class<O> oidType,
																   final String versionIndependentOidAsString,
																   final String versionOidAsString) {
		return OIDFromString.createVersionableOIDFromString(oidType,
															versionIndependentOidAsString,versionOidAsString);
	}
}
