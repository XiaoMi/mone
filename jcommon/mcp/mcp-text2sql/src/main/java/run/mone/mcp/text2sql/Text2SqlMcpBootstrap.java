package run.mone.mcp.text2sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.text2sql")
public class Text2SqlMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Text2SqlMcpBootstrap.class, args);
    }

}
