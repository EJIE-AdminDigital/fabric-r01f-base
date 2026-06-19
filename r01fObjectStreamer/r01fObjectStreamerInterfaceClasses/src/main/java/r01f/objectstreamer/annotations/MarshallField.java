package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * Set the tag name for an object's field when it's marshalled as XML or JSON
 *
 * Sample 1: basic marshall
 * <pre class='brush:java'>
 * 		@MarshallType(as="my-type")
 * 		public class MyType {
 * 			@MarshallField(as="my-field")
 * 			@Getter @Setter private MyOtherType _otherObj;
 * 		}
 * </pre>
 * generates:
 * <pre class='brush:xml'>
 * 		<my-type>
 * 			<my-field>...</my-field>
 * 		<my-type>
 * </pre>
 * <pre class='brush:json'>
 * 		{
 * 			"my-field" = {...}
 * 		}
 * </pre>
 *
 * Sample 2: mashall as XML attribute
 * <pre class='brush:java'>
 * 		@MarshallType(as="my-type")
 * 		public class MyType {
 * 			@MarshallField(as="my-field",
 * 							 whenXml=@MarshallFieldAsXml(attr=true))
 * 			@Getter @Setter private MyOtherType _otherObj;
 * 		}
 * </pre>
 * generates
 * <pre class='brush:xml'>
 * 		<my-type my-field="...">
 * 			...
 * 		<my-type>
 * </pre>
 *
 * Sample 3: marshall dates
 * <pre class='brush:java'>
 * 		@MarshallType(as="my-type")
 * 		public class MyType {
 * 			@MarshallField(as="my-field",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
 * 							 whenXml=@MarshallFieldAsXml(attr=true))
 * 			@Getter @Setter private MyOtherType _otherObj;
 * 		}
 * </pre>
 *
 * Sample 4: collections
 * <pre class='brush:java'>
 * 		@MarshallType(as="my-type")
 * 		public class MyType {
 * 			@MarshallField(as="my-col",
 * 							 whenXml=@MarshallFieldAsXml(collectionElementName = "my-other-type"))
 * 			@Getter @Setter private Collection<MyOtherType> _otherObjCol;
 * 		}
 * </pre>
 * generates:
 * <pre class='brush:xml'>
 * 		<my-type>
 * 			<my-col>
 * 				<my-other-type>...</my-other-type>
 * 				<my-other-type>...</my-other-type>
 * 				...
 * 			<my-col>
 * 		<my-type>
 * </pre>
 *
 */
@Target({
			ElementType.FIELD
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallField {

	public final static String MARKER_FOR_DEFAULT = "";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Explicitly sets the name the field is mashalled to/from
	 */
	public String as() default MARKER_FOR_DEFAULT;
	/**
	 * namespace
	 */
	public String namespace() default MARKER_FOR_DEFAULT;
	/**
	 * If the annotated field is an string, does the String content have to be escaped?
	 */
	public boolean escape() default false;
	/**
	 * Equivalent to @JsonDefaultValue
	 */
	public String defaultValue() default MARKER_FOR_DEFAULT;
	/**
	 * how does the date will be formated if the annotated field is a date
	 */
	public MarshallDateFormat dateFormat() default @MarshallDateFormat;
	/**
	 * How to marshall when the field is a collection-like field (Collection / Map)
	 */
	public MarshallCollectionField whenCollectionLike() default @MarshallCollectionField;
	/**
	 * How to marshall when xml
	 */
	public MarshallFieldAsXml whenXml() default @MarshallFieldAsXml();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public @interface MarshallFieldAsXml {
		public boolean attr() default false;
		public boolean asParentElementValue() default false;
		public String collectionElementName() default MARKER_FOR_DEFAULT;
	}
	public @interface MarshallCollectionField {
		public boolean useWrapping() default true;		// can be used to explicitly disable wrapping
	}
	public @interface MarshallDateFormat {
		public DateFormat use() default DateFormat.ISO8601;
		public String format() default MARKER_FOR_DEFAULT;
		public String locale() default "";		// ie: "es_ES"
		public String timezone() default "";	// ie: "GMT+1"
	}
	public enum DateFormat {
		EPOCH,
		ISO8601,
		CUSTOM;
	}
}
