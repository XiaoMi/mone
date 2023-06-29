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
import com.xiaomi.mone.app.dao.mapper.HeraProjectGroupMapper;
import com.xiaomi.mone.app.dao.mapper.HeraProjectGroupUserMapper;
import com.xiaomi.mone.app.model.HeraProjectGroupUser;
import com.xiaomi.mone.app.model.HeraProjectGroupUserExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class HeraProjectGroupUserDao {

    @Resource
    private HeraProjectGroupUserMapper projectGroupUserMapper;

    public List<Integer> listGroupIdsByUser(String user){

        if(StringUtils.isBlank(user)){
            return null;
        }

        HeraProjectGroupUserExample example = new HeraProjectGroupUserExample();
        HeraProjectGroupUserExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andUserEqualTo(user);
        example.setOffset(0);
        example.setLimit(Integer.MAX_VALUE);
        List<HeraProjectGroupUser> heraProjectGroupUsers = null;
        try {
            heraProjectGroupUsers = projectGroupUserMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraProjectGroupUserDao#listGroupIdsByUser error! exception:{}",e.getMessage(),e);
        }

        if(CollectionUtils.isEmpty(heraProjectGroupUsers)){
            log.info("listGroupIdsByUser no data found!user:{}",user);
            return null;
        }

        return heraProjectGroupUsers.stream().map(t -> t.getProjectGroupId()).collect(Collectors.toList());
    }

    public List<HeraProjectGroupUser> listByProjectGroupId(Integer projectGroupId){

        if(projectGroupId == null){
            log.error("listByProjectGroupId param is invalid!");
            return null;
        }

        HeraProjectGroupUserExample example = new HeraProjectGroupUserExample();
        HeraProjectGroupUserExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andProjectGroupIdEqualTo(projectGroupId);
        example.setOffset(1);
        example.setLimit(Integer.MAX_VALUE);
        List<HeraProjectGroupUser> heraProjectGroupUsers = null;
        try {
            heraProjectGroupUsers = projectGroupUserMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("HeraProjectGroupUserDao#listGroupIdsByUser error! exception:{}",e.getMessage(),e);
        }

        return heraProjectGroupUsers;
    }

    public Integer batchInsert(List<HeraProjectGroupUser> users){
        try {
            return projectGroupUserMapper.batchInsert(users);
        } catch (Exception e) {
            String userInfos = new Gson().toJson(users);
            log.error("batchInsert error!exception : {},userInfos : {}",e.getMessage(),userInfos,e);
            return 0;
        }
    }

    public Integer delById(Integer id){
        try {
            return projectGroupUserMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("delById error!exception : {}",e.getMessage(),e);
            return 0;
        }
    }

    public Integer delByGroupId(Integer groupId){

        HeraProjectGroupUserExample example = new HeraProjectGroupUserExample();
        HeraProjectGroupUserExample.Criteria ca = example.createCriteria();
        ca.andProjectGroupIdEqualTo(groupId);
        try {
            return projectGroupUserMapper.deleteByExample(example);
        } catch (Exception e) {
            log.error("delByGroupId error!exception : {}",e.getMessage(),e);
            return 0;
        }
    }

}
