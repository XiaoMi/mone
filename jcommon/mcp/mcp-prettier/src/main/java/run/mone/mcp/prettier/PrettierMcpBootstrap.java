package run.mone.mcp.prettier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.prettier")
public class PrettierMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(PrettierMcpBootstrap.class, args);
    }
}