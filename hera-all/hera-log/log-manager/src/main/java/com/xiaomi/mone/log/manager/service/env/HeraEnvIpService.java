/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.env;

import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.mone.log.manager.model.vo.LogAgentListBo;

import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 19:25
 */
public interface HeraEnvIpService {
    /**
     * 查询该node下所有的pod信息
     *
     * @param nodeIp
     * @return
     */
    List<LogAgentListBo> queryInfoByNodeIp(String nodeIp);

    Map<String, List<LogAgentListBo>> queryAgentIpByPodIps(List<String> podIps);

    /**
     * 根据pode Ip查询node ip
     *
     * @param ips
     * @return
     */
    List<LogPattern.IPRel> queryActualIps(List<String> ips, String agentIp);
}
