
package run.mone.mcp.coder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.coder", "run.mone.hive.spring.starter"})
public class CoderAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(CoderAgentBootstrap.class, args);
    }
}
