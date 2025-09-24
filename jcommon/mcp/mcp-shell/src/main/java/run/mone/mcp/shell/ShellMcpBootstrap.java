package run.mone.mcp.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.shell")
public class ShellMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ShellMcpBootstrap.class, args);
    }
}