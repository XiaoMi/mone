package com.xiaomi.mone.app.service.project.group;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupAppRequest;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupModel;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.dao.HeraBaseInfoDao;
import com.xiaomi.mone.app.dao.HeraProjectGroupAppDao;
import com.xiaomi.mone.app.dao.HeraProjectGroupDao;
import com.xiaomi.mone.app.dao.HeraProjectGroupUserDao;
import com.xiaomi.mone.app.enums.CommonError;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraProjectGroup;
import com.xiaomi.mone.app.model.HeraProjectGroupApp;
import com.xiaomi.mone.app.model.HeraProjectGroupUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2023/6/2 11:24 上午
 */
@Service
@Slf4j
public class HeraProjectGroupService {

    @Autowired
    HeraProjectGroupDao projectGroupDao;

    @Autowired
    HeraProjectGroupUserDao groupUserDao;

    @Autowired
    HeraProjectGroupAppDao projectGroupAppDao;

    @Autowired
    HeraBaseInfoDao heraBaseInfoDao;

    public Result create(HeraProjectGroupDataRequest request){

        Integer parentGroupId = request.getParentGroupId();
        List<HeraProjectGroupModel> heraProjectGroupModels = projectGroupDao.listByIds(Lists.newArrayList(parentGroupId), request.getType(), null);
        if(heraProjectGroupModels != null && heraProjectGroupModels.size() < 1){
            return Result.fail(-1,"there is no data  which project group  id is "+parentGroupId);
        }

        HeraProjectGroupModel projectGroup = new HeraProjectGroupModel();
        projectGroup.setRelationObjectId(request.getRelationObjectId());
        projectGroup.setType(request.getType());
        List<HeraProjectGroupModel> search = projectGroupDao.search(projectGroup, null, null);
        if(!CollectionUtils.isEmpty(search)){
            return Result.fail(-1,"the data relationObjectId: " +request.getRelationObjectId()+ " has exist!");
        }

        HeraProjectGroup group = new HeraProjectGroup();
        BeanUtils.copyProperties(request,group);
        Integer groupId = projectGroupDao.create(group);

        if(!CollectionUtils.isEmpty(request.getUsers())){
            List<HeraProjectGroupUser> toCreateUsers = new ArrayList<>();
            request.getUsers().forEach(t -> {
                HeraProjectGroupUser user = new HeraProjectGroupUser();
                user.setProjectGroupId(groupId);
                user.setUser(t);
                user.setStatus(0);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                toCreateUsers.add(user);
            });

            groupUserDao.batchInsert(toCreateUsers);
        }


        if(!CollectionUtils.isEmpty(request.getApps())){

            List<HeraProjectGroupApp> toCreateApps = new ArrayList<>();
            for(HeraProjectGroupAppRequest app : request.getApps()){
                if(app.getAppId() == null || app.getPlatFormType() == null){
                    log.error("create project group param app is error! request:{}",request.toString());
                    continue;
                }

                Integer baseInfoId = heraBaseInfoDao.idByBindIdsAndPlat(String.valueOf(app.getAppId()), app.getPlatFormType());
                HeraProjectGroupApp groupApp = new HeraProjectGroupApp();
                groupApp.setProjectGroupId(groupId);
                groupApp.setAppBaseInfoId(baseInfoId);
                groupApp.setStatus(0);
                groupApp.setCreateTime(new Date());
                groupApp.setUpdateTime(new Date());
                toCreateApps.add(groupApp);
            }

            projectGroupAppDao.batchInsert(toCreateApps);
        }

        return Result.success(groupId);

    }


    public Result update(HeraProjectGroupDataRequest request){

        if(request.getParentGroupId() != null){
            Integer parentGroupId = request.getParentGroupId();
            List<HeraProjectGroupModel> heraProjectGroupModels = projectGroupDao.listByIds(Lists.newArrayList(parentGroupId), request.getType(), null);
            if(heraProjectGroupModels != null && heraProjectGroupModels.size() < 1){
                return Result.fail(-1,"there is no project Group data which Id is "+parentGroupId);
            }
        }

        if(request.getRelationObjectId() != null && request.getType() != null){
            HeraProjectGroupModel projectGroup = new HeraProjectGroupModel();
            projectGroup.setRelationObjectId(request.getRelationObjectId());
            projectGroup.setType(request.getType());
            List<HeraProjectGroupModel> search = projectGroupDao.search(projectGroup, null, null);
            if(!CollectionUtils.isEmpty(search) && !search.get(0).getId().equals(request.getId())){
                return Result.fail(-1,"the data relationObjectId: " +request.getRelationObjectId()+ " has exist!");
            }
        }


        log.info("update project Group request:{}",new Gson().toJson(request));
        HeraProjectGroup group = new HeraProjectGroup();
        BeanUtils.copyProperties(request,group);
        log.info("update project Group group:{}",new Gson().toJson(group));
        Integer groupId = projectGroupDao.update(group);

        updateUsers(request);

        updateApps(request);

        return Result.success(groupId);

    }

