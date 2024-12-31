package run.mone.ultraman.common;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.tesla.ip.bo.Action;
import com.xiaomi.youpin.tesla.ip.bo.M78CodeGenerationInfo;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.M78Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.Version;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/4/20 23:01
 */
@Slf4j
public class CodeUtils {

    private static final int CODE_STATISTICS_SOURCE_IDEA = 1;

    public static String format(String code) {
        return code.replaceFirst("\n+", "").replaceAll("import.*\n", "");
    }

    public static String formatRemoveEnter(String code) {
        return code.replaceFirst("\n+", "");
    }

    public static String formatRemoveEnter1(String code) {
        return code.replaceFirst("：\n+", "");
    }

    public static List<String> getImportList(String code) {
        return Arrays.stream(code.split("\n|;")).filter(it -> it.startsWith("import")).map(it -> it.replaceAll(";|import| ", "")).collect(Collectors.toList());
    }

    public static List<String> getImportList2(String code) {
        return Arrays.stream(code.split("\n|;")).filter(it -> it.startsWith("import")).map(it -> it.replaceAll(";|import", "")).collect(Collectors.toList());
    }

    public static String getComment(String code) {
        return code.replaceAll("/\\*\\*|\\*/| +\\* ", "").replaceAll("<p>", "\n");
    }

    /**
     * upload statistics
     */
    public static void uploadCodeGenInfo(M78CodeGenerationInfo info) {
        SafeRun.run(() -> {
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            info.setCtime(now);
            info.setUtime(now);
            info.setSource(CODE_STATISTICS_SOURCE_IDEA);
            info.setUsername(AthenaContext.ins().getUserName());
            info.setPluginVersion(new Version().toString());
            info.setIp(AthenaContext.ins().getLocalAddress());
            info.setSystemVersion(System.getProperty("os.name").toLowerCase());
            info.setIdeVersion(ApplicationInfo.getInstance().getBuild().asString());
            Result<Boolean> ok = M78Service.uploadCodeInfo(GsonUtils.gson.toJson(info));
            log.info("upload code info, status:{}", ok);
        });
    }

    public static void uploadCodeGenInfo(String code, String comment, String projectName, String className) {
        uploadCodeGenInfo(Action.GENERATE_CODE.getCode(), code, comment, projectName, className);
    }

    public static void uploadCodeGenInfo(int actionCode, String code, String comment, Project project) {
        String projectName = project == null ? "" : project.getName();
        PsiClass psiClass = CodeService.getPsiClassInRead(project);
        String className = psiClass == null ? "" : psiClass.getName();
        uploadCodeGenInfo(actionCode, code, comment, projectName, className);
    }

    public static void uploadCodeGenInfo(int actionCode, String code, String comment, String projectName, String className) {
        M78CodeGenerationInfo info = new M78CodeGenerationInfo();
        info.setCodeLinesCount(CodeUtils.getLineCnt(code, false));
        info.setAnnotation(CodeUtils.checkLineStartsWithComment(comment));
        info.setProjectName(projectName);
        info.setClassName(className);
        info.setMethodName("GENERATED");
        info.setAction(actionCode);
        uploadCodeGenInfo(info);
    }

    public static void uploadCodeGenInfo(int actionCode, String projectName, String className, String methodName) {
        M78CodeGenerationInfo info = new M78CodeGenerationInfo();
        info.setClassName(className);
        info.setProjectName(projectName);
        info.setMethodName(methodName == null ? "" : methodName);
        info.setCodeLinesCount(0);
        info.setAction(actionCode);
        uploadCodeGenInfo(info);
    }

    public static int getLineCnt(String code, boolean markDown) {
        int lineCount = 1; // 如果字符串不为空至少有一行

        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '\n') {
                lineCount++;
            }
        }
        if (markDown) {
            return Math.max(lineCount - 2, 0);
        }
        return lineCount;
    }

    // 帮我生成一个方法，用于判断当前行是否是//开头，如果是，则返回这行，如果不是，返回null。入参是String，返回值是String，注意判断String非空，并且在判断前要trim
    public static String checkLineStartsWithComment(String line) {
        if (line != null && !line.trim().isEmpty()) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("//") || trimmedLine.startsWith("/**")) {
                return trimmedLine;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String code = " /**\n" +
                "     * 方法作用:\n" +
                "     * 对两个整数进行求和操作\n" +
                "     * <p>\n" +
                "     * 方法参数:\n" +
                "     * 参数a和b分别为两个整数\n" +
                "     * <p>\n" +
                "     * 方法返回类型:\n" +
                "     * int\n" +
                "     * <p>\n" +
                "     * 方法逻辑:\n" +
                "     * 将参数a和参数b相加,并返回结果\n" +
                "     */";

        System.out.println(getComment(code));
    }

}
