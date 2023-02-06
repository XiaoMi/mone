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

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.mapper.PluginMapper;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStep;
import com.youpin.xiaomi.tesla.bo.PlugInfo;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_BASE;

/**
 * @author gaoyibo
 * @create 2019-04-29 22:36
 */

@Slf4j
@Service
public class PluginService {

    @Autowired
    private PluginMapper pluginMapper;

    /**
     * 需要使用异步
     */
    @Reference(group = "${dubbo.group}", interfaceClass = TeslaGatewayService.class, check = false, async = true)
    private TeslaGatewayService teslaGatewayService;

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private Redis redis;

    @Autowired
    private Dao dao;

    @Autowired
    private FlowService flowService;

    @Autowired
    private ProjectCompilationService projectCompilationService;

    @Autowired
    private GitlabService gitlabService;

    public int createNewPlugin(String name, byte[] fileContent, long ctime, long utime, String creator, String url, String projectId) {
        //0->demo-plugin  1->0.0.1
        String[] ss = name.split("_|\\.jar");

        PluginInfoBo pluginInfoBo = new PluginInfoBo();
        pluginInfoBo.setCreator(creator);
        pluginInfoBo.setCtime(ctime);
        pluginInfoBo.setUtime(utime);
        pluginInfoBo.setUrl(url);
        pluginInfoBo.setName(ss[0]);
        dao.insert(pluginInfoBo);


        long now = System.currentTimeMillis();
        PluginData pluginData = new PluginData();
        pluginData.setData(fileContent);
        pluginData.setUrl(url);
        pluginData.setVersion(ss[1]);
        pluginData.setPluginId(pluginInfoBo.getId());
        pluginData.setCtime(now);
        pluginData.setUtime(now);
        dao.insert(pluginData);


        pluginInfoBo.setDataId(pluginData.getId());
        if (StringUtils.isNotEmpty(projectId)) {
            pluginInfoBo.setProjectId(Integer.valueOf(projectId));
        }
        dao.update(pluginInfoBo);

        return pluginInfoBo.getId();
    }


    public void insertPlugin(int pluginId, String name, byte[] fileContent, long ctime, long utime, String creator, String url) {
        String[] ss = name.split("_|\\.jar");

        long now = System.currentTimeMillis();
        PluginData pluginData = new PluginData();
        pluginData.setData(fileContent);
        pluginData.setUrl(url);
        pluginData.setVersion(ss[1]);
        pluginData.setPluginId(pluginId);
        pluginData.setCtime(now);
        pluginData.setUtime(now);
        dao.insert(pluginData);

        //更新plugin的信息
        dao.update(PluginInfoBo.class, Chain.make("data_id", pluginData.getId()).add("url", url), Cnd.where("id", "=", pluginId));
    }


    public List<Plugin> getPluginList() {
        return pluginMapper.getPluginList();
    }

    public boolean deletePlugin(Integer id) {
        return pluginMapper.deletePlugin(id);
    }

    public PluginFileContent getPluginFileContent(Integer id) {
        return pluginMapper.getPluginFileContent(id);
    }

    public void updatePlugin(Integer id, Integer status, long utime, String url) {
        pluginMapper.updatePlugin(status, utime, id, url);
    }


    /**
     * 执行上传
     *
     * @param file
     * @param account
     * @param url
     * @return
     */
    public Result<Boolean> execUpload(MultipartFile file, SessionAccount account, String url, String projectId) {
        if (file == null || file.isEmpty()) {
            Result.success(false);
        }

        String originalFileName = file.getOriginalFilename();

        if (!originalFileName.endsWith(".jar")) {
            return Result.fail(CommonError.FileTypeError);
        }

        try {
            byte[] fileBytes = file.getBytes();

            long now = System.currentTimeMillis();

            log.info(account.getUsername());

            createNewPlugin(originalFileName, fileBytes, now, now, account.getUsername(), url, projectId);

            return Result.success(true);
        } catch (Exception e) {
            log.info("execUpload", e.getMessage());
            return Result.success(false);
        }
    }

