/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gateway.filter;

import com.youpin.xiaomi.tesla.bo.DubboApiInfo;
import com.youpin.xiaomi.tesla.bo.GroupConfig;
import com.youpin.xiaomi.tesla.bo.ResponseConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * 访问的上下文:在一个调用链中,这个是串行的
 */
@Data
public class FilterContext implements Serializable {

    /**
     * 用户可以修改post的body
     */
    public static String New_Body = "New_Body";

    /**
     * 用户可以修改route type
     */
    public static String New_Route_Type = "New_Route_Type";

    /**
     * 支持原始接口返回byte数组
     */
    public static final String SUPPORT_ORIGIN_BYTES = "SUPPORT_ORIGIN_BYTES";

    /**
     * trace logs的最大长度
     */
    private static final int TRACE_LOGS_LENGTH = 4000;

    /**
     * 用户设置新的dubbo api info
     */
    private DubboApiInfo dubboApiInfo;

    /**
     * switch filter 开关
     */
    private int switchflag = 0;

    /**
     * 添加一项或多项权限
     */
    public final void switchEnable(int permission) {
        switchflag |= permission;
    }

    /**
     * 删除一项或多项权限
     */
    public final void switchDisable(int permission) {
        switchflag &= ~permission;
    }

    public final boolean switchIsAllow(int permission) {
        return (switchflag & permission) == permission;
    }

    private static final Logger logger = LoggerFactory.getLogger(FilterContext.class);

    private long beginTime;

    private long hbegin;

    private String uid;

    private String ip;

    private String userAgent;

    private boolean next;

    private boolean enableLog = true;

    private String traceId;

    private String spanId;

    private String callId;

    private long rpcUseTime;

    public FilterContext() {
        beginTime = System.currentTimeMillis();
    }

    public FilterContext(boolean enableLog) {
        this();
        this.enableLog = enableLog;
    }

    private transient RequestContext requestContext;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> attachments = new HashMap<>(1);

    private Map<String, String> dubboContextAttachments;

    private ResponseConfig responseConfig;

    private GroupConfig groupConfig;


    public String getAttachment(String key, String defaultValue) {
        String value = this.attachments.get(key);
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public void setAttachment(String key, String value) {
        this.attachments.put(key, value);
    }

    /**
     * 默认实现是直接打日志
     */
    private transient Consumer<String> logConsumer = (msg) -> logger.info(msg);

    public void log(String msg) {
        if (enableLog) {
            logConsumer.accept(msg);
        }
    }

    /**
     * 默认不再开放了，这个使用成本太高了，业务得知道什么时候释放，如果遇到异常。。。就都凉了
     *
     * @param data
     * @param direct
     * @return
     */
    public ByteBuf byteBuf(byte[] data, boolean direct) {
        if (direct) {
            return requestContext.byteBuf(data);
        } else {
            return byteBuf(data);
        }
    }

    public ByteBuf byteBuf(byte[] data) {
        return Unpooled.wrappedBuffer(data);
    }


    public void addTraceEvent(String name, Long time) {
        // 逻辑由探针实现
    }

    public void addTraceEvent(String name, Map<String, String> supplier) {
        // 逻辑由探针实现
    }

    public void addBusErrorEvent(String eventName, int errorCode, String errorMsg) {
        // 逻辑由探针实现
    }

    public static String reduceTraceMessage(String errorMsg) {
        if (null != errorMsg && errorMsg.length() > TRACE_LOGS_LENGTH) {
            return errorMsg.substring(0, TRACE_LOGS_LENGTH) + "......";
        }
        return errorMsg;
    }
}
