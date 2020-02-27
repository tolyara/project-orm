package annotations;

import java.lang.annotation.*;

/**
 * Annotation for fields our DB entities
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String fieldName();
	boolean isAutoIncrement() default false;
	
}
