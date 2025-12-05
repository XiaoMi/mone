package run.mone.mcp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.gateway", "run.mone.hive.mcp.service"})
public class GatewayMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMcpBootstrap.class, args);
    }
}
