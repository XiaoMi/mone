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
public class GetRecordingConfigListReq implements Serializable {

    @HttpApiDocClassDefine(value = "sourceType", required = false, description = "来源类型", defaultValue = "1")
    private int sourceType;

    @HttpApiDocClassDefine(value = "envType", required = false, description = "网关环境类型", defaultValue = "1")
    private int envType;

    @HttpApiDocClassDefine(value = "status", required = false, description = "状态", defaultValue = "1")
    private int status;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "dp")
    private String creator;

    @HttpApiDocClassDefine(value = "name", required = false, description = "名称", defaultValue = "test")
    private String name;

    @HttpApiDocClassDefine(value = "page", required = true, description = "当前的页码", defaultValue = "1")
    private int page;

    @HttpApiDocClassDefine(value = "pageSize", required = true, description = "每页条数", defaultValue = "10")
    private int pageSize;
}
