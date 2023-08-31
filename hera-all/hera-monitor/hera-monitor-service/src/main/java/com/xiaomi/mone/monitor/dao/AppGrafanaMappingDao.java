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

package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppGrafanaMappingMapper;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaMapping;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaMappingExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppGrafanaMappingDao {


    @Autowired
    private AppGrafanaMappingMapper appGrafanaMappingMapper;

    public Long getDataTotal(){
        return appGrafanaMappingMapper.countByExample(null);
    }

    public List<AppGrafanaMapping> getData(Integer offset, Integer pageSize){
        AppGrafanaMappingExample example = new AppGrafanaMappingExample();
        example.setOffset(offset);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");
        return appGrafanaMappingMapper.selectByExample(example);
    }

    public int generateGrafanaMapping(AppGrafanaMapping appGrafanaMapping) {
        if (null == appGrafanaMapping) {
            log.error("[AppGrafanaMappingDao.generateGrafanaMapping] null appGrafanaMapping");
            return 0;
        }

        appGrafanaMapping.setCreateTime(new Date());
        appGrafanaMapping.setUpdateTime(new Date());

        try {
            int affected = appGrafanaMappingMapper.insert(appGrafanaMapping);
            if (affected < 1) {
                log.warn("[AppGrafanaMappingDao.generateGrafanaMapping] failed to insert AppGrafanaMapping: {}", appGrafanaMapping.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppGrafanaMappingDao.generateGrafanaMapping] failed to insert AppGrafanaMapping: {}, err: {}", appGrafanaMapping.toString(), e);
            return 0;
        }
        return 1;
    }

    public AppGrafanaMapping getByAppName(String appName){

            AppGrafanaMappingExample example = new AppGrafanaMappingExample();
            AppGrafanaMappingExample.Criteria ca = example.createCriteria();
            ca.andAppNameEqualTo(appName);
            List<AppGrafanaMapping> appGrafanaMappings= appGrafanaMappingMapper.selectByExample(example);
            if(appGrafanaMappings.size()>0){
                return appGrafanaMappings.get(0);
            }
            return null;
    }

    public int updateByPrimaryKey(AppGrafanaMapping appGrafanaMapping){

        try {
            appGrafanaMapping.setUpdateTime(new Date());
            int i = appGrafanaMappingMapper.updateByPrimaryKey(appGrafanaMapping);
            if(i < 0){
                log.warn("[AppGrafanaMappingDao.updateByPrimaryKey] failed to update AppGrafanaMapping: {}", appGrafanaMapping.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppGrafanaMappingDao.updateByPrimaryKey] failed to update AppGrafanaMapping: {}, err: {}", appGrafanaMapping.toString(), e);
            return 0;
        }

        return 1;

    }


}
