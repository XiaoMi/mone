package run.mone.mcp.time;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class TimeMcpBootstrap {
    public static void main(String[] args) {
        log.info("mcp time start");
        SpringApplication.run(TimeMcpBootstrap.class, args);
    }
}