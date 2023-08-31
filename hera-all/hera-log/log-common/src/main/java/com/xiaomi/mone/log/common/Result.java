/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.common;

import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.exception.CommonException;

/**
 * Created by zhangzhiyong on 29/05/2018.
 * http json 返回结果
 */
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> fail(CommonError error) {
        return new Result<>(error.getCode(), error.getMessage());
    }

    public static <T> Result<T> failParam(String errorMsg) {
        return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
    }

    public static <T> Result<T> fail(CommonException ex) {
        return new Result<>(ex.getCode(), ex.getMessage());
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
