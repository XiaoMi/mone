package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.codegen.generator.ClassGenerator;
import com.xiaomi.youpin.codegen.generator.DirectoryGenerator;
import com.xiaomi.youpin.codegen.generator.FileGenerator;
import com.xiaomi.youpin.codegen.generator.PomGenerator;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilterGen {
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, String filterOrder, String params, String cname, String desc, String gitAddress, String isSystem) {

        String packagePath = packageName.replaceAll("\\.", "/");

        try {
            //创建项目
            DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "src/main/java/" + packagePath + "/filter");
            directoryGenerator.generator();

            generageParentPom(projectPath, projectName, groupId, versionId);
            generageGitignore(projectPath, projectName);

            //生成入口类
            generateFilter(projectPath, projectName, packageName, author, packagePath + "/filter", "src/main/java", filterOrder);

            //生成配置文件
            generateExtensions(projectPath, projectName, versionId, desc, author, packageName, gitAddress, params, cname, isSystem);
            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");

        } catch (Exception e) {
            log.error("FilterGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private void generateExtensions(String projectPath, String projectName, String version, String desc, String author, String packageName, String gitAddress, String params, String cname, String system) {
        if (!system.equals("0") && !system.equals("1")) {
            system = "0";
        }

        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, "src/main/resources/FilterDef", "filter_def.tml");
        Map<String, Object> m = new HashMap<>(9);
        m.put("name", projectName);
        m.put("filter", packageName + ".filter." + StringUtils.capitalize(projectName));
        m.put("date", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        m.put("version", version);
        m.put("desc", desc);
        m.put("author", author);
        m.put("gitAddress", gitAddress);
        m.put("params", params);
        m.put("cname", cname);
        m.put("system", system);
        fileGenerator.generator(m);
    }

    private void generateFilter(String projectPath, String projectName, String packageName, String author, String packagePath, String serviceSrcPath, String filterOrder) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName), "filter_class.tml");
        Map<String, Object> m = new HashMap<>(4);
        m.put("package", packageName + ".filter");
        m.put("author", author);
        m.put("filterOrder", filterOrder);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "filter_pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        pomGenerator.generator(mpom);
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        mdGenerator.generator(new HashMap<>());
    }

}
