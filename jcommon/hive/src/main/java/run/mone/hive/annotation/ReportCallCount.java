package run.mone.hive.annotation;

import java.lang.annotation.*;

/**
 * ReportCallCount注解
 * 用于标记需要上报调用次数的方法，被标记的方法将自动统计调用次数并上报到hive-manager
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportCallCount {
    
    /**
     * 业务名称，用于标识不同的业务场景
     * 
     * @return 业务名称
     */
    String businessName() default "";
    
    /**
     * 方法描述
     * 
     * @return 方法描述
     */
    String description() default "";
    
    /**
     * 是否异步上报，默认为true避免阻塞业务逻辑
     * 
     * @return 是否异步上报
     */
    boolean async() default true;
}

