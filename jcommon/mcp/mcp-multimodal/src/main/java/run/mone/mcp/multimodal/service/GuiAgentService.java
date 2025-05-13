package run.mone.mcp.multimodal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.util.ActionResponseParser;
import run.mone.mcp.multimodal.util.ImageProcessingUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for interacting with the GUI Agent model
 */
@Service
public class GuiAgentService {

    @Autowired
    private LLM llm;

    @Autowired
    private MultimodalService multimodalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gui.agent.systemPrompt:}")
    private String defaultSystemPrompt;

    /**
     * Default system prompt for GUI agent
     */
    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are a GUI agent. You are given a task and your action history, with screenshots. " +
                    "You need to perform the next action to complete the task.\n" +
                    "## Output Format\n```\nThought: ...\nAction: ...\n```\n" +
                    "## Action Space\n" +
                    "click(start_box='[x1, y1, x2, y2]')\n" +
                    "left_double(start_box='[x1, y1, x2, y2]')\n" +
                    "right_single(start_box='[x1, y1, x2, y2]')\n" +
                    "drag(start_box='[x1, y1, x2, y2]', end_box='[x3, y3, x4, y4]')\n" +
                    "hotkey(key='')\n" +
                    "type(content='') #If you want to submit your input, use \"\\n\" at the end of `content`.\n" +
                    "scroll(start_box='[x1, y1, x2, y2]', direction='down or up or right or left')\n" +
                    "wait() #Sleep for 5s and take a screenshot to check for any changes.\n" +
                    "finished(content='xxx') # Use escape characters \\\\', \\\\\", and \\\\n in content part to ensure we can parse the content in normal string format.\n" +
                    "## Note\n" +
                    "- Use Chinese in `Thought` part.\n" +
                    "- Write a small plan and finally summarize your next action (with its target element) in one sentence in `Thought` part.";

    /**
     * Run the GUI agent on an image with user instructions
     *
     * @param imagePath  Path to the screenshot image
     * @param userPrompt User's instruction for the GUI agent
     * @return The model's response
     */
    public Mono<String> run(String imagePath, String userPrompt) {
        try {
            String base64Image = ImageProcessingUtil.imageToBase64(imagePath);
            LLM llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_UI_TARS).temperature(Prompt.temperature).build());
            LLM.LLMCompoundMsg m = LLM.getLlmCompoundMsg(userPrompt,
                    Message.builder()
                            .images(Lists.newArrayList(base64Image))
                            .build());
            m.setImageType("png");
            Flux<String> flux = llm.compoundMsgCall(m
                    , Prompt.systemPrompt);
            return flux.collect(Collectors.joining());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * Parse the model response and process the action
     *
     * @param modelResponse The raw model response
     * @return JSON string with parsed action data
     */
    public String parseActionOutput(String modelResponse) {
        return ActionResponseParser.parseActionOutput(modelResponse);
    }

    /**
     * Visualize the action by drawing boxes and arrows on the image
     *
     * @param imagePath       Path to the original image
     * @param parsedOutput    Parsed output JSON string from parseActionOutput
     * @param outputImagePath Path where to save the output image
     * @return Path to the saved visualized image
     * @throws IOException If there's an error processing the image
     */
    public String visualizeAction(String imagePath, String parsedOutput, String outputImagePath) throws IOException {
        // Parse the output JSON
        JsonNode json = objectMapper.readTree(parsedOutput);

        // Load the image
        BufferedImage image = ImageIO.read(new File(imagePath));
        Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

        // Get coordinates and direction
        int[] startBox = null;
        int[] endBox = null;
        String direction = null;

        if (json.has("start_box") && !json.get("start_box").isNull()) {
            // Convert from relative [0-1000] to absolute coordinates
            int[] relativeStartBox = new int[4];
            for (int i = 0; i < 4; i++) {
                relativeStartBox[i] = json.get("start_box").get(i).asInt();
            }
            startBox = ImageProcessingUtil.coordinatesConvert(relativeStartBox, imageSize);
        }

        if (json.has("end_box") && !json.get("end_box").isNull()) {
            // Convert from relative [0-1000] to absolute coordinates
            int[] relativeEndBox = new int[4];
            for (int i = 0; i < 4; i++) {
                relativeEndBox[i] = json.get("end_box").get(i).asInt();
            }
            endBox = ImageProcessingUtil.coordinatesConvert(relativeEndBox, imageSize);
        }

        if (json.has("direction") && !json.get("direction").isNull()) {
            direction = json.get("direction").asText();
        }

        // Draw boxes and arrows
        BufferedImage resultImage = ImageProcessingUtil.drawBoxAndArrow(image, startBox, endBox, direction);

        // Save the image
        File outputFile = new File(outputImagePath);
        outputFile.getParentFile().mkdirs(); // Create directories if they don't exist
        String format = outputImagePath.substring(outputImagePath.lastIndexOf('.') + 1);
        ImageIO.write(resultImage, format, outputFile);

        return outputImagePath;
    }

    /**
     * Execute the parsed action using MultimodalService
     *
     * @param parsedOutput Parsed output JSON string from parseActionOutput
     * @return Result of the execution
     */
    public Flux<String> executeAction(String parsedOutput) {
        try {
            JsonNode json = objectMapper.readTree(parsedOutput);
            String action = json.get("action").asText("");

            switch (action) {
                case "click":
                    if (json.has("start_box") && !json.get("start_box").isNull()) {
                        int[] coords = getBoxCenter(json.get("start_box"));
                        return multimodalService.click(coords[0], coords[1]);
                    }
                    break;

                case "left_double":
                    if (json.has("start_box") && !json.get("start_box").isNull()) {
                        int[] coords = getBoxCenter(json.get("start_box"));
                        return multimodalService.doubleClick(coords[0], coords[1]);
                    }
                    break;

                case "right_single":
                    if (json.has("start_box") && !json.get("start_box").isNull()) {
                        int[] coords = getBoxCenter(json.get("start_box"));
                        return multimodalService.rightClick(coords[0], coords[1]);
                    }
                    break;

                case "drag":
                    if (json.has("start_box") && json.has("end_box") &&
                            !json.get("start_box").isNull() && !json.get("end_box").isNull()) {
                        int[] startCoords = getBoxCenter(json.get("start_box"));
                        int[] endCoords = getBoxCenter(json.get("end_box"));
                        return multimodalService.dragAndDrop(
                                startCoords[0], startCoords[1],
                                endCoords[0], endCoords[1]);
                    }
                    break;

                case "type":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        return multimodalService.typeText(content);
                    }
                    break;

                case "hotkey":
                    if (json.has("key") && !json.get("key").isNull()) {
                        String key = json.get("key").asText("");
                        return multimodalService.pressHotkey(List.of(key));
                    }
                    break;

                case "wait":
                    try {
                        Thread.sleep(5000);
                        return Flux.just("等待5秒完成");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return Flux.just("等待被中断: " + e.getMessage());
                    }

                case "finished":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        return Flux.just("任务完成: " + content);
                    }
                    return Flux.just("任务完成");

                default:
                    return Flux.just("不支持的操作: " + action);
            }

            return Flux.just("操作执行失败: 参数不完整");
        } catch (Exception e) {
            return Flux.just("操作执行失败: " + e.getMessage());
        }
    }

    /**
     * Get the center coordinates of a bounding box
     */
    private int[] getBoxCenter(JsonNode boxNode) {
        // Assuming relative coordinates [0-1000]
        int x1 = boxNode.get(0).asInt();
        int y1 = boxNode.get(1).asInt();
        int x2 = boxNode.get(2).asInt();
        int y2 = boxNode.get(3).asInt();

        // Return center point
        return new int[]{(x1 + x2) / 2, (y1 + y2) / 2};
    }
} 