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

package com.xiaomi.youpin.gwdash.common;

public enum ReviewStatusEnum {
    UNDER_REVIEW(0), // 审核中
    PASS(1), // 已通过
    REFUSE(2), // 驳回
    TO_BE_REVIEW(3),//未申请
    EMERGENCY_RELEASE(4);//紧急发布

    private int code;

    ReviewStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
