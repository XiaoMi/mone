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
public class PluginGen {
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, String url) {
        String srcPath = "/src/main/java/";
        String testPath = "/src/test/java/";
        String deployPath = "/deploy/manifests";

        String catAppdatas = "/data/appdatas/cat";
        String catApplogs = "/data/applogs/cat";

        String packagePath = packageName.replaceAll("\\.", "/");

        try {
            //创建项目
            DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "");
            directoryGenerator.generator();

            //创建module
            DirectoryGenerator apiModule = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                    File.separator + srcPath);
            apiModule.generator();

            generateApiPom(projectPath, projectName, groupId, versionId);


            DirectoryGenerator commonModule = new DirectoryGenerator(projectPath, projectName, projectName + "-common"
                    + File.separator + srcPath
            );
            commonModule.generator();

            generateCommonPom(projectPath, projectName, groupId, versionId);


            String serviceSrcPath = projectName + "-service" + File.separator + srcPath;
            String serviceFullSrcPath = serviceSrcPath + File.separator + packagePath;

//        generateDubboService(author, projectPath, projectName, packageName);


            DirectoryGenerator serviceModule = new DirectoryGenerator(projectPath, projectName, serviceFullSrcPath);
            serviceModule.generator();

            generateServicePom(projectPath, projectName, groupId, author, versionId, url);

            generageParentPom(projectPath, projectName, groupId, versionId);
            generageGitignore(projectPath, projectName);

            //生成入口类
            generateHandler(projectPath, projectName, packageName, author, url, packagePath, serviceSrcPath);

            //生成plugin handler 配置文件
            generateExtensions(projectPath, projectName, packageName);
            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("SpringBootProGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generateExtensions(String projectPath, String projectName, String handlerPkg) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-service/src/main/resources/META-INF");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-service/src/main/resources/META-INF/extensions.idx", "extensions.tml");
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", handlerPkg);
        m.put("project", StringUtils.capitalize(projectName));
        fileGenerator.generator(m);
    }

    private void generateHandler(String projectPath, String projectName, String packageName, String author, String url, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName) + "Handler", "handler_class.tml");
        Map<String, Object> m = new HashMap<>(4);
        m.put("package", packageName);
        m.put("author", author);
        m.put("url", url);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }


    private void generateDubboService(String author, String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName) + "Service", "dubbo_service_class.tml");
        Map<String, Object> m = new HashMap<>(3);
        m.put("package", packageName);
        m.put("project", StringUtils.capitalize(projectName));
        m.put("author", author);
        classGenerator.generator(m);
    }


    private void generateApiPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator apiPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-api", "pom_api.tml");
        Map<String, Object> ampom = new HashMap<>();
        ampom.put("groupId", groupId);
        ampom.put("parent_artifactId", projectName);
        ampom.put("artifactId", projectName + "-api");
        ampom.put("version", versionId + "-SNAPSHOT");
        ampom.put("plugin_id", projectName);
        ampom.put("version_id", versionId);
        apiPomGenerator.generator(ampom);
    }

    private void generateCommonPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator commonPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-common", "pom_common.tml");
        Map<String, Object> cmpom = new HashMap<>();
        cmpom.put("groupId", groupId);
        cmpom.put("parent_artifactId", projectName);
        cmpom.put("artifactId", projectName + "-common");
        cmpom.put("version", versionId + "-SNAPSHOT");
        commonPomGenerator.generator(cmpom);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "plugin_pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        mpom.put("api_module", projectName + "-api");
        mpom.put("common_module", projectName + "-common");
        mpom.put("service_module", projectName + "-service");
        pomGenerator.generator(mpom);
    }

    private void generateServicePom(String projectPath, String projectName, String groupId, String author, String versionId,
                                    String url

    ) {
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-service", "pom_service.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-service");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("plugin_id", projectName);
        smpom.put("version_id", versionId);
        smpom.put("author", author);
        smpom.put("plugin", projectName);
        smpom.put("plugin_url", url);
        smpom.put("plugin_class", "com.xiaomi.youpin.tesla.plug.TeslaPlugin");
        servicePomGenerator.generator(smpom);
    }

}
