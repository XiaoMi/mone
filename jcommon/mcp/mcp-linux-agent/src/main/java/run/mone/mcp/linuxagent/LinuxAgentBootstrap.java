package run.mone.mcp.linuxagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.linuxagent")
public class LinuxAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(LinuxAgentBootstrap.class, args);
    }
}
