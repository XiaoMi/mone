package run.mone.moner.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.Point;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SystemRobotServiceTest {

    @Autowired
    private SystemRobotService systemRobotService;

    @Test
    public void testTakeScreenshot() throws Exception {
        // 执行截图
        String screenshotPath = systemRobotService.takeScreenshot();
        
        // 验证文件是否存在
        Path path = Paths.get(screenshotPath);
        assertTrue(Files.exists(path), "Screenshot file should exist");
        
        // 验证文件大小是否大于0
        long fileSize = Files.size(path);
        assertTrue(fileSize > 0, "Screenshot file should not be empty");
        
        // 清理测试文件
        Files.delete(path);
    }

    @Test
    public void testMouseOperations() throws Exception {
        // 保存初始鼠标位置
        Point initialPosition = systemRobotService.getMousePosition();
        
        // 测试移动鼠标到特定位置
        int testX = 1843;
        int testY = 711;
        systemRobotService.moveMouse(testX, testY);
        
        // 给系统一点时间来完成鼠标移动
        Thread.sleep(200);
        
        // 获取当前鼠标位置
        Point currentPosition = systemRobotService.getMousePosition();
        
        // 验证鼠标位置
        assertEquals(testX, currentPosition.x, 5, "Mouse X position should be approximately at target");
        assertEquals(testY, currentPosition.y, 5, "Mouse Y position should be approximately at target");
        
        // 将鼠标移回原位置
        systemRobotService.moveMouse(initialPosition.x, initialPosition.y);
    }

    @Test
    public void testMousePositionTracking() {
        // 测试获取鼠标位置功能
        Point position = systemRobotService.getMousePosition();
        
        // 验证返回的位置不为null
        assertNotNull(position, "Mouse position should not be null");
        
        // 验证坐标值在合理范围内
        assertTrue(position.x >= 0, "X coordinate should be non-negative");
        assertTrue(position.y >= 0, "Y coordinate should be non-negative");
    }
} 