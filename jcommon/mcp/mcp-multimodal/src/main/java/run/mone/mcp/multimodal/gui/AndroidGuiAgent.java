package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.multimodal.config.AndroidConfig;
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.service.AndroidGuiAgentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

        // 使用 DOUBAO_VISION 生成任务列表
        String modelOutput = androidGuiAgentService.run(imagePath, prompt, "", LLMProvider.DOUBAO_VISION).block();
        log.info("模型输出: {}", modelOutput);
        modelOutput = extractJsonArray(modelOutput);
        return modelOutput;
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
    public void run2(String instruction, FluxSink<String> sink) {
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

        try {
            JsonArray array = JsonParser.parseString(s).getAsJsonArray();
            array.forEach(it -> {
                String str = it.getAsString();
                log.info("执行任务: {}", str);
                run2(str, sink);

                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
                    log.warn("等待被中断", e);
                }
            });
            sink.next("所有任务执行结束");
            sink.complete();
        } catch (Exception e) {
            log.error("解析任务列表失败", e);
            sink.next("解析任务列表失败: " + e.getMessage());
            sink.complete();
        }

        return "finish";
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