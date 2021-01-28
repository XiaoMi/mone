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

package com.xiaomi.data.push.uds.po;

import com.google.gson.annotations.Expose;
import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 */
@Data
public class UdsCommand implements Serializable {

    @Expose
    public static final AtomicLong requestId = new AtomicLong(0);

    @Expose
    private Channel channel;

    private Map<String, String> attachments = new HashMap<>();

    /**
     * 0 request 1 response
     */
    private int type;

    private long id;

    private String app;

    private String remoteApp;

    private long timeout = 1000;

    private String cmd;

    private String serviceName;

    private String methodName;

    private String[] paramTypes;

    private String[] params;

    private byte[][] byteParams;

    private byte[] data;

    private boolean mesh = true;

    private int code;

    private String message;

    private boolean oneway;

    /**
     * 0 json 1 msgpack
     */
    private byte serializeType;

    public static UdsCommand createResponse(UdsCommand request) {
        UdsCommand res = new UdsCommand();
        res.setId(request.getId());
        res.setSerializeType(request.getSerializeType());
        res.setType(1);
        return res;
    }

    public boolean isRequest() {
        return type == 0;
    }

    public void putAtt(String key, String value) {
        this.attachments.put(key, value);
    }

    public String getAtt(String key, String defaultValue) {
        return this.attachments.getOrDefault(key, defaultValue);
    }


    public static UdsCommand createErrorResponse(long id, String message) {
        UdsCommand res = new UdsCommand();
        res.setType(1);
        res.setId(id);
        res.setCode(500);
        res.setMessage(message);
        return res;
    }


    public static UdsCommand createRequest() {
        UdsCommand req = new UdsCommand();
        req.setId(requestId.incrementAndGet());
        req.setSerializeType(RcurveConfig.ins().getCodeType());
        return req;
    }

    public void setData(Object data) {
        ICodes codes = CodesFactory.getCodes(this.getSerializeType());
        this.data = codes.encode(data);
    }

    public <T> T getData(Type type) {
        if (null == this.data) {
            return null;
        }
        ICodes codes = CodesFactory.getCodes(this.getSerializeType());
        return codes.decode(this.data, type);
    }

}
