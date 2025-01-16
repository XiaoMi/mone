package run.mone.m78.server.config.auth;

import run.mone.m78.api.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 14:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleControl {
    UserRoleEnum role();

    String strategy() default "default";

    boolean permissions() default false;

}
