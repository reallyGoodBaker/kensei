package top.rgb39.kensei.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class Reflects {

    public static boolean match(Class<?> target, Class<? extends Annotation> annotationClass, Map<String, Object> fields) {
        Annotation anno = target.getAnnotation(annotationClass);

        if (anno == null) {
            return false;
        }

        try {
            for (var entry: fields.entrySet()) {
                Method getter = annotationClass.getDeclaredMethod(entry.getKey());
                var value = getter.invoke(anno);
                if (value == null || !value.equals(entry.getValue()))
                    return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
