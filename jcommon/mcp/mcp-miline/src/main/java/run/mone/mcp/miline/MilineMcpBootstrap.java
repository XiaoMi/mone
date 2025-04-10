
package run.mone.mcp.miline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.miline")
public class MilineMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MilineMcpBootstrap.class, args);
    }
}
