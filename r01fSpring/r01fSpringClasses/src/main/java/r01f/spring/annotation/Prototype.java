package r01f.spring.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;
/*
 *  @Prototype is equivalent to use @Scope("prototype").
 *  --------------------------------------------------
 *  When a bean is scoped as Prototype, the Spring IoC container creates a new instance of that bean every time a request for that bean is made.
    In contrast to the default Singleton scope (where Spring creates one single instance and shares it everywhere),
    the Prototype scope follows a "create-on-demand" policy.
    State	Ideal for stateful beans (like a user context or a specific data processor).
 * 
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) 
public @interface Prototype {
	//
}