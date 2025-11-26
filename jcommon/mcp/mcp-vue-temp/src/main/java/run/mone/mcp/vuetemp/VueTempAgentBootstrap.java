package run.mone.mcp.vuetemp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.vuetemp", "run.mone.hive.mcp.service"})
@EnableScheduling
public class VueTempAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(VueTempAgentBootstrap.class, args);
    }
}
