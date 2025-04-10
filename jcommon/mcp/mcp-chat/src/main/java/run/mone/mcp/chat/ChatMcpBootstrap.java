package run.mone.mcp.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("run.mone.mcp.chat")
@EnableScheduling
public class ChatMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ChatMcpBootstrap.class, args);
    }
}
