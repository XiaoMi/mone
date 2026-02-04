package run.mone.mcp.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.gateway", "run.mone.hive.mcp.service"})
@EnableDubbo
public class GatewayMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMcpBootstrap.class, args);
    }
}
