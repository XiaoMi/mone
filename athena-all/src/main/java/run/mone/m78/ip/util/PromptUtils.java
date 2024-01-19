package run.mone.m78.ip.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.dialog.DialogResult;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.bo.CreateClassMeta;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.ConfigUtils;
import run.mone.m78.ip.dialog.ChromeDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
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
        return Maps.newHashMap();
    }


    /**
     * 创建类(class enum)
     *
     * @param project
     * @param modelName
     * @param promptName
     */
    public static void createClass(Project project, final String modelName, String promptName) {

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

    }

    /**
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

    /**
     * 更新方法
     *
     * @param req
     */
    public static void updateMethod(GenerateCodeReq req) {

    }

    /**
     * 给一个方法添加逐行注释,或者代码
     *
     * @param promptName
     * @param req
     */
    public static void lineByLineCommentOrCode(String promptName, GenerateCodeReq req) {

    }

    private static String filterAnnoCode(String codeStr, PromptInfo promptInfo) {
        return codeStr;
    }

    private static void addImportList(Project project, Map<String, String> map) {

    }

    public static List<String> getImportList(PromptInfo promptInfo) {
        return null;
    }

    /**
     * @param req
     * @param map
     * @return
     */
    @NotNull
    private static String getCode(GenerateCodeReq req, Map<String, String> map) {
        return "";
    }

    @NotNull
    private static String getScope(GenerateCodeReq req, Map<String, String> map) {
        return "";
    }


    private static Pair<String, String> getPackageAndClass(DialogResult result) {
        return null;
    }

    private static DialogResult showDialog(Project project, Module module, String data) {
        return null;
    }

    /**
     * 生成接口
     *
     * @param req
     */
    public static void generateInterface(GenerateCodeReq req) {


    }

    /**
     * 用来生成测试方法
     */
    public static void select(GenerateCodeReq req, Project project, Module module, PromptInfo promptInfo) {

    }

    public static void checkPomVersion(GenerateCodeReq req) {
    }

    public static void generateAnnoForBootStrap(String promptName, Project project) {

    }

}
