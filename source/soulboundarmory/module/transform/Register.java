package soulboundarmory.module.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 The values of fields with annotations of this type will be registered automatically.
 */
@Target(ElementType.FIELD)
public @interface Register {
	/**
	 The identifier of the object to register. Namespace may be omitted.

	 @return the identifier of the object to register
	 */
	String value();

	/**
	 The identifier of the registry into which to register the object.
	 It may be omitted if the annotated field's declaring type is annotated by {@link RegisterAll}.

	 @return the identifier of the registry into which to register the object
	 */
	String registry() default "";
}
