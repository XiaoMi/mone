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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.BizUtils;
import com.xiaomi.youpin.gwdash.common.CheckResult;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.context.DashServerContext;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import com.youpin.xiaomi.tesla.bo.GatewayInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.router.address.Address;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentService.class);

    @Autowired
    private DashServerContext opsServerContext;

    @Autowired
    private TenantComponent tenantComponent;

    @Autowired
    private TeslaGatewayServiceGroup teslaGatewayServiceGroup;

    @Autowired
    private Dao dao;

    public Result<AgentListResult> getAgentList() {
        String tenant = tenantComponent.getTenement();
        AgentListResult ret = new AgentListResult();
        List<GatewayServerInfo> list = dao.query(GatewayServerInfo.class,
                Cnd.where("tenant", "=", tenant).and("utime", ">", System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)));
        List<AgentDetail> agents = list.stream().map(it -> {
            AgentDetail agentDetail = new AgentDetail();
            agentDetail.setId(it.getKey());
            agentDetail.setServerName("GateWay");
            agentDetail.setIp(it.getHost());
            agentDetail.setPort(String.valueOf(it.getPort()));
            agentDetail.setUtime(it.getUtime());
            agentDetail.setGroup(it.getGroup());
            return agentDetail;
        }).collect(Collectors.toList());

        ret.setTotal(agents.size());

        ret.setAgentList(agents);
        LOGGER.debug("[ApiInfoService.getApiList] result: {}", ret);

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<GatewayServerInfo> getByIp(String ip) {
        try {
            GatewayServerInfo gatewayServerInfo = dao.fetch(GatewayServerInfo.class, Cnd.where("host", "=", ip));
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), gatewayServerInfo);
        } catch (Exception e) {
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), null);
        }
    }

    public Result<Integer> delAgents(StringIDsParam param) {
        if (param == null || param.getIds() == null || param.getIds().size() <= 0) {
            LOGGER.error("[AgentService.delAgents] invalid id list param: {}", param);
            return new Result<>(CommonError.InvalidIDParamError.getCode(), "无效的id参数列表");
        }
        param.getIds().stream().forEach(it -> {
            opsServerContext.delGatewayInfo(it);
        });
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), null);
    }

    public Result<Void> updateAgentGroup(AgentUpdateParam param) {
        CheckResult checkResult = BizUtils.chkAgentUpdateParam(param);
        if (!checkResult.isValid()) {
            LOGGER.error("[AgentService.updateAgent] invalid param, check msg: {}, param: {}",
                    checkResult.getMsg(), param);
            return new Result<>(checkResult.getCode(), checkResult.getMsg());
        }
        GatewayInfo gatewayInfo = new GatewayInfo();
        gatewayInfo.setIp(param.getIp());
        gatewayInfo.setKey(param.getId());
        gatewayInfo.setPort(Integer.valueOf(param.getPort()));
        gatewayInfo.setGroup(param.getGroup());
        opsServerContext.addOrUpdateGatewayInfoGroup(gatewayInfo);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Result<Map> getAgentDetailInfo(String ip, String port) {
        try {
            Address address = new Address(ip, Integer.parseInt(port));
            RpcContext.getContext().setAttachment("address", address);
            String tenant = tenantComponent.getTenement();
            Map obj = (Map) teslaGatewayServiceGroup.getGatewayInfo(tenant);
            Map data = (Map) obj.get("data");
            return Result.success(data);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } finally {
            RpcContext.getContext().clearAttachments();
        }
    }
}