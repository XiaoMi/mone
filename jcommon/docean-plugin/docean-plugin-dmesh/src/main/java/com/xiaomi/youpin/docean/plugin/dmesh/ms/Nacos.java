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

package com.xiaomi.youpin.docean.plugin.dmesh.ms;


import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

/**
 * @author dingpei@xiaomi.com
 * nacos配置操作
 */
@MeshMsService(interfaceClass = Nacos.class, name = "nacos")
public interface Nacos {
    String getConfigStr(String dataId, String group, int timeout);

    boolean publishConfig(String dataId, String group, String content);

    boolean deleteConfig(String dataId, String group);

}
