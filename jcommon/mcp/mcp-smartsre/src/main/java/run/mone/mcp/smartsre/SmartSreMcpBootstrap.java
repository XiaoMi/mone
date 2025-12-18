package run.mone.mcp.smartsre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.smartsre", "run.mone.hive.mcp.service"})
public class SmartSreMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SmartSreMcpBootstrap.class, args);
    }
}
