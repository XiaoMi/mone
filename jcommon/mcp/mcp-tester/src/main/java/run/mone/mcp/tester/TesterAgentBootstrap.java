
package run.mone.mcp.tester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * MCP Tester Agent Bootstrap
 * 用于启动单元测试生成代理服务
 *
 * @date 2025/12/09
 */
@SpringBootApplication
@ComponentScan({"run.mone.mcp.tester", "run.mone.hive.spring.starter"})
public class TesterAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(TesterAgentBootstrap.class, args);
    }
}
