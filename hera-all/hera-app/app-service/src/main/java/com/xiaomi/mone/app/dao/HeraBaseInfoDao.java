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

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.dao.mapper.HeraAppBaseInfoMapper;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppBaseInfoExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class HeraBaseInfoDao {


    @Resource
    private HeraAppBaseInfoMapper heraAppBaseInfoMapper;

    public Integer delById(Integer id){
        if(id == null){
            log.error("HeraBaseInfoDao.delById invalid param,id is null");
        }

        return heraAppBaseInfoMapper.deleteByPrimaryKey(id);

    }


    public Long countByParticipant(HeraAppBaseQuery query){

        try {
            return heraAppBaseInfoMapper.countByParticipant(query);
        } catch (Exception e) {
            log.error("HeraBaseInfoDao#countByParticipant error!" + e.getMessage(),e);
            return null;
        }

    }
    public List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query){

        query.initPageParam();
        try {
            return heraAppBaseInfoMapper.selectByParticipant(query);
        } catch (Exception e) {
            log.error("HeraBaseInfoDao#queryByParticipant error!" + e.getMessage(),e);
            return null;
        }

    }


    public Long count(HeraAppBaseInfoModel baseInfo){

        HeraAppBaseInfoExample example = new HeraAppBaseInfoExample();

        HeraAppBaseInfoExample.Criteria ca = example.createCriteria();

        if(baseInfo.getStatus() != null){
            ca.andStatusEqualTo(baseInfo.getStatus());
        }else{
            ca.andStatusEqualTo(0);
        }

        if(baseInfo.getBindId() != null){
            ca.andBindIdEqualTo(baseInfo.getBindId());
        }

        if(baseInfo.getBindType() != null){
            ca.andBindTypeEqualTo(baseInfo.getBindType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppName())){
            ca.andAppNameLike("%" + baseInfo.getAppName() + "%");
        }

        if(StringUtils.isNotBlank(baseInfo.getAppCname())){
            ca.andAppCnameLike("%" + baseInfo.getAppCname() + "%");
        }

        if(baseInfo.getAppType() != null){
            ca.andAppTypeEqualTo(baseInfo.getAppType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppLanguage())){
            ca.andAppLanguageLike("%" + baseInfo.getAppLanguage() + "%");
        }

        if(baseInfo.getPlatformType() != null){
            ca.andPlatformTypeEqualTo(baseInfo.getPlatformType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppSignId())){
            ca.andAppSignIdLike("%" + baseInfo.getAppSignId() + "%");
        }

        if(baseInfo.getIamTreeId() != null){
            ca.andIamTreeIdEqualTo(baseInfo.getIamTreeId());
        }

        example.setOrderByClause("id desc");

        try {
            return heraAppBaseInfoMapper.countByExample(example);
        } catch (Exception e) {
            log.error("HeraBaseInfoDao#count error!" + e.getMessage(),e);
            return null;
        }

    }

    public List<HeraAppBaseInfo> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum){

        if(pageCount == null || pageCount.intValue() <=0){
            pageCount = 1;
        }
        if(pageNum == null || pageNum.intValue() <=0){
            pageNum = 10;
        }

        HeraAppBaseInfoExample example = new HeraAppBaseInfoExample();

        //默认查询未删除的数据
        HeraAppBaseInfoExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if(baseInfo.getStatus() != null){
            ca.andStatusEqualTo(baseInfo.getStatus());
        }

        if(baseInfo.getBindId() != null){
            ca.andBindIdEqualTo(baseInfo.getBindId());
        }

        if(baseInfo.getBindType() != null){
            ca.andBindTypeEqualTo(baseInfo.getBindType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppName())){
            ca.andAppNameLike("%" + baseInfo.getAppName() + "%");
        }

        if(StringUtils.isNotBlank(baseInfo.getAppCname())){
            ca.andAppCnameLike("%" + baseInfo.getAppCname() + "%");
        }

        if(baseInfo.getAppType() != null){
            ca.andAppTypeEqualTo(baseInfo.getAppType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppLanguage())){
            ca.andAppLanguageLike("%" + baseInfo.getAppLanguage() + "%");
        }

        if(baseInfo.getPlatformType() != null){
            ca.andPlatformTypeEqualTo(baseInfo.getPlatformType());
        }

        if(StringUtils.isNotBlank(baseInfo.getAppSignId())){
            ca.andAppSignIdLike("%" + baseInfo.getAppSignId() + "%");
        }

        if(baseInfo.getIamTreeId() != null){
            ca.andIamTreeIdEqualTo(baseInfo.getIamTreeId());
        }

        example.setOffset((pageCount-1) * pageNum);
        example.setLimit(pageNum);
        example.setOrderByClause("id desc");

        try {
            return heraAppBaseInfoMapper.selectByExampleWithBLOBs(example);
        } catch (Exception e) {
            log.error("HeraBaseInfoDao#query error!" + e.getMessage(),e);
            return null;
        }

    }


    public HeraAppBaseInfo getById(Integer id){

        if(id == null){
            log.info("HeraBaseInfoDao.getById id is null!");
            return null;
        }

        return heraAppBaseInfoMapper.selectByPrimaryKey(id);
    }


    public int create(HeraAppBaseInfo heraAppBaseInfo) {
        if (null == heraAppBaseInfo) {
            log.error("[HeraBaseInfoDao.create] null heraAppBaseInfo");
            return 0;
        }

        heraAppBaseInfo.setCreateTime(new Date());
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(0);

        try {
            int affected = heraAppBaseInfoMapper.insert(heraAppBaseInfo);
            if (affected < 1) {
                log.warn("[HeraBaseInfoDao.create] failed to insert heraAppBaseInfo: {}", heraAppBaseInfo.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraBaseInfoDao.create] failed to insert heraAppBaseInfo: {}, err: {}", heraAppBaseInfo.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(HeraAppBaseInfo heraAppBaseInfo) {
        if (null == heraAppBaseInfo) {
            log.error("[HeraBaseInfoDao.update] null heraAppBaseInfo");
            return 0;
        }
        heraAppBaseInfo.setUpdateTime(new Date());

        try {
            int affected = heraAppBaseInfoMapper.updateByPrimaryKeyWithBLOBs(heraAppBaseInfo);
            if (affected < 1) {
                log.warn("[HeraBaseInfoDao.update] failed to update heraAppBaseInfo: {}", heraAppBaseInfo.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraBaseInfoDao.update] failed to update heraAppBaseInfo: {}, err: {}", heraAppBaseInfo.toString(), e);
            return 0;
        }
        return 1;
    }


}
