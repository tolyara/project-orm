package annotations;

import storages.Actions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    String table();
    String column();

    Actions onUpdate() default Actions.NOACTION;
    Actions onDelete() default Actions.NOACTION;
}
