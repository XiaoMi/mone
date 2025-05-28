package run.mone.mcp.idea.composer.handler.role;

import run.mone.hive.roles.Role;
import run.mone.hive.schema.RoleContext;
import run.mone.hive.llm.LLM;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目架构师角色
 * 负责分析项目需求和定义项目结构
 */
@Slf4j
public class ProjectArchitect extends Role {

    public ProjectArchitect() {
        super("ProjectArchitect", "负责分析项目需求，定义项目结构，选择技术栈");
    }

    @Override
    protected void init() {
        super.init();
        this.rc.setReactMode(RoleContext.ReactMode.BY_ORDER);
    }

    @Override
    protected int observe() {
        log.info("ProjectArchitect observing...");
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
     * 分析用户需求，提取项目信息
     */
    public JsonObject analyzeRequirement(String requirement, LLM llm) {
        // 使用LLM分析需求
        String prompt = """
            作为一个项目架构师，请分析以下项目需求，并提取关键信息：
            
            需求：%s
            
            请以JSON格式返回以下信息：
            1. projectName: 项目名称（英文，小写，用横线分隔）
            2. projectType: 项目类型（spring-boot 或 maven-multi-module）
            3. basePackage: 基础包名（如：com.example.demo）
            4. description: 项目描述
            
            只返回JSON，不要其他任何内容。
            """.formatted(requirement);
        
        String response = llm.chat(prompt);
        
        // 解析JSON响应
        try {
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            log.error("解析LLM响应失败", e);
            // 返回默认配置
            JsonObject defaultInfo = new JsonObject();
            defaultInfo.addProperty("projectName", "demo");
            defaultInfo.addProperty("projectType", "spring-boot");
            defaultInfo.addProperty("basePackage", "com.example.demo");
            defaultInfo.addProperty("description", requirement);
            return defaultInfo;
        }
    }

    /**
     * 分析项目类型，给出结构建议
     */
    public String analyzeProjectType(String projectType) {
        return switch (projectType.toLowerCase()) {
            case "spring-boot" -> """
                建议的Spring Boot项目结构：
                1. 使用标准的Spring Boot项目结构
                2. 采用分层架构：Controller -> Service -> Repository
                3. 使用Spring Boot最新稳定版本
                4. 添加必要的Spring Boot Starter依赖
                """;
            case "maven-multi-module" -> """
                建议的Maven多模块项目结构：
                1. 创建父模块管理依赖版本
                2. 拆分为以下子模块：
                   - common：公共代码和工具类
                   - api：对外接口定义
                   - service：业务逻辑实现
                   - web：Web层和控制器
                3. 使用依赖继承和依赖管理
                """;
            default -> "未知的项目类型：" + projectType;
        };
    }

    /**
     * 根据项目类型建议依赖
     */
    public String suggestDependencies(String projectType) {
        return switch (projectType.toLowerCase()) {
            case "spring-boot" -> """
                建议的Spring Boot项目依赖：
                1. spring-boot-starter-web：Web开发基础依赖
                2. spring-boot-starter-data-jpa：数据访问层依赖
                3. spring-boot-starter-test：测试依赖
                4. lombok：简化代码开发
                5. spring-boot-starter-validation：参数校验
                """;
            case "maven-multi-module" -> """
                建议的Maven多模块项目依赖：
                1. 父模块：
                   - spring-boot-dependencies：依赖版本管理
                2. common模块：
                   - commons-lang3：通用工具类
                   - lombok：代码简化工具
                3. api模块：
                   - swagger-annotations：API文档
                4. service模块：
                   - spring-boot-starter-data-jpa：数据访问
                5. web模块：
                   - spring-boot-starter-web：Web开发
                """;
            default -> "未知的项目类型：" + projectType;
        };
    }
} 