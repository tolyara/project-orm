package annotations;


import storages.Actions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    String table();

    String column();

    Actions onUpdate() default Actions.NOACTION;

    Actions onDelete() default Actions.NOACTION;
}