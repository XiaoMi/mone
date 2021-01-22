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

package com.xiaomi.youpin.tesla.billing.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.tesla.billing.bo.BillingOperationBo;
import com.xiaomi.youpin.tesla.billing.bo.CostBo;
import com.xiaomi.youpin.tesla.billing.bo.ResourceBo;
import com.xiaomi.youpin.tesla.billing.bo.ResourceUseInfo;
import com.xiaomi.youpin.tesla.billing.common.BillingOperationTypeEnum;
import com.xiaomi.youpin.tesla.billing.common.BillingPlatformEnum;
import com.xiaomi.youpin.tesla.billing.common.BillingTypeEnum;
import com.xiaomi.youpin.tesla.billing.dataobject.ResourceOperatingRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenghao
 * @description: 计费操作消费者
 */
@Slf4j
@Service
public class BillingOperationMqConsumer {

    @Resource
    private DefaultMQPushConsumer consumer;

    @Resource
    private NutDao dao;

    @Resource
    private CostService costService;

    @Resource
    private ResourceService resourceService;

    @Value("$billing_operation_topic")
    private String topic;

    public void init() {
        log.info("BillingOperationMqConsumer init");
        try {
            consumer.subscribe(topic, "");
        } catch (MQClientException e) {
            log.error("BillingOperationMqConsumer error", e);
        }

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            log.info(Thread.currentThread().getName() + " Receive New Messages: " + msgs);

            //返回消费状态
            //CONSUME_SUCCESS 消费成功
            //RECONSUME_LATER 消费失败，需要稍后重新消费
            // todo 升配降配 如果资源数量发生变化，会有问题
            Gson gson = new Gson();
            BillingOperationBo billingOperationBo = null;
            for (MessageExt ext : msgs) {
                try {
                    String s = new String(ext.getBody(), "UTF-8");
                    billingOperationBo = gson.fromJson((s), BillingOperationBo.class);
                } catch (UnsupportedEncodingException e) {
                    log.error("BillingOperationMqConsumer UnsupportedEncodingException error", e);
                }
            }

            //BillingOperationBo billingOperationBo = gson.fromJson(new String(msgs.get()), BillingOperationBo.class);
            log.info(billingOperationBo.toString());

            if (billingOperationBo.getBillingPlatform() == BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode()) {
                this.idc(billingOperationBo);
            } else {
                log.error("计费平台错误");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        //调用start()方法启动consumer
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("fail to start consume, nameSerAddress");
        }
        log.info("BillingOperationMqConsumer Started.");
    }

    /**
     * idc机房
     *
     * @param billingOperationBo
     */
    private void idc(BillingOperationBo billingOperationBo) {
        // idc只有按分钟
        if (billingOperationBo.getBillingType() == BillingTypeEnum.BY_MINUTE.getCode()) {
            // 按分钟
            this.byMintue(billingOperationBo);
        } else {
            log.error("idc计费类型错误");
        }
    }

    /**
     * 云平台机房
     *
     * @param billingOperationBo
     */
    private void cloud(BillingOperationBo billingOperationBo) {
        // 云平台 有包年包月和按分钟
        if (billingOperationBo.getBillingType() == BillingTypeEnum.BY_MINUTE.getCode()) {
            // 按分钟
            this.byMintue(billingOperationBo);
        } else if (billingOperationBo.getBillingType() == BillingTypeEnum.ALL_YEAR_ALL_DAY.getCode()) {
            // 包年包月
            this.allYealAllMonth(billingOperationBo);
        } else {
            log.error("云平台计费类型错误");
        }
    }

