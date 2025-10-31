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
                根据图片和需求帮我拆分下操作列表.
                
                支持的操作:
                click(point='<point>x1 y1</point>')
                left_double(point='<point>x1 y1</point>')
                right_single(point='<point>x1 y1</point>')
                drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')
                hotkey(key='ctrl c') # Split keys with a space and use lowercase. Also, do not use more than 3 keys in one hotkey action.
                type(content='xxx') # Use escape characters \\\\', \\\\\\", and \\\\n in content part to ensure we can parse the content in normal python string format. If you want to submit your input, use \\\\n at the end of content.\s
                scroll(point='<point>x1 y1</point>', direction='down or up or right or left') # Show more information on the `direction` side.
                wait() #Sleep for 5s and take a screenshot to check for any changes.
                                   finished(content='xxx') # Use escape characters \\\\', \\\\", and \\\\n in content part to ensure we can parse the content in normal python string format.
                //主要是根据你分析图片返回给用户一些信息
                message(content='')
                
                
                秉承的一些原则:
                1.如果发现某个界面已经是打开状态了,就不用点击这个界面的图标再次触发
                
                
                例子1:
                需求:打开Terminal (如果你发现Terminal本身是打开的,你就不需点击Terminal图标,而是点击Terminal本身,让其获得焦点)
                返回:(一定要是json格式)
                [
                "1.点击Terminal图标 (click)",
                "2.finished(content='finish')"
                ]
                
                例子2:
                需求:滚动滑条
                返回:
                [
                "1.点击idea的Code编辑区域 (click)",
                "2.在编辑界面滑动滚轮 (scroll)"
                "3.finished(content='finish')"
                ]
                
                例子3:
                需求:识别下打开的是什么软件
                [
                "1.在当前截图中,识别下打开的是什么软件 (message)",
                "2.finished(content='finish')"
                ]
                
                
                
                -----
                需求:%s
                返回:(一定要是json<json array>格式)
                
                
                """.formatted(instruction);
        String modelOutput = guiAgentService.run(imagePath, prompt, "").block();
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

            guiAgentService.executeAction(new Gson().toJson(ImmutableMap.of("action","finished")))
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


    public void run(String instruction, FluxSink<String> sink) {
        String s = taskList(instruction);
        System.out.println(s);

        sink.next("任务计划:\n" + s);

        JsonArray array = JsonParser.parseString(s).getAsJsonArray();
        array.forEach(it -> {
            String str = it.getAsString();
            log.info("run:{}", str);
            run2(str, sink);
        });
        sink.complete();
    }

}
