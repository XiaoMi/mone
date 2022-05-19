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
import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.youpin.codegen.bo.Constants.DEP_API;
import static com.xiaomi.youpin.codegen.bo.Constants.DEP_APP;
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
        pomMap.put("api_module", projectName + "-api");
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
            new DirectoryGenerator(projectPath, projectName, projectName + "-app" + srcPath + packagePath+"/health").generator();
            ArrayList<Dependency> appDepList = dep.get(DEP_APP);
            generateAppPom(pomMap, projectPath, projectName, appDepList);
            new FileGenerator(projectPath, projectName, projectName + "-app/" + srcPath + packagePath + "/package-info.java", "cn-sales-crm/package-info.tml").generator(pomMap);
            new FileGenerator(projectPath, projectName, projectName + "-app/" + srcPath + packagePath + "/health/DubboHealthServiceImpl.java", "cn-sales-crm/DubboHealthServiceImpl.tml").generator(pomMap);

            // 创建module: api
            new DirectoryGenerator(projectPath, projectName, projectName + "-api" + srcPath + packagePath+"/provider").generator();
            ArrayList<Dependency> clientDepList = dep.get(DEP_API);
            generateAPIPom(pomMap, projectPath, projectName, clientDepList);
            new FileGenerator(projectPath, projectName, projectName + "-api/" + srcPath + packagePath + "/package-info.java", "cn-sales-crm/package-info.tml").generator(pomMap);
            new FileGenerator(projectPath, projectName, projectName + "-api/" + srcPath + packagePath + "/provider/DubboHealthService.java", "cn-sales-crm/DubboHealthService.tml").generator(pomMap);

            // 创建module: domain
            new DirectoryGenerator(projectPath, projectName, projectName + "-domain" + srcPath + packagePath + "/domain/constant").generator();
            ArrayList<Dependency> domainDepList = dep.get(DEP_DOMAIN);
            generateDomainPom(pomMap, projectPath, projectName, domainDepList);
            new FileGenerator(projectPath, projectName, projectName + "-domain/" + srcPath + packagePath + "/domain/package-info.java", "cn-sales-crm/package-info.tml")
                    .generator(ImmutableMap.of("package", packageName + ".domain"));
            new FileGenerator(projectPath, projectName, projectName + "-domain/" + srcPath + packagePath + "/domain/constant/Constant.java", "cn-sales-crm/constant.tml")
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
                    File.separator + srcPath + File.separator + packagePath + File.separator + "common").generator();
            new DirectoryGenerator(projectPath, projectName, "start" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "config").generator();
            new DirectoryGenerator(projectPath, projectName, "start" +
                    File.separator + srcPath + File.separator + packagePath + File.separator + "interceptor").generator();
            ArrayList<Dependency> serverDepList = dep.get(DEP_SERVER);
            generateStartPom(pomMap, projectPath, projectName, serverDepList);
            // 生成入口类
            generateBootstrap(projectPath, projectName, packageName, groupId, packagePath, "start" + File.separator + srcPath);
            // 生成配置文件
            generateResources(projectPath, projectName, projectName);
            generateLogback(projectPath, projectName);
            // 生成config
            generateCommon(projectPath, projectName, packageName, packagePath + File.separator + "common", "start" + File.separator + srcPath);
            generateConfig(projectPath, projectName, packageName, packagePath + File.separator + "config", "start" + File.separator + srcPath);
            generateInterceptor(projectPath, projectName, packageName, packagePath + File.separator + "interceptor", "start" + File.separator + srcPath);
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
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/dev").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/pre").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/prod").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/test").generator();

        // 生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/bootstrap.yml", "cn-sales-crm/bootstrap_yml.tml");
        FileGenerator devGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/dev/bootstrap.yml", "cn-sales-crm/bootstrap-dev_yml.tml");
        FileGenerator preGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/pre/bootstrap.yml", "cn-sales-crm/bootstrap-pre_yml.tml");
        FileGenerator prodGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/prod/bootstrap.yml", "cn-sales-crm/bootstrap-prod_yml.tml");
        FileGenerator testGenerator = new FileGenerator(projectPath, projectName, "start/src/main/resources/test/bootstrap.yml", "cn-sales-crm/bootstrap-test_yml.tml");

        Map<String, Object> m = new HashMap<>(1);
        m.put("artifactId", artifactId);
        fileGenerator.generator(m);
        devGenerator.generator(m);
        preGenerator.generator(m);
        prodGenerator.generator(m);
        testGenerator.generator(m);
    }

    private void generateLogback(String projectPath, String projectName) {
        //生成文件夹
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/dev").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/pre").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/prod").generator();
        new DirectoryGenerator(projectPath, projectName, "start/src/main/resources/test").generator();

        //生成文件
        Map<String, Object> m = new HashMap<>(0);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/logback-spring.xml", "cn-sales-crm/logback-spring-xml.tml").generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/dev/logback-spring.xml", "cn-sales-crm/logback-spring-dev-xml.tml").generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/pre/logback-spring.xml", "cn-sales-crm/logback-spring-pre-xml.tml").generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/prod/logback-spring.xml", "cn-sales-crm/logback-spring-prod-xml.tml").generator(m);
        new FileGenerator(projectPath, projectName, "start/src/main/resources/test/logback-spring.xml", "cn-sales-crm/logback-spring-test-xml.tml").generator(m);
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

    private void generateCommon(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, serviceSrcPath + packagePath).generator();
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "SentinelExceptionHandler", "cn-sales-crm/sentinel_exception_handler.tml").generator(m);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "GlobalExceptionHandler", "cn-sales-crm/global_exception_handler.tml").generator(m);
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
//        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "SentinelExceptionHandler", "cn-sales-crm/sentinel_exception_handler.tml").generator(m);
    }

    private void generateInterceptor(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        // 生成文件夹
        new DirectoryGenerator(projectPath, projectName, serviceSrcPath + packagePath).generator();
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "NotXssInjectionInterceptor", "cn-sales-crm/not_xssInjection_interceptor.tml").generator(m);
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

    private void generateAPIPom(Map<String, Object> mpom, String projectPath, String projectName, ArrayList<Dependency> dep) {
        String tmpName = "cn-sales-crm/springboot_pom_api.tml";
        //生成client module 下的pom文件
        Map<String, Object> ampom = new HashMap<>(mpom);
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
