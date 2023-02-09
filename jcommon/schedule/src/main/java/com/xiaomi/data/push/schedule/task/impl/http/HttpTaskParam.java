package com.xiaomi.data.push.schedule.task.impl.http;

import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class HttpTaskParam {

    private String url;

    private String methodType;

    private String body;

    private Map<String,String> headers;
}
