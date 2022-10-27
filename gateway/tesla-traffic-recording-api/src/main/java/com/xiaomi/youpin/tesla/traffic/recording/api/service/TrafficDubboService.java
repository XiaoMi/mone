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

package com.xiaomi.youpin.tesla.traffic.recording.api.service;

import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayResponse;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.Traffic;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.TrafficList;

/**
 * @author dingpei
 * @author goodjava@qq.com
 */
public interface TrafficDubboService {

    /**
     * 获取流量列表
     * @return
     */
    Result<TrafficList> getTrafficList(GetTrafficReq req);


    /**
     * 获取
     * @param req
     * @return
     */
    Result<Traffic> getTraffic(GetTrafficReq req);


    /**
     * 删除
     * @param req
     * @return
     */
    Result<Boolean> delTraffic(GetTrafficReq req);


    /**
     * 更新(只会更新header 和 body)
     * @param req
     * @return
     */
    Result<Boolean> updateTraffic(GetTrafficReq req);

    /**
     * 获取最后一次调用的结果
     * @param req
     * @return
     */
    Result<ReplayResponse> getLastCallResult(GetTrafficReq req);

}
