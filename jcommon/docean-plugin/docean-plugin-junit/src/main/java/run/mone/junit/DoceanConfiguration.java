package run.mone.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author goodjava@qq.com
 * @date 2024/3/3 09:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // 应用于类
public @interface DoceanConfiguration {
    String[] basePackage();
}
