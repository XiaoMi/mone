
package run.mone.mcp.idea.composer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.idea.composer", "run.mone.hive.mcp.service"})
@EnableScheduling
public class IdeaComposerMcpBootstrap {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IdeaComposerMcpBootstrap.class, args);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
