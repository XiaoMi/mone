package com.xiaomi.data.push.common;

import lombok.Data;

import java.util.Map;

/**
 * api 上下文只能放到参数的第一个.理论上不放在第一个也行,这样会增加代码的复杂度,没必要-__-!!!
 *
 * @author goodjava@qq.com
 */
@Data
public class RequestContext {

    /**
     * traceid 每次调用的唯一标识
     */
    private String traceId;

    /**
     * 调用时的开始时间
     */
    private long startTime;

    /**
     * 附加参数
     */
    private Map<String, Object> attachments;

}
