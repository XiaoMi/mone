package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:15
 */
@Slf4j
public class ExecuteTool implements ITool {

    public static final String name = "execute";

    @Override
    public boolean completed() {
        return true;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String description() {
        return """
            A tool for executing system commands or scripts. Use this when the user needs to run a command in the system environment.
            This tool allows for executing commands in the system shell and returning the results. It can be used for tasks like file operations,
            system maintenance, configuration, or any other command-line operations. Use this tool with caution and ensure the commands are safe to execute.
            """;
    }

    @Override
    public String parameters() {
        return """
                - command: (required) The command to execute in the system shell. This should be a valid command that the system can understand and execute.
                - working_directory: (optional) The directory in which to execute the command. If not specified, the current working directory will be used.
                - timeout: (optional) Maximum time in seconds to wait for the command to complete. Default is 60 seconds.
                """;
    }

    @Override
    public String usage() {
        return """
            <execute>
            <command>ls -la</command>
            <working_directory>/tmp</working_directory>
            <timeout>30</timeout>
            </execute>
            """;
    }

    @Override
    public String example() {
        return """
            Example 1: List files in the current directory
            <execute>
            <command>ls -la</command>
            </execute>
            
            Example 2: Run a script with arguments
            <execute>
            <command>./script.sh arg1 arg2</command>
            <working_directory>/path/to/scripts</working_directory>
            </execute>
            """;
    }

    @Override
    public JsonObject execute(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        
        try {
            // 检查必要参数
            if (!inputJson.has("command") || StringUtils.isBlank(inputJson.get("command").getAsString())) {
                log.error("执行命令请求缺少必需的command参数");
                result.addProperty("error", "缺少必需参数'command'");
                return result;
            }
            
            String command = inputJson.get("command").getAsString();
            String workingDirectory = inputJson.has("working_directory") ? 
                    inputJson.get("working_directory").getAsString() : System.getProperty("user.dir");
            int timeout = inputJson.has("timeout") ? 
                    inputJson.get("timeout").getAsInt() : 60; // 默认超时60秒
            
            return executeCommand(command, workingDirectory, timeout);
            
        } catch (Exception e) {
            log.error("执行命令时发生异常", e);
            result.addProperty("error", "执行命令失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 执行系统命令并获取输出
     * 
     * @param command 要执行的命令
     * @param workingDirectory 工作目录
     * @param timeout 超时时间（秒）
     * @return 包含执行结果的JsonObject
     */
    private JsonObject executeCommand(String command, String workingDirectory, int timeout) {
        JsonObject result = new JsonObject();
        Process process = null;
        
        try {
            log.info("执行命令: {}, 工作目录: {}, 超时: {}秒", command, workingDirectory, timeout);
            
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // 根据操作系统设置命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }
            
            // 设置工作目录
            if (StringUtils.isNotBlank(workingDirectory)) {
                File workDir = new File(workingDirectory);
                if (!workDir.exists() || !workDir.isDirectory()) {
                    log.error("指定的工作目录不存在或不是目录: {}", workingDirectory);
                    result.addProperty("error", "工作目录不存在或不是目录: " + workingDirectory);
                    return result;
                }
                processBuilder.directory(workDir);
            }
            
            // 合并标准输出和错误输出
            processBuilder.redirectErrorStream(true);
            
            // 启动进程
            process = processBuilder.start();
            
            // 读取输出
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
            }
            
            // 等待进程完成，设置超时
            boolean completed = process.waitFor(timeout, TimeUnit.SECONDS);
            
            if (!completed) {
                // 超时，强制终止进程
                process.destroyForcibly();
                log.warn("命令执行超时 ({}秒): {}", timeout, command);
                result.addProperty("error", "命令执行超时 (" + timeout + "秒)");
                return result;
            }
            
            // 获取退出代码
            int exitCode = process.exitValue();
            
            // 构建结果
            StringBuilder output = new StringBuilder();
            for (String line : outputLines) {
                output.append(line).append("\n");
            }
            
            result.addProperty("exit_code", exitCode);
            result.addProperty("output", output.toString());
            
            if (exitCode == 0) {
                log.info("命令执行成功，退出代码: {}", exitCode);
            } else {
                log.warn("命令执行完成，但退出代码非零: {}", exitCode);
            }
            
        } catch (IOException e) {
            log.error("执行命令IO异常", e);
            result.addProperty("error", "执行命令IO异常: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("命令执行被中断", e);
            result.addProperty("error", "命令执行被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            // 确保进程被销毁
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        
        return result;
    }
} 