    /**
     * 包年包月计费
     *
     * @param billingOperationBo
     */
    private void allYealAllMonth(BillingOperationBo billingOperationBo) {
        List<String> resourceListIds = billingOperationBo.getResourceIds();
        Gson gson = new Gson();

        if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.CREATE_RESOURCE.getCode()) {
            // 创建资源
            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.beginCostResourceBoListInit(billingOperationBo, resourceListIds));
            // 批量
            costService.beginCost(costBo);

        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.DESTROY_RESOURCE.getCode()) {
            // 销毁资源

            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.stopCostResourceBoListInit(resourceListIds));
            // 批量
            costService.stopCost(costBo);
        } else {
            log.error("包年包月计费操作类型错误");
        }
    }

    /**
     * 按分钟计费
     *
     * @param billingOperationBo
     */
    private void byMintue(BillingOperationBo billingOperationBo) {

        //资源列表(机器就是ip列表)
        List<String> resourceListIds = billingOperationBo.getResourceIds();

        if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.CREATE_RESOURCE.getCode()) {
            // 创建资源

            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.beginCostResourceBoListInit(billingOperationBo, resourceListIds));
            // 批量
            costService.beginCost(costBo);

            //同时也上线机器了
            if (billingOperationBo.getSubType() == BillingOperationBo.SubType.machine.ordinal()) {
                resourceListIds.stream().forEach(resourceKey->{
                    ResourceOperatingRecord record = new ResourceOperatingRecord();
                    record.setResourceKey(resourceKey);
                    resourceService.addRecord(record, billingOperationBo.getSendTime());
                });
            }

        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.DESTROY_RESOURCE.getCode()) {
            // 销毁资源

            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.stopCostResourceBoListInit(resourceListIds));
            // 批量
            costService.stopCost(costBo);

            //同时也下线机器了
            if (billingOperationBo.getSubType() == BillingOperationBo.SubType.machine.ordinal()) {
                resourceListIds.stream().forEach(resourceKey -> {
                    resourceService.closeRecord(resourceKey, billingOperationBo.getSendTime());
                });
            }

        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.RISE_RESOURCE.getCode()) {
            // 升配
            // 先停止原有计费
            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.stopCostResourceBoListInit(resourceListIds));
            // 批量
            costService.stopCost(costBo);

            // 再启动新计费
            CostBo costBoBegin = new CostBo();
            costBoBegin.setType("1");
            // 系统账户
            costBoBegin.setAccountId((int)billingOperationBo.getAccountId());
            costBoBegin.setResourceBoList(this.beginCostResourceBoListInit(billingOperationBo, resourceListIds));
            // 批量
            costService.beginCost(costBoBegin);


        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.DROP_RESOURCE.getCode()) {
            // 降配
            // 先停止原有计费
            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.stopCostResourceBoListInit(resourceListIds));
            // 批量
            costService.stopCost(costBo);

            // 再启动新计费
            CostBo costBoBegin = new CostBo();
            costBoBegin.setType("1");
            // 系统账户
            costBoBegin.setAccountId((int)billingOperationBo.getAccountId());
            costBoBegin.setResourceBoList(this.beginCostResourceBoListInit(billingOperationBo, resourceListIds));
            // 批量
            costService.beginCost(costBoBegin);

        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.START_RESOURCE.getCode()) {
            // 启动服务

            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.beginCostResourceBoListInit(billingOperationBo, resourceListIds));
            // 批量
            costService.beginCost(costBo);

        } else if (billingOperationBo.getBillingOperation() == BillingOperationTypeEnum.STOP_RESOURCE.getCode()) {
            // 停止服务

            CostBo costBo = new CostBo();
            costBo.setType("1");
            // 系统账户
            costBo.setAccountId((int)billingOperationBo.getAccountId());
            costBo.setResourceBoList(this.stopCostResourceBoListInit(resourceListIds));
            // 批量
            costService.stopCost(costBo);

        } else {
            log.error("按分钟计费操作类型错误");
        }
    }

    /**
     * 停止计费数据组装
     *
     * @param resourceListIds
     * @return
     */
    private List<ResourceBo> stopCostResourceBoListInit(List<String> resourceListIds) {
        List<ResourceBo> resourceBoList = new ArrayList<>();
        resourceListIds.forEach(it -> {
            ResourceBo resourceBo = new ResourceBo();
            resourceBo.setResourceKey(it);
            resourceBoList.add(resourceBo);
        });
        return resourceBoList;
    }

    /**
     * 开始计费数据组装
     *
     * @param billingOperationBo
     * @param resourceListIds
     * @return
     */
    private List<ResourceBo> beginCostResourceBoListInit(BillingOperationBo billingOperationBo, List<String> resourceListIds) {
        List<ResourceBo> resourceBoList = new ArrayList<>();
        resourceListIds.forEach(it -> {
            ResourceBo resourceBo = new ResourceBo();
            resourceBo.setProdcutId(2);// todo
            resourceBo.setResourceKey(it);
            resourceBo.setBizId(billingOperationBo.getBizId());
            resourceBo.setSubBizId(billingOperationBo.getSubBizId());

            ResourceUseInfo resourceUseInfo = new ResourceUseInfo();
            // 总CPU
            resourceUseInfo.setCpuNum(Integer.valueOf(billingOperationBo.getAllNorms().getCpu()));
            // 使用cpu
            resourceUseInfo.setUseCpuNum(Integer.valueOf(billingOperationBo.getUseNorms().getCpu()));
            resourceBo.setResourceInfo(resourceUseInfo);
            resourceBoList.add(resourceBo);
        });
        return resourceBoList;
    }

    public void test(BillingOperationBo billingOperationBo) {
//        Gson gson = new Gson();
//        BillingOperationBo billingOperationBo = gson.fromJson(new String(test.toString()), BillingOperationBo.class);
        log.info(billingOperationBo.toString());
        //

        if (billingOperationBo.getBillingPlatform() == BillingPlatformEnum.CLOUD_COMPUTER_ROOM.getCode()) {
            // 云平台
            this.cloud(billingOperationBo);
        } else if (billingOperationBo.getBillingPlatform() == BillingPlatformEnum.IDC_COMPUTER_ROOM.getCode()) {
            // idc
            this.idc(billingOperationBo);
        } else {
            log.error("计费平台错误");
        }
    }

}