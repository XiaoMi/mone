package run.mone.mcp.idea.composer.handler.role;

import run.mone.hive.roles.Role;
import run.mone.hive.schema.RoleContext;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目构建者角色
 * 负责生成基础代码和配置文件
 */
@Slf4j
public class ProjectBuilder extends Role {

    private String projectRoot;

    public ProjectBuilder() {
        super("ProjectBuilder", "负责生成项目基础代码，创建配置文件，实现基础功能");
        // 默认在当前目录创建项目
        this.projectRoot = System.getProperty("user.dir");
    }

    public void setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
    }

    @Override
    protected void init() {
        super.init();
        this.rc.setReactMode(RoleContext.ReactMode.BY_ORDER);
    }

    @Override
    protected int observe() {
        log.info("ProjectBuilder observing...");
        if (this.rc.news.isEmpty()) {
            log.info("No new messages for ProjectBuilder");
            return 0;
        }
        
        // 将消息添加到内存中
        this.rc.news.forEach(msg -> {
            log.info("ProjectBuilder received message: {}", msg.getContent());
            this.rc.getMemory().add(msg);
            
            // 如果消息是发给ProjectBuilder的，则触发action执行
            if (msg.getSendTo() != null && msg.getSendTo().contains(this.getName())) {
                log.info("Message is for ProjectBuilder, triggering actions");
                this.getActions().forEach(action -> {
                    log.info("Adding action to queue: {}", action.getClass().getSimpleName());
                    this.addTodo(action);
                });
            }
        });
        
        // 清空消息队列
        this.rc.news.clear();
        
        return 1;
    }

    /**
     * 生成项目代码
     */
    public String generateProjectCode(JsonObject projectInfo) {
        StringBuilder result = new StringBuilder();
        String projectName = projectInfo.get("projectName").getAsString();
        String projectType = projectInfo.get("projectType").getAsString();
        String basePackage = projectInfo.get("basePackage").getAsString();
        
        try {
            // 获取项目路径
            String projectPath = new File(projectRoot, projectName).getAbsolutePath();
            
            // 生成pom.xml
            String pomContent = generatePomXml(projectName, projectType, basePackage);
            writeFile(new File(projectPath, "pom.xml"), pomContent);
            result.append("Generated pom.xml\n");
            
            // 生成主应用类
            String mainClassName = toCamelCase(projectName) + "Application";
            String mainClassContent = generateMainClass(mainClassName, basePackage);
            String mainClassPath = String.format("src/main/java/%s/%s.java",
                    basePackage.replace(".", "/"),
                    mainClassName);
            writeFile(new File(projectPath, mainClassPath), mainClassContent);
            result.append("Generated main class: ").append(mainClassPath).append("\n");
            
            // 生成配置文件
            String applicationYml = generateApplicationYml(projectName);
            writeFile(new File(projectPath, "src/main/resources/application.yml"), applicationYml);
            result.append("Generated application.yml\n");
            
            // 生成README.md
            String readmeContent = generateReadme(projectName, projectType);
            writeFile(new File(projectPath, "README.md"), readmeContent);
            result.append("Generated README.md\n");
            
            // 如果是Spring Boot项目，生成基础代码
            if ("spring-boot".equalsIgnoreCase(projectType)) {
                generateSpringBootCode(projectPath, basePackage, result);
            }
            
            log.info("Project code generation completed for: {}", projectName);
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error generating project code", e);
            throw new RuntimeException("Failed to generate project code", e);
        }
    }

    private void writeFile(File file, String content) throws IOException {
        log.info("Writing file: {}", file.getAbsolutePath());
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private String generatePomXml(String projectName, String projectType, String basePackage) {
        return String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <name>%s</name>
                
                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>2.7.0</version>
                </parent>
                
                <properties>
                    <java.version>17</java.version>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
                </properties>
                
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-web</artifactId>
                    </dependency>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-jpa</artifactId>
                    </dependency>
                    <dependency>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <optional>true</optional>
                    </dependency>
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-test</artifactId>
                        <scope>test</scope>
                    </dependency>
                </dependencies>
                
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-maven-plugin</artifactId>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """, basePackage, projectName, projectName);
    }

    private String generateMainClass(String className, String basePackage) {
        return String.format("""
            package %s;
            
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            
            @SpringBootApplication
            public class %s {
                public static void main(String[] args) {
                    SpringApplication.run(%s.class, args);
                }
            }
            """, basePackage, className, className);
    }

    private String generateApplicationYml(String projectName) {
        return String.format("""
            spring:
              application:
                name: %s
              datasource:
                url: jdbc:mysql://localhost:3306/%s?useUnicode=true&characterEncoding=utf8&useSSL=false
                username: root
                password: root
              jpa:
                hibernate:
                  ddl-auto: update
                show-sql: true
            
            server:
              port: 8080
            """, projectName, projectName.replace("-", "_"));
    }

    private String generateReadme(String projectName, String projectType) {
        return String.format("""
            # %s
            
            ## 项目简介
            这是一个基于%s的项目。
            
            ## 技术栈
            - Spring Boot
            - Spring Data JPA
            - MySQL
            - Maven
            
            ## 快速开始
            1. 确保已安装以下环境：
               - JDK 17
               - Maven
               - MySQL
            
            2. 克隆项目到本地
            
            3. 修改`application.yml`中的数据库配置
            
            4. 运行项目
               ```bash
               mvn spring-boot:run
               ```
            
            ## 项目结构
            ```
            src/main/java/
            ├── controller/    # 控制器层
            ├── service/      # 服务层
            ├── repository/   # 数据访问层
            └── model/       # 数据模型
            ```
            """, projectName, projectType);
    }

    private void generateSpringBootCode(String projectPath, String basePackage, StringBuilder result) throws IOException {
        // 生成基础包结构
        String basePath = String.format("src/main/java/%s", basePackage.replace(".", "/"));
        
        // 生成示例实体类
        String entityPath = basePath + "/model/entity/User.java";
        String entityContent = generateUserEntity(basePackage);
        writeFile(new File(projectPath, entityPath), entityContent);
        result.append("Generated entity: ").append(entityPath).append("\n");
        
        // 生成示例Repository
        String repoPath = basePath + "/repository/UserRepository.java";
        String repoContent = generateUserRepository(basePackage);
        writeFile(new File(projectPath, repoPath), repoContent);
        result.append("Generated repository: ").append(repoPath).append("\n");
        
        // 生成示例Service
        String servicePath = basePath + "/service/UserService.java";
        String serviceContent = generateUserService(basePackage);
        writeFile(new File(projectPath, servicePath), serviceContent);
        result.append("Generated service: ").append(servicePath).append("\n");
        
        // 生成示例Controller
        String controllerPath = basePath + "/controller/UserController.java";
        String controllerContent = generateUserController(basePackage);
        writeFile(new File(projectPath, controllerPath), controllerContent);
        result.append("Generated controller: ").append(controllerPath).append("\n");
    }

    private String generateUserEntity(String basePackage) {
        return String.format("""
            package %s.model.entity;
            
            import lombok.Data;
            import javax.persistence.*;
            
            @Data
            @Entity
            @Table(name = "users")
            public class User {
                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;
                
                private String username;
                private String email;
                private String password;
            }
            """, basePackage);
    }

    private String generateUserRepository(String basePackage) {
        return String.format("""
            package %s.repository;
            
            import %s.model.entity.User;
            import org.springframework.data.jpa.repository.JpaRepository;
            
            public interface UserRepository extends JpaRepository<User, Long> {
                User findByUsername(String username);
            }
            """, basePackage, basePackage);
    }

    private String generateUserService(String basePackage) {
        return String.format("""
            package %s.service;
            
            import %s.model.entity.User;
            import %s.repository.UserRepository;
            import lombok.RequiredArgsConstructor;
            import org.springframework.stereotype.Service;
            
            @Service
            @RequiredArgsConstructor
            public class UserService {
                private final UserRepository userRepository;
                
                public User createUser(User user) {
                    return userRepository.save(user);
                }
                
                public User getUserById(Long id) {
                    return userRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                }
            }
            """, basePackage, basePackage, basePackage);
    }

    private String generateUserController(String basePackage) {
        return String.format("""
            package %s.controller;
            
            import %s.model.entity.User;
            import %s.service.UserService;
            import lombok.RequiredArgsConstructor;
            import org.springframework.web.bind.annotation.*;
            
            @RestController
            @RequestMapping("/api/users")
            @RequiredArgsConstructor
            public class UserController {
                private final UserService userService;
                
                @PostMapping
                public User createUser(@RequestBody User user) {
                    return userService.createUser(user);
                }
                
                @GetMapping("/{id}")
                public User getUser(@PathVariable Long id) {
                    return userService.getUserById(id);
                }
            }
            """, basePackage, basePackage, basePackage);
    }

    private String toCamelCase(String input) {
        StringBuilder camelCase = new StringBuilder();
        boolean nextUpper = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                camelCase.append(nextUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                nextUpper = false;
            } else {
                nextUpper = true;
            }
        }
        
        return camelCase.toString();
    }
} 