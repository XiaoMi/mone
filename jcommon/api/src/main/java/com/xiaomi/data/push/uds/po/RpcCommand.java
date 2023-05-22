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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import run.mone.api.Cons;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/22 17:14
 */
@Data
public class RpcCommand {


    /**
     * 魔术码
     */
    protected byte magic;

    /**
     * 用来存储标志位
     */
    protected int flag;

    protected Map<String, String> attachments = new HashMap<>();

    protected long id;

    protected String app;

    protected String remoteApp;

    protected long timeout = 1000;

    protected String cmd;

    protected String serviceName;

    protected String methodName;

    protected String[] paramTypes;


    /**
     * 容易造成困扰,且用String(gson)性能确实不太行
     */
    @Deprecated
    protected String[] params;

    protected byte[][] byteParams = new byte[][]{};

    protected byte[] data;

    /**
     * 序列化类型
     */
    protected byte serializeType = -1;

    public byte[] data() {
        return data;
    }

    protected Gson gson = new GsonBuilder().setDateFormat(Cons.EFFAULT_DATE_STYLE).create();

    public <T> T getData(Class clazz) {
        String s = new String(this.data);
        return (T) gson.fromJson(s, clazz);
    }

    public boolean isProvider() {
        return Boolean.TRUE.toString().equals(attachments.get(Cons.SIDE_TYPE_SERVER));
    }

    public boolean isConsumer() {
        return Boolean.TRUE.toString().equals(attachments.get(Cons.SIDE_TYPE_CLIENT));
    }
}
