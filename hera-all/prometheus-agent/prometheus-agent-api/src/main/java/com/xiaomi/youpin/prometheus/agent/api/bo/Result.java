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

package com.xiaomi.youpin.prometheus.agent.api.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dingpei
 */
@Data
public class Result<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private final static int SUCCESS_CODE = 0;

    private final static int FAILURE_CODE = 500;

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result();
        result.setCode(FAILURE_CODE);
        result.setMessage(msg);
        return result;
    }


    public static <T> Result<T> success(int code, T data) {
        Result result = new Result();
        result.setCode(code);
        result.setData(data);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setData(data);
        result.setMessage("success");
        return result;
    }

}
