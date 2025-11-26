package run.mone.mcp.cursor.miapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.cursor.miapi", "run.mone.hive.mcp.service"})
public class MiApiMcpBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(MiApiMcpBootStrap.class, args);
    }
}
