package com.xiaomi.mone.tpc.login.anno;

import com.xiaomi.mone.tpc.login.enums.RpcTypeEnum;

import java.lang.annotation.*;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 9:38
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthExclude {

}
