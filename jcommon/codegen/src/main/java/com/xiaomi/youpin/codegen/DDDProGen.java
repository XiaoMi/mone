/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.codegen;

import com.xiaomi.youpin.codegen.bo.Dependency;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.youpin.codegen.bo.Constants.*;

@Slf4j
public class DDDProGen {

    public Result<String> generateAndZip(String projectPath, String projectName, String groupId, String packageName, String author, String versionId, HashMap<String, ArrayList<Dependency>> dep) {

        String srcPath = "/src/main/java/";
        String testPath = "/src/test/java/";

        String packagePath = packageName.replaceAll("\\.", "/");

        if (dep == null) {
            dep = new HashMap<>();
        }
        Map<String, Object> pomMap = new HashMap<>();
        pomMap.put("groupId", groupId);
        pomMap.put("artifactId", projectName);
        pomMap.put("version", versionId + "-SNAPSHOT");
        pomMap.put("api_module", projectName + "-api");
        pomMap.put("app_module", projectName + "-app");
        pomMap.put("server_module", projectName + "-server");
        pomMap.put("domain_module", projectName + "-domain");
        pomMap.put("infrastructure_module", projectName + "-infrastructure");
        pomMap.put("parent_artifactId", projectName);
        pomMap.put("version_id", versionId);
        pomMap.put("bootstrap", this.adapterProjectNameToCamelName(projectName) + "Bootstrap");
        pomMap.put("package", packageName);
        try {
            // 创建项目
            new DirectoryGenerator(projectPath, projectName, "").generator();
            generageParentPom(pomMap, projectPath, projectName);
            generageReadMe(projectPath, projectName);
            generageGitignore(projectPath, projectName);


            // 创建module: api
            new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                    File.separator + srcPath + File.separator + packagePath + "api" + File.separator + "api").generator();
            ArrayList<Dependency> apiDepList = dep.get(DEP_API);
            generateApiPom(pomMap, projectPath, projectName, apiDepList);
            generateDubboApi(projectPath, projectName, packageName, packagePath + "api" + File.separator + "api", projectName + "-api" + File.separator + srcPath, author);


            // 创建module: app
            new DirectoryGenerator(projectPath, projectName, projectName + "-app" +
                    File.separator + srcPath + File.separator + packagePath + "app" + File.separator + "provider").generator();
            ArrayList<Dependency> appDepList = dep.get(DEP_APP);
            generateAppPom(pomMap, projectPath, projectName, appDepList);
            generateDubboApp(projectPath, projectName, packageName, packagePath + "app" + File.separator + "provider", projectName + "-app" + File.separator + srcPath, author);


            // 创建module: domain
            new DirectoryGenerator(projectPath, projectName, projectName + "-domain" +
                    File.separator + srcPath + File.separator + packagePath + "domain" + File.separator + "constant").generator();
            ArrayList<Dependency> domainDepList = dep.get(DEP_DOMAIN);
            generateDomainPom(pomMap, projectPath, projectName, domainDepList);
            generateDubboDomain(projectPath, projectName, packageName, packagePath + "domain" + File.separator + "constant", projectName + "-domain" + File.separator + srcPath, author);


            // 创建module: infrastructure
            new DirectoryGenerator(projectPath, projectName, projectName + "-infrastructure").generator();
            ArrayList<Dependency> infrastructureDepList = dep.get(DEP_INFRASTRUCTURE);
            generateInfrastructurePom(pomMap, projectPath, projectName, infrastructureDepList);

            // 创建module: server
            new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                    File.separator + srcPath + File.separator + packagePath + "server" + File.separator + "bootstrap").generator();
            new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                    File.separator + srcPath + File.separator + packagePath + "server" + File.separator + "config").generator();
            ArrayList<Dependency> serverDepList = dep.get(DEP_SERVER);
            generateServerPom(pomMap, projectPath, projectName, serverDepList);
            // 生成入口类
            generateBootstrap(projectPath, projectName, packageName, author, packagePath + "server" + File.separator + "bootstrap", projectName + "-server" + File.separator + srcPath);
            // 生成配置文件
            generateResources(projectPath, projectName, versionId);
            generateLogback(projectPath, projectName);
            // 生成dubbo相关
            generateDubboProperties(projectPath, projectName);
            generateDubboConfig(projectPath, projectName, packageName, packagePath + "server" + File.separator + "config", projectName + "-server" + File.separator + srcPath);
            // 生成nacos相关
            generateNacosConfig(projectPath, projectName, packageName, packagePath + "server" + File.separator + "config", projectName + "-server" + File.separator + srcPath);
            // 生成cat相关
