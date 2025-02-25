package run.mone.mcp.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.file")
public class FileFormatMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(FileFormatMcpBootstrap.class, args);
    }

}
