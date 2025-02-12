package run.mone.mcp.nacosconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.nacosconfig")
public class NcosConfigMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(NcosConfigMcpBootstrap.class, args);
    }

}
