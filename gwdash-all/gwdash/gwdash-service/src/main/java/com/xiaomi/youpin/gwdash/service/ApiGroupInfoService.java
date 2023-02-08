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

import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoListResult;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoParam;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoUpdateParam;
import com.xiaomi.youpin.gwdash.bo.IDsParam;
import com.xiaomi.youpin.gwdash.common.BizUtils;
import com.xiaomi.youpin.gwdash.common.CheckResult;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.ApiGroupInfoDao;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.hermes.entity.Group;
import com.xiaomi.youpin.hermes.service.GroupService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiGroupInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupInfoService.class);

    @Autowired
    private ApiGroupInfoDao groupInfoDao;

    @Reference(check = false, interfaceClass = GroupService.class, group = "${ref.hermes.service.group}")
    private GroupService groupService;
    @Autowired
    LoginService loginService;

    public Result<Void> newApiGroupInfo(ApiGroupInfoParam param) {

        CheckResult chkResult = BizUtils.chkNewApiGroupInfoParam(param);

        if (!chkResult.isValid()) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] invalid param, check msg: {}, param: {}",
                    chkResult.getMsg(), param);
            return new Result<>(chkResult.getCode(), chkResult.getMsg());
        }

        Group group = new Group();
        group.setName(param.getName());
        group.setDescription(param.getDescription());
        int id = groupService.createGroup(group);
        if (id <= 0) {
            return new Result<>(CommonError.InvalidParamError.code, CommonError.InvalidParamError.message);
        }

        ApiGroupInfo groupInfo = new ApiGroupInfo();
        try {
            BeanUtils.copyProperties(param, groupInfo);
        } catch (Exception e) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] failed to execute bean copy for ApiGroupInfoParam, param: {}, err: {}",
                    param, e);
            return new Result<>(CommonError.BeanCopyError.code, CommonError.BeanCopyError.message);
        }
        groupInfo.setId(id);

        int inserted = groupInfoDao.newApiGroupInfo(groupInfo);

        if (inserted <= 0) {
            LOGGER.error("[ApiGroupInfoService.newApiGroupInfo] failed to insert api group info into db, group info: {}", groupInfo);
            return new Result<>(CommonError.UnknownError.code, CommonError.UnknownError.message);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }

    public Result<ApiGroupInfoListResult> getApiGroupList(int pageNo, int pageSize) {
        ApiGroupInfoListResult ret = new ApiGroupInfoListResult();
        int qty = groupService.getTotalAmount();
        ret.setTotal(qty);
        List<Group> groups = groupService.getGroupByPage(pageNo, pageSize);
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
        }
        List<ApiGroupInfo> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (Group e : groups) {
            ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
            BeanUtils.copyProperties(e, apiGroupInfo);
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                apiGroupInfo.setBaseUrl(tmp.getBaseUrl());
            }
            groupInfos.add(apiGroupInfo);
        }
        ret.setGroupList(groupInfos);
        LOGGER.debug("[ApiGroupInfoService.getApiGroupList] pageNo: {}, pageSize: {}, result: {}",
                pageNo, pageSize, ret);

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    /**
     * @param groupList 非admin会把账号的分组信息带过来直接用
     * @return
     */
    public Result<ApiGroupInfoListResult> getApiGroupListAll(List<Group> groupList) {


        ApiGroupInfoListResult ret = new ApiGroupInfoListResult();
        List<Group> groups = new ArrayList<>();
        if (groupList == null) {
            groups = groupService.getAllGroups();
        } else {
            groups=groupList;
        }
        if (groups == null || groups.size() == 0) {
            ret.setTotal(0);
            ret.setGroupList(new ArrayList<>());
            return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
        }
        List<ApiGroupInfo> groupInfos = new ArrayList<>(groups.size());
        ApiGroupInfo tmp;
        for (Group e : groups) {
            tmp = groupInfoDao.getApiGroupByGid(e.getId());
            if (tmp != null) {
                ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
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
        LOGGER.debug("[ApiGroupInfoService.getApiGroupListAll] result: {}", ret);

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    public Result<Integer> delApiGroup(IDsParam param) {

        if (param == null || param.getIds() == null || param.getIds().size() <= 0) {
            LOGGER.error("[ApiGroupInfoService.delApiGroup] invalid id list param: {}", param);
            return new Result<>(CommonError.InvalidIDParamError.code, "无效的id参数列表");
        }

        int deleted = groupInfoDao.delApiGroupInfo(param.getIds());
        LOGGER.debug("[ApiGroupInfoService.delApiGroup] {} api groups have been deleted, ids: {}", deleted, param.getIds());

        return new Result<>(CommonError.Success.code, CommonError.Success.message, deleted);
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
            Group group = groupService.queryGroupById(param.getId());
            if (group == null) {
                return new Result<>(CommonError.UpdateAccountError.code, CommonError.UpdateAccountError.message);
            }
            ApiGroupInfo groupInfo = new ApiGroupInfo();
            groupInfo.setBaseUrl(param.getBaseUrl());
            groupInfo.setGid(param.getId());
            groupInfo.setDescription(group.getDescription());
            groupInfo.setName(group.getName());
            groupInfoDao.newApiGroupInfo(groupInfo);
        } else {
            if (!param.getBaseUrl().equals(apiGroupInfo.getBaseUrl())) {
                apiGroupInfo.setBaseUrl(param.getBaseUrl());
                groupInfoDao.updateApiGroupInfoById(apiGroupInfo);
            }
        }
        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }

    public ApiGroupInfo queryGroupById(int gid) {
        Group group = groupService.queryGroupById(gid);
        if (group == null) {
            return null;
        }
        ApiGroupInfo apiGroupInfo = new ApiGroupInfo();
        BeanUtils.copyProperties(group, apiGroupInfo);
        return apiGroupInfo;
    }

    public List<ApiGroupInfo> getGidInfosByIds(String gids) {
        List<Group> list = groupService.getGroupInfoByGids(gids);
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

}
