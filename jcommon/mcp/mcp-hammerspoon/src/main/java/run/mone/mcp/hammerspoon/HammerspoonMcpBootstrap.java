package run.mone.mcp.hammerspoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.hammerspoon")
public class HammerspoonMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(HammerspoonMcpBootstrap.class, args);
    }

}
