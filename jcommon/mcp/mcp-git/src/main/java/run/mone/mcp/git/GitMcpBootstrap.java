package run.mone.mcp.git;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.git")
public class GitMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(GitMcpBootstrap.class, args);
    }

}
