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

package com.xiaomi.youpin.quota.exception;

/**
 * Created by xuzheng5
 * common errors
 */
public enum CommonError {

    Success(0, "success"),
    UnknownError(1, "unknown error"),
    UnknownResource(1001, "无效的resource ip"),
    UnableToFindValidResource(1002, "no valid resource found"),
    UnknownQuota(1003, "无效的quota"),
    UnknownQuotaRequest(1004, "无效的QuotaRequest"),
    FailedToAddNewQuota(1005, "添加新quota失败");


    public int code;
    public String message;

    CommonError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
