package run.mone.mcp.moon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.moon")
public class MoonBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MoonBootstrap.class, args);
    }
}