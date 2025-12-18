package run.mone.mcp.multimodal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.util.ActionResponseParser;
import run.mone.mcp.multimodal.util.ImageProcessingUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for interacting with the GUI Agent model
 */
@Service
@ConditionalOnProperty(name = "mcp.agent.type", havingValue = "default", matchIfMissing = true)
public class GuiAgentService {

    @Autowired
    private LLM llm;

    @Autowired
    private MultimodalService multimodalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gui.agent.systemPrompt:}")
    private String defaultSystemPrompt;

    /**
     * UI Provider 配置，默认使用 DOUBAO_UI_TARS
     */
    @Value("${mcp.gui.ui.provider:DOUBAO_UI_TARS}")
    private String uiProvider;

    /**
     * Default system prompt for GUI agent
     */
    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are a GUI agent. You are given a task and your action history, with screenshots. " +
                    "You need to perform the next action to complete the task.\n" +
                    "## Output Format\n```\nThought: ...\nAction: ...\n```\n" +
                    "## Action Space\n" +
                    "click(point='<point>x1 y1</point>')\n" +
                    "left_double(point='<point>x1 y1</point>')\n" +
                    "right_single(point='<point>x1 y1</point>')\n" +
                    "drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')\n" +
                    "hotkey(key='ctrl c') # Split keys with a space and use lowercase. Also, do not use more than 3 keys in one hotkey action.\n" +
                    "type(content='xxx') # Use escape characters \\\\', \\\\\", and \\\\n in content part to ensure we can parse the content in normal python string format. If you want to submit your input, use \\\\n at the end of content.\n" +
                    "scroll(point='<point>x1 y1</point>', direction='down or up or right or left') # Show more information on the `direction` side.\n" +
                    "wait() # Sleep for 5s and take a screenshot to check for any changes.\n" +
                    "finished(content='xxx') # Use escape characters \\\\', \\\\\", and \\\\n in content part to ensure we can parse the content in normal python string format.\n" +
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
    public Mono<String> run(String imagePath, String userPrompt, String systemPrompt) {
        try {
            String base64Image = ImageProcessingUtil.imageToBase64(imagePath);
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(LLMProvider.valueOf(uiProvider))
                    .temperature(Prompt.temperature)
                    .thinking(true)
                    .build());
            LLM.LLMCompoundMsg m = LLM.getLlmCompoundMsg(userPrompt,
                    Message.builder()
                            .images(Lists.newArrayList(base64Image))
                            .build());
            m.setImageType("png");
            Flux<String> flux = llm.compoundMsgCall(m
                    ,systemPrompt);
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
     * Visualize the action by drawing points and arrows on the image
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
        int[] startPoint = null;
        int[] endPoint = null;
        String direction = null;

        // Handle single point (for click, scroll, etc.)
        if (json.has("point") && !json.get("point").isNull()) {
            int[] relativePoint = getPointCoordinates(json.get("point"));
            if (relativePoint != null) {
                // Convert from relative coordinates to absolute coordinates
                startPoint = convertPointToBox(relativePoint, imageSize);
            }
        }

        // Handle start_point (for drag)
        if (json.has("start_point") && !json.get("start_point").isNull()) {
            int[] relativePoint = getPointCoordinates(json.get("start_point"));
            if (relativePoint != null) {
                startPoint = convertPointToBox(relativePoint, imageSize);
            }
        }

        // Handle end_point (for drag)
        if (json.has("end_point") && !json.get("end_point").isNull()) {
            int[] relativePoint = getPointCoordinates(json.get("end_point"));
            if (relativePoint != null) {
                endPoint = convertPointToBox(relativePoint, imageSize);
            }
        }

        if (json.has("direction") && !json.get("direction").isNull()) {
            direction = json.get("direction").asText();
        }

        // Draw boxes and arrows
        BufferedImage resultImage = ImageProcessingUtil.drawBoxAndArrow(image, startPoint, endPoint, direction);

        // Save the image
        File outputFile = new File(outputImagePath);
        outputFile.getParentFile().mkdirs(); // Create directories if they don't exist
        String format = outputImagePath.substring(outputImagePath.lastIndexOf('.') + 1);
        ImageIO.write(resultImage, format, outputFile);

        return outputImagePath;
    }

    /**
     * Convert a point [x, y] to a small box [x1, y1, x2, y2] for visualization
     * Point coordinates are in relative [0-1000] range
     * 
     * @param point [x, y] in relative coordinates
     * @param imageSize The size of the image
     * @return [x1, y1, x2, y2] box coordinates in absolute pixels
     */
    private int[] convertPointToBox(int[] point, Dimension imageSize) {
        if (point == null || point.length != 2) {
            return null;
        }
        
        // Convert from [0-1000] to actual image coordinates
        int x = (int) (point[0] * imageSize.width / 1000.0);
        int y = (int) (point[1] * imageSize.height / 1000.0);
        
        // Create a small box around the point (10x10 pixels)
        int boxSize = 10;
        return new int[]{
            Math.max(0, x - boxSize / 2),
            Math.max(0, y - boxSize / 2),
            Math.min(imageSize.width, x + boxSize / 2),
            Math.min(imageSize.height, y + boxSize / 2)
        };
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
                case "message":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        return Flux.just(content);
                    }
                    break;
                    
                case "scroll":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null && json.has("direction") && !json.get("direction").isNull()) {
                            String direction = json.get("direction").asText("");
                            // Convert to screen coordinates
                            Point screenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(coords[0], coords[1]), 
                                    new Dimension(1000, 1000));
                            // Determine scroll direction value
                            int scrollAmount = direction.equals("down") || direction.equals("right") ? -3 : 3;
                            return multimodalService.scrollWheel(scrollAmount);
                        }
                    }
                    break;
                    
                case "click":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null) {
                            // Convert to screen coordinates
                            Point screenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(coords[0], coords[1]), 
                                    new Dimension(1000, 1000));
                            return multimodalService.click(screenPoint.x, screenPoint.y);
                        }
                    }
                    break;

                case "left_double":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null) {
                            // Convert to screen coordinates
                            Point screenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(coords[0], coords[1]), 
                                    new Dimension(1000, 1000));
                            return multimodalService.doubleClick(screenPoint.x, screenPoint.y);
                        }
                    }
                    break;

                case "right_single":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null) {
                            // Convert to screen coordinates
                            Point screenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(coords[0], coords[1]), 
                                    new Dimension(1000, 1000));
                            return multimodalService.rightClick(screenPoint.x, screenPoint.y);
                        }
                    }
                    break;

                case "drag":
                    if (json.has("start_point") && json.has("end_point") &&
                            !json.get("start_point").isNull() && !json.get("end_point").isNull()) {
                        int[] startCoords = getPointCoordinates(json.get("start_point"));
                        int[] endCoords = getPointCoordinates(json.get("end_point"));
                        
                        if (startCoords != null && endCoords != null) {
                            // Convert to screen coordinates
                            Point startScreenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(startCoords[0], startCoords[1]), 
                                    new Dimension(1000, 1000));
                            Point endScreenPoint = ImageProcessingUtil.imageToScreenCoordinates(
                                    new Point(endCoords[0], endCoords[1]), 
                                    new Dimension(1000, 1000));
                            
                            return multimodalService.dragAndDrop(
                                    startScreenPoint.x, startScreenPoint.y,
                                    endScreenPoint.x, endScreenPoint.y);
                        }
                    }
                    break;

                case "type":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        return multimodalService.typeTextV2(content);
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
     * Get point coordinates from JSON array [x, y]
     * 
     * @param pointNode JSON node containing [x, y] array
     * @return Array with [x, y] coordinates, or null if invalid
     */
    private int[] getPointCoordinates(JsonNode pointNode) {
        if (pointNode == null || !pointNode.isArray() || pointNode.size() != 2) {
            return null;
        }
        
        int x = pointNode.get(0).asInt();
        int y = pointNode.get(1).asInt();
        
        return new int[]{x, y};
    }
} 