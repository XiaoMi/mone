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

package run.mone.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author shanwb
 * @date 2023-08-16
 */
@Data
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    private String traceId;
    private Map<String, String> attributes;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(int code, String message, T data, String traceId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }

    public static <T> Result<T> success(T data) {
        return new Result(ErrorCode.SUCCESS, "success", data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result(code, message, null);
    }

    public static <T> Result<T> fromException(Throwable e) {
        return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", traceId='" + traceId + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
