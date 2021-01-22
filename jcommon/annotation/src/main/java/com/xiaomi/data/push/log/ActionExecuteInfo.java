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

package com.xiaomi.data.push.log;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 */
public class ActionExecuteInfo {

    /**
     * 执行的次数
     */
    @Getter
    private AtomicLong executeNum = new AtomicLong(0);

    /**
     * 执行成功的次数
     */
    private AtomicLong successNum = new AtomicLong(0);

    /**
     * 执行失败的次数
     */
    private AtomicLong failureNum = new AtomicLong(0);


    @Getter
    private AtomicLong rt = new AtomicLong();


    public AtomicLong getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(AtomicLong successNum) {
        this.successNum = successNum;
    }

    public AtomicLong getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(AtomicLong failureNum) {
        this.failureNum = failureNum;
    }
}
