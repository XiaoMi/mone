package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import run.mone.mcp.multimodal.config.Prompt;
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


    public String taskList(String instruction) {
        String imagePath = multimodalService.captureScreenshotWithRobot(null).blockFirst();
        String prompt = """
                根据图片和需求帮我拆分下操作列表.
                支持的操作:
                click(start_box='[x1, y1, x2, y2]')
                left_double(start_box='[x1, y1, x2, y2]')
                right_single(start_box='[x1, y1, x2, y2]')
                drag(start_box='[x1, y1, x2, y2]', end_box='[x3, y3, x4, y4]')
                hotkey(key='')
                type(content='') #If you want to submit your input, use "\\n" at the end of `content`.
                scroll(start_box='[x1, y1, x2, y2]', direction='down or up or right or left')
                finished
                
                
                秉承的一些原则:
                1.如果发现某个界面已经是打开状态了,就不用点击这个界面的图标再次触发
                
                
                例子1:
                需求:打开Terminal (如果你发现Terminal本身是打开的,你就不需点击Terminal图标,而是点击Terminal本身,让其获得焦点)
                返回:(一定要是json格式)
                [
                "1.点击Terminal图标 (click)",
                "2.finished"
                ]
                
                例子2:
                需求:滚动滑条
                返回:
                [
                "1.点击idea的Code编辑区域 (click)",
                "2.在编辑界面滑动滚轮 (scroll)"
                "3.finished"
                ]
                
                
                
                -----
                需求:%s
                返回:(一定要是json格式)
                
                
                """.formatted(instruction);
        String modelOutput = guiAgentService.run(imagePath, prompt, "").block();
        return modelOutput;
    }


    public void run2(String instruction) {
        if (instruction.contains(".finished")) {
            guiAgentService.executeAction("finished")
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
            //String modelOutput2 = guiAgentService.run(visualizedPath, "我的鼠标现在就是那个红点,你帮我计算下是否在正确的位置上,根据你的判断返回新的click信息   \n原始需求:"+instruction).block();
            //parsedOutput = guiAgentService.parseActionOutput(modelOutput2);

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


    public void run(String instruction) {
        String s = taskList(instruction);
        System.out.println(s);
        JsonArray array = JsonParser.parseString(s).getAsJsonArray();
        array.forEach(it -> {
            String str = it.getAsString();
            log.info("run:{}", str);
            run2(str);
        });


    }

}
