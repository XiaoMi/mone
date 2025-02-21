package run.mone.mcp.terminal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.terminal")
public class McpTerminalApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpTerminalApplication.class, args);
	}

}
