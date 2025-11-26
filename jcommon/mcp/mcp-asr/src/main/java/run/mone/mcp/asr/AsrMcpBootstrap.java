package run.mone.mcp.asr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 龚文
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.asr")
@Slf4j
public class AsrMcpBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(AsrMcpBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}