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

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.quota.bo.Result;
import com.xiaomi.youpin.quota.service.ResourceService;
import com.xiaomi.youpin.tesla.billing.bo.CostBo;
import com.xiaomi.youpin.tesla.billing.common.TimeUtils;
import com.xiaomi.youpin.tesla.billing.dataobject.*;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.cri.Static;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/8/4
 */
@Service
@Slf4j
public class CostService {

    @Resource
    private NutDao dao;

    @Resource
    private ProductService productService;

    @Reference(check = false, interfaceClass = com.xiaomi.youpin.quota.service.ResourceService.class, group = "$quota_group")
    private ResourceService resourceService;

    /**
     * 开始计费
     *
     * @param costBo
     */
    public void beginCost(CostBo costBo) {
        long now = costBo.getTime();
        costBo.getResourceBoList().forEach(it -> {

            ResourceDo rd = dao.fetch(ResourceDo.class, Cnd.where("resource_key", "=", it.getResourceKey()));
            Product product = dao.fetch(Product.class, Cnd.where("name", "=", "1cpu").and("type", "=", "1").limit(1).orderBy("id", "asc"));
            //此资源没有录入过
            if (null == rd) {
                ResourceDo resourceDo = new ResourceDo();
                resourceDo.setResourceKey(it.getResourceKey());
                if (product != null) {
                    resourceDo.setProductId(product.getId());
                } else {
                    log.error("product is null");
                    resourceDo.setProductId(1);
                }

                rd = dao.insert(resourceDo);
            }

            //消费表记录
            Cost cost = new Cost();
            cost.setStatus(0);
            cost.setProductId(product.getId());
            cost.setAccountId(costBo.getAccountId());
            cost.setResourceId(rd.getId());
            cost.setCtime(now);
            cost.setUtime(now);
            cost.setBizId(it.getBizId());
            cost.setSubBizId(it.getSubBizId());
            if (null != costBo.getBeginTime()) {
                cost.setBeginTime(costBo.getBeginTime());
            } else {
                cost.setBeginTime(now);
            }

            //计算费用先使用cpu一个维度
            //总的cpu数量
            cost.setCpuNum(it.getResourceInfo().getCpuNum());
            //使用的cpu数量
            cost.setUseCpuNum(it.getResourceInfo().getUseCpuNum());


            cost = dao.insert(cost);

            //记录消费资源表
            ResourceCost rc = new ResourceCost();
            rc.setResourceId(rd.getId());
            rc.setCostId(cost.getId());
            rc.setBizId(it.getBizId());
            rc.setSubBizId(it.getSubBizId());
            rc.setResourceKey(it.getResourceKey());
            dao.insert(rc);


        });
    }

    /**
     * 停止计费 (对业务方而言,所有资源都是按分钟计费的)
     *
     * @param costBo
     */
    public void stopCost(CostBo costBo) {
        costBo.getResourceBoList().forEach(it -> {
            int status = 1;
            ResourceDo rd = dao.fetch(ResourceDo.class, Cnd.where("resource_key", "=", it.getResourceKey()));
            dao.update(Cost.class,
                    Chain.make("status", status)
                            .add("end_time", costBo.getTime())
                    , Cnd.where("resource_id", "=", rd.getId()));
        });
    }


    /**
     * 每日出账
     */
    public void costByDay() {
//        long begin = TimeUtils.dayBegin();
//        long end = TimeUtils.dayEnd();

        List<ResourceDo> resourceDoList = dao.query(ResourceDo.class, Cnd.where("status", "=", 0));
        if (resourceDoList.size() > 0) {
            log.info("costByDay resourceDoList size:{}", resourceDoList.size());
            List<Report> reportList = new ArrayList<>();
            resourceDoList.forEach(it -> {

                //Product product = dao.fetch(Product.class, Cnd.where("id", "=", it.getProductId()));
                Result<Long> ipPrice = resourceService.getPrice(it.getResourceKey());
                //System.out.println(ipPrice.toString());
                // 单位是分，换算成元
                Object object = ipPrice.getData();
                //Long temp = ipPrice.getData();
                //Integer priceYuan = (int)t / 100;
                Integer priceYuan = Integer.valueOf(object.toString()) / 100;
                //Integer priceYuan = 0;

                // 可能是一个集合
                List<ResourceCost> resourceCostList = dao.query(ResourceCost.class, Cnd.where("resource_id", "=", it.getId()));

                if (priceYuan != null && resourceCostList.size() > 0) {
                    resourceCostList.forEach(resourceCostListIt -> {

                        Cost cost = dao.fetch(Cost.class, Cnd.where("id", "=", resourceCostListIt.getCostId()).and("status", "=", "0"));
                        if (cost != null) {
                            Report report = new Report();
                            report.setName("");
                            // type=1 代表按日账单
                            report.setType(1);
                            report.setPrice(priceYuan * cost.getUseCpuNum());
                            report.setAccountId((int)resourceCostListIt.getBizId());
                            report.setSubBizId((int)resourceCostListIt.getSubBizId());
                            // 获取昨天时间
                            long time = TimeUtils.getYesterday();
                            report.setCtime(time);
                            log.info("reportList report:{}", report);
                            reportList.add(report);
                            //dao.insert(report);
                        } else {
                            log.error("Cost is null, product:{}, resourceCostList.size:{}", ipPrice.toString(), resourceCostList.size());
                        }

                    });

                } else {
                    log.error("Product || resourceCostList is null:{}", it.toString());
                }

            });
            log.info("reportList size:{}", reportList.size());
            if (reportList.size() > 0) {
                dao.fastInsert(reportList);
            }

        }
    }

    /**
     * 每月出账
     */
    public void costByMonth() {
        // 上个月第一天
        long begin = TimeUtils.lastMonthFirstDay();
        // 上个月最后一天
        long end = TimeUtils.lastMonthLastDay();
        log.info("costByMonth,begin:{},end:{}", begin, end);
        log.info("costByMonth,begin:{},end:{}", TimeUtils.Long2Date(begin), TimeUtils.Long2Date(end));
        List<Report> reportList = dao.query(Report.class, Cnd.where(new Static(" ctime between " + begin + " and " + end)).and("type", "=", 1));

        if (reportList.size() > 0) {
            log.info("costByMonth reportList size:{}", reportList.size());
            // todo 需要优化
            // key biz_id+'_'+sub_biz_id value 本月的价格
            Map<String, Long> priceMap = new HashMap<>();
            reportList.forEach(it -> {
                String key = it.getAccountId() + "_" + it.getSubBizId();
                if (priceMap.get(key) != null) {
                    // 有值要叠加
                    Long money = (Long)priceMap.get(key) + it.getPrice();
                    priceMap.put(key, money);
                } else {
                    // 没有直接put
                    priceMap.put(key, it.getPrice());
                }

            });

            List<Report> reportInsertList = new ArrayList<>();
            // 按照环境和系统id 生成月账单
            priceMap.forEach((key, value) -> {
                long bizId = Long.valueOf(key.split("_")[0]);
                long subBizId = Long.valueOf(key.split("_")[1]);
                Report report = new Report();
                report.setName("");
                // type=2 代表按月账单
                report.setType(2);
                report.setPrice(value);
                report.setAccountId((int)bizId);
                report.setSubBizId((int)subBizId);
                // 获取昨天时间
                long time = TimeUtils.getYesterday();
                report.setCtime(time);
                reportInsertList.add(report);
                //dao.insert(report);
            });
            if (reportInsertList.size() > 0) {
                dao.fastInsert(reportInsertList);
            }
        }
    }

}
