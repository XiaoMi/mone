package run.mone.mcp.hammerspoon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import run.mone.mcp.hammerspoon.function.tigertrade.dto.Version;

@SpringBootApplication
@ComponentScan("run.mone.mcp.hammerspoon")
@Slf4j
public class HammerspoonMcpBootstrap {

    public static void main(String[] args) {
        log.info("version:{}", new Version());
        SpringApplication.run(HammerspoonMcpBootstrap.class, args);
    }

}
