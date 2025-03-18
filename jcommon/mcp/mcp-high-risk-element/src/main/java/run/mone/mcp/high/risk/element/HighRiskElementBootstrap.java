package run.mone.mcp.high.risk.element;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.high.risk.element")
@Slf4j
public class HighRiskElementBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(HighRiskElementBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
