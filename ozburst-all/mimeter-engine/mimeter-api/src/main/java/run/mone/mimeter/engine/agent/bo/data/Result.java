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

package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Data
public class Result<D> implements Serializable {

    private int code;

    private String message;

    private D data;

    private boolean ok;

    private CommonReqInfo commonReqInfo;

    private Map<String,String> respHeaders;

    private String triggerCp;

    /**
     * 触发的检查点条件
     */
    private String triggerFilterCondition;

    private long rt;

    //bytes
    private long size;

    public Result() {
    }

    public Result(int code, String message, D data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<String> cancel() {
        return new Result<>(501, "cancel", "cancel");
    }

    public static <D> Result<D> fail(int errorCode, String message,D data) {
        return new Result(errorCode, message,data);
    }

    public static <D> Result<D> success(D data) {
        return new Result(200, "ok",data);
    }

    public boolean isSuccess() {
        return checkSuccess(this.code);
    }

    public static boolean checkSuccess(final int code) {
        return code == 0 || code == 200;
    }
}
