/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dongzhenxing on 2023/2/24 7:00 PM
 */
@Data
public class PullTrafficReqBase implements Serializable {

//    /**
//     * 开启使用流量数据的接口id列表
//     */
//    private List<Integer> useTrafficApiIds;

    /**
     * 每个接口所用的流量配置及筛选范围
     */
    private List<PullApiTrafficReq> apiTrafficReqList;

}