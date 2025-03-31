package run.mone.mcp.knowledge.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.knowledge.base")
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