    public Result<Boolean> createPlugin(SessionAccount account, String url, int projectId, String group, String name, String commitId) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        long now = System.currentTimeMillis();
        PluginInfoBo pluginInfoBo = new PluginInfoBo();
        pluginInfoBo.setCreator(account.getUsername());
        pluginInfoBo.setCtime(now);
        pluginInfoBo.setUtime(now);
        pluginInfoBo.setUrl(url);
        pluginInfoBo.setName(name);
        pluginInfoBo.setGitName(name);
        pluginInfoBo.setGitGroup(group);
        pluginInfoBo.setProjectId(projectId);
        dao.insert(pluginInfoBo);

        // 开始构建
        String gitUrl = GIT_BASE + group + "/" + name;
        CompileParam compileParam = new CompileParam();
        compileParam.setGitUrl(gitUrl);
        compileParam.setBranch(commitId);
        compileParam.setGitName(gitlabAccessToken.getName());
        compileParam.setGitToken(gitlabAccessToken.getToken());
        compileParam.setProfile("");
        compileParam.setBuildPath("");
        compileParam.setCustomParams("");
        compileParam.setJarPath(name + "-service");
        ProjectCompileRecord projectCompileRecord =
                projectCompilationService.startCloudCompile(compileParam);

        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginInfoBo.getId());
        pluginData.setUrl(url);
        pluginData.setPluginId(pluginInfoBo.getId());
        pluginData.setCompileId(projectCompileRecord.getId());
        pluginData.setCommitId(commitId);
        pluginData.setCtime(now);
        pluginData.setUtime(now);
        dao.insert(pluginData);

        return Result.success(true);
    }

    public Result<Boolean> editPlugin(SessionAccount account, int pluginId, String url, int projectId, String commitId) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        PluginInfoBo pluginInfoBo = dao.fetch(PluginInfoBo.class, Cnd.where("id", "=", pluginId));
        if (null == pluginInfoBo) {
            return new Result<>(1, "更新插件不存在", false);
        }
        long now = System.currentTimeMillis();
        pluginInfoBo.setProjectId(projectId);
        pluginInfoBo.setUrl(url);
        pluginInfoBo.setUtime(now);
        dao.update(pluginInfoBo);

        PluginData pluginData = dao.fetch(PluginData.class, Cnd.where("commit_id", "=", commitId)
                .and("plugin_id", "=", pluginInfoBo.getId()));

        if (null != pluginData) {
            ProjectCompileRecord projectCompileRecord =
                    dao.fetch(ProjectCompileRecord.class, Cnd.where("id", "=", pluginData.getCompileId()));
            if (null != projectCompileRecord) {
                int status = projectCompileRecord.getStatus();
                int step = projectCompileRecord.getStep();
                if (status == TaskStatus.running.ordinal()) {
                    return Result.success(true);
                } else if (step == TaskStep.upload.ordinal()
                    && status == TaskStatus.success.ordinal()) {
                    return Result.success(true);
                }
            }
        }

        if (null == pluginData) {
            pluginData = new PluginData();
        }

        // 开启构建
        String gitName = pluginInfoBo.getGitName();
        String gitUrl = GIT_BASE + pluginInfoBo.getGitGroup() + "/" + gitName;
        CompileParam compileParam = new CompileParam();
        compileParam.setGitUrl(gitUrl);
        compileParam.setBranch(commitId);
        compileParam.setGitName(gitlabAccessToken.getName());
        compileParam.setGitToken(gitlabAccessToken.getToken());
        compileParam.setProfile("");
        compileParam.setBuildPath("");
        compileParam.setCustomParams("");
        compileParam.setJarPath(gitName + "-service");
        ProjectCompileRecord projectCompileRecord = projectCompilationService.startCloudCompile(compileParam);
        pluginData.setPluginId(pluginInfoBo.getId());
        pluginData.setUrl(url);
        pluginData.setPluginId(pluginInfoBo.getId());
        pluginData.setCompileId(projectCompileRecord.getId());
        pluginData.setCommitId(commitId);
        pluginData.setCtime(now);
        pluginData.setUtime(now);
        dao.insertOrUpdate(pluginData);

        return Result.success(true);
    }


    /**
     * 在原有插件下,上传新的jar
     *
     * @param pluginId
     * @param file
     * @param account
     * @param url
     * @return
     */
    public Result<Boolean> execUpdateUpload(int pluginId, MultipartFile file, SessionAccount account, String url, String uploadFile, String projectId) {
        //上传文件
        if (uploadFile.equals("true")) {
            if (file == null || file.isEmpty()) {
                Result.success(false);
            }
            String originalFileName = file.getOriginalFilename();
            if (!originalFileName.endsWith(".jar")) {
                return Result.fail(CommonError.FileTypeError);
            }
            try {
                byte[] fileBytes = file.getBytes();
                long now = System.currentTimeMillis();
                log.info(account.getUsername());
                insertPlugin(pluginId, originalFileName, fileBytes, now, now, account.getUsername(), url);
                return Result.success(true);
            } catch (Exception e) {
                return Result.success(false);
            }
        } else {
            //只单纯的修改url
            PluginInfoBo bo = dao.fetch(PluginInfoBo.class, pluginId);
            bo.setUrl(url);
            bo.setUtime(System.currentTimeMillis());
            if (StringUtils.isNotEmpty(projectId)) {
                bo.setProjectId(Integer.valueOf(projectId));
            }
            dao.update(bo);
            return Result.success(true);
        }
    }


    public PluginData getLastPluginData(int dataId) {
        PluginData data = dao.fetch(PluginData.class, Cnd.where("id", "=", dataId).orderBy("utime", "desc"));
        return data;
    }


    private String getDsIds(String url) {
        ApiInfoExample example = new ApiInfoExample();
        example.createCriteria().andUrlEqualTo(url);
        List<ApiInfo> list = apiInfoMapper.selectByExample(example);
        if (list.size() == 1) {
            return list.get(0).getDsIds();
        }
        return "";
    }

    public Result<Integer> execStart(PluginDeleteParam param) {
        long now = System.currentTimeMillis();
        PlugInfo plugInfo = new PlugInfo();

        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, param.getId());

        if (StringUtils.isEmpty(bo.getFlowKey())) {
            //没有权限停止
            return new Result(900, "需要申请权限", 900);
        }

        if (!bo.getFlowKey().split(":")[1].equals(String.valueOf(param.getAccountId()))) {
            //有人正在发布中
            return new Result(901, "有人正在发布中", 901);
        }


        String flowKey = bo.getFlowKey();

        //已经发起过流程
        int status = flowService.getStatus(flowKey);
        if (status == 0) {
            return new Result(200, "审核中", 200);
        } else if (status == 2) {
            //还没审批
            bo.setFlowKey("");
            dao.update(bo);
            return new Result(300, "被拒绝", 300);
        } else if (status == 4) {
            //容错
            bo.setFlowKey("");
            dao.update(bo);
            return new Result(300, "被拒绝", 300);
        }

        plugInfo.setPluginId(bo.getName());
        plugInfo.setDataId(bo.getDataId());
        plugInfo.setFileName(bo.getName() + ".jar");
        plugInfo.setName(bo.getName());
        plugInfo.setVersion(param.getVersion());

        PluginData data = dao.fetch(PluginData.class, bo.getDataId());
        plugInfo.setData(data.getData());

        String url = bo.getUrl();

        plugInfo.setUrl(url);
        plugInfo.setDsIds(getDsIds(url));

        redis.set(Keys.pluginKey(bo.getName()), plugInfo.getDsIds());

        //debug模式,启动一台
        if (null != param.getAddressList() && param.getAddressList().size() > 0) {

            String address = param.getAddressList().get(0);
            String[] ss = address.split(":");

            RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ss[0]);
            RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, ss[1]);
            teslaGatewayService.startPlugin(plugInfo);
            //0 成功启动

            bo.setUtime(now);
            bo.setFlowKey("");
            dao.update(bo);

            return Result.success(0);
        }

        List<String> groupList = param.getGroupList();

        List<Future<Object>> futures = new ArrayList<>();
        //发送给所有gateway
        if (null == groupList || groupList.size() == 0) {
            List<GatewayServerInfo> l = dao.query(GatewayServerInfo.class, Cnd.where("utime", ">", now - TimeUnit.MINUTES.toMillis(1)));
            l.stream().forEach(it -> {
                try {
                    RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, it.getHost());
                    RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, String.valueOf(it.getPort()));
                    teslaGatewayService.startPlugin(plugInfo);
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        } else {
            //发送给选中的gateway
            groupList.stream().forEach(g -> {
                List<GatewayServerInfo> l = dao.query(GatewayServerInfo.class, Cnd.where("group", "=", g).and("utime", ">", now - TimeUnit.MINUTES.toMillis(1)));
                l.stream().forEach(it -> {
                    try {
                        RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, it.getHost());
                        RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, String.valueOf(it.getPort()));
                        teslaGatewayService.startPlugin(plugInfo);
                        futures.add(RpcContext.getContext().getFuture());
                    } catch (Throwable ex) {
                        log.error(ex.getMessage());
                    }
                });
            });
        }

        long count = futures.stream().filter(it -> {
            try {
                com.xiaomi.youpin.infra.rpc.Result<Boolean> r = (com.xiaomi.youpin.infra.rpc.Result<Boolean>) it.get(3, TimeUnit.SECONDS);
                return r.getData();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }).count();

        bo.setUtime(now);
        bo.setStatus(1);
        dao.update(bo);
        log.info("start finish:{}", count > 0);

        //-1 有没启动的
        return new Result(0, "启动成功", count > 0 ? 0 : -1);
    }

    public Result<Boolean> execStop(PluginStopParam param) {
        PlugInfo plugInfo = new PlugInfo();
        Integer id = param.getId();

        long now = System.currentTimeMillis();

        PluginInfoBo bo = dao.fetch(PluginInfoBo.class, id);

        if (StringUtils.isEmpty(bo.getFlowKey())) {
            //没有权限停止
            return new Result(900, "需要申请权限", 900);
        }

        if (!bo.getFlowKey().split(":")[1].equals(String.valueOf(param.getAccountId()))) {
            //有人正在发布中
            return new Result(901, "有人正在发布中", 901);
        }


        plugInfo.setPluginId(bo.getName());
        plugInfo.setDataId(bo.getDataId());
        plugInfo.setFileName(bo.getName() + ".jar");
        plugInfo.setName(bo.getName());
        plugInfo.setUrl(bo.getUrl());


        //debug模式,停止一台
        if (null != param.getAddressList() && param.getAddressList().size() > 0) {
            String address = param.getAddressList().get(0);
            String[] ss = address.split(":");

            RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, ss[0]);
            RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, ss[1]);
            try {
                teslaGatewayService.stopPlugin(plugInfo);
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
            return Result.success(true);
        }


        bo.setStatus(0);
        bo.setUtime(now);
        dao.update(bo);

        List<String> groupList = new ArrayList<>();

        String groupInfo = bo.getGroupInfo();
        if (StringUtils.isNotEmpty(groupInfo)) {
            String[] ss = groupInfo.split(",");
            Arrays.stream(ss).forEach(it -> groupList.add(it));
        }

        //发送给所有gateway
        if (null == groupList || groupList.size() == 0) {
            List<GatewayServerInfo> l = dao.query(GatewayServerInfo.class, Cnd.where("utime", ">", now - TimeUnit.MINUTES.toMillis(1)));
            l.stream().forEach(it -> {
                try {
                    RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, it.getHost());
                    RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, String.valueOf(it.getPort()));
                    teslaGatewayService.stopPlugin(plugInfo);
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }
            });
        } else {
            //发送给选中的gateway
            groupList.stream().forEach(g -> {
                List<GatewayServerInfo> l = dao.query(GatewayServerInfo.class, Cnd.where("group", "=", g).and("utime", ">", now - TimeUnit.MINUTES.toMillis(1)));
                l.stream().forEach(it -> {
                    try {
                        RpcContext.getContext().setAttachment(Constants.PROVIDER_IP, it.getHost());
                        RpcContext.getContext().setAttachment(Constants.PROVIDER_PORT, String.valueOf(it.getPort()));
                        teslaGatewayService.stopPlugin(plugInfo);
                    } catch (Throwable ex) {
                        log.error(ex.getMessage());
                    }
                });
            });
        }


        return Result.success(true);

    }

    public Result<List<GitlabCommit>> getGitlabCommits(String group, String name, SessionAccount account) throws UnsupportedEncodingException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        return gitlabService.getGitlabAllCommit(URLEncoder.encode(group + "/" + name, "UTF-8"), "master", gitlabAccessToken.getToken());
    }
}