    public Result delete(Integer id){

        HeraProjectGroupModel model = new HeraProjectGroupModel();
        model.setParentGroupId(id);
        List<HeraProjectGroupModel> search = projectGroupDao.search(model, null, null);
        if(search != null && search.size() > 0){
            return Result.fail(-1,"The current node cannot be deleted because it has child nodes！");
        }

        List<HeraProjectGroupApp> apps = projectGroupAppDao.listByProjectGroupId(id);
        if(!CollectionUtils.isEmpty(apps)){
            return Result.fail(-1,"The current node cannot be deleted because it has apps！");
        }

        projectGroupAppDao.delByGroupId(id);

        groupUserDao.delByGroupId(id);

        projectGroupDao.delById(id);

        return Result.success(id);

    }

    private void updateUsers(HeraProjectGroupDataRequest request){

        //参数为null，认为本次不更新，空list回删除全部现有数据
        if(request.getUsers() != null){

            List<String> updateUsers = request.getUsers();

            List<HeraProjectGroupUser> existUsers = groupUserDao.listByProjectGroupId(request.getId());

            if(CollectionUtils.isEmpty(existUsers)){
                List<HeraProjectGroupUser> users = new ArrayList<>();
                updateUsers.forEach(t -> {
                    HeraProjectGroupUser userI = new HeraProjectGroupUser();
                    userI.setProjectGroupId(request.getId());
                    userI.setUser(t);
                    userI.setStatus(0);
                    userI.setCreateTime(new Date());
                    userI.setUpdateTime(new Date());
                    users.add(userI);
                });
                Integer integer = groupUserDao.batchInsert(users);

                return;
            }


            /**
             * 删除本次参数不包含的成员（需先进行此步骤）
             */
            List<HeraProjectGroupUser> delUsers = existUsers.stream().filter(t -> !updateUsers.contains(t.getUser())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(delUsers)) {
                delUsers.forEach(t -> {
                    groupUserDao.delById(t.getId());
                });
            }

            if(CollectionUtils.isEmpty(updateUsers)){
                return;
            }

            /**
             * 添加本次新增的成员
             */
            updateUsers.removeAll(existUsers.stream().map(t -> t.getUser()).collect(Collectors.toList()));

            List<HeraProjectGroupUser> users = new ArrayList<>();
            updateUsers.forEach(t -> {
                HeraProjectGroupUser userI = new HeraProjectGroupUser();
                userI.setProjectGroupId(request.getId());
                userI.setUser(t);
                userI.setStatus(0);
                userI.setCreateTime(new Date());
                userI.setUpdateTime(new Date());
                users.add(userI);

            });
            Integer integer = groupUserDao.batchInsert(users);

        }
    }

    private void updateApps(HeraProjectGroupDataRequest request){

        List<HeraProjectGroupAppRequest> updateApps = request.getApps();

        if(updateApps == null){
            return;
        }

        List<HeraProjectGroupApp> existApps = projectGroupAppDao.listByProjectGroupId(request.getId());

        if(CollectionUtils.isEmpty(existApps)){

            //没有历史数据，本次也没有同步新数据
            if(updateApps.size() == 0){
                return;
            }

            List<HeraProjectGroupApp> apps = new ArrayList<>();
            for(HeraProjectGroupAppRequest appRequest : updateApps){

                if(appRequest.getAppId() == null || appRequest.getPlatFormType() == null){
                    log.error("updateApps param app is invalid! request:{}",request.toString());
                    continue;
                }

                Integer baseInfoId = heraBaseInfoDao.idByBindIdsAndPlat(String.valueOf(appRequest.getAppId()), appRequest.getPlatFormType());
                if(baseInfoId == null){
                    log.error("updateApps error! no baseInfo found for appId:{},platFormType:{}",appRequest.getAppId(),appRequest.getPlatFormType());
                    continue;
                }
                HeraProjectGroupApp app = new HeraProjectGroupApp();
                app.setProjectGroupId(request.getId());
                app.setAppBaseInfoId(baseInfoId);
                app.setStatus(0);
                app.setCreateTime(new Date());
                app.setUpdateTime(new Date());
                apps.add(app);
            }

            projectGroupAppDao.batchInsert(apps);
            return;
        }

        List<Integer> updateAppIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(updateApps)){
            for (HeraProjectGroupAppRequest appRequest : updateApps) {

                if(appRequest.getAppId() == null || appRequest.getPlatFormType() == null){
                    log.error("updateApps param error! request : {}",request.toString());
                    continue;
                }

                Integer baseInfoId = heraBaseInfoDao.idByBindIdsAndPlat(String.valueOf(appRequest.getAppId()), appRequest.getPlatFormType());
                if(baseInfoId != null){
                    updateAppIds.add(baseInfoId);
                }
            }
        }

