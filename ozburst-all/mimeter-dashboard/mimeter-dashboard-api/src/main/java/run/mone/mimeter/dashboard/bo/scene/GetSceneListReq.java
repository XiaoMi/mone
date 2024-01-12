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

package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetSceneListReq implements Serializable {

    @HttpApiDocClassDefine(value = "page")
    private int page;

    @HttpApiDocClassDefine(value = "pageSize")
    private int pageSize;

    @HttpApiDocClassDefine(value = "keyword", required = false)
    private String keyword;

    @HttpApiDocClassDefine(value = "sceneType", required = false, description = "场景类型 0:http 1:dubbo ")
    private Integer sceneType;

    @HttpApiDocClassDefine(value = "status", description = "筛选场景状态 0:待运行、1:运行成功、4:运行中、5:已停止")
    private Integer status;

    @HttpApiDocClassDefine(value = "tenant", ignore = true, required = false)
    private String tenant;
}
