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

package com.youpin.xiaomi.tesla.plugin.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class Response<D> implements Serializable {

    private int code;
    private String msg;
    private D data;
    private String traceId;
    private String spanId;
    private String cmd;
    private int id;
    private Map<String, String> headers = new HashMap<>();


    public Response(int code, String msg, D data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(int code, String msg, D data, String cmd) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.cmd = cmd;
    }

    public static <D> Response<D> success(D data) {
        return new Response<>(0, "succ", data);
    }

    public static <D> Response<D> success(String cmd, D data) {
        return new Response<>(0, "succ", data, cmd);
    }


}
