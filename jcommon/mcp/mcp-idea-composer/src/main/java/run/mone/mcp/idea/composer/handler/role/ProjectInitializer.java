package run.mone.mcp.idea.composer.handler.role;

import run.mone.hive.roles.Role;
import run.mone.hive.schema.RoleContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目初始化器角色
 * 负责创建项目基础结构和配置
 */
@Slf4j
public class ProjectInitializer extends Role {

    private String projectRoot;

    public ProjectInitializer() {
        super("ProjectInitializer", "负责创建项目基础结构，初始化项目配置，设置依赖管理");
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
        log.info("ProjectInitializer observing...");
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
     * 生成Spring Boot项目的目录结构
     */
    public List<String> generateSpringBootStructure(String basePackage) {
        List<String> paths = new ArrayList<>();
        String basePath = "src/main/java/" + basePackage.replace(".", "/");
        String baseTestPath = "src/test/java/" + basePackage.replace(".", "/");
        
        // 主要源码目录
        paths.add(basePath + "/controller");
        paths.add(basePath + "/service");
        paths.add(basePath + "/service/impl");
        paths.add(basePath + "/repository");
        paths.add(basePath + "/model");
        paths.add(basePath + "/model/entity");
        paths.add(basePath + "/model/dto");
        paths.add(basePath + "/config");
        paths.add(basePath + "/util");
        
        // 资源目录
        paths.add("src/main/resources");
        paths.add("src/main/resources/static");
        paths.add("src/main/resources/templates");
        
        // 测试目录
        paths.add(baseTestPath + "/controller");
        paths.add(baseTestPath + "/service");
        paths.add("src/test/resources");
        
        return paths;
    }

    /**
     * 生成Maven多模块项目的目录结构
     */
    public List<String> generateMultiModuleStructure(String basePackage) {
        List<String> paths = new ArrayList<>();
        
        // 父模块
        paths.add("pom.xml");
        
        // 公共模块
        paths.add("common/src/main/java/" + basePackage.replace(".", "/") + "/common");
        paths.add("common/src/main/resources");
        paths.add("common/pom.xml");
        
        // API模块
        paths.add("api/src/main/java/" + basePackage.replace(".", "/") + "/api");
        paths.add("api/src/main/resources");
        paths.add("api/pom.xml");
        
        // 服务模块
        paths.add("service/src/main/java/" + basePackage.replace(".", "/") + "/service");
        paths.add("service/src/main/resources");
        paths.add("service/pom.xml");
        
        // Web模块
        paths.add("web/src/main/java/" + basePackage.replace(".", "/") + "/web");
        paths.add("web/src/main/resources");
        paths.add("web/src/main/resources/static");
        paths.add("web/src/main/resources/templates");
        paths.add("web/pom.xml");
        
        return paths;
    }

    /**
     * 根据项目类型生成相应的目录结构
     */
    public List<String> generateProjectStructure(String projectType, String basePackage) {
        return switch (projectType.toLowerCase()) {
            case "spring-boot" -> generateSpringBootStructure(basePackage);
            case "maven-multi-module" -> generateMultiModuleStructure(basePackage);
            default -> new ArrayList<>();
        };
    }

    /**
     * 实际创建项目目录
     */
    public void createProjectDirectories(String projectName, String projectType, String basePackage) {
        List<String> paths = generateProjectStructure(projectType, basePackage);
        String projectPath = new File(projectRoot, projectName).getAbsolutePath();
        
        // 创建项目根目录
        new File(projectPath).mkdirs();
        
        // 创建所有子目录
        for (String path : paths) {
            File dir = new File(projectPath, path);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    log.info("创建目录成功：{}", dir.getAbsolutePath());
                } else {
                    log.error("创建目录失败：{}", dir.getAbsolutePath());
                }
            } else {
                log.info("目录已存在：{}", dir.getAbsolutePath());
            }
        }
    }
} 