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

import com.xiaomi.youpin.gwdash.bo.BillingReq;
import com.xiaomi.youpin.gwdash.bo.DockerQueryParam;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.selector.Selector;
import com.xiaomi.youpin.quota.bo.ModifyQuotaRes;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 用来筛选docker机器
 */
@Service
@Slf4j
public class DockerMachineSelector2 implements Selector<MachineBo, DockerQueryParam> {


    @Reference(group = "${ref.quota.service.group}", interfaceClass = QuotaService.class, check = false, retries = 0)
    private QuotaService quotaService;

    @Autowired
    private ApiServerBillingService billingService;

    @Override
    public List<MachineBo> select(DockerQueryParam param) {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setCpu(param.getCpuNum());
        quotaInfo.setBizId(param.getEnvId());
        quotaInfo.setProjectId(param.getProjectId());
        quotaInfo.setNum((int) param.getNum());
        quotaInfo.setMem(param.getMem());
        quotaInfo.setPorts(param.getPorts());
        quotaInfo.setLabels(param.getLabels());
        ModifyQuotaRes res = quotaService.modifyQuota(quotaInfo);

        //这次操作后,实际拥有的机器数量
        param.setRealNum(res.getCurrIps().size());

        //如果是扩容,就拿扩容的机器,否则就拿到全部机器
        List<ResourceBo> ips = param.isExpansion() ? res.getIps() : res.getCurrIps();

        //发生了删除机器的情况
        if (res.getType().equals("remove")) {
            List<MachineBo> removeIps = res.getIps().stream().map(it -> {
                MachineBo bo = new MachineBo();
                bo.setIp(it.getIp());
                bo.setCpuCore(it.getCpuCore());
                return bo;
            }).collect(Collectors.toList());
            param.setRemoveMachines(removeIps);
        }

        //正常的获取机器列表(但可能发生升配或者降配)
        if (res.getType().equals("get")) {
            //发生了升配置
            if (res.getSubType() == ModifyQuotaRes.SubType.upgrade.ordinal() && res.isSuccess()) {
                log.info("upgrade success");
                BillingReq billingReq = new BillingReq();
                billingReq.setResourceKeyList(ips.stream().map(it -> it.getIp()).collect(Collectors.toList()));
                billingReq.setType(BillingReq.BillingType.upgrade.ordinal());
                billingReq.setEnvId(param.getEnvId());
                billingReq.setProjectId(param.getProjectId());
                billingReq.setCpu(res.getCpu());
                billingReq.setUseCpu(res.getUseCpu());
                billingService.upgrade(billingReq);
            }

            //发生了降配
            if (res.getSubType() == ModifyQuotaRes.SubType.downgrade.ordinal() && res.isSuccess()) {
                log.info("downgrade success");
                BillingReq billingReq = new BillingReq();
                billingReq.setResourceKeyList(ips.stream().map(it -> it.getIp()).collect(Collectors.toList()));
                billingReq.setType(BillingReq.BillingType.upgrade.ordinal());
                billingReq.setEnvId(param.getEnvId());
                billingReq.setProjectId(param.getProjectId());
                billingReq.setCpu(res.getCpu());
                billingReq.setUseCpu(res.getUseCpu());
                billingService.downgrade(billingReq);
            }
        }


        return ips.stream().map(it -> {
            MachineBo bo = new MachineBo();
            bo.setIp(it.getIp());
            bo.setCpuCore(it.getCpuCore());
            return bo;
        }).collect(Collectors.toList());
    }
}
