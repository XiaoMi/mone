package run.mone.mcp.multimodal.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android 专用的响应解析器
 * 解析 LLM 输出的 Android GUI Agent 操作指令
 *
 * ## Action Space (Android)
 * click(point='<point>x1 y1</point>')
 * long_press(point='<point>x1 y1</point>')
 * type(content='') - 支持中文，使用 "\\n" 提交
 * scroll(point='<point>x1 y1</point>', direction='down or up or right or left')
 * open_app(app_name='')
 * drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')
 * press_home()
 * press_back()
 * finished(content='xxx')
 *
 * @author goodjava@qq.com
 * @date 2025/12/13
 */
@Slf4j
public class AndroidResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设备屏幕宽度（默认 1080）
     */
    private static int screenWidth = 1080;

    /**
     * 设备屏幕高度（默认 2400）
     */
    private static int screenHeight = 2400;

    /**
     * 设置设备屏幕尺寸
     * 用于将相对坐标(0-1000)转换为设备屏幕绝对坐标
     *
     * @param width  屏幕宽度
     * @param height 屏幕高度
     */
    public static void setScreenSize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        log.info("设置屏幕尺寸: {}x{}", width, height);
    }

    /**
     * 获取当前设置的屏幕宽度
     */
    public static int getScreenWidth() {
        return screenWidth;
    }

    /**
     * 获取当前设置的屏幕高度
     */
    public static int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 解析模型输出文本为结构化的 JSON 格式
     *
     * @param outputText 模型原始输出文本
     * @return 解析后的 JSON 字符串
     */
    public static String parseActionOutput(String outputText) {
        ObjectNode result = objectMapper.createObjectNode();

        // 初始化默认值
        result.put("thought", "");
        result.put("action", "");
        result.putNull("point");
        result.putNull("start_point");
        result.putNull("end_point");
        result.putNull("content");
        result.putNull("direction");
        result.putNull("app_name");

        if (outputText == null || outputText.isEmpty()) {
            return toJson(result);
        }

        // 提取 Thought 部分
        extractThought(outputText, result);

        // 提取 Action 部分
        extractAction(outputText, result);

        return toJson(result);
    }

    /**
     * 提取 Thought 部分
     */
    private static void extractThought(String outputText, ObjectNode result) {
        Pattern thoughtPattern = Pattern.compile("Thought:(.*?)(?=\\nAction:|$)", Pattern.DOTALL);
        Matcher thoughtMatcher = thoughtPattern.matcher(outputText);
        if (thoughtMatcher.find()) {
            String thought = thoughtMatcher.group(1).trim();
            result.put("thought", thought);
        }
    }

    /**
     * 提取 Action 部分
     */
    private static void extractAction(String outputText, ObjectNode result) {
        // 匹配 Action: 后面的内容，支持多行
        Pattern actionPattern = Pattern.compile("Action:\\s*(.*?)(?:#.*)?(?=\\n\\n|\\nThought:|$)", Pattern.DOTALL);
        Matcher actionMatcher = actionPattern.matcher(outputText);

        if (actionMatcher.find()) {
            String actionText = actionMatcher.group(1).trim();

            if (actionText.isEmpty()) {
                return;
            }

            // 解析 action 类型和参数
            parseActionText(actionText, result);
        }
    }

    /**
     * 解析 action 文本
     *
     * @param actionText action 文本，如 "click(point='<point>317 582</point>')"
     * @param result     结果对象
     */
    private static void parseActionText(String actionText, ObjectNode result) {
        // 匹配 action_name(params) 或 action_name()
        Pattern actionPattern = Pattern.compile("(\\w+)\\s*\\(([^)]*)\\)");
        Matcher actionMatcher = actionPattern.matcher(actionText);

        if (actionMatcher.find()) {
            String actionType = actionMatcher.group(1).trim();
            String paramsText = actionMatcher.group(2).trim();

            result.put("action", actionType);
            log.debug("解析 Action 类型: {}", actionType);

            // 根据不同的 action 类型解析参数
            switch (actionType.toLowerCase()) {
                case "click":
                case "long_press":
                    parsePointParam(paramsText, "point", result);
                    break;

                case "type":
                    parseContentParam(paramsText, result);
                    break;

                case "scroll":
                    parsePointParam(paramsText, "point", result);
                    parseDirectionParam(paramsText, result);
                    break;

                case "open_app":
                    parseAppNameParam(paramsText, result);
                    break;

                case "drag":
                    parsePointParam(paramsText, "start_point", result);
                    parsePointParam(paramsText, "end_point", result);
                    break;

                case "press_home":
                case "press_back":
                    // 无参数
                    break;

                case "finished":
                case "message":
                    parseContentParam(paramsText, result);
                    break;

                default:
                    log.warn("未知的 Action 类型: {}", actionType);
            }
        } else {
            // 可能是无括号的格式，尝试直接匹配 action 名称
            String[] parts = actionText.split("\\s+");
            if (parts.length > 0) {
                result.put("action", parts[0].trim());
            }
        }
    }

    /**
     * 解析 point 参数
     * 支持格式: point='<point>x y</point>'
     *
     * 坐标转换说明：
     * - 模型返回的是相对坐标(0-1000)，左上角(0,0)，右下角(1000,1000)
     * - 需要转换为设备屏幕的绝对坐标
     * - 转换公式：screenX = relativeX / 1000.0 * screenWidth
     *
     * @param paramsText 参数文本
     * @param paramName  参数名称 (point, start_point, end_point)
     * @param result     结果对象
     */
    private static void parsePointParam(String paramsText, String paramName, ObjectNode result) {
        // 匹配 point='<point>x y</point>' 或 start_point='<point>x y</point>'
        String patternStr = paramName + "\\s*=\\s*['\"]?<point>\\s*(\\d+)\\s+(\\d+)\\s*</point>['\"]?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(paramsText);

        if (matcher.find()) {
            try {
                int relativeX = Integer.parseInt(matcher.group(1));
                int relativeY = Integer.parseInt(matcher.group(2));

                // 将相对坐标(0-1000)转换为设备屏幕绝对坐标
                int absoluteX = (int) (relativeX / 1000.0 * screenWidth);
                int absoluteY = (int) (relativeY / 1000.0 * screenHeight);

                result.putArray(paramName).add(absoluteX).add(absoluteY);
                log.debug("解析 {} 坐标: 相对({}, {}) -> 绝对({}, {})",
                        paramName, relativeX, relativeY, absoluteX, absoluteY);
            } catch (NumberFormatException e) {
                log.warn("解析 {} 坐标失败: {}", paramName, e.getMessage());
            }
        }
    }

    /**
     * 将相对坐标(0-1000)转换为设备屏幕绝对坐标
     *
     * @param relativeX 相对 X 坐标 (0-1000)
     * @param relativeY 相对 Y 坐标 (0-1000)
     * @return int[] 包含 [absoluteX, absoluteY]
     */
    public static int[] relativeToAbsoluteCoordinates(int relativeX, int relativeY) {
        int absoluteX = (int) (relativeX / 1000.0 * screenWidth);
        int absoluteY = (int) (relativeY / 1000.0 * screenHeight);
        return new int[]{absoluteX, absoluteY};
    }

    /**
     * 将相对坐标(0-1000)转换为设备屏幕绝对坐标（指定屏幕尺寸）
     *
     * @param relativeX 相对 X 坐标 (0-1000)
     * @param relativeY 相对 Y 坐标 (0-1000)
     * @param width     屏幕宽度
     * @param height    屏幕高度
     * @return int[] 包含 [absoluteX, absoluteY]
     */
    public static int[] relativeToAbsoluteCoordinates(int relativeX, int relativeY, int width, int height) {
        int absoluteX = (int) (relativeX / 1000.0 * width);
        int absoluteY = (int) (relativeY / 1000.0 * height);
        return new int[]{absoluteX, absoluteY};
    }

    /**
     * 解析 content 参数
     * 支持格式: content='xxx' 或 content="xxx"
     * 处理转义字符: \\', \\", \\n
     *
     * @param paramsText 参数文本
     * @param result     结果对象
     */
    private static void parseContentParam(String paramsText, ObjectNode result) {
        // 匹配 content='...' 或 content="..."
        // 使用非贪婪匹配，并处理转义的引号
        Pattern pattern = Pattern.compile("content\\s*=\\s*(['\"])(.+?)(?<!\\\\)\\1", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(paramsText);

        if (matcher.find()) {
            String content = matcher.group(2);
            // 处理转义字符
            content = content.replace("\\\\'", "'")
                    .replace("\\\\\"", "\"")
                    .replace("\\\\n", "\n")
                    .replace("\\'", "'")
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n");
            result.put("content", content);
            log.debug("解析 content: {}", content.length() > 50 ? content.substring(0, 50) + "..." : content);
        }
    }

    /**
     * 解析 direction 参数
     * 支持格式: direction='down' 或 direction="up"
     * 有效值: up, down, left, right
     *
     * @param paramsText 参数文本
     * @param result     结果对象
     */
    private static void parseDirectionParam(String paramsText, ObjectNode result) {
        Pattern pattern = Pattern.compile("direction\\s*=\\s*['\"]?(up|down|left|right)['\"]?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(paramsText);

        if (matcher.find()) {
            String direction = matcher.group(1).toLowerCase();
            result.put("direction", direction);
            log.debug("解析 direction: {}", direction);
        }
    }

    /**
     * 解析 app_name 参数
     * 支持格式: app_name='微信' 或 app_name="wechat"
     *
     * @param paramsText 参数文本
     * @param result     结果对象
     */
    private static void parseAppNameParam(String paramsText, ObjectNode result) {
        // 匹配 app_name='...' 或 app_name="..."
        Pattern pattern = Pattern.compile("app_name\\s*=\\s*(['\"])(.+?)\\1");
        Matcher matcher = pattern.matcher(paramsText);

        if (matcher.find()) {
            String appName = matcher.group(2);
            // 处理转义
            appName = appName.replace("\\'", "'").replace("\\\"", "\"");
            result.put("app_name", appName);
            log.debug("解析 app_name: {}", appName);
        }
    }

    /**
     * 将结果对象转换为 JSON 字符串
     */
    private static String toJson(ObjectNode result) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            log.error("JSON 序列化失败", e);
            return "{}";
        }
    }

    /**
     * 从模型输出中提取所有 Action（用于多操作场景）
     *
     * @param modelOutput 模型输出文本
     * @return Action 文本列表
     */
    public static java.util.List<String> parseMultipleActions(String modelOutput) {
        java.util.List<String> actions = new java.util.ArrayList<>();

        if (modelOutput == null || modelOutput.isEmpty()) {
            return actions;
        }

        // 提取 Action: 后面的内容
        Pattern actionSectionPattern = Pattern.compile("Action:\\s*(.+?)(?=\\n\\n|\\nThought:|$)", Pattern.DOTALL);
        Matcher actionSectionMatcher = actionSectionPattern.matcher(modelOutput);

        if (actionSectionMatcher.find()) {
            String actionSection = actionSectionMatcher.group(1).trim();

            // 匹配所有支持的 Android actions
            // 支持: click, long_press, type, scroll, open_app, drag, press_home, press_back, finished, message
            Pattern actionPattern = Pattern.compile(
                    "(click|long_press|type|scroll|open_app|drag|press_home|press_back|finished|message)\\s*\\([^)]*\\)",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher actionMatcher = actionPattern.matcher(actionSection);

            while (actionMatcher.find()) {
                String action = actionMatcher.group().trim();
                if (!action.isEmpty()) {
                    actions.add(action);
                    log.debug("解析到 Action: {}", action);
                }
            }
        }

        return actions;
    }

    /**
     * 验证解析后的 action 是否有效
     *
     * @param parsedJson 解析后的 JSON 字符串
     * @return 是否有效
     */
    public static boolean isValidAction(String parsedJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(parsedJson);
            String action = node.has("action") ? node.get("action").asText("") : "";

            if (action.isEmpty()) {
                return false;
            }

            // 验证 Android 支持的 action 类型
            switch (action.toLowerCase()) {
                case "click":
                case "long_press":
                    return node.has("point") && !node.get("point").isNull();

                case "type":
                    return node.has("content") && !node.get("content").isNull();

                case "scroll":
                    return node.has("point") && !node.get("point").isNull()
                            && node.has("direction") && !node.get("direction").isNull();

                case "open_app":
                    return node.has("app_name") && !node.get("app_name").isNull();

                case "drag":
                    return node.has("start_point") && !node.get("start_point").isNull()
                            && node.has("end_point") && !node.get("end_point").isNull();

                case "press_home":
                case "press_back":
                    return true;

                case "finished":
                case "message":
                    return true; // content 是可选的

                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("验证 Action 失败", e);
            return false;
        }
    }
}