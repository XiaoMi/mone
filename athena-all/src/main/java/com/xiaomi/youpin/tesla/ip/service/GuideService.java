package com.xiaomi.youpin.tesla.ip.service;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.bo.z.EmbeddingStatus;
import com.xiaomi.youpin.tesla.ip.common.ConfigCenter;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.listener.Req;
import com.xiaomi.youpin.tesla.ip.ui.VersionUi;
import com.xiaomi.youpin.tesla.ip.util.*;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.CodeReq;
import run.mone.ultraman.bo.Version;
import run.mone.ultraman.common.ActionEventUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.TestRunnerUtils;
import run.mone.ultraman.http.HttpClient;
import run.mone.ultraman.service.AthenaCodeService;
import run.mone.ultraman.service.ModuleService;
import run.mone.ultraman.state.ProjectFsmManager;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/6/24 14:02
 */
public class GuideService {

    private static Gson gson = GsonUtils.gson;

    private static ExecutorService pool = Executors.newSingleThreadExecutor();

    /**
     * AI引导方法，根据请求参数中的问题，获取相应的提示信息列表
     *
     * @param req 请求参数对象，包含问题信息
     * @return 返回提示信息列表的JSON字符串
     */
    public static String aiGuide(Req req, Project project) {
        AiCodeRes res = new AiCodeRes();
        String question = req.getData().get("question");
        if (question.equals("flush")) {
            Prompt.flush();
            res.setMsg("flush success");
            return gson.toJson(res);
        }

        if (question.startsWith("invoke_code:")) {
            List<String> list = Splitter.on(":").splitToList(question);
            if (list.size() == 4) {
                String codeName = list.get(1);
                String method = list.get(2);
                String param = list.get(3);
                Object codeRes = ScriptService.ins().invoke(ScriptService.getScript(codeName), method, Maps.newHashMap(), param);
                res.setMsg("invoke code success:" + codeRes);
            } else {
                res.setMsg(list + " list size != 4");
            }
            return gson.toJson(res);
        }

        //添加一个功能(entity mapper service controller),调用的事flow
        if (question.startsWith("feature:")) {
            callFlow(question, "256", project.getName(), new JsonObject());
            return gson.toJson(res);
        }

        //添加一个功能(entity mapper service controller),调用的事flow (主要通过调用本地模板)
        if (question.startsWith("feature2:")) {
            String className = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
                PsiClass psiClass = CodeService.getPsiClass(project);
                return psiClass.getQualifiedName();
            });
            JsonObject object = new JsonObject();
            object.addProperty("codeGenClassName", className);
            callFlow(question, "30016", project.getName(), object);
            return gson.toJson(res);
        }

        //生成单元测试类
        if (question.startsWith("cjunit")) {
            ApplicationManager.getApplication().invokeLater(() -> {
                List<ClassInfo> list = AnnoUtils.findClassWithAnno(project, "com.xiaomi.youpin.docean.anno.Service", null);
                System.out.println(list);
                list.stream().limit(3).forEach(ci -> {
                    String className = "run.mone.test.service." + ci.getSimpleName() + "Test";
                    PsiClass psiClass = PsiClassUtils.findClassByName(project, className);
                    if (null != psiClass) {
                        return;
                    }
                    //创建这个PsiClass
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("testName", ci.getSimpleName() + "Test");
                    jsonObject.addProperty("pojoName", ci.getSimpleName().replace("Service", ""));
                    String param = new String(Base64.getEncoder().encode(GsonUtils.gson.toJson(jsonObject).getBytes()));
                    ActionEventUtils.setupAndExecuteJavaAppConfiguration("run.mone.test.service.CodeGen", param, ProjectUtils.projectFromManager(project.getName()));
                });
            });
            return gson.toJson(res);
        }

        //直接生成单元测试(所有service直接生成单元测试)
        if (question.startsWith("junit")) {
            ApplicationManager.getApplication().invokeLater(() -> {
                //查找到所有Service类
                List<ClassInfo> list = AnnoUtils.findClassWithAnno(project, "com.xiaomi.youpin.docean.anno.Service", null);
                list.stream().forEach(ci -> {
                    String testClassName = "run.mone.test.service." + ci.getSimpleName() + "Test";
                    //查看这个测试类是否存在,如果存在则直接跳过
                    PsiClass psiClass = PsiClassUtils.findClassByName(project, testClassName);
                    if (null != psiClass) {
                        //获取单元测试的
                        List<String> methods = getNonConstructorMethodTexts(psiClass);
                        if (!methods.isEmpty()) {
                            return;
                        }

                        //获取需要测试的那个类
                        PsiClass pc = PsiClassUtils.findClassByName(project, ci.getClassName());
                        if (null == pc) {
                            return;
                        }

                        //获取实际要测试的类的方法集合
                        @NotNull List<String> mList = getNonConstructorMethodTexts(pc);
                        if (mList.isEmpty()) {
                            return;
                        }

                        //保障单线程执行,一起操作ide,和一起操作ws都是有问题的
                        pool.submit(() -> {
                            Mutable<Pair<String, Editor>> mutable = new MutableObject<>();
                            ApplicationManager.getApplication().invokeAndWait(()->{
                                String testClassCode = PromptUtils.openUnitTestClass(project, testClassName);
                                Editor editor = CodeService.getEditor(project);
                                mutable.setValue(Pair.of(testClassCode, editor));
                            });

                            String testClassCode = mutable.getValue().getKey();
                            Editor editor = mutable.getValue().getValue();

                            mList.forEach(mText -> {
                                CountDownLatch latch = new CountDownLatch(1);
                                PromptUtils.generateUnitTest(project, mText, testClassName, new HashMap<>(), testClassCode, editor, latch);
                                try {
                                    latch.await(20, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                //插入换行
                                CodeService.writeCode2(project, editor, "\n\n");
                            });

                        });

                    }

                });


            });
            return gson.toJson(res);
        }

        //bug fix,调用的事flow
        if (question.startsWith("fix")) {
            JsonObject jo = new JsonObject();
            JsonObject input = new JsonObject();
            ApplicationManager.getApplication().invokeLater(() -> {
                PsiMethod psiMethod = CodeService.getMethod(project);
                TestRunnerUtils.runTest(project, psiMethod.getName(), pair -> {
                    //用来存储错误信息
                    input.addProperty("input", pair.getValue());
                    input.addProperty("host", AthenaContext.ins().getLocalAddress());
                    input.addProperty("port", AthenaContext.ins().getLocalPort());
                    input.addProperty("projectName", project.getName());
                    jo.add("input", input);

                    jo.addProperty("userName", AthenaContext.ins().getUserName());
                    jo.addProperty("flowId", "60014");
                    new Thread(() -> HttpClient.post("https://xxx", jo.toString())).start();
                });
            });
            return gson.toJson(res);
        }

        if (question.startsWith("test")) {
            ApplicationManager.getApplication().invokeLater(() -> PsiMethodUtils.extractMethodCallParts(project, CodeService.getEditor(project)));
            return gson.toJson(res);
        }

        if (question.startsWith("model")) {
            List<String> list = Splitter.on(":").splitToList(question);
            if (list.size() == 2) {
                AthenaContext.ins().setGptModel(list.get(1));
                res.setMsg("set model success model:" + list.get(1));
            }
            return gson.toJson(res);
        }

        if (question.startsWith("aidebug")) {
            List<String> list = Splitter.on(":").splitToList(question);
            if (list.size() == 2) {
                AthenaContext.ins().setDebugAiProxy(Boolean.valueOf(list.get(1)));
                res.setMsg("ai debug:" + list.get(1));
            }
            return gson.toJson(res);
        }

        if (question.equals("refreshconfig")) {
            ResourceUtils.getAthenaConfig(project, true);
            res.setMsg("refresh config success");
            return gson.toJson(res);
        }

        //获取作者信息
        if (question.equals("author")) {
            return author(res);
        }

        if (question.startsWith("troubleshoot")) {
            RobotService.troubleshoot(null, RobotReq.builder().project(project).param(question).build());
            return "";
        }

        //获取embedding 信息
        if (question.equals("embedding_status")) {
            return embeddingStatus(req, res, project);
        }

        //打开配置
        if (question.equals("config")) {
            ApplicationManager.getApplication().invokeLater(() -> LabelUtils.showLabelConfigUi(project));
            res.setMsg("show config success");
            return gson.toJson(res);
        }

        if (question.equals("collected")) {
            res.setMsg(" ");
            res.setPromptInfoList(buildAiCodePromptRes(Prompt.getCollected()));
            return gson.toJson(res);
        }

        if (question.equals("debug")) {
            res.setMsg(new Version().toString());
            return gson.toJson(res);
        }

        //刷新业务代码到知识库
        if (question.equals("flush_biz")) {
            return flushBizCode(req, res, project);
        }

        if (question.equals("class_len")) {
            return getClassLen(res, project);
        }

        //停止状态机
        if (question.equals("stop")) {
            ProjectFsmManager.stop(project.getName());
            res.setMsg("退出状态机");
            return gson.toJson(res);
        }

        //git 代码push
        if (question.equals("push")) {
            PromptInfo promptInfo = Prompt.getPromptInfo("bot_call");
            ApplicationManager.getApplication().invokeLater(() -> PromptService.dynamicInvoke(GenerateCodeReq.builder().promptInfo(promptInfo).project(project).promptType(PromptType.executeBot).build()));
            res.setMsg("push");
            return gson.toJson(res);
        }

        //代码review
        if (question.equals("review")) {
            PromptInfo promptInfo = Prompt.getPromptInfo("bot_stream");
            ApplicationManager.getApplication().invokeLater(() -> PromptService.dynamicInvoke(GenerateCodeReq.builder().promptInfo(promptInfo).project(project).promptType(PromptType.executeBot).build()));
            res.setMsg("review");
            return gson.toJson(res);
        }

        //查询状态机状态
        if (question.equals("state")) {
            String state = ProjectFsmManager.state(project.getName());
            res.setMsg("state:" + state);
            return gson.toJson(res);
        }

        //测试prompt
        if (question.startsWith("prompt:")) {
            String promptName = question.split(":")[1];
            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
            PromptType promptType = Prompt.getPromptType(promptInfo);
            ApplicationManager.getApplication().invokeLater(() -> {
                PromptService.testPrompt(GenerateCodeReq.builder().project(project)
                        .promptName(promptName).promptInfo(promptInfo).promptType(promptType).build());
            });
            res.setMsg("ok");
            return gson.toJson(res);
        }

        AiCodeRes cache = ConfigCenter.build(question);
        if (null == cache) {
            res.setMsg(Message.unsupportedCommand);
            return gson.toJson(res);
        }
        res.setMsg(cache.getMsg());
        if (!"help".equals(question)) {
            List<AiCodePromptRes> promptRes = Lists.newArrayList();
            List<PromptInfo> promptList = Prompt.getPromptInfoByTag(cache.getTagName());
            promptRes.addAll(cache.getPromptInfoList());
            promptRes.addAll(buildAiCodePromptRes(promptList));
            res.setPromptInfoList(promptRes);
        } else {
            res.setPromptInfoList(cache.getPromptInfoList());
        }
        return gson.toJson(res);
    }

    @NotNull
    private static List<String> getNonConstructorMethodTexts(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).filter(it2 -> {
            if (it2 instanceof PsiMethodImpl && !it2.isConstructor()) {
                return true;
            }
            return false;
        }).map(PsiElement::getText).collect(Collectors.toList());
    }

    private static void callFlow(String question, String flowId, String projectName, JsonObject jsonObject) {
        List<String> list = Splitter.on(":").splitToList(question);
        JsonObject jo = new JsonObject();
        JsonObject input = new JsonObject();
        input.addProperty("input", list.get(1));
        input.addProperty("host", AthenaContext.ins().getLocalAddress());
        input.addProperty("port", AthenaContext.ins().getLocalPort());
        input.addProperty("projectName", projectName);

        jsonObject.keySet().forEach(key -> input.addProperty(key, jsonObject.get(key).getAsString()));

        jo.add("input", input);
        jo.addProperty("userName", AthenaContext.ins().getUserName());
        jo.addProperty("flowId", flowId);
        //异步化调用
        new Thread(() -> HttpClient.post("https://xx", jo.toString())).start();
    }


    //获取当前打开类的长度
    private static String getClassLen(AiCodeRes res, Project project) {
        MutableInt num = new MutableInt();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            String text = CodeService.getClassText(project);
            num.setValue(text.length());
        });
        res.setMsg(String.valueOf(num.getValue()));
        return gson.toJson(res);
    }

    //获取作者信息
    private static String author(AiCodeRes res) {
        ApplicationManager.getApplication().invokeLater(() -> {
            VersionUi versionUi = new VersionUi();
            versionUi.show();
        });
        res.setMsg(new Version().toString());
        return gson.toJson(res);
    }

    private static void refreshProject(String targetExtractionDir) {
        VirtualFile targetFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(targetExtractionDir));
        RefreshQueue.getInstance().refresh(false, true, null, targetFile);
    }

    private static List<AiCodePromptRes> buildAiCodePromptRes(List<PromptInfo> promptInfoList) {
        List<AiCodePromptRes> list = promptInfoList.stream().filter(p -> p.getTags().stream().anyMatch(tag -> tag.getName().equals("plugin"))).map(promptInfo -> {
            AiCodePromptRes promptRes = new AiCodePromptRes();
            BeanUtil.copyProperties(promptInfo, promptRes);
            promptRes.setType("cmd");
            promptRes.setShowDialog(promptInfo.getLabels().getOrDefault("showDialog", "false"));
            return promptRes;
        }).collect(Collectors.toList());
        return list;
    }

    //刷新业务代码到知识库
    private static String flushBizCode(Req req, AiCodeRes res, Project project) {
        String moduleName = req.getData().get("module");
        String scope = req.getData().get("scope");
        ApplicationManager.getApplication().invokeLater(() -> {
            if ("project".equals(scope)) {
                //Refresh all modules.
                List<String> list = ProjectUtils.listAllModules(project).stream().filter(it -> !it.equals(req.getData().get("project"))).collect(Collectors.toList());
                list.stream().forEach(it -> ModuleService.uploadModelText(project, getModule(it, project)));
            } else {
                ModuleService.uploadModelText(project, getModule(moduleName, project));
            }
        });
        res.setMsg("flush biz success");
        return gson.toJson(res);
    }

    private static String embeddingStatus(Req req, AiCodeRes res, Project project) {
        String scope = req.getData().getOrDefault("scope", "project");
        CodeReq.CodeReqBuilder builder = CodeReq.builder();
        builder.projectName(project.getName());
        if (!scope.equals("project")) {
            builder.moduleName(getModule(req.getData().get("module"), project).getName());
        }
        EmbeddingStatus status = AthenaCodeService.embeddingStatus(builder.build());
        String msg = scope.equals("project") ? ("project_" + req.getData().get("project")) : ("module_" + req.getData().get("module"));
        res.setMsg(gson.toJson(status) + ":" + msg);
        return gson.toJson(res);
    }

    @Nullable
    private static Module getModule(String moduleName, Project project) {
        if (StringUtils.isEmpty(moduleName)) {
            Mutable<Module> mutable = new MutableObject<>();
            ApplicationManager.getApplication().invokeAndWait(() -> {
                mutable.setValue(ProjectUtils.getCurrentModule(project));
            });
            return mutable.getValue();
        } else {
            return ProjectUtils.getModuleWithName(project, moduleName);
        }


    }
}
