package run.mone.mcp.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.file", "run.mone.hive.mcp.service"})
@Slf4j
public class FileMcpBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(FileMcpBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}