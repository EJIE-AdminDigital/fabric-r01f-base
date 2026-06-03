package r01f.objectstreamer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * Set the tag name for an object marshalled as xml or json
 * For example, if an object is to be marshalled as XML within a tag like: 
 * <pre class='brush:xml'>
 * 		<my-type>
 * 			...
 *		</my-type>
 * </pre>
 * annotate the type as:
 * <pre class='brush:java'>
 * 		@MarshallType(as="my-type")
 * 		public class MyType {
 * 			...
 * 		}
 * </pre> 
 */
@Target({
			ElementType.TYPE
		})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation @MarshallAnnotation
public @interface MarshallType {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public final static String MARKER_FOR_DEFAULT = "";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Explicitly sets the name the field is mashalled to/from
     */
    public String as();
    /**
     * The namespace
     */
    public String namespace() default MARKER_FOR_DEFAULT;
    /**
     * When a type is an instance of a polymorphic type, sets the id to be used
     * at the typeId property
     */
    public String typeId() default MARKER_FOR_DEFAULT;
}
