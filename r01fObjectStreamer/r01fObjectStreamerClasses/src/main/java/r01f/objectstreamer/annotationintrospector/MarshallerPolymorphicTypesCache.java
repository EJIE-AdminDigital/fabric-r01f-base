package r01f.objectstreamer.annotationintrospector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.reflection.scanner.ClassgraphScanHandler;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import tools.jackson.databind.jsontype.NamedType;

@Slf4j
class MarshallerPolymorphicTypesCache {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// Type scanner
	private final ClassgraphScanHandler _typeScan;

	private final boolean _log = log.isTraceEnabled();
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	MarshallerPolymorphicTypesCache(final ClassgraphScanHandler typeScan) {
		_typeScan = typeScan;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A cache of polymorphic types found
	 * 	- Key: the CONCRETE type
	 * 	- Value: a record that contains
	 * 				- the CONCRETE type
	 * 				- the nearest SUPER-TYPE annotated with @MarshallPolymorphicTypeInfo
	 * The cache is filled on demand when a type is requested
	 *
	 * BEWARE that when a type to be cached, it's nearest super-type annotated with @MarshallPolymorphicTypeInfo is found
	 * 		  and ALL its subtypes are cached at once (including the requested type)
	 */
	private final Map<Class<?>,MarshallerPolymorphicTypeCacheItem<?>> _cache = new ConcurrentHashMap<>();
	/**
	 * A cache of subtypes found for each super type annotated with @MarshallPolymorphicTypeInfo
	 */
	private final Map<Class<?>,Set<Class<?>>> _subTypesBySuperTypeCache = new ConcurrentHashMap<>();
/////////////////////////////////////////////////////////////////////////////////////////
//	FIND METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the type with the given type id
	 * BEWARE!! the type MUST be cached BEFORE requesting it by typeId
	 * @param typeId
	 * @return
	 */
	public Class<?> getTypeWithId(final String typeId) {
		if (_log) log.warn("--------cache.getTypeWithId: {}",typeId);
		Collection<Class<?>> typesWithTypeId = _cache.values()
													 .stream()
													 .filter(item -> item.typeId().equals(typeId))
													 .map(item -> item.type())
													 .collect(Collectors.toList());
		// BEWARE!! the type MUST be cached BEFORE requesting it by typeId
		if (typesWithTypeId.size() > 1) throw new IllegalStateException("More than one type found for typeId=" + typeId + ": " + typesWithTypeId);
		Class<?> outTypeWithId = CollectionUtils.hasData(typesWithTypeId) ? typesWithTypeId.iterator().next()
																		  : null;
		return outTypeWithId;
	}
	/**
	 * Finds the typeId for the given type
	 * (if the type is not cached, it is cached now)
	 * @param type
	 * @return
	 */
	public String getTypeIdFor(final Class<?> type) {
		if (_log) log.warn("--------cache.getTypeIdFor: {}",type);
		MarshallerPolymorphicTypeCacheItem<?> cacheItem = _findOrCacheItemFor(type);	// if not in cache, cache it now
		return cacheItem != null ? cacheItem.typeId()
								 : null;
	}
	/**
	 * Gets the @MarshallPolymorphicTypeInfo annotation from the given type's nearest super type annotated with it
	 * (if the type is not cached, it is cached now)
	 * @param type
	 * @return
	 */
	public MarshallPolymorphicTypeInfo getMarshallPolymorphicTypeInfo(final Class<?> type) {
		if (_log) log.warn("--------cache.getMarshallPolymorphicTypeInfo: {}",type);

		// maybe the type itself is annotated with @MarshallPolymorphicTypeInfo
		MarshallPolymorphicTypeInfo self = type.getAnnotation(MarshallPolymorphicTypeInfo.class);
		if (self != null) return self;

		// if not, find the nearest super type annotated with @MarshallPolymorphicTypeInfo
		MarshallerPolymorphicTypeCacheItem<?> cacheItem = _findOrCacheItemFor(type);
		return cacheItem != null ? cacheItem.getMarshallPolymorphicTypeInfo()
								 : null;
	}
	/**
	 * Finds all the subtypes of the given super type alongside with their typeId
	 * @param <T>
	 * @param superType
	 * @return
	 */
	public <T> List<NamedType> getSubTypesAlongsideWithTypeIdOfSuperType(final Class<? super T> superType) {
		if (_log) log.warn("--------cache.getMarshallPolymorphicTypeInfo: " + superType);

		// [1] - ensure the subtypes of the super type are cached
		Set<Class<?>> subTypes = _subTypesBySuperTypeCache.get(superType);
		if (subTypes == null) subTypes = _findAndCacheAllSubtypesOfMarshallPolymorphicTypeInfoAnnotatedSuperType(superType);

		// [2] - build the output list
		//		 BEWARE! at this pint the subtypes are cached
		List<NamedType> outSubTypes = _cache.values()
											.stream()
										    .filter(item -> item.superTypeEquals(superType))
										    .map(item -> new NamedType(item.type(),
																	   item.typeId()))
										    .collect(Collectors.toList());
		return outSubTypes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private <T> MarshallerPolymorphicTypeCacheItem<T> _findOrCacheItemFor(final Class<T> type) {
		MarshallerPolymorphicTypeCacheItem<T> outItem = null;

		// [1] - find the cache item for the type
		outItem = (MarshallerPolymorphicTypeCacheItem<T>)_cache.get(type);

		// [2] - not cached: cache it now
		if (outItem == null) outItem = _cache(type);

		return outItem;
	}
	/**
	 * Caches the given type with the following steps:
	 * 	1 - find the nearest super type annotated with @MarshallPolymorphicTypeInfo
	 *  2 - find and cache all subtypes of that super type
	 *
	 * BEWARE that this method NOT ONLY caches the given type but ALL subtypes of the nearest super type annotated with @MarshallPolymorphicTypeInfo
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> MarshallerPolymorphicTypeCacheItem<T> _cache(final Class<T> type) {
		if (_log) log.warn("<polymorphic sub-type find> START CACHING type={}",
						   type);
		// do not process types that are collections/arrays or not within accepted packages
		if (MarshallerAnnotationIntrospectorUtil.isCollectionOrArrayType(type)
		 || MarshallerAnnotationIntrospectorUtil.isNOTWithinAcceptedPackages(JavaPackage.of(type),
				 															 _typeScan.getAcceptedJavaPackages())) {
			return null;
		}
		// [1] - find the nearest super type annotated with @MarshallPolymorphicTypeInfo
		Class<? super T> superTypeAnnotatedWithMarshallPolymorphicTypeInfo = MarshallerAnnotationUtils.findSuperTypeAnnotatedWithMarshallPolymorphicTypeInfoFor(type);

		// a bit of logging
		if (_log) log.warn("<polymorphic sub-type find> nearest super type of {} annotated with @{}: {}",
		  				   type,MarshallPolymorphicTypeInfo.class.getSimpleName(),
		  				   superTypeAnnotatedWithMarshallPolymorphicTypeInfo);

		// [2] - if found, find and cache all subtypes of that super type
		if (superTypeAnnotatedWithMarshallPolymorphicTypeInfo != null) {
			_findAndCacheAllSubtypesOfMarshallPolymorphicTypeInfoAnnotatedSuperType(superTypeAnnotatedWithMarshallPolymorphicTypeInfo);
		}

		// [99] - Return
		return (MarshallerPolymorphicTypeCacheItem<T>)_cache.get(type);
	}
	/**
	 * Find all subtypes of a super type annotated with @MarshallPolymorphicTypeInfo and cache them
	 * @param superType
	 */
	@SuppressWarnings("unchecked")
	private <S> Set<Class<?>> _findAndCacheAllSubtypesOfMarshallPolymorphicTypeInfoAnnotatedSuperType(final Class<S> superTypeAnnotatedWithMarshallPolymorphicTypeInfo) {
		// [0] - maybe already cached
		if (_subTypesBySuperTypeCache.containsKey(superTypeAnnotatedWithMarshallPolymorphicTypeInfo)) return _subTypesBySuperTypeCache.get(superTypeAnnotatedWithMarshallPolymorphicTypeInfo);	// already cached

		// [1] - Find subtypes of the type annotated with @MarshallPolymorphicTypeInfo
		//		 Warning: Extending or Implementing && just instanciable subtypes
		Set<Class<?>> subTypes = _typeScan.scanForTypesExtendingOrImplementing(superTypeAnnotatedWithMarshallPolymorphicTypeInfo)
										  .stream()
										  //.filter(ReflectionUtils::isInstanciable)	// BEWARE!! cache abstract subtypes too
										  .collect(Collectors.toUnmodifiableSet());
		// [2] - For each subtype, find its typeId and cache it
		if (CollectionUtils.hasData(subTypes)) {
			// [2.1] - Cache each subtype
			for (Class<?> subType : subTypes) {
				// a) Maybe the subType was already cached
				if (_cache.containsKey(subType)) continue;	// already cached

				// b) find the typeId from the @TypeMarshall annotation or @JsonRootName
				String typeId = MarshallerAnnotationUtils.findPolymorphicTypeIdFor(subType);
				if (Strings.isNullOrEmpty(typeId)) continue;	// skip types with no type id

				// c) create the cache item
				MarshallerPolymorphicTypeCacheItem<?> subTypeCacheItem = MarshallerPolymorphicTypeCacheItem.createFor((Class<? extends S>)subType,typeId,
																											   		  superTypeAnnotatedWithMarshallPolymorphicTypeInfo);
				_cache.put(subType,subTypeCacheItem);
			}
			// [2.2] - cache the found subtypes for the super type
			_subTypesBySuperTypeCache.put(superTypeAnnotatedWithMarshallPolymorphicTypeInfo,subTypes);

			// [2.3] - log found subtypes
			if (_log) log.warn("<polymorphic sub-type find> subtypes of  @{}-annotated type={}:\n{}",
							   MarshallPolymorphicTypeInfo.class.getSimpleName(),superTypeAnnotatedWithMarshallPolymorphicTypeInfo,
			  				   subTypes.stream()
			  				   		   .map(t -> String.format("\t-%s",t))
			  				   		   .collect(Collectors.joining("\n")));
		} else {
			// [2.1] - no subtypes found: cache empty set
			_subTypesBySuperTypeCache.put(superTypeAnnotatedWithMarshallPolymorphicTypeInfo,Collections.EMPTY_SET);

			if (_log) log.warn("<polymorphic sub-type find> NO subtypes found for @{}-annotated type={}: This could be a error.....",
					  				   MarshallPolymorphicTypeInfo.class.getSimpleName(),superTypeAnnotatedWithMarshallPolymorphicTypeInfo);
		}
		// [99] - return
		return subTypes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE ITEM
/////////////////////////////////////////////////////////////////////////////////////////
	private record MarshallerPolymorphicTypeCacheItem<T>(Class<T> type,String typeId,
													     Class<? super T> superType) {
		////////// Constructor / builder
		static <T> MarshallerPolymorphicTypeCacheItem<T> createFor(final Class<T> type,final String typeId,
																   final Class<? super T> superType) {
			return new MarshallerPolymorphicTypeCacheItem<T>(type,typeId,
														     superType);
		}
		public boolean superTypeEquals(final Class<?> superType) {
			return this.superType() == superType;
		}
		public MarshallPolymorphicTypeInfo getMarshallPolymorphicTypeInfo() {
			return this.superType
					   .getAnnotation(MarshallPolymorphicTypeInfo.class);
		}
	}
}
