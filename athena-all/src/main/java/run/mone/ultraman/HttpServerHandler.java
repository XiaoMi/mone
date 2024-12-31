package run.mone.ultraman;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.RobotContext;
import com.xiaomi.youpin.tesla.ip.bo.RobotReq;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.JavaClassUtils;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.RobotService;
import com.xiaomi.youpin.tesla.ip.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.mutable.MutableObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import run.mone.openai.OpenaiCall;
import run.mone.ultraman.bo.AiReq;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.common.ActionEventUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.PythonExecutor;
import run.mone.ultraman.common.SafeRun;
import run.mone.ultraman.http.HttpClient;
import run.mone.ultraman.http.HttpResponseUtils;
import run.mone.ultraman.http.Param;
import run.mone.ultraman.http.handler.ReadCodeHandler;
import run.mone.ultraman.http.handler.WriteCodeHandler;
import run.mone.ultraman.service.AgentService;
import run.mone.ultraman.service.AthenaCodeService;
import run.mone.ultraman.state.PromptAndFunctionProcessor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Gson gson = new Gson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String uri = req.uri();

        // 使用java.net.URI解析URI字符串
        URI parsedUri = new URI(uri);

        // 获取路径部分
        String path = parsedUri.getPath();

        if (uri.startsWith("/favicon.ico")) {
            ctx.writeAndFlush(HttpResponseUtils.createResponse(HttpResponseStatus.NOT_FOUND, "404")).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        RobotContext context = new RobotContext();

        Param p = new Param();
        p.decode(req);
        String data = "ok";
        String message = "success";

        JsonObject resObj = new JsonObject();
        resObj.addProperty("code", 0);
        if (path.equals("/tianye")) {
            ByteBuf jsonBuf = req.content();
            String reqString = jsonBuf.toString(CharsetUtil.UTF_8);
            log.info("call tianye req:{}", reqString);
            JsonElement element = JsonParser.parseString(reqString);
            JsonObject obj = (JsonObject) element;
            if (element.isJsonObject()) {
                String cmd = obj.get("cmd").getAsString();
                switch (cmd) {
                    case "close_all_tab": {
                        log.info("close_all_tab");
                        CodeService.invokeLater(() -> AthenaContext.ins().getProjectMap().keySet().forEach(projectName -> AgentService.closeAllOpenFilesInProject(AthenaContext.ins().getProjectMap().get(projectName))));
                        break;
                    }
                    //创建代码或者修改代码
                    case "write_code": {
                        resObj = new WriteCodeHandler().execute(obj, resObj);
                        break;
                    }
                    //创建测试类
                    case "generate_test_class": {
                        String projectName = obj.get("projectName").getAsString();
                        String moduleName = obj.get("moduleName").getAsString();
                        String promptName = getPromptNameFromJsonObject(obj);
                        Project project = AthenaContext.ins().getProjectMap().get(projectName);
                        //如果指定package,则这个package下的所有类都会创建测试类
                        String packageName = getPackageNameFromJsonObject(obj);
                        List<String> list = getClassListFromPackage(packageName, project, obj);
                        list.stream().forEach(className -> {
                            log.info("generate_test_class class name:{}", className);
                            String code = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                                PsiClass psiClass = PsiClassUtils.findClassByName(project, className);
                                PsiElement javaFile = psiClass.getParent();
                                return javaFile.getText();
                            });
                            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
                            JsonObject jsonRes = PromptAndFunctionProcessor.callPrompt(project, promptInfo, ImmutableMap.of("code", code));
                            String testClassCode = extractTestClassCodeFromJsonResponse(jsonRes);
                            AthenaClassInfo info = AthenaCodeService.classInfo(testClassCode);
                            ApplicationManager.getApplication().invokeLater(() -> {
                                PsiClass clazz = PsiClassUtils.findClassByName(project, info.getPackagePath() + "." + info.getName());
                                if (null == clazz) {
                                    //没有package则直接创建
                                    WriteCommandAction.runWriteCommandAction(project, () -> {
                                        PsiDirectory directory = PsiClassUtils.getTestSourceDirectory(project, moduleName);
                                        directory = PsiClassUtils.createPackageDirectories(directory, info.getPackagePath());
                                        PsiClassUtils.createClass(project, info.getName(), testClassCode, false, directory);
                                        log.info("create test class:{} success", info.getName());
                                    });
                                } else {
                                    log.info("class {} already exists", info.getName());
                                }
                            });
                        });
                        break;
                    }
                    //快速修复代码
                    case "fix_code": {
                        processJsonObjectAndFixCode(obj, resObj);
                        break;
                    }
                    //执行代码
                    case "run_code": {
                        ApplicationManager.getApplication().invokeLater(() -> ActionEventUtils.executeDefaultRunAction());
                    }
                    //打开代码
                    case "open_code": {
                        String projectName = obj.get("projectName").getAsString();
                        String className = obj.get("className").getAsString();
                        ApplicationManager.getApplication().invokeLater(() -> {
                            JavaClassUtils.openClass(ProjectUtils.projectFromManager(projectName), className);
                        });
                    }
                    //列出project
                    case "list_project": {
                        listProject(ctx, resObj);
                        return;
                    }
                    //列出module
                    case "list_module": {
                        listModule(ctx, obj, resObj);
                        return;
                    }
                    //执行java程序
                    case "execute_java": {
                        executeJava(obj);
                        break;
                    }
                    //读取code
                    case "read_code": {
                        resObj = new ReadCodeHandler().execute(obj, resObj);
                        break;
                    }
                    //创建包路径
                    case "create_package": {
                        createPackage(obj);
                        break;
                    }
                    case "auto_app": {
                        // 调用jcommen genreateCodeBase
                        ProjectUtils projectUtils = new ProjectUtils();
                        Project project = ProjectUtils.projectFromManager();
                        String projectName = obj.get("projectName").getAsString();
                        String groupId = obj.get("groupId").getAsString();
                        String author = obj.get("author").getAsString();
                        Optional<String> autoAppPath = projectUtils.generateProjectBase(project, projectName, "run.mone", groupId, author);
                        // openProject
                        autoAppPath.ifPresent(string -> projectUtils.openProject(project, string));
                        break;
                    }
                }
            }
            sendRes(ctx, resObj);
            return;
        }

        //mp3
        if (uri.contains("audio")) {
            ByteBuf jsonBuf = req.content();
            String base64Data = jsonBuf.toString(CharsetUtil.UTF_8);
            byte[] bytes = Base64.getDecoder().decode(base64Data);
            AudioInputStream a = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            Clip c = AudioSystem.getClip();
            c.open(a);
            c.start();
            sendRes(ctx, getObj("ok", "success"));
            return;
        }
        //这里会调用python代码(然后python最后会代用到ai)
        if (uri.contains("go")) {
            RobotService.consoleMessage(context, RobotReq.builder().param("call go v:" + p.getV()).build());
            String scriptPath = ConfigUtils.getConfig().getMvnPath();
            PythonExecutor.run(scriptPath + "run.sh", p.getV());
        }
        //解析出来实际要执行的命令(curl http://127.0.0.1:3458/ai?cmd=在终端执行命令ls -la)
        if (uri.contains("ai")) {
            RobotService.consoleMessage(context, RobotReq.builder().param("call ai cmd:" + p.getCmd()).build());
            ByteBuf jsonBuf = req.content();
            String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
            AiReq aiReq = gson.fromJson(jsonStr, AiReq.class);

            String cmd = aiReq.getCmd();
            p.setCmd(cmd);

            if (aiReq.getAi().equals("chatglm")) {
                String res = HttpClient.callChatgml(aiReq.getCmd());
                RobotService.consoleMessage(context, RobotReq.builder().param("call glm res:" + res).build());
                String scriptPath = ConfigUtils.getConfig().getMvnPath();
                PythonExecutor.run(scriptPath + "play_run.sh", res);
                sendRes(ctx, getObj(res, "success"));
                return;
            }

            if (aiReq.getAi().equals("chatgpt")) {
                String key = ConfigUtils.getConfig().getChatgptKey();
                String proxy = ConfigUtils.getConfig().getChatgptProxy();
                String res = OpenaiCall.call(key, proxy, "%s", aiReq.getCmd());
                RobotService.consoleMessage(context, RobotReq.builder().param("call gpt res:" + res).build());
                String scriptPath = ConfigUtils.getConfig().getMvnPath();
                PythonExecutor.run(scriptPath + "play_run.sh", res);
                sendRes(ctx, getObj(res, "success"));
                return;
            }

            //不执行ai,直接自己解析,方便测试
            else if (cmd.startsWith("run:")) {
                RobotService.consoleMessage(context, RobotReq.builder().param("debug pattern").build());
                String[] cmds = cmd.split(":");
                p.setPath(cmds[1]);
                if (cmds.length >= 3) {
                    p.setParam(cmds[2]);
                } else {
                    p.setParam("");
                }
            } else {
//                Pair<Integer, String> res = ChatGptService.callOpenai(Prompt.get("cmd"), cmd);
                Pair<Integer, String> res = null;
                if (res.getKey() != 0) {
                    RobotService.consoleMessage(context, RobotReq.builder().param(res.getValue()).build());
                    FullHttpResponse response = HttpResponseUtils.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, res.getValue());
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                RobotService.consoleMessage(context, RobotReq.builder().param("call ai cmd:" + p.getCmd() + " res:" + res).build());
                message = res.getValue();
                Pair<String, String> pair = p.getPathAndParam(res.getValue());
                p.setPath(pair.getKey());
                p.setParam(pair.getValue());
            }
        }
        data = RobotService.invoke(p.getPath(), context, RobotReq.builder().param(p.getParam()).build());
        RobotService.consoleMessage(context, RobotReq.builder().param("call robot res:" + data + " param:" + p.getParam() + " method:" + p.getPath()).build());
        MutableObject mo = new MutableObject(data);
        SafeRun.run(() -> {
            if (null != mo.getValue() && mo.getValue().toString().length() > 0) {
                RobotService.consoleMessage(context, RobotReq.builder().param("say res:" + mo.getValue()).build());
                String scriptPath = ConfigUtils.getConfig().getMvnPath();
                PythonExecutor.run(scriptPath + "play_run.sh", mo.getValue().toString());
            }
        });
        JsonObject obj = getObj(data, message);
        // 创建http响应
        sendRes(ctx, obj);
    }


    //执行java代码(比如创建sql 比如创建orm先关的类),有些代码最好是生成的
    private static void executeJava(JsonObject obj) {
        log.info("execute java obj:{}", obj);
        String className = obj.get("className").getAsString();
        String type = obj.get("type").getAsString();
        JsonObject jsonObj = new JsonObject();
        if (type.equals("create_table")) {
            String sql = obj.get("sql").getAsString();
            jsonObj.addProperty("sql", sql);
        } else if (type.equals("create_code")) {
            if (obj.has("tableName")) {
                jsonObj.addProperty("tableName", obj.get("tableName").getAsString());
            }
            if (obj.has("pojoName")) {
                jsonObj.addProperty("pojoName", obj.get("pojoName").getAsString());
            }
        }
        jsonObj.addProperty("type", type);
        String param = new String(Base64.getEncoder().encode(GsonUtils.gson.toJson(jsonObj).getBytes()));
        ActionEventUtils.setupAndExecuteJavaAppConfiguration(className, param, ProjectUtils.projectFromManager(obj.get("projectName").getAsString()));
    }

    private void listProject(ChannelHandlerContext ctx, JsonObject resObj) {
        resObj.add("data", gson.toJsonTree(ProjectUtils.listOpenProjects()));
        sendRes(ctx, resObj);
    }

    private void listModule(ChannelHandlerContext ctx, JsonObject obj, JsonObject resObj) {
        String projectName = obj.get("projectName").getAsString();
        resObj.add("data", gson.toJsonTree(ProjectUtils.listAllModules(ProjectUtils.projectFromManager(projectName))));
        sendRes(ctx, resObj);
    }

    private static void createPackage(JsonObject obj) {
        String projectName = obj.get("projectName").getAsString();
        String moduleName = obj.get("moduleName").getAsString();
        String packageName = obj.get("packageName").getAsString();
        CodeService.createPackageInModule(ProjectUtils.getModuleWithName(ProjectUtils.projectFromManager(projectName), moduleName), packageName);
    }

    private static void processJsonObjectAndFixCode(JsonObject obj, JsonObject resObj) {
        boolean blockOnFailure = false;
        try {
            // 找到已生成的pojo or service or controller
            String projectName = obj.get("projectName").getAsString();
//                            String className = obj.get("className").getAsString();
            JsonArray classList = obj.get("classList").getAsJsonArray();
            String packageName = obj.get("packageName").getAsString();
            JsonElement blockJson = obj.get("blockOnFailure");
            if (blockJson != null && "true".equals(blockJson.getAsString())) {
                blockOnFailure = true;
            }
            if (StringUtils.isBlank(projectName)) {
                return;
            }
            for (int i = 0; i < classList.size(); i++) {
                String clsName = getFQCN(packageName, classList.get(i).getAsString());
                String code = getCode(projectName, clsName);
                // 尝试quick fix
                // TODO: not working for now
                // ApplicationManager.getApplication().invokeLater(() -> CodeService.tryQuickFix(code, obj, projectName, className));
                // 尝试基于parse的fix
                ApplicationManager.getApplication().invokeLater(() -> CodeService.tryParserBasedFix(code, projectName, clsName));
            }
            resObj.addProperty("success", "true");
            return;
        } catch (Exception e) {
            log.error("Error while try to fix code, nested exception is:", e);
        }
        // HINT: whether to block on failure
        if (!blockOnFailure) {
            resObj.addProperty("success", "true");
        }
    }

    @NotNull
    private static List<String> getClassListFromPackage(String packageName, Project project, JsonObject obj) {
        List<String> list = null;
        if (StringUtils.isNotEmpty(packageName)) {
            list = ApplicationManager.getApplication().runReadAction((Computable<List<String>>) () -> PackageUtils.getClassList(project, packageName));
        }
        if (null == list) {
            String className = obj.get("className").getAsString();
            list = Lists.newArrayList(className);
        }
        return list;
    }

    private static String getPromptNameFromJsonObject(JsonObject obj) {
        String promptName = "test_class_code";
        if (obj.has("promptName")) {
            promptName = obj.get("promptName").getAsString();
        }
        return promptName;
    }

    private static String getPackageNameFromJsonObject(JsonObject obj) {
        String packageName = "";
        if (obj.has("packageName")) {
            packageName = obj.get("packageName").getAsString();
        }
        return packageName;
    }

    private static String extractTestClassCodeFromJsonResponse(JsonObject jsonRes) {
        String testClassCode = jsonRes.get("data").getAsString();
        testClassCode = MarkDownUtils.extractCodeBlock(testClassCode);
        return testClassCode;
    }

    private static String getCode(String projectName, String className) {
        try {
            return ReadAction.compute(() -> {
                // 确保我们在正确的上下文中
                if (!ApplicationManager.getApplication().isReadAccessAllowed()) {
                    throw new IllegalStateException("Read access is not allowed");
                }
                PsiClass psiClass = PsiClassUtils.findClassByName(ProjectUtils.projectFromManager(projectName), className);
                return psiClass.getText();
            });
        } catch (Throwable e) {
            log.error("failed to get code for class: {}, in project:{}", className, projectName);
            return "";
        }
    }

    private static String getFQCN(String packageName, String className) {
        return StringUtils.joinWith(".", packageName, className);
    }

    private void sendRes(ChannelHandlerContext ctx, JsonObject obj) {
        FullHttpResponse response = HttpResponseUtils.createResponse(HttpResponseStatus.OK, gson.toJson(obj));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @NotNull
    private static JsonObject getObj(String data, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("code", 0);
        obj.addProperty("message", message);
        obj.addProperty("data", data);
        return obj;
    }


}