package com.xiaomi.mone.buddy.agent;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 7/26/21
 */
@Data
public class AgentContext {

    private long beginTime;

    private long endTime;

    private String clazz;

    private String methodName;

    private Object[] params;

    private Object res;

}
