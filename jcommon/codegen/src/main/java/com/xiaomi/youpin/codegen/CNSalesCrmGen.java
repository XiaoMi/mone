/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.xiaomi.youpin.codegen;

import com.google.common.collect.ImmutableMap;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.youpin.codegen.bo.Constants.DEP_APP;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_CLIENT;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_CONTROLLER;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_DOMAIN;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_INFRASTRUCTURE;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_SERVER;

@Slf4j
public class CNSalesCrmGen {
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
        pomMap.put("app_module", projectName + "-app");
        pomMap.put("client_module", projectName + "-client");
        pomMap.put("controller_module", projectName + "-controller");
        pomMap.put("domain_module", projectName + "-domain");
        pomMap.put("infrastructure_module", projectName + "-infrastructure");
        pomMap.put("start_module", "start");
        pomMap.put("bootstrap", this.adapterProjectNameToCamelName(projectName) + "Application");
        pomMap.put("package", packageName);
        pomMap.put("project", this.adapterProjectNameToCamelName(projectName));
        try {
            // 创建项目
            new DirectoryGenerator(projectPath, projectName, "").generator();
            generageParentPom(pomMap, projectPath, projectName);
            generageReadMe(projectPath, projectName);
            generageGitignore(projectPath, projectName);
            generageMitag(projectPath, projectName);


            // 创建module: controller
            new DirectoryGenerator(projectPath, projectName, projectName + "-controller" + srcPath + packagePath + "/controller").generator();
            ArrayList<Dependency> controllerDepList = dep.get(DEP_CONTROLLER);
            generateControllerPom(pomMap, projectPath, projectName, controllerDepList);
            new FileGenerator(projectPath, projectName, projectName + "-controller/" + srcPath + packagePath + "/controller/package-info.java", "cn-sales-crm/package-info.tml")
                    .generator(ImmutableMap.of("package", packageName + ".controller"));


            // 创建module: app
            new DirectoryGenerator(projectPath, projectName, projectName + "-app" + srcPath + packagePath).generator();
            ArrayList<Dependency> appDepList = dep.get(DEP_APP);
            generateAppPom(pomMap, projectPath, projectName, appDepList);
            new FileGenerator(projectPath, projectName, projectName + "-app/" + srcPath + packagePath + "/package-info.java", "cn-sales-crm/package-info.tml").generator(pomMap);

            // 创建module: client
            new DirectoryGenerator(projectPath, projectName, projectName + "-client" + srcPath + packagePath).generator();
            ArrayList<Dependency> clientDepList = dep.get(DEP_CLIENT);
            generateClientPom(pomMap, projectPath, projectName, clientDepList);
            new FileGenerator(projectPath, projectName, projectName + "-client/" + srcPath + packagePath + "/package-info.java", "cn-sales-crm/package-info.tml").generator(pomMap);

            // 创建module: domain
            new DirectoryGenerator(projectPath, projectName, projectName + "-domain" + srcPath + packagePath + "/domain").generator();
            ArrayList<Dependency> domainDepList = dep.get(DEP_DOMAIN);
            generateDomainPom(pomMap, projectPath, projectName, domainDepList);
            new FileGenerator(projectPath, projectName, projectName + "-domain/" + srcPath + packagePath + "/domain/package-info.java", "cn-sales-crm/package-info.tml")
                    .generator(ImmutableMap.of("package", packageName + ".domain"));

            // 创建module: infrastructure
            new DirectoryGenerator(projectPath, projectName, projectName + "-infrastructure" + srcPath + packagePath).generator();
            ArrayList<Dependency> infrastructureDepList = dep.get(DEP_INFRASTRUCTURE);
            generateInfrastructurePom(pomMap, projectPath, projectName, infrastructureDepList);
            new FileGenerator(projectPath, projectName, projectName + "-infrastructure/" + srcPath + packagePath + "/package-info.java", "cn-sales-crm/package-info.tml").generator(pomMap);

            // 创建module: start
//            new DirectoryGenerator(projectPath, projectName, "start" +
//                    File.separator + srcPath + File.separator + packagePath + File.separator + "filter").generator();
            new DirectoryGenerator(projectPath, projectName, "start" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "config").generator();
            ArrayList<Dependency> serverDepList = dep.get(DEP_SERVER);
            generateStartPom(pomMap, projectPath, projectName, serverDepList);
            // 生成入口类
            generateBootstrap(projectPath, projectName, packageName, groupId, packagePath, "start" + File.separator + srcPath);
            // 生成配置文件
            generateResources(projectPath, projectName, projectName);
            generateLogback(projectPath, projectName);
            // 生成config
            generateConfig(projectPath, projectName, packageName, packagePath + File.separator + "config", "start" + File.separator + srcPath);
            // 生成filter
//            generateFilter(projectPath, projectName, packageName, packagePath + File.separator + "filter", "start" + File.separator + srcPath);
            // 生成test
            generateTest(projectPath, projectName, packageName, packagePath, "start" + File.separator + testPath);


            FileUtils.compress(projectPath + File.separator + projectName, projectPath + File.separator + projectName + ".zip");
        } catch (Exception e) {
            log.error("CNSalesCrmGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    private void generateResources(String projectPath, String projectName, String artifactId) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/prod").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/test").generator();

        // 生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/bootstrap.yml", "cn-sales-crm/bootstrap_yml.tml");
        FileGenerator devGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/prod/bootstrap.yml", "cn-sales-crm/bootstrap-prod_yml.tml");
        FileGenerator stagingGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/test/bootstrap.yml", "cn-sales-crm/bootstrap-test_yml.tml");

        Map<String, Object> m = new HashMap<>(1);
        m.put("artifactId", artifactId);
        fileGenerator.generator(m);
        devGenerator.generator(m);
        stagingGenerator.generator(m);
    }

