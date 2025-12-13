package run.mone.mcp.multimodal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.mcp.multimodal.android.AndroidService;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.util.ActionResponseParser;
import run.mone.mcp.multimodal.util.ImageProcessingUtil;

import java.util.stream.Collectors;

/**
 * Android GUI Agent 服务
 * 负责与 LLM 交互分析 Android 界面截图，并执行相应的操作
 *
 * @author goodjava@qq.com
 * @date 2025/12/13
 */
@Slf4j
@Service
public class AndroidGuiAgentService {

    @Autowired
    private AndroidService androidService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 当前设备序列号（可选）
     */
    private String deviceSerial;

    /**
     * 设置当前操作的设备
     */
    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    /**
     * 运行 GUI Agent 分析 Android 截图
     *
     * @param imagePath    截图路径
     * @param userPrompt   用户指令
     * @param systemPrompt 系统提示词
     * @return 模型响应
     */
    public Mono<String> run(String imagePath, String userPrompt, String systemPrompt) {
        try {
            String base64Image = ImageProcessingUtil.imageToBase64(imagePath);
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(LLMProvider.DOUBAO_UI_TARS)
                    .temperature(Prompt.temperature)
                    .thinking(true)
                    .build());
            LLM.LLMCompoundMsg m = LLM.getLlmCompoundMsg(userPrompt,
                    Message.builder()
                            .images(Lists.newArrayList(base64Image))
                            .build());
            m.setImageType("png");
            Flux<String> flux = llm.compoundMsgCall(m, systemPrompt);
            return flux.collect(Collectors.joining());
        } catch (Exception e) {
            log.error("运行 Android GUI Agent 失败", e);
            return Mono.error(e);
        }
    }

    /**
     * 解析模型响应中的 Action
     *
     * @param modelResponse 模型响应
     * @return 解析后的 JSON 字符串
     */
    public String parseActionOutput(String modelResponse) {
        return ActionResponseParser.parseActionOutput(modelResponse);
    }

    /**
     * 执行解析后的 Action
     *
     * @param parsedOutput 解析后的 JSON 字符串
     * @return 执行结果
     */
    public Flux<String> executeAction(String parsedOutput) {
        try {
            JsonNode json = objectMapper.readTree(parsedOutput);
            String action = json.get("action").asText("");

            log.info("执行 Android 操作: {}", action);

            switch (action) {
                case "click":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null) {
                            return androidService.tap(coords[0], coords[1], deviceSerial);
                        }
                    }
                    break;

                case "long_press":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null) {
                            return androidService.longPress(coords[0], coords[1], deviceSerial);
                        }
                    }
                    break;

                case "type":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        // 使用支持中文的输入法切换方法
                        return androidService.inputTextWithImeSwitching(content, deviceSerial);
                    }
                    break;

                case "scroll":
                    if (json.has("point") && !json.get("point").isNull()) {
                        int[] coords = getPointCoordinates(json.get("point"));
                        if (coords != null && json.has("direction") && !json.get("direction").isNull()) {
                            String direction = json.get("direction").asText("");
                            return androidService.scroll(coords[0], coords[1], direction, deviceSerial);
                        }
                    }
                    break;

                case "drag":
                    if (json.has("start_point") && json.has("end_point") &&
                            !json.get("start_point").isNull() && !json.get("end_point").isNull()) {
                        int[] startCoords = getPointCoordinates(json.get("start_point"));
                        int[] endCoords = getPointCoordinates(json.get("end_point"));

                        if (startCoords != null && endCoords != null) {
                            return androidService.drag(
                                    startCoords[0], startCoords[1],
                                    endCoords[0], endCoords[1],
                                    deviceSerial);
                        }
                    }
                    break;

                case "open_app":
                    if (json.has("app_name") && !json.get("app_name").isNull()) {
                        String appName = json.get("app_name").asText("");
                        return androidService.openApp(appName, deviceSerial);
                    }
                    break;

                case "press_home":
                    return androidService.pressHome(deviceSerial);

                case "press_back":
                    return androidService.pressBack(deviceSerial);

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

                case "message":
                    if (json.has("content") && !json.get("content").isNull()) {
                        String content = json.get("content").asText("");
                        return Flux.just(content);
                    }
                    break;

                default:
                    return Flux.just("不支持的操作: " + action);
            }

            return Flux.just("操作执行失败: 参数不完整");
        } catch (Exception e) {
            log.error("执行 Action 失败", e);
            return Flux.just("操作执行失败: " + e.getMessage());
        }
    }

    /**
     * 从 JSON 节点获取坐标
     *
     * @param pointNode JSON 节点
     * @return [x, y] 坐标数组
     */
    private int[] getPointCoordinates(JsonNode pointNode) {
        if (pointNode == null || !pointNode.isArray() || pointNode.size() != 2) {
            return null;
        }

        int x = pointNode.get(0).asInt();
        int y = pointNode.get(1).asInt();

        return new int[]{x, y};
    }

    /**
     * 截取 Android 设备屏幕
     *
     * @param filePath 保存路径（可选）
     * @return 截图文件路径
     */
    public Flux<String> captureScreenshot(String filePath) {
        return androidService.screenshot(filePath, deviceSerial);
    }

    /**
     * 截取 Android 设备屏幕并返回 Base64
     *
     * @return Base64 编码的截图
     */
    public Flux<String> captureScreenshotBase64() {
        return androidService.screenshotBase64(deviceSerial);
    }
}