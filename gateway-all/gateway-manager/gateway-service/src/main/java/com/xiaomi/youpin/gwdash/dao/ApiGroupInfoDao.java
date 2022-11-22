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

package com.xiaomi.youpin.gwdash.dao;

import com.xiaomi.youpin.gwdash.dao.mapper.ApiGroupInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfoExample;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ApiGroupInfoDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupInfoDao.class);

    @Resource
    private ApiGroupInfoMapper apiGroupInfoMapper;

    public ApiGroupInfo getGroupInfoById(int id){
        return apiGroupInfoMapper.selectByid(id);
    }

    public int newApiGroupInfo(ApiGroupInfo info) {
        if (null == info) {
            LOGGER.error("[ApiGroupInfoDao.newApiGroupInfo] null ApiGroupInfo");
            return 0;
        }

        long now = System.currentTimeMillis();

        info.setCtime(now);
        info.setUtime(now);

        try {
            int affected = apiGroupInfoMapper.insert(info);
            if (affected < 1) {
                LOGGER.warn("[ApiGroupInfoDao.newApiGroupInfo] failed to insert ApiGroupInfo: {}", info);
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("[ApiGroupInfoDao.newApiGroupInfo] failed to insert ApiGroupInfo: {}, err: {}", info, e);
            return 0;
        }
        return 1;
    }

    public List<ApiGroupInfo> getApiGroupList(int offset, int pageSize) {
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        ApiGroupInfoExample.Criteria criteria = example.createCriteria();
        example.setOffset(offset);
        example.setLimit(pageSize);

        List<ApiGroupInfo> list = apiGroupInfoMapper.selectByExample(example);
        return list;
    }

    public ApiGroupInfo getApiGroupByGid(int gid) {
        return apiGroupInfoMapper.selectByGid(gid);
    }

    public List<ApiGroupInfo> getApiGroupByGids(List<Integer> ids) {
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        if(!CollectionUtils.isEmpty(ids)){
            ApiGroupInfoExample.Criteria criteria = example.createCriteria();
            criteria.andGIdIn(ids);
        }
        List<ApiGroupInfo> list = apiGroupInfoMapper.selectByExample(example);
        return list;
    }

    public ApiGroupInfo getApiGroupByBaseUrl(String baseUrl){
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        ApiGroupInfoExample.Criteria ca = example.createCriteria().andBaseUrlEqualTo(baseUrl);
        List<ApiGroupInfo> apiGroupInfos= apiGroupInfoMapper.selectByExample(example);
        if(apiGroupInfos.size()>0){
            return apiGroupInfos.get(0);
        }
        return null;
    }

    public List<ApiGroupInfo> getApiGroupListAll() {
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        ApiGroupInfoExample.Criteria criteria = example.createCriteria();

        List<ApiGroupInfo> list = apiGroupInfoMapper.selectByExample(example);
        return list;
    }

    public int getApiGroupQty() {
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        ApiGroupInfoExample.Criteria criteria = example.createCriteria();
        return apiGroupInfoMapper.countByExample(example);
    }

    public int delApiGroupInfo(List<Integer> ids) {
        ApiGroupInfoExample example = new ApiGroupInfoExample();
        ApiGroupInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        int affected = apiGroupInfoMapper.deleteByExample(example);
        if (affected < ids.size()) {
            LOGGER.warn("[ApiGroupInfoDao.delApiGroupInfo] failed to delete all api group info, ids: {}", ids);
        }

        return affected;
    }

    public int updateApiGroupInfoById(ApiGroupInfo info) {
        info.setUtime(System.currentTimeMillis());
        return apiGroupInfoMapper.updateByPrimaryKey(info);
    }


}
