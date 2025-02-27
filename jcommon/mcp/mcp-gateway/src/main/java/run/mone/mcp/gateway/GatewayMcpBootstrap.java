package run.mone.mcp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.gateway")
public class GatewayMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMcpBootstrap.class, args);
    }

}
