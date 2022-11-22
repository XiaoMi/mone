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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class DubboSource implements Serializable {

    @HttpApiDocClassDefine(value = "serviceName", required = false, description = "dubbo服务名", defaultValue = "com.xiaomi.ceshi.DubboService")
    private String serviceName;

    @HttpApiDocClassDefine(value = "group", required = false, description = "dubbo分组", defaultValue = "staging")
    private String group;

    @HttpApiDocClassDefine(value = "methods", required = false, description = "dubbo方法", defaultValue = "test")
    private String methods;

    @HttpApiDocClassDefine(value = "version", required = false, description = "dubbo版本", defaultValue = "1.0")
    private String version;
}
