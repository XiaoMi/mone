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

import com.xiaomi.mone.monitor.dao.mapper.AppCapacityAutoAdjustRecordMapper;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustRecord;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustRecordExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppCapacityAutoAdjustRecordDao {


    @Resource
    private AppCapacityAutoAdjustRecordMapper adjustRecordMapper;


    public Long count(AppCapacityAutoAdjustRecord appCapacityAutoAdjustRecord,boolean isLikeContainer){


        AppCapacityAutoAdjustRecordExample example = new AppCapacityAutoAdjustRecordExample();

        //默认查询未删除的数据
        AppCapacityAutoAdjustRecordExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if(appCapacityAutoAdjustRecord.getStatus() != null){
            ca = example.createCriteria().andStatusEqualTo(appCapacityAutoAdjustRecord.getStatus());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjustRecord.getNameSpace())){
            ca.andNameSpaceEqualTo(appCapacityAutoAdjustRecord.getNameSpace());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjustRecord.getContainer())){
            if(isLikeContainer){
                ca.andContainerLike(appCapacityAutoAdjustRecord.getContainer() + "%");
            }else{
                ca.andContainerEqualTo(appCapacityAutoAdjustRecord.getContainer());
            }

        }


        try {
            return adjustRecordMapper.countByExample(example);
        } catch (Exception e) {
            log.error("AppCapacityAutoAdjustDao#count error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<AppCapacityAutoAdjustRecord> query(AppCapacityAutoAdjustRecord appCapacityAutoAdjustRecord,Integer pageCount,Integer pageNum,boolean isLikeContainer){

        if(pageCount == null){
            pageCount = 1;
        }
        if(pageNum == null){
            pageNum = 10;
        }

        AppCapacityAutoAdjustRecordExample example = new AppCapacityAutoAdjustRecordExample();
        //默认查询未删除的数据
        AppCapacityAutoAdjustRecordExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if(appCapacityAutoAdjustRecord.getStatus() != null){
            ca = example.createCriteria().andStatusEqualTo(appCapacityAutoAdjustRecord.getStatus());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjustRecord.getNameSpace())){
            ca.andNameSpaceEqualTo(appCapacityAutoAdjustRecord.getNameSpace());
        }

        if(StringUtils.isNotBlank(appCapacityAutoAdjustRecord.getContainer())){
            if(isLikeContainer){
                ca.andContainerLike(appCapacityAutoAdjustRecord.getContainer() + "%");
            }else{
                ca.andContainerEqualTo(appCapacityAutoAdjustRecord.getContainer());
            }
        }
        if(appCapacityAutoAdjustRecord.getEnvId() != null){
            ca.andEnvIdEqualTo(appCapacityAutoAdjustRecord.getEnvId());
        }

        example.setOffset((pageCount-1) * pageNum);
        example.setLimit(pageNum);
        example.setOrderByClause("id desc");

        try {
            return adjustRecordMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("AppCapacityAutoAdjustRecordDao#query error!" + e.getMessage(),e);
            return null;
        }

    }


    public AppCapacityAutoAdjustRecord getById(Integer id){

        if(id == null){
            log.info("AppCapacityAutoAdjustRecordDao.getById id is null!");
            return null;
        }

        return adjustRecordMapper.selectByPrimaryKey(id);
    }


    public int create(AppCapacityAutoAdjustRecord appCapacityAutoAdjustRecord) {
        if (null == appCapacityAutoAdjustRecord) {
            log.error("[AppCapacityAutoAdjustRecordDao.create] null appCapacityAutoAdjustRecord");
            return 0;
        }

        appCapacityAutoAdjustRecord.setCreateTime(new Date());
        appCapacityAutoAdjustRecord.setUpdateTime(new Date());
        appCapacityAutoAdjustRecord.setStatus(0);

        try {
            int affected = adjustRecordMapper.insert(appCapacityAutoAdjustRecord);
            if (affected < 1) {
                log.warn("[AppCapacityAutoAdjustRecordDao.create] failed to insert appCapacityAutoAdjustRecord: {}", appCapacityAutoAdjustRecord.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppCapacityAutoAdjustRecordDao.create] failed to insert appCapacityAutoAdjustRecord: {}, err: {}", appCapacityAutoAdjustRecord.toString(), e);
            return 0;
        }
        return 1;
    }



}
