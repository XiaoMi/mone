///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.docker.Safe;
//import com.xiaomi.youpin.gwdash.bo.BillingReq;
//import com.xiaomi.youpin.gwdash.rocketmq.BillingRocketMQProvider;
//import com.xiaomi.youpin.tesla.billing.bo.BResult;
//import com.xiaomi.youpin.tesla.billing.bo.BillingOperationBo;
//import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
//import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
//import com.xiaomi.youpin.tesla.billing.common.BillingOperationTypeEnum;
//import com.xiaomi.youpin.tesla.billing.common.BillingPlatformEnum;
//import com.xiaomi.youpin.tesla.billing.common.BillingTypeEnum;
//import com.xiaomi.youpin.tesla.billing.dataobject.BillingNorms;
//import com.xiaomi.youpin.tesla.billing.service.BillingService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//
///**
// * @author goodjava@qq.com
// * billing 操作服务类
// */
//@Service
//@Slf4j
//public class ApiServerBillingService {
//
//
//    @Reference(group = "${ref.billing.service.group}", interfaceClass = BillingService.class, check = false)
//    private BillingService billingService;
//
//
//    @Autowired
//    private BillingRocketMQProvider billingRocketMQProvider;
//
//
//    /**
//     * 获取报表
//     *
//     * @return
//     */
//    public BResult<ReportRes> report(long projectId, long envId) {
//        log.info("report projectId:{} envId:{}", projectId, envId);
////        ReportBo reportBo = new ReportBo();
////        reportBo.setBizId(projectId);
////        reportBo.setSubBizId(envId);
////        LocalDate ld = LocalDate.now();
////        long now = System.currentTimeMillis();
//        return billingService.getBillingDetail(2020, projectId, envId);
//        // return billingService.generateBizReport(reportBo, ld.getYear(), ld.getMonthValue(), now);
//    }
//
//
//    /**
//     * 初始化报表内部数据
//     *
//     * @param reportBo
//     * @return
//     */
//    public BResult<ReportRes> initReport(ReportBo reportBo) {
//        return billingService.initReport(reportBo);
//    }
//
//
//    /**
//     * 下线一台机器(不再对此台机器计费)
//     */
//    public boolean offline(BillingReq req) {
//        log.info("offline:{}", req);
//        Safe.run(() -> {
//            BillingOperationBo bo = new BillingOperationBo();
//            bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
//            bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
//            bo.setBillingOperation(BillingOperationTypeEnum.DESTROY_RESOURCE.getCode());
//            bo.setSubType(req.getSubType());
//            bo.setAccountId(req.getProjectId());
//            bo.setOperationTime(System.currentTimeMillis());
//            bo.setResourceIds(req.getResourceKeyList());
//            bo.setBizId(req.getProjectId());
//            bo.setSubBizId(req.getEnvId());
//            bo.setSendTime(System.currentTimeMillis());
//
//
//            BillingNorms useBillingNorms = new BillingNorms();
//            useBillingNorms.setCpu(String.valueOf(req.getUseCpu()));
//            BillingNorms allNorms = new BillingNorms();
//            allNorms.setCpu(String.valueOf(req.getCpu()));
//            bo.setUseNorms(useBillingNorms);
//            bo.setAllNorms(allNorms);
//
//
//            billingRocketMQProvider.send(new Gson().toJson(bo));
//        });
//        return true;
//    }
//
//    /**
//     * 上线一台机器(开始对此台机器计费)
//     *
//     * @return
//     */
//    public boolean online(BillingReq req) {
//        log.info("online:{}", req);
//        Safe.run(() -> {
//            BillingOperationBo bo = new BillingOperationBo();
//            bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
//            bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
//            bo.setBillingOperation(BillingOperationTypeEnum.CREATE_RESOURCE.getCode());
//            bo.setSubType(req.getSubType());
//            bo.setAccountId(req.getProjectId());
//            bo.setOperationTime(System.currentTimeMillis());
//            bo.setResourceIds(req.getResourceKeyList());
//            bo.setBizId(req.getProjectId());
//            bo.setSubBizId(req.getEnvId());
//            bo.setSendTime(System.currentTimeMillis());
//
//            BillingNorms useBillingNorms = new BillingNorms();
//            useBillingNorms.setCpu(String.valueOf(req.getUseCpu()));
//            BillingNorms allNorms = new BillingNorms();
//            allNorms.setCpu(String.valueOf(req.getCpu()));
//            bo.setUseNorms(useBillingNorms);
//            bo.setAllNorms(allNorms);
//
//
//            billingRocketMQProvider.send(new Gson().toJson(bo));
//        });
//        return true;
//    }
//
//
//    /**
//     * 升级配置
//     * @param req
//     * @return
//     */
//    public boolean upgrade(BillingReq req) {
//        log.info("online:{}", req);
//        Safe.run(() -> {
//            BillingOperationBo bo = new BillingOperationBo();
//            bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
//            bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
//            bo.setBillingOperation(BillingOperationTypeEnum.RISE_RESOURCE.getCode());
//            bo.setSubType(req.getSubType());
//            bo.setAccountId(req.getProjectId());
//            bo.setOperationTime(System.currentTimeMillis());
//            bo.setResourceIds(req.getResourceKeyList());
//            bo.setBizId(req.getProjectId());
//            bo.setSubBizId(req.getEnvId());
//            bo.setSendTime(System.currentTimeMillis());
//
//
//            BillingNorms useBillingNorms = new BillingNorms();
//            useBillingNorms.setCpu(String.valueOf(req.getUseCpu()));
//            BillingNorms allNorms = new BillingNorms();
//            allNorms.setCpu(String.valueOf(req.getCpu()));
//            bo.setUseNorms(useBillingNorms);
//            bo.setAllNorms(allNorms);
//
//
//            billingRocketMQProvider.send(new Gson().toJson(bo));
//        });
//        return true;
//    }
//
//
//    /**
//     * 降低配置
//     * @param req
//     * @return
//     */
//    public boolean downgrade(BillingReq req) {
//        log.info("online:{}", req);
//        Safe.run(() -> {
//            BillingOperationBo bo = new BillingOperationBo();
//            bo.setBillingPlatform(BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode());
//            bo.setBillingType(BillingTypeEnum.BY_MINUTE.getCode());
//            bo.setBillingOperation(BillingOperationTypeEnum.DROP_RESOURCE.getCode());
//            bo.setSubType(req.getSubType());
//            bo.setAccountId(req.getProjectId());
//            bo.setOperationTime(System.currentTimeMillis());
//            bo.setResourceIds(req.getResourceKeyList());
//            bo.setBizId(req.getProjectId());
//            bo.setSubBizId(req.getEnvId());
//            bo.setSendTime(System.currentTimeMillis());
//
//            BillingNorms useBillingNorms = new BillingNorms();
//            useBillingNorms.setCpu(String.valueOf(req.getUseCpu()));
//            BillingNorms allNorms = new BillingNorms();
//            allNorms.setCpu(String.valueOf(req.getCpu()));
//            bo.setUseNorms(useBillingNorms);
//            bo.setAllNorms(allNorms);
//
//
//            billingRocketMQProvider.send(new Gson().toJson(bo));
//        });
//        return true;
//    }
//
//
//
//
//}
