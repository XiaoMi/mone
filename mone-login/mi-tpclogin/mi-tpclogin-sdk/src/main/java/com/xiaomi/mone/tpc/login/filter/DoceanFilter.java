package com.xiaomi.mone.tpc.login.filter;

import com.xiaomi.youpin.docean.mvc.MvcContext;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * @project: mi-tpclogin
 * @author: zgf1
 * @date: 2022/11/8 11:49
 */
public abstract class DoceanFilter {

    public abstract void init(Map<String, String> filterConfig);

    public abstract boolean doFilter(MvcContext mvcContext);

    public abstract void destroy();

}
