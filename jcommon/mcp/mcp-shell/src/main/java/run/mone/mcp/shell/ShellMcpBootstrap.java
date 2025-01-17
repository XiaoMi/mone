package run.mone.mcp.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mpc.shell")
public class ShellMcpBootstrap {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ShellMcpBootstrap.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}