    private void generateLogback(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "start/src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/logback-spring.xml", "cn-sales-crm/springboot_logback.tml");
        Map<String, Object> m = new HashMap<>(0);
        fileGenerator.generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/prod/logback-spring.xml", "cn-sales-crm/springboot_logback.tml").generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/test/logback-spring.xml", "cn-sales-crm/springboot_logback.tml").generator(m);
    }


    private void generateBootstrap(String projectPath, String projectName, String packageName, String groupId, String packagePath, String serviceSrcPath) {
        String templateName = "cn-sales-crm/springboot_bootstrap_class.tml";
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, this.adapterProjectNameToCamelName(projectName) + "Application", templateName);
//        String[] strs = packageName.split("\\.");
//        String[] nstrs = Arrays.copyOf(strs, strs.length - 1);
        Map<String, Object> m = new HashMap<>(4);
        m.put("package", packageName);
//        m.put("basePackages", String.join(".", nstrs));
        m.put("groupId", groupId);
        m.put("project", this.adapterProjectNameToCamelName(projectName));
        classGenerator.generator(m);
    }

    private void generateConfig(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, serviceSrcPath + packagePath).generator();
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "BeanConfig", "cn-sales-crm/bean_config.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "CustomRedisCacheManager", "cn-sales-crm/custom_redis_cache_manager.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DbConfig", "cn-sales-crm/db_config.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboConfiguration", "cn-sales-crm/dubbo_configuration.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboProperties", "cn-sales-crm/dubbo_properties.tml").generator(m);
//        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "RequestOriginParserDefinition", "cn-sales-crm/request_origin_parser_definition.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "SentinelExceptionHandler", "cn-sales-crm/sentinel_exception_handler.tml").generator(m);
    }

    private void generateFilter(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, serviceSrcPath + packagePath).generator();
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "RpcExceptionFilter", "cn-sales-crm/rpc_exception_filter.tml").generator(m);
    }

    private void generateTest(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, serviceSrcPath + packagePath + "/app").generator();
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        m.put("bootstrap", this.adapterProjectNameToCamelName(projectName) + "Application");
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, this.adapterProjectNameToCamelName(projectName) + "ApplicationTests", "cn-sales-crm/CnSalesCrmDemoApplicationTests.tml").generator(m);
//        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath + "/app", "TestAddrProvider", "cn-sales-crm/TestAddrProvider.tml").generator(m);
//        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath + "/app", "TestRedisProvider", "cn-sales-crm/TestRedisProvider.tml").generator(m);
    }

    private void generateControllerPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "cn-sales-crm/springboot_pom_controller.tml";
        //生成controller module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-controller", tmpName).generator(ampom);
    }

    private void generateClientPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "cn-sales-crm/springboot_pom_client.tml";
        //生成client module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-client", tmpName).generator(ampom);
    }

    private void generateAppPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "cn-sales-crm/springboot_pom_app.tml";
        //生成api module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
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
        String tmpName = "cn-sales-crm/springboot_pom_domain.tml";
        //生成domain module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
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
        String tmpName = "cn-sales-crm/springboot_pom_infrastructure.tml";
        //生成infrastructure module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + projectName + "-infrastructure", tmpName).generator(ampom);
    }

    private void generateStartPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "cn-sales-crm/springboot_pom_start.tml";
        //生成start module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);

        StringBuilder sb = new StringBuilder();
        if (dep != null) {
            for (Dependency d : dep) {
                sb.append(d.toString());
            }
        }
        ampom.put("dependency", sb.toString());
        new PomGenerator(projectPath, projectName + File.separator + "start", tmpName).generator(ampom);
    }

    private void generageParentPom(Map<String, Object> mpom, String projectPath, String projectName) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "cn-sales-crm/springboot_pom.tml");
        pomGenerator.generator(mpom);
    }

    private void generageReadMe(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, "README.md", "cn-sales-crm/readme.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "cn-sales-crm/springboot_gitignore.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("project_name", projectName);
        mdGenerator.generator(smpom);
    }

    private void generageMitag(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, "mitag.sh", "cn-sales-crm/mitag.tml");
        mdGenerator.generator(new HashMap<>());
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
