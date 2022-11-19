package soulboundarmory.module.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 A registry will be generated for each method with an annotation of this type
 and the method will be transformed to return the registry.
 */
@Target(ElementType.METHOD)
public @interface Registry {
	/**
	 Return the name of the registry. Namespace may be omitted.

	 @return the name of the registry
	 */
	String value();
}
