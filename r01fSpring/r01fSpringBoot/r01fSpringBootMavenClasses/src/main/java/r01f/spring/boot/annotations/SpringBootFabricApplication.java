package r01f.spring.boot.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AliasFor;
import r01f.spring.boot.SpringBootAutoconfigurationNames;



 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(excludeName = { 
										SpringBootAutoconfigurationNames.V4.DATA_SOURCE,       
										SpringBootAutoconfigurationNames.V4.HIBERNATE_JPA,     
										SpringBootAutoconfigurationNames.V4.TRANSACTION
								      })
public @interface SpringBootFabricApplication {
    @AliasFor(annotation = SpringBootApplication.class)
    String[] scanBasePackages() default {};
}