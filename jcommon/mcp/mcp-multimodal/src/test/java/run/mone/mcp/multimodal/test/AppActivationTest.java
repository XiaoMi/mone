package run.mone.mcp.multimodal.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.util.List;

/**
 * 应用激活和截图测试类
 * 演示如何在多Space场景下正确激活应用并截图
 */
@SpringBootTest
@Slf4j
public class AppActivationTest {

    @Autowired
    private MultimodalService multimodalService;

    /**
     * 测试获取运行中的应用列表
     */
    @Test
    public void testGetRunningApplications() {
        List<String> apps = multimodalService.getRunningApplications().blockFirst();
        log.info("当前运行的应用数量: {}", apps.size());
        apps.forEach(app -> log.info("  - {}", app));
    }

    /**
     * 测试获取已安装的应用列表
     */
    @Test
    public void testGetInstalledApplications() {
        List<String> apps = multimodalService.getInstalledApplications().blockFirst();
        log.info("已安装的应用数量: {}", apps.size());
        apps.forEach(app -> log.info("  - {}", app));
    }

    /**
     * 测试激活Chrome浏览器
     */
    @Test
    public void testActivateChrome() {
        // 支持多种写法
        String result1 = multimodalService.activateApplication("chrome").blockFirst();
        log.info("激活Chrome结果: {}", result1);

        // 或者使用中文
        String result2 = multimodalService.activateApplication("谷歌浏览器").blockFirst();
        log.info("激活Chrome结果(中文): {}", result2);
    }

    /**
     * 测试激活微信
     */
    @Test
    public void testActivateWeChat() {
        // 支持多种写法
        String result1 = multimodalService.activateApplication("微信").blockFirst();
        log.info("激活微信结果: {}", result1);

        String result2 = multimodalService.activateApplication("wechat").blockFirst();
        log.info("激活WeChat结果: {}", result2);
    }

    /**
     * 测试激活华泰证券
     */
    @Test
    public void testActivateHuatai() {
        // 支持多种写法
        String result1 = multimodalService.activateApplication("华泰证券").blockFirst();
        log.info("激活华泰证券结果: {}", result1);

        String result2 = multimodalService.activateApplication("huatai").blockFirst();
        log.info("激活华泰结果: {}", result2);
    }

    /**
     * 测试带应用激活的截图功能 - Chrome
     */
    @Test
    public void testCaptureScreenshotWithChromeActivation() {
        log.info("开始测试激活Chrome并截图...");
        
        // 激活Chrome并截图
        String result = multimodalService.captureScreenshotWithAppActivation("chrome", null).blockFirst();
        log.info("截图结果: {}", result);
    }

    /**
     * 测试带应用激活的截图功能 - 微信
     */
    @Test
    public void testCaptureScreenshotWithWeChatActivation() {
        log.info("开始测试激活微信并截图...");
        
        // 激活微信并截图
        String result = multimodalService.captureScreenshotWithAppActivation("微信", null).blockFirst();
        log.info("截图结果: {}", result);
    }

    /**
     * 测试带应用激活的截图功能 - 华泰证券
     */
    @Test
    public void testCaptureScreenshotWithHuataiActivation() {
        log.info("开始测试激活华泰证券并截图...");
        
        // 激活华泰证券并截图
        String result = multimodalService.captureScreenshotWithAppActivation("华泰证券", null).blockFirst();
        log.info("截图结果: {}", result);
    }

    /**
     * 测试检查应用是否运行
     */
    @Test
    public void testIsApplicationRunning() {
        boolean chromeRunning = multimodalService.isApplicationRunning("Google Chrome");
        log.info("Chrome是否运行: {}", chromeRunning);

        boolean wechatRunning = multimodalService.isApplicationRunning("WeChat");
        log.info("WeChat是否运行: {}", wechatRunning);

        boolean huataiRunning = multimodalService.isApplicationRunning("专业版Ⅲ(MAC)");
        log.info("华泰证券是否运行: {}", huataiRunning);
    }

    /**
     * 完整流程测试：激活应用 -> 截图 -> 执行操作
     */
    @Test
    public void testCompleteWorkflow() {
        log.info("=== 开始完整工作流程测试 ===");
        
        // 1. 获取运行中的应用
        List<String> runningApps = multimodalService.getRunningApplications().blockFirst();
        log.info("Step 1: 发现 {} 个运行中的应用", runningApps.size());
        
        // 2. 激活Chrome
        log.info("Step 2: 激活Chrome浏览器...");
        String activateResult = multimodalService.activateApplication("chrome").blockFirst();
        log.info("激活结果: {}", activateResult);
        
        // 3. 截图
        log.info("Step 3: 截取Chrome窗口...");
        String screenshotPath = multimodalService.captureScreenshotWithRobot(null).blockFirst();
        log.info("截图保存至: {}", screenshotPath);
        
        // 4. 可以继续执行其他操作（点击、输入等）
        log.info("Step 4: 现在可以在Chrome窗口上执行其他操作了");
        
        log.info("=== 完整工作流程测试完成 ===");
    }
}

