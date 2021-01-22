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

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.AgentListResult;
import com.xiaomi.youpin.gwdash.bo.AgentUpdateParam;
import com.xiaomi.youpin.gwdash.bo.GatewayServerInfo;
import com.xiaomi.youpin.gwdash.bo.ListParam;
import com.xiaomi.youpin.gwdash.bo.StringIDsParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.AgentService;
import com.youpin.xiaomi.tesla.bo.GatewayInfo;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private Dao dao;

    @RequestMapping(value = "/api/agent/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<AgentListResult> getAgentList(@RequestBody ListParam param) {
        LOGGER.info("[AgentController.getAgentList] param: {}", param);
        return agentService.getAgentList();

    }

    @RequestMapping(value = "/api/agent/del", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Integer> delAgentInfo(@RequestBody StringIDsParam param) {
        LOGGER.info("[AgentController.delAgentInfo] param: {}", param);
        dao.clear(GatewayServerInfo.class, Cnd.where("key", "in", param.getIds()));
        return agentService.delAgents(param);
    }

    @RequestMapping(value = "/api/agent/update", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> updateAgentInfo(@RequestBody AgentUpdateParam param) {
        LOGGER.info("[AgentController.updateAgentInfo] param: {}", param);
        return agentService.updateAgentGroup(param);
    }

    @RequestMapping(value = "/api/agent/datail/info", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<GatewayInfo> getAgentDetailInfo(@RequestBody AgentUpdateParam param) {
        LOGGER.info("[AgentController.getAgentDetailInfo] param: {}", param);
        return agentService.getAgentDetailInfo(param.getIp(), param.getPort());
    }
}
