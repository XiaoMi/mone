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
package run.mone.hera.operator.common;

public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),

    // Parameter issue
    invalidParamError(1001, "Invalid parameter"),
    DeleteJobFail(1006, "Request interface deletion failed."),
    UpdateJobFail(1007, "Request interface update failed."),
    RequestBodyIsEmpty(1008, "The request body is empty."),
    ScrapeIdIsEmpty(1009, "The query fetch id is empty."),
    ThisUserNotHaveAuth(1010, "This user does not have permission."),
    NoOperPermission(1013, "No operation permission."),
    OperFailed(1014, "Operation failed."),
    REPEAT_ADD_PROJECT(1016, "Duplicate add item"),
    UNKNOWN_TYPE(1017, "Unknown type"),
    INVALID_USER(4001, "Invalid user identity."),
    NO_DATA_FOUND(4004, "Data not found.");

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
