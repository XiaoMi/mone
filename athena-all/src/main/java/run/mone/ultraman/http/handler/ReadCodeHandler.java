package run.mone.ultraman.http.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.PsiMethodUtils;
import com.xiaomi.youpin.tesla.ip.service.QuickFixInvokeUtil;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.PsiClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.mutable.MutableObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.service.AthenaCodeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/4/28 15:43
 */
@Slf4j
public class ReadCodeHandler extends BaseHandler {


    public JsonObject execute(JsonObject obj, JsonObject resObj) {

        log.info("req:{}", obj);

        String projectName = getProjectName(obj);
        String packageName = GsonUtils.get(obj, "packageName", () -> "");

        JsonElement clsJson = obj.get("className");
        JsonElement clsListJson = obj.get("classList");

        //获取当前打开的类
        boolean openClass = obj.has("openClass");

        if (StringUtils.isBlank(projectName)) {
            return resObj;
        }

        Project project = ProjectUtils.projectFromManager(projectName);

        //读取当前打开的class
        if (openClass) {
            return getJsonObjectWithErrors(resObj, project);
        }

        //读取指定method,并且携带行号
        if (obj.has("openMethod")) {
            MutableObject mo = new MutableObject();
            ApplicationManager.getApplication().invokeAndWait(()->{
                mo.setValue(getMethodCode(obj, resObj, project));
            });
            return (JsonObject) mo.getValue();
        }

        //replaceMethod
        if (obj.has("replaceMethod")) {
            return replaceMethodContent(obj, resObj, project);
        }


        if (clsJson != null) {
            String className = getFQCN(packageName, clsJson.getAsString());
            resObj.addProperty("data", getCode(projectName, className));
        } else if (clsListJson != null) {
            JsonArray classList = clsListJson.getAsJsonArray();
//                            Map<String/*className*/, String/*code*/> codeMap = new HashMap();
            JsonArray modifiedClassArray = new JsonArray();
            for (int i = 0; i < classList.size(); i++) {
                String clsName = getFQCN(packageName, classList.get(i).getAsString());
                String code = getCode(projectName, clsName);
                if (StringUtils.isNotBlank(code)) {
                    AthenaClassInfo info = AthenaCodeService.classInfo(code);
                    modifiedClassArray.add(GsonUtils.gson.toJsonTree(info.getClassCode()));
                }
            }
            resObj.add("modifiedClassArray", modifiedClassArray);
        }
        return resObj;
    }

    @NotNull
    private static JsonObject replaceMethodContent(JsonObject obj, JsonObject resObj, Project project) {
        String methodCode = obj.get("methodCode").getAsString();
        String className = obj.get("className").getAsString();
        String methodName = obj.get("methodName").getAsString();
        ApplicationManager.getApplication().invokeLater(() -> {
            log.info("className:{} methodName:{}", className, methodName);
            PsiMethod psiMethod = CodeService.getPsiMethod(project, className, methodName);
            log.info("get method:{}", psiMethod);
            //替换方法内容
            PsiMethodUtils.replacePsiMethod(project, psiMethod, methodCode.trim());
            //打开要修改的类
            CodeService.openJavaClass(project, className);
        });
        resObj.addProperty("data", "ok");
        return resObj;
    }

    @NotNull
    private static JsonObject getMethodCode(JsonObject obj, JsonObject resObj, Project project) {
        String className = obj.get("className").getAsString();
        String methodName = obj.get("methodName").getAsString();
        String methodCode = CodeService.getPsiMethodByName(project, className, methodName);
        resObj.addProperty("methodCode", methodCode);
        return resObj;
    }

    @NotNull
    private static JsonObject getJsonObjectWithErrors(JsonObject resObj, Project project) {
        //获取这个类的错误
        MutableObject code = new MutableObject();
        PsiFile psi = CodeService.getPsiFile(project);
        code.setValue(psi.getText());
        List<String> errors = CodeService.invoke(() -> ApplicationManager.getApplication().runReadAction((Computable<List<String>>) () -> QuickFixInvokeUtil.findAllErrors(project, psi).stream().map(it -> "问题:" + it.getText() + "  问题描述:" + it.getDescription()).collect(Collectors.toList())));
        resObj.addProperty("code", (String) code.getValue());
        resObj.add("error", GsonUtils.gson.toJsonTree(errors));
        return resObj;
    }


    private static String getFQCN(String packageName, String className) {
        return StringUtils.joinWith(".", packageName, className);
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

}
