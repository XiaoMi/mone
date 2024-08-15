package run.mone.ultraman.http.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.xiaomi.youpin.tesla.ip.common.Safe;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.PsiClassUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.http.HttpResponseUtils;
import run.mone.ultraman.service.AthenaCodeService;

/**
 * @author goodjava@qq.com
 * @date 2024/4/28 14:53
 */
@Slf4j
public class WriteCodeHandler extends BaseHandler{

    public JsonObject execute(JsonObject obj, JsonObject resObj) {
        log.info("write code param:\n{}\n", obj);
        //如果不传projectName,则自己计算(但如果超过1个,则就去第1个)
        String projectName = getProjectName(obj);
        String moduleName = GsonUtils.get(obj, "moduleName", () -> "");
        boolean hasCode = obj.has("code");
        //处理单个类的替换
        if (handleCodeReplacement(hasCode, obj, projectName, resObj)) {
            return resObj;
        }
        JsonArray codeList = obj.getAsJsonArray("codeList");
        //这个类的定义(去除public method 的实现部分)
        JsonArray codeDefinitionArray = new JsonArray();
        codeList.forEach(it -> Safe.run(() -> {
            String code = it.getAsJsonObject().getAsJsonPrimitive("output").getAsString();
            log.debug("create class:\n{}", code);
            AthenaClassInfo info = AthenaCodeService.classInfo(code);
            codeDefinitionArray.add(GsonUtils.gson.toJsonTree(info.getClassCode()));
            Project project = AthenaContext.ins().getProjectMap().get(projectName);
            CodeService.invoke(() -> {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    PsiDirectory directory = PsiClassUtils.getSourceDirectoryWithModule(project, moduleName);
                    directory = PsiClassUtils.createPackageDirectories(directory, info.getPackagePath());
                    PsiClassUtils.createClass(project, info.getName(), code, false, directory);
                    log.info("create class:{} success", info.getName());
                });
                return null;
            });
        }));
        log.info("code definition:{}", codeDefinitionArray);
        resObj.add("codeDefinitionArray", codeDefinitionArray);
        return resObj;
    }




    private boolean handleCodeReplacement(boolean hasCode, JsonObject obj, String projectName, JsonObject resObj) {
        if (hasCode) {
            boolean replace = GsonUtils.get(obj, "replace", false);
            if (replace) {
                CodeService.invoke(() -> {
                    String code = obj.get("code").getAsString();
                    AthenaClassInfo info = AthenaCodeService.classInfo(code);
                    PsiClass psiClass = PsiClassUtils.findClassByName(ProjectUtils.projectFromManager(projectName), info.getClassName());
                    CodeService.replaceFileContent((PsiFile) psiClass.getParent(), obj.get("code").getAsString());
                    resObj.addProperty("data", "success");
                    return null;
                });
                return true;
            }
        }
        return false;
    }


}
