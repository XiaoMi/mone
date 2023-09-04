/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.controller.interceptor;

import com.google.common.collect.Sets;
import com.xiaomi.hera.trace.context.TraceIdUtil;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.tpc.login.filter.DoceanReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;
import static com.xiaomi.mone.log.manager.common.ManagerConstant.SPACE_PAGE_URL;
import static com.xiaomi.mone.log.manager.common.ManagerConstant.TPC_HOME_URL_HEAD;

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
        Config config = Config.ins();
        Map<String, String> map = new HashMap<>();
        map.put(ConstUtil.devMode, config.get("tpc.devMode", "false"));
        map.put(ConstUtil.innerAuth, "false");
        map.put(ConstUtil.authTokenUrl, config.get("auth_token_url", "http://127.0.0.1:8098/login/token/parse"));
        map.put(ConstUtil.ignoreUrl, "/alert/get");
        map.put(ConstUtil.loginUrl, config.get("tpc_login_url", ""));
        map.put(ConstUtil.logoutUrl, config.get("tpc_logout_url", ""));

        doceanReqUserFilter.init(map);
    }

    private static final Integer MAX_LENGTH = 3000;


    private String filterUrls = Config.ins().get("filter_urls", Strings.EMPTY);

    private static final Set<String> TPC_HEADERS_URLS = Sets.newHashSet();

    private List<String> filterUrlList;

    {
        filterUrlList = Arrays.stream(filterUrls.split(SYMBOL_COMMA)).distinct().collect(Collectors.toList());
        TPC_HEADERS_URLS.add(SPACE_PAGE_URL);
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
            return;
//            throw new MilogManageException("please go to login");
        }
        AuthUserVo userVo = (AuthUserVo) mvcContext.session().getAttribute(ConstUtil.TPC_USER);
        Tpc tpc = Ioc.ins().getBean(Tpc.class);
        MoneUserContext.setCurrentUser(userVo, tpc.isAdmin(userVo.getAccount(), userVo.getUserType()));
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        solveResHeaders();
        clearThreadLocal();
        return super.after(context, method, res);
    }

    private void solveResHeaders() {
        MvcContext mvcContext = ContextHolder.getContext().get();
        mvcContext.getResHeaders().put("traceId", TraceIdUtil.traceId() == null ? StringUtils.EMPTY : TraceIdUtil.traceId());
        if (TPC_HEADERS_URLS.contains(mvcContext.getPath())) {
            mvcContext.getResHeaders().put(TPC_HOME_URL_HEAD, Config.ins().get(TPC_HOME_URL_HEAD, "https://127.0.0.1"));
        }
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
