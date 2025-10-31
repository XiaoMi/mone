package run.mone.hive.mcp.service.command;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RoleCommand注解
 * 用于标记Role命令处理类，被标记的类将被自动扫描并注册到RoleCommandFactory中
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RoleCommand {
    
    /**
     * 命令名称，如果不指定则使用类名
     * 
     * @return 命令名称
     */
    String value() default "";
    
    /**
     * 命令描述
     * 
     * @return 命令描述
     */
    String description() default "";
    
    /**
     * 命令优先级，数值越小优先级越高
     * 在匹配命令时，优先级高的命令会优先匹配
     * 
     * @return 优先级
     */
    int priority() default 100;
}
