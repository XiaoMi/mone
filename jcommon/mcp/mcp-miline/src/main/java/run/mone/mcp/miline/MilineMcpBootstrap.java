
package run.mone.mcp.miline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.miline", "run.mone.hive.mcp.service"})
public class MilineMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MilineMcpBootstrap.class, args);
    }
}
