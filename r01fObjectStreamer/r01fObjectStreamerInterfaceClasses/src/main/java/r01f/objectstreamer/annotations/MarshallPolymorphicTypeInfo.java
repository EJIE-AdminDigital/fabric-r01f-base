package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * This annotation is necessary when a field is defined using an interface or an abstract class
 * In this case Jackson needs more information about the object type in order to DESERIALIZE it
 * <pre class='brush:java'>
 * 		@MarshallType(as="myType")
 *		@Accessors(prefix="_")
 * 		public clas MyType {
 * 			@MarshallField(as="polymorphicField")
 * 			@Getter @Setter MyInterface _myField;		<-- jackson NEEDS info to guess the field's type
 * 															when deserializing
 * 		}
 * 		public interface MyInterface {
 * 			...
 * 		}
 * 		public class MyInterfaceImpl1
 * 		  implements MyInterface {
 * 			...
 * 		}
 * 		public class MyInterfaceImpl2
 * 		  implements MyInterface {
 * 			...
 * 		}
 * </pre>
 * This object will be SERIALIZED as:
 * <pre class='brush:xml'>
 * 		<myType>
 * 			<polymorphicField>
 * 				...
 * 			</polymorphicField>
 * 		</myType>
 * </pre>
 *
 * BUT how does Jackson know what concrete implementation to instantiate when DE-serializing the XML or JSON?
 * Jackson needs MORE information, such a typeId attribute at the xml as:
 * <pre class='brush:xml'>
 * 		<myType>
 * 			<polymorphicField typeId='myInterfaceImpl2'>	<-- using this attribute Jackson knows what concrete type to instantiate
 * 				...
 * 			</polymorphicField>
 * 		</myType>
 * </pre>
 *
 * In order to do so, the interface MUST be annotated as:
 * <pre class='brush:java'>
 * 		@MarshallPolymorphicTypeInfo(typeIdPropertyName="typeId")
 * 		public interface MyInterface {
 * 			...
 * 		}
 * </pre>
 * And the concrete implementations MUST be annotated with @MarshallType setting the "typeId" property that will be used as the typeId value
 * (if typeId is NOT set the as property will be used)
 * <pre class='brush:java'>
 * 		@MarshallType(as="myInterfaceImpl1" typeId="impl1")
 * 		public class MyInterfaceImpl1
 * 		  implements MyInterface {
 * 			...
 * 		}
 * 		@MarshallType(as="myInterfaceImpl2" typeId="impl2")
 * 		public class MyInterfaceImpl2
 * 		  implements MyInterface {
 * 			...
 * 		}
 * </pre>
 *
 * BEWARE!!!!!
 * There are "edge" cases where the JsonTypeInfo is NOT needed, such as when we NEVER want to include type information
 *
 * CASE 1: OIDs, which are ALWAYS serialized as their string representation
 * <pre class='brush:java'>
 * 		@MarshallPolymorphicTypeInfo(includeTypeInfo=MarshalTypeInfoIncludeCase.NEVER)
 * 	 	public interface OID {
 * 			...
 * 		}
 * </pre>
 *
 * CASE 2: A generic wrapper type that will contain a SIMPLE object serialized as a string (ie: Year, MonthOfYear, etc)
 * 		   the serialized object SHOULD be like:
 * 				{
 * 					"aField":"2024"
 * 				}
 * 		  and NOT like:
 * 				{
 * 					"aField": {
 * 						"typeId":"java.time.Year",
 * 						"value": "2024"
 * 					}
 * 				}
 * 		  ... in order to achieve this, the _aField field has to be annotated as:
 * <pre class='brush:java'>
 *		public class GenericWrapper<T> {
 *			@MarshallField(as="aField")
 *			@MarshallPolymorphicTypeInfo(includeTypeInfo=MarshalTypeInfoIncludeCase.NEVER)	// when T is a SIMPLE type (ie: Year, MonthOfYear, etc) we don't need type info
 * 			private T _afield;
 * 		}
 * </pre>
 */
@Target({
			ElementType.TYPE,
			ElementType.FIELD,
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallPolymorphicTypeInfo {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The property name for the type identifier
	 */
	public String typeIdPropertyName() default "typeId";
//	/**
//	 * Is the type identifier visible for the deserializer?
//	 * 	- if this property is TRUE the [type identifier] VALUE will be passed as part of the stream to the DE-serializer (the type that performs the stream deserialization)
//	 *	  (ie: the typeId property is included in stream so the deserializer can see it)
//	 *  - if this property is FALSE the [type identifier] VALUE will be handled INTERNALLY at the TypeDeserializer (the class that identifies the concrete type)
//	 * 	  and NOT visible by the DE-serializer (the type that performs the stream deserialization)
//	 * 	  (ie: the typeId property is NOT included in stream so the deserializer cannot see it)
//	 */
//	public boolean typeInfoAvailableWhenDeserializing() default false;
	/**
	 * When to include type information
	 * (see
	 * @return
	 */
	public MarshalTypeInfoIncludeCase includeTypeInfo() default MarshalTypeInfoIncludeCase.ALWAYS;

//	public MarshallTypeInfoInclude includeTypeInfo() default @MarshallTypeInfoInclude();
//
//	public @interface MarshallTypeInfoInclude {
//		public MarshalTypeInfoIncludeCase type() default MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE;
//		public MarshalTypeInfoIncludeCase property() default MarshalTypeInfoIncludeCase.WHEN_ABSTRACT_OR_INTERFACE;
//	}
	public enum MarshalTypeInfoIncludeCase {
		NEVER,
		ALWAYS,
		WHEN_ABSTRACT_OR_INTERFACE;
	}
}
