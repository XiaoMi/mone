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

package run.mone.mimeter.dashboard.bo.sla;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetSlaListReq implements Serializable {

    @HttpApiDocClassDefine(value = "slaName", required = false, description = "sla名称", defaultValue = "sla名称")
    private String slaName;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "1")
    private String creator;

    @HttpApiDocClassDefine(value = "page", required = true, description = "当前的页码", defaultValue = "1")
    private int page;

    @HttpApiDocClassDefine(value = "pageSize", required = true, description = "每页条数", defaultValue = "10")
    private int pageSize;
}
