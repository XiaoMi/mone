package run.mone.hive.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Init处理器
 * 负责分析代码库并生成MCODE.md文件
 */
public class InitProcessor {
    
    private final LLMTaskProcessor llm;
    private final TaskCallbacks callbacks;
    
    // 常见的构建和配置文件模式
    private static final List<String> BUILD_FILE_PATTERNS = List.of(
        "pom.xml", "build.gradle", "build.gradle.kts", "package.json", "package-lock.json",
        "yarn.lock", "Makefile", "CMakeLists.txt", "Dockerfile", "docker-compose.yml",
        "requirements.txt", "setup.py", "pyproject.toml", "Cargo.toml", "go.mod",
        "composer.json", "Gemfile", "Podfile", "tsconfig.json", "webpack.config.js"
    );
    
    // 常见的文档文件
    private static final List<String> DOC_FILE_PATTERNS = List.of(
        "README.md", "README.rst", "README.txt", "CHANGELOG.md", "CONTRIBUTING.md",
        "LICENSE", "LICENSE.txt", ".cursorrules", ".cursor/rules/", 
        ".github/copilot-instructions.md", "docs/", "documentation/"
    );
    
    
    public InitProcessor(LLMTaskProcessor llm, TaskCallbacks callbacks) {
        this.llm = llm;
        this.callbacks = callbacks;
    }
    
    /**
     * 执行完整的Init流程
     * @param workingDirectory 工作目录
     * @return 创建的MCODE.md文件路径
     */
    public String executeInit(String workingDirectory) {
        try {
            callbacks.say("INIT", "🔍 Starting codebase analysis for MCODE.md generation...");
            
            // Step 1: 检查是否已存在MCODE.md
            callbacks.say("INIT", "📋 Step 1: Checking for existing MCODE.md");
            String existingMcodePath = checkExistingMcode(workingDirectory);
            
            // Step 2: 分析项目结构
            callbacks.say("INIT", "🏗️ Step 2: Analyzing project structure");
            String projectStructure = analyzeProjectStructure(workingDirectory);
            
            // Step 3: 查找构建和配置文件
            callbacks.say("INIT", "⚙️ Step 3: Finding build and configuration files");
            String buildConfigs = findBuildConfigurations(workingDirectory);
            
            // Step 4: 查找现有文档
            callbacks.say("INIT", "📚 Step 4: Finding existing documentation");
            String existingDocs = findExistingDocumentation(workingDirectory);
            
            // Step 5: 分析关键源文件
            callbacks.say("INIT", "🔍 Step 5: Analyzing key source files");
            String sourceAnalysis = analyzeKeySourceFiles(workingDirectory);
            
            // Step 6: 识别开发命令
            callbacks.say("INIT", "⚡ Step 6: Identifying development commands");
            String devCommands = identifyDevelopmentCommands(workingDirectory, buildConfigs);
            
            // Step 7: 生成MCODE.md内容
            callbacks.say("INIT", "📝 Step 7: Generating MCODE.md content");
            String mcodeContent = generateMcodeContent(
                existingMcodePath, projectStructure, buildConfigs, 
                existingDocs, sourceAnalysis, devCommands
            );
            
            // Step 8: 创建或更新MCODE.md文件
            callbacks.say("INIT", "💾 Step 8: Creating/updating MCODE.md file");
            String mcodeFilePath = createMcodeFile(workingDirectory, mcodeContent);
            
            callbacks.say("INIT", "✅ MCODE.md generation completed successfully!");
            callbacks.say("INIT", "📁 MCODE.md saved to: " + mcodeFilePath);
            
            return mcodeFilePath;
            
        } catch (Exception e) {
            callbacks.say("ERROR", "Init process failed: " + e.getMessage());
            throw new InitExecutionException("Init execution failed", e);
        }
    }
    
    /**
     * Step 1: 检查是否已存在MCODE.md
     */
    private String checkExistingMcode(String workingDirectory) {
        Path mcodePath = Paths.get(workingDirectory, "MCODE.md");
        if (Files.exists(mcodePath)) {
            try {
                String existingContent = Files.readString(mcodePath);
                callbacks.say("INIT", "📄 Found existing MCODE.md, will suggest improvements");
                return existingContent;
            } catch (IOException e) {
                callbacks.say("WARNING", "Could not read existing MCODE.md: " + e.getMessage());
            }
        } else {
            callbacks.say("INIT", "📄 No existing MCODE.md found, will create new one");
        }
        return null;
    }
    
    /**
     * Step 2: 分析项目结构
     */
    private String analyzeProjectStructure(String workingDirectory) {
        String structurePrompt = String.format("""
            Analyze the project structure for directory: %s

            Please provide:
            1. Main programming language and framework
            2. Key directories and their purposes
            3. Entry points (main classes, index files, etc.)
            4. Architecture patterns (MVC, microservices, monolith, etc.)
            5. Key modules or components

            Focus on high-level structure that would help a new developer understand the codebase quickly.
            """, workingDirectory);
        
        return llm.sendMessage(structurePrompt);
    }
    
