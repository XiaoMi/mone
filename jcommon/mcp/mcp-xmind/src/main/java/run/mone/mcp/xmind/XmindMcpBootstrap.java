package run.mone.mcp.xmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan("run.mone.mcp.xmind")
@Slf4j
public class XmindMcpBootstrap {
    public static void main(String[] args) {
        log.info("mcp xmind start");
        SpringApplication.run(XmindMcpBootstrap.class, args);
    }
}