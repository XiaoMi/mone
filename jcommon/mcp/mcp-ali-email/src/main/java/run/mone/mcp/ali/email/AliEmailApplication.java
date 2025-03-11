package run.mone.mcp.ali.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hobo
 * @description:
 * @date 2025-02-19 17:18
 */
@SpringBootApplication
@ComponentScan({"run.mone.mcp.ali.email"})
public class AliEmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliEmailApplication.class, args);
    }

}
