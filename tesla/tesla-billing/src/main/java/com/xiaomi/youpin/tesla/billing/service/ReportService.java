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
import com.xiaomi.youpin.quota.bo.BizResource;
import com.xiaomi.youpin.quota.bo.Result;
import com.xiaomi.youpin.quota.service.ResourceService;
import com.xiaomi.youpin.tesla.billing.bo.*;
import com.xiaomi.youpin.tesla.billing.common.TimeUtils;
import com.xiaomi.youpin.tesla.billing.dataobject.*;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.cri.Static;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/8/4
 */
@Slf4j
@Service
public class ReportService {

    @Resource
    private NutDao dao;

    @Resource
    private ProductService productService;

    @Resource
    private CostService costService;


    @Reference(check = false, interfaceClass = ResourceService.class, group = "$quota_group")
    private ResourceService resourceService;

    @Resource
    private AccountService accountService;

    /**
     * 生成报表(月维度)  这个月从1号到现在花了多少钱
     * <p>
     * 给业务看的
     *
     * @param reportBo
     */
    public ReportBo generateReportForMonth(ReportBo reportBo, int year, int month, long now) {
        long bizId = reportBo.getBizId();
        log.info("generateReportForMonth {} {} {} {}", reportBo, year, month, now);
        List<Cost> list = null;
        if (bizId != 0) {
            //projectId 维度
            list = dao.query(Cost.class, Cnd.where("biz_id", "=", bizId));
        } else {
            //envId 维度
            long envId = reportBo.getSubBizId();
            list = dao.query(Cost.class, Cnd.where("sub_biz_id", "=", envId));
        }


        long monthBegin = TimeUtils.monthBegin(year, month);
        long monthEnd = TimeUtils.monthEnd(year, month);

        //计算价格
        Long price = list.stream().map(it -> {
            long beginTime = it.getBeginTime();
            long endTime = it.getEndTime();

            //从这个阅处开始算
            if (beginTime < monthBegin) {
                beginTime = monthBegin;
            }

            //时间周期超过了这个月的,但只需计算这个月的
            if (endTime > monthEnd) {
                endTime = monthEnd;
            }

            //目前还没有结束
            if (endTime == 0) {
                endTime = now;
            }

            //所使用的的时间
            long time = endTime - beginTime;
            long p = productService.getPrice(it.getProductId());

            float v = it.getUseCpuNum() * 1.0f / it.getCpuNum();

            return Float.valueOf(p * (time / TimeUnit.MINUTES.toMillis(1)) * v).longValue();
        }).reduce((a, b) -> a + b).get();

        Report report = new Report();
        report.setPrice(price);
        dao.insert(report);

        ReportBo res = new ReportBo();
        res.setPrice(report.getPrice());
        res.setType(report.getType());
        res.setBizId((int)bizId);

        log.info("res:{}", res);

        return res;
    }

    /**
     * 开通(init)
     *
     * @param reportBo
     */
    public void init(ReportBo reportBo) {
        long bizId = reportBo.getBizId();
        Account account = dao.fetch(Account.class, Cnd.where("biz_id", "=", bizId));
        if (null == account) {
            reportBo.getSubBizIdList().stream().forEach(envId -> {
                Result<List<com.xiaomi.youpin.quota.bo.ResourceBo>> res = resourceService.getResourceByEnvId(envId.intValue());
                List<ResourceBo> list = res.getData().stream().map(it -> {
                    ResourceBo resourceBo = new ResourceBo();
                    resourceBo.setResourceKey(it.getIp());
                    ResourceUseInfo info = new ResourceUseInfo();
                    info.setCpuNum(it.getCpu());

                    BizResource data =it.getBizIds().get(Long.valueOf(envId));

                    if (null != data) {
                        log.info("init resource:{}", data);
                        info.setUseCpuNum(data.getCpus().size());
                    } else {
                        info.setUseCpuNum(1);
                        log.error("init error:{} {}", envId, it.getId());
                    }
                    resourceBo.setResourceInfo(info);
                    resourceBo.setBizId(bizId);
                    resourceBo.setSubBizId(envId);
                    return resourceBo;
                }).collect(Collectors.toList());
                CostBo cost = new CostBo();
                LocalDate ld = LocalDate.now();
                //从月初开始
                cost.setBeginTime(TimeUtils.monthBegin(ld.getYear(), ld.getMonthValue()));
                cost.setResourceBoList(list);
                costService.beginCost(cost);
            });

            AccountBo accountBo = new AccountBo();
            accountBo.setBizId(reportBo.getBizId());
            accountBo.setSubBizIdList(reportBo.getSubBizIdList());
            accountService.createAccount(accountBo);
        }
    }


    /**
     * 和云平台对账的记录(一个月生成一次)
     */
    public List<ResourceRecord> reconciliation(int year, int month) {
        List<ResourceDo> list = dao.query(ResourceDo.class, null);

        return list.stream().map(it -> {
            Product product = productService.getProduct(it.getProductId());
            ResourceRecord record = new ResourceRecord();
            record.setResourceKey(it.getResourceKey());
            //包月的
            if (product.getType() == Product.ProductType.month.ordinal()) {
                record.setPrice(product.getPrice());
            }

            //按分钟计费
            if (product.getType() == Product.ProductType.minute.ordinal()) {
                //TODO 这里可以修改成sql
                List<ResourceOperatingRecord> rlist = dao.query(ResourceOperatingRecord.class, Cnd.where("resource_id", "=", it.getId()));

                long monthBegin = TimeUtils.monthBegin(year, month);
                long monthEnd = TimeUtils.monthEnd(year, month);

                //查出总用时
                OptionalLong useTime = rlist.stream().mapToLong(it2 -> {
                    long endTime = it2.getEndTime();


                    //不是这个月内的,不需要计算
                    if (endTime < monthBegin) {
                        return 0;
                    }


                    //计费还没有结束
                    if (endTime == 0) {
                        endTime = monthEnd;
                    }

                    long beginTime = it2.getBeginTime();
                    if (beginTime < monthBegin) {
                        beginTime = monthBegin;
                    }

                    long time = endTime - beginTime;
                    return time;
                }).reduce((a, b) -> a + b);

                //计算价格
                record.setPrice(product.getPrice() * (useTime.getAsLong() / TimeUnit.MINUTES.toMillis(1)));
            }
            return record;
        }).collect(Collectors.toList());


    }

    public List<Report> getBillingDetail(long startTime, long endTime, long accountId, long environment) {
        log.info("startTime:{}，endTime:{}，accountId:{}，environment:{}", startTime, endTime, accountId, environment);
        List<Report> list = dao.query(Report.class, Cnd.where(new Static(" ctime between " + startTime + " and " + endTime)).and("type", "=", 2).and("ctime", ">=", startTime).and("account_id", "=", accountId).and("sub_biz_id", "=", environment));
        return list;
    }

    public List<Report> getAppManagementBillingDetail(long startTime, long endTime, long accountId) {
        log.info("startTime:{}，endTime:{}，accountId:{}", startTime, endTime, accountId);
        List<Report> list = dao.query(Report.class, Cnd.where(new Static(" ctime between " + startTime + " and " + endTime)).and("type", "=", 1).and("account_id", "=", accountId));
        return list;
    }

}

