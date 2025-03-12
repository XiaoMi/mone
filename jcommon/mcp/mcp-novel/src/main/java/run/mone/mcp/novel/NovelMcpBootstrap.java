package run.mone.mcp.novel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.novel")
public class NovelMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(NovelMcpBootstrap.class, args);
    }

}
