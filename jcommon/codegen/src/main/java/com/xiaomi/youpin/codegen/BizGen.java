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

/**
 * Biz业务项目代码生成器
 * 用于生成完整的Spring Boot业务项目，包含安全认证、日志、异常处理等基础组件
 *
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
@Slf4j
public class BizGen {

    /**
     * 生成Biz项目并打包（简化版本）
     */
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId,
                                          String packageName, String author, String versionId,
                                          String description) {
        return generateAndZip(projectPath, projectName, groupId, packageName, author, versionId,
                description, "3.2.0", "21", "8080", projectName,
                "ThisIsASecretKeyForJWT1234567890", "86400000");
    }

    /**
     * 生成Biz项目并打包（完整参数版本）
     *
     * @param projectPath       项目生成路径
     * @param projectName       项目名称
     * @param groupId           Maven GroupId
     * @param packageName       包名
     * @param author            作者
     * @param versionId         版本号
     * @param description       项目描述
     * @param springBootVersion Spring Boot版本
     * @param javaVersion       Java版本
     * @param serverPort        服务端口
     * @param dbName            数据库名称
     * @param jwtSecret         JWT密钥
     * @param jwtExpiration     JWT过期时间（毫秒）
     * @return 生成的zip文件路径
     */
    public Result<String> generateAndZip(String projectPath, String projectName, String groupId,
                                          String packageName, String author, String versionId,
                                          String description, String springBootVersion, String javaVersion,
                                          String serverPort, String dbName, String jwtSecret, String jwtExpiration) {

        String srcPath = "/src/main/java/";
        String resourcesPath = "/src/main/resources/";
        String packagePath = packageName.replaceAll("\\.", "/");
        String bootstrapClassName = adapterProjectNameToCamelName(projectName) + "Application";

        try {
            // 创建项目根目录
            DirectoryGenerator rootDir = new DirectoryGenerator(projectPath, projectName, "");
            rootDir.generator();

            // 创建包目录结构
            createPackageStructure(projectPath, projectName, srcPath, packagePath);

            // 创建resources目录
            DirectoryGenerator resourcesDir = new DirectoryGenerator(projectPath, projectName, resourcesPath);
            resourcesDir.generator();

            // 创建.hive目录
            DirectoryGenerator hiveDir = new DirectoryGenerator(projectPath, projectName, ".hive");
            hiveDir.generator();

            // 生成pom.xml
            generatePom(projectPath, projectName, groupId, versionId, description, springBootVersion, javaVersion);

            // 生成Bootstrap启动类
            generateBootstrap(projectPath, projectName, packageName, author, packagePath, srcPath, bootstrapClassName);

            // 生成配置类
            generateConfigs(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成过滤器
            generateFilters(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成工具类
            generateUtils(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成DTO
            generateDtos(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成AOP切面
            generateAops(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成异常处理
            generateExceptions(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Security相关类
            generateSecurityClasses(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Model
            generateModels(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Repository
            generateRepositories(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Service
            generateServices(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Annotation
            generateAnnotations(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Resolver
            generateResolvers(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成Controller
            generateControllers(projectPath, projectName, packageName, author, packagePath, srcPath);

            // 生成配置文件
            generateApplicationProperties(projectPath, projectName, resourcesPath, serverPort, dbName, jwtSecret, jwtExpiration);
            generateLogback(projectPath, projectName, resourcesPath);

            // 生成文档
            generateReadme(projectPath, projectName, versionId, author, description, bootstrapClassName,
                    packageName, groupId, dbName, serverPort, jwtSecret, jwtExpiration, springBootVersion, javaVersion);

            // 生成Hive Agent配置
            generateHiveAgentConfigs(projectPath, projectName, javaVersion, packageName);

            // 打包成zip
            FileUtils.compress(projectPath + File.separator + projectName,
                    projectPath + File.separator + projectName + ".zip");

            log.info("Biz project generated successfully: {}", projectName);

        } catch (Exception e) {
            log.error("BizGen failed for project: " + projectName, e);
            return Result.fail(GeneralCodes.InternalError, "InternalError: " + e.getMessage());
        }

        return Result.success(projectPath + File.separator + projectName + ".zip");
    }

    /**
     * 创建包目录结构
     */
    private void createPackageStructure(String projectPath, String projectName, String srcPath, String packagePath) {
        String[] dirs = {"config", "filter", "util", "dto", "aop", "exception",
                "controller", "service", "repository", "model", "security", "annotation", "resolver"};
        for (String dir : dirs) {
            DirectoryGenerator dirGen = new DirectoryGenerator(projectPath, projectName,
                    srcPath + packagePath + "/" + dir);
            dirGen.generator();
        }
    }

    /**
     * 生成pom.xml
     */
    private void generatePom(String projectPath, String projectName, String groupId, String versionId,
                              String description, String springBootVersion, String javaVersion) {
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "biz/pom_xml.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("groupId", groupId);
        m.put("artifactId", projectName);
        m.put("version", versionId);
        m.put("projectName", projectName);
        m.put("description", description);
        m.put("springBootVersion", springBootVersion);
        m.put("javaVersion", javaVersion);
        pomGenerator.generator(m);
    }

    /**
     * 生成Bootstrap启动类
     */
    private void generateBootstrap(String projectPath, String projectName, String packageName,
                                     String author, String packagePath, String srcPath,
                                     String bootstrapClassName) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath, bootstrapClassName, "biz/bootstrap.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("bootstrapClassName", bootstrapClassName);
        classGenerator.generator(m);
    }

    /**
     * 生成配置类
     */
    private void generateConfigs(String projectPath, String projectName, String packageName,
                                   String author, String packagePath, String srcPath) {
        String configPath = packagePath + "/config";
        String date = getCurrentDate();

        // SecurityConfig
        ClassGenerator securityConfig = new ClassGenerator(projectPath, projectName, srcPath,
                configPath, "SecurityConfig", "biz/config/security_config.tml");
        Map<String, Object> m1 = new HashMap<>();
        m1.put("package", packageName);
        m1.put("author", author);
        m1.put("date", date);
        securityConfig.generator(m1);

        // WebMvcConfig
        ClassGenerator webMvcConfig = new ClassGenerator(projectPath, projectName, srcPath,
                configPath, "WebMvcConfig", "biz/config/web_mvc_config.tml");
        Map<String, Object> m2 = new HashMap<>();
        m2.put("package", packageName);
        m2.put("author", author);
        m2.put("date", date);
        webMvcConfig.generator(m2);

        // AppConfig
        ClassGenerator appConfig = new ClassGenerator(projectPath, projectName, srcPath,
                configPath, "AppConfig", "biz/config/app_config.tml");
        Map<String, Object> m3 = new HashMap<>();
        m3.put("package", packageName);
        m3.put("author", author);
        m3.put("date", date);
        appConfig.generator(m3);
    }

    /**
     * 生成过滤器
     */
    private void generateFilters(String projectPath, String projectName, String packageName,
                                   String author, String packagePath, String srcPath) {
        ClassGenerator filter = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/filter", "JwtAuthenticationFilter", "biz/filter/jwt_authentication_filter.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        filter.generator(m);
    }

    /**
     * 生成工具类
     */
    private void generateUtils(String projectPath, String projectName, String packageName,
                                 String author, String packagePath, String srcPath) {
        ClassGenerator util = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/util", "JwtUtil", "biz/util/jwt_util.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        util.generator(m);
    }

    /**
     * 生成DTO
     */
    private void generateDtos(String projectPath, String projectName, String packageName,
                               String author, String packagePath, String srcPath) {
        String dtoPath = packagePath + "/dto";
        String date = getCurrentDate();
        
        String[] dtos = {
            "ApiResponse:biz/dto/api_response.tml",
            "UserDTO:biz/dto/user_dto.tml",
            "RegisterRequest:biz/dto/register_request.tml",
            "LoginRequest:biz/dto/login_request.tml",
            "AuthResponse:biz/dto/auth_response.tml",
            "UpdateUserRequest:biz/dto/update_user_request.tml"
        };
        
        for (String dto : dtos) {
            String[] parts = dto.split(":");
            ClassGenerator generator = new ClassGenerator(projectPath, projectName, srcPath,
                    dtoPath, parts[0], parts[1]);
            Map<String, Object> m = new HashMap<>();
            m.put("package", packageName);
            m.put("author", author);
            m.put("date", date);
            generator.generator(m);
        }
    }

    /**
     * 生成AOP切面
     */
    private void generateAops(String projectPath, String projectName, String packageName,
                               String author, String packagePath, String srcPath) {
        String aopPath = packagePath + "/aop";
        String date = getCurrentDate();

        // HttpLoggingAspect
        ClassGenerator loggingAspect = new ClassGenerator(projectPath, projectName, srcPath,
                aopPath, "HttpLoggingAspect", "biz/aop/http_logging_aspect.tml");
        Map<String, Object> m1 = new HashMap<>();
        m1.put("package", packageName);
        m1.put("author", author);
        m1.put("date", date);
        loggingAspect.generator(m1);

        // ExceptionHandlingAspect
        ClassGenerator exceptionAspect = new ClassGenerator(projectPath, projectName, srcPath,
                aopPath, "ExceptionHandlingAspect", "biz/aop/exception_handling_aspect.tml");
        Map<String, Object> m2 = new HashMap<>();
        m2.put("package", packageName);
        m2.put("author", author);
        m2.put("date", date);
        exceptionAspect.generator(m2);
    }

    /**
     * 生成异常处理
     */
    private void generateExceptions(String projectPath, String projectName, String packageName,
                                      String author, String packagePath, String srcPath) {
        ClassGenerator exception = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/exception", "GlobalExceptionHandler", "biz/exception/global_exception_handler.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        exception.generator(m);
    }

    /**
     * 生成application.properties
     */
    private void generateApplicationProperties(String projectPath, String projectName,
                                                 String resourcesPath, String serverPort,
                                                 String dbName, String jwtSecret, String jwtExpiration) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                resourcesPath + "application.properties", "biz/application_properties.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("serverPort", serverPort);
        m.put("dbName", dbName);
        m.put("jwtSecret", jwtSecret);
        m.put("jwtExpiration", jwtExpiration);
        fileGenerator.generator(m);
    }

    /**
     * 生成logback.xml
     */
    private void generateLogback(String projectPath, String projectName, String resourcesPath) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                resourcesPath + "logback.xml", "biz/logback.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("projectName", projectName);
        fileGenerator.generator(m);
    }

    /**
     * 生成README.md
     */
    private void generateReadme(String projectPath, String projectName, String version, String author,
                                 String description, String bootstrapClassName, String packageName,
                                 String groupId, String dbName, String serverPort, String jwtSecret,
                                 String jwtExpiration, String springBootVersion, String javaVersion) {
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName,
                "README.md", "biz/readme.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("projectName", projectName);
        m.put("version", version);
        m.put("author", author);
        m.put("description", description);
        m.put("bootstrapClassName", bootstrapClassName);
        m.put("package", packageName);
        m.put("artifactId", projectName);
        m.put("dbName", dbName);
        m.put("serverPort", serverPort);
        m.put("jwtSecret", jwtSecret);
        m.put("jwtExpiration", jwtExpiration);
        m.put("springBootVersion", springBootVersion);
        m.put("javaVersion", javaVersion);
        fileGenerator.generator(m);
    }

    /**
     * 生成Security相关类
     */
    private void generateSecurityClasses(String projectPath, String projectName, String packageName,
                                           String author, String packagePath, String srcPath) {
        ClassGenerator customUserDetails = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/security", "CustomUserDetails", "biz/security/custom_user_details.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        customUserDetails.generator(m);
    }

    /**
     * 生成Model类
     */
    private void generateModels(String projectPath, String projectName, String packageName,
                                  String author, String packagePath, String srcPath) {
        ClassGenerator user = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/model", "User", "biz/model/user.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        user.generator(m);
    }

    /**
     * 生成Repository接口
     */
    private void generateRepositories(String projectPath, String projectName, String packageName,
                                        String author, String packagePath, String srcPath) {
        ClassGenerator userRepo = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/repository", "UserRepository", "biz/repository/user_repository.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        userRepo.generator(m);
    }

    /**
     * 生成Service类
     */
    private void generateServices(String projectPath, String projectName, String packageName,
                                    String author, String packagePath, String srcPath) {
        String servicePath = packagePath + "/service";
        String date = getCurrentDate();
        
        String[] services = {
            "CustomUserDetailsService:biz/service/custom_user_details_service.tml",
            "UserService:biz/service/user_service.tml"
        };
        
        for (String service : services) {
            String[] parts = service.split(":");
            ClassGenerator generator = new ClassGenerator(projectPath, projectName, srcPath,
                    servicePath, parts[0], parts[1]);
            Map<String, Object> m = new HashMap<>();
            m.put("package", packageName);
            m.put("author", author);
            m.put("date", date);
            generator.generator(m);
        }
    }

    /**
     * 生成Annotation注解
     */
    private void generateAnnotations(String projectPath, String projectName, String packageName,
                                       String author, String packagePath, String srcPath) {
        ClassGenerator authUser = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/annotation", "AuthUser", "biz/annotation/auth_user.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        authUser.generator(m);
    }

    /**
     * 生成Resolver解析器
     */
    private void generateResolvers(String projectPath, String projectName, String packageName,
                                     String author, String packagePath, String srcPath) {
        ClassGenerator resolver = new ClassGenerator(projectPath, projectName, srcPath,
                packagePath + "/resolver", "UserArgumentResolver", "biz/resolver/user_argument_resolver.tml");
        Map<String, Object> m = new HashMap<>();
        m.put("package", packageName);
        m.put("author", author);
        m.put("date", getCurrentDate());
        resolver.generator(m);
    }

    /**
     * 生成Controller控制器
     */
    private void generateControllers(String projectPath, String projectName, String packageName,
                                       String author, String packagePath, String srcPath) {
        String controllerPath = packagePath + "/controller";
        String date = getCurrentDate();
        
        String[] controllers = {
            "HealthController:biz/controller/health_controller.tml",
            "UserController:biz/controller/user_controller.tml"
        };
        
        for (String controller : controllers) {
            String[] parts = controller.split(":");
            ClassGenerator generator = new ClassGenerator(projectPath, projectName, srcPath,
                    controllerPath, parts[0], parts[1]);
            Map<String, Object> m = new HashMap<>();
            m.put("package", packageName);
            m.put("author", author);
            m.put("date", date);
            generator.generator(m);
        }
    }

    /**
     * 生成Hive Agent配置文件
     */
    private void generateHiveAgentConfigs(String projectPath, String projectName,
                                            String javaVersion, String packageName) {
        Map<String, Object> m = new HashMap<>();
        m.put("javaVersion", javaVersion);
        m.put("package", packageName);
        m.put("projectName", projectName);

        // 全栈开发助手
        FileGenerator agentGen = new FileGenerator(projectPath, projectName,
                ".hive/agent.md", "biz/hive/agent.tml");
        agentGen.generator(m);

        // 后端开发助手
        FileGenerator backendGen = new FileGenerator(projectPath, projectName,
                ".hive/backend-agent.md", "biz/hive/backend-agent.tml");
        backendGen.generator(m);

        // 前端开发助手
        FileGenerator frontendGen = new FileGenerator(projectPath, projectName,
                ".hive/frontend-agent.md", "biz/hive/frontend-agent.tml");
        frontendGen.generator(new HashMap<>());
    }

    /**
     * 将项目名转换为驼峰命名
     */
    private String adapterProjectNameToCamelName(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        try {
            String[] strings = name.split("-");
            if (strings.length > 1) {
                StringBuilder res = new StringBuilder();
                for (String str : strings) {
                    if (!StringUtils.isEmpty(str)) {
                        res.append(StringUtils.capitalize(str));
                    }
                }
                return res.toString();
            }
        } catch (Exception e) {
            return StringUtils.capitalize(name);
        }
        return StringUtils.capitalize(name);
    }

    /**
     * 获取当前日期
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(new Date());
    }

    /**
     * 主函数，用于测试
     */
    public static void main(String[] args) {
        BizGen bizGen = new BizGen();

        // 测试参数
        String projectPath = "/tmp/biz-test";
        String projectName = "my-shop";
        String groupId = "run.mone";
        String packageName = "run.mone.shop";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String description = "My E-commerce System";

        Result<String> result = bizGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId, description
        );

        if (result.getCode() == 0) {
            System.out.println("✅ Biz项目生成成功！");
            System.out.println("📦 生成文件位置: " + result.getData());
            System.out.println("\n🎯 生成的项目包含：");
            System.out.println("  ✓ Spring Boot 基础框架");
            System.out.println("  ✓ Spring Security + JWT 认证");
            System.out.println("  ✓ JPA + H2 数据库");
            System.out.println("  ✓ 用户认证体系（User, UserRepository, CustomUserDetailsService, UserService）");
            System.out.println("  ✓ 完整的配置类（Security, WebMvc, App）");
            System.out.println("  ✓ JWT 认证过滤器和工具类");
            System.out.println("  ✓ 统一 API 响应格式");
            System.out.println("  ✓ 完整的用户DTO（UserDTO, RegisterRequest, LoginRequest等）");
            System.out.println("  ✓ 用户控制器（HealthController, UserController）");
            System.out.println("  ✓ @AuthUser注解和参数解析器");
            System.out.println("  ✓ HTTP 日志记录切面");
            System.out.println("  ✓ 全局异常处理");
            System.out.println("  ✓ Logback 日志配置");
            System.out.println("  ✓ Hive Agent 配置（全栈/后端/前端）");
        } else {
            System.err.println("❌ Biz项目生成失败: " + result.getMessage());
        }
    }
}

