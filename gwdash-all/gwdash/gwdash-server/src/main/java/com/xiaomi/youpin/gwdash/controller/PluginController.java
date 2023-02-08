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

import com.google.common.collect.Maps;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.bo.RequestParam;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import com.xiaomi.youpin.gwdash.service.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStep;
import com.youpin.xiaomi.tesla.bo.PlugInfo;
import com.youpin.xiaomi.tesla.plugin.common.DsUtils;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gaoyibo
 * @create 2019-04-29 14:41
 */
@RestController
@Slf4j
public class PluginController {

    @Reference(group = "${dubbo.group}", interfaceClass = TeslaGatewayService.class, check = false)
    private TeslaGatewayService teslaGatewayService;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private Dao dao;


    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private Redis redis;

    @Autowired
    private MyKsyunService myKsyunService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

//    @Autowired
//    private ResourceService resourceService;

//    @Autowired
//    private AgentManager agentManager;


//    @Autowired
//    private WebSSHService webSSHService;

    /**
     * 展示 plugins 列表
     **/
    @RequestMapping(value = "/api/plugin/list", method = RequestMethod.GET)
    public Result<List<PluginInfoBo>> getApiList() {
        List<PluginInfoBo> pluginList = dao.query(PluginInfoBo.class, null);
        pluginList.forEach(it -> {
            int dataId = it.getDataId();
            PluginData data = pluginService.getLastPluginData(dataId);
            if (null != data) {
                it.setDataId(data.getId());
                it.setDataVersion(data.getVersion());
            }
        });
        return Result.success(pluginList);
    }


    /**
     * 上传 plugin (create)
     * 新建
     */
    @RequestMapping(value = "/api/plugin/create/upload", method = RequestMethod.POST)
    public Result<Boolean> createUpload(HttpServletRequest request, HttpServletResponse response, @org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        String url = request.getParameter("url");
        String projectId = request.getParameter("projectId");
        return pluginService.execUpload(file, account, url, projectId);
    }

