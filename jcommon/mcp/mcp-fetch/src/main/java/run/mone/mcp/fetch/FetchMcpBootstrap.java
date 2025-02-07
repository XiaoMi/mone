
package run.mone.mcp.fetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.fetch")
public class FetchMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(FetchMcpBootstrap.class, args);
    }
}
