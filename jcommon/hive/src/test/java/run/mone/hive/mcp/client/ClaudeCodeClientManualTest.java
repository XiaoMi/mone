package run.mone.hive.mcp.client;

import lombok.extern.slf4j.Slf4j;

/**
 * 手动测试 ClaudeCodeClient
 * 这个类用于手动测试，不会在自动化测试中运行
 */
@Slf4j
public class ClaudeCodeClientManualTest {

    public static void main(String[] args) {
        log.info("=== ClaudeCodeClient 手动测试 ===");

        ClaudeCodeClient client = new ClaudeCodeClient();


        log.info("\n=== 所有测试完成 ===");
    }

    private static void printResult(String testName, ClaudeCodeClient.ClaudeCodeResult result) {
        log.info("【{}】结果:", testName);
        log.info("  - Success: {}", result.isSuccess());
        log.info("  - Exit Code: {}", result.getExitCode());
        log.info("  - Output Length: {} chars", result.getOutput() != null ? result.getOutput().length() : 0);
        log.info("  - Error Length: {} chars", result.getError() != null ? result.getError().length() : 0);

        if (result.isSuccess()) {
            log.info("  - Output Content:\n{}", result.getOutput());
        } else {
            log.error("  - Error Content:\n{}", result.getError());
        }
    }
}
