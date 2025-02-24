import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.pty4j.function.Pty4jFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author caobaoyu
 * @description:
 * @date 2025-02-21 09:54
 */
public class Pty4jFunctionTest {
    
    @Test
    void testVimOperations() throws InterruptedException {
        Pty4jFunction pty4jFunction = new Pty4jFunction();
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();
        
        // 1. 先创建文件
        params.put("action", "execute");
        params.put("command", "touch test.txt");
        params.put("sessionId", sessionId);
        
        McpSchema.CallToolResult result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        // 等待文件创建完成
        Thread.sleep(1000);
        
        // 2. 使用vim打开文件
        params.clear();
        params.put("action", "execute");
        params.put("command", "vim test.txt");
        params.put("sessionId", sessionId);
        
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("Vim started: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        // 等待vim完全启动
        Thread.sleep(3000);
        
        // 获取初始状态
        params.clear();
        params.put("action", "getOutput");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        System.out.println("Initial state: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        // 3. 进入插入模式
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", "i");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 4. 输入文本
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", "Hello, this is a test file.\n");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 5. 退出插入模式
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", "\u001b"); // ESC键
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 6. 保存文件
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", ":w\n");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 7. 获取当前输出
        params.clear();
        params.put("action", "getOutput");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("Current buffer: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        // 8. 退出vim
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", ":q\n");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 9. 验证文件内容
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "cat test.txt");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("File content: " + ((McpSchema.TextContent)result.content().get(0)).text());
        assertTrue(((McpSchema.TextContent)result.content().get(0)).text().contains("Hello, this is a test file"));
        
        // 10. 清理测试文件
//        params.clear();
//        params.put("action", "execute");
//        params.put("sessionId", sessionId);
//        params.put("command", "rm test.txt");
//        result = pty4jFunction.apply(params);
//        assertFalse(result.isError());
        
        // 11. 关闭终端会话
        params.clear();
        params.put("action", "close");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
    }

    @Test
    void testTopCommand() throws InterruptedException {
        Pty4jFunction pty4jFunction = new Pty4jFunction();
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();
        
        // 1. 启动 top 命令
        params.put("action", "execute");
        params.put("command", "top");
        params.put("sessionId", sessionId);
        
        McpSchema.CallToolResult result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        // 等待 top 输出
        Thread.sleep(2000);
        
        // 2. 获取输出
        params.clear();
        params.put("action", "getOutput");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        System.out.println("Top output: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        // 3. 发送 q 命令退出
        params.clear();
        params.put("action", "input");
        params.put("sessionId", sessionId);
        params.put("input", "q");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        // 4. 关闭会话
        params.clear();
        params.put("action", "close");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
    }

    @Test
    void testCurlCommand() throws InterruptedException {
        Pty4jFunction pty4jFunction = new Pty4jFunction();
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();
        
        // 1. 执行 curl 命令（使用 httpbin.org 作为测试）
        params.put("action", "execute");
        params.put("command", "curl -s https://httpbin.org/get");
        params.put("sessionId", sessionId);
        
        McpSchema.CallToolResult result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        // 等待响应
        Thread.sleep(2000);
        
        // 2. 获取输出
        params.clear();
        params.put("action", "getOutput");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        String output = ((McpSchema.TextContent)result.content().get(0)).text();
        System.out.println("Curl output: " + output);
        
        // 验证输出包含预期的内容
        assertTrue(output.contains("\"url\": \"https://httpbin.org/get\""));
        
        // 3. 关闭会话
        params.clear();
        params.put("action", "close");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
    }

    @Test
    void testLsAndCdCommands() throws InterruptedException {
        Pty4jFunction pty4jFunction = new Pty4jFunction();
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();
        
        // 1. 创建测试目录结构
        params.put("action", "execute");
        params.put("command", "mkdir -p test_dir/subdir");
        params.put("sessionId", sessionId);
        McpSchema.CallToolResult result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        // 创建一些测试文件
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "touch test_dir/file1.txt test_dir/subdir/file2.txt");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        
        Thread.sleep(1000);
        
        // 2. 列出当前目录
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "ls -la");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("Initial directory listing: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        Thread.sleep(1000);
        
        // 3. 进入测试目录
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "cd test_dir && pwd");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("Current directory: " + ((McpSchema.TextContent)result.content().get(0)).text());
        
        // 4. 列出测试目录内容
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "ls -la");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        String output = ((McpSchema.TextContent)result.content().get(0)).text();
        System.out.println("Test directory listing: " + output);
        
        // 验证输出包含预期的文件

        // 5. 进入子目录
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "cd src && ls -la");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        output = ((McpSchema.TextContent)result.content().get(0)).text();
        System.out.println("Subdirectory listing: " + output);
        

        // 6. 返回上级目录
        params.clear();
        params.put("action", "execute");
        params.put("sessionId", sessionId);
        params.put("command", "cd .. && pwd");
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
        System.out.println("Back to parent directory: " + ((McpSchema.TextContent)result.content().get(0)).text());

        // 8. 关闭会话
        params.clear();
        params.put("action", "close");
        params.put("sessionId", sessionId);
        result = pty4jFunction.apply(params);
        assertFalse(result.isError());
    }
}
