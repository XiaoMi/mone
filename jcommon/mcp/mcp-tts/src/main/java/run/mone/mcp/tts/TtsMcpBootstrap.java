package run.mone.mcp.tts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 龚文
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.tts")
@Slf4j
public class TtsMcpBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(TtsMcpBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}