package run.mone.mcp.codecheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 负责代码安全检查的MCP服务
 * 
 * @author goodjava@qq.com
 */
@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.codecheck", "run.mone.hive.mcp.service"})
@EnableScheduling
public class CodeCheckMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(CodeCheckMcpBootstrap.class, args);
    }
}
