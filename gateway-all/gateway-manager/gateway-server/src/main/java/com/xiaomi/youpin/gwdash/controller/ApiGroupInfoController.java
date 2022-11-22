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

package com.xiaomi.youpin.gwdash.controller;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.annotation.OperationLog;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.*;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gwdash.common.Consts.SKIP_MI_DUN_USER_NAME;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiGroupInfoController {

    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupInfoController.class);

    @Resource
    private ApiGroupInfoService groupService;

    @Autowired
    private GroupInfoService groupInfoService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    @Value("${skip.midun:false}")
    private boolean skipMiDun;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private GroupServiceApiRpc groupServiceAPI;

    @Autowired
    private TenantComponent tenementComponent;

    @RequestMapping(value = "/apigroup/new", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.ADD, exclusion = OperationLog.Column.RESULT)
    public Result<Void> newApiGroupInfo(@RequestBody ApiGroupInfoParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("[ApiGroupInfoController.newApiGroupInfo] param: {}", param);

        SessionAccount account = getSessionAccount();

        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.newApiGroupInfo(param);
    }


    @RequestMapping(value = "/apigroup/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result getApiGroupList(@RequestBody ListParam param, HttpServletRequest request) {

        LOGGER.info("[ApiGroupInfoController.getApiGroupList] param: {}, envConfig.isInternet:[{}]", param, envConfig.isInternet());
        if (param == null) {
            return new Result<>(CommonError.InvalidPageParamError.getCode(), CommonError.InvalidPageParamError.getMessage());
        }

        if (param.getPageNo() <= 0) {
            param.setPageNo(1);
        }

        if (param.getPageSize() <= 0) {
            param.setPageSize(Consts.DEFAULT_PAGE_SIZE);
        }

        if (!envConfig.isInternet()) {
            return groupService.getApiGroupList(param.getPageNo(), param.getPageSize());
        } else {
            return Result.success(groupServiceAPI.describeGroups(param.getPageNo(), param.getPageSize()));
        }

    }

    @RequestMapping(value = "/apigroup/listall", method = RequestMethod.GET)
    public Result getApiGroupListAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount user = loginService.getAccountFromSession();
        if (null == user && !skipMiDun) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (true) {
            return groupService.getApiGroupListAll(null);
        }

        String username = user != null ? user.getUsername() : request.getHeader(SKIP_MI_DUN_USER_NAME);

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(username);
        //获取分组信息
        SessionAccount account = getSessionAccount();
        //获取角色信息
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        // return groupService.listApiGroupByRolesAndInfos(roles, account.getGidInfos(), username);
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            //admin
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                return Result.success(groupServiceAPI.describeGroupByName(null));
            } else {
                return groupService.getApiGroupListAll(null);
            }
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            //work
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(account.getGidInfos());
                log.info("getApiGroupListAll work gids:[{}], dto:[{}]", account.getGidInfos(), entityDTO);
                return Result.success(groupServiceAPI.describeGroupByName(entityDTO));
            } else {
                return groupService.listApiGroupByRolesAndInfos(roles, account.getGidInfos(), username);
            }
        } else {
            //guest
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(account.getGidInfos());
                log.info("getApiGroupListAll guest gids:[{}], dto:[{}]", account.getGidInfos(), entityDTO);
                return Result.success(groupServiceAPI.describeGroupByName(entityDTO));
            } else {
                return groupService.listApiGroupByRolesAndInfos(roles, account.getGidInfos(), username);
            }
        }

    }

    private List<GroupInfoEntityDTO> getEntityDTO(List<GroupInfoEntity> entities) {
        List<GroupInfoEntityDTO> result = new ArrayList<>();
        if (entities == null || entities.size() == 0) {
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

    @RequestMapping(value = "/apigroup/listall2", method = RequestMethod.GET)
    public Result getApiGroupListAll2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount user = loginService.getAccountFromSession();

        if (null == user) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (true) {
            return groupService.getApiGroupListAll2(null);
        }
        String username = user.getUsername();
        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(username);
        //获取分组信息
        SessionAccount account = getSessionAccount();
        //获取角色信息
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);

        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            //admin
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                return Result.success(groupServiceAPI.describeGroupAll(null));
            } else {
                return groupService.getApiGroupListAll2(null);
            }
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            //work
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(account.getGidInfos());
                log.info("getApiGroupListAll work gids:[{}], dto:[{}]", account.getGidInfos(), entityDTO);
                return Result.success(groupServiceAPI.describeGroupAll(entityDTO));
            } else {
                return this.groupService.listApiGroupByRolesAndInfos(roles, account.getGidInfos(), username, 2);
            }

        } else {
            //guest
            // 外网和内网分开处理
            if (envConfig.isInternet()) {
                List<GroupInfoEntityDTO> entityDTO = getEntityDTO(account.getGidInfos());
                log.info("getApiGroupListAll work gids:[{}], dto:[{}]", account.getGidInfos(), entityDTO);
                return Result.success(groupServiceAPI.describeGroupAll(entityDTO));
            } else {
                return this.groupService.listApiGroupByRolesAndInfos(roles, account.getGidInfos(), username, 2);
            }
        }
    }

    @RequestMapping(value = "/apigroup/del", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.DEL, exclusion = OperationLog.Column.RESULT)
    public Result<Integer> delApiGroup(@RequestBody IDsParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("[ApiGroupInfoController.delApiGroup] param: {}", param);
        SessionAccount account = getSessionAccount();
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.delApiGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.delApiGroup] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.delApiGroup(param);
    }

    @RequestMapping(value = "/apigroup/update", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> updateApiGroup(@RequestBody ApiGroupInfoUpdateParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("[ApiGroupInfoController.updateApiGroup] param: {}", param);
        SessionAccount account = getSessionAccount();
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.updateApiGroup(param);
    }

    @RequestMapping(value = "init/group", method = RequestMethod.GET)
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Boolean> initGroup(HttpServletRequest request) {
        SessionAccount account = getSessionAccount();
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] current user not have valid account info in session");
            return new Result<>(401, "请登录", false);
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] not authorized to operate group");
            return new Result(403, CommonError.NotAuthorizedGroupOptError.getMessage(), false);
        }

        return groupService.initGroup();
    }

    @RequestMapping(value = "/init/gid", method = RequestMethod.GET)
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Boolean> initGid(HttpServletRequest request) {
        SessionAccount account = getSessionAccount();
        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] current user not have valid account info in session");
            return new Result<>(401, "请登录", false);
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] not authorized to operate group");
            return new Result(403, CommonError.NotAuthorizedGroupOptError.getMessage(), false);
        }

        return groupService.initGid();
    }


    @RequestMapping(value = "/apigroup/owngroups", method = RequestMethod.GET)
    public Result<List<Map<String, String>>> owngroups(HttpServletRequest request, HttpServletResponse response) {
        SessionAccount user = loginService.getAccountFromSession();
        if (null == user) {
            return new Result<List<Map<String, String>>>(401, "请登录", Lists.newArrayList());
        }

        String username = user.getUsername();

        List<Map<String, String>> ownGroups = groupInfoService.getOwnGroups(username);

        LOGGER.info("owngroups username:[{}], ownGroups:[{}]", username, new Gson().toJson(ownGroups));

        return Result.success(ownGroups);
    }

    private SessionAccount getSessionAccount() {
        SessionAccount account = loginService.getAccountFromSession();
        log.info("getSessionAccount account : [{}]", account);
        return account;
    }

//    private UserInfoVO getUserInfo(HttpServletRequest request) {
//        //UserInfoVO userInfo = AegisFacade.getUserInfo(request);
//        UserInfoVO userInfo = new UserInfoVO();
//        userInfo.setUser("root");
//        log.info("getUserInfo userInfo : [{}]", userInfo);
//        return userInfo;
//    }

}
