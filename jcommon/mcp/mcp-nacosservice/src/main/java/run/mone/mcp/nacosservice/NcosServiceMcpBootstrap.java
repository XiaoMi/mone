package run.mone.mcp.nacosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.nacosservice")
public class NcosServiceMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(NcosServiceMcpBootstrap.class, args);
    }

}
