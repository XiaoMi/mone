package run.mone.m78.ip.service;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.mutable.Mutable;
import org.apache.commons.lang.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.RobotContext;
import run.mone.m78.ip.bo.RobotReq;
import run.mone.m78.ip.common.ChromeUtils;
import run.mone.m78.ip.util.*;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.common.GitProjectOpener;
import run.mone.ultraman.service.AthenaCodeService;

import java.io.IOException;
import java.lang.reflect.Method;
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

    }

    public static String getTroubleshootType(RobotContext context, RobotReq req) {
        return "";
    }


    public static String getPromptCmd(String cmd) {
        return "";
    }


    /**
     * 这里的逻辑先调用chatgpt拿到命令列表,然后调用相应的函数
     *
     * @param req
     */
    private static void bot0(GenerateCodeReq req) {

    }

    //执行脚本
    public static Object runScript(Map<String, Object> map, String promptCmd) {
        return null;
    }

    public static Object runScriptMethod(Map<String, Object> map, String methodName) {
        return null;
    }

    private static Map<String, Object> makeBindMap() {
        return ImmutableMap.of(
                "log", log,
                "gson", gson
        );
    }

    private static void setKV(Map<String, Object> map, GenerateCodeReq req) {

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

    }


}
