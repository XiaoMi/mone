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

import com.xiaomi.mone.monitor.dao.mapper.AppMonitorConfigMapper;
import com.xiaomi.mone.monitor.dao.model.AppMonitorConfig;
import com.xiaomi.mone.monitor.dao.model.AppMonitorConfigExample;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppMonitorConfigDao {


    @Autowired
    private AppMonitorConfigMapper appMonitorConfigMapper;


    public AppMonitorConfig getById(Integer id){
        return appMonitorConfigMapper.selectByPrimaryKey(id);
    }

    public Long getTotal(Integer projectId,Integer type,String configName,Integer status_,Integer page, Integer pageSize){

        AppMonitorConfigExample example = new AppMonitorConfigExample();

        //默认查询有效数据
        Integer status = 0;
        if(status_ != null){
            status = status_;
        }

        AppMonitorConfigExample.Criteria ca = example.createCriteria().andStatusEqualTo(status);

        if(projectId != null){
            ca.andProjectIdEqualTo(projectId);
        }

        if(type != null){
            ca.andConfigTypeEqualTo(type);
        }

        if (StringUtils.isNotBlank(configName)){
            ca.andConfigNameLike(configName);
        }

        return appMonitorConfigMapper.countByExample(example);

    }
    public Result<PageData> getConfig(Integer projectId, Integer type, String configName, Integer status_, Integer page, Integer pageSize){

        PageData pd = new PageData();
        AppMonitorConfigExample example = new AppMonitorConfigExample();
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        //默认查询有效数据
        Integer status = 0;
        if(status_ != null){
            status = status_;
        }

        AppMonitorConfigExample.Criteria ca = example.createCriteria().andStatusEqualTo(status);

        if(projectId != null){
            ca.andProjectIdEqualTo(projectId);
        }

        if(type != null){
            ca.andConfigTypeEqualTo(type);
        }

        if (StringUtils.isNotBlank(configName)){
            ca.andConfigNameLike(configName);
        }

        Long total = this.getTotal(projectId, type, configName, status_, page, pageSize);

        List<AppMonitorConfig> appMonitorConfigs = appMonitorConfigMapper.selectByExample(example);

        pd.setPage(page);
        pd.setPageSize(pageSize);
        pd.setTotal(total);
        pd.setList(appMonitorConfigs);

        return Result.success(pd);

    }

    public int create(AppMonitorConfig appMonitorConfig) {
        if (null == appMonitorConfig) {
            log.error("[AppMonitorConfigDao.create] null appMonitorConfig");
            return 0;
        }

        appMonitorConfig.setCreateTime(new Date());
        appMonitorConfig.setUpdateTime(new Date());

        try {
            int affected = appMonitorConfigMapper.insert(appMonitorConfig);
            if (affected < 1) {
                log.warn("[AppMonitorConfigDao.create] failed to insert appMonitorConfig: {}", appMonitorConfig.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppMonitorConfigDao.create] failed to insert appMonitorConfig: {}, err: {}", appMonitorConfig.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(AppMonitorConfig appMonitorConfig) {
        if (null == appMonitorConfig) {
            log.error("[AppMonitorConfigDao.update] null appMonitor");
            return 0;
        }

        appMonitorConfig.setUpdateTime(new Date());

        try {
            int affected = appMonitorConfigMapper.updateByPrimaryKey(appMonitorConfig);
            if (affected < 1) {
                log.warn("[AppMonitorConfigDao.update] failed to update appMonitor: {}", appMonitorConfig.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppMonitorConfigDao.update] failed to update appMonitor: {}, err: {}", appMonitorConfig.toString(), e);
            return 0;
        }
        return 1;
    }



}
