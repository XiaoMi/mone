package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.codegen.generator.ClassGenerator;
import com.xiaomi.youpin.codegen.generator.DirectoryGenerator;
import com.xiaomi.youpin.codegen.generator.PomGenerator;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FaasGen {
    private static final String DEFAULT_MODEL = "mi-function";
    //    private static final String handlerDir = "handler";
    private static final String srcPath = "/src/main/java/";
    private static final String testPath = "/src/test/java/";

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId, null);
    }

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, String functionName) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId, null, functionName);
    }

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, String moduleName, String functionName) {
        try {
            if (StringUtils.isEmpty(moduleName)) {
                moduleName = DEFAULT_MODEL;
            }
            if (StringUtils.isEmpty(functionName)) {
                functionName = this.adapterProjectNameToCamelName(projectName) + "Handler";
            }
            String packagePath = packageName.replaceAll("\\.", "/");
            new DirectoryGenerator(projectPath, projectName, moduleName + File.separator + srcPath + File.separator + packagePath).generator();
            new DirectoryGenerator(projectPath, projectName, moduleName + File.separator + testPath + File.separator + packagePath + File.separator + "test").generator();

            // handler
            String templateName = "faas/handler.tml";
            Map<String, Object> m = new HashMap<>(3);
            m.put("package", packageName);
            m.put("author", author);
            m.put("project", this.adapterProjectNameToCamelName(projectName));
            m.put("functionName", functionName);
            m.put("moduleName", moduleName);
            new ClassGenerator(projectPath, projectName, moduleName + File.separator + srcPath, packagePath,
                    functionName, templateName).
                    generator(m);
            // test
            templateName = "faas/test.tml";
            new ClassGenerator(projectPath, projectName, moduleName + File.separator + testPath, packagePath + File.separator + "test",
                    "DefaultTest", templateName).
                    generator(m);
            // pom
            Map<String, Object> mpom = new HashMap<>();
            mpom.put("groupID", groupId);
            mpom.put("project", projectName);
            mpom.put("version", versionId + "-SNAPSHOT");
            mpom.put("functionName", functionName);
            mpom.put("moduleName", moduleName);
            new PomGenerator(projectPath, projectName + File.separator + moduleName, "faas/func_pom.tml").generator(mpom);
            new PomGenerator(projectPath, projectName, "faas/pom.tml").generator(mpom);

            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("FaasGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }
        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private String adapterProjectNameToCamelName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        try {
            String[] strings = name.split("-");
            if (strings.length > 1) {
                String res = "";
                for (int i = 0; i < strings.length; i++) {
                    if (!StringUtils.isEmpty(strings[i])) {
                        res = res + StringUtils.capitalize(strings[i]);
                    }
                }
                return res;
            }
        } catch (Exception e) {
            return StringUtils.capitalize(name);
        }
        return StringUtils.capitalize(name);
    }
}
