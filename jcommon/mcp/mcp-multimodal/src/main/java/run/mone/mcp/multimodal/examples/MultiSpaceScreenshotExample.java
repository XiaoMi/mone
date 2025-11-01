package run.mone.mcp.multimodal.examples;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.util.List;

/**
 * 多Space场景下的截图和应用激活示例
 * 
 * 演示如何在macOS多Space环境中：
 * 1. 激活指定应用（自动切换Space）
 * 2. 截取正确的窗口内容
 * 3. 执行后续操作
 * 
 * @author goodjava@qq.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiSpaceScreenshotExample {

    private final MultimodalService multimodalService;

    /**
     * 示例1: 激活Chrome浏览器并截图
     * 
     * 使用场景：
     * - Chrome在另一个Space中
     * - 需要截取Chrome页面进行分析
     */
    public String captureChrome() {
        log.info("=== 示例1: 激活Chrome并截图 ===");
        
        // 方式1：一步完成（推荐）
        String screenshot = multimodalService
            .captureScreenshotWithAppActivation("chrome", null)
            .blockFirst();
        
        log.info("Chrome截图保存在: {}", screenshot);
        return screenshot;
    }

    /**
     * 示例2: 激活微信并截图
     * 
     * 使用场景：
     * - 监控微信消息
     * - 自动回复
     */
    public String captureWeChat() {
        log.info("=== 示例2: 激活微信并截图 ===");
        
        // 使用中文别名
        String screenshot = multimodalService
            .captureScreenshotWithAppActivation("微信", null)
            .blockFirst();
        
        log.info("微信截图保存在: {}", screenshot);
        return screenshot;
    }

    /**
     * 示例3: 激活华泰证券并截图
     * 
     * 使用场景：
     * - 股票行情监控
     * - 自动交易
     */
    public String captureHuatai() {
        log.info("=== 示例3: 激活华泰证券并截图 ===");
        
        String screenshot = multimodalService
            .captureScreenshotWithAppActivation("华泰证券", null)
            .blockFirst();
        
        log.info("华泰证券截图保存在: {}", screenshot);
        return screenshot;
    }

    /**
     * 示例4: 完整工作流 - 激活、截图、操作
     * 
     * 使用场景：
     * - 需要在Chrome中执行一系列操作
     * - 填写表单、点击按钮等
     */
    public void chromeAutomation() {
        log.info("=== 示例4: Chrome自动化工作流 ===");
        
        try {
            // 1. 激活Chrome（会自动切换到Chrome所在的Space）
            log.info("Step 1: 激活Chrome...");
            String result = multimodalService.activateApplication("chrome").blockFirst();
            log.info("激活结果: {}", result);
            
            // 2. 等待窗口稳定
            Thread.sleep(500);
            
            // 3. 截图
            log.info("Step 2: 截取Chrome窗口...");
            String screenshot = multimodalService.captureScreenshotWithRobot(null).blockFirst();
            log.info("截图保存在: {}", screenshot);
            
            // 4. 执行操作（假设要点击地址栏）
            log.info("Step 3: 点击地址栏...");
            multimodalService.click(400, 50).blockFirst();
            
            // 5. 输入网址
            log.info("Step 4: 输入网址...");
            multimodalService.typeTextV2("https://example.com").blockFirst();
            
            // 6. 按回车
            log.info("Step 5: 按回车键...");
            multimodalService.pressHotkey(List.of("ENTER")).blockFirst();
            
            log.info("Chrome自动化完成!");
            
        } catch (InterruptedException e) {
            log.error("操作被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 示例5: 多应用监控
     * 
     * 使用场景：
     * - 依次截取多个应用的窗口
     * - 生成报告或监控面板
     */
    public void monitorMultipleApps() {
        log.info("=== 示例5: 多应用监控 ===");
        
        String[] apps = {"chrome", "微信", "华泰证券"};
        
        for (String app : apps) {
            try {
                log.info("正在处理应用: {}", app);
                
                // 检查应用是否运行
                String actualAppName = getActualAppName(app);
                if (!multimodalService.isApplicationRunning(actualAppName)) {
                    log.warn("应用 {} 未运行，跳过", app);
                    continue;
                }
                
                // 激活并截图
                String screenshot = multimodalService
                    .captureScreenshotWithAppActivation(app, null)
                    .blockFirst();
                
                log.info("  ✓ 截图保存: {}", screenshot);
                
                // 等待一段时间再处理下一个
                Thread.sleep(2000);
                
            } catch (InterruptedException e) {
                log.error("操作被中断", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log.info("多应用监控完成!");
    }

    /**
     * 示例6: 智能应用选择
     * 
     * 使用场景：
     * - 根据用户指令自动选择要操作的应用
     */
    public String smartAppSelection(String instruction) {
        log.info("=== 示例6: 智能应用选择 ===");
        log.info("用户指令: {}", instruction);
        
        String targetApp = null;
        
        // 简单的关键词匹配
        if (instruction.contains("chrome") || instruction.contains("浏览器") || instruction.contains("网页")) {
            targetApp = "chrome";
        } else if (instruction.contains("微信") || instruction.contains("wechat")) {
            targetApp = "微信";
        } else if (instruction.contains("华泰") || instruction.contains("股票") || instruction.contains("证券")) {
            targetApp = "华泰证券";
        }
        
        if (targetApp != null) {
            log.info("识别到目标应用: {}", targetApp);
            String screenshot = multimodalService
                .captureScreenshotWithAppActivation(targetApp, null)
                .blockFirst();
            log.info("截图完成: {}", screenshot);
            return screenshot;
        } else {
            log.warn("无法识别目标应用，使用默认截图");
            return multimodalService.captureScreenshotWithRobot(null).blockFirst();
        }
    }

    /**
     * 示例7: 查看系统应用状态
     * 
     * 使用场景：
     * - 调试时查看可用的应用
     * - 生成应用列表
     */
    public void listApplications() {
        log.info("=== 示例7: 查看系统应用状态 ===");
        
        // 获取运行中的应用
        List<String> runningApps = multimodalService.getRunningApplications().blockFirst();
        log.info("当前运行的应用 ({} 个):", runningApps.size());
        runningApps.forEach(app -> log.info("  ✓ {}", app));
        
        log.info("");
        
        // 获取已安装的应用（前20个）
        List<String> installedApps = multimodalService.getInstalledApplications().blockFirst();
        log.info("已安装的应用 (前20个，总共 {} 个):", installedApps.size());
        installedApps.stream()
            .limit(20)
            .forEach(app -> log.info("  • {}", app));
    }

    /**
     * 辅助方法：获取应用的实际名称
     * 简化的映射逻辑，实际使用中会从 APP_NAME_MAPPING 查找
     */
    private String getActualAppName(String alias) {
        return switch (alias.toLowerCase()) {
            case "chrome", "谷歌浏览器" -> "Google Chrome";
            case "微信", "wechat", "wx" -> "WeChat";
            case "华泰证券", "华泰", "huatai" -> "专业版Ⅲ(MAC)";
            default -> alias;
        };
    }

    /**
     * 运行所有示例
     */
    public void runAllExamples() {
        log.info("======================================");
        log.info("  开始运行所有示例");
        log.info("======================================");
        log.info("");
        
        try {
            // 示例1-3: 基本截图
            captureChrome();
            Thread.sleep(1000);
            
            captureWeChat();
            Thread.sleep(1000);
            
            captureHuatai();
            Thread.sleep(1000);
            
            // 示例4: 完整工作流
            chromeAutomation();
            Thread.sleep(2000);
            
            // 示例5: 多应用监控
            monitorMultipleApps();
            
            // 示例6: 智能选择
            smartAppSelection("请帮我截取浏览器的内容");
            Thread.sleep(1000);
            
            // 示例7: 应用列表
            listApplications();
            
        } catch (InterruptedException e) {
            log.error("示例运行被中断", e);
            Thread.currentThread().interrupt();
        }
        
        log.info("");
        log.info("======================================");
        log.info("  所有示例运行完成！");
        log.info("======================================");
    }
}

