package run.mone.mcp.multimodal.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单操作提取器
 * 用于从任务描述中直接提取不需要坐标定位的操作，避免不必要的截图和LLM调用
 *
 * 支持的简单操作（不需要截图+LLM定位）：
 * - type(content='xxx') - 文本输入
 * - open_app(app_name='xxx') - 打开应用
 * - press_home() - 按Home键
 * - press_back() - 按返回键
 * - finished(content='xxx') - 任务完成
 * - message(content='xxx') - 消息
 * - wait() - 等待
 *
 * 需要坐标定位的操作（必须截图+LLM分析）：
 * - click(point='<point>x y</point>')
 * - long_press(point='<point>x y</point>')
 * - scroll(point='<point>x y</point>', direction='xxx')
 * - drag(start_point='<point>x y</point>', end_point='<point>x y</point>')
 *
 * @author goodjava@qq.com
 * @date 2025/12/16
 */
@Slf4j
public class SimpleActionExtractor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 判断任务描述是否是简单操作（不需要截图+LLM定位）
     *
     * 注意：type 操作虽然参数完整，但需要确保焦点在正确的输入框上，
     * 因此 type 操作不作为独立的简单操作，只在 click+type 连续模式中才跳过分析。
     *
     * @param taskDescription 任务描述，如 "2.输入用户名admin (type, content='admin')"
     * @return true 如果是简单操作
     */
    public static boolean isSimpleAction(String taskDescription) {
        if (taskDescription == null || taskDescription.isEmpty()) {
            return false;
        }

        String lower = taskDescription.toLowerCase();

        // 【重要】type 操作不作为独立的简单操作！
        // 虽然 type 的参数(content)是完整的，但需要确保焦点在正确的输入框上
        // type 操作只在 click+type 连续模式中才跳过分析（由 isClickThenTypePattern 处理）
        // 单独的 type 操作仍需要截图+LLM分析来确认焦点位置

        // open_app 操作：必须包含 app_name= 才能直接执行
        // 这是真正的简单操作，不需要坐标，直接通过包名启动应用
        if (lower.contains("open_app") && containsAppNameParam(taskDescription)) {
            return true;
        }

        // 无参数系统操作：这些是系统级按键，不需要坐标
        if (lower.contains("press_home") || lower.contains("press_back") || lower.contains("wait()")) {
            return true;
        }

        // finished 和 message：这些是流程控制，不需要坐标
        if (lower.contains("finished") || lower.contains("message")) {
            return true;
        }

        return false;
    }

    /**
     * 判断任务描述是否是可以跳过截图分析的 type 操作
     * 只有在确认前一步是 click 操作时才返回 true
     *
     * @param taskDescription 任务描述
     * @param previousTask 前一个任务描述（用于判断是否有前置 click）
     * @return true 如果是可以直接执行的 type 操作
     */
    public static boolean isTypeActionAfterClick(String taskDescription, String previousTask) {
        if (taskDescription == null || previousTask == null) {
            return false;
        }

        String lower = taskDescription.toLowerCase();
        String prevLower = previousTask.toLowerCase();

        // 当前任务是 type 操作
        boolean isType = (lower.contains("type") || lower.contains("输入")) && containsContentParam(taskDescription);

        // 前一个任务是 click 操作
        boolean prevIsClick = prevLower.contains("click") || prevLower.contains("点击");

        return isType && prevIsClick;
    }

    /**
     * 判断任务是否需要坐标定位
     *
     * @param taskDescription 任务描述
     * @return true 如果需要截图+LLM分析来获取坐标
     */
    public static boolean needsCoordinates(String taskDescription) {
        if (taskDescription == null || taskDescription.isEmpty()) {
            return true;
        }

        String lower = taskDescription.toLowerCase();

        // 这些操作需要坐标
        return lower.contains("click")
            || lower.contains("long_press")
            || lower.contains("scroll")
            || lower.contains("drag");
    }

    /**
     * 从任务描述中直接提取操作，返回可执行的 JSON
     *
     * @param taskDescription 任务描述
     * @return 解析后的 JSON 字符串，如果无法提取则返回 empty
     */
    public static Optional<String> extractAction(String taskDescription) {
        if (taskDescription == null || taskDescription.isEmpty()) {
            return Optional.empty();
        }

        // 尝试提取各种简单操作
        Optional<String> result;

        result = extractTypeAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractOpenAppAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractPressHomeAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractPressBackAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractFinishedAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractMessageAction(taskDescription);
        if (result.isPresent()) return result;

        result = extractWaitAction(taskDescription);
        if (result.isPresent()) return result;

        return Optional.empty();
    }

    /**
     * 提取 type 操作
     * 匹配格式：type(content='xxx') 或 type, content='xxx' 或 (type, content='xxx')
     */
    private static Optional<String> extractTypeAction(String taskDescription) {
        // 多种匹配模式
        Pattern[] patterns = {
            // type(content='xxx') 或 type(content="xxx")
            Pattern.compile("type\\s*\\(\\s*content\\s*=\\s*['\"](.+?)['\"]\\s*\\)", Pattern.DOTALL),
            // (type, content='xxx') 在括号描述中
            Pattern.compile("\\(\\s*type\\s*,\\s*content\\s*=\\s*['\"](.+?)['\"]\\s*\\)", Pattern.DOTALL),
            // content='xxx' 当任务明确包含 type 关键字时
            Pattern.compile("content\\s*=\\s*['\"](.+?)['\"]", Pattern.DOTALL)
        };

        String lower = taskDescription.toLowerCase();
        if (!lower.contains("type") && !lower.contains("输入")) {
            return Optional.empty();
        }

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(taskDescription);
            if (matcher.find()) {
                String content = matcher.group(1);
                // 处理转义字符
                content = unescapeContent(content);
                return Optional.of(buildActionJson("type", "content", content));
            }
        }

        return Optional.empty();
    }

    /**
     * 提取 open_app 操作
     */
    private static Optional<String> extractOpenAppAction(String taskDescription) {
        Pattern[] patterns = {
            // open_app(app_name='xxx')
            Pattern.compile("open_app\\s*\\(\\s*app_name\\s*=\\s*['\"](.+?)['\"]\\s*\\)"),
            // (open_app, app_name='xxx')
            Pattern.compile("\\(\\s*open_app\\s*,\\s*app_name\\s*=\\s*['\"](.+?)['\"]\\s*\\)")
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(taskDescription);
            if (matcher.find()) {
                String appName = matcher.group(1);
                return Optional.of(buildActionJson("open_app", "app_name", appName));
            }
        }

        return Optional.empty();
    }

    /**
     * 提取 press_home 操作
     */
    private static Optional<String> extractPressHomeAction(String taskDescription) {
        String lower = taskDescription.toLowerCase();
        if (lower.contains("press_home") || lower.contains("按home") || lower.contains("返回主屏幕")) {
            return Optional.of(buildActionJson("press_home", null, null));
        }
        return Optional.empty();
    }

    /**
     * 提取 press_back 操作
     */
    private static Optional<String> extractPressBackAction(String taskDescription) {
        String lower = taskDescription.toLowerCase();
        if (lower.contains("press_back") || lower.contains("按返回键")) {
            return Optional.of(buildActionJson("press_back", null, null));
        }
        return Optional.empty();
    }

    /**
     * 提取 finished 操作
     */
    private static Optional<String> extractFinishedAction(String taskDescription) {
        String lower = taskDescription.toLowerCase();
        if (!lower.contains("finished")) {
            return Optional.empty();
        }

        // 尝试提取 content
        Pattern pattern = Pattern.compile("finished\\s*\\(\\s*content\\s*=\\s*['\"](.+?)['\"]\\s*\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(taskDescription);

        if (matcher.find()) {
            String content = unescapeContent(matcher.group(1));
            return Optional.of(buildActionJson("finished", "content", content));
        }

        // 无 content 的 finished
        if (lower.contains("finished()") || lower.contains("finished(")) {
            return Optional.of(buildActionJson("finished", null, null));
        }

        // 包含 finished 关键字
        return Optional.of(buildActionJson("finished", null, null));
    }

    /**
     * 提取 message 操作
     */
    private static Optional<String> extractMessageAction(String taskDescription) {
        String lower = taskDescription.toLowerCase();
        if (!lower.contains("message")) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile("message\\s*\\(\\s*content\\s*=\\s*['\"](.+?)['\"]\\s*\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(taskDescription);

        if (matcher.find()) {
            String content = unescapeContent(matcher.group(1));
            return Optional.of(buildActionJson("message", "content", content));
        }

        return Optional.of(buildActionJson("message", null, null));
    }

    /**
     * 提取 wait 操作
     */
    private static Optional<String> extractWaitAction(String taskDescription) {
        String lower = taskDescription.toLowerCase();
        if (lower.contains("wait()") || lower.contains("wait(") || lower.contains("等待")) {
            return Optional.of(buildActionJson("wait", null, null));
        }
        return Optional.empty();
    }

    /**
     * 检查是否包含 content= 参数
     */
    private static boolean containsContentParam(String taskDescription) {
        return taskDescription.matches("(?i).*content\\s*=\\s*['\"].+?['\"].*");
    }

    /**
     * 检查是否包含 app_name= 参数
     */
    private static boolean containsAppNameParam(String taskDescription) {
        return taskDescription.matches("(?i).*app_name\\s*=\\s*['\"].+?['\"].*");
    }

    /**
     * 处理转义字符
     */
    private static String unescapeContent(String content) {
        if (content == null) return null;
        return content
                .replace("\\\\'", "'")
                .replace("\\\\\"", "\"")
                .replace("\\\\n", "\n")
                .replace("\\'", "'")
                .replace("\\\"", "\"")
                .replace("\\n", "\n");
    }

    /**
     * 构建 Action JSON
     */
    private static String buildActionJson(String action, String paramName, String paramValue) {
        try {
            ObjectNode result = objectMapper.createObjectNode();
            result.put("action", action);
            result.putNull("point");
            result.putNull("start_point");
            result.putNull("end_point");
            result.putNull("direction");

            if (paramName != null && paramValue != null) {
                result.put(paramName, paramValue);
            } else {
                result.putNull("content");
                result.putNull("app_name");
            }

            log.info("直接提取简单操作: action={}, {}={}", action, paramName, paramValue);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            log.error("构建 Action JSON 失败", e);
            return "{}";
        }
    }

    /**
     * 判断当前任务和下一个任务是否是 click + type 连续模式
     * 这种模式可以在执行完 click 后直接执行 type，不需要重新截图分析
     *
     * @param currentTask 当前任务描述
     * @param nextTask 下一个任务描述
     * @return true 如果是 click + type 模式
     */
    public static boolean isClickThenTypePattern(String currentTask, String nextTask) {
        if (currentTask == null || nextTask == null) {
            return false;
        }

        String currentLower = currentTask.toLowerCase();
        String nextLower = nextTask.toLowerCase();

        // 当前任务是点击输入框类操作
        boolean isClickOnInput = currentLower.contains("click") &&
            (currentLower.contains("输入框") || currentLower.contains("input") ||
             currentLower.contains("搜索") || currentLower.contains("search") ||
             currentLower.contains("文本框") || currentLower.contains("text"));

        // 下一个任务是输入操作且有 content
        boolean isTypeWithContent = (nextLower.contains("type") || nextLower.contains("输入"))
            && containsContentParam(nextTask);

        return isClickOnInput && isTypeWithContent;
    }

    /**
     * 判断当前任务和下一个任务是否是 click + scroll 连续模式
     *
     * @param currentTask 当前任务描述
     * @param nextTask 下一个任务描述
     * @return true 如果是 click + scroll 模式
     */
    public static boolean isClickThenScrollPattern(String currentTask, String nextTask) {
        if (currentTask == null || nextTask == null) {
            return false;
        }

        String currentLower = currentTask.toLowerCase();
        String nextLower = nextTask.toLowerCase();

        // 当前任务是点击获取焦点类操作
        boolean isClickForFocus = currentLower.contains("click") &&
            (currentLower.contains("焦点") || currentLower.contains("focus") ||
             currentLower.contains("区域") || currentLower.contains("area"));

        // 下一个任务是滚动操作
        boolean isScroll = nextLower.contains("scroll") || nextLower.contains("滚动");

        return isClickForFocus && isScroll;
    }
}
