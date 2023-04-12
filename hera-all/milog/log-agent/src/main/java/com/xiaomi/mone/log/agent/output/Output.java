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

package com.xiaomi.mone.log.agent.output;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@EqualsAndHashCode
public abstract class Output implements Serializable {

    /**
     * rocketmq、talos
     */
    private String type;

    private String tag;

    public String getOutputType() {
        return type;
    }

    public void setOutputType(String outputType) {
        this.type = outputType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * getEndpoint
     * @return
     */
    public abstract String getEndpoint();

    public abstract String getServiceName();
}
