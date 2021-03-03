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

import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.bo.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public interface ResourceService {

    /**
     * 获取资源列表
     *
     * @return
     */
    Result<List<ResourceBo>> list();

    /**
     * 获取资源列表
     */
    Result<Map<String, Object>> list(int page, int pageSize, int status, HashMap<String,String> map);

    /**
     * 更新资源信息
     *
     * @param resourceBo
     * @return
     */
    Result<Boolean> updateResource(ResourceBo resourceBo);


    /**
     * 更新order
     *
     * @param id,rorder
     * @return
     */
    Result<Boolean> updateOrderById(int id, int rorder) throws Exception;

    /**
     * 根据ip获取resource
     */
    Result<ResourceBo> getResourceByIp(String ip) throws Exception;

    /**
     * 根据envid获取resource
     */
    Result<List<ResourceBo>> getResourceByEnvId(int envId);

    /**
     * 根据projectid获取resource
     */
    Result<List<ResourceBo>> getResourceByProjectId(int projectId);

    /**
     * 根据ip获取单价价格
     * @param ip
     * @return
     */
    Result<Long> getPrice(String ip);

    /**
     * 设置ip的总价格
     * @param ip
     * @param price
     * @return
     */
    Result<Integer> setPrice(String ip, long price);

}
