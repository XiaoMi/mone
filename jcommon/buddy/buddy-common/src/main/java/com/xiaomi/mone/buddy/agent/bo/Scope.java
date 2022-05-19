package com.xiaomi.mone.buddy.agent.bo;

import java.util.UUID;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:41
 */
public class Scope {

    public static void start() {
        Span span = Context.getContext().getSpan();
        if (null == span) {
            span = new Span();
            span.setTraceId(UUID.randomUUID().toString());
            Context.getContext().setSpan(span);
        }
    }

    public static void close() {
        Context.removeContext();
    }

}
