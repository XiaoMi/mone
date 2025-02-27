package run.mone.mcp.ipinfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.ipinfo")
@Slf4j
public class IpinfoMcpBootstrap {
    public static void main(String[] args) {
        log.info("mcp ipinfo start");
        SpringApplication.run(IpinfoMcpBootstrap.class, args);
    }
}