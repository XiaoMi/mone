
package run.mone.mcp.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.chat")
public class ChatMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ChatMcpBootstrap.class, args);
    }
}
