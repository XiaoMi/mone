package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.RobotContext;
import com.xiaomi.youpin.tesla.ip.bo.RobotReq;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.client.MyHttpClient;
import com.xiaomi.youpin.tesla.ip.common.ChromeUtils;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.mutable.Mutable;
import org.apache.commons.lang.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.common.GitProjectOpener;
import run.mone.ultraman.http.HttpClient;
import run.mone.ultraman.service.AthenaCodeService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/4/19 14:21
 * <p>
 * 所有通过语音过来的请求,都会在这里处理
 */
@Slf4j
public class RobotService {

    private static Gson gson = new Gson();

    public static String invoke(String methodName, RobotContext context, RobotReq req) {
        try {
            Method method = RobotService.class.getMethod(methodName, RobotContext.class, RobotReq.class);
            if (null == method) {
                return null;
            }
            Object obj = method.invoke(null, context, req);
            if (obj instanceof Void || null == obj) {
                return "ok";
            }
            return obj.toString();
        } catch (NoSuchMethodException ex) {
            return "主人你说的话我不懂";
        } catch (Throwable ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }


    public static String openBrowser(RobotContext context, RobotReq req) {
        BrowserUtil.browse(req.getParam(), ProjectUtils.projectFromManager());
        return "ok";
    }

    public static String test(String param) {
        return "res:" + param;
    }


    public static String test(RobotContext context, RobotReq req) {
        return "res:" + req.getParam();
    }

    public static String openClass(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.openJavaClass(ProjectUtils.projectFromManager(), req.getParam()));
        return "ok";
    }

