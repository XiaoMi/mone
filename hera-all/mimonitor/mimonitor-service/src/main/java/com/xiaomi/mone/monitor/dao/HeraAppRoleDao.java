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

import com.xiaomi.mone.monitor.dao.mapper.HeraAppBaseInfoMapper;
import com.xiaomi.mone.monitor.dao.mapper.HeraAppRoleMapper;
import com.xiaomi.mone.monitor.dao.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class HeraAppRoleDao {

    @Resource
    private HeraAppRoleMapper heraAppRoleMapper;

    public Integer delById(Integer id){
        if(id == null){
            log.error("HeraAppRoleDao.delById invalid param,id is null");
        }

        try {
            return heraAppRoleMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("HeraAppRoleDao.delById error!{}",e.getMessage(),e);
            return 0;
        }
    }

    public Long count(HeraAppRole heraAppRole){


        HeraAppRoleExample example = new HeraAppRoleExample();

        //默认查询未删除的数据
        HeraAppRoleExample.Criteria ca = example.createCriteria();
        if(heraAppRole.getStatus() != null){
            ca.andStatusEqualTo(heraAppRole.getStatus());
        }else{
            ca.andStatusEqualTo(0);
        }

        if(StringUtils.isNotEmpty(heraAppRole.getAppId())){
            ca.andAppIdEqualTo(heraAppRole.getAppId());
        }

        if(heraAppRole.getAppPlatform() != null){
            ca.andAppPlatformEqualTo(heraAppRole.getAppPlatform());
        }

        if(StringUtils.isNotEmpty(heraAppRole.getUser())){
            ca.andUserEqualTo(heraAppRole.getUser());
        }

        if(heraAppRole.getRole() != null){
            ca.andRoleEqualTo(heraAppRole.getRole());
        }


        try {
            return heraAppRoleMapper.countByExample(example);
        } catch (Exception e) {
            log.error("HeraAppRoleDao#count error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<HeraAppRole> query(HeraAppRole heraAppRole,Integer pageCount,Integer pageNum){

        if(pageCount == null || pageCount.intValue() <=0){
            pageCount = 1;
        }
        if(pageNum == null || pageNum.intValue() <=0){
            pageNum = 10;
        }

        HeraAppRoleExample example = new HeraAppRoleExample();

        //默认查询未删除的数据
        HeraAppRoleExample.Criteria ca = example.createCriteria();
        if(heraAppRole.getStatus() != null){
            ca.andStatusEqualTo(heraAppRole.getStatus());
        }else{
            ca.andStatusEqualTo(0);
        }

        if(StringUtils.isNotEmpty(heraAppRole.getAppId())){
            ca.andAppIdEqualTo(heraAppRole.getAppId());
        }

        if(heraAppRole.getAppPlatform() != null){
            ca.andAppPlatformEqualTo(heraAppRole.getAppPlatform());
        }

        if(StringUtils.isNotEmpty(heraAppRole.getUser())){
            ca.andUserEqualTo(heraAppRole.getUser());
        }

        if(heraAppRole.getRole() != null){
            ca.andRoleEqualTo(heraAppRole.getRole());
        }

        example.setOffset((pageCount-1) * pageNum);
        example.setLimit(pageNum);

        try {
            return heraAppRoleMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraAppRoleDao#query error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<HeraAppRole> queryByPlatTypes(String appId,List<Integer> platTypes,Integer pageCount,Integer pageNum){

        if(pageCount == null || pageCount.intValue() <=0){
            pageCount = 1;
        }
        if(pageNum == null || pageNum.intValue() <=0){
            pageNum = 10;
        }

        HeraAppRoleExample example = new HeraAppRoleExample();

        for(Integer platForm : platTypes){
            //默认查询未删除的数据
            HeraAppRoleExample.Criteria ca = example.createCriteria();
            ca.andStatusEqualTo(0);
            ca.andAppIdEqualTo(appId);

            ca.andAppPlatformEqualTo(platForm);

            example.or(ca);
        }

        example.setOffset((pageCount-1) * pageNum);
        example.setLimit(pageNum);

        try {
            return heraAppRoleMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraAppRoleDao#queryByPlatTypes error!" + e.getMessage(),e);
            return null;
        }

    }

    public int create(HeraAppRole heraAppRole) {
        if (null == heraAppRole) {
            log.error("[HeraAppRoleDao.create] null heraAppRole");
            return 0;
        }

        heraAppRole.setCreateTime(new Date());
        heraAppRole.setUpdateTime(new Date());
        heraAppRole.setStatus(0);

        try {
            int affected = heraAppRoleMapper.insert(heraAppRole);
            if (affected < 1) {
                log.warn("[HeraAppRoleDao.create] failed to insert heraAppRole: {}", heraAppRole.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraAppRoleDao.create] failed to insert heraAppRole: {}, err: {}", heraAppRole.toString(), e);
            return 0;
        }
        return 1;
    }

    public int batchCreate(List<HeraAppRole> heraAppRoles) {
        if (CollectionUtils.isEmpty(heraAppRoles)) {
            log.error("[HeraAppRoleDao.batchCreate] null heraAppRoles");
            return 0;
        }

        heraAppRoles.forEach(heraAppRole -> {
            heraAppRole.setCreateTime(new Date());
            heraAppRole.setUpdateTime(new Date());
            heraAppRole.setStatus(0);
        });


        try {
            int affected = heraAppRoleMapper.batchInsert(heraAppRoles);
            if (affected < 1) {
                log.warn("[HeraAppRoleDao.batchCreate] failed to insert heraAppRoles: {}", heraAppRoles);
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraAppRoleDao.batchCreate] failed to insert heraAppRoles: {}, err: {}", heraAppRoles, e);
            return 0;
        }
        return 1;
    }

    public int update(HeraAppRole heraAppRole) {
        if (null == heraAppRole) {
            log.error("[HeraAppRoleDao.update] null heraAppRole");
            return 0;
        }
        heraAppRole.setUpdateTime(new Date());

        try {
            int affected = heraAppRoleMapper.updateByPrimaryKey(heraAppRole);
            if (affected < 1) {
                log.warn("[HeraAppRoleDao.update] failed to update heraAppRole: {}", heraAppRole.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraAppRoleDao.update] failed to update heraAppRole: {}, err: {}", heraAppRole.toString(), e);
            return 0;
        }
        return 1;
    }


}
