package com.xiaomi.youpin.docean.mvc;

/**
 * @Author goodjava@qq.com
 * @Date 2021/5/16 17:24
 */
public interface MvcServlet {

    String path();

    String method();

    Object execute(Object param);

}
