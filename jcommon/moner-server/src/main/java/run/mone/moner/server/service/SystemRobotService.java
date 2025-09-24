package run.mone.moner.server.service;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SystemRobotService {
    private static final Logger logger = LoggerFactory.getLogger(SystemRobotService.class);
    private final Robot robot;

    public SystemRobotService() {
        try {
            System.setProperty("java.awt.headless", "false");
            this.robot = new Robot();
        } catch (AWTException e) {
            logger.error("Failed to initialize Robot", e);
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }

    /**
     * 截取全屏截图
     * @return 截图文件的路径
     */
    public String takeScreenshot() {
        try {
            // 获取屏幕尺寸
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            
            // 捕获屏幕
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);

            // 生成文件路径
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "system_screenshot_" + timestamp + ".png";
            Path screenshotPath = Paths.get("screenshots", fileName);
            
            // 确保目录存在
            screenshotPath.getParent().toFile().mkdirs();
            
            // 保存图片
            ImageIO.write(screenCapture, "PNG", screenshotPath.toFile());
            
            logger.info("System screenshot saved to: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (Exception e) {
            logger.error("Failed to take system screenshot", e);
            throw new RuntimeException("System screenshot failed", e);
        }
    }

    /**
     * 移动鼠标到指定坐标
     * @param x X坐标
     * @param y Y坐标
     */
    public void moveMouse(int x, int y) {
        try {
            robot.mouseMove(x, y);
            // 添加小延迟确保鼠标移动完成
            robot.delay(100);
            logger.info("System mouse moved to coordinates: x={}, y={}", x, y);
        } catch (Exception e) {
            logger.error("Failed to move system mouse to coordinates: x={}, y={}", x, y, e);
            throw new RuntimeException("System mouse movement failed", e);
        }
    }

    /**
     * 获取当前鼠标位置
     * @return Point对象，包含x和y坐标
     */
    public Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
} 