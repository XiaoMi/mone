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

package com.xiaomi.mone.app.dao;

import com.google.gson.Gson;
import com.xiaomi.mone.app.dao.mapper.HeraProjectGroupAppMapper;
import com.xiaomi.mone.app.dao.mapper.HeraProjectGroupMapper;
import com.xiaomi.mone.app.model.HeraProjectGroupApp;
import com.xiaomi.mone.app.model.HeraProjectGroupAppExample;
import com.xiaomi.mone.app.model.HeraProjectGroupUserExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class HeraProjectGroupAppDao {

    @Resource
    private HeraProjectGroupAppMapper projectGroupAppMapper;

    public List<Integer> getAppBaseInfoIds(List<Integer> projectGroupIds){

        HeraProjectGroupAppExample example = new HeraProjectGroupAppExample();
        HeraProjectGroupAppExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andProjectGroupIdIn(projectGroupIds);
        List<HeraProjectGroupApp> heraProjectGroupApps = projectGroupAppMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(heraProjectGroupApps)){
            return null;
        }

        return heraProjectGroupApps.stream().map(t -> t.getAppBaseInfoId()).collect(Collectors.toList());
    }

    public List<HeraProjectGroupApp> listByProjectGroupId(Integer projectGroupId){

        HeraProjectGroupAppExample example = new HeraProjectGroupAppExample();
        HeraProjectGroupAppExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andProjectGroupIdEqualTo(projectGroupId);
        return projectGroupAppMapper.selectByExample(example);
    }

    public Integer batchInsert(List<HeraProjectGroupApp> apps){
        try {
            return projectGroupAppMapper.batchInsert(apps);
        } catch (Exception e) {
            String appsInfo = new Gson().toJson(apps);
            log.error("App batchInsert exception! exception:"+e.getMessage() + ",appsInfo:" + appsInfo,e);
        }
        return 0;
    }

    public Integer delById(Integer id){
        if(id == null){
            log.error("delById param is invalid!id : {}",id);
        }

        try {
            return projectGroupAppMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    public Integer delByGroupId(Integer groupId){

        HeraProjectGroupAppExample example = new HeraProjectGroupAppExample();
        HeraProjectGroupAppExample.Criteria ca = example.createCriteria();
        ca.andProjectGroupIdEqualTo(groupId);
        try {
            return projectGroupAppMapper.deleteByExample(example);
        } catch (Exception e) {
            log.error("delByGroupId error!exception : {}",e.getMessage(),e);
            return 0;
        }
    }

}
