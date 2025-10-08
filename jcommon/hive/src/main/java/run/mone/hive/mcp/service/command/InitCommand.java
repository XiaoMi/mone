package run.mone.hive.mcp.service.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.schema.Message;

/**
 * Init命令处理类
 * 处理 /init 命令，用于分析代码库并创建MCODE.md文件
 * 对应Claude Code中的/init指令功能
 */
@Slf4j
public class InitCommand extends RoleBaseCommand {

    public InitCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(Message message) {
        if (message == null) {
            return false;
        }
        String content = message.getContent();
        return content != null && content.trim().toLowerCase().startsWith("/init");
    }

    @Override
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        return content.trim().toLowerCase().startsWith("/init");
    }

    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        try {
            sink.next("🔧 正在分析代码库并生成MCODE.md文件...\n");
            
            // 生成init命令的完整提示词
            String initPrompt = generateInitPrompt();
            
            // 创建新的消息，包含生成的提示词
            Message initMessage = Message.builder()
                    .clientId(message.getClientId())
                    .userId(message.getUserId())
                    .agentId(message.getAgentId())
                    .role("user")
                    .sentFrom(message.getSentFrom())
                    .content(initPrompt)
                    .data(message.getData())
                    .build();

            sendMessages(sink,
                "📋 已生成代码库分析指令\n",
                "🔍 开始分析项目结构和配置文件...\n",
                "📝 将创建或更新MCODE.md文件\n\n"
            );

            // 将处理后的消息传递给role处理
            if (role != null) {
                role.putMessage(initMessage);
            } else {
                sendErrorAndComplete(sink, "当前没有可用的Agent来处理init命令");
                return;
            }

            log.info("成功执行init命令, from: {}", from);

        } catch (Exception e) {
            log.error("处理init命令失败: {}", e.getMessage(), e);
            sendErrorAndComplete(sink, "执行init命令失败: " + e.getMessage());
        } finally {
            sink.complete();
        }
    }

    /**
     * 生成Init命令的完整提示词
     */
    private String generateInitPrompt() {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("<explicit_instructions type=\"init\">\n");
        prompt.append("Your task is to analyze this codebase and create a MCODE.md file, which will be given to future instances of Mone Code to operate in this repository.\n\n");
        
        prompt.append("## What to add:\n\n");
        prompt.append("1. **Commands that will be commonly used**, such as how to build, lint, and run tests. Include the necessary commands to develop in this codebase, such as how to run a single test.\n");
        prompt.append("2. **High-level code architecture and structure** so that future instances can be productive more quickly. Focus on the \"big picture\" architecture that requires reading multiple files to understand.\n\n");
        
        prompt.append("## Usage notes:\n\n");
        prompt.append("- If there's already a MCODE.md, suggest improvements to it.\n");
        prompt.append("- When you make the initial MCODE.md, do not repeat yourself and do not include obvious instructions like \"Provide helpful error messages to users\", \"Write unit tests for all new utilities\", \"Never include sensitive information (API keys, tokens) in code or commits\"\n");
        prompt.append("- Avoid listing every component or file structure that can be easily discovered\n");
        prompt.append("- Don't include generic development practices\n");
        prompt.append("- If there are Cursor rules (in .cursor/rules/ or .cursorrules) or Copilot rules (in .github/copilot-instructions.md), make sure to include the important parts.\n");
        prompt.append("- If there is a README.md, make sure to include the important parts.\n");
        prompt.append("- Do not make up information such as \"Common Development Tasks\", \"Tips for Development\", \"Support and Documentation\" unless this is expressly included in other files that you read.\n");
        prompt.append("- Be sure to prefix the file with the following text:\n\n");
        
        prompt.append("```\n");
        prompt.append("# MCODE.md\n\n");
        prompt.append("This file provides guidance to Mone Code (run.mone/code) when working with code in this repository.\n");
        prompt.append("```\n\n");
        
        prompt.append("## Analysis Steps:\n\n");
        prompt.append("1. **First, check if MCODE.md already exists** - if it does, read it and suggest improvements\n");
        prompt.append("2. **Analyze the project structure** - use list_files and read_file tools to understand the codebase\n");
        prompt.append("3. **Look for build and development files** - check for package.json, pom.xml, build.gradle, Makefile, etc.\n");
        prompt.append("4. **Check for existing documentation** - look for README.md, docs/, .cursor/rules/, .cursorrules, .github/copilot-instructions.md\n");
        prompt.append("5. **Analyze key source files** - understand the main architecture and entry points\n");
        prompt.append("6. **Identify common development commands** - build, test, lint, run commands\n");
        prompt.append("7. **Create or update MCODE.md** with the gathered information\n\n");
        
        prompt.append("## Required Tools to Use:\n\n");
        prompt.append("- `list_files` - to explore the project structure\n");
        prompt.append("- `read_file` - to read configuration files, README, and key source files\n");
        prompt.append("- `search_files` - to find specific patterns like build files, test files, etc.\n");
        prompt.append("- `write_to_file` - to create or update the MCODE.md file\n\n");
        
        prompt.append("Start by checking if MCODE.md already exists, then proceed with the analysis.\n");
        prompt.append("</explicit_instructions>\n");
        
        return prompt.toString();
    }

    @Override
    public String getCommandName() {
        return "/init";
    }

    @Override
    public String getCommandDescription() {
        return "分析代码库并创建MCODE.md文件，帮助未来的Mone Code实例在此仓库中操作";
    }
}
