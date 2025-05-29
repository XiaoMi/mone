package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;
import run.mone.mcp.multimodal.service.GuiAgentService;

/**
 * Example application showing how to use the GUI agent functions
 * Run with VM argument: -Dspring.profiles.active=gui-agent-example
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.multimodal")
public class GuiAgentExample {

    @Component
    @RequiredArgsConstructor
    public static class GuiAgentRunner {

        private final GuiAgentService guiAgentService;

        private final GuiAgent guiAgent;

        private final ObjectMapper objectMapper = new ObjectMapper();

        public void run(FluxSink<String> sink, String... args) throws Exception {
            if (args.length < 2) {
                System.err.println("Usage: GuiAgentExample <screenshot-path> <instruction>");
                System.err.println("Example: GuiAgentExample ./screenshot.png \"Click on the Settings icon\"");
                return;
            }
            String instruction = args[1];
            guiAgent.run(instruction, sink);
        }
    }
} 