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

import com.xiaomi.youpin.tesla.billing.bo.BResult;
import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
import com.xiaomi.youpin.tesla.billing.bo.ReportRes;

/**
 * @author goodjava@qq.com
 */
public interface BillingService {


    /**
     * 获取业务报告
     *
     * @param req
     * @return
     */
    BResult<ReportRes> generateBizReport(ReportBo req, int year, int month, long now);

    /**
     * 获取云平台报告
     *
     * @param year
     * @param month
     * @return
     */
    BResult<ReportRes> generateCloudReport(int year, int month);

    /**
     * 每日账单定时任务
     */
    void billingTaskDay();


    /**
     * init 报表数据 初始化
     *
     * @return
     */
    BResult<ReportRes> initReport(ReportBo req);

    /**
     * todo
     * 1，通过项目名称 查询总价 当月的
     * 2，通过项目名称 + 时间 查询明细和总价
     */

    /**
     * 通过项目名称 + 时间 查询明细和总价
     * @param year
     * @param accountId
     * @param environment
     * @return
     */
    BResult<ReportRes> getBillingDetail(int year, long accountId, long environment);

    /**
     * 每月账单定时任务
     */
    void billingTaskMonth();

    /**
     * 应用管理的账单查询
     * @param accountId
     * @return
     */
    BResult<ReportRes> getAppManagementBillingDetail(long accountId);

}
