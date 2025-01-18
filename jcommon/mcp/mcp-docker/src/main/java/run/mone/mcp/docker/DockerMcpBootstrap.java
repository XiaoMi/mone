
package run.mone.mcp.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.docker")
public class DockerMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(DockerMcpBootstrap.class, args);
    }
}
