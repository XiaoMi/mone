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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xiaomi.mone.monitor.bo.AppViewType;
import com.xiaomi.mone.monitor.dao.mapper.AppMonitorMapper;
import com.xiaomi.mone.monitor.dao.model.AlarmHealthQuery;
import com.xiaomi.mone.monitor.dao.model.AlarmHealthResult;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.AppMonitorExample;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class AppMonitorDao {


    @Resource
    private AppMonitorMapper appMonitorMapper;

    public Long getDataTotal(AppMonitor appMonitor,String ownerName,String careUser){
        AppMonitorExample example = new AppMonitorExample();
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);


        if (StringUtils.isNotBlank(appMonitor.getProjectName())){
            ca.andProjectNameLike("%" + appMonitor.getProjectName() + "%");
        }
        if(appMonitor.getAppSource() != null){
            ca.andAppSourceEqualTo(appMonitor.getAppSource());
        }

        if (StringUtils.isNotBlank(ownerName)){
            ca.andOwnerEqualTo(ownerName);
        }
        if (StringUtils.isNotBlank(careUser)){
            ca.andCareUserEqualTo(careUser);
        }
        return appMonitorMapper.countByExample(example);
    }

    public List<AppMonitor> getDataByGroupBy(Integer offset, Integer pageSize) {
        try {
            return appMonitorMapper.selectByGroupBy(offset, pageSize);
        } catch (Exception e) {
            log.error("AppMonitorDao.selectByGroupBy查询异常", e);
            return null;
        }
    }

    public List<AppMonitor> getData(Integer offset, Integer pageSize){
        AppMonitorExample example = new AppMonitorExample();
        example.setOffset(offset);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");
        return appMonitorMapper.selectByExample(example);
    }

    public Long countByBaseInfoIds(List<Integer> baseInfoIds,String user){

        if(CollectionUtils.isEmpty(baseInfoIds)){
            return 0l;
        }

        AppMonitorExample example = new AppMonitorExample();
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andBaseInfoIdIn(baseInfoIds);
        ca.andOwnerEqualTo(user);
        return appMonitorMapper.countByExample(example);
    }

    public List<AppMonitor> getDataByBaseInfoIds(List<Integer> baseInfoIds,String user,Integer page, Integer pageSize){

        if(page.intValue() <=0){
            page = 1;
        }

        if(pageSize <= 0){
            pageSize = 10;
        }

        AppMonitorExample example = new AppMonitorExample();
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andBaseInfoIdIn(baseInfoIds);
        ca.andOwnerEqualTo(user);
        example.setOffset((page - 1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");
        return appMonitorMapper.selectByExample(example);
    }

    public AppMonitor getById(Integer id){
        return appMonitorMapper.selectByPrimaryKey(id);
    }

    public AppMonitor getByAppId(Integer projectId){
        return getByAppIdAndName(projectId, null);
    }

    public AppMonitor getByAppIdAndName(Integer projectId, String projectName){
        if (projectId == null) {
            throw new IllegalArgumentException("projectId不能为空");
        }
        AppMonitorExample example = new AppMonitorExample();
        example.setOrderByClause("id desc");
        example.setLimit(1);
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andProjectIdEqualTo(projectId);
        if (StringUtils.isNotBlank(projectName)) {
            ca.andProjectNameEqualTo(projectName);
        }
        List<AppMonitor> appMonitors = appMonitorMapper.selectByExample(example);

        if(CollectionUtils.isEmpty(appMonitors)){
            log.info("AppMonitorDao.getByAppId no data found! projectId :{}",projectId);
            return null;
        }

        return appMonitors.get(0);

    }

    public AppMonitor getByIamTreeId(Integer aimTreeId){

        AppMonitorExample example = new AppMonitorExample();
        example.setOrderByClause("id desc");

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andIamTreeIdEqualTo(aimTreeId);

        List<AppMonitor> appMonitors = appMonitorMapper.selectByExample(example);

        if(CollectionUtils.isEmpty(appMonitors)){
            log.info("AppMonitorDao.aimTreeId no data found! aimTreeId :{}",aimTreeId);
            return null;
        }

        return appMonitors.get(0);

    }

    public AppMonitor getByIamTreeIdAndAppId(Integer aimTreeId,Integer appId){

        AppMonitorExample example = new AppMonitorExample();
        example.setOrderByClause("id desc");

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andIamTreeIdEqualTo(aimTreeId);
        ca.andProjectIdEqualTo(appId);

        List<AppMonitor> appMonitors = appMonitorMapper.selectByExample(example);

        if(CollectionUtils.isEmpty(appMonitors)){
            log.info("AppMonitorDao.getByIamTreeIdAndAppId no data found! aimTreeId :{}",aimTreeId);
            return null;
        }

        return appMonitors.get(0);

    }

    public Long getDataTotalByOr(String appName,String userName,String careUser){
        AppMonitorExample example = new AppMonitorExample();
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        if (StringUtils.isNotBlank(appName)){
            ca.andProjectNameLike("%" + appName + "%");
        }
        if (StringUtils.isNotBlank(userName)){
            ca.andOwnerEqualTo(userName);
        }

        AppMonitorExample.Criteria ca1 = example.createCriteria();
        ca1.andStatusEqualTo(0);
        if (StringUtils.isNotBlank(appName)){
            ca1.andProjectNameLike("%" + appName + "%");
        }
        if (StringUtils.isNotBlank(userName)){
            ca1.andCareUserEqualTo(userName);
        }
        example.or(ca1);

        return appMonitorMapper.countByExample(example);
    }

    public List<AppMonitor> getMyOwnerOrCareApp(String appName,String userName,Integer page, Integer pageSize){

        AppMonitorExample example = new AppMonitorExample();
        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        if (StringUtils.isNotBlank(appName)){
            ca.andProjectNameLike("%" + appName + "%");
        }
        if (StringUtils.isNotBlank(userName)){
            ca.andOwnerEqualTo(userName);
        }

        AppMonitorExample.Criteria ca1 = example.createCriteria();
        ca1.andStatusEqualTo(0);
        if (StringUtils.isNotBlank(appName)){
            ca1.andProjectNameLike("%" + appName + "%");
        }
        if (StringUtils.isNotBlank(userName)){
            ca1.andCareUserEqualTo(userName);
        }

        example.or(ca1);
        example.setOrderByClause("owner DESC,id desc");

        return appMonitorMapper.selectByExample(example);

    }

    public List<AppMonitor> getAllApps(Integer page, Integer pageSize){

        AppMonitorExample example = new AppMonitorExample();
        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id asc");

        return appMonitorMapper.selectByExample(example);

    }

    public List<AppMonitor> getMyOwnerOrCareAppById(Integer projectId,Integer appSource,String userName){

        AppMonitorExample example = new AppMonitorExample();

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        if (projectId != null){
            ca.andProjectIdEqualTo(projectId);
        }

        if (StringUtils.isNotBlank(userName)){
            ca.andOwnerEqualTo(userName);
        }

        if(appSource != null){
            ca.andAppSourceEqualTo(appSource);
        }

        AppMonitorExample.Criteria ca1 = example.createCriteria();
        ca.andStatusEqualTo(0);
        if (projectId != null){
            ca1.andProjectIdEqualTo(projectId);
        }

        if (StringUtils.isNotBlank(userName)){
            ca1.andCareUserEqualTo(userName);
        }

        if(appSource != null){
            ca1.andAppSourceEqualTo(appSource);
        }

        example.or(ca1);

        return appMonitorMapper.selectByExample(example);

    }

    public List<AppMonitor> getByProjectIdAndPlat(Integer projectId,Integer appSource){

        AppMonitorExample example = new AppMonitorExample();

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        if (projectId != null){
            ca.andProjectIdEqualTo(projectId);
        }
        if(appSource != null){
            ca.andAppSourceEqualTo(appSource);
        }

        return appMonitorMapper.selectByExample(example);

    }

    public List<AppMonitor> getAllMyAppDistinct(String userName,String appName,Integer page, Integer pageSize){

        int offset = (page - 1) * pageSize;

        return appMonitorMapper.selectAllMyAppDistinct(userName,appName,offset,pageSize);

    }

    public Long countAllMyAppDistinct(String userName,String appName){

        return appMonitorMapper.countAllMyAppDistinct(userName,appName);

    }

    public List<AppMonitor> getMyOwnerApp(AppMonitor appMonitor,String userName,Integer page, Integer pageSize){

        AppMonitorExample example = new AppMonitorExample();
        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");


        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andOwnerEqualTo(userName);

        if (StringUtils.isNotBlank(appMonitor.getProjectName())){
            ca.andProjectNameLike("%" + appMonitor.getProjectName() + "%");
        }
        if(appMonitor.getAppSource() != null){
            ca.andAppSourceEqualTo(appMonitor.getAppSource());
        }


        return appMonitorMapper.selectByExample(example);

    }

    public Set<Integer> selectTreeIdByOwnerOrCareUser(String userName){
        List<Integer> treeIdList = appMonitorMapper.selectTreeIdByOwnerOrCareUser(userName);
        if (CollectionUtils.isEmpty(treeIdList)) {
            return null;
        }
        return new HashSet<>(treeIdList);
    }

    public List<AppMonitor> getMyCareApp(String appName,String userName,Integer page, Integer pageSize){

        AppMonitorExample example = new AppMonitorExample();
        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andCareUserEqualTo(userName);

        if (StringUtils.isNotBlank(appName)){
            ca.andProjectNameLike("%" + appName + "%");
        }

        return appMonitorMapper.selectByExample(example);

    }

    public List<AppMonitor> listAppsByBaseInfoId(Integer baseInfoId){

        AppMonitorExample example = new AppMonitorExample();
        example.setOrderByClause("id desc");

        AppMonitorExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andBaseInfoIdEqualTo(baseInfoId);

        return appMonitorMapper.selectByExample(example);

    }



    public int create(AppMonitor appMonitor) {
        if (null == appMonitor) {
            log.error("[AppMonitorDao.create] null appMonitor");
            return 0;
        }
        if (appMonitor.getAppSource() == null) {
            appMonitor.setAppSource(0);
        }
        appMonitor.setCreateTime(new Date());
        appMonitor.setUpdateTime(new Date());
        appMonitor.setStatus(0);

        try {
            int affected = appMonitorMapper.insert(appMonitor);
            if (affected < 1) {
                log.warn("[AppMonitorDao.create] failed to insert appMonitor: {}", appMonitor.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppMonitorDao.create] failed to insert appMonitor: {}, err: {}", appMonitor.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(AppMonitor appMonitor) {
        if (null == appMonitor) {
            log.error("[AppMonitorDao.update] null appMonitor");
            return 0;
        }
        if (appMonitor.getAppSource() == null) {
            appMonitor.setAppSource(0);
        }
        appMonitor.setUpdateTime(new Date());

        try {
            int affected = appMonitorMapper.updateByPrimaryKey(appMonitor);
            if (affected < 1) {
                log.warn("[AppMonitorDao.update] failed to update appMonitor: {}", appMonitor.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppMonitorDao.update] failed to update appMonitor: {}, err: {}", appMonitor.toString(), e);
            return 0;
        }
        return 1;
    }

    public int delete(Integer id) {
        if (null == id) {
            log.error("[AppMonitorDao.delete] null id");
            return 0;
        }

        try {
            int affected = appMonitorMapper.deleteByPrimaryKey(id);
            if (affected < 1) {
                log.warn("[AppMonitorDao.delete] failed to update id: {}", id);
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppMonitorDao.delete] failed to delete id: {}, err: {}", id, e);
            return 0;
        }
        return 1;
    }

    public PageData<List<AppMonitor>> getMyAndCareAppList(String user, String appName, int pageNo, int pageSize, boolean needPage) {
        log.info("AppMonitorDao.getMyAndCareAppList qry request; user={}, appName={}", user, appName);
        PageData<List<AppMonitor>> pageData = new PageData<>();
        pageData.setPage(pageNo);
        pageData.setPageSize(pageSize);
        pageData.setTotal(0L);
        try {
            Page<AppMonitor> page = null;
            if (needPage) {
                page = PageHelper.startPage(pageNo, pageSize);
            }
            List<AppMonitor> list = appMonitorMapper.getMyAndCareAppList(user, appName);
            pageData.setList(list);
            if(page != null) {
                pageData.setTotal(page.getTotal());
            }
            log.info("AppMonitorDao.getMyAndCareAppList qry response; user={}, appName={}, pageData={}", user, appName, pageData);
        } catch (Exception e) {
            log.error("AppMonitorDao.getMyAndCareAppList qry excep; user={}, appName={}", user, appName, e);
        }
        return pageData;
    }

    public AppMonitor getMyApp(Integer projectId, Integer iamTreeId, String userName, AppViewType viewType){

        AppMonitorExample example = new AppMonitorExample();

        AppMonitorExample.Criteria ca = example.createCriteria()
                .andStatusEqualTo(0).andProjectIdEqualTo(projectId);
        if (iamTreeId != null) {
            ca.andIamTreeIdEqualTo(iamTreeId);
        }
        if(AppViewType.MyApp == viewType){
            ca.andOwnerEqualTo(userName);
        }else if(AppViewType.MyCareApp == viewType){
            ca.andCareUserEqualTo(userName);
        }else{
            log.error("AppMonitorDao.getMyApp param viewType is error!viewType : {}",viewType);
            return null;
        }


        List<AppMonitor> appMonitors = appMonitorMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(appMonitors)){
            return appMonitors.get(0);
        }

        return null;
    }

    public List<AlarmHealthResult> selectAppHealth(AlarmHealthQuery query){
        return appMonitorMapper.selectAlarmHealth(query);
    }

    public List<AppMonitor> selectByIAMId(Integer iamId, Integer iamType, String userName){
        return appMonitorMapper.selectByIAMId(iamId,iamType,userName);
    }

}
