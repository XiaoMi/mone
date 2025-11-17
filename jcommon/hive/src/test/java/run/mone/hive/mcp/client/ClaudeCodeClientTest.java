package run.mone.hive.mcp.client;

import org.junit.Test;

/**
 * ClaudeCodeClient 测试类
 */
public class ClaudeCodeClientTest {



    @Test
    public void testExecuteNonInteractive() {
        System.out.println("===== 测试方法3: 非交互模式 =====");
        ClaudeCodeClient client = new ClaudeCodeClient();
        
        ClaudeCodeClient.ClaudeCodeResult result = client.execute("11+22=?");
        
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Exit Code: " + result.getExitCode());
        System.out.println("Output Length: " + result.getOutput().length());
        System.out.println("Output: " + result.getOutput());
        System.out.println("Error: " + result.getError());
    }


}
