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

import com.google.gson.Gson;
import com.xiaomi.data.push.micloud.MiCloud;
import com.xiaomi.data.push.micloud.bo.request.CatalystRequest;
import com.xiaomi.data.push.micloud.bo.response.CatalystResponse;
import com.xiaomi.data.push.micloud.bo.response.Data;
import com.xiaomi.data.push.micloud.bo.response.OrderDetail;
import com.xiaomi.data.push.micloud.bo.response.SubmitOrder;
import com.xiaomi.youpin.gwdash.bo.ApplyMachineBo;
import com.xiaomi.youpin.gwdash.bo.ApplyMachineParam;
import com.xiaomi.youpin.gwdash.common.ApplyMachineEnum;
import com.xiaomi.youpin.gwdash.service.factory.ApplyMachineParamsFactory;
import com.xiaomi.youpin.gwdash.service.factory.IApplyMachineParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zhangjunyi
 * created on 2020/6/24 3:17 下午
 */
@Service
@Slf4j
public class ApplyMachineService {

    @Autowired
    private Dao dao;

    @Autowired
    private ApplyMachineParamsFactory applyMachineParamsFactory;

    @Autowired
    private MiCloud miCloud;

    private static final String token ="service-mione;c039e64387a72d66ae7ef4010b904d05;c039e64387a72d66ae7ef4010b904d05";

    public Map<String, Object> list(int page, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("total", dao.count(ApplyMachineBo.class));
        map.put("list", dao.query(ApplyMachineBo.class,
                Cnd.orderBy().desc("id"),
                new Pager(page, pageSize)));
        return map;
    }

    public Object info() {
        return miCloud.getProviderInfo("aliyun", token);
    }

    public OrderDetail orderDetail(int orderId) {
        if (0 == orderId) {
            log.info("ApplyMachineService#orderDetail orderId is 0");
            return null;
        }
        ApplyMachineBo applyMachineBo = dao.fetch(ApplyMachineBo.class, Cnd.where("order_id", "=", orderId));
        if (null == applyMachineBo) {
            log.info("ApplyMachineService#orderDetail applyMachineBo is null for orderId {}", orderId);
            return null;
        }
        OrderDetail orderDetail = miCloud.orderDetail(orderId,token);
        applyMachineBo.setOrderDetail(orderDetail);
        dao.update(applyMachineBo);
        return orderDetail;
    }

    public Boolean applyMachine(String username, ApplyMachineParam param) {
        String suitId = param.getSuitId();
        String siteId = param.getSiteId();
        String env = param.getEnv();
        Optional<ApplyMachineEnum> optional = ApplyMachineEnum.getApplyMachineEnum(suitId, siteId, env);
        if (optional.isPresent()) {
            ApplyMachineEnum applyMachineEnum = optional.get();
            long now = System.currentTimeMillis();
            ApplyMachineBo applyMachineBo = new ApplyMachineBo();
            applyMachineBo.setCreator(username);
            applyMachineBo.setUtime(now);
            applyMachineBo.setCtime(now);
            applyMachineBo.setEnv(env);
            applyMachineBo.setSuitId(suitId);
            applyMachineBo.setSiteId(siteId);
            try {
                IApplyMachineParam iApplyMachineParam = applyMachineParamsFactory.getParamMake(applyMachineEnum.getParam());
                if (null != iApplyMachineParam) {
                    SubmitOrder orderSubmitRes = miCloud.submitOrder(iApplyMachineParam.getOrderInfo(suitId, siteId), token);
                    if (null != orderSubmitRes) {
                        applyMachineBo.setOrderId(orderSubmitRes.getOrderId());
                        applyMachineBo.setOrderRes(new Gson().toJson(orderSubmitRes));
                    }
                }
            } finally {
                dao.insert(applyMachineBo);
            }
            return true;
        }
        return false;
    }

    public CatalystResponse initMachine(int orderId) {
        if (0 == orderId) {
            log.info("ApplyMachineService#initMachine orderId is 0");
            return null;
        }
        ApplyMachineBo applyMachineBo = dao.fetch(ApplyMachineBo.class, Cnd.where("order_id", "=", orderId));
        if (null == applyMachineBo) {
            log.info("ApplyMachineService#initMachine applyMachineBo is null for orderId {}", orderId);
            return null;
        }
        String env = applyMachineBo.getEnv();
        String suitId = applyMachineBo.getSuitId();
        String siteId = applyMachineBo.getSiteId();
        Optional<ApplyMachineEnum> optional = ApplyMachineEnum.getApplyMachineEnum(suitId, siteId, env);
        OrderDetail orderDetail = applyMachineBo.getOrderDetail();
        if (optional.isPresent() && null != orderDetail
                && null != orderDetail.getSuborders()) {
            List<String> ips = new Vector<>();
            orderDetail.getSuborders().stream().forEach(suborder -> {
                suborder.getDeliveryInfo().stream().forEach(deliveryInfo -> {
                    if (StringUtils.isNotEmpty(deliveryInfo.getIpaddr())) {
                        ips.add(deliveryInfo.getIpaddr());
                    }
                });
            });
            if (ips.size() > 0) {
                // 发送初始化请求
                CatalystRequest request = new CatalystRequest();
                request.setProvider(optional.get().getProvider());
                request.setIps(ips);
                CatalystResponse catalystResponse = miCloud.initMachine(request);
                if (null != catalystResponse) {
                    applyMachineBo.setCatalystResponse(catalystResponse);
                    Data data = catalystResponse.getData();
                    if (null != data) {
                        applyMachineBo.setInitSequence(data.getSequence());
                    }
                    dao.update(applyMachineBo);
                }
                return catalystResponse;
            }
        }
        return null;
    }

    public CatalystResponse getInitMachineInfo(int orderId) {
        if (0 == orderId) {
            return null;
        }
        ApplyMachineBo applyMachineBo = dao.fetch(ApplyMachineBo.class, Cnd.where("order_id", "=", orderId));
        if (null == applyMachineBo) {
            return null;
        }
        String sequence = applyMachineBo.getInitSequence();
        if (StringUtils.isEmpty(sequence)) {
            return null;
        }
        return miCloud.machineInfo(sequence);
    }
}
