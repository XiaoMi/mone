package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.config.AndroidConfig;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.service.AndroidGuiAgentService;
import run.mone.mcp.multimodal.util.SimpleActionExtractor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android GUI Agent
 * 负责分析 Android 设备截图，拆分任务并执行操作
 *
 * @author goodjava@qq.com
 * @date 2025/12/13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AndroidGuiAgent {

    private final AndroidGuiAgentService androidGuiAgentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Random random = new Random();

    /**
     * Vision Provider 配置，默认使用 DOUBAO_VISION
     */
    @Value("${mcp.android.vision.provider:DOUBAO_VISION}")
    private String visionProvider;

    /**
     * 是否启用任务纠正检测（默认关闭）
     */
    private boolean enableTaskCorrection = false;

    /**
     * 启用任务纠正检测
     */
    public void enableTaskCorrection() {
        this.enableTaskCorrection = true;
        log.info("任务纠正检测已启用");
    }

    /**
     * 禁用任务纠正检测
     */
    public void disableTaskCorrection() {
        this.enableTaskCorrection = false;
        log.info("任务纠正检测已禁用");
    }

    /**
     * 设置任务纠正检测开关
     *
     * @param enable true 启用，false 禁用
     */
    public void setEnableTaskCorrection(boolean enable) {
        this.enableTaskCorrection = enable;
        log.info("任务纠正检测: {}", enable ? "启用" : "禁用");
    }

    /**
     * 获取任务纠正检测状态
     *
     * @return true 已启用，false 已禁用
     */
    public boolean isTaskCorrectionEnabled() {
        return enableTaskCorrection;
    }

    /**
     * 根据指令拆分任务列表
     *
     * @param instruction 用户指令
     * @return 任务列表 JSON 数组
     */
    public String taskList(String instruction) {
        // 从 Android 设备截图
        String imagePath = androidGuiAgentService.captureScreenshot(null).blockFirst();
        log.info("Android 截图路径: {}", imagePath);

        String prompt = """
                根据 Android 设备截图和需求帮我拆分下操作列表。每一步都要明确指定操作类型和参数。
                
                ## 支持的操作类型 (Android Action Space):
                click(point='<point>x1 y1</point>') - 点击操作
                long_press(point='<point>x1 y1</point>') - 长按操作
                type(content='具体内容') - 输入文本，content必须明确指定要输入的具体内容（支持中文）
                scroll(point='<point>x1 y1</point>', direction='down/up/right/left') - 滚动操作
                open_app(app_name='应用名') - 打开应用，支持中英文名称如：微信、wechat、QQ、抖音
                drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>') - 拖拽操作
                press_home() - 按 Home 键返回主屏幕
                press_back() - 按返回键
                finished(content='完成信息') - 任务完成
                message(content='消息内容') - 向用户返回分析信息
                
                ## 重要规则:
                1. 每一步必须明确标注操作类型（click、type、scroll、open_app等）
                2. 对于type操作，必须在括号内明确写出 content='具体要输入的内容'，支持中文
                3. 对于click操作，必须描述清楚点击的目标元素
                4. 操作步骤要符合人类操作习惯：先点击输入框获取焦点，再输入内容
                5. 如果需要打开应用，优先使用 open_app 操作
                6. 每一步的描述格式：序号.操作描述 (操作类型, 参数说明)
                
                ## 例子1 - 打开微信:
                需求: 打开微信
                返回: (必须是json array格式)
                [
                "1.打开微信应用 (open_app, app_name='微信')",
                "2.finished(content='已成功打开微信')"
                ]
                
                ## 例子2 - 微信发消息:
                需求: 在微信中给张三发送消息"你好"
                分析: 需要先点击搜索，搜索联系人，进入聊天，输入消息并发送
                返回:
                [
                "1.点击微信顶部搜索框 (click, 定位到界面顶部的搜索区域)",
                "2.输入联系人名字张三 (type, content='张三')",
                "3.点击搜索结果中的张三 (click, 定位到搜索结果列表中的联系人)",
                "4.点击底部消息输入框 (click, 定位到聊天界面底部的输入框)",
                "5.输入消息内容 (type, content='你好')",
                "6.点击发送按钮 (click, 定位到输入框右侧的发送按钮)",
                "7.finished(content='已成功给张三发送消息：你好')"
                ]
                
                ## 例子3 - 滚动浏览:
                需求: 在当前页面向下滚动查看更多内容
                返回:
                [
                "1.在屏幕中央向下滚动 (scroll, direction='down')",
                "2.finished(content='已完成向下滚动')"
                ]
                
                ## 例子4 - 返回操作:
                需求: 返回上一页
                返回:
                [
                "1.按返回键 (press_back)",
                "2.finished(content='已返回上一页')"
                ]
                
                ## 例子5 - 信息识别:
                需求: 识别当前打开的是什么应用
                返回:
                [
                "1.分析当前截图识别应用 (message, 分析截图中的界面特征、标题栏等信息)",
                "2.finished(content='finish')"
                ]
                
                %s
                
                -----
                现在请根据 Android 设备截图和下面的需求生成操作列表：
                
                需求: %s
                
                返回: (必须是json array格式，每一步都要明确标注操作类型,但不需要你提供任何参数,尤其是x,y),并且需要把这个结果用<list></list>标签所包裹
                
                """.formatted(AndroidConfig.MEITUAN_KFC_WORKFLOW, instruction);

        // 使用配置的 Vision Provider 生成任务列表
        String modelOutput = androidGuiAgentService.run(imagePath, prompt, "", LLMProvider.valueOf(visionProvider)).block();
        log.info("模型输出: {}", modelOutput);
        modelOutput = extractJsonArray(modelOutput);
        return modelOutput;
    }

    /**
     * 纠正任务列表
     * 根据当前截图、已有任务列表和当前任务，判断是否需要生成新的任务列表
     *
     * 使用场景：
     * - 当前任务与当前页面不匹配时（如页面跳转异常、弹窗干扰等）
     * - 需要根据实际页面状态调整后续任务
     *
     * @param originalTaskList 原始任务列表（JSON 数组字符串）
     * @param currentTaskIndex 当前执行到的任务索引（从0开始）
     * @param currentTask      当前正在执行的任务描述
     * @param originalGoal     原始目标/需求
     * @return 新的任务列表（JSON 数组字符串），如果不需要纠正则返回空字符串 ""
     */
    public String correctTaskList(String originalTaskList, int currentTaskIndex, String currentTask, String originalGoal) {
        // 从 Android 设备截图
        String imagePath = androidGuiAgentService.captureScreenshot(null).blockFirst();
        log.info("纠正任务 - 截图路径: {}", imagePath);

        String prompt = """
                ## 任务：判断当前页面状态是否与预期任务匹配，如果不匹配则生成新的任务列表

                ## 背景信息：
                - 原始目标: %s
                - 原始任务列表: %s
                - 当前执行到第 %d 个任务（从1开始）
                - 当前任务描述: %s

                ## 你需要做的：
                1. 仔细观察当前 Android 设备截图
                2. 判断当前页面状态是否与"当前任务描述"匹配
                3. 如果匹配（页面状态正常，可以继续执行当前任务）：
                   - 返回: <need_correct>false</need_correct>
                4. 如果不匹配（页面状态异常，需要调整任务）：
                   - 分析当前页面实际状态
                   - 根据原始目标，生成从当前页面状态到完成目标的新任务列表
                   - 返回: <need_correct>true</need_correct>
                   - 并提供新的任务列表，用 <list></list> 包裹

                ## 常见需要纠正的情况：
                - 出现了意外弹窗（广告、权限请求、登录提示等）
                - 页面加载失败或超时
                - 点击位置错误导致进入了错误页面
                - 应用崩溃或返回了主屏幕
                - 网络错误提示

                ## 返回格式示例：

                ### 不需要纠正时：
                <need_correct>false</need_correct>

                ### 需要纠正时：
                <need_correct>true</need_correct>
                <reason>发现页面出现了登录弹窗，需要先关闭弹窗再继续</reason>
                <list>
                [
                "1.点击弹窗右上角的关闭按钮 (click, 关闭登录弹窗)",
                "2.继续原任务：%s",
                ...后续任务...
                ]
                </list>

                ## 支持的操作类型：
                click, long_press, type, scroll, open_app, drag, press_home, press_back, finished, message

                请根据截图分析并返回结果：
                """.formatted(
                        originalGoal,
                        originalTaskList,
                        currentTaskIndex + 1,
                        currentTask,
                        currentTask
                );

        // 使用配置的 Vision Provider 分析页面状态
        String modelOutput = androidGuiAgentService.run(imagePath, prompt, "", LLMProvider.valueOf(visionProvider)).block();
        log.info("纠正任务 - 模型输出: {}", modelOutput);

        // 解析是否需要纠正
        boolean needCorrect = parseNeedCorrect(modelOutput);

        if (!needCorrect) {
            log.info("纠正任务 - 不需要纠正，继续执行原任务");
            return "";
        }

        // 需要纠正，提取新的任务列表
        String newTaskList = extractJsonArray(modelOutput);
        if (newTaskList != null && !newTaskList.isEmpty()) {
            log.info("纠正任务 - 生成新任务列表: {}", newTaskList);

            // 提取纠正原因
            String reason = extractCorrectionReason(modelOutput);
            if (reason != null && !reason.isEmpty()) {
                log.info("纠正原因: {}", reason);
            }

            return newTaskList;
        }

        log.warn("纠正任务 - 需要纠正但未能生成新任务列表");
        return "";
    }

    /**
     * 解析模型输出中是否需要纠正
     *
     * @param modelOutput 模型输出
     * @return true 需要纠正，false 不需要纠正
     */
    private boolean parseNeedCorrect(String modelOutput) {
        if (modelOutput == null) return false;

        // 查找 <need_correct>true</need_correct> 或 <need_correct>false</need_correct>
        Pattern pattern = Pattern.compile("<need_correct>\\s*(true|false)\\s*</need_correct>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(modelOutput);

        if (matcher.find()) {
            return "true".equalsIgnoreCase(matcher.group(1).trim());
        }

        // 如果没有找到标签，检查是否包含新的任务列表作为备用判断
        return modelOutput.contains("<list>") && modelOutput.contains("</list>");
    }

    /**
     * 提取纠正原因
     *
     * @param modelOutput 模型输出
     * @return 纠正原因，如果没有则返回 null
     */
    private String extractCorrectionReason(String modelOutput) {
        if (modelOutput == null) return null;

        Pattern pattern = Pattern.compile("<reason>(.+?)</reason>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(modelOutput);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * 从字符串中提取 JSON 数组
     * 优先从 <list></list> 标签中提取，如果没有则直接查找 JSON 数组
     * 如果都没有找到，尝试将逗号分隔的字符串转换为 JSON 数组
     *
     * @param input 输入字符串
     * @return JSON 数组字符串
     */
    public static String extractJsonArray(String input) {
        if (input == null) return null;

        // 优先从 <list></list> 标签中提取内容
        String content = extractFromListTag(input);
        if (content == null) {
            content = input;
        }

        // 从内容中提取 JSON 数组
        int start = content.indexOf('[');
        while (start != -1) {
            int count = 0;
            for (int i = start; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '[') count++;
                else if (c == ']') count--;
                if (count == 0 && c == ']') {
                    return content.substring(start, i + 1);
                }
            }
            start = content.indexOf('[', start + 1);
        }

        // 容错处理：如果没有找到 JSON 数组，尝试将逗号分隔的字符串转换为数组
        String converted = tryConvertToJsonArray(content);
        if (converted != null) {
            log.info("已将逗号分隔的字符串转换为 JSON 数组");
            return converted;
        }

        return null;
    }

    /**
     * 尝试将逗号分隔的引号字符串转换为 JSON 数组
     * 例如: "1.xxx", "2.yyy", "3.zzz" -> ["1.xxx", "2.yyy", "3.zzz"]
     *
     * @param content 内容字符串
     * @return JSON 数组字符串，如果无法转换则返回 null
     */
    private static String tryConvertToJsonArray(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        content = content.trim();

        // 检查是否包含引号分隔的字符串模式
        // 匹配 "xxx", "yyy" 或 'xxx', 'yyy' 的模式
        Pattern pattern = Pattern.compile("([\"'])(.+?)\\1\\s*,?", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        List<String> items = new ArrayList<>();
        while (matcher.find()) {
            String item = matcher.group(2).trim();
            if (!item.isEmpty()) {
                // 转义内部的双引号
                item = item.replace("\"", "\\\"");
                items.add("\"" + item + "\"");
            }
        }

        if (items.isEmpty()) {
            return null;
        }

        // 构建 JSON 数组
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(items.get(i));
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * 从 <list></list> 标签中提取内容（只提取最后一组标签）
     *
     * @param input 输入字符串
     * @return 标签内的内容，如果没有找到则返回 null
     */
    private static String extractFromListTag(String input) {
        if (input == null) return null;

        String startTag = "<list>";
        String endTag = "</list>";

        // 找最后一个 </list> 标签
        int endIndex = input.lastIndexOf(endTag);
        if (endIndex == -1) return null;

        // 在 </list> 之前找最近的 <list> 标签
        int startIndex = input.lastIndexOf(startTag, endIndex);
        if (startIndex == -1) return null;

        return input.substring(startIndex + startTag.length(), endIndex).trim();
    }

    /**
     * 从模型输出中解析多个 Action
     *
     * @param modelOutput 模型输出文本
     * @return Action 列表
     */
    private List<String> parseMultipleActions(String modelOutput) {
        List<String> actions = new ArrayList<>();

        if (modelOutput == null || modelOutput.isEmpty()) {
            return actions;
        }

        // 提取 Action: 后面的内容
        Pattern actionSectionPattern = Pattern.compile("Action:\\s*(.+?)(?=\\n\\n|$)", Pattern.DOTALL);
        Matcher actionSectionMatcher = actionSectionPattern.matcher(modelOutput);

        if (actionSectionMatcher.find()) {
            String actionSection = actionSectionMatcher.group(1).trim();

            // 匹配所有的 action
            Pattern actionPattern = Pattern.compile(
                    "(\\w+)\\s*\\([^)]*(?:<point>[^<]*</point>)[^)]*\\)|" +
                            "(\\w+)\\s*\\([^)]+\\)|" +
                            "(\\w+)\\s*\\(\\)",
                    Pattern.DOTALL
            );

            Matcher actionMatcher = actionPattern.matcher(actionSection);

            while (actionMatcher.find()) {
                String action = actionMatcher.group().trim();
                if (!action.isEmpty()) {
                    actions.add(action);
                    log.info("解析到 Action: {}", action);
                }
            }
        }

        return actions;
    }

    /**
     * 执行单个任务步骤
     *
     * @param instruction 任务指令
     * @param sink        Flux Sink
     */
    public void executeGuiAutomation(String instruction, FluxSink<String> sink) {
        if (instruction.contains(".finished")) {
            androidGuiAgentService.executeAction(new Gson().toJson(ImmutableMap.of("action", "finished")))
                    .doOnNext(System.out::println)
                    .blockLast();
            return;
        }

        // 从 Android 设备截图
        String imagePath = androidGuiAgentService.captureScreenshot(null).blockFirst();

        log.info("Android 截图路径: {}", imagePath);

        // 检查截图文件是否存在
        if (imagePath == null || imagePath.startsWith("错误") || imagePath.startsWith("截图失败")) {
            log.error("截图失败: {}", imagePath);
            sink.next("截图失败: " + imagePath + "\n");
            return;
        }

        Path path = Paths.get(imagePath);
        if (!Files.exists(path)) {
            log.error("截图文件不存在: {}", imagePath);
            sink.next("截图文件不存在: " + imagePath + "\n");
            return;
        }

        System.out.println("运行 Android GUI Agent:");
        System.out.println("截图: " + imagePath);
        System.out.println("指令: " + instruction);
        System.out.println();

        try {
            // Step 1: 分析截图
            System.out.println("Step 1: 分析 Android 截图...");

            // 使用 Android 专用的 system prompt
            String modelOutput = androidGuiAgentService.run(imagePath, instruction, Prompt.androidSystemPrompt).block();

            System.out.println("\n模型输出:\n" + modelOutput);

            // Step 2: 解析 Action
            System.out.println("\nStep 2: 解析 Actions...");
            List<String> actionList = parseMultipleActions(modelOutput);

            if (actionList.isEmpty()) {
                log.warn("未找到多个 action，回退到单个 action 解析");
                String parsedOutput = androidGuiAgentService.parseActionOutput(modelOutput);
                actionList.add(parsedOutput);
            }

            System.out.println("\n找到 " + actionList.size() + " 个操作");
            sink.next("解析到 " + actionList.size() + " 个操作\n");

            // Step 3 & 4: 执行每个 action
            for (int i = 0; i < actionList.size(); i++) {
                String actionText = actionList.get(i);
                System.out.println("\n========== 执行操作 " + (i + 1) + "/" + actionList.size() + " ==========");
                System.out.println("操作: " + actionText);
                sink.next("\n" + actionText + "\n");

                // 构造完整的模型输出格式进行解析
                String fullActionOutput = "Thought: 执行操作 " + (i + 1) + "\nAction: " + actionText;
                String parsedOutput = androidGuiAgentService.parseActionOutput(fullActionOutput);
                System.out.println("解析结果:\n" + parsedOutput);

                // 执行 action
                sink.next("\n执行第 " + (i + 1) + " 个操作:\n" + parsedOutput + "\n");

                JsonNode parsedJson = objectMapper.readTree(parsedOutput);
                String action = parsedJson.get("action").asText("");

                System.out.println("操作类型: " + action);
                System.out.println("执行中...");

                String res = androidGuiAgentService.executeAction(parsedOutput)
                        .doOnNext(System.out::println)
                        .blockLast();

                sink.next("结果: " + res + "\n");

                // 在多个 action 之间添加延迟
                if (i < actionList.size() - 1) {
                    Thread.sleep(500);
                }
            }

            System.out.println("\nAndroid GUI Agent 执行完成，共执行 " + actionList.size() + " 个操作");
            sink.next("所有操作执行完成！\n");
        } catch (Exception e) {
            log.error("执行失败", e);
            sink.next("执行失败: " + e.getMessage() + "\n");
        }
    }

    /**
     * 运行 Android GUI Agent
     *
     * @param instruction 用户指令
     * @param sink        Flux Sink
     * @return 执行结果
     */
    public String run(String instruction, FluxSink<String> sink) {
        String s = taskList(instruction);
        log.info("任务列表: {}", s);

        sink.next("任务计划:\n" + s);

        if (s == null || s.isEmpty()) {
            sink.next("任务解析失败，无法生成任务列表");
            sink.complete();
            return "error";
        }

        // 执行任务列表（带纠正逻辑）
        executeTaskListWithCorrection(s, instruction, sink, 0);

        return "finish";
    }

    /**
     * 执行任务列表（带纠正逻辑和简单操作优化）
     *
     * 性能优化：
     * 1. 简单操作（type, open_app, press_home, press_back, finished, message）直接从任务描述提取参数执行，跳过截图+LLM分析
     * 2. click + type 连续模式：执行 click 后直接执行 type，不需要重新截图分析
     * 3. 在执行每个任务前检查是否需要纠正，如果需要则切换到新的任务列表
     *
     * @param taskListJson   任务列表 JSON 字符串
     * @param originalGoal   原始目标
     * @param sink           Flux Sink
     * @param correctionDepth 纠正深度（防止无限递归）
     */
    private void executeTaskListWithCorrection(String taskListJson, String originalGoal, FluxSink<String> sink, int correctionDepth) {
        // 防止无限递归纠正
        final int MAX_CORRECTION_DEPTH = 3;
        if (correctionDepth > MAX_CORRECTION_DEPTH) {
            log.warn("纠正深度超过最大限制 {}，停止执行", MAX_CORRECTION_DEPTH);
            sink.next("纠正次数过多，停止执行");
            sink.complete();
            return;
        }

        try {
            JsonArray array = JsonParser.parseString(taskListJson).getAsJsonArray();
            int totalTasks = array.size();
            int i = 0;
            String previousTask = null; // 追踪前一个已执行的任务

            while (i < totalTasks) {
                String currentTask = array.get(i).getAsString();
                String nextTask = (i + 1 < totalTasks) ? array.get(i + 1).getAsString() : null;

                log.info("执行任务 [{}/{}]: {}", i + 1, totalTasks, currentTask);
                sink.next("\n--- 任务 [" + (i + 1) + "/" + totalTasks + "] ---\n" + currentTask);

                // 优化1: 检查是否是简单操作（open_app, press_home, press_back, finished, message）
                // 这些操作不需要坐标，可以直接执行
                if (SimpleActionExtractor.isSimpleAction(currentTask)) {
                    log.info("检测到简单操作，跳过截图+LLM分析，直接执行");
                    sink.next("⚡ 简单操作，直接执行\n");
                    executeSimpleAction(currentTask, sink);
                }
                // 优化2: 检查是否是 click + type 连续模式（向前看）
                // 当前是 click，下一个是 type，可以合并执行
                else if (nextTask != null && SimpleActionExtractor.isClickThenTypePattern(currentTask, nextTask)) {
                    log.info("检测到 click + type 连续模式，合并执行");
                    sink.next("⚡ click + type 连续模式，合并执行\n");

                    // 执行 click（需要截图+LLM定位）
                    executeGuiAutomation(currentTask, sink);

                    // 模拟人类操作：随机延迟 500-1200ms，避免被风控检测
                    int humanDelay = randomDelay(500, 1200);
                    log.info("模拟人类操作延迟: {}ms", humanDelay);
                    Thread.sleep(humanDelay);

                    // 直接执行 type（不需要重新截图分析，因为焦点已在输入框）
                    previousTask = currentTask; // 更新前一个任务
                    i++;
                    currentTask = array.get(i).getAsString();
                    log.info("合并执行任务 [{}/{}]: {}", i + 1, totalTasks, currentTask);
                    sink.next("\n--- 任务 [" + (i + 1) + "/" + totalTasks + "] (合并执行) ---\n" + currentTask);
                    executeSimpleAction(currentTask, sink);
                }
                // 普通操作：需要截图+LLM分析
                else {
                    executeGuiAutomation(currentTask, sink);
                }

                // 在执行任务后，检查是否需要纠正（跳过 finished 任务的纠正检查）
                // 只有在启用任务纠正检测时才进行检查
                if (enableTaskCorrection && !currentTask.contains("finished") && !currentTask.contains("message")) {
                    String correctedTaskList = correctTaskList(taskListJson, i, currentTask, originalGoal);

                    if (correctedTaskList != null && !correctedTaskList.isEmpty()) {
                        // 需要纠正，切换到新的任务列表
                        log.info("检测到需要纠正任务列表，切换到新列表执行");
                        sink.next("\n⚠️ 检测到页面异常，正在纠正任务列表...\n");
                        sink.next("新任务列表:\n" + correctedTaskList);

                        // 递归执行纠正后的任务列表
                        executeTaskListWithCorrection(correctedTaskList, originalGoal, sink, correctionDepth + 1);
                        return; // 退出当前任务列表的执行
                    }
                }

                // 任务间等待（根据操作类型设置不同的等待时间）
                int waitTime = getWaitTimeForTask(currentTask);
                log.debug("任务间等待: {}ms", waitTime);
                try {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                } catch (InterruptedException e) {
                    log.warn("等待被中断", e);
                    Thread.currentThread().interrupt();
                }

                // 更新前一个任务记录，用于判断 type 是否可以直接执行
                previousTask = currentTask;
                i++;
            }

            sink.next("所有任务执行结束");
            sink.complete();

        } catch (Exception e) {
            log.error("解析任务列表失败", e);
            sink.next("解析任务列表失败: " + e.getMessage());
            sink.complete();
        }
    }

    /**
     * 生成指定范围内的随机延迟时间
     * 用于模拟人类操作的随机性，避免被 App 风控系统检测
     *
     * @param minMs 最小延迟（毫秒）
     * @param maxMs 最大延迟（毫秒）
     * @return 随机延迟时间（毫秒）
     */
    private int randomDelay(int minMs, int maxMs) {
        return minMs + random.nextInt(maxMs - minMs + 1);
    }

    /**
     * 根据任务类型获取适当的等待时间
     * 不同操作需要的等待时间差异很大
     *
     * @param taskDescription 任务描述
     * @return 等待时间（毫秒）
     */
    private int getWaitTimeForTask(String taskDescription) {
        if (taskDescription == null || taskDescription.isEmpty()) {
            return randomDelay(1500, 2500);
        }

        String lower = taskDescription.toLowerCase();

        // open_app: 应用启动需要较长时间，尤其是大型App（微信、淘宝等）
        if (lower.contains("open_app") || lower.contains("打开应用")) {
            return randomDelay(2500, 4000);
        }

        // press_home: 返回主屏幕，系统级操作，较快但需要等待动画
        if (lower.contains("press_home") || lower.contains("返回主屏幕")) {
            return randomDelay(800, 1500);
        }

        // press_back: 返回上一页，较快
        if (lower.contains("press_back") || lower.contains("返回键")) {
            return randomDelay(600, 1200);
        }

        // type: 输入文字后需要等待输入法响应和界面更新
        if (lower.contains("type") || lower.contains("输入")) {
            return randomDelay(500, 1000);
        }

        // scroll: 滚动后需要等待内容加载
        if (lower.contains("scroll") || lower.contains("滚动")) {
            return randomDelay(1000, 2000);
        }

        // finished/message: 只是返回信息，几乎不需要等待
        if (lower.contains("finished") || lower.contains("message")) {
            return randomDelay(100, 300);
        }

        // click: 点击后需要等待界面响应
        if (lower.contains("click") || lower.contains("点击")) {
            return randomDelay(1500, 2500);
        }

        // 默认等待时间
        return randomDelay(1500, 2500);
    }

    /**
     * 执行简单操作（不需要截图+LLM分析）
     * 直接从任务描述中提取参数并执行
     *
     * @param taskDescription 任务描述
     * @param sink Flux Sink
     */
    private void executeSimpleAction(String taskDescription, FluxSink<String> sink) {
        Optional<String> actionJson = SimpleActionExtractor.extractAction(taskDescription);

        if (actionJson.isPresent()) {
            String parsedOutput = actionJson.get();
            log.info("直接执行简单操作: {}", parsedOutput);

            String res = androidGuiAgentService.executeAction(parsedOutput)
                    .doOnNext(System.out::println)
                    .blockLast();

            sink.next("结果: " + res + "\n");
        } else {
            log.warn("无法从任务描述中提取简单操作，回退到完整流程: {}", taskDescription);
            sink.next("无法直接提取操作，使用完整流程\n");
            executeGuiAutomation(taskDescription, sink);
        }
    }

    /**
     * 设置操作的目标设备
     *
     * @param deviceSerial 设备序列号
     */
    public void setDeviceSerial(String deviceSerial) {
        androidGuiAgentService.setDeviceSerial(deviceSerial);
    }
}