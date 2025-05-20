package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import run.mone.mcp.multimodal.service.GuiAgentService;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2025/5/14 13:16
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuiAgent {

    private final GuiAgentService guiAgentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MultimodalService multimodalService;

    public void run(String instruction) {
        //截图
        String imagePath = multimodalService.captureScreenshotWithRobot(null).blockFirst();

        log.info("imagePath:{}", imagePath);

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

            String modelOutput = guiAgentService.run(imagePath, instruction).block();

//                String modelOutput = """
//                        Thought: 我看到搜索框里已经有了"上海一民警受贿200余万获刑"这个关键词，现在只需要点击右边那个蓝色的"百度一下"按钮就能开始搜索了。这个按钮就在搜索框的右侧，很容易找到。
//                        Action: click(start_box='<bbox>637 354 637 354</bbox>')
//                        """;

            System.out.println("\nModel output:\n" + modelOutput);

            // Step 2: Parse the action output
            System.out.println("\nStep 2: Parsing action...");
            String parsedOutput = guiAgentService.parseActionOutput(modelOutput);
            System.out.println("\nParsed action:\n" + parsedOutput);

            // Step 3: Visualize the action
            System.out.println("\nStep 3: Creating visualization...");
            String outputImagePath = path.getParent().resolve("output_" + path.getFileName()).toString();
            //标完红点的图像
            String visualizedPath = guiAgentService.visualizeAction(imagePath, parsedOutput, outputImagePath);
            System.out.println("\nVisualization saved to: " + visualizedPath);

            //判断红点是否在正确的位置 (通过两次确定,让x,y 更准)
            String modelOutput2 = guiAgentService.run(visualizedPath, "我的鼠标现在就是那个红点,你帮我计算下是否在正确的位置上,根据你的判断返回新的click信息   \n原始需求:"+instruction).block();
            parsedOutput = guiAgentService.parseActionOutput(modelOutput2);

            // Step 4: Execute action (if requested)
            JsonNode parsedJson = objectMapper.readTree(parsedOutput);
            String action = parsedJson.get("action").asText("");

            System.out.println("\nStep 4: Action detected: " + action);
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
