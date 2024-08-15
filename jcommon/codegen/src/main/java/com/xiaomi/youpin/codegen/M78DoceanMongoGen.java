package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.bo.Dependency;
import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.codegen.generator.FileGenerator;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class M78DoceanMongoGen {
    //    private static final String DEFAULT_MODEL = "mi-function";
    private final String tmpPath = "m78-docean-mongo";
    //    private static final String handlerDir = "handler";
//    private static final String srcPath = "/src/main/java/";
//    private static final String testPath = "/src/test/java/";

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, HashMap<String, ArrayList<Dependency>> dep) {
        String packagePath = packageName.replaceAll("\\.", "/");
        String camelName = this.adapterProjectNameToCamelName(projectName);
        Map<String, Object> m = new HashMap<>();
        m.put("group", groupId);
        m.put("artifactId", projectName);
        m.put("version", versionId);
        m.put("author", author);
        m.put("camelName", camelName);
        m.put("package", packageName);
        try {
//            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-common", "src", "test", "java"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-server", "src", "main", "java", packagePath, "bootstrap"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-server", "src", "test", "java"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-service", "src", "test", "java"));
//            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-service", "src", "main", "resources"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-api", "src", "main", "java", packagePath, "api", "bo"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-api", "src", "main", "java", packagePath, "api", "vo"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-server", "src", "main", "java", packagePath, "controller"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-common", "src", "main", "java", packagePath, "common"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-server", "src", "main", "resources", "config"));
            FileUtils.createDirectories(FileUtils.join(projectPath, projectName, projectName + "-service", "src", "main", "java", packagePath, "service"));
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-service", "src", "main", "java", packagePath, "service", "MongoService.java"), FileUtils.join(tmpPath, "mongoservice.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-api", "src", "main", "java", packagePath, "api", "bo", "MongoBo.java"), FileUtils.join(tmpPath, "mongobo.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-api", "src", "main", "java", packagePath, "api", "bo", "Page.java"), FileUtils.join(tmpPath, "page.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-service", "pom.xml"), FileUtils.join(tmpPath, "service-pom.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-service", "src", "main", "java", packagePath, "service", "ServiceImpl1.java"), FileUtils.join(tmpPath, "serviceimpl1.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "resources", "config", "staging.properties"), FileUtils.join(tmpPath, "staging.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "resources", "config.properties"), FileUtils.join(tmpPath, "config.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "resources", "logback.xml"), FileUtils.join(tmpPath, "logback.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join("pom.xml"), FileUtils.join(tmpPath, "pom.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-common", "src", "main", "java", packagePath, "common", "Result.java"), FileUtils.join(tmpPath, "result.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "java", packagePath, "bootstrap", "Bootstrap.java"), FileUtils.join(tmpPath, "bootstrap.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "java", packagePath, "controller", "MongodbController.java"), FileUtils.join(tmpPath, "mongodbcontroller.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "pom.xml"), FileUtils.join(tmpPath, "server-pom.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "resources", "config", "dev.properties"), FileUtils.join(tmpPath, "dev.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-server", "src", "main", "resources", "config", "online.properties"), FileUtils.join(tmpPath, "online.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-api", "pom.xml"), FileUtils.join(tmpPath, "api-pom.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-api", "src", "main", "java", packagePath, "api", "IService.java"), FileUtils.join(tmpPath, "iservice.tml")).generator(m);
            new FileGenerator(projectPath, projectName, FileUtils.join(projectName + "-common", "pom.xml"), FileUtils.join(tmpPath, "common-pom.tml")).generator(m);

            new FileGenerator(projectPath, projectName, FileUtils.join(".gitignore"), "springboot_gitignore.tml").generator(m);
            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("SpringBootProGen failed ", e);
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
