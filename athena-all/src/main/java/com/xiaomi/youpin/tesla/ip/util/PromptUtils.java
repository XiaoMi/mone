package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.bo.prompt.PromptParam;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.consumer.CodeConsumer;
import com.xiaomi.youpin.tesla.ip.dialog.*;
import com.xiaomi.youpin.tesla.ip.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.background.AthenaEditorTask;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.common.Code;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.http.HttpClient;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/5/29 10:09
 */
@Slf4j
public class PromptUtils {


    private static final Gson gson = new Gson();


    public static Map<String, Object> getParamsFromAiProxy(String promptName, String metaStr) {
        return getParamsFromAiProxy(promptName, metaStr, false);
    }

    public static Map<String, Object> getParamsFromAiProxy(String promptName, String metaStr, boolean useSelect) {
        try {
            String aiRes = HttpClient.callAiProxy("params", ImmutableMap.of("name", promptName));
            Type typeOfT = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Map<String, Integer> m = gson.fromJson(aiRes, typeOfT);
            Map<String, Object> v = m.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> ""));
            if (useSelect) {
                return v;
            }
            //使用meta中的信息
            if (StringUtils.isNotEmpty(metaStr)) {
                Type to = new TypeToken<List<PromptParam>>() {
                }.getType();
                List<PromptParam> promptParamList = gson.fromJson(metaStr, to);
                promptParamList.forEach(it -> v.put(it.getName(), it));
            }
            return v;
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return Maps.newHashMap();
        }
    }


    /**
     * 创建类(class enum)
     *
     * @param project
     * @param modelName
     * @param promptName
     */
    public static void createClass(Project project, final String modelName, String promptName) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        List<CreateClassMeta> classMetas = gson.fromJson(promptInfo.getMeta(), new TypeToken<List<CreateClassMeta>>() {
        }.getType());
        CreateClassMeta createClassMeta = CollectionUtils.isEmpty(classMetas) ? null : classMetas.get(0);
        Editor editor = CodeService.getEditor(project);
        String[] params = getParams(createClassMeta, editor);
        Map<String, Object> v = getParamsFromAiProxy(promptName, promptInfo.getMeta(), ObjectUtils.notEqual(null, createClassMeta) && createClassMeta.isUseSelect());
        Map<String, List<String>> mList = new HashMap<>();
        mList.put("module", ProjectUtils.listAllModules(project));
        ParamTableDialog ppt = new ParamTableDialog(ParamDialogReq.builder().title("param").build(), project, v, mList, promptInfo);
        ppt.setConsumer((values) -> {
            String packageStr = values.get("package").toString();
            String className = values.get("class").toString();
            String modulePath = values.get("module").toString();
            String newModelName = StringUtils.isNotBlank(modulePath) ? modulePath : getModelName(modelName, values);

            CodeService.createEmptyClass(project, newModelName, packageStr, className);
            PsiClass psiClass = CodeService.getPsiClass2(project);
            TextRange textRange = psiClass.getTextRange();
            CodeService.deleteTextRange(project, psiClass.getTextRange());
            Editor codeEditor = CodeService.getEditor(project);
            codeEditor.getCaretModel().moveToOffset(textRange.getStartOffset());
            Map<String, String> pm = values.entrySet().stream().collect(Collectors.toMap(en -> en.getKey(), en -> en.getValue().toString()));
            CodeService.generateCodeWithAi3(project, promptName, params, pm, (p, code) -> CodeService.writeCode2(p, codeEditor, code));
        });
        ppt.show();
    }

    private static String getModelName(String modelName, Map<String, Object> values) {
        if (StringUtils.isEmpty(modelName)) {
            modelName = values.getOrDefault("module", "").toString();
        }
        return modelName;
    }

    @Nullable
    private static String[] getParams(CreateClassMeta createClassMeta, Editor editor) {
        String[] params = null;
        if (ObjectUtils.notEqual(null, createClassMeta) && createClassMeta.isUseSelect()) {
            params = new String[]{EditorUtils.getSelectContent(editor)};
        }
        return params;
    }


    public static void createClass2(Project project, String promptName, String showDialog, Map<String, String> param) {
        String isShowDialog = StringUtils.isEmpty(showDialog) ? "true" : showDialog;
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        Map<String, Object> v = getParamsFromAiProxy(promptName, promptInfo.getMeta());
        if ("true".equals(isShowDialog)) {
            String url = buildDialogUrl(project, v, promptInfo);
            ChromeDialog chromeDialog = new ChromeDialog(url, project);
            chromeDialog.show();
            return;
        }

        String packageStr = param.get("package").toString();
        String className = param.get("class").toString();
        String modelName = param.get("modelName").toString();

        CodeService.createEmptyClass(project, modelName, packageStr, className);

        PsiClass psiClass = CodeService.getPsiClass2(project);
        TextRange textRange = psiClass.getTextRange();
        CodeService.deleteTextRange(project, psiClass.getTextRange());
        Editor codeEditor = CodeService.getEditor(project);
        codeEditor.getCaretModel().moveToOffset(textRange.getStartOffset());

        Map<String, String> pm = param.entrySet().stream().collect(Collectors.toMap(en -> en.getKey(), en -> en.getValue().toString()));
        CodeService.generateCodeWithAi3(project, promptName, new String[]{}, pm, (p, code) -> CodeService.writeCode2(p, codeEditor, code));

    }


    public static void createClass3(Project project, String promptName, Map<String, String> param) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);

        String packageStr = param.get("package").toString();
        String className = param.get("class").toString();
        String modelName = param.get("modelName").toString();

        CodeService.createEmptyClass(project, modelName, packageStr, className);

        PsiClass psiClass = CodeService.getPsiClass2(project);
        TextRange textRange = psiClass.getTextRange();
        CodeService.deleteTextRange(project, psiClass.getTextRange());
        Editor codeEditor = CodeService.getEditor(project);
        codeEditor.getCaretModel().moveToOffset(textRange.getStartOffset());

    }


    /**
     * $$->隐藏
     * $@$->选择框
     *
     * @param project
     * @param param
     * @param promptInfo
     * @return
     */
    private static String buildDialogUrl(Project project, Map<String, Object> param, PromptInfo promptInfo) {
        if (!param.containsKey("model")) {
            param.put("$@$model", ProjectUtils.listAllModules(project));
        }
        if (!param.containsKey("package")) {
            param.put("package", "");
        }
        if (param.containsKey("class")) {
            param.put("class", "");
        }
        param.put("$$showDialog", "false");
        param.put("$$prompt", promptInfo.getPromptName());
        param.put("$$desc", promptInfo.getDesc());
        return ConfigUtils.getConfig().getChatServer() + "/code-form" + "?param=" + URLEncoder.encode(gson.toJson(param));
    }

    /**
     * 给方法添加注释
     *
     * @param req
     */
    public static void addComment(GenerateCodeReq req) {
        Project project = req.getProject();
        PsiMethod method = CodeService.getMethod(project);
        Editor editor = CodeService.getEditor(project);
        if (null == method) {
            HintUtils.show(editor, Message.selectMethodMsg, true);
            return;
        }
        String c = method.getText();
        Map<String, String> map = Maps.newHashMap();
        map.put("code", c);

        AthenaTask task = new AthenaTask(req.getProject(), "Athena", req.getPromptName(), "", map);
        task.setType("method_comment");
        PromptContext context = new PromptContext();
        context.setEditor(editor);
        task.setPromptContext(context);
        task.setReq(req);
        AthenaTask.start(task);
    }


    /**
     * 修改class
     *
     * @param project
     * @param promptName
     */
    public static void modifyClass(Project project, String promptName) {
        PsiClass psiClass = CodeService.getPsiClass2(project);
        String codeStr = psiClass.getText();
        CodeService.deletePsiClass(project, psiClass);
        Editor codeEditor = CodeService.getEditor(project);
        CodeService.generateCodeWithAi2(project, promptName, new String[]{codeStr}, (p, code) -> CodeService.writeCode2(p, codeEditor, code));
    }


    /**
     * 创建一个文件
     *
     * @param req
     */
    public static void createFile(String promptName, String fileName, GenerateCodeReq req) {
        fileName = req.getPromptInfo().getLabels().getOrDefault("fileName", "athena_");
        String scope = req.getPromptInfo().getLabels().getOrDefault("scope", "method");
        String codeStr = "";
        if (scope.equals("method")) {
            codeStr = CodeService.getMethod(req.getProject()).getText();
        } else if (scope.equals("class")) {
            PsiClass psiClass = CodeService.getPsiClass(req.getProject());
            codeStr = psiClass.getText();
            fileName = fileName + psiClass.getName();
        } else if (scope.equals("select")) {
            codeStr = CodeService.getSelectedText(CodeService.getEditor(req.getProject()));
        }
        final String c = codeStr;
        TextService.writeContent(req.getProject(), fileName + ".md", req.getModule().getName(), () -> {
            Editor editor = CodeService.getEditor(req.getProject());
            Map<String, String> m = Maps.newHashMap();
            m.put("code", c);
            CodeService.generateCodeWithAi3(req.getProject(), promptName, new String[]{req.getMeta()}, m, (p, code) -> CodeService.writeCode4(p, editor, code, false));
        });
    }

    /**
     * 修改一个类，会清空编辑区的所有内容
     *
     * @param req
     */
    public static void updateClass(GenerateCodeReq req) {
        PsiClass psiClass = CodeService.getPsiClass(req.getProject());
        String classCode = psiClass.getText();
        TextRange textRange = psiClass.getTextRange();
        CodeService.deleteTextRange(req.getProject(), textRange);
        Editor editor = CodeService.getEditor(req.getProject());
        CodeService.generateCodeWithAi2(req.getProject(), req.getPromptName(), new String[]{classCode}, (p, code) -> CodeService.writeCode2(p, editor, code));
    }

    public static void modifyMethod(GenerateCodeReq req) {
        Editor editor = CodeService.getEditor(req.getProject());
        if (null == editor) {
            return;
        }
        //修改方法名
        if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "modify_method_name") || LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "modify_method_param_name")) {
            Map<String, String> m = new HashMap<>();

            PsiMethod method = CodeService.getMethod(req.getProject());
            if (null == method) {
                HintUtils.show(editor, com.xiaomi.youpin.tesla.ip.bo.Message.selectMethodMsg, true);
                return;
            }
            m.put("code", method.getText());
            m.put("paramName", req.getParam().getOrDefault("paramName", ""));
            AthenaEditorTask task = new AthenaEditorTask(req, "Athena", m, new MessageConsumer() {
                private StringBuilder sb = new StringBuilder();

                @Override
                public void onEvent(AiMessage message) {
                    sb.append(message.getText());
                }

                @Override
                public void end(AiMessage message) {
                    if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "modify_method_name")) {
                        PsiMethodUtils.renameMethod(req.getProject(), method, sb.toString());
                    } else {
                        PsiMethodUtils.renameParameter(req.getProject(), (PsiParameter) req.getObjMap().get("parameter"), sb.toString());
                    }
                }
            });
            AthenaEditorTask.start(task);
        }
    }

    /**
     * 给一个方法添加逐行注释,或者代码
     *
     * @param promptName
     * @param req
     */
    public static void lineByLineCommentOrCode(String promptName, GenerateCodeReq req) {
        Editor editor = null;
        try {
            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
            Map<String, String> map = promptInfo.getLabels();
            editor = req.getEditor();
            addImportList(req.getProject(), map);
            String codeStr = getCode(req, map);
            codeStr = filterAnnoCode(codeStr, promptInfo);
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(req.getProject());
            @Nullable PsiFile psiFile = documentManager.getPsiFile(editor.getDocument());
            Code code = new Code();
            code.setEditor(editor);
            code.setPromptInfo(promptInfo);
            code.setDocument(editor.getDocument());
            code.setProject(req.getProject());
            code.setPsiFile(psiFile);
            AthenaEditorTask.start(new AthenaEditorTask(req, "Athena", codeStr, ImmutableMap.of("code", codeStr), new CodeConsumer(editor, req) {
                @Override
                public void onEvent(AiMessage message) {
                    code.append(message.getText());
                }
            }));
        } catch (Throwable ex) {
            HintUtils.show(editor, ex.getMessage(), true);
        }
    }

    private static String filterAnnoCode(String codeStr, PromptInfo promptInfo) {
        String str = promptInfo.getLabels().getOrDefault("delete", "").trim();
        if (StringUtils.isEmpty(str)) {
            return codeStr;
        }
        String[] skipArray = str.split(",");
        if (skipArray.length > 0) {
            return codeStr.lines().map(it -> {
                boolean has = Arrays.stream(skipArray).filter(it2 -> it.contains(it2)).findAny().isPresent();
                if (has) {
                    int begin = it.indexOf("@");
                    return it.substring(0, begin);
                }
                return it;
            }).collect(Collectors.joining("\n"));
        }
        return codeStr;
    }

    private static void addImportList(Project project, Map<String, String> map) {
        if (map.containsKey("import")) {
            String str = map.get("import");
            Type typeOfT = new TypeToken<List<String>>() {
            }.getType();
            List<String> importList = gson.fromJson(str, typeOfT);
            CodeService.addImport(project, importList);
        }
    }

    public static List<String> getImportList(PromptInfo promptInfo) {
        Map<String, String> labels = promptInfo.getLabels();
        if (null != labels && labels.containsKey("import")) {
            String str = labels.get("import");
            Type typeOfT = new TypeToken<List<String>>() {
            }.getType();
            List<String> importList = gson.fromJson(str, typeOfT);
            return importList;
        }
        return Lists.newArrayList();
    }

    /**
     * 根据lable里的scope获取代码,有可能是method,也有可能是class
     *
     * @param req
     * @param map
     * @return
     */
    @NotNull
    private static String getCode(GenerateCodeReq req, Map<String, String> map) {
        String codeStr = "";
        String scope = getScope(req, map);
        if (scope.equals("class")) {
            codeStr = CodeService.getClassAndLineNumbers(CodeService.getPsiClass(req.getProject()));
        } else {
            PsiMethod method = CodeService.getMethod(req.getProject());
            if (null == method) {
                throw new RuntimeException(Message.selectMethodMsg);
            }
            codeStr = CodeService.getMethodAndLineNumbers(method);
        }
        return codeStr;
    }

    @NotNull
    private static String getScope(GenerateCodeReq req, Map<String, String> map) {
        String scope = "method";
        if (null != map && map.containsKey("scope")) {
            scope = map.getOrDefault("scope", "nearby");
        }
        //就近选择(离method近,就是method,离class近就是class)
        if (scope.equals("nearby")) {
            PsiMethod method = CodeService.getMethod(req.getProject());
            if (null != method) {
                scope = "method";
            } else {
                scope = "class";
            }
        }
        return scope;
    }


    private static Pair<String, String> getPackageAndClass(DialogResult result) {
        String className = result.getData().get("class");
        String[] strs = className.split("\\.");
        String packageStr = Arrays.stream(strs).limit(strs.length - 1).collect(Collectors.joining("."));
        return Pair.of(packageStr, strs[strs.length - 1]);

    }

    private static DialogResult showDialog(Project project, Module module, String data) {
        DialogReq dialogReq = gson.fromJson(data, DialogReq.class);
        dialogReq.setModule(module);
        SelectClassAndMethodDialog dialog = new SelectClassAndMethodDialog(project, dialogReq);
        dialog.show();
        int exitCode = dialog.getExitCode();
        if (exitCode != DialogWrapper.OK_EXIT_CODE) {
            return null;
        }
        DialogResult result = dialog.getResult();
        return result;
    }

    /**
     * 生成接口
     *
     * @param req
     */
    public static void generateInterface(GenerateCodeReq req) {
        Project project = req.getProject();
        String ps = req.getClassCode();
        Map<String, String> map = new HashMap<>();
        map.put("code", ps);
        String qualifiedName = req.getQualifiedName();
        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));

        ImportCode code = CodeService.createClassAndAddEmptyLine(project, req.getModule().getName(), packageName, req.getClassName(), "Interface", false, true);

        AthenaEditorTask.start(new AthenaEditorTask(req, "Athena", ps, map, new MessageConsumer() {
            @Override
            public void onEvent(AiMessage message) {
                code.append(message.getText());
            }
        }));

    }


    public static void generateTestMethod(GenerateCodeReq req) {
        Project project = req.getProject();
        if (StringUtils.isEmpty(req.getMethodCode())) {
            HintUtils.show(req.getEditor(), Message.selectMethodMsg, true);
            return;
        }
        String methodCode = req.getMethodCode();
        String qualifiedName = req.getQualifiedName();
        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));

        ImportCode code = CodeService.createClassAndAddEmptyLine(project, req.getModule().getName(), packageName, req.getClassName(), "Test", true, false);

        Map<String, String> map = new HashMap<>();
        map.put("code", "public class" + req.getClassName() + "{" + methodCode + " \n}");
        AthenaEditorTask.start(new AthenaEditorTask(req, "Athena", methodCode, map, new MessageConsumer() {
            @Override
            public void onEvent(AiMessage message) {
                code.append(message.getText());
            }
        }));


    }

    /**
     * 操作之前这里有选择(选择类名或者方法名)
     * 更底层的机制是根据一段代码,生成一个方法或者替代一个方法
     * <p>
     * 用来生成测试方法
     */
    public static void select(GenerateCodeReq req, Project project, Module module, PromptInfo promptInfo) {
        String codeClassName = CodeService.getPsiClass(project).getQualifiedName();
        PsiClass pc = CodeService.getPsiClass(project);
        boolean springClass = LabelUtils.open("check_spring") && SpringUtils.isSpringClass(pc);

        PsiMethod pm = CodeService.getMethod(project);
        if (null == pm) {
            HintUtils.show(CodeService.getEditor(project), Message.selectMethodMsg, true);
            return;
        }
        String methodCode = pm.getText();

        //选择类和方法
        DialogResult result = showDialog(project, module, promptInfo.getMeta());

        if (null == result) {
            return;
        }
        String methodName = result.getData().get("method");
        String className = result.getData().get("class");

        //创建新类
        if (result.getData().get("new_class").equals("true")) {
            Pair<String, String> pair = getPackageAndClass(result);
            String addAnno = promptInfo.getLabels().getOrDefault("add_anno", "");
            List<String> annoList = gson.fromJson(addAnno, new TypeToken<List<String>>() {
            }.getType());
            CodeService.createEmptyClass(project, module.getName(), pair.getKey(), pair.getValue(), true, annoList);
        }

        PsiClass psiClass = CodeService.openJavaClass(project, className);

        String testClassCode = psiClass.getText();

        Editor editor = CodeService.getEditor(project);

        Map<String, String> map = new HashMap<>();

        //spring 单元测试,需要把字段注入
        if (springClass) {
            String serviceName = pc.getQualifiedName();
            String shortServiceName = pc.getNameIdentifier().getText();
            String junitVersion = ImportUtils.junitVersion(psiClass);
            PromptService.addImports(req, project, serviceName, null, editor, true, true, junitVersion);
            PromptService.addFields(project, serviceName, shortServiceName, psiClass, editor);
            //spring 单元测试带上之前的测试代码
            map.put("context", editor.getDocument().getText());
        } else {
            map.put("context", "");
        }

        //创建新方法,还是覆盖老的方法(并且光标移动到合理的位置)
        if (methodName.equals("New")) {
            PsiMethod @NotNull [] methods = psiClass.getMethods();
            int offset = 0;
            if (0 == methods.length) {
                offset = psiClass.getTextRange().getEndOffset() - 3;
            } else {
                PsiMethod psiMethod = methods[methods.length - 1];
                offset = psiMethod.getTextRange().getEndOffset();
            }
            editor.getCaretModel().moveToOffset(offset);
            CodeService.writeCode2(project, editor, "\n");
        } else {
            PsiMethod psiMethod = CodeService.getMethod(psiClass, methodName);
            CodeService.deleteMethod(project, psiMethod);
        }

        generateUnitTest(project, methodCode, codeClassName, map, testClassCode, editor);
    }

    /**
     * 打开单元测试类
     * 设置偏移量，如果方法数组长度为 0 则进行特定计算，否则根据最后一个方法的结束偏移量计算
     */
    public static String openUnitTestClass(Project project, String className) {
        PsiClass psiClass = CodeService.openJavaClass(project, className);
        String testClassCode = psiClass.getText();
        Editor editor = CodeService.getEditor(project);
        PsiMethod @NotNull [] methods = psiClass.getMethods();
        int offset = 0;
        if (0 == methods.length) {
            offset = psiClass.getTextRange().getEndOffset() - 3;
        } else {
            PsiMethod psiMethod = methods[methods.length - 1];
            offset = psiMethod.getTextRange().getEndOffset();
        }
        editor.getCaretModel().moveToOffset(offset);
        CodeService.writeCode2(project, editor, "\n");
        return testClassCode;
    }

    //生成单元测试代码
    public static void generateUnitTest(Project project, String methodCode, String codeClassName, Map<String, String> map, String testClassCode, Editor editor) {
        generateUnitTest(project, methodCode, codeClassName, map, testClassCode, editor, null);
    }

    public static void generateUnitTest(Project project, String methodCode, String codeClassName, Map<String, String> map, String testClassCode, Editor editor, CountDownLatch latch) {
        //生成测试方法的时候,让他能推理出来类名
        methodCode = "public class " + codeClassName + " {\n" + methodCode + "\n}";
        map.put("code", methodCode);
        //单元测试那个类的内容
        map.put("testClassCode", testClassCode);
        invokePrompt(project, Const.GENERATE_UNIT_TEST, map, editor, latch);
    }


    private static void invokePrompt(Project project, String promptName, Map<String, String> param, Editor editor) {
        invokePrompt(project, promptName, param, editor, null);
    }

    private static void invokePrompt(Project project, String promptName, Map<String, String> param, Editor editor, CountDownLatch latch) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PromptType promptType = Prompt.getPromptType(promptInfo);
        //直接使用bot
        PromptService.dynamicInvoke(GenerateCodeReq.builder()
                .project(project)
                .promptType(promptType)
                .promptName(promptName)
                .promptInfo(promptInfo)
                .countDownLatch(latch)
                .param(param)
                .editor(editor)
                .build());
    }

    public static void checkPomVersion(GenerateCodeReq req) {
        Editor editor = CodeService.getEditor(req.getProject());
        PromptInfo promptInfo = Prompt.getPromptInfo(req.getPromptName());
        Map<String, String> lables = promptInfo.getLabels();
        Dependency dependency = gson.fromJson(lables.get("version"), Dependency.class);
        Pair<Integer, String> checkRes = XmlService.checkPomVersion(req.getProject(), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
        HintUtils.show(editor, checkRes.getKey() == 0 ? Result.success(checkRes.getValue()) : Result.fail(-1, checkRes.getValue()));
    }

    public static void generateAnnoForBootStrap(String promptName, Project project) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PsiClass applicationBootStrapClass = PsiClassUtils.findApplicationRunClass(project);
        if (applicationBootStrapClass == null) {
            HintUtils.show(CodeService.getEditor(project), Result.fail(-1, "没有找到启动类！"));
            return;
        }
        Map<String, String> labels = promptInfo.getLabels();
        String annos = labels.get("anno");
        List<String> annoList = gson.fromJson(annos, new TypeToken<List<String>>() {
        }.getType());
        Editor editor = EditorUtils.getEditorFromPsiClass(project, applicationBootStrapClass);
        CodeService.addImport(project, editor, gson.fromJson(labels.get("import"), new TypeToken<List<String>>() {
        }.getType()));
        CodeService.addClassAnno(project, applicationBootStrapClass, annoList);
    }

}
