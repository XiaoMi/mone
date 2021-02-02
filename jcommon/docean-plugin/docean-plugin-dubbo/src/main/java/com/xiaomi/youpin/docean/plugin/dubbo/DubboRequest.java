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

package com.xiaomi.youpin.docean.plugin.dubbo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/16/21
 */
@Data
public class DubboRequest implements Serializable {

    private String serviceName;
    private String methodName;
    private String group = "";
    private String version = "";
    private int timeout = 1000;
    private String[] parameterTypes;
    private Object[] args;

    /**
     * 地址(如果有,就是定向发送)
     */
    private String addr;

    private String ip;

}
