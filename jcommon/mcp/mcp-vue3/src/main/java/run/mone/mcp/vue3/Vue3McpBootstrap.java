package run.mone.mcp.vue3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.vue3")
public class Vue3McpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Vue3McpBootstrap.class, args);
    }
}
