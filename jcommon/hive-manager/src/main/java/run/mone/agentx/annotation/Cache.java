package run.mone.agentx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    /**
     * Cache key
     */
    String key() default "";

    /**
     * Expire time in seconds
     */
    long expire() default 3600;

    /**
     * Removal strategy: TIME or MANUAL
     */
    RemovalStrategy strategy() default RemovalStrategy.TIME;
}
