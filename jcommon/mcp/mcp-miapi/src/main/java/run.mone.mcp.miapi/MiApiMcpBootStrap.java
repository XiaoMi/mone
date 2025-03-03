package run.mone.mcp.miapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("run.mone.mcp.miapi")
public class MiApiMcpBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(MiApiMcpBootStrap.class, args);
    }
}
