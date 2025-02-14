package run.mone.mcp.feishu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.feishu"})
public class FeishuApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeishuApplication.class, args);
    }
} 