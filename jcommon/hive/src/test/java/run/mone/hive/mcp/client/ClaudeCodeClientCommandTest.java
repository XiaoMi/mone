package run.mone.hive.mcp.client;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 测试命令构建是否正确
 */
public class ClaudeCodeClientCommandTest {

    @Test
    public void testCommandBuildOrder() throws Exception {
        ClaudeCodeClient client = new ClaudeCodeClient();
        
        // 使用反射访问 private 方法
        Method buildCommandMethod = ClaudeCodeClient.class.getDeclaredMethod(
            "buildCommand", String.class, boolean.class, String.class
        );
        buildCommandMethod.setAccessible(true);
        
        // 测试1: 只有 prompt
        @SuppressWarnings("unchecked")
        List<String> cmd1 = (List<String>) buildCommandMethod.invoke(
            client, "hello", false, null
        );
        System.out.println("测试1 - 只有 prompt:");
        System.out.println("  命令: " + String.join(" ", cmd1));
        System.out.println("  期望: claude -p hello");
        System.out.println();
        
        // 测试2: 带 -c 参数
        @SuppressWarnings("unchecked")
        List<String> cmd2 = (List<String>) buildCommandMethod.invoke(
            client, "hello", true, null
        );
        System.out.println("测试2 - 带 -c 参数:");
        System.out.println("  命令: " + String.join(" ", cmd2));
        System.out.println("  期望: claude -c -p hello");
        System.out.println();
        
        // 测试3: 带 conversation-id
        @SuppressWarnings("unchecked")
        List<String> cmd3 = (List<String>) buildCommandMethod.invoke(
            client, "hello", true, "conv-123"
        );
        System.out.println("测试3 - 带 conversation-id:");
        System.out.println("  命令: " + String.join(" ", cmd3));
        System.out.println("  期望: claude -c --conversation-id conv-123 -p hello");
        System.out.println();
    }

    @Test
    public void testActualExecution() {
        System.out.println("===== 实际执行测试 =====");
        ClaudeCodeClient client = new ClaudeCodeClient();
        
        // 测试简单命令
        System.out.println("\n执行命令: claude -c -p \"hello\"");
        ClaudeCodeClient.ClaudeCodeResult result = client.execute("hello", true, null);
        
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Exit Code: " + result.getExitCode());
        System.out.println("Output Length: " + result.getOutput().length());
        if (result.getOutput().length() > 0) {
            System.out.println("Output: " + result.getOutput());
        }
        if (!result.getError().isEmpty()) {
            System.out.println("Error: " + result.getError());
        }
    }
}
