package run.mone.mcp.idea.composer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.idea.composer", "run.mone.hive.mcp.service"})
@EnableScheduling
public class CodeMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(CodeMcpBootstrap.class, args);
    }
}
