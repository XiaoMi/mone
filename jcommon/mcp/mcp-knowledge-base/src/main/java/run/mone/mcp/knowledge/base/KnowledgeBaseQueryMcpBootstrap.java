package run.mone.mcp.knowledge.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.knowledge.base", "run.mone.hive.mcp.service"})
@Slf4j
public class KnowledgeBaseQueryMcpBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(KnowledgeBaseQueryMcpBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}