    /**
     * 创建plugin
     *
     * @param request
     * @param response
     * @param url
     * @param projectId
     * @param group
     * @param name
     * @param commitId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/plugin/create", method = RequestMethod.POST)
    public Result<Boolean> createPlugin(
            HttpServletRequest request,
            HttpServletResponse response,
            @org.springframework.web.bind.annotation.RequestParam("url") String url,
            @org.springframework.web.bind.annotation.RequestParam("projectId") int projectId,
            @org.springframework.web.bind.annotation.RequestParam("group") String group,
            @org.springframework.web.bind.annotation.RequestParam("name") String name,
            @org.springframework.web.bind.annotation.RequestParam("commitId") String commitId) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return pluginService.createPlugin(account, url, projectId, group, name, commitId);
    }

    /**
     * 编辑plugin
     *
     * @param request
     * @param response
     * @param pluginId
     * @param url
     * @param projectId
     * @param commitId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/plugin/edit", method = RequestMethod.POST)
    public Result<Boolean> editPlugin(
            HttpServletRequest request,
            HttpServletResponse response,
            @org.springframework.web.bind.annotation.RequestParam("pluginId") int pluginId,
            @org.springframework.web.bind.annotation.RequestParam("url") String url,
            @org.springframework.web.bind.annotation.RequestParam("projectId") int projectId,
            @org.springframework.web.bind.annotation.RequestParam("commitId") String commitId) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return pluginService.editPlugin(account, pluginId, url, projectId, commitId);
    }


    /**
     *
     */
    @RequestMapping(value = "/api/plugin/commits", method = RequestMethod.GET)
    public Result<List<GitlabCommit>> getGitlabCommits(
            HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestParam("group") String group,
            @org.springframework.web.bind.annotation.RequestParam("name") String name
    ) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return pluginService.getGitlabCommits(group, name, account);
    }


    /**
     * 上传 plugin (update)
     * 在原有plugin上更新jar包数据
     */
    @RequestMapping(value = "/api/plugin/update/upload", method = RequestMethod.POST)
    public Result<Boolean> updateUpload(HttpServletRequest request, HttpServletResponse response, @org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[AccountController.newApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        String url = request.getParameter("url");
        String pluginId = request.getParameter("pluginId");
        String uploadFile = request.getParameter("uploadFile");
        if (uploadFile == null) {
            uploadFile = "true";
        }

        String projectId = request.getParameter("projectId");
        return pluginService.execUpdateUpload(Integer.valueOf(pluginId), file, account, url, uploadFile, projectId);
    }


    /**
     * 获取 plugin 版本列表
     * 需要传pluginId
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/plugin/version/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<PluginData>> list(@RequestBody PluginDataListParam param) {
        List<PluginData> list = dao.query(PluginData.class, Cnd.where("plugin_id", "=", param.getPluginId()));
        List<PluginData> newList = list.stream().filter(it -> {
            if (null != it.getData()) {
                return true;
            } else if (0 != it.getCompileId()) {
                ProjectCompileRecord projectCompileRecord =
                        dao.fetch(ProjectCompileRecord.class, Cnd.where("id", "=", it.getCompileId()));
                if (null == projectCompileRecord) {
                    return false;
                }
                if (projectCompileRecord.getStep() == TaskStep.upload.ordinal()
                        && projectCompileRecord.getStatus() == TaskStatus.success.ordinal()) {
                    it.setData(myKsyunService.getFileByDownloadKey(projectCompileRecord.getJarKey()));
                    String version = it.getCommitId();
                    int len = version.length();
                    it.setVersion(len >= 7 ? version.substring(0, 7) : version);
                    dao.update(it);
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return Result.success(newList);
    }


    /**
     * 删除 plugin:
     * 1. 本地删除 plugin 文件
     * 2. tesla service 删除
     * ttt
     */
    @RequestMapping(value = "/api/plugin/delete", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> delete(@RequestBody PluginDeleteParam param) {
        PlugInfo plugInfo = new PlugInfo();
        Integer id = param.getId();

        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, id);

        //归档
        bo.setStatus(2);

        dao.update(bo);

        try {
            plugInfo.setPluginId(bo.getName());
            plugInfo.setName(bo.getName());
            plugInfo.setFileName(bo.getName() + ".jar");
            teslaGatewayService.stopPlugin(plugInfo);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return Result.success(true);
    }


    @RequestMapping(value = "/api/plugin/update/version", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> updatePluginVersion(@RequestBody PluginUpdateVersionParam param) {
        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, param.getId());
        bo.setDataId(param.getDataId());
        dao.update(bo);
        return Result.success(true);
    }


    /**
     * 启动plugin  (100 发起了流程  200 还没审批(审核中) 300 被拒绝 0 执行成功 -2 没有权限)
     *
     * @param request
     * @param response
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/plugin/start", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Integer> start(HttpServletRequest request, HttpServletResponse response, @RequestBody PluginDeleteParam param) {
        log.info("start");

        SessionAccount account = loginService.getAccountFromSession(request);

        int accountId = account.getId().intValue();
        param.setAccountId(accountId);
        if (null != param.getGroupList()) {
            PluginInfoBo info = dao.fetch(PluginInfoBo.class, param.getId());
            if (null != info) {
                info.setGroupInfo(param.getGroupList().stream().collect(Collectors.joining(",")));
            }
        }


        return pluginService.execStart(param);
    }

    @RequestMapping(value = "/api/plugin/stop", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> stop(HttpServletRequest request, @RequestBody PluginStopParam param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        PluginInfoBo info = dao.fetch(PluginInfoBo.class, param.getId());
        if (null != info) {
            log.info(info.getGroupInfo());
        }

        param.setAccountId(account.getId().intValue());

        return pluginService.execStop(param);
    }


    /**
     * 获取分组信息
     *
     * @return
     */
    @RequestMapping(value = "/api/plugin/group/list", method = RequestMethod.GET)
    public Result<List<GroupInfo>> groupList() {
        List<Record> list = dao.query("gateway_server_info", null, null, "distinct `group`");
        List<GroupInfo> data = list.stream().map(it -> {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setName(it.getString("group"));
            return groupInfo;
        }).collect(Collectors.toList());

        return Result.success(data);
    }


    //------------------for idea plugin------------


    /**
     * idea插件上传 plugin
     */
    @RequestMapping(value = "/open/plugin/upload", method = RequestMethod.POST)
    public Result<Boolean> uploadWithNoAuth(HttpServletRequest request, @org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) {
        String token = request.getParameter("token");
        String userName = request.getParameter("userName");
        Account account = userService.queryUserByName(userName);
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), token)) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }


        String url = request.getParameter("url");

        SessionAccount sessionAccount = new SessionAccount();
        sessionAccount.setUsername("plugin");
        return pluginService.execUpload(file, sessionAccount, url, "");
    }


    /**
     * idea插件上传(更新) plugin
     */
    @RequestMapping(value = "/open/plugin/update/upload", method = RequestMethod.POST)
    public Result<Boolean> updateUploadWithNoAuth(HttpServletRequest request, @org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) {
        String token = request.getParameter("token");
        String userName = request.getParameter("userName");

        Account account = userService.queryUserByName(userName);
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), token)) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }

        String url = request.getParameter("url");

        SessionAccount sessionAccount = new SessionAccount();
        sessionAccount.setUsername("plugin");
        String pluginId = request.getParameter("pluginId");
        return pluginService.execUpdateUpload(Integer.valueOf(pluginId), file, sessionAccount, url, "true", "");
    }


    /**
     * idea插件发出启动插件命令
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/plugin/start", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Integer> startWithNoAuth(@RequestBody PluginDeleteParam param) {
        log.info("start plugin user:{}", param.getUserName());
        Account account = userService.queryUserByName(param.getUserName());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        param.setAccountId(account.getId().intValue());
        return pluginService.execStart(param);
    }

    /**
     * idea插件发出停止命令
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/plugin/stop", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> stopWithNoAuth(@RequestBody PluginStopParam param) {
        Account account = userService.queryUserByName(param.getUserName());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        param.setAccountId(account.getId().intValue());
        return pluginService.execStop(param);
    }

    /**
     * idea插件查询信息
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/plugin/info", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<PluginInfoBo> info(@RequestBody PluginStatusParam param) {
        log.info("info user:{}", param.getUserName());
        Account account = userService.queryUserByName(param.getUserName());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, Cnd.where("name", "=", param.getName()));
        if (null != bo) {
            return Result.success(bo);
        }
        //就是没有此插件
        return Result.success(null);
    }


    /**
     * 获取datasource的配置
     * 方便plugin 开发和单侧
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/open/plugin/dsconfig", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<String> getDsConfig(@RequestBody GetDsParam param) {
        Account account = userService.queryUserByName(param.getUserName());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        ApiInfoExample example = new ApiInfoExample();
        example.createCriteria().andUrlEqualTo(param.getUrl());
        List<ApiInfo> list = apiInfoMapper.selectByExample(example);
        if (list.size() <= 0) {
            return Result.success("");
        }

        ApiInfo info = list.get(0);
        String dsIds = info.getDsIds();
        if (StringUtils.isEmpty(dsIds)) {
            return Result.success("");
        }

        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, Cnd.where("url", "=", param.getUrl()));

        String json = DsUtils.dsToString(dsIds, String.valueOf(bo.getId()), it -> redis.get(Keys.dsKey(Integer.valueOf(it))));
        return Result.success(json);
    }

//    /**
//     * 获取docker资源列表
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/docker/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Map<String, Object>> resourceList(@RequestBody RequestParam param) {
//        checkAccount(param, "open/docker/list");
//        com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> result = resourceService.getResourceList(1, 100, 0, Maps.newHashMap());
//        if (result.getCode() == 0) {
//            return Result.success(result.getData());
//        }
//        return Result.success(Maps.newHashMap());
//    }
//
//
//    /**
//     * 获取所有机器列表
//     *
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/machine/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<List<String>> machineList(@RequestBody RequestParam param) {
//        checkAccount(param, "open/machine/list");
//        List<String> list = agentManager.clientList();
//        return Result.success(list);
//    }
//
//
//    @RequestMapping(value = "/open/plantsshkey", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> plantSshKey(@RequestBody RequestParam param) {
//        checkAccount(param, "open/plantSshKey");
//        webSSHService.plantSshKey(param.getAddress());
//        return Result.success(true);
//    }
//
//
//    @RequestMapping(value = "/open/removesshkey", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> removeSshKey(@RequestBody RequestParam param) {
//        checkAccount(param, "open/removeSshKey");
//        webSSHService.removeSshKey(param.getAddress());
//        return Result.success(true);
//    }
//
//
//    private void checkAccount(@RequestBody RequestParam param, String cmd) {
//        Account account = userService.queryUserByName(param.getUserName());
//        log.info("open call {} {}", account.getName());
//        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
//            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
//        }
//    }
//

}
