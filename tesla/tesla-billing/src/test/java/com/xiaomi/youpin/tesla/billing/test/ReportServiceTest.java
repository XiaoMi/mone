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

package com.xiaomi.youpin.tesla.billing.test;


import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.billing.bo.BResult;
import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
import com.xiaomi.youpin.tesla.billing.service.BillingServiceImpl;
import com.xiaomi.youpin.tesla.billing.service.ReportService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2020/8/5
 */
public class ReportServiceTest extends BaseTest {


    @Test
    public void testReport() {
        ReportService service = Ioc.ins().getBean(ReportService.class);
        ReportBo bo = new ReportBo();
        bo.setBizId(1);
        ReportBo res = service.generateReportForMonth(bo,2017,1,1);
        System.out.println(res);
    }
    @Test
    public void testBillingdetail() {
//        ReportService service = Ioc.ins().getBean(ReportService.class);
//        List<Report> list = service.getBillingDetail(1597288273433l,System.currentTimeMillis(), 1, "1");
//        System.out.println(list.size());
        BillingServiceImpl billingService = Ioc.ins().getBean(BillingServiceImpl.class);
        BResult<ReportRes> bResult = billingService.getBillingDetail(2020, 1, 1);
        System.out.println(bResult.toString());
    }

    @Test
    public void tsinit() {
        BillingServiceImpl billingService = Ioc.ins().getBean(BillingServiceImpl.class);
        ReportBo req = new ReportBo();
        req.setBizId(5);
        req.setCtime(10);
        req.setName("test");
        req.setPrice(200);
        List<Long> list = new ArrayList<>();
        list.add(27l);
        req.setSubBizIdList(list);
        req.setType(1);
        billingService.initReport(req);
    }

}
