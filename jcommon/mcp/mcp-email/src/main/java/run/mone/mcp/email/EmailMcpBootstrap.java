package run.mone.mcp.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.email")
@Slf4j
public class EmailMcpBootstrap {
    public static void main(String[] args) {
        log.info("mcp email start");
        SpringApplication.run(EmailMcpBootstrap.class, args);
    }
}