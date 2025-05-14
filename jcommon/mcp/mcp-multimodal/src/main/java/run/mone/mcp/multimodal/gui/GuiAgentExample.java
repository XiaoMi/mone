package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import run.mone.mcp.multimodal.service.GuiAgentService;

/**
 * Example application showing how to use the GUI agent functions
 * Run with VM argument: -Dspring.profiles.active=gui-agent-example
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.multimodal")
public class GuiAgentExample {

    public static void main(String[] args) {
        args = new String[]{"/tmp/v.png",
//                "click 百度一下 按钮"
                "click 文库 标签"
//                "click 搜索框"
        };
        SpringApplication.run(GuiAgentExample.class, args);
    }

    @Component
    @Profile("gui-agent-example")
    @RequiredArgsConstructor
    public static class GuiAgentRunner implements CommandLineRunner {

        private final GuiAgentService guiAgentService;

        private final GuiAgent guiAgent;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void run(String... args) throws Exception {
            if (args.length < 2) {
                System.err.println("Usage: GuiAgentExample <screenshot-path> <instruction>");
                System.err.println("Example: GuiAgentExample ./screenshot.png \"Click on the Settings icon\"");
                return;
            }
            String instruction = args[1];
            guiAgent.run(instruction);
        }
    }
} 