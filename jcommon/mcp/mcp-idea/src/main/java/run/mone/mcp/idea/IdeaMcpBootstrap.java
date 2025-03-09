
package run.mone.mcp.idea;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import run.mone.mcp.idea.config.Const;

@SpringBootApplication
@ComponentScan("run.mone.mcp.idea")
@Slf4j
public class IdeaMcpBootstrap {

    public static void main(String[] args) {
        try {
            log.info("version:{}", Const.VERSION);
            SpringApplication.run(IdeaMcpBootstrap.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
