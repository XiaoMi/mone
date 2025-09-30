package run.mone.mcp.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.chat", "run.mone.hive.mcp.service"})
@EnableScheduling
public class ChatAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ChatAgentBootstrap.class, args);
    }
}