    public static String openProject(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            GitProjectOpener.openGitProject(req.getParam());
        });
        return "ok";
    }

    public static String createPackage(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.createPackage(ProjectUtils.projectFromManager(), req.getParam());
        });
        return "ok";
    }

    public static String generateMoonHandler(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            // todo 具体的业务逻辑用req中用户的定义
            CodeService.generateMoonHandler(ProjectUtils.projectFromManager());
        });
        return "ok";
    }

    public static void message(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Messages.showMessageDialog(ProjectUtils.projectFromManager(), "mone", req.getParam(), Messages.getInformationIcon());
        });
    }

    public static void consoleMessage(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            UltramanConsole.append(req.getParam());
        });
    }

    public static void addMethod(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.addMethod(ProjectUtils.projectFromManager(), req.getParam()));
    }

    public static void addStatement(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.addStatementToMethod(ProjectUtils.projectFromManager(), req.getParam()));
    }

    public static void moveLine(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.moveLine(ProjectUtils.projectFromManager(), Integer.valueOf(req.getParam()));
        });
    }

    //关闭当前编辑器
    public static void closeEditor(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.closeEditor(req.getProject());
        });
    }

    //执行代码
    public static void runClass(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            RunUntil.run(req.getProject());
        });
    }

    //滚动屏幕
    public static void scroll(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            EditorUtils.scroll(req.getProject());
        });
    }

    public static void getClassByAnno(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.getClassByServiceAnno(ProjectUtils.projectFromManager(), req.getParam());
        });
    }

    public static void listRecentProjects(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ProjectUtils.recentProjects();
        });
    }

    public static void listAllModules(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ProjectUtils.listAllModules(ProjectUtils.projectFromManager());
        });
    }

    public static void getPackageClass(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.getClassesInPackage(ProjectUtils.projectFromManager(), req.getParam());
        });
    }

    public static void addComment(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CodeService.getClassesInPackage(ProjectUtils.projectFromManager(), req.getParam());
        });
    }

    public static void removeComments(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.removeComments(ProjectUtils.projectFromManager()));
    }

    public static void generateMethod(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.generateMethod(ProjectUtils.projectFromManager(), req.getParam()));
    }

    public static void go(RobotContext context, RobotReq req) {
        // 为了适配语音输入的路由
    }

    public static List<String> projects(RobotContext context, RobotReq req) {
        return ProjectUtils.listOpenProjects();
    }

    public static List<String> modules(RobotContext context, RobotReq req) {
        return ProjectUtils.listAllModules(ProjectUtils.projectFromManager(req.getParam()));
    }

    public static String say(RobotContext context, RobotReq req) {
        return req.getParam();
    }


    public static Object formatCode(RobotContext context, RobotReq req) {
        Mutable obj = new MutableObject();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            CodeService.formatCode(req.getProject());
            obj.setValue("success");
        });
        return obj.getValue();
    }

    public static void deleteLine(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> CodeService.deleteLine(ProjectUtils.projectFromManager(), Integer.valueOf(req.getParam())));
    }


    public static void terminal(RobotContext context, RobotReq req) {
        log.info("terminal:{}", req.getParam());
        ApplicationManager.getApplication().invokeLater(() -> TerminalUtils.send(req.getProject(), req.getParam()));
    }

    //同声翻译
    public static void translate(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", req.getParam());
                    String res = AthenaCodeService.callProxy(req.getProject(), map, "fanyi", 10).value();
                    System.out.println(res);
                    ChromeUtils.call(req.getProject().getName(), res, 0);
                }
        );
    }

    //官网检索
    public static void web123(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", req.getParam());
                    String res = AthenaCodeService.callProxy(req.getProject(), map, "web123", 10).value();
                    System.out.println(res);
                    ChromeUtils.call(req.getProject().getName(), res, 0);
                }
        );
    }


    //打开某个指定的app
    public static void openApp(RobotContext context, RobotReq req) {
        try {
            Runtime.getRuntime().exec("/usr/bin/open -a " + req.getParam());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //错误分析
    public static void troubleshoot(RobotContext context, RobotReq req) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String promptName = "other";
            String troubleshootType = getTroubleshootType(context, req);
            if (troubleshootType.contains("dubbo")) {
                promptName = "dubbo_troubleshoot";
            } /*else if (troubleshootType.contains("nacos")){
                promptName = "nacos_troubleshoot";
            }*/ else {
                promptName = "troubleshoot";
            }

            Map<String, String> map = new HashMap<>();
            map.put("errorMsg", req.getParam());
            String res = AthenaCodeService.callProxy(req.getProject(), map, promptName, 10).value();
            System.out.println(promptName + ":" + res);
            ChromeUtils.call(req.getProject().getName(), res, 0);
        });
    }

    public static String getTroubleshootType(RobotContext context, RobotReq req) {
        Map<String, String> map = new HashMap<>();
        map.put("errorMsg", req.getParam());
        return AthenaCodeService.callProxy(req.getProject(), map, "type_troubleshoot", 10).value();
    }


    public static String getPromptCmd(String cmd) {
        return CodeService.call("bot", ImmutableMap.of("code", cmd)).trim();
    }


    /**
     * 这里的逻辑先调用chatgpt拿到命令列表,然后调用相应的函数
     *
     * @param req
     */
    private static void bot0(GenerateCodeReq req) {
        try {
            String res = CodeService.call(req.getPromptName(), req.getParam()).trim();
            UltramanConsole.append(req.getProject(), "bot:\n" + res + "\n");

            if (res.equals("scroll")) {
                RobotService.scroll(null, RobotReq.builder().project(req.getProject()).build());
            }

            if (res.startsWith("translate")) {
                RobotService.translate(null, RobotReq.builder().project(req.getProject()).param(res.substring(10)).build());
            }

            if (res.startsWith("web123")) {
                RobotService.web123(null, RobotReq.builder().project(req.getProject()).param(res.substring(7)).build());
            }

            if (res.equals("generate interface")) {
                dynamicCallPrompt(req, "generateInterface");
            }

            //执行脚本(所有代码存储在z平台)
            if (res.startsWith("run script")) {
                Map<String, Object> map = Maps.newHashMap();
                setKV(map, req);
                Object r = runScript(map, res);
                if (null != r) {
                    if (r instanceof Throwable t) {
                        ChromeUtils.call(req.getProject().getName(), "error:" + t.getMessage(), "", 0);
                    } else if (r instanceof AiChatMessage<?> am) {
                        ChromeUtils.call(req.getProject().getName(), am);
                    } else {
                        ChromeUtils.call(req.getProject().getName(), r.toString(), "", 0);
                    }
                }
                return;
            }


            if (res.equals("unsupport")) {
                ChromeUtils.call(req.getProject().getName(), "这个指令不支持", "", 0);
            } else {
                if (req.getParam().get("code").equals("帮助")) {
                    ChromeUtils.call(req.getProject().getName(), res, "", 0);
                } else {
                    ChromeUtils.call(req.getProject().getName(), "指令:" + res + "执行成功", "", 0);
                }
            }
        } catch (Throwable ex) {
            String message = ex.getMessage();
            ChromeUtils.call(req.getProject().getName(), "调用指令的时候发生错误:" + message, "", 0);
            log.error(ex.getMessage(), ex);
        }
    }

    //执行脚本
    public static Object runScript(Map<String, Object> map, String promptCmd) {
        String[] ss = promptCmd.split("\\$\\$");
        Arrays.stream(ss).forEach(it -> {
            String[] kv = it.split("#");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        });
        if (ss.length == 1) {
            map.put("methodName", ss[0]);
        }
        log.info("map:{}", map);
        String methodName = map.get("methodName").toString();
        String httpRes = HttpClient.get(AthenaContext.ins().getZAddr() + "/api/z/open/function/query?name=" + methodName);
        JsonObject jsonObject = new Gson().fromJson(httpRes, JsonObject.class);
        String functionCode = jsonObject.getAsJsonObject("data").get("scriptContent").getAsString();
        Object r = ScriptService.ins().invoke(functionCode, "call", makeBindMap(), map);
        return r;
    }

    public static Object runScriptMethod(Map<String, Object> map, String methodName) {
        String httpRes = HttpClient.get(AthenaContext.ins().getZAddr() + "/api/z/open/function/query?name=" + methodName);
        JsonObject jsonObject = new Gson().fromJson(httpRes, JsonObject.class);
        String functionCode = jsonObject.getAsJsonObject("data").get("scriptContent").getAsString();
        Object r = ScriptService.ins().invoke(functionCode, "call", makeBindMap(), map);
        return r;
    }

    private static Map<String, Object> makeBindMap() {
        return ImmutableMap.of(
                "log", log,
                "gson", gson,
                "zAddr", Prompt.getzAddr(),
                "ztoken", ConfigUtils.getConfig().getzToken(),
                "okHttpClient", MyHttpClient.getInstance()
        );
    }

    private static void setKV(Map<String, Object> map, GenerateCodeReq req) {
        map.put("classCode", req.getClassCode());
        map.put("methodCode", req.getMethodCode());
        map.put("fileText", req.getVirtualFileText());
        map.put("project", req.getProject());
        map.put("req", req);
        map.put("editor", req.getEditor());
        map.put("zToken", ConfigUtils.getConfig().getzToken());
    }


    //支持机器人命令
    public static void bot(GenerateCodeReq req) {
        AthenaTask.start(new Task.Backgroundable(req.getProject(), "bot run", true) {
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                bot0(req);
            }
        });

    }

    public static void dynamicCallPrompt(GenerateCodeReq req, String promptName) {
        ApplicationManager.getApplication().invokeLater(() -> {
            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
            PromptType promptType = Prompt.getPromptType(promptInfo);
            PromptService.dynamicInvoke(GenerateCodeReq.builder()
                    .project(req.getProject())
                    .promptName(promptInfo.getPromptName())
                    .promptInfo(promptInfo)
                    .promptType(promptType)
                    .build());
        });
    }


}