        /**
         * 删除本次不包含的，如果本次传递了空list，则删除全部历史数据。
         */
        List<HeraProjectGroupApp> toDeleteApps = existApps.stream().filter(t -> !updateAppIds.contains(t.getAppBaseInfoId())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(toDeleteApps)){
            toDeleteApps.forEach(t->{
                projectGroupAppDao.delById(t.getId());
            });
        }

        if(CollectionUtils.isEmpty(updateAppIds)){
            return;
        }

        /**
         * 添加本次新增的
         */
        updateAppIds.removeAll(existApps.stream().map(t -> t.getAppBaseInfoId()).collect(Collectors.toList()));
        if(!CollectionUtils.isEmpty(updateAppIds)){
            List<HeraProjectGroupApp> apps = new ArrayList<>();
            for(HeraProjectGroupAppRequest appRequest : updateApps){

                if(appRequest.getAppId() == null || appRequest.getPlatFormType() == null){
                    log.error("updateApps param app is invalid! request:{}",request.toString());
                    continue;
                }

                Integer baseInfoId = heraBaseInfoDao.idByBindIdsAndPlat(String.valueOf(appRequest.getAppId()), appRequest.getPlatFormType());
                if(baseInfoId == null){
                    log.error("updateApps error! no baseInfo found for appId:{},platFormType:{}",appRequest.getAppId(),appRequest.getPlatFormType());
                    continue;
                }
                HeraProjectGroupApp app = new HeraProjectGroupApp();
                app.setProjectGroupId(request.getId());
                app.setAppBaseInfoId(baseInfoId);
                app.setStatus(0);
                app.setCreateTime(new Date());
                app.setUpdateTime(new Date());
                apps.add(app);
            }

            projectGroupAppDao.batchInsert(apps);
        }

    }


    public Result<ProjectGroupTreeNode> getFullTree(Integer type){

        HeraProjectGroupModel rootGroupNode = getRootGroupNode(type);
        if(rootGroupNode == null){
            log.error("getFullTree no root data found for type : {}",type);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        HeraProjectGroupModel model = new HeraProjectGroupModel();
        Long count = projectGroupDao.count(model);
        if(count == null || count.intValue() <= 0){
            log.error("getFullTree error! count node data return : {}",count);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        List<HeraProjectGroupModel> allData = new ArrayList<>();
        Integer page = count.intValue() % 300 == 0 ? count.intValue() / 300 : count.intValue() / 300 + 1;
        for(int i=0;i<page;i++){
            allData.addAll(projectGroupDao.search(model,i+1,300));
        }

        TreeQueryBuilder treeBuilder = new TreeQueryBuilder(new HashSet<>(allData));

        Result<ProjectGroupTreeNode> treeByProjectGroup = treeBuilder.getTreeByProjectGroup(rootGroupNode);
        return treeByProjectGroup;
    }


    public Result<ProjectGroupTreeNode> getTreeByUser(String user,Integer type,String projectGroupName){

        HeraProjectGroupModel rootGroupNode = getRootGroupNode(type);
        if(rootGroupNode == null){
            log.error("getTreeByUser no root data found for type : {}",type);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        List<HeraProjectGroupModel> nodesByUser = getNodesByUser(user,type,projectGroupName);
        if(CollectionUtils.isEmpty(nodesByUser)){
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        Set<HeraProjectGroupModel> treeNodes = pathToRootData(nodesByUser,type);

        TreeQueryBuilder treeBuilder = new TreeQueryBuilder(treeNodes);
        Result<ProjectGroupTreeNode> treeByProjectGroup = treeBuilder.getTreeByProjectGroup(rootGroupNode);
        return treeByProjectGroup;
    }

    public Result<List<HeraAppBaseInfoModel>> searchGroupApps(String user,Integer groupType,Integer projectGroupId,String appName, Integer page, Integer pageSize){

        List<Integer> groupIds = new ArrayList<>();

        List<Integer> userGroupIds = groupUserDao.listGroupIdsByUser(user);

        if (CollectionUtils.isEmpty(userGroupIds)) {
            log.info("getGroupApps no group data found for user! user:{},projectGroupId:{},groupType:{},appName:{}",user,projectGroupId,groupType,appName);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        if(projectGroupId != null){
            if(!userGroupIds.contains(projectGroupId)){
                log.info("getGroupApps user:{} has no authorization for assign projectGroupId:{},groupType:{}",user,projectGroupId,groupType);
                return Result.fail(CommonError.NO_AUTHORIZATION);
            }
            groupIds.add(projectGroupId);
        }else{
            groupIds.addAll(userGroupIds);
        }

        List<HeraProjectGroupModel> projectGroupsByType = projectGroupDao.listByIds(groupIds, groupType, null);
        if(CollectionUtils.isEmpty(projectGroupsByType)){
            log.info("getGroupApps no assign type group data found for user! user:{},projectGroupId:{},groupType:{},appName:{}",user,projectGroupId,groupType,appName);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        //重置为指定类型的组
        groupIds = projectGroupsByType.stream().map(t -> t.getId()).collect(Collectors.toList());

        List<Integer> appBaseInfoIds = projectGroupAppDao.getAppBaseInfoIds(groupIds);
        if(CollectionUtils.isEmpty(appBaseInfoIds)){
            log.info("getGroupApps no apps data found! user:{},appBaseInfoIds:{},groupType:{}",user,appBaseInfoIds,groupType);
            return Result.fail(CommonError.NOT_EXISTS_DATA);
        }

        HeraAppBaseInfoModel appBaseInfoModel = new HeraAppBaseInfoModel();
        if(StringUtils.isNotBlank(appName)){
            appBaseInfoModel.setAppName(appName);
        }
        List<HeraAppBaseInfo> heraAppBaseInfos = heraBaseInfoDao.searchAppsByIds(appBaseInfoIds, appBaseInfoModel, page, pageSize);
        if(CollectionUtils.isEmpty(heraAppBaseInfos)){
            return Result.success();
        }

        List<HeraAppBaseInfoModel> list = new ArrayList<>();
        heraAppBaseInfos.forEach(t -> {
            HeraAppBaseInfoModel model = new HeraAppBaseInfoModel();
            BeanUtils.copyProperties(t, model);
            list.add(model);
        });

        return Result.success(list);
    }

    private HeraProjectGroupModel getRootGroupNode(Integer type){

        HeraProjectGroupModel model = new HeraProjectGroupModel();
        model.setParentGroupId(-1);
        model.setType(type);
        List<HeraProjectGroupModel> rootNodes = projectGroupDao.search(model,null,null);

        if(CollectionUtils.isEmpty(rootNodes)){
            log.error("getRootGroupNode error! no data parenId is -1 for type : {}",type);
            return null;
        }

        if(rootNodes.size() > 1){
            log.error("getRootGroupNode error! more than 1 root data found! parenId is -1 and type : {}",type);
        }

        return rootNodes.get(0);
    }


    public List<HeraProjectGroupModel> getNodesByUser(String user,Integer type,String projectGroupName){

        if(StringUtils.isBlank(user)){
            log.error("getNodesByUser user is invalid!user:{},projectGroupName:{}",user);
            return null;
        }

        List<Integer> integers = groupUserDao.listGroupIdsByUser(user);
        if (CollectionUtils.isEmpty(integers)) {
            return null;
        }

        return projectGroupDao.listByIds(integers,type,projectGroupName);

    }

    /**
     * 遍历收集指定节点到根节点路径上的所有节点数据
     *
     * @param searchData
     * @return
     */
    public Set<HeraProjectGroupModel> pathToRootData(List<HeraProjectGroupModel> searchData,Integer type){

        Set<HeraProjectGroupModel> result = new HashSet<>();

        if(CollectionUtils.isEmpty(searchData)){
            return result;
        }
        result.addAll(searchData);

        List<Integer> parentIds = searchData.stream().filter(t -> t.getParentGroupId() != null && t.getParentGroupId().intValue() != -1).map(k -> k.getParentGroupId()).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(parentIds)){
            return result;
        }

        List<HeraProjectGroupModel> parentNodes = projectGroupDao.listByIds(parentIds,type,null);
        if(CollectionUtils.isEmpty(parentNodes)){
            return result;
        }

        result.addAll(pathToRootData(parentNodes,type));

        return result;

    }


}
