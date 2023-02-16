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

package com.xiaomi.mone.log.manager.controller.interceptor;

import com.xiaomi.hera.trace.context.TraceIdUtil;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.tpc.login.filter.DoceanReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/2 15:18
 */
@Slf4j
@Component
public class HttpRequestInterceptor extends EnhanceInterceptor {

    @Resource
    private DoceanReqUserFilter doceanReqUserFilter;

    public HttpRequestInterceptor() {
        doceanReqUserFilter = new DoceanReqUserFilter();
        Map<String, String> map = new HashMap<>();
        map.put(ConstUtil.logoutUrl, "/user-manage/login");
        map.put(ConstUtil.loginUrl, "/user-manage/login");
        map.put(ConstUtil.devMode, "true");
        map.put(ConstUtil.innerAuth, "false");
        map.put(ConstUtil.authTokenUrl, "http://127.0.0.1:8098/login/token/parse");
        map.put(ConstUtil.ignoreUrl, "/alert/get");
        doceanReqUserFilter.init(map);
    }

    private static final Integer MAX_LENGTH = 3000;

    private String filterUrls = Config.ins().get("filter_urls", Strings.EMPTY);

    private List<String> filterUrlList;

    {
        filterUrlList = Arrays.stream(filterUrls.split(SYMBOL_COMMA)).distinct().collect(Collectors.toList());
    }

    @Override
    public void before(AopContext aopContext, Method method, Object[] args) {
        /**
         * 上下文中会拿不到用户信息
         */
        if (filterUrlList.contains(method.getName())) {
            return;
        }
        MvcContext mvcContext = ContextHolder.getContext().get();
        saveUserInfoThreadLocal(mvcContext);
    }

    private void saveUserInfoThreadLocal(MvcContext mvcContext) {
        if (!doceanReqUserFilter.doFilter(mvcContext)) {
            throw new RuntimeException("用户信息读取失败");
        }
        AuthUserVo userVo = (AuthUserVo) mvcContext.session().getAttribute(ConstUtil.TPC_USER);
        MoneUserContext.setCurrentUser(userVo);
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        MvcContext mvcContext = ContextHolder.getContext().get();
        mvcContext.getHeaders().put("traceId", TraceIdUtil.traceId());
        clearThreadLocal();
        return super.after(context, method, res);
    }

    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        clearThreadLocal();
        super.exception(context, method, ex);
    }

    private void clearThreadLocal() {
        MoneUserContext.clear();
    }

}
