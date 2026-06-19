package r01f.objectstreamer.annotationintrospector;

import java.lang.annotation.Annotation;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.outline.TypeOutline.OutlineTreeNode;
import r01f.util.types.Strings;
import tools.jackson.databind.JavaType;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class MarshallerAnnotationUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds the nearest super type annotated with @MarshallPolymorphicTypeInfo
	 * @param type
	 * @param marshallPolymorphicTypeAnnotatedSuperTypeByTypeCache
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? super T> findSuperTypeAnnotatedWithMarshallPolymorphicTypeInfoFor(final Class<T> type) {
		// find the @MarshallPolymorphicTypeInfo up in the hierarchy
		TypeOutline typeOutline = TypeOutline.from(type);
		Class<? super T> outSuperTypeOrInterfaceAnnotated = (Class<? super T>)_recurseFindSuperTypeOrInterfaceAnnotated(MarshallPolymorphicTypeInfo.class,
																			   						  					typeOutline.getBottomType());
		return outSuperTypeOrInterfaceAnnotated;
	}
	private static <A extends Annotation> Class<?> _recurseFindSuperTypeOrInterfaceAnnotated(final Class<A> annType,
																		 				 	 final OutlineTreeNode node) {
		Class<?> outSuperTypeOrInterfaceAnnotted = null;

		A annotation = node.getObjectType().getAnnotation(annType);
		if (annotation == null) {
			for (OutlineTreeNode parent : node.getParents()) {
				outSuperTypeOrInterfaceAnnotted = _recurseFindSuperTypeOrInterfaceAnnotated(annType,
																			  				parent);
				if (outSuperTypeOrInterfaceAnnotted != null) break;
			}
		} else {
			outSuperTypeOrInterfaceAnnotted = node.getObjectType();
		}
		return outSuperTypeOrInterfaceAnnotted;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static String findPolymorphicTypeIdFor(final Class<?> type) {
		String outTypeId = null;

		// If the @MarshallType(as="...",typeId="...") is present:
		//	- If typeId is set, use it as typeId
		//	- Else use 'as' value as typeId

		// [1] - Use @MarshallType
		MarshallType typeMarshallAnn = type.getAnnotation(MarshallType.class);
		if (typeMarshallAnn != null) {
			outTypeId = MarshallType.MARKER_FOR_DEFAULT.equals(typeMarshallAnn.typeId()) ? typeMarshallAnn.as()
																					  : typeMarshallAnn.typeId();
		}
		// [2] - else, if the @JsonTypeName is present, use its value as typeId
		// 		 otherwise if @JsonRootName is present, use its value as typeId
		else {
			JsonTypeName jsonTypeNameAnn = type.getAnnotation(JsonTypeName.class);
			if (jsonTypeNameAnn != null) {
				outTypeId = jsonTypeNameAnn.value();
			} else {
				JsonRootName jsonRootNameAnn = type.getAnnotation(JsonRootName.class);
				if (jsonRootNameAnn != null) outTypeId = jsonRootNameAnn.value();
			}
		}
		// if not annotation is present, use the full class name
		if (Strings.isNullOrEmpty(outTypeId)) {
			log.trace("[WARNING] Type {} does NOT have the @{} annotation or the @{} annotation does NOT include the 'typeId' attribute: the full class name will be used as typeId",
				     type,MarshallType.class.getSimpleName(),MarshallType.class.getSimpleName());
			outTypeId = type.getName();
		}
		return outTypeId;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Does the type has a given annotation in it's hierarchy?
	 * @param annType
	 * @param javaType
	 * @return
	 */
	public static boolean hasSuperTypeAnnotatedWith(final Class<? extends Annotation> annType,
													final JavaType javaType) {
		TypeOutline typeOutline = TypeOutline.from(javaType.getRawClass());
		return _recurseFindSuperTypeOrInterfaceAnnotatedWith(annType,
															 typeOutline.getBottomType());
	}
	private static boolean _recurseFindSuperTypeOrInterfaceAnnotatedWith(final Class<? extends Annotation> annType,
																		 final OutlineTreeNode node) {
		boolean outAnnotated = node.getObjectType().isAnnotationPresent(annType);
		if (!outAnnotated) {
			for (OutlineTreeNode parent : node.getParents()) {
				outAnnotated = _recurseFindSuperTypeOrInterfaceAnnotatedWith(annType,
																			 parent);
				if (outAnnotated) break;
			}
		}
		return outAnnotated;
	}
}
