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

import com.xiaomi.mone.monitor.dao.mapper.AppCapacityAutoAdjustMapper;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjust;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppCapacityAutoAdjustDao {


    @Resource
    private AppCapacityAutoAdjustMapper appCapacityAutoAdjustMapper;


    public Long count(AppCapacityAutoAdjust appCapacityAutoAdjust){


        AppCapacityAutoAdjustExample example = new AppCapacityAutoAdjustExample();

        //默认查询未删除的数据
        AppCapacityAutoAdjustExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if(appCapacityAutoAdjust.getStatus() != null){
            ca = example.createCriteria().andStatusEqualTo(appCapacityAutoAdjust.getStatus());
        }

        if(appCapacityAutoAdjust.getAutoCapacity() != null){
            ca.andAutoCapacityEqualTo(appCapacityAutoAdjust.getAutoCapacity());
        }

        if(appCapacityAutoAdjust.getAppId() != null){
            ca.andAppIdEqualTo(appCapacityAutoAdjust.getAppId());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjust.getContainer())){
            ca.andContainerLike("%" + appCapacityAutoAdjust.getContainer() + "%");
        }


        example.setOrderByClause("id desc");

        try {
            return appCapacityAutoAdjustMapper.countByExample(example);
        } catch (Exception e) {
            log.error("AppCapacityAutoAdjustDao#count error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<AppCapacityAutoAdjust> query(AppCapacityAutoAdjust appCapacityAutoAdjust,Integer pageCount,Integer pageNum){

        if(pageCount == null){
            pageCount = 1;
        }
        if(pageNum == null){
            pageNum = 10;
        }

        AppCapacityAutoAdjustExample example = new AppCapacityAutoAdjustExample();
        //默认查询未删除的数据
        AppCapacityAutoAdjustExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if(appCapacityAutoAdjust.getStatus() != null){
            ca = example.createCriteria().andStatusEqualTo(appCapacityAutoAdjust.getStatus());
        }

        if(appCapacityAutoAdjust.getAutoCapacity() != null){
            ca.andAutoCapacityEqualTo(appCapacityAutoAdjust.getAutoCapacity());
        }

        if(appCapacityAutoAdjust.getAppId() != null){
            ca.andAppIdEqualTo(appCapacityAutoAdjust.getAppId());
        }

        if(appCapacityAutoAdjust.getPipelineId() != null){
            ca.andPipelineIdEqualTo(appCapacityAutoAdjust.getPipelineId());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjust.getContainer())){
            ca.andContainerLike("%" + appCapacityAutoAdjust.getContainer() + "%");
        }

        example.setOffset((pageCount-1) * pageNum);
        example.setLimit(pageNum);
        example.setOrderByClause("id desc");

        try {
            return appCapacityAutoAdjustMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("AppCapacityAutoAdjustDao#query error!" + e.getMessage(),e);
            return null;
        }

    }


    public AppCapacityAutoAdjust getById(Integer id){

        if(id == null){
            log.info("AppCapacityAutoAdjustDao.getById id is null!");
            return null;
        }

        return appCapacityAutoAdjustMapper.selectByPrimaryKey(id);
    }


    public int create(AppCapacityAutoAdjust appCapacityAutoAdjust) {
        if (null == appCapacityAutoAdjust) {
            log.error("[AppCapacityAutoAdjustDao.create] null appCapacityAutoAdjust");
            return 0;
        }

        appCapacityAutoAdjust.setCreateTime(new Date());
        appCapacityAutoAdjust.setUpdateTime(new Date());
        appCapacityAutoAdjust.setStatus(0);

        try {
            int affected = appCapacityAutoAdjustMapper.insert(appCapacityAutoAdjust);
            if (affected < 1) {
                log.warn("[AppCapacityAutoAdjustDao.create] failed to insert AppCapacityAutoAdjust: {}", appCapacityAutoAdjust.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppCapacityAutoAdjustDao.create] failed to insert AppCapacityAutoAdjust: {}, err: {}", appCapacityAutoAdjust.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(AppCapacityAutoAdjust appCapacityAutoAdjust) {
        if (null == appCapacityAutoAdjust) {
            log.error("[AppCapacityAutoAdjustDao.update] null appCapacityAutoAdjust");
            return 0;
        }
        appCapacityAutoAdjust.setUpdateTime(new Date());

        try {
            int affected = appCapacityAutoAdjustMapper.updateByPrimaryKey(appCapacityAutoAdjust);
            if (affected < 1) {
                log.warn("[AppCapacityAutoAdjustDao.update] failed to update appCapacityAutoAdjust: {}", appCapacityAutoAdjust.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppCapacityAutoAdjustDao.update] failed to update appCapacityAutoAdjust: {}, err: {}", appCapacityAutoAdjust.toString(), e);
            return 0;
        }
        return 1;
    }


}
