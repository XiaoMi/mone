/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.auth;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.aop.ProceedingJoinPoint;
import com.xiaomi.youpin.docean.aop.anno.Aspect;
import com.xiaomi.youpin.docean.aop.anno.Before;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import lombok.extern.slf4j.Slf4j;
import run.mone.bo.PathAuth;
import run.mone.bo.User;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

/**
 * @author goodjava@qq.com
 */
@Aspect
@Slf4j
public class AuthAop {

    /**
     * 在方法执行前进行权限验证的切面方法
     * 根据方法上的 @Auth 注解和当前用户的角色进行权限验证
     * 如果用户没有相应的权限,则抛出 RuntimeException
     * 同时记录了一些日志信息,包括用户名、角色、请求路径等
     */
    @Before(anno = Auth.class)
    public void before(ProceedingJoinPoint point) {
        log.info("before:" + Arrays.toString(point.getArgs()));
        MvcContext context = ContextHolder.getContext().get();
        User user = (User) context.session().getAttribute("user");
        Method method = point.getMethod();
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        log.info("name:{} role:{} path:{}", user.getUsername(), user.getRole(), requestMapping.path());
        Auth auth = method.getAnnotation(Auth.class);

        ConcurrentMap<String, PathAuth> map = Ioc.ins().getBean("authMap");
        String path = context.getPath();
        //这里其实是数据库设置的(每次启动抓取一次)
        PathAuth pa = map.get(path);
        log.info("{}", pa);

        String role = auth.role();
        if (null != pa) {
            if (StringUtils.isNotEmpty(pa.getRole())) {
                role = pa.getRole();
            }
        }

        //没有就当普通用户处理
        if (null == user.getRole()) {
            user.setRole("user");
        }

        //必须有后台管理权限
        if (role.equals("admin")) {
            if (null == user || !user.getRole().equals("admin")) {
                throw new RuntimeException("role error");
            }
        }
        //必须登录
        if (role.equals("user")) {
            if (null == user) {
                throw new RuntimeException("role error");
            }
        }

    }


}
