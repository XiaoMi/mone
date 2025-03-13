package run.mone.mcp.dayu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.dayu")
public class DayuMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(DayuMcpBootstrap.class, args);
    }

}
