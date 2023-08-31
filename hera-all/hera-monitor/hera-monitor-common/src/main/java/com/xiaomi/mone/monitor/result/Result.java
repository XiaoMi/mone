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

package com.xiaomi.mone.monitor.result;


/**
 * @author gaoxihui
 * @date 2021/7/10 7:36 下午
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

    public boolean isSuccess(){
        return ErrorCode.success.getCode() == this.code ? true : false;
    }

    public static <T> Result<T> fail(ErrorCode error) {
        return new Result<>(error.getCode(), error.getMessage());
    }

    public static <T> Result<T> fail(int code,String msg) {
        return new Result<>(code, msg);
    }

    public static <T> Result<T> fail(ExceptionCode ex) {
        return new Result<>(ex.getCode(), ex.getMessage());
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), null);
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
