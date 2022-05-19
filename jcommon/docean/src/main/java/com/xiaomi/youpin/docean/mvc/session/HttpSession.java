package com.xiaomi.youpin.docean.mvc.session;

/**
 * @author goodjava@qq.com
 */
public interface HttpSession {


    String SESSIONID = "DOCEANID";

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    String getId();

    void invalidate();


}
