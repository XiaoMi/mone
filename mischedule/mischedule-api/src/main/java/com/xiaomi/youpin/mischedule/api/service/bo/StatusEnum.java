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

package com.xiaomi.youpin.mischedule.api.service.bo;

/**
 * @author gaoyibo
 */
public enum StatusEnum {
    CHECK(CompileTaskStep.check.ordinal(), "running", "check 项目"),
    BEFORE_CLONE(CompileTaskStep.clone.ordinal(), "running", "clone 项目"),
    CLONE_SUCCESS_AND_START_BUILD(CompileTaskStep.build.ordinal(), "running", "clone " +
            "项目成功开始构建项目"),
    FIND_JAR(CompileTaskStep.findJar.ordinal(), "running", "构建完成寻找 jar 包"),
    // FOUND_AND_UPLOAD("40", "running", "开始上传 jar 包"),
    UPLOADED(CompileTaskStep.upload.ordinal(), "success", "上传 jar 包完成");

    private int step;
    private String status;
    private String message;

    public int getStep() {
        return step;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    StatusEnum(int step, String status, String message) {
        this.step = step;
        this.status = status;
        this.message = message;
    }

    ;
}
