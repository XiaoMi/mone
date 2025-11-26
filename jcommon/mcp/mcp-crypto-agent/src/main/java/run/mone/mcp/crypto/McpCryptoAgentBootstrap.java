package run.mone.mcp.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.crypto.crypto")
public class McpCryptoAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(McpCryptoAgentBootstrap.class, args);
    }
}

