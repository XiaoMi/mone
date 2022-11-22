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
import java.util.List;

@Data
public class RecordingConfigList implements Serializable {


    /**
     * 返回的信息列表
     */
    @HttpApiDocClassDefine(value = "list", required = false, description = "返回的信息列表", defaultValue = "")
    private List<RecordingConfig> list;

    /**
     * 当前的页码
     */
    @HttpApiDocClassDefine(value = "page", required = true, description = "当前的页码", defaultValue = "1")
    private int page;

    @HttpApiDocClassDefine(value = "pagesize", required = true, description = "每页条数", defaultValue = "10")
    private int pagesize;

    /**
     * 总记录数
     */
    @HttpApiDocClassDefine(value = "total", required = true, description = "总记录数", defaultValue = "100")
    private int total;

}
