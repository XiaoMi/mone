package run.mone.mcp.idea.composer.handler.role;

import run.mone.hive.roles.Role;
import run.mone.hive.schema.RoleContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目构建者角色
 * 负责生成基础代码和配置文件
 */
@Slf4j
public class ProjectBuilder extends Role {

    public ProjectBuilder() {
        super("ProjectBuilder", "负责生成项目基础代码，创建配置文件，实现基础功能");
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
            return 0;
        }
        
        // 将消息添加到内存中
        this.rc.news.forEach(msg -> this.rc.getMemory().add(msg));
        
        // 清空消息队列
        this.rc.news.clear();
        
        return 1;
    }

    /**
     * 生成Spring Boot项目的基础代码
     */
    public Map<String, String> generateSpringBootCode(String basePackage, String projectName) {
        Map<String, String> files = new HashMap<>();
        
        // 主应用类
        String mainClass = """
            package %s;
            
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            
            @SpringBootApplication
            public class %sApplication {
                public static void main(String[] args) {
                    SpringApplication.run(%sApplication.class, args);
                }
            }
            """.formatted(basePackage, capitalize(projectName), capitalize(projectName));
        files.put(basePackage.replace(".", "/") + "/" + capitalize(projectName) + "Application.java", mainClass);
        
        // 配置文件
        String applicationYml = """
            spring:
              application:
                name: %s
              datasource:
                url: jdbc:mysql://localhost:3306/%s
                username: root
                password: root
                driver-class-name: com.mysql.cj.jdbc.Driver
              jpa:
                hibernate:
                  ddl-auto: update
                show-sql: true
            
            server:
              port: 8080
            """.formatted(projectName, projectName.replace("-", "_"));
        files.put("src/main/resources/application.yml", applicationYml);
        
        // pom.xml
        String pomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
            
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <name>%s</name>
                <description>Spring Boot project</description>
            
                <parent>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-parent</artifactId>
                    <version>2.7.0</version>
                </parent>
            
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
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <scope>runtime</scope>
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
                            <configuration>
                                <excludes>
                                    <exclude>
                                        <groupId>org.projectlombok</groupId>
                                        <artifactId>lombok</artifactId>
                                    </exclude>
                                </excludes>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """.formatted(basePackage, projectName, projectName);
        files.put("pom.xml", pomXml);
        
        return files;
    }

    /**
     * 生成Maven多模块项目的基础代码
     */
    public Map<String, String> generateMultiModuleCode(String basePackage, String projectName) {
        Map<String, String> files = new HashMap<>();
        
        // 父模块pom.xml
        String parentPom = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
            
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <packaging>pom</packaging>
            
                <modules>
                    <module>common</module>
                    <module>api</module>
                    <module>service</module>
                    <module>web</module>
                </modules>
            
                <properties>
                    <java.version>17</java.version>
                    <spring-boot.version>2.7.0</spring-boot.version>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                </properties>
            
                <dependencyManagement>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-dependencies</artifactId>
                            <version>${spring-boot.version}</version>
                            <type>pom</type>
                            <scope>import</scope>
                        </dependency>
                    </dependencies>
                </dependencyManagement>
            
                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-compiler-plugin</artifactId>
                            <version>3.8.1</version>
                            <configuration>
                                <source>${java.version}</source>
                                <target>${java.version}</target>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """.formatted(basePackage, projectName);
        files.put("pom.xml", parentPom);
        
        // 子模块pom.xml模板
        String moduleTemplate = """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
            
                <parent>
                    <groupId>%s</groupId>
                    <artifactId>%s</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </parent>
            
                <artifactId>%s-%s</artifactId>
            
                <dependencies>
                    <!-- 子模块特定依赖 -->
                </dependencies>
            </project>
            """;
        
        // 为每个子模块生成pom.xml
        String[] modules = {"common", "api", "service", "web"};
        for (String module : modules) {
            files.put(module + "/pom.xml", moduleTemplate.formatted(basePackage, projectName, projectName, module));
        }
        
        return files;
    }

    /**
     * 根据项目类型生成相应的代码
     */
    public Map<String, String> generateProjectCode(String projectType, String basePackage, String projectName) {
        return switch (projectType.toLowerCase()) {
            case "spring-boot" -> generateSpringBootCode(basePackage, projectName);
            case "maven-multi-module" -> generateMultiModuleCode(basePackage, projectName);
            default -> new HashMap<>();
        };
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
} 