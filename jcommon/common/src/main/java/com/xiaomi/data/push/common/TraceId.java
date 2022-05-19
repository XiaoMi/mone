package com.xiaomi.data.push.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author goodjava@qq.com
 */
public abstract class TraceId {

    public static String getTraceId(Object[] objs) {
        if (null != objs && objs.length == 1 && objs[0] != null && objs[0].getClass().equals(RequestContext.class)) {
            String traceId = ((RequestContext) objs[0]).getTraceId();
            if (StringUtils.isEmpty(traceId)) {
                return "";
            }
            return traceId;
        }
        return "";
    }

}
