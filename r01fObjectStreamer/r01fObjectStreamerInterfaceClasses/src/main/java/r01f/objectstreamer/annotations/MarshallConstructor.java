package r01f.objectstreamer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.lang.annotation.ElementType;

/**
 * @MarshallConstructor
 * Indicates the constructor to use
 *    
     :: Use it when your class has multiple constructors that could potentially match the input. Without this annotation, Jackson might get confused between:

		Path(String element)		
		Path(String[] elements)		
		Path(Object obj)
		
	By marking the String constructor as DELEGATING, you are explicitly telling Jackson: "If you see a raw String, this is the specific constructor you must use"
 */

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 
public @interface MarshallConstructor {
  //  
}