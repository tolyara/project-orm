package annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {

	String fieldName();
	boolean isAutoIncrement() default false;
	
}
