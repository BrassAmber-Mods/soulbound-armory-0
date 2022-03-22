package soulboundarmory.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Register {
    String[] value() default "";
}
