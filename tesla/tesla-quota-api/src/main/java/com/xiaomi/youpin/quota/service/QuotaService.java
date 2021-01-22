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

package com.xiaomi.youpin.quota.service;

import com.xiaomi.youpin.quota.bo.*;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
public interface QuotaService {

    /**
     * 创建配额
     *
     * @param quotaInfo
     * @return
     */
    Result<List<ResourceBo>> createQuota(QuotaInfo quotaInfo);

    /**
     * 获取生配的信息(是否可升级配置,如果不能,则返回相差的资源)
     *
     * @param quotaInfo
     * @return
     */
    Result<List<UpgradeBo>> getUpgradeInfo(QuotaInfo quotaInfo);


    /**
     * 获取扩容信息(可以扩容的机器的数量)
     *
     * @param quotaInfo
     * @return
     */
    Result<ExpansionBo> getExpansionInfo(QuotaInfo quotaInfo);


    /**
     * 销毁所有配额
     *
     * @param quotaInfo
     * @return
     */
    Result<List<ResourceBo>> destoryQuota(QuotaInfo quotaInfo);


    /**
     * 服务器漂移(归还一台服务器资源,再获得一台服务器资源)
     * 可以粗略的认为:用新的一台服务器,替换老的服务器
     *
     * @param quotaInfo
     * @return
     */
    Result<ResourceBo> drift(QuotaInfo quotaInfo);

    /**
     * 资源下线(可以认为是机器下线,上边的quota需要都清除掉)
     * @param quotaInfo
     * @return
     */
    Result<List<ResourceBo>> offline(QuotaInfo quotaInfo);

    /**
     * 修改配额
     *
     * @param quotaInfo
     * @return
     */
    ModifyQuotaRes modifyQuota(QuotaInfo quotaInfo);


    Result<Boolean> removeQuota(String ip, long bizId, long projectId);

    /**
     * 更新quota信息
     *
     * @param ip
     * @param bizId
     * @param time
     * @return
     */
    Result<Boolean> updateQuota(String ip, long bizId, long time);


    Result<List<QuotaInfo>> quotaList(long bizId);

    /**
     * 修正数据
     * @return
     */
    Result<Boolean> revise();
}
