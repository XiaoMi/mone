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

package com.xiaomi.youpin.quota.bo;


import com.xiaomi.youpin.quota.exception.CommonError;
import lombok.Data;

import java.io.Serializable;


/**
 * @author goodjava@qq.com
 */
@Data
public class Result<D> implements Serializable {

    private int code;

    private String message;

    private D data;

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, D data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(D data) {
        this.data = data;
        this.code = CommonError.Success.code;
        this.message = CommonError.Success.message;
    }

    public static <D> Result<D> fail(CommonError error) {
        return new Result<D>(error.code, error.message);
    }
}
