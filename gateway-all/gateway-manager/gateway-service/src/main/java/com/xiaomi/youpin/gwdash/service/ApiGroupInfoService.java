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

package com.xiaomi.youpin.gwdash.service;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.bo.openApi.GwGroupEntity;
import com.xiaomi.youpin.gwdash.bo.openApi.GwUser;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import com.xiaomi.youpin.gwdash.dao.ApiGroupInfoDao;
import com.xiaomi.youpin.gwdash.dao.mapper.GwUserInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.gwdash.dao.model.GwUserInfo;
import com.xiaomi.youpin.gwdash.dao.model.MetaData;
import com.xiaomi.youpin.gwdash.dao.model.MetaDataRelation;
import com.xiaomi.youpin.gwdash.exception.CommonError;

import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.entity.Group;
//import com.xiaomi.youpin.hermes.service.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// todo: 需要提供全量的dubbo服务
@Service(group="${owner.dubbo.group}", interfaceClass = GroupServiceAPI.class, timeout = 5000)
public class ApiGroupInfoService implements GroupServiceAPI{

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupInfoService.class);

    @Autowired
    private ApiGroupInfoDao groupInfoDao;

//    @Reference(check = false, interfaceClass = GroupService.class, group = "${ref.hermes.service.group}")
//    private GroupService oldGroupService;

    @Autowired
    private GroupInfoService groupService;

    @Resource
    private GwUserInfoMapper userInfoDao;

    @Autowired
    private MetaDataService metaDataService;

    @Autowired
    private MetaDataRelationService metaDataRelationService;

    @Autowired
    UserService userService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private TenantComponent tenementService;

    /**
     * 用于信息校验
     */
    private static Cache<Integer, Boolean> apiGroupCache = newCache();

    private static synchronized Cache<Integer, Boolean> newCache() {
        if (apiGroupCache != null) {
            return apiGroupCache;
        }
        LOGGER.info("gw manager api group info service created new api group cache");

        // current size is 63
        return CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    public static Cache<Integer, Boolean> getApiGroupCache() {
        if (apiGroupCache == null) {
            apiGroupCache = newCache();
        }
        return apiGroupCache;
    }

    private synchronized void reloadCache() {
        if (!getApiGroupCache().asMap().isEmpty()) {
            return;
        }
        List<ApiGroupInfo> apiGroups = this.groupInfoDao.getApiGroupListAll();
        apiGroups.forEach(group -> getApiGroupCache().put(group.getGid(), true));
        LOGGER.info("gw manager api group info service reloaded api group cache, size is {}", getApiGroupCache().size());
    }

    public boolean existedApiGroup(Integer input) {
        if (getApiGroupCache().asMap().isEmpty()) {
            this.reloadCache();
        }
        try {
            if (getApiGroupCache().asMap().containsKey(input)) {
                return getApiGroupCache().asMap().get(input);
            }
            return getApiGroupCache().get(input, () -> {
                ApiGroupInfo groupInfo = this.groupInfoDao.getApiGroupByGid(input);
                boolean res = groupInfo != null;
                getApiGroupCache().put(input, res);

                if (!res) {
                    LOGGER.warn("ApiGroupInfoService existedApiGroup invalid group id {}, cache size of {}",
                            input, getApiGroupCache().size());
                }
                return res;
            });
        } catch (ExecutionException e) {
            LOGGER.error("ApiGroupInfoService existedApiGroup error", e);
            return false;
        }
    }

    public Result<Void> newApiGroupInfo(ApiGroupInfoParam param) {

        CheckResult chkResult = BizUtils.chkNewApiGroupInfoParam(param);

        if (!chkResult.isValid()) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] invalid param, check msg: {}, param: {}",
                    chkResult.getMsg(), param);
            return new Result<>(chkResult.getCode(), chkResult.getMsg());
        }

        GroupInfoEntity group = new GroupInfoEntity();
        group.setName(param.getName());
        group.setDescription(param.getDescription());
        int id = groupService.createGroup(group);
        if (id <= 0) {
            return new Result<>(CommonError.InvalidParamError.getCode(), CommonError.InvalidParamError.getMessage());
        }

        ApiGroupInfo groupInfo = new ApiGroupInfo();
        try {
            BeanUtils.copyProperties(param, groupInfo);

        } catch (Exception e) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] failed to execute bean copy for ApiGroupInfoParam, param: {}, err: {}",
                    param, e);
            return new Result<>(CommonError.BeanCopyError.getCode(), CommonError.BeanCopyError.getMessage());
        }
        groupInfo.setGid(id);

        int inserted = groupInfoDao.newApiGroupInfo(groupInfo);

        if (inserted <= 0) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] failed to insert api group info into db, group info: {}", groupInfo);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage());
        }

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }


    /**
     * @param groupList 非admin会把账号的分组信息带过来直接用
     * @return
     */
    public Result<ApiGroupInfoListResult> getApiGroupListAll(List<GroupInfoEntity> groupList) {
        ApiGroupInfoListResult ret = new ApiGroupInfoListResult();
        List<GroupInfoEntity> groups = new ArrayList<>();
        if (groupList == null) {
            groups = groupService.getAllGroups();
        } else {
            groups = groupList;
        }
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
        }
        List<ApiGroupInfo> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (GroupInfoEntity e : groups) {
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
                BeanUtils.copyProperties(e, apiGroupInfo);
                apiGroupInfo.setGid(e.getId());

                if (e.getCreationDate() != null && e.getModifyDate() != null) {
                    apiGroupInfo.setCtime(e.getCreationDate().getTime());
                    apiGroupInfo.setUtime(e.getModifyDate().getTime());
                }
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                groupInfos.add(apiGroupInfo);
            }
        }
        ret.setGroupList(groupInfos);
        ret.setTotal(groupInfos.size());
        LOGGER.debug("[ApiGroupInfoService.getApiGroupListAll] result: {}", ret);

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    /**
     * @param groupList 非admin会把账号的分组信息带过来直接用
     * @return
     */
    public Result<ApiGroupInfoListResult> getApiGroupListAll2(List<GroupInfoEntity> groupList) {


        ApiGroupInfoListResult ret = new ApiGroupInfoListResult();
        List<GroupInfoEntity> groups = new ArrayList<>();
        if (groupList == null) {
            groups = groupService.getAllGroups();
        } else {
            groups = groupList;
        }
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
        }
        List<ApiGroupInfo> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (GroupInfoEntity e : groups) {
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
                BeanUtils.copyProperties(e, apiGroupInfo);
                apiGroupInfo.setId(tmp.getId());
                apiGroupInfo.setGid(e.getId());

                if (e.getCreationDate() != null && e.getModifyDate() != null) {
                    apiGroupInfo.setCtime(e.getCreationDate().getTime());
                    apiGroupInfo.setUtime(e.getModifyDate().getTime());
                }
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                groupInfos.add(apiGroupInfo);
            }
        }
        ret.setGroupList(groupInfos);
        ret.setTotal(groupInfos.size());
        LOGGER.debug("[ApiGroupInfoService.getApiGroupListAll] result: {}", ret);

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    public Result<Integer> delApiGroup(IDsParam param) {

        if (param == null || param.getIds() == null || param.getIds().size() <= 0) {
            LOGGER.error("[ApiGroupInfoService.delApiGroup] invalid id list param: {}", param);
            return new Result<>(CommonError.InvalidIDParamError.getCode(), "无效的id参数列表");
        }

        int deleted = groupInfoDao.delApiGroupInfo(param.getIds());
        LOGGER.debug("[ApiGroupInfoService.delApiGroup] {} api groups have been deleted, ids: {}", deleted, param.getIds());

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), deleted);
    }

    public Result<Void> updateApiGroup(ApiGroupInfoUpdateParam param) {
        CheckResult checkResult = BizUtils.chkUpdateApiGroupParam(param);

        if (!checkResult.isValid()) {
            LOGGER.error("[ApiGroupInfoService.updateApiGroup] invalid param, check msg: {}, param: {}",
                    checkResult.getMsg(), param);
            return new Result<>(checkResult.getCode(), checkResult.getMsg());
        }

        ApiGroupInfo apiGroupInfo = groupInfoDao.getApiGroupByGid(param.getId());
        if (apiGroupInfo == null) {
            GroupInfoEntity group = groupService.queryGroupById(param.getId());
            if (group == null) {
                return new Result<>(CommonError.UpdateAccountError.getCode(), CommonError.UpdateAccountError.getMessage());
            }
            ApiGroupInfo groupInfo = new ApiGroupInfo();
            groupInfo.setBaseUrl(param.getBaseUrl());
            groupInfo.setGid(param.getId());
            groupInfo.setDescription(group.getDescription());
            groupInfo.setName(group.getName());
            groupInfoDao.newApiGroupInfo(groupInfo);
        } else {
            apiGroupInfo.setBaseUrl(param.getBaseUrl());
            apiGroupInfo.setName(param.getName());
            apiGroupInfo.setDescription(param.getDescription());
            groupInfoDao.updateApiGroupInfoById(apiGroupInfo);
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public ApiGroupInfo queryGroupById(int gid) {
        GroupInfoEntity group = groupService.queryGroupById(gid);
        if (group == null) {
            return null;
        }
        ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
        BeanUtils.copyProperties(group, apiGroupInfo);
        return apiGroupInfo;
    }

    public List<ApiGroupInfo> getGidInfosByIds(String gids) {
        List<GroupInfoEntity> list = groupService.getGroupInfoByGids(gids);
        List<ApiGroupInfo> groupInfoList = new ArrayList<>();
        if (list.size() > 0) {
            groupInfoList = list.stream().map(it -> {
                ApiGroupInfo tmp = new ApiGroupInfo();
                BeanUtils.copyProperties(it, tmp);
                return tmp;
            }).collect(Collectors.toList());

        }

        return groupInfoList;
    }

    public Map<String, Object> getApiGroupByUserName(String userName) {

        Map<String, Object> map = new HashMap<>();

        List<String> gids = userService.describeOwnerGids(userName);

        List<ApiGroupInfo> apiGroupByGids = groupInfoDao.getApiGroupByGids(null);
        map.put("apiGroups", apiGroupByGids);
        map.put("myGids", gids);
        LOGGER.debug("getApiGroupByUserName userName:{},myGis:{},result:{}", userName, gids, apiGroupByGids);
        return map;
    }


    public Result<Boolean> initGroup() {
//        List<Group> list = oldGroupService.getAllGroups();
//        LOGGER.info("initGroup list:[{}]", list);
//        list.stream().forEach(it -> {
//            GroupInfoEntity old = groupService.queryGroupById(it.getId());
//            if(old == null){
//                GroupInfoEntity groupInfoEntity = new GroupInfoEntity();
//                groupInfoEntity.setId(it.getId());
//                groupInfoEntity.setName(it.getName());
//                groupInfoEntity.setDescription(it.getDescription());
//                groupInfoEntity.setCreationDate(it.getCreationDate());
//                groupInfoEntity.setModifyDate(it.getModifyDate());
//                groupService.createGroupWithId(groupInfoEntity);
//            }else{
//                old.setName(it.getName());
//                old.setDescription(it.getDescription());
//                old.setCreationDate(it.getCreationDate());
//                old.setModifyDate(it.getModifyDate());
//                groupService.updateGroupInfo(old);
//            }
//        });
        return Result.success(true);
    }

    public Result<Boolean> initGid() {
        List<Account> list = userService.getAllAccountList();
        list.stream().forEach(it -> {
            GWAccount gwAccount = userInfoDao.queryAccountById(it.getId().intValue());
            GwUserInfo gwUserInfo = new GwUserInfo();
            if(gwAccount == null){
                gwUserInfo.setId(it.getId().intValue());
                gwUserInfo.setUserName(it.getUserName());
                gwUserInfo.setUserPhone("");
                gwUserInfo.setGids(it.getGid());
                gwUserInfo.setStatus((byte) 0);
                gwUserInfo.setCreateDate(new Date());
                gwUserInfo.setModifyDate(new Date());
                userInfoDao.insertWithId(gwUserInfo);
            }
        });
        return Result.success(true);
    }

    @Override
    public GwUser describeUserByName(String username) {
        GwUser gwUser = new GwUser();
        if(StringUtils.isBlank(username)){
            return gwUser;
        }
        GWAccount gwAccount = userService.queryGWUserByName(username);
        LOGGER.debug("describeUserByName username:[{}], gwAccount:[{}]", username, gwAccount);

        if(gwAccount == null){
            return gwUser;
        }

        gwUser.setId(gwAccount.getId());
        gwUser.setUserName(gwAccount.getUserName());
        gwUser.setGid(gwAccount.getGid());

        List<GwGroupEntity> entities = new ArrayList<>();
        if(gwAccount.getGidInfos() != null && gwAccount.getGidInfos().size()>0) {
            entities = gwAccount.getGidInfos().stream().map((e) -> {
                GwGroupEntity entity = new GwGroupEntity();
                entity.setId(e.getId());
                entity.setName(e.getName());
                entity.setDescription(e.getDescription());
                return entity;
            }).collect(Collectors.toList());
        }

        gwUser.setGidInfos(entities);


        return gwUser;
    }

    public Result<ApiGroupInfoListResult> getApiGroupList(int pageNo, int pageSize) {
        ApiGroupInfoListResult ret = new ApiGroupInfoListResult();
        int qty = groupService.getTotalAmount();
        ret.setTotal(qty);
        List<GroupInfoEntity> groups = groupService.getGroupByPage(pageNo, pageSize);
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
        }
        List<ApiGroupInfo> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (GroupInfoEntity e : groups) {
            ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
            BeanUtils.copyProperties(e, apiGroupInfo);
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                //查聚合ID  根据ApiGroupInfoID 查拿到聚合ID 即source
                List<MetaDataRelation> sourceIdList = metaDataRelationService.getMetaDataRelationByTargetList(Collections.singletonList(tmp.getId()), MetaDataRelationTypeEnum.ApiGroupCluster2ApiGroup.getType());
                //查域名ID   根据source 查 taget taget即域名ID
                List<MetaDataRelation> targetIdList = new ArrayList<>();
                if (sourceIdList != null && sourceIdList.size() > 0) {
                    targetIdList = metaDataRelationService.getMetaDataRelationBySourceList(sourceIdList.stream().map(MetaDataRelation::getSource).collect(Collectors.toList()), MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType());
                    List<MetaData> metaDataNameList = metaDataService.getMetaDataList(sourceIdList.stream().map(MetaDataRelation::getSource).collect(Collectors.toList()), MetaDataTypeEnum.APiGroupCluster.getType());
                    apiGroupInfo.setMetaDataName(metaDataNameList.get(0).getName());
                }
                // 根据域名ID 查metadata表 查到域名name
                List<MetaData> domainList = new ArrayList<>();
                if (targetIdList != null && targetIdList.size() > 0) {
                    domainList = metaDataService.getMetaDataList(targetIdList.stream().map(MetaDataRelation::getTarget).collect(Collectors.toList()), MetaDataTypeEnum.Domain.getType());
                }
                if (domainList != null && domainList.size() > 0) {
                    apiGroupInfo.setDomainList(domainList.stream().map(MetaData::getName).collect(Collectors.toList()));
                }
            }
            groupInfos.add(apiGroupInfo);
        }
        ret.setGroupList(groupInfos);
        LOGGER.debug("[ApiGroupInfoService.getApiGroupList] pageNo: {}, pageSize: {}, result: {}",
                pageNo, pageSize, ret);

        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    @Override
    public ApiGroupInfoListDTO describeGroups(int pageNo, int pageSize) {
        LOGGER.info("dubbo invoker describeGroups pageNo:[{}], pageSize:[{}]", pageNo, pageSize);
        Result<ApiGroupInfoListResult> apiGroupList = getApiGroupList(pageNo, pageSize);

        ApiGroupInfoListResult result = apiGroupList.getData();
        ApiGroupInfoListDTO dto = new ApiGroupInfoListDTO();
        if(result != null){
            BeanUtils.copyProperties(result,dto);
        }

        return dto;
    }



    @Override
    public ApiGroupInfoListResultDTO describeGroupByName(List<GroupInfoEntityDTO> gids) {
        LOGGER.info("dubbo invoker describeGroupByName gids:[{}]", new Gson().toJson(gids));
        ApiGroupInfoListResultDTO ret = new ApiGroupInfoListResultDTO();
        List<GroupInfoEntityDTO> groups = new ArrayList<>();
        if (gids == null) {
            groups = groupService.getAllGroupDTOs();
        } else {
            groups = gids;
        }
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return ret;
        }

        List<ApiGroupInfoDTO> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (GroupInfoEntityDTO e : groups) {
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                ApiGroupInfoDTO apiGroupInfo = new ApiGroupInfoDTO();
                BeanUtils.copyProperties(e, apiGroupInfo);
                apiGroupInfo.setGid(e.getId());
                apiGroupInfo.setCtime(e.getCreationDate().getTime());
                apiGroupInfo.setUtime(e.getModifyDate().getTime());
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                groupInfos.add(apiGroupInfo);
            }
        }
        ret.setGroupList(groupInfos);
        ret.setTotal(groupInfos.size());
        LOGGER.info("[dubbo ApiGroupInfoService.describeGroupByName] result: {}", ret);

        return ret;
    }


    @Override
    public ApiGroupInfoListResultDTO describeGroupAll(List<GroupInfoEntityDTO> groupList) {
        ApiGroupInfoListResultDTO ret = new ApiGroupInfoListResultDTO();
        List<GroupInfoEntityDTO> groups = new ArrayList<>();
        if (groupList == null) {
            groups = groupService.getAllGroupDTOs();
        } else {
            groups = groupList;
        }
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return ret;
        }
        List<ApiGroupInfoDTO> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (GroupInfoEntityDTO e : groups) {
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                ApiGroupInfoDTO apiGroupInfo = new ApiGroupInfoDTO();
                BeanUtils.copyProperties(e, apiGroupInfo);
                apiGroupInfo.setId(tmp.getId());
                apiGroupInfo.setGid(e.getId());
                apiGroupInfo.setCtime(e.getCreationDate().getTime());
                apiGroupInfo.setUtime(e.getModifyDate().getTime());
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                groupInfos.add(apiGroupInfo);
            }
        }
        ret.setGroupList(groupInfos);
        ret.setTotal(groupInfos.size());
        LOGGER.debug("[ApiGroupInfoService.getApiGroupListAll] result: {}", ret);

        return ret;
    }

    @Override
    public ApiGroupInfoDTO describeGroupById(int gid) {
        ApiGroupInfo apiGroupByGid = groupInfoDao.getApiGroupByGid(gid);
        ApiGroupInfoDTO groupInfoDTO = new ApiGroupInfoDTO();
        groupInfoDTO.setMetaDataName(apiGroupByGid.getMetaDataName());
        groupInfoDTO.setDomainList(apiGroupByGid.getDomainList());
        groupInfoDTO.setGid(apiGroupByGid.getGid());
        groupInfoDTO.setId(apiGroupByGid.getId());
        groupInfoDTO.setName(apiGroupByGid.getName());
        groupInfoDTO.setDescription(apiGroupByGid.getDescription());
        groupInfoDTO.setBaseUrl(apiGroupByGid.getBaseUrl());
        groupInfoDTO.setCtime(apiGroupByGid.getCtime());
        groupInfoDTO.setUtime(apiGroupByGid.getUtime());

        return groupInfoDTO;
    }

    @Override
    public List<ApiGroupInfoDTO> describeGroupsByIds(List<Integer> gids) {
        List<ApiGroupInfoDTO> groupInfos = new ArrayList<>(gids.size());
        gids.stream().forEach(gid -> {
            ApiGroupInfo tmp = groupInfoDao.getApiGroupByGid(gid);
            if (tmp != null) {
                ApiGroupInfoDTO apiGroupInfo = new ApiGroupInfoDTO();
                apiGroupInfo.setMetaDataName(tmp.getMetaDataName());
                apiGroupInfo.setDomainList(tmp.getDomainList());
                apiGroupInfo.setGid(tmp.getGid());
                apiGroupInfo.setId(tmp.getId());
                apiGroupInfo.setName(tmp.getName());
                apiGroupInfo.setDescription(tmp.getDescription());
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                apiGroupInfo.setCtime(tmp.getCtime());
                apiGroupInfo.setUtime(tmp.getUtime());

                groupInfos.add(apiGroupInfo);
            }
        });
        LOGGER.debug("describeGroupsByIds gids:[{}],groupInfos:[{}]", gids, new Gson().toJson(groupInfos));
        return groupInfos;
    }

    @Override
    public List<ApiGroupInfoDTO> describeGroupsByApiIds(List<Integer> apiIds) {
        List<ApiGroupInfoDTO> groupInfos = new ArrayList<>(apiIds.size());
        ApiGroupInfo tmp;
        for (Integer id : apiIds) {
            tmp = groupInfoDao.getGroupInfoById(id);
            LOGGER.info("describeGroupsByApiIds tmp:[{}]", tmp);
            if (tmp != null) {
                ApiGroupInfoDTO apiGroupInfo = new ApiGroupInfoDTO();
                apiGroupInfo.setMetaDataName(tmp.getMetaDataName());
                apiGroupInfo.setDomainList(tmp.getDomainList());
                apiGroupInfo.setGid(tmp.getGid());
                apiGroupInfo.setId(tmp.getId());
                apiGroupInfo.setName(tmp.getName());
                apiGroupInfo.setDescription(tmp.getDescription());
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
                apiGroupInfo.setCtime(tmp.getCtime());
                apiGroupInfo.setUtime(tmp.getUtime());

                groupInfos.add(apiGroupInfo);
            }
        }
        LOGGER.info("describeGroupsByIds gids:[{}],groupInfos:[{}]",apiIds,new Gson().toJson(groupInfos));
        return groupInfos;
    }

    @Override
    public ApiGroupInfoDTO getApiGroupByBaseUrl(String urlPrefix) {
        ApiGroupInfoDTO groupInfoDTO = new ApiGroupInfoDTO();
        if(StringUtils.isBlank(urlPrefix)) {
            return groupInfoDTO;
        }

        ApiGroupInfo groupInfo = groupInfoDao.getApiGroupByBaseUrl(urlPrefix);

        groupInfoDTO.setMetaDataName(groupInfo.getMetaDataName());
        groupInfoDTO.setDomainList(groupInfo.getDomainList());
        groupInfoDTO.setGid(groupInfo.getGid());
        groupInfoDTO.setId(groupInfo.getId());
        groupInfoDTO.setName(groupInfo.getName());
        groupInfoDTO.setDescription(groupInfo.getDescription());
        groupInfoDTO.setBaseUrl(groupInfo.getBaseUrl());
        groupInfoDTO.setCtime(groupInfo.getCtime());
        groupInfoDTO.setUtime(groupInfo.getUtime());

        return groupInfoDTO;
    }

    public Result listApiGroupByRolesAndInfos(List<RoleBo> roles, List<GroupInfoEntity> gidInfos, String username) {
        return this.listApiGroupByRolesAndInfos(roles, gidInfos, username, 1);
    }

    public Result listApiGroupByRolesAndInfos(List<RoleBo> roles, List<GroupInfoEntity> gidInfos, String username, Integer version) {
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            //admin
            // 外网和内网分开处理
            if(envConfig.isInternet()){
                return Result.success(describeGroupByName(null));
            }else{
                return version == 1 ? getApiGroupListAll(null) : getApiGroupListAll2(null);
            }
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            //work
            // 外网和内网分开处理
            if(envConfig.isInternet()){
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(gidInfos);
                LOGGER.info("getApiGroupListAll work username:[{}] gids:[{}], dto:[{}]",username, gidInfos, entityDTO);
                return Result.success(describeGroupByName(entityDTO));
            }else{
                return version == 1 ? getApiGroupListAll(gidInfos) : getApiGroupListAll2(gidInfos);
            }
        } else {
            //guest
            // 外网和内网分开处理
            if(envConfig.isInternet()){
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(gidInfos);
                LOGGER.info("getApiGroupListAll guest username:[{}] gids:[{}], dto:[{}]",username, gidInfos, entityDTO);
                return Result.success(describeGroupByName(entityDTO));
            }else{
                return version == 1 ? getApiGroupListAll(gidInfos) : getApiGroupListAll2(gidInfos);
            }
        }
    }

    private List<GroupInfoEntityDTO> getEntityDTO(List<GroupInfoEntity> entities){
        List<GroupInfoEntityDTO> result = new ArrayList<>();
        if(entities == null || entities.size() == 0){
            return result;
        }

        result = entities.stream().map(t -> {
            GroupInfoEntityDTO dto = new GroupInfoEntityDTO();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setCreationDate(t.getCreationDate());
            dto.setModifyDate(t.getModifyDate());

            return dto;
        }).collect(Collectors.toList());

        return result;
    }
}
