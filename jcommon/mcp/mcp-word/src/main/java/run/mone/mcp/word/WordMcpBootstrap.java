package run.mone.mcp.word;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.word.config", "run.mone.mcp.word.function", "run.mone.mcp.word.server", "run.mone.mcp.word.service"})
@Slf4j
public class WordMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(WordMcpBootstrap.class, args);
    }
}