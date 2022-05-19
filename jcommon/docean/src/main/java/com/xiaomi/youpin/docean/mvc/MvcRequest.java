package com.xiaomi.youpin.docean.mvc;

import com.google.gson.JsonElement;
import lombok.Data;

import java.util.Map;


/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Data
public class MvcRequest {

    private String path;

    private String serviceName;

    private String methodName;

    private JsonElement arguments;

    private byte[] body;

    private String method;

    private Map<String,String> headers;

    private Map<String, String> params;
}
