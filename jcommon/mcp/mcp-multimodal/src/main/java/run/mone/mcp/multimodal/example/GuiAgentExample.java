package run.mone.mcp.multimodal.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import run.mone.mcp.multimodal.service.GuiAgentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Example application showing how to use the GUI agent functions
 * Run with VM argument: -Dspring.profiles.active=gui-agent-example
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.multimodal")
public class GuiAgentExample {

    public static void main(String[] args) {
        args = new String[]{"/tmp/v.png", "click 百度一下 按钮"};
        SpringApplication.run(GuiAgentExample.class, args);
    }

    @Component
    @Profile("gui-agent-example")
    public static class GuiAgentRunner implements CommandLineRunner {

        @Autowired
        private GuiAgentService guiAgentService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void run(String... args) throws Exception {
            if (args.length < 2) {
                System.err.println("Usage: GuiAgentExample <screenshot-path> <instruction>");
                System.err.println("Example: GuiAgentExample ./screenshot.png \"Click on the Settings icon\"");
                return;
            }

            String imagePath = args[0];
            String instruction = args[1];

            // Check if image exists
            Path path = Paths.get(imagePath);
            if (!Files.exists(path)) {
                System.err.println("Error: Image file not found: " + imagePath);
                return;
            }

            System.out.println("Running GUI agent with:");
            System.out.println("Image: " + imagePath);
            System.out.println("Instruction: " + instruction);
            System.out.println();

            try {
                // Step 1: Analyze the screenshot with instruction
                System.out.println("Step 1: Analyzing screenshot...");

//                String modelOutput = guiAgentService.run(imagePath, instruction).block();

                String modelOutput = """
                        Thought: 我看到搜索框里已经有了"上海一民警受贿200余万获刑"这个关键词，现在只需要点击右边那个蓝色的"百度一下"按钮就能开始搜索了。这个按钮就在搜索框的右侧，很容易找到。
                        Action: click(start_box='<bbox>637 354 637 354</bbox>')
                        """;

                System.out.println("\nModel output:\n" + modelOutput);

                // Step 2: Parse the action output
                System.out.println("\nStep 2: Parsing action...");
                String parsedOutput = guiAgentService.parseActionOutput(modelOutput);
                System.out.println("\nParsed action:\n" + parsedOutput);

                // Step 3: Visualize the action
                System.out.println("\nStep 3: Creating visualization...");
                String outputImagePath = path.getParent().resolve("output_" + path.getFileName()).toString();
                String visualizedPath = guiAgentService.visualizeAction(imagePath, parsedOutput, outputImagePath);
                System.out.println("\nVisualization saved to: " + visualizedPath);

                // Step 4: Execute action (if requested)
                JsonNode parsedJson = objectMapper.readTree(parsedOutput);
                String action = parsedJson.get("action").asText("");

                System.out.println("\nStep 4: Action detected: " + action);
                System.out.println("To execute this action, confirm with 'yes' or press Enter to skip:");

                System.out.println("\nExecuting action...");

                guiAgentService.executeAction(parsedOutput)
                        .doOnNext(System.out::println)
                        .blockLast();

                System.out.println("\nGUI agent process completed.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 