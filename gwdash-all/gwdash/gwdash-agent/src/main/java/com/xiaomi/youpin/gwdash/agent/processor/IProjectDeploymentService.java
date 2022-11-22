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

package com.xiaomi.youpin.gwdash.agent.processor;

import com.xiaomi.youpin.gwdash.bo.MachineBo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
public interface IProjectDeploymentService {

    void autoScale(String envId, String scaleType);

    int uptime(String envId);

    void updateResourceInfo(String ip, long bizId, int cpu, long mem, Set<Integer> ports, Map<String, String> labels);

    /**
     * 根据提供的envId 获取 此时此刻部署的机器列表
     *
     * @param envId
     * @return
     */
    List<MachineBo> getMachineList(long envId);

    /**
     * 根据envId 获取部署的机器列表(2分钟cache时间)
     *
     * @param envId
     * @return
     */
    List<MachineBo> getMachineListFromCache(final long envId);


    /**
     * 发送消息到飞书
     *
     * @param msg
     */
    void sendFeiShuMsg(String msg);

}
