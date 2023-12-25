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

package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/5/23
 */
@Data
public class DubboData extends BaseData implements Serializable,Cloneable {

    private String serviceName;

    private String methodName;

    private String group;

    private String version;

    private String mavenVersion;

    private String dubboEnv;

    private List<String> requestParamTypeList;

    private volatile String requestBody;

    private Integer requestTimeout;

    private ConcurrentHashMap<String, String> attachments = new ConcurrentHashMap<>();

    @Override
    public DubboData clone() throws CloneNotSupportedException {
        return (DubboData) super.clone();
    }
}
