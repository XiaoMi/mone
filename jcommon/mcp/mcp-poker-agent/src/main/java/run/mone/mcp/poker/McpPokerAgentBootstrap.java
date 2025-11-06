package run.mone.mcp.poker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.poker")
public class McpPokerAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(McpPokerAgentBootstrap.class, args);
    }
}
