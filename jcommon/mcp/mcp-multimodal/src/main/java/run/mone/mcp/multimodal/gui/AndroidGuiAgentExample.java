package run.mone.mcp.multimodal.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;
import run.mone.mcp.multimodal.android.AndroidService;
import run.mone.mcp.multimodal.service.AndroidGuiAgentService;

import javax.annotation.PostConstruct;

/**
 * Android GUI Agent 示例
 * 演示如何使用 Android GUI Agent 打开微信
 *
 * 运行前请确保：
 * 1. Android 设备已通过 ADB 连接
 * 2. 设备已安装 ADBKeyboard（用于中文输入）
 * 3. 设备已安装微信
 *
 * @author goodjava@qq.com
 * @date 2025/12/13
 */
@Slf4j
@RequiredArgsConstructor
//@Service  // 取消注释以启用自动运行
public class AndroidGuiAgentExample {

    private final AndroidService androidService;
    private final AndroidGuiAgentService androidGuiAgentService;
    private final AndroidGuiAgent androidGuiAgent;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 示例：打开微信
     */
    @PostConstruct
    public void init() throws Exception {
        // 创建 Flux Sink 用于接收执行结果
        UnicastProcessor<String> processor = UnicastProcessor.create();
        FluxSink<String> sink = processor.sink();

        // 订阅结果输出
        processor.subscribe(
                msg -> System.out.println("[Android Agent] " + msg),
                error -> System.err.println("[Error] " + error.getMessage()),
                () -> System.out.println("[Android Agent] 任务完成")
        );

        // 示例1：直接打开微信应用
        // String instruction = "打开微信";

        // 示例2：打开微信并进入聊天
        // String instruction = "打开微信，点击第一个聊天";

        // 示例3：打开微信发送消息
        String instruction = "打开微信";

        log.info("开始执行指令: {}", instruction);
        androidGuiAgent.run(instruction, sink);
    }

    /**
     * 独立运行示例（不依赖 Spring 自动注入）
     */
    public static void main(String[] args) {
        System.out.println("=== Android GUI Agent Example ===");
        System.out.println("演示：打开微信");
        System.out.println();

        // 注意：实际运行需要 Spring 环境
        // 可以通过以下方式启动：
        // 1. 在 Spring Boot 应用中取消 @Service 注释
        // 2. 或者使用 SpringApplication.run() 启动完整应用

        System.out.println("请通过 Spring Boot 应用启动此示例：");
        System.out.println("1. 取消 @Service 注释");
        System.out.println("2. 确保 Android 设备已连接: adb devices");
        System.out.println("3. 启动应用后会自动执行打开微信的指令");
    }

    /**
     * 手动执行指令的方法（供外部调用）
     *
     * @param instruction 要执行的指令
     */
    public void executeInstruction(String instruction) {
        UnicastProcessor<String> processor = UnicastProcessor.create();
        FluxSink<String> sink = processor.sink();

        processor.subscribe(
                msg -> System.out.println("[Android Agent] " + msg),
                error -> System.err.println("[Error] " + error.getMessage()),
                () -> System.out.println("[Android Agent] 任务完成")
        );

        log.info("执行指令: {}", instruction);
        androidGuiAgent.run(instruction, sink);
    }

    /**
     * 简单操作示例：直接使用 AndroidService 打开微信
     * 不经过 GUI Agent 分析，直接执行
     */
    public void openWeChatDirectly() {
        System.out.println("直接打开微信（不使用 GUI Agent）...");

        // 方式1：通过应用名打开
        androidService.openApp("微信", null)
                .subscribe(
                        result -> System.out.println("结果: " + result),
                        error -> System.err.println("错误: " + error.getMessage())
                );

        // 方式2：通过包名打开
        // androidService.launchApp("com.tencent.mm", null)
        //         .subscribe(result -> System.out.println("结果: " + result));
    }

    /**
     * 截图示例
     */
    public void takeScreenshot() {
        System.out.println("截取 Android 设备屏幕...");

        androidService.screenshot(null, null)
                .subscribe(
                        path -> System.out.println("截图保存至: " + path),
                        error -> System.err.println("截图失败: " + error.getMessage())
                );
    }

    /**
     * 获取设备列表示例
     */
    public void listDevices() {
        System.out.println("获取已连接的 Android 设备...");

        androidService.getDevices()
                .subscribe(
                        devices -> {
                            if (devices.isEmpty()) {
                                System.out.println("没有已连接的设备");
                            } else {
                                System.out.println("已连接设备:");
                                devices.forEach(d -> System.out.println("  - " + d));
                            }
                        },
                        error -> System.err.println("获取设备失败: " + error.getMessage())
                );
    }
}