//            generateCatProperties(projectPath, projectName);

            // check style
            new FileGenerator(projectPath, projectName, "checkstyle.xml", "ddd/checkstyle.tml").generator(pomMap);
            new FileGenerator(projectPath, projectName, "checkstyle-suppression.xml", "ddd/checkstyle-suppression.tml").generator(pomMap);

            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("DDDProGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private void generateResources(String projectPath, String projectName, String versionId) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config").generator();

        // 生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/application.properties", "ddd/springboot_application_properties.tml");
        FileGenerator devGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/dev.properties", "ddd/springboot_application_properties_dev.tml");
        FileGenerator stagingGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/staging.properties", "ddd/springboot_application_properties_st.tml");
        FileGenerator c3Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c3.properties", "ddd/springboot_application_properties_c3.tml");
        FileGenerator c4Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c4.properties", "ddd/springboot_application_properties_c4.tml");
        FileGenerator previewGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/preview.properties", "ddd/springboot_application_properties_preview.tml");
        FileGenerator dockerFileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/Dockerfile", "ddd/springboot_docker_file.tml");

        Map<String, Object> m = new HashMap<>(3);
        m.put("appName", projectName);
        m.put("project", projectName);
        m.put("version", versionId + "-SNAPSHOT");
        fileGenerator.generator(m);
        devGenerator.generator(m);
        stagingGenerator.generator(m);
        c3Generator.generator(m);
        c4Generator.generator(m);
        previewGenerator.generator(m);
        dockerFileGenerator.generator(m);
    }

    private void generateLogback(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/logback-spring.xml", "ddd/springboot_logback.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("project", projectName);
        fileGenerator.generator(m);
    }

    private void generateCatProperties(String projectPath, String projectName) {
        //生成文件夹
        new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/META-INF").generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/META-INF/app.properties", "springboot_cat_properties.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("appName", projectName);
        fileGenerator.generator(m);
    }

    private void generateDubboProperties(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/dubbo.properties", "springboot_dubbo_properties.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("project", projectName);
        fileGenerator.generator(m);
    }

    private void generateBootstrap(String projectPath, String projectName, String packageName, String author, String packagePath, String serviceSrcPath) {
        String templateName = "ddd/springboot_bootstrap_class.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, this.adapterProjectNameToCamelName(projectName) + "Bootstrap", templateName);
        Map<String, Object> m = new HashMap<>(3);
        m.put("package", packageName);
        m.put("author", author);
        m.put("project", this.adapterProjectNameToCamelName(projectName));
        classGenerator.generator(m);
    }

    private void generateDubboApiImp(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        String tmpName = "cmi/springboot_dubbo_api_imp_class.tml";

        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboHealthServiceImpl", tmpName);
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        classGenerator.generator(m);
    }

    private void generateDubboApi(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath, String author) {
        String tmpName = "ddd/springboot_dubbo_api_class.tml";
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("author", author);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboHealthProvider", tmpName).generator(m);
    }

    private void generateDubboApp(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath, String author) {
        String tmpName = "ddd/springboot_dubbo_app_class.tml";
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("author", author);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboHealthProviderImpl", tmpName).generator(m);
    }

    private void generateDubboDomain(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath, String author) {
        String tmpName = "ddd/springboot_dubbo_domain_class.tml";
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("author", author);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboConst", tmpName).generator(m);
    }

    private void generateDubboConfig(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        String templateName = "ddd/springboot_dubbo_config.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboConfiguration", templateName);
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }

    private void generateNacosConfig(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        String templateName = "ddd/springboot_nacos_config.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "NacosConfiguration", templateName);
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        classGenerator.generator(m);
    }

    private void generateApiPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "ddd/springboot_pom_api.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        ampom.put("artifactId", projectName + "-api");
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-api", tmpName).generator(ampom);
    }

    private void generateAppPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "ddd/springboot_pom_app.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        ampom.put("artifactId", projectName + "-app");
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-app", tmpName).generator(ampom);
    }

    private void generateDomainPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "ddd/springboot_pom_domain.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        ampom.put("artifactId", projectName + "-domain");
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-domain", tmpName).generator(ampom);
    }

    private void generateInfrastructurePom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "ddd/springboot_pom_infrastructure.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        ampom.put("artifactId", projectName + "-infrastructure");
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-infrastructure", tmpName).generator(ampom);
    }

    private void generateServerPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "ddd/springboot_pom_server.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        ampom.put("artifactId", projectName + "-server");

        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-server", tmpName).generator(ampom);
    }

    private void generageParentPom(Map<String, Object> mpom, String projectPath, String projectName) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "ddd/springboot_pom.tml");
        pomGenerator.generator(mpom);
    }

    private void generageReadMe(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, "README.md", "springboot_readme.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "ddd/springboot_gitignore.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("project_name", projectName);
        mdGenerator.generator(smpom);
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
