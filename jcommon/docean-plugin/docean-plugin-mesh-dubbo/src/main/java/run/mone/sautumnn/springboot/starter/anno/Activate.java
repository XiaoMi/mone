package run.mone.sautumnn.springboot.starter.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Activate {

    String[] group() default {};

    String[] value() default {};

    int order() default 0;
}
