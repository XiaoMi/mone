package com.xiaomi.youpin.docean.mvc;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 */
@Data
public class HttpRequestMethod {

    private String path;

    private String httpMethod;

    private Method method;

    private Object obj;

    private long timeout;


}
