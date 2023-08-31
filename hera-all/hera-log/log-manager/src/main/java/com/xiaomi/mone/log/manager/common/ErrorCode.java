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
package com.xiaomi.mone.log.manager.common;

public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),
    CREATE_ALERT_FAILURE(2, "failed to create alert"),
    SUBMIT_FLINK_JOB(3, "failed to submit flink job"),
    ALERT_NOT_FOUND(4, "Alert not found"),
    ALERT_REMOVE_FAILED(5, "failed to remove alert"),
    FAIL_PARAM(6, "参数异常");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
