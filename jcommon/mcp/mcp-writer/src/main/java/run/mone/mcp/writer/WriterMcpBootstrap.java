
package run.mone.mcp.writer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.writer")
public class WriterMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(WriterMcpBootstrap.class, args);
    }
}
