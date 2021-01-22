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

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.tesla.billing.bo.BResult;
import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
import com.xiaomi.youpin.tesla.billing.bo.ResourceRecord;
import com.xiaomi.youpin.tesla.billing.common.CommonError;
import com.xiaomi.youpin.tesla.billing.common.TimeUtils;
import com.xiaomi.youpin.tesla.billing.dataobject.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 * 提供对外的dubbo 接口
 */
@Slf4j
@Service(group = "online",interfaceClass = BillingService.class)
public class BillingServiceImpl implements BillingService {

    @Resource
    private ReportService reportService;

    @Resource
    private CostService costService;


    @Override
    public BResult<ReportRes> generateBizReport(ReportBo req, int year, int month, long now) {
        ReportBo reportBo = reportService.generateReportForMonth(req, year, month, now);
        BResult<ReportRes> bResult = new BResult<ReportRes>();
        ReportRes reportRes = new ReportRes();
        reportRes.setPrice(reportBo.getPrice());
        bResult.setCode(CommonError.Success.code);
        bResult.setData(reportRes);
        bResult.setMessage(CommonError.Success.message);
        return bResult;
    }

    @Override
    public BResult<ReportRes> generateCloudReport(int year, int month) {
        List<ResourceRecord> resourceRecordList = reportService.reconciliation(year, month);
        long price = resourceRecordList.stream().mapToLong(ResourceRecord::getPrice).sum();
        BResult<ReportRes> bResult = new BResult<ReportRes>();
        ReportRes reportRes = new ReportRes();
        reportRes.setPrice(price);
        bResult.setCode(CommonError.Success.code);
        bResult.setData(reportRes);
        bResult.setMessage(CommonError.Success.message);
        return bResult;
    }

    @Override
    public void billingTaskDay() {
        log.info("billingTaskDay start");
        costService.costByDay();
        log.info("billingTaskDay end");
    }

    @Override
    public void billingTaskMonth() {
        log.info("billingTaskMonth start");
        costService.costByMonth();
        log.info("billingTaskMonth end");
    }

    @Override
    public BResult<ReportRes> getAppManagementBillingDetail(long accountId) {
        long thisMonthFirstDay = TimeUtils.thisMonthFirstDay();
        long today = TimeUtils.dayEnd();
        log.info("accountId:{},thisMonthFirstDay:{},today:{}", accountId, thisMonthFirstDay, today);
        String startTime = TimeUtils.Long2StringDate(thisMonthFirstDay);
        String endTime = TimeUtils.Long2StringDate(today);
        log.info("startTime:{},endTime:{}", startTime, endTime);
        List<Report> reportList = reportService.getAppManagementBillingDetail(thisMonthFirstDay, today, accountId);

        List<ReportBo> reportBoList = new ArrayList<>();
        //BigDecimal bigDecimal = new BigDecimal(0);
        AtomicLong temp = new AtomicLong();
        Map<Integer, Long> map = new HashMap<>();
        reportList.forEach(it -> {
            //ReportBo reportBo = new ReportBo();
            //BeanUtils.copyProperties(it, reportBo);
            //reportBo.setBizId(it.getAccountId());
            //reportBoList.add(reportBo);
            Long subBizId = map.get(it.getSubBizId());
            if (subBizId == null) {
                map.put(it.getSubBizId(), it.getPrice());
            } else {
                map.put(it.getSubBizId(), it.getPrice() + subBizId);
            }
            temp.addAndGet(it.getPrice());

            //bigDecimal.add(new BigDecimal(it.getPrice()));
            //System.out.println(bigDecimal.intValue());
        });
        // 按照环境id 算sum
        map.forEach((k, v) -> {
            ReportBo reportBo = new ReportBo();
            reportBo.setBizId((int)accountId);
            reportBo.setPrice(v);
            reportBo.setSubBizId(k);
            reportBoList.add(reportBo);
        });

        BResult<ReportRes> bResult = new BResult<>();
        ReportRes reportRes = new ReportRes();
        reportRes.setPrice(temp.longValue());
        reportRes.setReportBoList(reportBoList);
        bResult.setData(reportRes);
        log.info("result:{}", bResult.toString());
        return bResult;
    }

    @Override
    public BResult<ReportRes> initReport(ReportBo req) {
        log.info("init param:{}", req.toString());
        reportService.init(req);
        return new BResult<>();
    }

    @Override
    public BResult<ReportRes> getBillingDetail(int year, long accountId, long environment) {
        log.info("year:{},accountId:{},environment:{}", year, accountId, environment);
        long startTime = TimeUtils.String2LongDate(year + "-01-01 00:00:00");
        long endTime = TimeUtils.String2LongDate(year + "-12-31 23:59:59");
        log.info("startTime:{},endTime:{}", startTime, endTime);
        List<Report> reportList = reportService.getBillingDetail(startTime, endTime, accountId, environment);
        List<ReportBo> reportBoList = new ArrayList<>();
        //BigDecimal bigDecimal = new BigDecimal(0);
        AtomicLong temp = new AtomicLong();
        Map<Integer, Object> mapData = new HashMap<>();
        reportList.forEach(it -> {
            ReportBo reportBo = new ReportBo();
            BeanUtils.copyProperties(it, reportBo);
            reportBo.setBizId(it.getAccountId());
            LocalDateTime localDateTime = TimeUtils.Long2Date(it.getCtime());
            int month = localDateTime.getMonthValue();
            mapData.put(month, reportBo);
            //reportBoList.add(reportBo);
            temp.addAndGet(it.getPrice());
            //bigDecimal.add(new BigDecimal(it.getPrice()));
            //System.out.println(bigDecimal.intValue());
        });
//        if (reportBoList.size() != 12) {
//            int tempCount = 12 - reportBoList.size();
//            for (int i = 0; i < tempCount; i++) {
//                ReportBo reportBo = new ReportBo();
//                reportBoList.add(reportBo);
//            }
//        }
        for (int i = 1; i < 13; i++) {
            if (mapData.get(i) == null) {
                ReportBo reportBo = new ReportBo();
                reportBoList.add(reportBo);
            } else {
                reportBoList.add((ReportBo)mapData.get(i));
            }
        }

        BResult<ReportRes> bResult = new BResult<>();
        ReportRes reportRes = new ReportRes();
        reportRes.setPrice(temp.longValue());
        reportRes.setReportBoList(reportBoList);
        bResult.setData(reportRes);
        log.info("result:{}", bResult.toString());
        return bResult;
    }

}
