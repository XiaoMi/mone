
package run.mone.mcp.applescript;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.applescript")
public class AppleScriptMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(AppleScriptMcpBootstrap.class, args);
    }
}
