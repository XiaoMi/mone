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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import io.netty.channel.Channel;
import lombok.Data;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.Serializable;
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

    @Expose
    private Channel channel;

    private String data;

    private byte[] bytes;

    private boolean mesh = true;

    private int code;

    private String message;

    private boolean oneway;

    /**
     * 0 json 1 msgpack
     */
    private int serializeType;

    public boolean isRequest() {
        return type == 0;
    }

    public void putAtt(String key, String value) {
        this.attachments.put(key, value);
    }

    public String getAtt(String key, String defaultValue) {
        return this.attachments.getOrDefault(key, defaultValue);
    }

    public static UdsCommand createResponse() {
        UdsCommand res = new UdsCommand();
        res.setType(1);
        return res;
    }

    public static UdsCommand createResponse(long id) {
        UdsCommand res = new UdsCommand();
        res.setType(1);
        res.setId(id);
        return res;
    }

    public static UdsCommand createErrorResponse(long id, String message) {
        UdsCommand res = new UdsCommand();
        res.setType(1);
        res.setId(id);
        res.setCode(500);
        res.setMessage(message);
        return res;
    }

    public static UdsCommand createResponse(UdsCommand req, Object obj) {
        UdsCommand res = new UdsCommand();
        res.setType(1);
        res.setId(req.getId());
        res.setSerializeType(req.getSerializeType());
        if (req.getSerializeType() == 0) {
            res.setData(new Gson().toJson(obj));
        }
        if (req.getSerializeType() == 1) {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            try {
                byte[] d = objectMapper.writeValueAsBytes(obj);
                res.setBytes(d);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    public static UdsCommand createRequest() {
        UdsCommand req = new UdsCommand();
        req.setId(requestId.incrementAndGet());
        return req;
    }

}
