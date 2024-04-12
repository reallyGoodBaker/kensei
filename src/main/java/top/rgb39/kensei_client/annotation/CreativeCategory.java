package top.rgb39.kensei_client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreativeCategory {
    String id() default "kensei";
    String nameComponent() default "item.kensei";
    boolean useAsIcon() default false;
}
