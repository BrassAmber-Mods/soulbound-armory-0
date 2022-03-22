package soulboundarmory.lib.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 {@code static final} field: single argument (identifier) or nothing; search by type.
 <p>
 {@code static native} method with IForgeRegistry return type: generate a registry matching the specified type and return it whenever invoked

 Type: try to register all fields matching registries
 - by type if value is absent or
 - by registry identifiers if value is present.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Register {
    String[] value() default "";
}
