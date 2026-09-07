package r01f.eventbus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

@Qualifier
@Target({ElementType.FIELD,
		 ElementType.PARAMETER, 
		 ElementType.TYPE, 
		 ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBusFor {
	//public String usedFor();
	String value();
}
