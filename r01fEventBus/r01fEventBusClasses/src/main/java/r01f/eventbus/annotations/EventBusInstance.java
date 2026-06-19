package r01f.eventbus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,
		 ElementType.PARAMETER, 
		 ElementType.TYPE, 
		 ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBusInstance {
	public String usedFor();
}
