package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * 生成bash命令但不执行的函数
 * 根据用户需求生成相应的bash命令，返回给用户让其自行决定是否执行
 * 
 * @author goodjava@qq.com
 * @date 2025/1/11
 */
@Slf4j
public class BashCommandGeneratorFunction implements McpFunction {

    @Override
    public String getName() {
        return "generate_bash_command";
    }

    @Override
    public String getDesc() {
        return "根据用户的需求描述生成相应的bash命令，但不执行命令，而是直接返回生成的命令让用户自己决定是否执行。支持各种常见的bash操作，如文件操作、系统管理、网络操作、进程管理等。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "requirement": {
                        "type": "string",
                        "description": "用户的需求描述，描述想要完成的任务或操作"
                    },
                    "operating_system": {
                        "type": "string",
                        "description": "操作系统类型，如 linux, macos, windows 等，可选参数，默认为 linux",
                        "default": "linux"
                    },
                    "include_explanation": {
                        "type": "boolean",
                        "description": "是否包含命令的详细解释说明，默认为 true",
                        "default": true
                    }
                },
                "required": ["requirement"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        try {
            String requirement = (String) arguments.get("requirement");
            String operatingSystem = (String) arguments.getOrDefault("operating_system", "linux");
            boolean includeExplanation = Boolean.parseBoolean(arguments.getOrDefault("include_explanation", "true").toString());

            if (requirement == null || requirement.trim().isEmpty()) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：需求描述不能为空")), true));
            }

            // 生成bash命令
            String bashCommand = generateBashCommand(requirement.trim(), operatingSystem);
            
            // 构建返回结果
            JsonObject result = new JsonObject();
            result.addProperty("type", "bash_command");
            result.addProperty("command", bashCommand);
            result.addProperty("operating_system", operatingSystem);
            result.addProperty("requirement", requirement);
            
            if (includeExplanation) {
                String explanation = generateCommandExplanation(bashCommand, requirement);
                result.addProperty("explanation", explanation);
            }
            
            result.addProperty("warning", "⚠️ 请仔细检查命令后再执行，某些命令可能会对系统造成不可逆的影响");

            System.out.println("Generated bash command for requirement: " + requirement + ", command: " + bashCommand);

            return Flux.create(sink -> {
                sink.next(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(formatOutput(result, includeExplanation))), false));
                sink.complete();
            });

        } catch (Exception e) {
            System.err.println("Error generating bash command: " + e.getMessage());
            return Flux.create(sink -> {
                sink.next(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("生成bash命令时发生错误: " + e.getMessage())), true));
                sink.complete();
            });
        }
    }

    /**
     * 根据需求生成bash命令
     */
    private String generateBashCommand(String requirement, String operatingSystem) {
        String normalizedReq = requirement.toLowerCase();
        
        // 文件和目录操作
        if (normalizedReq.contains("创建文件") || normalizedReq.contains("新建文件")) {
            return extractFileCommand(requirement, "touch");
        }
        if (normalizedReq.contains("创建目录") || normalizedReq.contains("新建目录") || normalizedReq.contains("创建文件夹")) {
            return extractDirectoryCommand(requirement, "mkdir -p");
        }
        if (normalizedReq.contains("删除文件")) {
            return extractFileCommand(requirement, "rm");
        }
        if (normalizedReq.contains("删除目录") || normalizedReq.contains("删除文件夹")) {
            return extractDirectoryCommand(requirement, "rm -rf");
        }
        if (normalizedReq.contains("复制文件") || normalizedReq.contains("拷贝文件")) {
            return extractCopyCommand(requirement);
        }
        if (normalizedReq.contains("移动文件") || normalizedReq.contains("重命名文件")) {
            return extractMoveCommand(requirement);
        }
        if (normalizedReq.contains("查看文件") || normalizedReq.contains("显示文件内容")) {
            return extractFileCommand(requirement, "cat");
        }
        if (normalizedReq.contains("查找文件") || normalizedReq.contains("搜索文件")) {
            return extractFindCommand(requirement);
        }
        
        // 权限操作
        if (normalizedReq.contains("修改权限") || normalizedReq.contains("改变权限")) {
            return extractChmodCommand(requirement);
        }
        if (normalizedReq.contains("修改所有者") || normalizedReq.contains("改变所有者")) {
            return extractChownCommand(requirement);
        }
        
        // 进程管理
        if (normalizedReq.contains("查看进程") || normalizedReq.contains("显示进程")) {
            return "ps aux | grep";
        }
        if (normalizedReq.contains("杀死进程") || normalizedReq.contains("终止进程")) {
            return extractKillCommand(requirement);
        }
        
        // 系统信息
        if (normalizedReq.contains("查看磁盘空间") || normalizedReq.contains("磁盘使用")) {
            return "df -h";
        }
        if (normalizedReq.contains("查看内存") || normalizedReq.contains("内存使用")) {
            return "free -h";
        }
        if (normalizedReq.contains("查看系统信息")) {
            return "uname -a";
        }
        
        // 网络操作
        if (normalizedReq.contains("ping") || normalizedReq.contains("测试连接")) {
            return extractPingCommand(requirement);
        }
        if (normalizedReq.contains("下载") && (normalizedReq.contains("wget") || normalizedReq.contains("curl"))) {
            return extractDownloadCommand(requirement);
        }
        
        // 压缩解压
        if (normalizedReq.contains("压缩") || normalizedReq.contains("打包")) {
            return extractCompressCommand(requirement);
        }
        if (normalizedReq.contains("解压") || normalizedReq.contains("解包")) {
            return extractExtractCommand(requirement);
        }
        
        // Git操作
        if (normalizedReq.contains("git") && normalizedReq.contains("克隆")) {
            return extractGitCloneCommand(requirement);
        }
        if (normalizedReq.contains("git") && normalizedReq.contains("状态")) {
            return "git status";
        }
        if (normalizedReq.contains("git") && normalizedReq.contains("提交")) {
            return "git add . && git commit -m \"update\"";
        }
        
        // 默认返回一个通用的帮助提示
        return generateGenericCommand(requirement);
    }

    /**
     * 生成命令解释
     */
    private String generateCommandExplanation(String command, String requirement) {
        if (command.startsWith("touch")) {
            return "使用touch命令创建空文件，如果文件已存在则更新其时间戳";
        }
        if (command.startsWith("mkdir")) {
            return "使用mkdir命令创建目录，-p参数表示递归创建父目录";
        }
        if (command.startsWith("rm -rf")) {
            return "使用rm命令删除目录，-r表示递归删除，-f表示强制删除";
        }
        if (command.startsWith("rm")) {
            return "使用rm命令删除文件";
        }
        if (command.startsWith("cp")) {
            return "使用cp命令复制文件或目录";
        }
        if (command.startsWith("mv")) {
            return "使用mv命令移动或重命名文件";
        }
        if (command.startsWith("cat")) {
            return "使用cat命令显示文件内容";
        }
        if (command.startsWith("find")) {
            return "使用find命令查找文件或目录";
        }
        if (command.startsWith("chmod")) {
            return "使用chmod命令修改文件或目录权限";
        }
        if (command.startsWith("chown")) {
            return "使用chown命令修改文件或目录所有者";
        }
        if (command.startsWith("ps")) {
            return "使用ps命令查看系统进程信息";
        }
        if (command.startsWith("kill")) {
            return "使用kill命令终止指定进程";
        }
        if (command.startsWith("df")) {
            return "使用df命令查看磁盘空间使用情况，-h参数表示人性化显示";
        }
        if (command.startsWith("free")) {
            return "使用free命令查看内存使用情况，-h参数表示人性化显示";
        }
        if (command.startsWith("ping")) {
            return "使用ping命令测试网络连接";
        }
        if (command.startsWith("wget") || command.startsWith("curl")) {
            return "使用wget或curl命令下载文件";
        }
        if (command.startsWith("tar")) {
            return "使用tar命令进行文件压缩或解压操作";
        }
        if (command.startsWith("git")) {
            return "使用git命令进行版本控制操作";
        }
        
        return "根据您的需求生成的bash命令";
    }

    // 辅助方法：提取文件相关命令
    private String extractFileCommand(String requirement, String command) {
        // 简单的文件名提取逻辑，实际使用中可以更复杂
        String[] words = requirement.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains(".") && !words[i].startsWith(".")) {
                return command + " " + words[i];
            }
        }
        return command + " <filename>";
    }

    // 辅助方法：提取目录相关命令
    private String extractDirectoryCommand(String requirement, String command) {
        String[] words = requirement.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !word.contains(".") && !isCommonWord(word)) {
                return command + " " + word;
            }
        }
        return command + " <directory>";
    }

    // 其他辅助方法
    private String extractCopyCommand(String requirement) {
        return "cp <source> <destination>";
    }

    private String extractMoveCommand(String requirement) {
        return "mv <source> <destination>";
    }

    private String extractFindCommand(String requirement) {
        return "find . -name \"<filename>\"";
    }

    private String extractChmodCommand(String requirement) {
        return "chmod <permissions> <file>";
    }

    private String extractChownCommand(String requirement) {
        return "chown <user:group> <file>";
    }

    private String extractKillCommand(String requirement) {
        return "kill <pid>";
    }

    private String extractPingCommand(String requirement) {
        return "ping <hostname>";
    }

    private String extractDownloadCommand(String requirement) {
        return "wget <url>";
    }

    private String extractCompressCommand(String requirement) {
        return "tar -czf <archive.tar.gz> <files>";
    }

    private String extractExtractCommand(String requirement) {
        return "tar -xzf <archive.tar.gz>";
    }

    private String extractGitCloneCommand(String requirement) {
        return "git clone <repository-url>";
    }

    private String generateGenericCommand(String requirement) {
        return "# 请提供更具体的需求描述，以便生成准确的bash命令\n# 当前需求: " + requirement;
    }

    private boolean isCommonWord(String word) {
        String[] commonWords = {"创建", "新建", "删除", "文件", "目录", "文件夹", "查看", "显示", "修改", "复制", "移动"};
        for (String common : commonWords) {
            if (word.equals(common)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化输出结果
     */
    private String formatOutput(JsonObject result, boolean includeExplanation) {
        StringBuilder output = new StringBuilder();
        output.append("🔧 **生成的Bash命令**\n\n");
        output.append("**需求**: ").append(result.get("requirement").getAsString()).append("\n\n");
        output.append("**命令**:\n```bash\n").append(result.get("command").getAsString()).append("\n```\n\n");
        
        if (includeExplanation && result.has("explanation")) {
            output.append("**说明**: ").append(result.get("explanation").getAsString()).append("\n\n");
        }
        
        output.append("**操作系统**: ").append(result.get("operating_system").getAsString()).append("\n\n");
        output.append("⚠️ **警告**: ").append(result.get("warning").getAsString()).append("\n\n");
        output.append("请仔细检查命令的正确性，确认无误后再执行。");
        
        return output.toString();
    }
} 