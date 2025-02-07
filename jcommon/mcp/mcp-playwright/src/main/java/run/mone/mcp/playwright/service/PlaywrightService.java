package run.mone.mcp.playwright.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microsoft.playwright.Page;

@Service
public class PlaywrightService {
    private static final Logger logger = LoggerFactory.getLogger(PlaywrightService.class);


    /**
     * 截取屏幕截图
     *
     * @return 截图文件的路径
     */
    public String takeScreenshot(Page page) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "screenshot_" + timestamp + ".png";
            Path screenshotPath = Paths.get("screenshots", fileName);

            // 确保目录存在
            screenshotPath.getParent().toFile().mkdirs();

            // 进行截图
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));

            logger.info("Screenshot saved to: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            throw new RuntimeException("Screenshot failed", e);
        }
    }

    /**
     * 移动鼠标到指定坐标
     *
     * @param x X坐标
     * @param y Y坐标
     */
    public void moveMouse(Page page, int x, int y) {
        try {
            page.mouse().move(x, y);
            logger.info("Mouse moved to coordinates: x={}, y={}", x, y);
        } catch (Exception e) {
            logger.error("Failed to move mouse to coordinates: x={}, y={}", x, y, e);
            throw new RuntimeException("Mouse movement failed", e);
        }
    }
} 