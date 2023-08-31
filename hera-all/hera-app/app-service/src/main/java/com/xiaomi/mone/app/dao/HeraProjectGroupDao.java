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
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupModel;
import com.xiaomi.mone.app.dao.mapper.HeraAppRoleMapper;
import com.xiaomi.mone.app.dao.mapper.HeraProjectGroupMapper;
import com.xiaomi.mone.app.model.HeraAppRole;
import com.xiaomi.mone.app.model.HeraAppRoleExample;
import com.xiaomi.mone.app.model.HeraProjectGroup;
import com.xiaomi.mone.app.model.HeraProjectGroupExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class HeraProjectGroupDao {

    @Resource
    private HeraProjectGroupMapper projectGroupMapper;

    public Long count(HeraProjectGroupModel projectGroup){

        HeraProjectGroupExample example = new HeraProjectGroupExample();

        HeraProjectGroupExample.Criteria ca = example.createCriteria();

        //默认查询未删除的数据
        if(projectGroup.getStatus() != null){
            ca.andStatusEqualTo(projectGroup.getStatus());
        }else{
            ca.andStatusEqualTo(0);
        }

        if(projectGroup.getType() != null){
            ca.andTypeEqualTo(projectGroup.getType());
        }

        if(projectGroup.getParentGroupId() != null){
            ca.andParentGroupIdEqualTo(projectGroup.getParentGroupId());
        }

        if(projectGroup.getRelationObjectId() != null){
            ca.andRelationObjectIdEqualTo(projectGroup.getRelationObjectId());
        }

        if(StringUtils.isNotBlank(projectGroup.getName())){
            ca.andNameLike("%" + projectGroup.getName() + "%");
        }

        if(StringUtils.isNotBlank(projectGroup.getCnName())){
            ca.andCnNameLike("%" + projectGroup.getCnName() + "%");
        }

        try {
            return projectGroupMapper.countByExample(example);
        } catch (Exception e) {
            log.error("HeraProjectGroupDao#count error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<HeraProjectGroupModel> search(HeraProjectGroupModel projectGroup, Integer page, Integer pageSize){

        if(page == null || page.intValue() == 0){
            page = 1;
        }

        if(pageSize == null || pageSize.intValue() == 0){
            pageSize = 10;
        }

        HeraProjectGroupExample example = new HeraProjectGroupExample();

        HeraProjectGroupExample.Criteria ca = example.createCriteria();

        //默认查询未删除的数据
        if(projectGroup.getStatus() != null){
            ca.andStatusEqualTo(projectGroup.getStatus());
        }else{
            ca.andStatusEqualTo(0);
        }

        if(projectGroup.getType() != null){
            ca.andTypeEqualTo(projectGroup.getType());
        }

        if(projectGroup.getParentGroupId() != null){
            ca.andParentGroupIdEqualTo(projectGroup.getParentGroupId());
        }

        if(projectGroup.getRelationObjectId() != null){
            ca.andRelationObjectIdEqualTo(projectGroup.getRelationObjectId());
        }

        if(StringUtils.isNotBlank(projectGroup.getName())){
            ca.andNameLike("%" + projectGroup.getName() + "%");
        }

        if(StringUtils.isNotBlank(projectGroup.getCnName())){
            ca.andCnNameLike("%" + projectGroup.getCnName() + "%");
        }

        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        try {
            return projectGroupMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraProjectGroupDao#query error!" + e.getMessage(),e);
            return null;
        }

    }
    public List<HeraProjectGroupModel> listByIds(List<Integer> ids,Integer type,String projectGroupName,Integer level){

        if(CollectionUtils.isEmpty(ids)){
            log.error("listByIds param is invalid! ids : {}",ids);
            return null;
        }

        HeraProjectGroupExample example = new HeraProjectGroupExample();
        HeraProjectGroupExample.Criteria ca = example.createCriteria();

        ca.andStatusEqualTo(0);
        ca.andIdIn(ids);
        if(StringUtils.isNotBlank(projectGroupName)){
            ca.andNameLike("%" + projectGroupName + "%");
        }
        if(type != null){
            ca.andTypeEqualTo(type);
        }
        if(level != null){
            //查询节点级数小于指定level的数据
            ca.andLevelLessThan(level);
        }

        try {
            return projectGroupMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraProjectGroupDao#listByIds error!" + e.getMessage(),e);
            return null;
        }

    }

    public Integer create(HeraProjectGroup heraProjectGroup){
        heraProjectGroup.setStatus(0);
        heraProjectGroup.setCreateTime(new Date());
        heraProjectGroup.setUpdateTime(new Date());
        try {
            projectGroupMapper.insert(heraProjectGroup);
        } catch (Exception e) {
            log.error("create data fail! exception:{}",e.getMessage(),e);
        }

        return heraProjectGroup.getId();
    }

    public Integer update(HeraProjectGroup heraProjectGroup){
        heraProjectGroup.setUpdateTime(new Date());
        try {
            projectGroupMapper.updateByPrimaryKeySelective(heraProjectGroup);
        } catch (Exception e) {
            log.error("create data fail! exception:{}",e.getMessage(),e);
        }

        return heraProjectGroup.getId();
    }

    public Integer delById(Integer id){
        try {
            return projectGroupMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("delById data fail! exception:{}",e.getMessage(),e);
            return null;
        }
    }

}
