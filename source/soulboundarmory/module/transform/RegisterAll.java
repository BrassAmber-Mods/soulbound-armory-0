package soulboundarmory.module.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 Fields in types with this annotation whose declared types are {@link Class#isAssignableFrom assignable} from {@link #type}
 will be automatically registered to the registry whose identifier matches {@link #registry}.
 */
@Target(ElementType.TYPE)
public @interface RegisterAll {
	/**
	 @return the base type of the fields to register
	 */
	Class<?> type();

	/**
	 @return the identifier of the registry
	 */
	String registry();

	/**
	 @return whether fields must have {@link Register} annotations in order to be registered
	 boolean explicit() default false;
	 */
}
