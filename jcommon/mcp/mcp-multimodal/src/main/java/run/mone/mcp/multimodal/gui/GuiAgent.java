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
import run.mone.mcp.multimodal.config.Prompt;
import run.mone.mcp.multimodal.service.GuiAgentService;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public String taskList(String instruction) {
        String imagePath = multimodalService.captureScreenshotWithRobot(null).blockFirst();
        log.info("image path:{}", imagePath);
        String prompt = """
                根据图片和需求帮我拆分下操作列表。每一步都要明确指定操作类型和参数。
                
                ## 支持的操作类型:
                click(point='<point>x1 y1</point>') - 点击操作
                type(content='具体内容') - 输入文本，content必须明确指定要输入的具体内容
                left_double(point='<point>x1 y1</point>') - 双击操作
                right_single(point='<point>x1 y1</point>') - 右键点击
                drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>') - 拖拽操作
                hotkey(key='ctrl c') - 快捷键，多个键用空格分隔，使用小写，最多3个键
                scroll(point='<point>x1 y1</point>', direction='down/up/right/left') - 滚动操作
                wait() - 等待5秒
                finished(content='完成信息') - 任务完成
                message(content='消息内容') - 向用户返回分析信息
                
                ## 重要规则:
                1. 每一步必须明确标注操作类型（click、type、scroll等）
                2. 对于type操作，必须在括号内明确写出 content='具体要输入的内容'
                3. 对于click操作，必须描述清楚点击的目标元素
                4. 操作步骤要符合人类操作习惯：先点击输入框获取焦点，再输入内容
                5. 如果界面已经是打开状态，不要重复点击图标
                6. 每一步的描述格式：序号.操作描述 (操作类型, 参数说明)
                
                ## 例子1 - 打开应用:
                需求: 打开Terminal
                分析: 如果Terminal已经打开，只需点击窗口获取焦点；如果未打开，需要点击图标启动
                返回: (必须是json array格式)
                [
                "1.点击Terminal图标打开应用 (click, 定位到Dock栏或桌面上的Terminal图标)",
                "2.finished(content='已成功打开Terminal')"
                ]
                
                ## 例子2 - 表单填写(重点参考):
                需求: 在登录界面输入用户名admin和密码123456，然后点击登录按钮
                分析: 需要分别点击输入框，输入内容，最后点击按钮
                返回:
                [
                "1.点击用户名输入框 (click, 定位到界面中用户名对应的输入框位置)",
                "2.输入用户名admin (type, content='admin')",
                "3.点击密码输入框 (click, 定位到界面中密码对应的输入框位置)",
                "4.输入密码123456 (type, content='123456')",
                "5.点击登录按钮 (click, 定位到界面中的登录按钮位置)",
                "6.finished(content='已在登录界面输入用户名admin和密码123456，并点击登录按钮')"
                ]
                
                ## 例子3 - 证券交易表单(最接近您的场景):
                需求: 在国债逆回购界面输入证券代码204001，输入融券数量2手，并点击确定按钮
                分析: 这是一个典型的表单填写场景，需要依次点击输入框并输入内容
                返回:
                [
                "1.点击证券代码输入框 (click, 定位到界面中证券代码对应的输入框位置)",
                "2.输入证券代码204001 (type, content='204001')",
                "3.点击融券数量输入框 (click, 定位到界面中融券数量对应的输入框位置)",
                "4.输入融券数量2 (type, content='2')",
                "5.点击确定按钮 (click, 定位到界面中红色的确定按钮位置)",
                "6.finished(content='已在国债逆回购界面输入证券代码204001，输入融券数量2手，并点击确定按钮')"
                ]
                
                ## 例子4 - 滚动操作:
                需求: 滚动查看更多内容
                返回:
                [
                "1.点击目标区域获取焦点 (click, 定位到需要滚动的内容区域)",
                "2.向下滚动查看更多内容 (scroll, direction='down')",
                "3.finished(content='已完成滚动操作')"
                ]
                
                ## 例子5 - 信息识别:
                需求: 识别下打开的是什么软件
                返回:
                [
                "1.分析当前截图识别软件 (message, 分析截图中的窗口标题、界面特征等信息)",
                "2.finished(content='finish')"
                ]
                
                -----
                现在请根据截图和下面的需求生成操作列表：
                
                需求: %s
                
                返回: (必须是json array格式，每一步都要明确标注操作类型和参数)
                
                """.formatted(instruction);
        String modelOutput = guiAgentService.run(imagePath, prompt, "").block();
        log.warn("modelOutput:{}", modelOutput);
        modelOutput = extractJsonArray(modelOutput);
        return modelOutput;
    }

    /**
     * 从字符串中提取一个JSON数组（格式: [ ... ]），如果没有找到，返回null。
     *
     * @param input 输入字符串
     * @return 提取到的JSON数组字符串，或者null
     */
    public static String extractJsonArray(String input) {
        if (input == null) return null;
        int start = input.indexOf('[');
        while (start != -1) {
            int count = 0;
            for (int i = start; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '[') count++;
                else if (c == ']') count--;
                if (count == 0 && c == ']') {
                    // Found a matched JSON array
                    return input.substring(start, i + 1);
                }
            }
            // There could be another [ after this, try again
            start = input.indexOf('[', start + 1);
        }
        return null;
    }

    /**
     * 从模型输出中解析多个 Action
     * 支持格式：
     * Action: click(point='<point>317 582</point>')
     * type(content='你好')
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

            // 匹配所有的 action，支持多种格式
            // 匹配格式：action_name(param1='value1', param2='value2')
            Pattern actionPattern = Pattern.compile(
                    "(\\w+)\\s*\\([^)]*(?:<point>[^<]*</point>)[^)]*\\)|" +  // 包含 <point> 的 action
                            "(\\w+)\\s*\\([^)]+\\)|" +  // 普通的 action
                            "(\\w+)\\s*\\(\\)",  // 无参数的 action
                    Pattern.DOTALL
            );

            Matcher actionMatcher = actionPattern.matcher(actionSection);

            while (actionMatcher.find()) {
                String action = actionMatcher.group().trim();
                if (!action.isEmpty()) {
                    actions.add(action);
                    log.info("Parsed action: {}", action);
                }
            }
        }

        return actions;
    }


    public void run2(String instruction, FluxSink<String> sink) {
        if (instruction.contains(".finished")) {
            guiAgentService.executeAction(new Gson().toJson(ImmutableMap.of("action", "finished")))
                    .doOnNext(System.out::println)
                    .blockLast();
            return;
        }

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

            String modelOutput = guiAgentService.run(imagePath, instruction, Prompt.systemPrompt).block();

            System.out.println("\nModel output:\n" + modelOutput);

            // Step 2: Parse multiple actions from model output
            System.out.println("\nStep 2: Parsing actions...");
            List<String> actionList = parseMultipleActions(modelOutput);

            if (actionList.isEmpty()) {
                log.warn("No actions found in model output, fallback to single action parsing");
                // 如果没有解析到多个 action，使用原来的单个 action 解析方式
                String parsedOutput = guiAgentService.parseActionOutput(modelOutput);
                actionList.add(parsedOutput);
            }

            System.out.println("\nFound " + actionList.size() + " action(s) to execute");
            sink.next("解析到 " + actionList.size() + " 个操作\n");

            // Step 3 & 4: 对每个 action 进行可视化和执行
            for (int i = 0; i < actionList.size(); i++) {
                String actionText = actionList.get(i);
                System.out.println("\n========== Executing Action " + (i + 1) + "/" + actionList.size() + " ==========");
                System.out.println("Action text: " + actionText);
                sink.next("\n" + actionText + "\n");

                // 构造完整的模型输出格式进行解析
                String fullActionOutput = "Thought: Executing action " + (i + 1) + "\nAction: " + actionText;
                String parsedOutput = guiAgentService.parseActionOutput(fullActionOutput);
                System.out.println("Parsed action:\n" + parsedOutput);

                // 可视化 action（只对第一个或最后一个 action 进行可视化，避免生成太多图片）
                if (i == 0 || i == actionList.size() - 1) {
                    try {
                        String outputImagePath = path.getParent().resolve("output_action" + (i + 1) + "_" + path.getFileName()).toString();
                        String visualizedPath = guiAgentService.visualizeAction(imagePath, parsedOutput, outputImagePath);
                        System.out.println("Visualization saved to: " + visualizedPath);
                    } catch (Exception e) {
                        log.warn("Failed to visualize action: " + e.getMessage());
                    }
                }

                // 执行 action
                sink.next("\n执行第 " + (i + 1) + " 个操作:\n" + parsedOutput + "\n");

                JsonNode parsedJson = objectMapper.readTree(parsedOutput);
                String action = parsedJson.get("action").asText("");

                System.out.println("Action type: " + action);
                System.out.println("Executing...");

                String res = guiAgentService.executeAction(parsedOutput)
                        .doOnNext(System.out::println)
                        .blockLast();

                sink.next("结果: " + res + "\n");

                // 在多个 action 之间添加延迟，确保操作完成
                if (i < actionList.size() - 1) {
                    Thread.sleep(500);
                }
            }

            System.out.println("\nGUI agent process completed. Total actions executed: " + actionList.size());
            sink.next("所有操作执行完成！\n");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            sink.next("执行失败: " + e.getMessage() + "\n");
        }
    }


    public String run(String instruction, FluxSink<String> sink) {
        String s = taskList(instruction);
        log.info("task list:{}", s);

        sink.next("任务计划:\n" + s);

        JsonArray array = JsonParser.parseString(s).getAsJsonArray();
        array.forEach(it -> {
            String str = it.getAsString();
            log.info("run:{}", str);
            run2(str, sink);

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                log.warn("sleep 500ms Interrupted", e);
            }
        });
        sink.next("所有任务执行结束");
        sink.complete();
        return "finish";
    }

}
