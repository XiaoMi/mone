package com.xiaomi.youpin.tesla.rcurve.proxy.context;

import com.youpin.xiaomi.tesla.bo.DubboApiInfo;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Data
public class ProxyContext {

    private Map<String, String> attachments = new HashMap<>(1);

    private Map<String, String> headers;

    private String traceId;

    private String method;

    private ProxyType type;

    /**
     * 用户可以修改post的body
     */
    public static String New_Body = "New_Body";

    /**
     * 用户设置新的dubbo api info
     */
    private DubboApiInfo dubboApiInfo;

    private ChannelHandlerContext handlerContext;

    public String getAttachment(String key, String defaultValue) {
        String value = this.attachments.get(key);
        return Optional.ofNullable(value).orElse(defaultValue);
    }


}
