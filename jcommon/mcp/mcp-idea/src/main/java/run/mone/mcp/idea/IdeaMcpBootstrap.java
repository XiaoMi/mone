
package run.mone.mcp.idea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.idea")
public class IdeaMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(IdeaMcpBootstrap.class, args);
    }
}
