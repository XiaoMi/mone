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

package com.xiaomi.youpin.tesla.ip.listener;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.Action;
import com.xiaomi.youpin.tesla.ip.bo.AiCodePromptRes;
import com.xiaomi.youpin.tesla.ip.bo.AiCodeRes;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.ProjectModuleInfo;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.ServerInfo;
import com.xiaomi.youpin.tesla.ip.common.ConfigCenter;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.JavaClassUtils;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.enums.InvokePromptEnums;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.GuideService;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import com.xiaomi.youpin.tesla.ip.util.UltramanConsole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.SafeRun;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2021/11/20
 * <p>
 * https://github.com/supsunc/swing-jcef-spring  --> 参考
 * 也是通过页面调用过来的,这种的方式好处是不用开启端口,坏处是从单独的应用调用不过来
 */
@Slf4j
public class ChromeMessageRouterHandler extends CefMessageRouterHandlerAdapter {

    private Project project;

    private Gson gson = new Gson();

    public ChromeMessageRouterHandler(Project project) {
        this.project = project;
    }

    /**
     * 前端直接可以调用过来
     *
     * @param browser    The corresponding browser.
     * @param frame      The frame generating the event. Instance only valid within the scope of this
     *                   method.
     * @param query_id   The unique ID for the query.
     * @param request
     * @param persistent True if the query is persistent.
     * @param callback   Object used to continue or cancel the query asynchronously.
     * @return
     */
    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request, boolean persistent, CefQueryCallback callback) {
        if (request.indexOf("click:") == 0) {
            String res = "success";
            String msg = request.substring(6).trim();
            Req req = gson.fromJson(msg, Req.class);

            Map<String, Object> m = Maps.newHashMap();
            Map<String, String> p = Maps.newHashMap();
            m.put("project_path", project.getBasePath());
            m.put("console", new UltramanConsole());
            p.put("project_path", project.getBasePath());

            //处理本地调用和消息处理的
            Pair<Integer, String> messageRes = LocalHandler.handler(this.project, req);
            if (messageRes.getKey() == 0) {
                callback.success(messageRes.getValue());
                return true;
            }

            //多模态调用
            String visionRes = VisionHandler.handler(this.project, req);
            if (StringUtils.isNotEmpty(visionRes)) {
                callback.success(visionRes);
                return true;
            }

            // 从右侧聊天框插入代码到左侧代码编辑区
            if (req.getCmd().equals("idea_insert_code")) {
                log.info("idea insert code");
                String code = req.getData().get("code");
                ApplicationManager.getApplication().invokeLater(() -> {
                    CodeService.insertCode(project, code);
                });
                // 代码统计
                CodeUtils.uploadCodeGenInfo(Action.INSERT_CODE_FROM_CHAT.getCode(), code, "", project);
            }

            //调用miapi 这里需要拿到用户的key
            if (req.getCmd().equals("miapi")) {
                String key = ConfigUtils.getConfig().getChatgptKey();
                res = key;
            }

            //拿到用户的z token
            if (req.getCmd().equals("z_token")) {
                String key = ConfigUtils.getConfig().getzToken();
                res = key;
            }

            if (req.getCmd().equals("user")) {
                res = AthenaContext.ins().getUserName();
            }

            //获取http服务器信息和一些ide信息
            if (req.getCmd().equals("server_info")) {
                res = this.getServerInfo(req);
            }

            // 代码生成
            if (req.getCmd().equals("code_generate")) {
                this.codeGenerate(req);
            }

            // 获取prompt信息
            if (req.getCmd().equals("prompt_info")) {
                res = this.getPromptInfo(req);
            }

            // 获取调用类型
            if (req.getCmd().equals("get_invoke_type")) {
                Map<Integer, String> invokeMthMap = InvokePromptEnums.getInvokeMthMap();
                res = gson.toJson(invokeMthMap);
            }

            // 根据prompt信息生成代码
            if (req.getCmd().equals("handler_generate")) {
                res = this.handlerGenerate(req);
            }

            // 预装的plugin能力
            if (req.getCmd().equals("ai_code_prompt")) {
                if (LabelUtils.getLabelValue(this.project, Const.OPEN_GUIDE, "true").equals("false")) {
                    AiCodeRes aiCodeRes = new AiCodeRes();
                    aiCodeRes.setMsg("Athena");
                    res = gson.toJson(aiCodeRes);
                } else {
                    res = this.getAiCodePrompt();
                }
            }

            // 获取当前的project和module信息
            if (req.getCmd().equals("get_project_info")) {
                res = this.getProjectInfo();
            }

            if (req.getCmd().equals("guide")) {
                res = gson.toJson(ConfigCenter.getGuide());
            }

            //ai导航信息(上传代码的调用也再这里):?就能开始调用的服务,主要用来测试
            if (req.getCmd().equals("ai_guide")) {
                res = GuideService.aiGuide(req, this.project);
            }


            //有些提示词需要进行替换(比如选中的代码)
            String promptRes = PromptHandler.handler(project, req);
            if (StringUtils.isNotEmpty(promptRes)) {
                res = promptRes;
            }

            callback.success(res);
            return true;
        }
        return false;
    }


    /**
     * 获取服务器信息(本地可以开启一个http server)
     *
     * @param req 请求对象
     * @return 返回HTTP服务器信息的JSON字符串
     */
    public String getServerInfo(Req req) {
        ServerInfo info = new ServerInfo();
        info.setSend(true);
        info.setGptModel(AthenaContext.ins().getGptModel());
        info.setAiProxyDebug(AthenaContext.ins().isDebugAiProxy());
        info.setLocal(Boolean.valueOf(ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_LOCAL, "false")));
        info.setVision(Boolean.valueOf(ResourceUtils.getAthenaConfig().getOrDefault(Const.VISION, "false")));
        String portStr = ConfigUtils.getConfig().getChatServer();
        int port = 3458;
        if (!StringUtils.isEmpty(portStr)) {
            port = Integer.valueOf(port);
        }
        info.setPort(port);
        SafeRun.run(() -> {
            info.setProjectList(ProjectUtils.listOpenProjects());
            String curProjectName = req.getData().get("projectName");
            if (null != curProjectName) {
                List<String> moduleList = ProjectUtils.listAllModules(ProjectUtils.projectFromManager(curProjectName)).stream().filter(it -> !it.equals(curProjectName)).collect(Collectors.toList());
                info.setModuleList(moduleList);
            }
        });
        return gson.toJson(info);
    }

    /**
     * 生成Java代码并创建类。
     *
     * @param req 请求对象，包含生成代码和类名。
     * @return 返回操作结果。
     */
    public String codeGenerate(Req req) {
        String code = req.getData().get("code");
        String className = req.getData().get("className");
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Editor e = FileEditorManager.getInstance(project).getSelectedTextEditor();
            JavaClassUtils.createClass(project, e, className, code);
        });
        return "ok";
    }


    /**
     * 获取prompt信息
     *
     * @param req 请求对象
     * @return 返回HttpServerInfo对象的json字符串
     */
    public String getPromptInfo(Req req) {
        ServerInfo info = new ServerInfo();
        String portStr = ConfigUtils.getConfig().getChatServer();
        int port = 3458;
        if (!StringUtils.isEmpty(portStr)) {
            port = Integer.valueOf(port);
        }
        info.setPort(port);
        SafeRun.run(() -> {
            info.setProjectList(ProjectUtils.listOpenProjects());
            String curProjectName = req.getData().get("projectName");
            if (null != curProjectName) {
                info.setModuleList(ProjectUtils.listAllModules(ProjectUtils.projectFromManager(curProjectName)));
            }
            Prompt.flush();
            info.setPromptList(Prompt.getPromptMeta());
        });
        return gson.toJson(info);
    }

    /**
     * 处理生成代码请求
     *
     * @param req 请求对象
     * @return 返回字符串"ok"
     */
    public String handlerGenerate(Req req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String model = req.getData().get("model");
            String promptName = req.getData().get("prompt");
            String meta = req.getData().get("meta");
            String type = req.getData().get("type");
            String showDialog = req.getData().get("showDialog");

            Project p = ProjectUtils.project();
            if (null == p) {
                p = this.project;
            }
            String project = p.getName();
            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
            PromptType promptType = Prompt.getPromptType(promptInfo);
            GenerateCodeReq codeReq = GenerateCodeReq.builder().projectName(project).project(p).model(model).promptName(promptName)
                    .meta(meta).promptInfo(promptInfo).promptType(promptType).type(type)
                    .showDialog(showDialog).param(req.getData())
                    .build();
            PromptService.dynamicInvoke(codeReq);
        });
        return "ok";
    }


    /**
     * 获取AI代码提示信息
     *
     * @return AI代码提示信息的JSON字符串
     */
    private String getAiCodePrompt() {
        if (!Prompt.isLoadFinish()) {
            return gson.toJson(AiCodeRes.builder().msg("loading").build());
        }
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                boolean selected = false;
                if (editor != null) {
                    String selectedText = editor.getSelectionModel().getSelectedText();
                    selected = null != selectedText;
                }
                AiCodeRes codeRes = selected ? ConfigCenter.build("selected") : ConfigCenter.build("noselected");
                List<AiCodePromptRes> promptInfoList = Lists.newArrayList();
                promptInfoList.addAll(codeRes.getPromptInfoList());
                AiCodeRes result = new AiCodeRes();
                result.setMsg(codeRes.getMsg());
                result.setPromptInfoList(promptInfoList);
                sb.append(gson.toJson(result));
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return "";
        }
        return sb.toString();
    }


    /**
     * 获取项目信息并以JSON格式返回。
     * 该方法通过调用ApplicationManager的invokeLater方法，异步获取项目名称和模块名称，并将其设置到ProjectModuleInfo对象中。
     * 最终将ProjectModuleInfo对象转换为JSON格式返回。
     *
     * @return 以JSON格式返回的项目信息。
     */
    private String getProjectInfo() {
        ProjectModuleInfo info = new ProjectModuleInfo();
        String projectName = this.project.getName();
        info.setProjectName(projectName);
        return gson.toJson(info);
    }


    @Override
    public void onQueryCanceled(CefBrowser browser, CefFrame frame, long query_id) {
    }


}
