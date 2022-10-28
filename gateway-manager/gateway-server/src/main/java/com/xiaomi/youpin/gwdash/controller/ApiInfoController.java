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

import com.xiaomi.youpin.gwdash.annotation.OperationLog;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.BizUtils;
import com.xiaomi.youpin.gwdash.common.CheckResult;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.dao.model.DebugRecord;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectGen;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.ApiInfoService;
import com.xiaomi.youpin.gwdash.service.GitlabService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Dao;
import org.nutz.lang.stream.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ApiInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoController.class);

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private Dao dao;

    @RequestMapping(value = "/api/apiinfo/new", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.ADD, exclusion = OperationLog.Column.RESULT)
    public Result<Void> newApiInfo(@RequestBody ApiInfoParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("[ApiInfoController.newApiInfo] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Integer> allowGids = account.getGidInfos().stream().map(it -> it.getId()).collect(Collectors.toList());
        if (account.getRole().intValue() != Consts.ROLE_ADMIN && !allowGids.contains(param.getGroupId())) {
            LOGGER.warn("[AccountController.newApiInfo] not authorized to create api for this team");
            return Result.fail(CommonError.NotAuthorizedGroupError);
        }

        return apiInfoService.newApiInfo(param, account.getUsername());
    }


    @RequestMapping(value = "/api/apiinfo/modify/jarscript", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> modifyJarScript(@RequestBody ApiInfoUpdateParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("[ApiInfoController.modifyJarScript] param: {}", param);
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Integer> allowGids = account.getGidInfos().stream().map(it -> it.getId()).collect(Collectors.toList());
        if (account.getRole().intValue() != Consts.ROLE_ADMIN && !allowGids.contains(param.getGroupId())) {
            LOGGER.warn("[AccountController.modifyJarScript] not authorized to create api for this team");
            return Result.fail(CommonError.NotAuthorizedGroupError);
        }
        return apiInfoService.modifyScriptJarInfo(param);
    }


    /**
     * 根据项目 id 得到参数
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/apiinfo/getinfo/{id}", method = RequestMethod.GET)
    public Result<ApiInfoDetail> getInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        Project project = dao.fetch(Project.class, id);

        ProjectGen gen = project.getProjectGen();

        ApiInfoDetail apiInfoDetail = new ApiInfoDetail();

        apiInfoDetail.setServiceName("com.xiaomi.youpin.gis.service.GoodsInfoService");
        apiInfoDetail.setMethodName("getGoodsInfoList");
        apiInfoDetail.setParamTemplate("[\n" +
                "    {\n" +
                "        \"type\": \"com.xiaomi.youpin.gis.gis.GetGoodsRequest\",\n" +
                "        \"expression\": \"\"\n" +
                "    }\n" +
                "]");


        if (null != gen) {
            apiInfoDetail.setServiceName(gen.getPackageName() + ".api.service.DubboHealthService");
            apiInfoDetail.setMethodName("health");
            apiInfoDetail.setParamTemplate("[]");
        }

        List<GroupInfoEntity> gids = account.getGidInfos();
        if (gids.size() > 0) {
            apiInfoDetail.setGroupId(gids.get(0).getId());
        }

        return Result.success(apiInfoDetail);
    }

    @RequestMapping(value = "/api/apiinfo/detail", method = RequestMethod.GET)
    public Result<ApiInfoDetail> getInfoDetail(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") long id) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        String curUser = account.getUsername();
        return apiInfoService.getApiInfoDetail(id, curUser);
    }

    @RequestMapping(value = "/api/openapiinfo/new", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> openApiInfo(@RequestBody ApiInfoParam param, String token) throws IOException {
        LOGGER.info("[ApiInfoController.openApiInfo] param: {}", param);

        if (token == null || token.equals("") || !token.equals("")) {
            return Result.fail(CommonError.InvalidParamError);
        }

        return apiInfoService.newApiInfo(param, param.getName());
    }

    /**
     * 支持筛选
     * name 服务名称
     * urlString 路由
     * serviceName 服务名称
     * pathUrl 目标路径
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/apiinfo/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<ApiInfoListResult> getApiList(@RequestBody ListParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.info("[ApiInfoController.getApiList] param: {}", param);

        if (param == null) {
            return new Result<>(CommonError.InvalidPageParamError.getCode(), CommonError.InvalidPageParamError.getMessage());
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        LOGGER.info("[ApiInfoController.getApiList] account: {}", account);

        if (null == account) {
            LOGGER.warn("[AccountController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        //2019-09-12 14:36:05 根据param.getGroupType() 条件查询
        return apiInfoService.getApiList(param,account.getUsername(),account.getGidInfos()==null?new ArrayList<>():account.getGidInfos().stream().map(GroupInfoEntity::getId).collect(Collectors.toList()),account.getRole());
    }

    @RequestMapping(value = "/api/apiinfo/del", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.DEL, exclusion = OperationLog.Column.RESULT)
    public Result<Integer> delApiInfo(@RequestBody LongIDsParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.info("[ApiInfoController.delApiInfo] param: {}", param);
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.delApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Integer> gids = new ArrayList<>();
        if (account.getGidInfos() != null) {
            gids = account.getGidInfos().stream().map(it -> it.getId()).collect(Collectors.toList());
        }
        return apiInfoService.delApiInfo(param, gids, account.getRole());
    }


    /**
     * 导出配置
     *
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/apiexport", method = RequestMethod.POST, consumes = {"application/json"})
    public void exportApi(@RequestBody LongIDsParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String res = apiInfoService.export(param);
        response.setHeader("Content-Disposition", "attachment; filename=api.json");
        IOUtils.copy(new StringInputStream(res), response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * 导入配置
     *
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/apiimport", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> importApi(@RequestBody ImportParam param, HttpServletRequest request, HttpServletResponse response) {
        SessionAccount account = loginService.getAccountFromSession(request);
        Boolean ret = apiInfoService.importApi(param.getJson(), account.getUsername());
        return Result.success(ret);
    }

    @RequestMapping(value = "/api/apiInfo/updateCreator", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> updateApinfoCreator(@RequestBody ApiInfoUpdateCreatorParam apiInfoUpdateCreatorParam, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.updateApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        apiInfoUpdateCreatorParam.setOperator(account.getUsername());
        Boolean ret = apiInfoService.updateApiInfoCreator(apiInfoUpdateCreatorParam);
        return Result.success(ret);
    }

    @RequestMapping(value = "/api/apiInfo/batchUpdateCreator", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Integer> batchUpdateApinfoCreator(@RequestBody ApiInfoUpdateCreatorParam apiInfoUpdateCreatorParam, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.updateApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        apiInfoUpdateCreatorParam.setOperator(account.getUsername());
        Integer ret = apiInfoService.batchUpdateApinfoCreator(apiInfoUpdateCreatorParam);
        return Result.success(ret);
    }

    @RequestMapping(value = "/api/apiinfo/update", method = RequestMethod.POST, consumes = {"application/json"})
    @OperationLog(type = OperationLog.LogType.UPDATE, exclusion = OperationLog.Column.RESULT)
    public Result<Void> updateApiInfo(@RequestBody ApiInfoUpdateParam param,
                                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("[ApiInfoController.updateApiInfo] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.updateApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        ApiInfo dbApiInfo = apiInfoService.getOneById(param.getId());
        if(dbApiInfo == null){
            return Result.fail(CommonError.InvalidIDParamError);
        }
        Set<Integer> allowGids = account.getGidInfos().stream().map(GroupInfoEntity::getId).collect(Collectors.toSet());
        if (account.getRole() != Consts.ROLE_ADMIN && (!allowGids.contains(param.getGroupId()) || !allowGids.contains(dbApiInfo.getGroupId()))) {
            LOGGER.warn("[AccountController.updateApiInfo] not authorized to update api for this team");
            return Result.fail(CommonError.NotAuthorizedGroupError);
        }

        return apiInfoService.updateApiInfo(param, account.getUsername());
    }


    @RequestMapping(value = "/api/gitlab/commits", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<CommitHistoryResult> getCommits(@RequestBody GitlabOptParam param) {
        LOGGER.debug("[ApiInfoController.getCommits] param: {}", param);

        CheckResult chkResult = BizUtils.chkGitlabOptParam(param);
        if (!chkResult.isValid()) {
            LOGGER.error("[AccountController.getCommits] invalid param: {}, check result: {}", param, chkResult);
            return Result.fail(CommonError.InvalidParamError);
        }

        return gitlabService.getCommits("x.x.x.x", param.getProjectId(), param.getToken(), param.getPath(), param.getBranch());
    }

    @RequestMapping(value = "/api/gitlab/file", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<FileContentResult> getFileContent(@RequestBody GitlabOptParam param) {
        LOGGER.debug("[ApiInfoController.getFileContent] param: {}", param);

        CheckResult chkResult = BizUtils.chkGitlabOptParam(param);
        if (!chkResult.isValid()) {
            LOGGER.error("[AccountController.getFileContent] invalid param: {}, check result: {}", param, chkResult);
            return Result.fail(CommonError.InvalidParamError);
        }

        return gitlabService.getFileContent("x.x.x.x", param.getProjectId(), param.getToken(), param.getPath(), param.getBranch());
    }

    @RequestMapping(value = "/api/apiinfo/urlexist", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> existUrl(@RequestBody ApiInfoUpdateParam param) {
        LOGGER.debug("[ApiInfoController.existUrl] param: {}", param);
        if (param == null || StringUtils.isBlank(param.getUrl())) {
            LOGGER.error("[ApiInfoController.existUrl] invalid param for url test: {}", param);
            return Result.fail(CommonError.InvalidParamError);
        }

        boolean reslut = apiInfoService.existUrl(param.getUrl().trim(), param.getId());

        return Result.success(reslut);
    }

    @RequestMapping(value = "/api/apiinfo/getdebug", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<DebugRecord> getDebug(@RequestBody IDParam param) {
        LOGGER.debug("[ApiInfoController.getDebug] param: {}", param);
        if (param == null || param.getId() <= 0) {
            LOGGER.error("[ApiInfoController.getDebug] invalid param for getting debug info: {}", param);
            return Result.fail(CommonError.InvalidParamError);
        }

        return apiInfoService.getDebugRecordByAid(param.getId());
    }

    @RequestMapping(value = "/api/apiinfo/debug", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<DebugRecord> debug(@RequestBody ApiDebugParam param) {
        LOGGER.debug("[ApiInfoController.debug] param: {}", param);
        if (param == null || StringUtils.isBlank(param.getUrl())) {
            LOGGER.error("[ApiInfoController.debug] invalid param for url test: {}", param);
            return Result.fail(CommonError.InvalidParamError);
        }

        return apiInfoService.debug(param);
    }

    @RequestMapping(value = "/api/apiinfo/priority", method = RequestMethod.POST)
    public Result<Boolean> setPriority(
            @RequestParam("id") int id,
            @RequestParam("priority") int priority) {

        return apiInfoService.setPriority(id, priority);
    }

    @GetMapping("/api/apiinfo/get_api_modifier_by_path")
    public Result<ApiModifier> getApiModifierByPath(String path){
        SessionAccount account = loginService.getAccountFromSession();
        if(account == null){
            return Result.fail(CommonError.UnAuthorized);
        }
        return Result.success(apiInfoService.getApiModifierByPath(path));
    }
}
