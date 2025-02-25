package run.mone.mcp.pty4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author caobaoyu
 * @description:
 * @date 2025-02-20 17:21
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.pty4j")
public class Pty4jMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Pty4jMcpBootstrap.class, args);
    }

}