    /**
     * Step 3: 查找构建和配置文件
     */
    private String findBuildConfigurations(String workingDirectory) {
        StringBuilder configs = new StringBuilder();
        configs.append("""
            Build and Configuration Files Found:

            """);
        
        for (String pattern : BUILD_FILE_PATTERNS) {
            String searchPrompt = String.format(
                "Search for files matching pattern '%s' in directory: %s\n" +
                "If found, provide the file path and a brief description of its purpose.",
                pattern, workingDirectory
            );
            
            String result = llm.sendMessage(searchPrompt);
            if (result != null && !result.trim().isEmpty() && !result.contains("not found")) {
                configs.append("- ").append(pattern).append(": ").append(result).append("\n");
            }
        }
        
        return configs.toString();
    }
    
    /**
     * Step 4: 查找现有文档
     */
    private String findExistingDocumentation(String workingDirectory) {
        StringBuilder docs = new StringBuilder();
        docs.append("""
            Existing Documentation Found:

            """);
        
        for (String pattern : DOC_FILE_PATTERNS) {
            String searchPrompt = String.format(
                "Search for files matching pattern '%s' in directory: %s\n" +
                "If found, read the content and provide a summary of key information.",
                pattern, workingDirectory
            );
            
            String result = llm.sendMessage(searchPrompt);
            if (result != null && !result.trim().isEmpty() && !result.contains("not found")) {
                docs.append("- ").append(pattern).append(": ").append(result).append("\n");
            }
        }
        
        return docs.toString();
    }
    
    /**
     * Step 5: 分析关键源文件
     */
    private String analyzeKeySourceFiles(String workingDirectory) {
        String analysisPrompt = String.format("""
            Analyze key source files in directory: %s

            Please identify and analyze:
            1. Main entry points (main methods, index files, app.js, etc.)
            2. Core business logic files
            3. Configuration classes or modules
            4. Key interfaces or abstract classes
            5. Important utility or helper classes

            For each file, provide:
            - File path
            - Brief description of its purpose
            - Key functions or classes it contains
            """, workingDirectory);
        
        return llm.sendMessage(analysisPrompt);
    }
    
    /**
     * Step 6: 识别开发命令
     */
    private String identifyDevelopmentCommands(String workingDirectory, String buildConfigs) {
        String commandsPrompt = String.format("""
            Based on the build configurations found:
            %s

            And the project structure in: %s

            Identify the common development commands for this project:
            1. Build commands (compile, build, package)
            2. Test commands (run all tests, run single test, test coverage)
            3. Lint/format commands (code style, formatting)
            4. Run commands (start application, development server)
            5. Install/setup commands (dependencies, environment setup)
            6. Deploy commands (if applicable)

            Provide the exact commands that developers would use.
            """, buildConfigs, workingDirectory);
        
        return llm.sendMessage(commandsPrompt);
    }
    
    /**
     * Step 7: 生成MCODE.md内容
     */
    private String generateMcodeContent(String existingMcode, String projectStructure, 
                                      String buildConfigs, String existingDocs, 
                                      String sourceAnalysis, String devCommands) {
        
        String contentPrompt = String.format("""
            Generate a comprehensive MCODE.md file based on the following analysis:

            === EXISTING MCODE.md (if any) ===
            %s

            === PROJECT STRUCTURE ===
            %s

            === BUILD CONFIGURATIONS ===
            %s

            === EXISTING DOCUMENTATION ===
            %s

            === SOURCE ANALYSIS ===
            %s

            === DEVELOPMENT COMMANDS ===
            %s

            Create a MCODE.md file that:
            1. Starts with the required header
            2. Includes essential development commands
            3. Provides high-level architecture overview
            4. Incorporates important information from existing docs
            5. Avoids obvious or generic advice
            6. Focuses on project-specific information
            7. If existing MCODE.md exists, suggest improvements rather than repeating

            Format as a complete markdown document ready to be saved as MCODE.md
            """, existingMcode != null ? existingMcode : "None",
            projectStructure, buildConfigs, existingDocs, sourceAnalysis, devCommands);
        
        return llm.sendMessage(contentPrompt);
    }
    
    /**
     * Step 8: 创建MCODE.md文件
     */
    private String createMcodeFile(String workingDirectory, String content) throws IOException {
        Path mcodePath = Paths.get(workingDirectory, "MCODE.md");
        
        // 确保内容以正确的头部开始
        if (!content.startsWith("# MCODE.md")) {
            content = "# MCODE.md\n\n" +
                     "This file provides guidance to Mone Code (run.mone/code) when working with code in this repository.\n\n" +
                     content;
        }
        
        Files.writeString(mcodePath, content);
        return mcodePath.toString();
    }
}