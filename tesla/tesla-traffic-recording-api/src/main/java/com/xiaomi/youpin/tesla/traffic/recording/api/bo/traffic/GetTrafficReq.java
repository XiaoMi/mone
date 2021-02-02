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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @author dingpei
 */
@Data
public class GetTrafficReq implements Serializable {

    private int recordingConfigId;

    private int page;

    private int pageSize;

    private int id;

    private String newBody;

    private Map<String,String> newHeaders;
}
