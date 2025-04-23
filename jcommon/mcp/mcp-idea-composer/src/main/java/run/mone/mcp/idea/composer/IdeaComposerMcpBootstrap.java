
package run.mone.mcp.idea.composer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.idea.composer", "run.mone.hive.mcp.service"})
public class IdeaComposerMcpBootstrap {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IdeaComposerMcpBootstrap.class, args);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
